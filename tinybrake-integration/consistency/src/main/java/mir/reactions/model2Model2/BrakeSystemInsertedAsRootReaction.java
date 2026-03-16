package mir.reactions.model2Model2;

import java.util.function.Function;
import mir.routines.model2Model2.Model2Model2RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReaction;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;

@SuppressWarnings("all")
public class BrakeSystemInsertedAsRootReaction extends AbstractReaction {
    private InsertRootEObject<BrakeSystem> insertChange;

    public BrakeSystemInsertedAsRootReaction(
            final Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        super(routinesFacadeGenerator);
    }

    private static class Call extends AbstractRoutine.Update {
        public Call(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final InsertRootEObject insertChange,
                final BrakeSystem newValue,
                final int index,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            _routinesFacade.createAndRegisterControlSystem(newValue);
        }
    }

    public boolean isCurrentChangeMatchingTrigger(final EChange change) {
        if (!(change instanceof InsertRootEObject<?>)) {
            return false;
        }

        InsertRootEObject<tools.vitruv.methodologisttemplate.model.model.BrakeSystem> _localTypedChange =
                (InsertRootEObject<tools.vitruv.methodologisttemplate.model.model.BrakeSystem>) change;
        if (!(_localTypedChange.getNewValue() instanceof tools.vitruv.methodologisttemplate.model.model.BrakeSystem)) {
            return false;
        }
        this.insertChange = (InsertRootEObject<tools.vitruv.methodologisttemplate.model.model.BrakeSystem>) change;
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
        tools.vitruv.methodologisttemplate.model.model.BrakeSystem newValue =
                (tools.vitruv.methodologisttemplate.model.model.BrakeSystem) insertChange.getNewValue();
        int index = insertChange.getIndex();
        if (getLogger().isTraceEnabled()) {
            getLogger()
                    .trace("Passed complete precondition check of Reaction "
                            + this.getClass().getName());
        }

        new mir.reactions.model2Model2.BrakeSystemInsertedAsRootReaction.Call(executionState)
                .updateModels(insertChange, newValue, index, routinesFacade);
    }
}
