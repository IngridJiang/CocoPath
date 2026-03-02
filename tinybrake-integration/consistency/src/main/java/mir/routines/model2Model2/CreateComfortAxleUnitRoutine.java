package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;
import tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit;
import tools.vitruv.methodologisttemplate.model.model2.ControlSystem;

@SuppressWarnings("all")
public class CreateComfortAxleUnitRoutine extends AbstractRoutine {
    private CreateComfortAxleUnitRoutine.InputValues inputValues;

    private CreateComfortAxleUnitRoutine.Match.RetrievedValues retrievedValues;

    private CreateComfortAxleUnitRoutine.Create.CreatedValues createdValues;

    public class InputValues {
        public final BrakeSystem system;

        public final BrakeDisc sourceDisc;

        public InputValues(final BrakeSystem system, final BrakeDisc sourceDisc) {
            this.system = system;
            this.sourceDisc = sourceDisc;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final ControlSystem controlSystem;

            public RetrievedValues(final ControlSystem controlSystem) {
                this.controlSystem = controlSystem;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSource1(final BrakeSystem system, final BrakeDisc sourceDisc) {
            return sourceDisc;
        }

        public EObject getCorrepondenceSourceControlSystem(final BrakeSystem system, final BrakeDisc sourceDisc) {
            return system;
        }

        public CreateComfortAxleUnitRoutine.Match.RetrievedValues match(
                final BrakeSystem system, final BrakeDisc sourceDisc) throws IOException {
            if (hasCorrespondingElements(
                    getCorrepondenceSource1(system, sourceDisc), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit.class,
                    null, // correspondence precondition checker
                    null)) {
                return null;
            }
            tools.vitruv.methodologisttemplate.model.model2.ControlSystem controlSystem = getCorrespondingElement(
                    getCorrepondenceSourceControlSystem(system, sourceDisc), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.ControlSystem.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (controlSystem == null) {
                return null;
            }
            return new mir.routines.model2Model2.CreateComfortAxleUnitRoutine.Match.RetrievedValues(controlSystem);
        }
    }

    private static class Create extends AbstractRoutine.Create {
        public class CreatedValues {
            public final AxleControlUnit axleUnit;

            public CreatedValues(final AxleControlUnit axleUnit) {
                this.axleUnit = axleUnit;
            }
        }

        public Create(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public CreateComfortAxleUnitRoutine.Create.CreatedValues createElements() {
            tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit axleUnit = createObject(() -> {
                return tools.vitruv.methodologisttemplate.model.model2.impl.Model2FactoryImpl.eINSTANCE
                        .createAxleControlUnit();
            });
            return new CreateComfortAxleUnitRoutine.Create.CreatedValues(axleUnit);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakeSystem system,
                final BrakeDisc sourceDisc,
                final ControlSystem controlSystem,
                final AxleControlUnit axleUnit,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            axleUnit.setControlProfile("comfort");
            double _diameter = sourceDisc.getDiameter();
            double _divide = (_diameter / 50.0);
            double _multiply = (_divide * 0.85);
            axleUnit.setAbsDecelThreshold(_multiply);
            double mu = 0.30;
            String _material = sourceDisc.getMaterial();
            boolean _tripleNotEquals = (_material != null);
            if (_tripleNotEquals) {
                final String m = sourceDisc.getMaterial().toLowerCase();
                boolean _equals = m.equals("carbon ceramic");
                if (_equals) {
                    mu = 0.45;
                } else {
                    boolean _equals_1 = m.equals("cast iron");
                    if (_equals_1) {
                        mu = 0.35;
                    }
                }
            }
            double _diameter_1 = sourceDisc.getDiameter();
            double _divide_1 = (_diameter_1 / 2.0);
            double _multiply_1 = ((mu * 15000.0) * _divide_1);
            double _divide_2 = (_multiply_1 / 1000.0);
            axleUnit.setMaxBrakingTorque(_divide_2);
            axleUnit.setCalibrationOffset(0.0);
            double _absDecelThreshold = axleUnit.getAbsDecelThreshold();
            double _calibrationOffset = axleUnit.getCalibrationOffset();
            double _plus = (_absDecelThreshold + _calibrationOffset);
            axleUnit.setEffectiveBrakeGain(_plus);
            controlSystem.getAxleUnits().add(axleUnit);
            this.addCorrespondenceBetween(sourceDisc, axleUnit);
        }
    }

    public CreateComfortAxleUnitRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeSystem system,
            final BrakeDisc sourceDisc) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateComfortAxleUnitRoutine.InputValues(system, sourceDisc);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateComfortAxleUnitRoutine with input:");
            getLogger().trace("   inputValues.system: " + inputValues.system);
            getLogger().trace("   inputValues.sourceDisc: " + inputValues.sourceDisc);
        }
        retrievedValues = new mir.routines.model2Model2.CreateComfortAxleUnitRoutine.Match(getExecutionState())
                .match(inputValues.system, inputValues.sourceDisc);
        if (retrievedValues == null) {
            return false;
        }
        createdValues =
                new mir.routines.model2Model2.CreateComfortAxleUnitRoutine.Create(getExecutionState()).createElements();
        new mir.routines.model2Model2.CreateComfortAxleUnitRoutine.Update(getExecutionState())
                .updateModels(
                        inputValues.system,
                        inputValues.sourceDisc,
                        retrievedValues.controlSystem,
                        createdValues.axleUnit,
                        getRoutinesFacade());
        return true;
    }
}
