package mir.routines.amalthea2ascet;

import java.io.IOException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.AscetModule;
import tools.vitruv.methodologisttemplate.model.model.PeriodicTask;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.Task;

@SuppressWarnings("all")
public class CreatePeriodicTaskRoutine extends AbstractRoutine {
    private CreatePeriodicTaskRoutine.InputValues inputValues;

    private CreatePeriodicTaskRoutine.Match.RetrievedValues retrievedValues;

    private CreatePeriodicTaskRoutine.Create.CreatedValues createdValues;

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

        public CreatePeriodicTaskRoutine.Match.RetrievedValues match(
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
                    tools.vitruv.methodologisttemplate.model.model.PeriodicTask.class,
                    null, // correspondence precondition checker
                    null)) {
                return null;
            }
            return new mir.routines.amalthea2ascet.CreatePeriodicTaskRoutine.Match.RetrievedValues(AscetModule);
        }
    }

    private static class Create extends AbstractRoutine.Create {
        public class CreatedValues {
            public final PeriodicTask periodicTask;

            public CreatedValues(final PeriodicTask periodicTask) {
                this.periodicTask = periodicTask;
            }
        }

        public Create(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public CreatePeriodicTaskRoutine.Create.CreatedValues createElements() {
            tools.vitruv.methodologisttemplate.model.model.PeriodicTask periodicTask = createObject(() -> {
                return tools.vitruv.methodologisttemplate.model.model.impl.ModelFactoryImpl.eINSTANCE
                        .createPeriodicTask();
            });
            return new CreatePeriodicTaskRoutine.Create.CreatedValues(periodicTask);
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
                final PeriodicTask periodicTask,
                @Extension final Amalthea2ascetRoutinesFacade _routinesFacade) {
            EList<tools.vitruv.methodologisttemplate.model.model.Task> _tasks = AscetModule.getTasks();
            _tasks.add(periodicTask);
            periodicTask.setName(task.getName());
            this.addCorrespondenceBetween(periodicTask, container);
        }
    }

    public CreatePeriodicTaskRoutine(
            final Amalthea2ascetRoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final Task task,
            final ComponentContainer container) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreatePeriodicTaskRoutine.InputValues(task, container);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreatePeriodicTaskRoutine with input:");
            getLogger().trace("   inputValues.task: " + inputValues.task);
            getLogger().trace("   inputValues.container: " + inputValues.container);
        }
        retrievedValues = new mir.routines.amalthea2ascet.CreatePeriodicTaskRoutine.Match(getExecutionState())
                .match(inputValues.task, inputValues.container);
        if (retrievedValues == null) {
            return false;
        }
        createdValues =
                new mir.routines.amalthea2ascet.CreatePeriodicTaskRoutine.Create(getExecutionState()).createElements();
        new mir.routines.amalthea2ascet.CreatePeriodicTaskRoutine.Update(getExecutionState())
                .updateModels(
                        inputValues.task,
                        inputValues.container,
                        retrievedValues.AscetModule,
                        createdValues.periodicTask,
                        getRoutinesFacade());
        return true;
    }
}
