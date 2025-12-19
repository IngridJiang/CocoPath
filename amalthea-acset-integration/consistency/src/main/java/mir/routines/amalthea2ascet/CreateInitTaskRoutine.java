package mir.routines.amalthea2ascet;

import java.io.IOException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.AscetModule;
import tools.vitruv.methodologisttemplate.model.model.InitTask;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.Task;

@SuppressWarnings("all")
public class CreateInitTaskRoutine extends AbstractRoutine {
    private CreateInitTaskRoutine.InputValues inputValues;

    private CreateInitTaskRoutine.Match.RetrievedValues retrievedValues;

    private CreateInitTaskRoutine.Create.CreatedValues createdValues;

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

        public CreateInitTaskRoutine.Match.RetrievedValues match(final Task task, final ComponentContainer container)
                throws IOException {
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
                    tools.vitruv.methodologisttemplate.model.model.InitTask.class,
                    null, // correspondence precondition checker
                    null)) {
                return null;
            }
            return new mir.routines.amalthea2ascet.CreateInitTaskRoutine.Match.RetrievedValues(AscetModule);
        }
    }

    private static class Create extends AbstractRoutine.Create {
        public class CreatedValues {
            public final InitTask initTask;

            public CreatedValues(final InitTask initTask) {
                this.initTask = initTask;
            }
        }

        public Create(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public CreateInitTaskRoutine.Create.CreatedValues createElements() {
            tools.vitruv.methodologisttemplate.model.model.InitTask initTask = createObject(() -> {
                return tools.vitruv.methodologisttemplate.model.model.impl.ModelFactoryImpl.eINSTANCE.createInitTask();
            });
            return new CreateInitTaskRoutine.Create.CreatedValues(initTask);
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
                final InitTask initTask,
                @Extension final Amalthea2ascetRoutinesFacade _routinesFacade) {
            EList<tools.vitruv.methodologisttemplate.model.model.Task> _tasks = AscetModule.getTasks();
            _tasks.add(initTask);
            initTask.setName(task.getName());
            this.addCorrespondenceBetween(initTask, container);
        }
    }

    public CreateInitTaskRoutine(
            final Amalthea2ascetRoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final Task task,
            final ComponentContainer container) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateInitTaskRoutine.InputValues(task, container);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateInitTaskRoutine with input:");
            getLogger().trace("   inputValues.task: " + inputValues.task);
            getLogger().trace("   inputValues.container: " + inputValues.container);
        }
        retrievedValues = new mir.routines.amalthea2ascet.CreateInitTaskRoutine.Match(getExecutionState())
                .match(inputValues.task, inputValues.container);
        if (retrievedValues == null) {
            return false;
        }
        createdValues =
                new mir.routines.amalthea2ascet.CreateInitTaskRoutine.Create(getExecutionState()).createElements();
        new mir.routines.amalthea2ascet.CreateInitTaskRoutine.Update(getExecutionState())
                .updateModels(
                        inputValues.task,
                        inputValues.container,
                        retrievedValues.AscetModule,
                        createdValues.initTask,
                        getRoutinesFacade());
        return true;
    }
}
