package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit;

@SuppressWarnings("all")
public class UpdateControlParametersRoutine extends AbstractRoutine {
    private UpdateControlParametersRoutine.InputValues inputValues;

    private UpdateControlParametersRoutine.Match.RetrievedValues retrievedValues;

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

        public UpdateControlParametersRoutine.Match.RetrievedValues match(final BrakeDisc sourceDisc)
                throws IOException {
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
            return new mir.routines.model2Model2.UpdateControlParametersRoutine.Match.RetrievedValues(axleUnit);
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
            double _diameter = sourceDisc.getDiameter();
            final double base = (_diameter / 50.0);
            double multiplier = 1.0;
            String _controlProfile = axleUnit.getControlProfile();
            boolean _tripleNotEquals = (_controlProfile != null);
            if (_tripleNotEquals) {
                final String profile = axleUnit.getControlProfile();
                boolean _equals = profile.equals("sport");
                if (_equals) {
                    multiplier = 1.20;
                } else {
                    boolean _equals_1 = profile.equals("comfort");
                    if (_equals_1) {
                        multiplier = 0.85;
                    } else {
                        boolean _equals_2 = profile.equals("off_road");
                        if (_equals_2) {
                            multiplier = 0.70;
                        }
                    }
                }
            }
            axleUnit.setAbsDecelThreshold((base * multiplier));
            double mu = 0.30;
            String _material = sourceDisc.getMaterial();
            boolean _tripleNotEquals_1 = (_material != null);
            if (_tripleNotEquals_1) {
                final String m = sourceDisc.getMaterial().toLowerCase();
                boolean _equals_3 = m.equals("carbon ceramic");
                if (_equals_3) {
                    mu = 0.45;
                } else {
                    boolean _equals_4 = m.equals("cast iron");
                    if (_equals_4) {
                        mu = 0.35;
                    }
                }
            }
            double _diameter_1 = sourceDisc.getDiameter();
            double _divide = (_diameter_1 / 2.0);
            double _multiply = ((mu * 15000.0) * _divide);
            double _divide_1 = (_multiply / 1000.0);
            axleUnit.setMaxBrakingTorque(_divide_1);
            double _absDecelThreshold = axleUnit.getAbsDecelThreshold();
            double _calibrationOffset = axleUnit.getCalibrationOffset();
            double _plus = (_absDecelThreshold + _calibrationOffset);
            axleUnit.setEffectiveBrakeGain(_plus);
        }
    }

    public UpdateControlParametersRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeDisc sourceDisc) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new UpdateControlParametersRoutine.InputValues(sourceDisc);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine UpdateControlParametersRoutine with input:");
            getLogger().trace("   inputValues.sourceDisc: " + inputValues.sourceDisc);
        }
        retrievedValues = new mir.routines.model2Model2.UpdateControlParametersRoutine.Match(getExecutionState())
                .match(inputValues.sourceDisc);
        if (retrievedValues == null) {
            return false;
        }
        // This execution step is empty
        new mir.routines.model2Model2.UpdateControlParametersRoutine.Update(getExecutionState())
                .updateModels(inputValues.sourceDisc, retrievedValues.axleUnit, getRoutinesFacade());
        return true;
    }
}
