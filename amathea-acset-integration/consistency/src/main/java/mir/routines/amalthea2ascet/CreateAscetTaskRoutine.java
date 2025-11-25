package mir.routines.amalthea2ascet;

import java.io.IOException;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.Task;

@SuppressWarnings("all")
public class CreateAscetTaskRoutine extends AbstractRoutine {
    private CreateAscetTaskRoutine.InputValues inputValues;

    public class InputValues {
        public final Task task;

        public final ComponentContainer container;

        public InputValues(final Task task, final ComponentContainer container) {
            this.task = task;
            this.container = container;
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final Task task,
                final ComponentContainer container,
                @Extension final Amalthea2ascetRoutinesFacade _routinesFacade) {
            final String userMsg =
                    "A Task has been created. Please decide whether and which corresponding ASCET Task should be created.";
            final String interruptTaskOption = "Create InterruptTask";
            final String periodicTaskOption = "Create PeriodicTask";
            final String softwareTaskOption = "Create SoftwareTask";
            final String timeTableTaskOption = "Create TimeTableTask";
            final String doNothingOption = "Decide Later";
            final String[] options = {
                interruptTaskOption, periodicTaskOption, softwareTaskOption, timeTableTaskOption, doNothingOption
            };
            final Integer selected = this.executionState
                    .getUserInteractor()
                    .getSingleSelectionDialogBuilder()
                    .message(userMsg)
                    .choices(((Iterable<String>) Conversions.doWrapArray(options)))
                    .startInteraction();
            if (selected != null) {
                switch (selected) {
                    case 0:
                        _routinesFacade.createInterruptTask(task, container);
                        break;
                    case 1:
                        _routinesFacade.createPeriodicTask(task, container);
                        break;
                    case 2:
                        _routinesFacade.createSoftwareTask(task, container);
                        break;
                    case 3:
                        _routinesFacade.createTimeTableTask(task, container);
                        break;
                    case 4:
                        break;
                }
            }
        }
    }

    public CreateAscetTaskRoutine(
            final Amalthea2ascetRoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final Task task,
            final ComponentContainer container) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateAscetTaskRoutine.InputValues(task, container);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateAscetTaskRoutine with input:");
            getLogger().trace("   inputValues.task: " + inputValues.task);
            getLogger().trace("   inputValues.container: " + inputValues.container);
        }
        // This execution step is empty
        // This execution step is empty
        new mir.routines.amalthea2ascet.CreateAscetTaskRoutine.Update(getExecutionState())
                .updateModels(inputValues.task, inputValues.container, getRoutinesFacade());
        return true;
    }
}
