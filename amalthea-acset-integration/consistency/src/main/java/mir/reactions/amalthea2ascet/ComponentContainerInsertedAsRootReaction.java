package mir.reactions.amalthea2ascet;

import java.util.function.Function;
import mir.routines.amalthea2ascet.Amalthea2ascetRoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReaction;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;

@SuppressWarnings("all")
public class ComponentContainerInsertedAsRootReaction extends AbstractReaction {
    private InsertRootEObject<ComponentContainer> insertChange;

    public ComponentContainerInsertedAsRootReaction(
            final Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        super(routinesFacadeGenerator);
    }

    private static class Call extends AbstractRoutine.Update {
        public Call(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final InsertRootEObject insertChange,
                final ComponentContainer newValue,
                final int index,
                @Extension final Amalthea2ascetRoutinesFacade _routinesFacade) {
            _routinesFacade.createAndRegisterRootComponentContainer(newValue);
        }
    }

    public boolean isCurrentChangeMatchingTrigger(final EChange change) {
        if (!(change instanceof InsertRootEObject<?>)) {
            return false;
        }

        InsertRootEObject<tools.vitruv.methodologisttemplate.model.model2.ComponentContainer> _localTypedChange =
                (InsertRootEObject<tools.vitruv.methodologisttemplate.model.model2.ComponentContainer>) change;
        if (!(_localTypedChange.getNewValue()
                instanceof tools.vitruv.methodologisttemplate.model.model2.ComponentContainer)) {
            return false;
        }
        this.insertChange =
                (InsertRootEObject<tools.vitruv.methodologisttemplate.model.model2.ComponentContainer>) change;
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
        tools.vitruv.methodologisttemplate.model.model2.ComponentContainer newValue =
                (tools.vitruv.methodologisttemplate.model.model2.ComponentContainer) insertChange.getNewValue();
        int index = insertChange.getIndex();
        if (getLogger().isTraceEnabled()) {
            getLogger()
                    .trace("Passed complete precondition check of Reaction "
                            + this.getClass().getName());
        }

        new mir.reactions.amalthea2ascet.ComponentContainerInsertedAsRootReaction.Call(executionState)
                .updateModels(insertChange, newValue, index, routinesFacade);
    }
}
