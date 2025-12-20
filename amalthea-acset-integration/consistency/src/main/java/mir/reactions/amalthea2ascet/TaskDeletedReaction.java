package mir.reactions.amalthea2ascet;

import java.util.function.Function;
import mir.routines.amalthea2ascet.Amalthea2ascetRoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReaction;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.methodologisttemplate.model.model2.Task;

@SuppressWarnings("all")
public class TaskDeletedReaction extends AbstractReaction {
    private DeleteEObject<Task> deleteChange;

    public TaskDeletedReaction(final Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        super(routinesFacadeGenerator);
    }

    private static class Call extends AbstractRoutine.Update {
        public Call(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final DeleteEObject deleteChange,
                final Task affectedEObject,
                @Extension final Amalthea2ascetRoutinesFacade _routinesFacade) {
            _routinesFacade.deleteTask(affectedEObject);
        }
    }

    public boolean isCurrentChangeMatchingTrigger(final EChange change) {
        if (!(change instanceof DeleteEObject<?>)) {
            return false;
        }

        DeleteEObject<tools.vitruv.methodologisttemplate.model.model2.Task> _localTypedChange =
                (DeleteEObject<tools.vitruv.methodologisttemplate.model.model2.Task>) change;
        if (!(_localTypedChange.getAffectedElement() instanceof tools.vitruv.methodologisttemplate.model.model2.Task)) {
            return false;
        }
        this.deleteChange = (DeleteEObject<tools.vitruv.methodologisttemplate.model.model2.Task>) change;
        return true;
    }

    public void executeReaction(
            final EChange change,
            final ReactionExecutionState executionState,
            final RoutinesFacade routinesFacadeUntyped) {
        mir.routines.amalthea2ascet.Amalthea2ascetRoutinesFacade routinesFacade =
                (mir.routines.amalthea2ascet.Amalthea2ascetRoutinesFacade) routinesFacadeUntyped;
        if (!isCurrentChangeMatchingTrigger(change)) {
            return;
        }
        tools.vitruv.methodologisttemplate.model.model2.Task affectedEObject =
                (tools.vitruv.methodologisttemplate.model.model2.Task) deleteChange.getAffectedElement();
        if (getLogger().isTraceEnabled()) {
            getLogger()
                    .trace("Passed complete precondition check of Reaction "
                            + this.getClass().getName());
        }

        new mir.reactions.amalthea2ascet.TaskDeletedReaction.Call(executionState)
                .updateModels(deleteChange, affectedEObject, routinesFacade);
    }
}
