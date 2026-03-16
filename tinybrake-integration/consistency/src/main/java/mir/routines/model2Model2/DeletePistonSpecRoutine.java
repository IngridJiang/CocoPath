package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakePiston;
import tools.vitruv.methodologisttemplate.model.model2.PistonSpec;

@SuppressWarnings("all")
public class DeletePistonSpecRoutine extends AbstractRoutine {
    private DeletePistonSpecRoutine.InputValues inputValues;

    private DeletePistonSpecRoutine.Match.RetrievedValues retrievedValues;

    public class InputValues {
        public final BrakePiston sourcePiston;

        public InputValues(final BrakePiston sourcePiston) {
            this.sourcePiston = sourcePiston;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final PistonSpec pistonSpec;

            public RetrievedValues(final PistonSpec pistonSpec) {
                this.pistonSpec = pistonSpec;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSourcePistonSpec(final BrakePiston sourcePiston) {
            return sourcePiston;
        }

        public DeletePistonSpecRoutine.Match.RetrievedValues match(final BrakePiston sourcePiston) throws IOException {
            tools.vitruv.methodologisttemplate.model.model2.PistonSpec pistonSpec = getCorrespondingElement(
                    getCorrepondenceSourcePistonSpec(sourcePiston), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.PistonSpec.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (pistonSpec == null) {
                return null;
            }
            return new mir.routines.model2Model2.DeletePistonSpecRoutine.Match.RetrievedValues(pistonSpec);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakePiston sourcePiston,
                final PistonSpec pistonSpec,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            this.removeObject(pistonSpec);
            this.removeCorrespondenceBetween(sourcePiston, pistonSpec);
        }
    }

    public DeletePistonSpecRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakePiston sourcePiston) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new DeletePistonSpecRoutine.InputValues(sourcePiston);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine DeletePistonSpecRoutine with input:");
            getLogger().trace("   inputValues.sourcePiston: " + inputValues.sourcePiston);
        }
        retrievedValues = new mir.routines.model2Model2.DeletePistonSpecRoutine.Match(getExecutionState())
                .match(inputValues.sourcePiston);
        if (retrievedValues == null) {
            return false;
        }
        // This execution step is empty
        new mir.routines.model2Model2.DeletePistonSpecRoutine.Update(getExecutionState())
                .updateModels(inputValues.sourcePiston, retrievedValues.pistonSpec, getRoutinesFacade());
        return true;
    }
}
