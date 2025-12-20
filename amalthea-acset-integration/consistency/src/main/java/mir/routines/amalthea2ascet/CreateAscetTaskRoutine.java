package mir.routines.amalthea2ascet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
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
            final String initTaskOption = "Create InitTask";
            final String periodicTaskOption = "Create PeriodicTask";
            final String softwareTaskOption = "Create SoftwareTask";
            final String timeTableTaskOption = "Create TimeTableTask";
            final String doNothingOption = "Decide Later";
            final String[] options = {
                initTaskOption, periodicTaskOption, softwareTaskOption, timeTableTaskOption, doNothingOption
            };
            InputOutput.<String>println("[Reaction] About to call userInteractor.startInteraction()...");
            final Integer selected = this.executionState
                    .getUserInteractor()
                    .getSingleSelectionDialogBuilder()
                    .message(userMsg)
                    .choices(((Iterable<String>) Conversions.doWrapArray(options)))
                    .startInteraction();
            InputOutput.<String>println(("[Reaction] userInteractor returned: " + selected));
            Integer symbolicSelected = selected;
            if ((selected != null)) {
                try {
                    String _name_1 = task.getName();
                    final String qualifiedName = ("CreateAscetTaskRoutine:execute:userChoice_forTask_" + _name_1);
                    final Class<?> symbolicatorClass =
                            Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator");
                    final Method getOrMakeMethod = symbolicatorClass.getMethod(
                            "getOrMakeSymbolicInt", String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                    int _size = ((List<String>) Conversions.doWrapArray(options)).size();
                    int _minus = (_size - 1);
                    Object _invoke = getOrMakeMethod.invoke(
                            null, qualifiedName, selected, Integer.valueOf(0), Integer.valueOf(_minus));
                    final Integer taggedSelected = ((Integer) _invoke);
                    final Class<?> symbolicClass =
                            Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.SymbolicComparison");
                    final Method method = symbolicClass.getMethod(
                            "symbolicVitruviusChoice", Integer.class, Integer.TYPE, Integer.TYPE);
                    int _size_1 = ((List<String>) Conversions.doWrapArray(options)).size();
                    int _minus_1 = (_size_1 - 1);
                    Object _invoke_1 =
                            method.invoke(null, taggedSelected, Integer.valueOf(0), Integer.valueOf(_minus_1));
                    symbolicSelected = ((Integer) _invoke_1);
                    InputOutput.<String>println(
                            "[Reaction] Processed user choice with symbolic execution (tag reuse enabled)");
                } catch (final Throwable _t) {
                    if (_t instanceof Exception) {
                        final Exception e = (Exception) _t;
                        String _message = e.getMessage();
                        String _plus_1 = ("[Reaction] Symbolic processing failed: " + _message);
                        InputOutput.<String>println(_plus_1);
                        e.printStackTrace();
                        symbolicSelected = selected;
                    } else {
                        throw Exceptions.sneakyThrow(_t);
                    }
                }
            }
            if (symbolicSelected != null) {
                switch (symbolicSelected) {
                    case 0:
                        _routinesFacade.createInitTask(task, container);
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
