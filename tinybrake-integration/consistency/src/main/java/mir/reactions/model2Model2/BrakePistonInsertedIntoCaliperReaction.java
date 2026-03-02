package mir.reactions.model2Model2;

import java.util.function.Function;
import mir.routines.model2Model2.Model2Model2RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReaction;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model.BrakePiston;

@SuppressWarnings("all")
public class BrakePistonInsertedIntoCaliperReaction extends AbstractReaction {
    private InsertEReference<EObject> insertChange;

    public BrakePistonInsertedIntoCaliperReaction(
            final Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        super(routinesFacadeGenerator);
    }

    private static class Call extends AbstractRoutine.Update {
        public Call(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final InsertEReference insertChange,
                final BrakeCaliper affectedEObject,
                final EReference affectedFeature,
                final BrakePiston newValue,
                final int index,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            _routinesFacade.createAndTransformBrakePiston(affectedEObject, newValue);
        }
    }

    public boolean isCurrentChangeMatchingTrigger(final EChange change) {
        if (!(change instanceof InsertEReference<?>)) {
            return false;
        }

        InsertEReference<org.eclipse.emf.ecore.EObject> _localTypedChange =
                (InsertEReference<org.eclipse.emf.ecore.EObject>) change;
        if (!(_localTypedChange.getAffectedElement()
                instanceof tools.vitruv.methodologisttemplate.model.model.BrakeCaliper)) {
            return false;
        }
        if (!_localTypedChange.getAffectedFeature().getName().equals("pistons")) {
            return false;
        }
        if (!(_localTypedChange.getNewValue() instanceof tools.vitruv.methodologisttemplate.model.model.BrakePiston)) {
            return false;
        }
        this.insertChange = (InsertEReference<org.eclipse.emf.ecore.EObject>) change;
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
                (tools.vitruv.methodologisttemplate.model.model.BrakeCaliper) insertChange.getAffectedElement();
        EReference affectedFeature = insertChange.getAffectedFeature();
        tools.vitruv.methodologisttemplate.model.model.BrakePiston newValue =
                (tools.vitruv.methodologisttemplate.model.model.BrakePiston) insertChange.getNewValue();
        int index = insertChange.getIndex();
        if (getLogger().isTraceEnabled()) {
            getLogger()
                    .trace("Passed complete precondition check of Reaction "
                            + this.getClass().getName());
        }

        new mir.reactions.model2Model2.BrakePistonInsertedIntoCaliperReaction.Call(executionState)
                .updateModels(insertChange, affectedEObject, affectedFeature, newValue, index, routinesFacade);
    }
}
