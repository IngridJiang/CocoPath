package mir.routines.amalthea2ascet;

import java.io.IOException;
import java.lang.reflect.Method;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.InputOutput;
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
            InputOutput.<String>println("[Reaction] createAscetTask routine CALLED!");
            String _name = task.getName();
            String _plus = ("  - Task: " + _name);
            InputOutput.<String>println(_plus);
            InputOutput.<String>println(("  - Container: " + container));
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
            InputOutput.<String>println("[Reaction] About to call userInteractor.startInteraction()...");
            final Integer selected = this.executionState
                    .getUserInteractor()
                    .getSingleSelectionDialogBuilder()
                    .message(userMsg)
                    .choices(((Iterable<String>) Conversions.doWrapArray(options)))
                    .startInteraction();
            InputOutput.<String>println(("[Reaction] userInteractor returned: " + selected));
            Integer _xifexpression = null;
            if ((selected != null)) {
                Integer _xtrycatchfinallyexpression = null;
                try {
                    Integer _xblockexpression = null;
                    {
                        InputOutput.<String>println(
                                "[Reaction] Attempting to call SymbolicComparison.symbolicVitruviusChoice");
                        InputOutput.<String>println(("  - selected = " + selected));
                        final Class<?> symbolicClass =
                                Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.SymbolicComparison");
                        InputOutput.<String>println(("  - Found class: " + symbolicClass));
                        final Method method = symbolicClass.getMethod(
                                "symbolicVitruviusChoice", Integer.class, Integer.TYPE, Integer.TYPE);
                        InputOutput.<String>println(("  - Found method: " + method));
                        Object _invoke = method.invoke(null, selected, Integer.valueOf(0), Integer.valueOf(4));
                        final Integer result = ((Integer) _invoke);
                        InputOutput.<String>println(("  - Method returned: " + result));
                        _xblockexpression = result;
                    }
                    _xtrycatchfinallyexpression = _xblockexpression;
                } catch (final Throwable _t) {
                    if (_t instanceof Exception) {
                        final Exception e = (Exception) _t;
                        Integer _xblockexpression_1 = null;
                        {
                            String _name_1 = e.getClass().getName();
                            String _plus_1 = ("[Reaction] Failed to call SymbolicComparison: " + _name_1);
                            String _plus_2 = (_plus_1 + ": ");
                            String _message = e.getMessage();
                            String _plus_3 = (_plus_2 + _message);
                            InputOutput.<String>println(_plus_3);
                            e.printStackTrace();
                            _xblockexpression_1 = selected;
                        }
                        _xtrycatchfinallyexpression = _xblockexpression_1;
                    } else {
                        throw Exceptions.sneakyThrow(_t);
                    }
                }
                _xifexpression = _xtrycatchfinallyexpression;
            } else {
                Integer _xblockexpression = null;
                {
                    InputOutput.<String>println("[Reaction] Selected is null, not calling SymbolicComparison");
                    _xblockexpression = selected;
                }
                _xifexpression = _xblockexpression;
            }
            final Integer symbolicSelected = _xifexpression;
            if (symbolicSelected != null) {
                switch (symbolicSelected) {
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
