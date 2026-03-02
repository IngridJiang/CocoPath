package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;
import tools.vitruv.methodologisttemplate.model.model2.ControlSystem;

@SuppressWarnings("all")
public class CreateAndRegisterControlSystemRoutine extends AbstractRoutine {
    private CreateAndRegisterControlSystemRoutine.InputValues inputValues;

    private CreateAndRegisterControlSystemRoutine.Match.RetrievedValues retrievedValues;

    private CreateAndRegisterControlSystemRoutine.Create.CreatedValues createdValues;

    public class InputValues {
        public final BrakeSystem system;

        public InputValues(final BrakeSystem system) {
            this.system = system;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public RetrievedValues() {}
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSource1(final BrakeSystem system) {
            return system;
        }

        public CreateAndRegisterControlSystemRoutine.Match.RetrievedValues match(final BrakeSystem system)
                throws IOException {
            if (hasCorrespondingElements(
                    getCorrepondenceSource1(system), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.ControlSystem.class,
                    null, // correspondence precondition checker
                    null)) {
                return null;
            }
            return new mir.routines.model2Model2.CreateAndRegisterControlSystemRoutine.Match.RetrievedValues();
        }
    }

    private static class Create extends AbstractRoutine.Create {
        public class CreatedValues {
            public final ControlSystem controlSystem;

            public CreatedValues(final ControlSystem controlSystem) {
                this.controlSystem = controlSystem;
            }
        }

        public Create(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public CreateAndRegisterControlSystemRoutine.Create.CreatedValues createElements() {
            tools.vitruv.methodologisttemplate.model.model2.ControlSystem controlSystem = createObject(() -> {
                return tools.vitruv.methodologisttemplate.model.model2.impl.Model2FactoryImpl.eINSTANCE
                        .createControlSystem();
            });
            return new CreateAndRegisterControlSystemRoutine.Create.CreatedValues(controlSystem);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakeSystem system,
                final ControlSystem controlSystem,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            this.persistProjectRelative(system, controlSystem, "control_system.model2");
            this.addCorrespondenceBetween(system, controlSystem);
        }
    }

    public CreateAndRegisterControlSystemRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeSystem system) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateAndRegisterControlSystemRoutine.InputValues(system);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateAndRegisterControlSystemRoutine with input:");
            getLogger().trace("   inputValues.system: " + inputValues.system);
        }
        retrievedValues = new mir.routines.model2Model2.CreateAndRegisterControlSystemRoutine.Match(getExecutionState())
                .match(inputValues.system);
        if (retrievedValues == null) {
            return false;
        }
        createdValues = new mir.routines.model2Model2.CreateAndRegisterControlSystemRoutine.Create(getExecutionState())
                .createElements();
        new mir.routines.model2Model2.CreateAndRegisterControlSystemRoutine.Update(getExecutionState())
                .updateModels(inputValues.system, createdValues.controlSystem, getRoutinesFacade());
        return true;
    }
}
