package mir.routines.model2Model2;

import java.io.IOException;
import java.lang.reflect.Method;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.InputOutput;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;

/**
 * Routine: createAndConfigureAxleUnit
 *
 * Reads a continuous aggressiveness value from a free-text dialog, wraps it in a
 * symbolic integer covering [-1, 100], then computes a disc-dependent performance
 * score and uses if-interval comparisons on the score to choose the axle-unit profile.
 *
 * Score calculation (exercises Knarr-style expression propagation via IADD):
 *   discBonus        = (int)(sourceDisc.getDiameter() / 10.0)   (concrete)
 *   performanceScore = symbolicAggressiveness + discBonus        (IADD on symbolic + concrete)
 *
 * Interval partition (on performanceScore):
 *   performanceScore &lt;  0                      → skip
 *   0  &lt;= performanceScore &lt;  34 + discBonus   → off_road
 *   34 + discBonus &lt;= performanceScore &lt;  67 + discBonus  → comfort
 *   67 + discBonus &lt;= performanceScore          → sport
 *
 * When bytecode instrumentation is enabled (-Dgalette.symbolic.enabled=true
 * -Dgalette.instrument.prefix=mir/), the IADD and IF_ICMP* instructions are
 * automatically intercepted by TagPropagator, building compound expressions
 * like GT(ADD(var(aggressiveness), const(discBonus)), const(threshold)).
 */
@SuppressWarnings("all")
public class CreateAndConfigureAxleUnitRoutine extends AbstractRoutine {
    private CreateAndConfigureAxleUnitRoutine.InputValues inputValues;

    public class InputValues {
        public final BrakeSystem system;

        public final BrakeDisc sourceDisc;

        public InputValues(final BrakeSystem system, final BrakeDisc sourceDisc) {
            this.system = system;
            this.sourceDisc = sourceDisc;
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        /**
         * Record an if-comparison constraint via reflection (manual constraint path).
         * Falls back silently when knarr-runtime is not on the classpath.
         */
        private static void recordIfConstraint(String qualifiedName, Integer symbolicValue, String op, int threshold) {
            if (qualifiedName == null) return;
            try {
                Class<?> scClass = Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.SymbolicComparison");
                Method m =
                        scClass.getMethod("symbolicVitruviusIfComparison", Integer.class, String.class, Integer.TYPE);
                m.invoke(null, symbolicValue, op, threshold);
            } catch (Exception ignored) {
                // knarr-runtime not available — running without symbolic execution
            }
        }

        public void updateModels(
                final BrakeSystem system,
                final BrakeDisc sourceDisc,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            // Read continuous aggressiveness via free-text dialog
            final String rawProfile = this.executionState
                    .getUserInteractor()
                    .getTextInputDialogBuilder()
                    .message("Enter drive aggressiveness (0\u2013100, or -1 to skip):")
                    .startInteraction();

            // Parse; default 50 (comfort range) on invalid input
            Integer aggressiveness = Integer.valueOf(50);
            if (rawProfile != null) {
                try {
                    aggressiveness = Integer.valueOf(Integer.parseInt(rawProfile.trim()));
                } catch (final Throwable _t) {
                    if (!(_t instanceof Exception)) throw Exceptions.sneakyThrow(_t);
                    // keep default
                }
            }

            // Create symbolic integer covering the full range [-1, 100]; domain constraint
            // is recorded automatically by GaletteSymbolicator.getOrMakeSymbolicInt on first call.
            Integer symbolicAggressiveness = aggressiveness;
            String qualifiedName = null;
            try {
                String _valueOf = String.valueOf(sourceDisc.getDiameter());
                qualifiedName = ("CreateAndConfigureAxleUnitRoutine:execute:profileChoice_disc_" + _valueOf);
                final Class<?> symbolicatorClass =
                        Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator");
                final Method getOrMakeMethod = symbolicatorClass.getMethod(
                        "getOrMakeSymbolicInt", String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                symbolicAggressiveness = ((Integer) getOrMakeMethod.invoke(
                        null, qualifiedName, aggressiveness, Integer.valueOf(-1), Integer.valueOf(100)));
                InputOutput.<String>println("[Reaction] Processed profile choice with symbolic execution");
            } catch (final Throwable _t) {
                if (_t instanceof Exception) {
                    final Exception e = (Exception) _t;
                    InputOutput.<String>println("[Reaction] Symbolic processing failed: " + e.getMessage());
                    symbolicAggressiveness = aggressiveness;
                    qualifiedName = null;
                } else {
                    throw Exceptions.sneakyThrow(_t);
                }
            }

            // Compute disc-dependent performance score.
            // This IADD on a symbolic + concrete value is intercepted by TagPropagator's
            // expression propagation, producing ADD(var(aggressiveness), const(discBonus)).
            int discBonus = (int) (sourceDisc.getDiameter() / 10.0);
            int performanceScore = symbolicAggressiveness + discBonus; // ← IADD (expression propagation)
            int lowThreshold = 34 + discBonus;
            int highThreshold = 67 + discBonus;

            InputOutput.<String>println("[Reaction] discBonus=" + discBonus
                    + ", performanceScore=" + performanceScore
                    + ", thresholds=[" + lowThreshold + ", " + highThreshold + "]");

            // Interval dispatch on the computed score.
            // When bytecode instrumentation is active (-Dgalette.symbolic.enabled=true
            // -Dgalette.instrument.prefix=mir/), TagPropagator intercepts the IF_ICMP*
            // instructions and records compound constraints like:
            //   LT(ADD(var(aggressiveness), const(discBonus)), const(lowThreshold))
            //
            // For the manual constraint path (no agent), record equivalent constraints
            // on the raw variable with adjusted thresholds.
            if (performanceScore >= 0) {
                if (performanceScore < lowThreshold) {
                    // Manual constraint fallback: aggressiveness < 34
                    recordIfConstraint(qualifiedName, symbolicAggressiveness, "GE", 0);
                    recordIfConstraint(qualifiedName, symbolicAggressiveness, "LT", 34);
                    _routinesFacade.createOffRoadAxleUnit(system, sourceDisc);
                } else if (performanceScore < highThreshold) {
                    // Manual constraint fallback: 34 <= aggressiveness < 67
                    recordIfConstraint(qualifiedName, symbolicAggressiveness, "GE", 34);
                    recordIfConstraint(qualifiedName, symbolicAggressiveness, "LT", 67);
                    _routinesFacade.createComfortAxleUnit(system, sourceDisc);
                } else {
                    // Manual constraint fallback: aggressiveness >= 67
                    recordIfConstraint(qualifiedName, symbolicAggressiveness, "GE", 67);
                    _routinesFacade.createSportAxleUnit(system, sourceDisc);
                }
            }
            // applyCalibration's match silently fails (no-op) when skip was chosen
            // (no AxleControlUnit correspondence exists for this disc).
            _routinesFacade.applyCalibration(sourceDisc);
        }
    }

    public CreateAndConfigureAxleUnitRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeSystem system,
            final BrakeDisc sourceDisc) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateAndConfigureAxleUnitRoutine.InputValues(system, sourceDisc);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateAndConfigureAxleUnitRoutine with input:");
            getLogger().trace("   inputValues.system: " + inputValues.system);
            getLogger().trace("   inputValues.sourceDisc: " + inputValues.sourceDisc);
        }
        // This execution step is empty
        // This execution step is empty
        new mir.routines.model2Model2.CreateAndConfigureAxleUnitRoutine.Update(getExecutionState())
                .updateModels(inputValues.system, inputValues.sourceDisc, getRoutinesFacade());
        return true;
    }
}
