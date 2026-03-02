package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit;

@SuppressWarnings("all")
public class DeleteAxleControlUnitRoutine extends AbstractRoutine {
    private DeleteAxleControlUnitRoutine.InputValues inputValues;

    private DeleteAxleControlUnitRoutine.Match.RetrievedValues retrievedValues;

    public class InputValues {
        public final BrakeDisc sourceDisc;

        public InputValues(final BrakeDisc sourceDisc) {
            this.sourceDisc = sourceDisc;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final AxleControlUnit axleUnit;

            public RetrievedValues(final AxleControlUnit axleUnit) {
                this.axleUnit = axleUnit;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSourceAxleUnit(final BrakeDisc sourceDisc) {
            return sourceDisc;
        }

        public DeleteAxleControlUnitRoutine.Match.RetrievedValues match(final BrakeDisc sourceDisc) throws IOException {
            tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit axleUnit = getCorrespondingElement(
                    getCorrepondenceSourceAxleUnit(sourceDisc), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (axleUnit == null) {
                return null;
            }
            return new mir.routines.model2Model2.DeleteAxleControlUnitRoutine.Match.RetrievedValues(axleUnit);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakeDisc sourceDisc,
                final AxleControlUnit axleUnit,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            this.removeObject(axleUnit);
            this.removeCorrespondenceBetween(sourceDisc, axleUnit);
        }
    }

    public DeleteAxleControlUnitRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeDisc sourceDisc) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new DeleteAxleControlUnitRoutine.InputValues(sourceDisc);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine DeleteAxleControlUnitRoutine with input:");
            getLogger().trace("   inputValues.sourceDisc: " + inputValues.sourceDisc);
        }
        retrievedValues = new mir.routines.model2Model2.DeleteAxleControlUnitRoutine.Match(getExecutionState())
                .match(inputValues.sourceDisc);
        if (retrievedValues == null) {
            return false;
        }
        // This execution step is empty
        new mir.routines.model2Model2.DeleteAxleControlUnitRoutine.Update(getExecutionState())
                .updateModels(inputValues.sourceDisc, retrievedValues.axleUnit, getRoutinesFacade());
        return true;
    }
}
