package mir.reactions.model2Model2;

import java.util.function.Function;
import mir.routines.model2Model2.Model2Model2RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReaction;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;

@SuppressWarnings("all")
public class BrakeCaliperDeletedReaction extends AbstractReaction {
    private DeleteEObject<BrakeCaliper> deleteChange;

    public BrakeCaliperDeletedReaction(final Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        super(routinesFacadeGenerator);
    }

    private static class Call extends AbstractRoutine.Update {
        public Call(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final DeleteEObject deleteChange,
                final BrakeCaliper affectedEObject,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            _routinesFacade.deleteHydraulicActuator(affectedEObject);
        }
    }

    public boolean isCurrentChangeMatchingTrigger(final EChange change) {
        if (!(change instanceof DeleteEObject<?>)) {
            return false;
        }

        DeleteEObject<tools.vitruv.methodologisttemplate.model.model.BrakeCaliper> _localTypedChange =
                (DeleteEObject<tools.vitruv.methodologisttemplate.model.model.BrakeCaliper>) change;
        if (!(_localTypedChange.getAffectedElement()
                instanceof tools.vitruv.methodologisttemplate.model.model.BrakeCaliper)) {
            return false;
        }
        this.deleteChange = (DeleteEObject<tools.vitruv.methodologisttemplate.model.model.BrakeCaliper>) change;
        return true;
    }

    public void executeReaction(
            final EChange change,
            final ReactionExecutionState executionState,
            final RoutinesFacade routinesFacadeUntyped) {
        mir.routines.model2Model2.Model2Model2RoutinesFacade routinesFacade =
                (mir.routines.model2Model2.Model2Model2RoutinesFacade) routinesFacadeUntyped;
        if (!isCurrentChangeMatchingTrigger(change)) {
            return;
        }
        tools.vitruv.methodologisttemplate.model.model.BrakeCaliper affectedEObject =
                (tools.vitruv.methodologisttemplate.model.model.BrakeCaliper) deleteChange.getAffectedElement();
        if (getLogger().isTraceEnabled()) {
            getLogger()
                    .trace("Passed complete precondition check of Reaction "
                            + this.getClass().getName());
        }

        new mir.reactions.model2Model2.BrakeCaliperDeletedReaction.Call(executionState)
                .updateModels(deleteChange, affectedEObject, routinesFacade);
    }
}
