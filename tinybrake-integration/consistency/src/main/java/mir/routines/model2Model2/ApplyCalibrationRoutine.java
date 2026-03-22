package mir.routines.model2Model2;

import java.io.IOException;
import java.lang.reflect.Method;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.InputOutput;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit;

/**
 * Routine: applyCalibration
 *
 * The match block silently fails (no-op) when no AxleControlUnit corresponds to
 * sourceDisc (i.e. the profile was skip).  When an axle unit exists, reads a
 * continuous calibration level [0, 100] from a free-text dialog and applies an
 * if-interval partition:
 *
 *   calibLevel &lt;  33  → conservative (offset -0.5)
 *   calibLevel &lt;  67  → standard     (offset  0.0)
 *   else              → track        (offset +0.5)
 *
 * When native bytecode interception is enabled (-Dgalette.concolic.interception.enabled=true),
 * the if-interval comparisons are automatically captured by native bytecode interception
 * without explicit constraint recording calls.
 */
@SuppressWarnings("all")
public class ApplyCalibrationRoutine extends AbstractRoutine {
    private ApplyCalibrationRoutine.InputValues inputValues;

    private ApplyCalibrationRoutine.Match.RetrievedValues retrievedValues;

    public class InputValues {
        public final BrakeDisc sourceDisc;

        public InputValues(final BrakeDisc sourceDisc) {
            this.sourceDisc = sourceDisc;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final AxleControlUnit axleUnit;

            public RetrievedValues(final AxleControlUnit axleUnit) {
                this.axleUnit = axleUnit;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSourceAxleUnit(final BrakeDisc sourceDisc) {
            return sourceDisc;
        }

        public ApplyCalibrationRoutine.Match.RetrievedValues match(final BrakeDisc sourceDisc) throws IOException {
            tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit axleUnit = getCorrespondingElement(
                    getCorrepondenceSourceAxleUnit(sourceDisc), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (axleUnit == null) {
                return null;
            }
            return new mir.routines.model2Model2.ApplyCalibrationRoutine.Match.RetrievedValues(axleUnit);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakeDisc sourceDisc,
                final AxleControlUnit axleUnit,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            // Read continuous calibration level via free-text dialog
            final String rawCalib = this.executionState
                    .getUserInteractor()
                    .getTextInputDialogBuilder()
                    .message("Enter calibration level (0\u2013100):")
                    .startInteraction();

            // Parse; default 50 (standard range) on invalid input
            Integer calibLevel = Integer.valueOf(50);
            if (rawCalib != null) {
                try {
                    calibLevel = Integer.valueOf(Integer.parseInt(rawCalib.trim()));
                } catch (final Throwable _t) {
                    if (!(_t instanceof Exception)) throw Exceptions.sneakyThrow(_t);
                    // keep default
                }
            }

            // Create symbolic integer covering [0, 100]; domain constraint is recorded
            // automatically by GaletteSymbolicator.getOrMakeSymbolicInt on first call.
            Integer symbolicCalibLevel = calibLevel;
            String qualifiedName = null;
            try {
                String _valueOf = String.valueOf(sourceDisc.getDiameter());
                qualifiedName = ("ApplyCalibrationRoutine:execute:calibChoice_disc_" + _valueOf);
                final Class<?> symbolicatorClass =
                        Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator");
                final Method getOrMakeMethod = symbolicatorClass.getMethod(
                        "getOrMakeSymbolicInt", String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                symbolicCalibLevel = ((Integer) getOrMakeMethod.invoke(
                        null, qualifiedName, calibLevel, Integer.valueOf(0), Integer.valueOf(100)));
                InputOutput.<String>println("[Reaction] Processed calibration choice with symbolic execution");
            } catch (final Throwable _t) {
                if (_t instanceof Exception) {
                    final Exception e = (Exception) _t;
                    InputOutput.<String>println("[Reaction] Symbolic processing failed: " + e.getMessage());
                    symbolicCalibLevel = calibLevel;
                    qualifiedName = null;
                } else {
                    throw Exceptions.sneakyThrow(_t);
                }
            }

            // Interval dispatch — native bytecode interception captures these comparisons
            // automatically when -Dgalette.concolic.interception.enabled=true is set.
            if (symbolicCalibLevel < 33) {
                axleUnit.setCalibrationOffset(-0.5); // conservative
            } else if (symbolicCalibLevel < 67) {
                axleUnit.setCalibrationOffset(0.0); // standard
            } else {
                axleUnit.setCalibrationOffset(0.5); // track
            }
            double _absDecelThreshold = axleUnit.getAbsDecelThreshold();
            double _calibrationOffset = axleUnit.getCalibrationOffset();
            axleUnit.setEffectiveBrakeGain(_absDecelThreshold + _calibrationOffset);
        }
    }

    public ApplyCalibrationRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeDisc sourceDisc) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new ApplyCalibrationRoutine.InputValues(sourceDisc);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine ApplyCalibrationRoutine with input:");
            getLogger().trace("   inputValues.sourceDisc: " + inputValues.sourceDisc);
        }
        retrievedValues = new mir.routines.model2Model2.ApplyCalibrationRoutine.Match(getExecutionState())
                .match(inputValues.sourceDisc);
        if (retrievedValues == null) {
            return false;
        }
        // This execution step is empty
        new mir.routines.model2Model2.ApplyCalibrationRoutine.Update(getExecutionState())
                .updateModels(inputValues.sourceDisc, retrievedValues.axleUnit, getRoutinesFacade());
        return true;
    }
}
