package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;

@SuppressWarnings("all")
public class DeleteHydraulicActuatorRoutine extends AbstractRoutine {
    private DeleteHydraulicActuatorRoutine.InputValues inputValues;

    private DeleteHydraulicActuatorRoutine.Match.RetrievedValues retrievedValues;

    public class InputValues {
        public final BrakeCaliper sourceCaliper;

        public InputValues(final BrakeCaliper sourceCaliper) {
            this.sourceCaliper = sourceCaliper;
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

        public EObject getCorrepondenceSourceActuator(final BrakeCaliper sourceCaliper) {
            return sourceCaliper;
        }

        public DeleteHydraulicActuatorRoutine.Match.RetrievedValues match(final BrakeCaliper sourceCaliper)
                throws IOException {
            tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec actuator = getCorrespondingElement(
                    getCorrepondenceSourceActuator(sourceCaliper), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (actuator == null) {
                return null;
            }
            return new mir.routines.model2Model2.DeleteHydraulicActuatorRoutine.Match.RetrievedValues(actuator);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakeCaliper sourceCaliper,
                final HydraulicActuatorSpec actuator,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            this.removeObject(actuator);
            this.removeCorrespondenceBetween(sourceCaliper, actuator);
        }
    }

    public DeleteHydraulicActuatorRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeCaliper sourceCaliper) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new DeleteHydraulicActuatorRoutine.InputValues(sourceCaliper);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine DeleteHydraulicActuatorRoutine with input:");
            getLogger().trace("   inputValues.sourceCaliper: " + inputValues.sourceCaliper);
        }
        retrievedValues = new mir.routines.model2Model2.DeleteHydraulicActuatorRoutine.Match(getExecutionState())
                .match(inputValues.sourceCaliper);
        if (retrievedValues == null) {
            return false;
        }
        // This execution step is empty
        new mir.routines.model2Model2.DeleteHydraulicActuatorRoutine.Update(getExecutionState())
                .updateModels(inputValues.sourceCaliper, retrievedValues.actuator, getRoutinesFacade());
        return true;
    }
}
