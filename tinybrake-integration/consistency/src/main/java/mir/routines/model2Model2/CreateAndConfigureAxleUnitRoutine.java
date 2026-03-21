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
 * symbolic integer covering [-1, 100], then uses if-interval comparisons to choose
 * the axle-unit profile.
 *
 * Interval partition:
 *   aggressiveness <  0          → skip    (no AxleControlUnit created)
 *   0  &lt;= aggressiveness &lt;  34   → off_road (multiplier 0.70)
 *   34 &lt;= aggressiveness &lt;  67   → comfort  (multiplier 0.85)
 *   67 &lt;= aggressiveness &lt;= 100  → sport    (multiplier 1.20)
 *
 * When native bytecode interception is enabled (-Dgalette.concolic.interception.enabled=true),
 * the if-interval comparisons are automatically captured by ComparisonInterceptorVisitor
 * without explicit constraint recording calls.
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

            // Interval dispatch — native bytecode interception captures these comparisons
            // automatically when -Dgalette.concolic.interception.enabled=true is set.
            InputOutput.<String>println(
                    "[Reaction:CreateAndConfigureAxleUnit] symbolicAggressiveness=" + symbolicAggressiveness
                            + " (class=" + symbolicAggressiveness.getClass().getSimpleName() + ")");
            if (symbolicAggressiveness >= 0) {
                if (symbolicAggressiveness < 34) {
                    InputOutput.<String>println("[Reaction:CreateAndConfigureAxleUnit] -> off_road branch");
                    _routinesFacade.createOffRoadAxleUnit(system, sourceDisc);
                } else if (symbolicAggressiveness < 67) {
                    InputOutput.<String>println("[Reaction:CreateAndConfigureAxleUnit] -> comfort branch");
                    _routinesFacade.createComfortAxleUnit(system, sourceDisc);
                } else {
                    InputOutput.<String>println("[Reaction:CreateAndConfigureAxleUnit] -> sport branch");
                    _routinesFacade.createSportAxleUnit(system, sourceDisc);
                }
            } else {
                InputOutput.<String>println("[Reaction:CreateAndConfigureAxleUnit] -> skip branch (< 0)");
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
