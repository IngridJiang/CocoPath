package mir.routines.model2Model2;

import java.io.IOException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;
import tools.vitruv.methodologisttemplate.model.model2.PistonSpec;

@SuppressWarnings("all")
public class RecalculateActuatorForceRoutine extends AbstractRoutine {
    private RecalculateActuatorForceRoutine.InputValues inputValues;

    public class InputValues {
        public final HydraulicActuatorSpec actuator;

        public final BrakeCaliper caliper;

        public InputValues(final HydraulicActuatorSpec actuator, final BrakeCaliper caliper) {
            this.actuator = actuator;
            this.caliper = caliper;
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final HydraulicActuatorSpec actuator,
                final BrakeCaliper caliper,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            double totalArea = 0.0;
            EList<PistonSpec> _pistons = actuator.getPistons();
            for (final PistonSpec piston : _pistons) {
                double _pistonArea = piston.getPistonArea();
                double _plus = (totalArea + _pistonArea);
                totalArea = _plus;
            }
            actuator.setTotalPistonArea(totalArea);
            double _maxPressure = caliper.getMaxPressure();
            double _multiply = (totalArea * _maxPressure);
            double _multiply_1 = (_multiply * 100.0);
            actuator.setMaxHydraulicForce(_multiply_1);
        }
    }

    public RecalculateActuatorForceRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final HydraulicActuatorSpec actuator,
            final BrakeCaliper caliper) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new RecalculateActuatorForceRoutine.InputValues(actuator, caliper);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine RecalculateActuatorForceRoutine with input:");
            getLogger().trace("   inputValues.actuator: " + inputValues.actuator);
            getLogger().trace("   inputValues.caliper: " + inputValues.caliper);
        }
        // This execution step is empty
        // This execution step is empty
        new mir.routines.model2Model2.RecalculateActuatorForceRoutine.Update(getExecutionState())
                .updateModels(inputValues.actuator, inputValues.caliper, getRoutinesFacade());
        return true;
    }
}
