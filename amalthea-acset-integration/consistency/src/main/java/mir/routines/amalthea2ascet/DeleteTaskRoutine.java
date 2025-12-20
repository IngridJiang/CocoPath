package mir.routines.amalthea2ascet;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model2.Task;

@SuppressWarnings("all")
public class DeleteTaskRoutine extends AbstractRoutine {
    private DeleteTaskRoutine.InputValues inputValues;

    private DeleteTaskRoutine.Match.RetrievedValues retrievedValues;

    public class InputValues {
        public final Task task;

        public InputValues(final Task task) {
            this.task = task;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final tools.vitruv.methodologisttemplate.model.model.Task ascettask;

            public RetrievedValues(final tools.vitruv.methodologisttemplate.model.model.Task ascettask) {
                this.ascettask = ascettask;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSourceAscettask(final Task task) {
            return task;
        }

        public DeleteTaskRoutine.Match.RetrievedValues match(final Task task) throws IOException {
            tools.vitruv.methodologisttemplate.model.model.Task ascettask = getCorrespondingElement(
                    getCorrepondenceSourceAscettask(task), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model.Task.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (ascettask == null) {
                return null;
            }
            return new mir.routines.amalthea2ascet.DeleteTaskRoutine.Match.RetrievedValues(ascettask);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final Task task,
                final tools.vitruv.methodologisttemplate.model.model.Task ascettask,
                @Extension final Amalthea2ascetRoutinesFacade _routinesFacade) {
            if ((ascettask != null)) {
                this.removeObject(ascettask);
                this.removeCorrespondenceBetween(task, ascettask);
            }
        }
    }

    public DeleteTaskRoutine(
            final Amalthea2ascetRoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final Task task) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new DeleteTaskRoutine.InputValues(task);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine DeleteTaskRoutine with input:");
            getLogger().trace("   inputValues.task: " + inputValues.task);
        }
        retrievedValues =
                new mir.routines.amalthea2ascet.DeleteTaskRoutine.Match(getExecutionState()).match(inputValues.task);
        if (retrievedValues == null) {
            return false;
        }
        // This execution step is empty
        new mir.routines.amalthea2ascet.DeleteTaskRoutine.Update(getExecutionState())
                .updateModels(inputValues.task, retrievedValues.ascettask, getRoutinesFacade());
        return true;
    }
}
