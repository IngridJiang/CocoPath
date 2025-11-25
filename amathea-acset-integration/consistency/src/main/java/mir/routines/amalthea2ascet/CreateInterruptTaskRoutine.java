package mir.routines.amalthea2ascet;

import java.io.IOException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.AscetModule;
import tools.vitruv.methodologisttemplate.model.model.InterruptTask;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.Task;

@SuppressWarnings("all")
public class CreateInterruptTaskRoutine extends AbstractRoutine {
    private CreateInterruptTaskRoutine.InputValues inputValues;

    private CreateInterruptTaskRoutine.Match.RetrievedValues retrievedValues;

    private CreateInterruptTaskRoutine.Create.CreatedValues createdValues;

    public class InputValues {
        public final Task task;

        public final ComponentContainer container;

        public InputValues(final Task task, final ComponentContainer container) {
            this.task = task;
            this.container = container;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final AscetModule AscetModule;

            public RetrievedValues(final AscetModule AscetModule) {
                this.AscetModule = AscetModule;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSourceAscetModule(final Task task, final ComponentContainer container) {
            return container;
        }

        public EObject getCorrepondenceSource1(
                final Task task, final ComponentContainer container, final AscetModule AscetModule) {
            return task;
        }

        public CreateInterruptTaskRoutine.Match.RetrievedValues match(
                final Task task, final ComponentContainer container) throws IOException {
            tools.vitruv.methodologisttemplate.model.model.AscetModule AscetModule = getCorrespondingElement(
                    getCorrepondenceSourceAscetModule(task, container), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model.AscetModule.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (AscetModule == null) {
                return null;
            }
            if (hasCorrespondingElements(
                    getCorrepondenceSource1(task, container, AscetModule), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model.InterruptTask.class,
                    null, // correspondence precondition checker
                    null)) {
                return null;
            }
            return new mir.routines.amalthea2ascet.CreateInterruptTaskRoutine.Match.RetrievedValues(AscetModule);
        }
    }

    private static class Create extends AbstractRoutine.Create {
        public class CreatedValues {
            public final InterruptTask interruptTask;

            public CreatedValues(final InterruptTask interruptTask) {
                this.interruptTask = interruptTask;
            }
        }

        public Create(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public CreateInterruptTaskRoutine.Create.CreatedValues createElements() {
            tools.vitruv.methodologisttemplate.model.model.InterruptTask interruptTask = createObject(() -> {
                return tools.vitruv.methodologisttemplate.model.model.impl.ModelFactoryImpl.eINSTANCE
                        .createInterruptTask();
            });
            return new CreateInterruptTaskRoutine.Create.CreatedValues(interruptTask);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final Task task,
                final ComponentContainer container,
                final AscetModule AscetModule,
                final InterruptTask interruptTask,
                @Extension final Amalthea2ascetRoutinesFacade _routinesFacade) {
            EList<tools.vitruv.methodologisttemplate.model.model.Task> _tasks = AscetModule.getTasks();
            _tasks.add(interruptTask);
            interruptTask.setName(task.getName());
            this.addCorrespondenceBetween(interruptTask, container);
        }
    }

    public CreateInterruptTaskRoutine(
            final Amalthea2ascetRoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final Task task,
            final ComponentContainer container) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateInterruptTaskRoutine.InputValues(task, container);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateInterruptTaskRoutine with input:");
            getLogger().trace("   inputValues.task: " + inputValues.task);
            getLogger().trace("   inputValues.container: " + inputValues.container);
        }
        retrievedValues = new mir.routines.amalthea2ascet.CreateInterruptTaskRoutine.Match(getExecutionState())
                .match(inputValues.task, inputValues.container);
        if (retrievedValues == null) {
            return false;
        }
        createdValues =
                new mir.routines.amalthea2ascet.CreateInterruptTaskRoutine.Create(getExecutionState()).createElements();
        new mir.routines.amalthea2ascet.CreateInterruptTaskRoutine.Update(getExecutionState())
                .updateModels(
                        inputValues.task,
                        inputValues.container,
                        retrievedValues.AscetModule,
                        createdValues.interruptTask,
                        getRoutinesFacade());
        return true;
    }
}
