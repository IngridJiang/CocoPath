package mir.routines.model2Model2;

import java.io.IOException;
import java.lang.reflect.Method;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.InputOutput;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutine;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;
import tools.vitruv.methodologisttemplate.model.model2.ControlSystem;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;

@SuppressWarnings("all")
public class CreateAndTransformBrakeCaliperRoutine extends AbstractRoutine {
    private CreateAndTransformBrakeCaliperRoutine.InputValues inputValues;

    private CreateAndTransformBrakeCaliperRoutine.Match.RetrievedValues retrievedValues;

    private CreateAndTransformBrakeCaliperRoutine.Create.CreatedValues createdValues;

    public class InputValues {
        public final BrakeSystem system;

        public final BrakeCaliper sourceCaliper;

        public InputValues(final BrakeSystem system, final BrakeCaliper sourceCaliper) {
            this.system = system;
            this.sourceCaliper = sourceCaliper;
        }
    }

    private static class Match extends AbstractRoutine.Match {
        public class RetrievedValues {
            public final ControlSystem controlSystem;

            public RetrievedValues(final ControlSystem controlSystem) {
                this.controlSystem = controlSystem;
            }
        }

        public Match(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public EObject getCorrepondenceSource1(final BrakeSystem system, final BrakeCaliper sourceCaliper) {
            return sourceCaliper;
        }

        public EObject getCorrepondenceSourceControlSystem(final BrakeSystem system, final BrakeCaliper sourceCaliper) {
            return system;
        }

        public CreateAndTransformBrakeCaliperRoutine.Match.RetrievedValues match(
                final BrakeSystem system, final BrakeCaliper sourceCaliper) throws IOException {
            if (hasCorrespondingElements(
                    getCorrepondenceSource1(system, sourceCaliper), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec.class,
                    null, // correspondence precondition checker
                    null)) {
                return null;
            }
            tools.vitruv.methodologisttemplate.model.model2.ControlSystem controlSystem = getCorrespondingElement(
                    getCorrepondenceSourceControlSystem(system, sourceCaliper), // correspondence source supplier
                    tools.vitruv.methodologisttemplate.model.model2.ControlSystem.class,
                    null, // correspondence precondition checker
                    null,
                    false // asserted
                    );
            if (controlSystem == null) {
                return null;
            }
            return new mir.routines.model2Model2.CreateAndTransformBrakeCaliperRoutine.Match.RetrievedValues(
                    controlSystem);
        }
    }

    private static class Create extends AbstractRoutine.Create {
        public class CreatedValues {
            public final HydraulicActuatorSpec actuator;

            public CreatedValues(final HydraulicActuatorSpec actuator) {
                this.actuator = actuator;
            }
        }

        public Create(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public CreateAndTransformBrakeCaliperRoutine.Create.CreatedValues createElements() {
            tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec actuator = createObject(() -> {
                return tools.vitruv.methodologisttemplate.model.model2.impl.Model2FactoryImpl.eINSTANCE
                        .createHydraulicActuatorSpec();
            });
            return new CreateAndTransformBrakeCaliperRoutine.Create.CreatedValues(actuator);
        }
    }

    private static class Update extends AbstractRoutine.Update {
        public Update(final ReactionExecutionState reactionExecutionState) {
            super(reactionExecutionState);
        }

        public void updateModels(
                final BrakeSystem system,
                final BrakeCaliper sourceCaliper,
                final ControlSystem controlSystem,
                final HydraulicActuatorSpec actuator,
                @Extension final Model2Model2RoutinesFacade _routinesFacade) {
            // Read continuous pressure aggressiveness via free-text dialog
            final String rawPressure = this.executionState
                    .getUserInteractor()
                    .getTextInputDialogBuilder()
                    .message("Enter pressure curve aggressiveness (0\u2013100):")
                    .startInteraction();

            // Parse; default 50 (mid-range) on invalid input
            Integer pressureLevel = Integer.valueOf(50);
            if (rawPressure != null) {
                try {
                    pressureLevel = Integer.valueOf(Integer.parseInt(rawPressure.trim()));
                } catch (final Throwable _t) {
                    if (!(_t instanceof Exception)) throw Exceptions.sneakyThrow(_t);
                    // keep default
                }
            }

            // Create symbolic integer covering [0, 100]
            Integer symbolicPressureLevel = pressureLevel;
            try {
                String _valueOf = String.valueOf(sourceCaliper.getMaxPressure());
                final String qualifiedName =
                        ("CreateAndTransformBrakeCaliperRoutine:execute:pressureChoice_caliper_" + _valueOf);
                final Class<?> symbolicatorClass =
                        Class.forName("edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator");
                final Method getOrMakeMethod = symbolicatorClass.getMethod(
                        "getOrMakeSymbolicInt", String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                symbolicPressureLevel = ((Integer) getOrMakeMethod.invoke(
                        null, qualifiedName, pressureLevel, Integer.valueOf(0), Integer.valueOf(100)));
                InputOutput.<String>println("[Reaction] Processed pressure choice with symbolic execution");
            } catch (final Throwable _t) {
                if (_t instanceof Exception) {
                    final Exception e = (Exception) _t;
                    InputOutput.<String>println("[Reaction] Symbolic processing failed: " + e.getMessage());
                    symbolicPressureLevel = pressureLevel;
                } else {
                    throw Exceptions.sneakyThrow(_t);
                }
            }

            // Interval partition: < 50 → linear; >= 50 → progressive
            if (symbolicPressureLevel < 50) {
                actuator.setPressureResponse("linear");
            } else {
                actuator.setPressureResponse("progressive");
            }
            actuator.setTotalPistonArea(0.0);
            actuator.setMaxHydraulicForce(0.0);
            controlSystem.getActuators().add(actuator);
            this.addCorrespondenceBetween(sourceCaliper, actuator);
        }
    }

    public CreateAndTransformBrakeCaliperRoutine(
            final Model2Model2RoutinesFacade routinesFacade,
            final ReactionExecutionState reactionExecutionState,
            final CallHierarchyHaving calledBy,
            final BrakeSystem system,
            final BrakeCaliper sourceCaliper) {
        super(routinesFacade, reactionExecutionState, calledBy);
        this.inputValues = new CreateAndTransformBrakeCaliperRoutine.InputValues(system, sourceCaliper);
    }

    protected boolean executeRoutine() throws IOException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Called routine CreateAndTransformBrakeCaliperRoutine with input:");
            getLogger().trace("   inputValues.system: " + inputValues.system);
            getLogger().trace("   inputValues.sourceCaliper: " + inputValues.sourceCaliper);
        }
        retrievedValues = new mir.routines.model2Model2.CreateAndTransformBrakeCaliperRoutine.Match(getExecutionState())
                .match(inputValues.system, inputValues.sourceCaliper);
        if (retrievedValues == null) {
            return false;
        }
        createdValues = new mir.routines.model2Model2.CreateAndTransformBrakeCaliperRoutine.Create(getExecutionState())
                .createElements();
        new mir.routines.model2Model2.CreateAndTransformBrakeCaliperRoutine.Update(getExecutionState())
                .updateModels(
                        inputValues.system,
                        inputValues.sourceCaliper,
                        retrievedValues.controlSystem,
                        createdValues.actuator,
                        getRoutinesFacade());
        return true;
    }
}
