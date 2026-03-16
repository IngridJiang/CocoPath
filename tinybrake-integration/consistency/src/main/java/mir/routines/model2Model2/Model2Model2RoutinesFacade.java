package mir.routines.model2Model2;

import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model.BrakePiston;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;

@SuppressWarnings("all")
public class Model2Model2RoutinesFacade extends AbstractRoutinesFacade {
    public Model2Model2RoutinesFacade(
            final RoutinesFacadesProvider routinesFacadesProvider, final ReactionsImportPath reactionsImportPath) {
        super(routinesFacadesProvider, reactionsImportPath);
    }

    public boolean createAndRegisterControlSystem(final BrakeSystem system) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        CreateAndRegisterControlSystemRoutine routine =
                new CreateAndRegisterControlSystemRoutine(_routinesFacade, _executionState, _caller, system);
        return routine.execute();
    }

    public boolean createAndConfigureAxleUnit(final BrakeSystem system, final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        CreateAndConfigureAxleUnitRoutine routine =
                new CreateAndConfigureAxleUnitRoutine(_routinesFacade, _executionState, _caller, system, sourceDisc);
        return routine.execute();
    }

    public boolean createSportAxleUnit(final BrakeSystem system, final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        CreateSportAxleUnitRoutine routine =
                new CreateSportAxleUnitRoutine(_routinesFacade, _executionState, _caller, system, sourceDisc);
        return routine.execute();
    }

    public boolean createComfortAxleUnit(final BrakeSystem system, final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        CreateComfortAxleUnitRoutine routine =
                new CreateComfortAxleUnitRoutine(_routinesFacade, _executionState, _caller, system, sourceDisc);
        return routine.execute();
    }

    public boolean createOffRoadAxleUnit(final BrakeSystem system, final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        CreateOffRoadAxleUnitRoutine routine =
                new CreateOffRoadAxleUnitRoutine(_routinesFacade, _executionState, _caller, system, sourceDisc);
        return routine.execute();
    }

    public boolean applyCalibration(final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        ApplyCalibrationRoutine routine =
                new ApplyCalibrationRoutine(_routinesFacade, _executionState, _caller, sourceDisc);
        return routine.execute();
    }

    public boolean updateControlParameters(final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        UpdateControlParametersRoutine routine =
                new UpdateControlParametersRoutine(_routinesFacade, _executionState, _caller, sourceDisc);
        return routine.execute();
    }

    public boolean updateBrakingTorque(final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        UpdateBrakingTorqueRoutine routine =
                new UpdateBrakingTorqueRoutine(_routinesFacade, _executionState, _caller, sourceDisc);
        return routine.execute();
    }

    public boolean deleteAxleControlUnit(final BrakeDisc sourceDisc) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        DeleteAxleControlUnitRoutine routine =
                new DeleteAxleControlUnitRoutine(_routinesFacade, _executionState, _caller, sourceDisc);
        return routine.execute();
    }

    public boolean createAndTransformBrakePiston(final BrakeCaliper caliper, final BrakePiston sourcePiston) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        CreateAndTransformBrakePistonRoutine routine = new CreateAndTransformBrakePistonRoutine(
                _routinesFacade, _executionState, _caller, caliper, sourcePiston);
        return routine.execute();
    }

    public boolean deletePistonSpec(final BrakePiston sourcePiston) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        DeletePistonSpecRoutine routine =
                new DeletePistonSpecRoutine(_routinesFacade, _executionState, _caller, sourcePiston);
        return routine.execute();
    }

    public boolean createAndTransformBrakeCaliper(final BrakeSystem system, final BrakeCaliper sourceCaliper) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        CreateAndTransformBrakeCaliperRoutine routine = new CreateAndTransformBrakeCaliperRoutine(
                _routinesFacade, _executionState, _caller, system, sourceCaliper);
        return routine.execute();
    }

    public boolean deleteHydraulicActuator(final BrakeCaliper sourceCaliper) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        DeleteHydraulicActuatorRoutine routine =
                new DeleteHydraulicActuatorRoutine(_routinesFacade, _executionState, _caller, sourceCaliper);
        return routine.execute();
    }

    public boolean recalculateActuatorForce(final HydraulicActuatorSpec actuator, final BrakeCaliper caliper) {
        Model2Model2RoutinesFacade _routinesFacade = this;
        ReactionExecutionState _executionState = _getExecutionState();
        CallHierarchyHaving _caller = this._getCurrentCaller();
        RecalculateActuatorForceRoutine routine =
                new RecalculateActuatorForceRoutine(_routinesFacade, _executionState, _caller, actuator, caliper);
        return routine.execute();
    }
}
