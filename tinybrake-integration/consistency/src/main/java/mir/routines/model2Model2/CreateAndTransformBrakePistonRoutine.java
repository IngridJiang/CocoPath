package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model.BrakePiston;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;
import tools.vitruv.methodologisttemplate.model.model2.PistonSpec;

@SuppressWarnings("all")
public class CreateAndTransformBrakePistonRoutine extends AbstractRoutine {
    private CreateAndTransformBrakePistonRoutine.InputValues inputValues;

    private CreateAndTransformBrakePistonRoutine.Match.RetrievedValues retrievedValues;

    private CreateAndTransformBrakePistonRoutine.Create.CreatedValues createdValues;

    public class InputValues {
        public final BrakeCaliper caliper;

        public final BrakePiston sourcePiston;

        public InputValues(final BrakeCaliper caliper, final BrakePiston sourcePiston) {
            this.caliper = caliper;
            this.sourcePiston = sourcePiston;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final HydraulicActuatorSpec actuator;

            public RetrievedValues(final HydraulicActuatorSpec actuator) {
                this.actuator = actuator;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSource1(final BrakeCaliper caliper, final BrakePiston sourcePiston) {
            return sourcePiston;
        }

        public EObject getCorrepondenceSourceActuator(final BrakeCaliper caliper, final BrakePiston sourcePiston) {
            return caliper;
        }

        public CreateAndTransformBrakePistonRoutine.Match.RetrievedValues match(
                final BrakeCaliper caliper, final BrakePiston sourcePiston) throws IOException {
            if (hasCorrespondingElements(
                    getCorrepondenceSource1(caliper, sourcePiston), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.PistonSpec.class,
                    null, // correspondence precondition checker
                    null)) {
                return null;
            }
            tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec actuator = getCorrespondingElement(
                    getCorrepondenceSourceActuator(caliper, sourcePiston), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (actuator == null) {
                return null;
            }
            return new mir.routines.model2Model2.CreateAndTransformBrakePistonRoutine.Match.RetrievedValues(actuator);
        }
    }

    private static class Create extends AbstractRoutine.Create {
        public class CreatedValues {
            public final PistonSpec pistonSpec;

            public CreatedValues(final PistonSpec pistonSpec) {
                this.pistonSpec = pistonSpec;
            }
        }

        public Create(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public CreateAndTransformBrakePistonRoutine.Create.CreatedValues createElements() {
            tools.vitruv.methodologisttemplate.model.model2.PistonSpec pistonSpec = createObject(() -> {
                return tools.vitruv.methodologisttemplate.model.model2.impl.Model2FactoryImpl.eINSTANCE
                        .createPistonSpec();
            });
            return new CreateAndTransformBrakePistonRoutine.Create.CreatedValues(pistonSpec);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakeCaliper caliper,
                final BrakePiston sourcePiston,
                final HydraulicActuatorSpec actuator,
                final PistonSpec pistonSpec,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            double _diameter = sourcePiston.getDiameter();
            final double radius = (_diameter / 2.0);
            pistonSpec.setPistonArea(((Math.PI * radius) * radius));
            actuator.getPistons().add(pistonSpec);
            this.addCorrespondenceBetween(sourcePiston, pistonSpec);
            _routinesFacade.recalculateActuatorForce(actuator, caliper);
        }
    }

    public CreateAndTransformBrakePistonRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeCaliper caliper,
            final BrakePiston sourcePiston) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateAndTransformBrakePistonRoutine.InputValues(caliper, sourcePiston);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateAndTransformBrakePistonRoutine with input:");
            getLogger().trace("   inputValues.caliper: " + inputValues.caliper);
            getLogger().trace("   inputValues.sourcePiston: " + inputValues.sourcePiston);
        }
        retrievedValues = new mir.routines.model2Model2.CreateAndTransformBrakePistonRoutine.Match(getExecutionState())
                .match(inputValues.caliper, inputValues.sourcePiston);
        if (retrievedValues == null) {
            return false;
        }
        createdValues = new mir.routines.model2Model2.CreateAndTransformBrakePistonRoutine.Create(getExecutionState())
                .createElements();
        new mir.routines.model2Model2.CreateAndTransformBrakePistonRoutine.Update(getExecutionState())
                .updateModels(
                        inputValues.caliper,
                        inputValues.sourcePiston,
                        retrievedValues.actuator,
                        createdValues.pistonSpec,
                        getRoutinesFacade());
        return true;
    }
}
