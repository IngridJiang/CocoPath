/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Factory
 * @model kind="package"
 * @generated
 */
public interface Model2Package extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "model2";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://vitruv.tools/methodologisttemplate/model2";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "model2";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    Model2Package eINSTANCE = tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl.init();

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ControlSystemImpl <em>Control System</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.ControlSystemImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getControlSystem()
     * @generated
     */
    int CONTROL_SYSTEM = 0;

    /**
     * The feature id for the '<em><b>Axle Units</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_SYSTEM__AXLE_UNITS = 0;

    /**
     * The feature id for the '<em><b>Actuators</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_SYSTEM__ACTUATORS = 1;

    /**
     * The number of structural features of the '<em>Control System</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_SYSTEM_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Control System</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTROL_SYSTEM_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl <em>Axle Control Unit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getAxleControlUnit()
     * @generated
     */
    int AXLE_CONTROL_UNIT = 1;

    /**
     * The feature id for the '<em><b>Abs Decel Threshold</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD = 0;

    /**
     * The feature id for the '<em><b>Max Braking Torque</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE = 1;

    /**
     * The feature id for the '<em><b>Control Profile</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AXLE_CONTROL_UNIT__CONTROL_PROFILE = 2;

    /**
     * The feature id for the '<em><b>Calibration Offset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AXLE_CONTROL_UNIT__CALIBRATION_OFFSET = 3;

    /**
     * The feature id for the '<em><b>Effective Brake Gain</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN = 4;

    /**
     * The number of structural features of the '<em>Axle Control Unit</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AXLE_CONTROL_UNIT_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>Axle Control Unit</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AXLE_CONTROL_UNIT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl <em>Hydraulic Actuator Spec</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getHydraulicActuatorSpec()
     * @generated
     */
    int HYDRAULIC_ACTUATOR_SPEC = 2;

    /**
     * The feature id for the '<em><b>Pressure Response</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE = 0;

    /**
     * The feature id for the '<em><b>Total Piston Area</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA = 1;

    /**
     * The feature id for the '<em><b>Max Hydraulic Force</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE = 2;

    /**
     * The feature id for the '<em><b>Pistons</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HYDRAULIC_ACTUATOR_SPEC__PISTONS = 3;

    /**
     * The number of structural features of the '<em>Hydraulic Actuator Spec</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HYDRAULIC_ACTUATOR_SPEC_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Hydraulic Actuator Spec</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HYDRAULIC_ACTUATOR_SPEC_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.PistonSpecImpl <em>Piston Spec</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.PistonSpecImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getPistonSpec()
     * @generated
     */
    int PISTON_SPEC = 3;

    /**
     * The feature id for the '<em><b>Piston Area</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PISTON_SPEC__PISTON_AREA = 0;

    /**
     * The number of structural features of the '<em>Piston Spec</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PISTON_SPEC_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Piston Spec</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PISTON_SPEC_OPERATION_COUNT = 0;

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.ControlSystem <em>Control System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Control System</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ControlSystem
     * @generated
     */
    EClass getControlSystem();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model2.ControlSystem#getAxleUnits <em>Axle Units</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Axle Units</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ControlSystem#getAxleUnits()
     * @see #getControlSystem()
     * @generated
     */
    EReference getControlSystem_AxleUnits();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model2.ControlSystem#getActuators <em>Actuators</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Actuators</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ControlSystem#getActuators()
     * @see #getControlSystem()
     * @generated
     */
    EReference getControlSystem_Actuators();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit <em>Axle Control Unit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Axle Control Unit</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit
     * @generated
     */
    EClass getAxleControlUnit();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getAbsDecelThreshold <em>Abs Decel Threshold</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Abs Decel Threshold</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getAbsDecelThreshold()
     * @see #getAxleControlUnit()
     * @generated
     */
    EAttribute getAxleControlUnit_AbsDecelThreshold();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getMaxBrakingTorque <em>Max Braking Torque</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Braking Torque</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getMaxBrakingTorque()
     * @see #getAxleControlUnit()
     * @generated
     */
    EAttribute getAxleControlUnit_MaxBrakingTorque();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getControlProfile <em>Control Profile</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Control Profile</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getControlProfile()
     * @see #getAxleControlUnit()
     * @generated
     */
    EAttribute getAxleControlUnit_ControlProfile();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getCalibrationOffset <em>Calibration Offset</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Calibration Offset</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getCalibrationOffset()
     * @see #getAxleControlUnit()
     * @generated
     */
    EAttribute getAxleControlUnit_CalibrationOffset();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getEffectiveBrakeGain <em>Effective Brake Gain</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Effective Brake Gain</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getEffectiveBrakeGain()
     * @see #getAxleControlUnit()
     * @generated
     */
    EAttribute getAxleControlUnit_EffectiveBrakeGain();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec <em>Hydraulic Actuator Spec</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Hydraulic Actuator Spec</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec
     * @generated
     */
    EClass getHydraulicActuatorSpec();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getPressureResponse <em>Pressure Response</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Pressure Response</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getPressureResponse()
     * @see #getHydraulicActuatorSpec()
     * @generated
     */
    EAttribute getHydraulicActuatorSpec_PressureResponse();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getTotalPistonArea <em>Total Piston Area</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Total Piston Area</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getTotalPistonArea()
     * @see #getHydraulicActuatorSpec()
     * @generated
     */
    EAttribute getHydraulicActuatorSpec_TotalPistonArea();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getMaxHydraulicForce <em>Max Hydraulic Force</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Hydraulic Force</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getMaxHydraulicForce()
     * @see #getHydraulicActuatorSpec()
     * @generated
     */
    EAttribute getHydraulicActuatorSpec_MaxHydraulicForce();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getPistons <em>Pistons</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Pistons</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getPistons()
     * @see #getHydraulicActuatorSpec()
     * @generated
     */
    EReference getHydraulicActuatorSpec_Pistons();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.PistonSpec <em>Piston Spec</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Piston Spec</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.PistonSpec
     * @generated
     */
    EClass getPistonSpec();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.PistonSpec#getPistonArea <em>Piston Area</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Piston Area</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.PistonSpec#getPistonArea()
     * @see #getPistonSpec()
     * @generated
     */
    EAttribute getPistonSpec_PistonArea();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    Model2Factory getModel2Factory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ControlSystemImpl <em>Control System</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.ControlSystemImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getControlSystem()
         * @generated
         */
        EClass CONTROL_SYSTEM = eINSTANCE.getControlSystem();

        /**
         * The meta object literal for the '<em><b>Axle Units</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTROL_SYSTEM__AXLE_UNITS = eINSTANCE.getControlSystem_AxleUnits();

        /**
         * The meta object literal for the '<em><b>Actuators</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTROL_SYSTEM__ACTUATORS = eINSTANCE.getControlSystem_Actuators();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl <em>Axle Control Unit</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getAxleControlUnit()
         * @generated
         */
        EClass AXLE_CONTROL_UNIT = eINSTANCE.getAxleControlUnit();

        /**
         * The meta object literal for the '<em><b>Abs Decel Threshold</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD = eINSTANCE.getAxleControlUnit_AbsDecelThreshold();

        /**
         * The meta object literal for the '<em><b>Max Braking Torque</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE = eINSTANCE.getAxleControlUnit_MaxBrakingTorque();

        /**
         * The meta object literal for the '<em><b>Control Profile</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute AXLE_CONTROL_UNIT__CONTROL_PROFILE = eINSTANCE.getAxleControlUnit_ControlProfile();

        /**
         * The meta object literal for the '<em><b>Calibration Offset</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute AXLE_CONTROL_UNIT__CALIBRATION_OFFSET = eINSTANCE.getAxleControlUnit_CalibrationOffset();

        /**
         * The meta object literal for the '<em><b>Effective Brake Gain</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN = eINSTANCE.getAxleControlUnit_EffectiveBrakeGain();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl <em>Hydraulic Actuator Spec</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getHydraulicActuatorSpec()
         * @generated
         */
        EClass HYDRAULIC_ACTUATOR_SPEC = eINSTANCE.getHydraulicActuatorSpec();

        /**
         * The meta object literal for the '<em><b>Pressure Response</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE = eINSTANCE.getHydraulicActuatorSpec_PressureResponse();

        /**
         * The meta object literal for the '<em><b>Total Piston Area</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA = eINSTANCE.getHydraulicActuatorSpec_TotalPistonArea();

        /**
         * The meta object literal for the '<em><b>Max Hydraulic Force</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE =
                eINSTANCE.getHydraulicActuatorSpec_MaxHydraulicForce();

        /**
         * The meta object literal for the '<em><b>Pistons</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference HYDRAULIC_ACTUATOR_SPEC__PISTONS = eINSTANCE.getHydraulicActuatorSpec_Pistons();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.PistonSpecImpl <em>Piston Spec</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.PistonSpecImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getPistonSpec()
         * @generated
         */
        EClass PISTON_SPEC = eINSTANCE.getPistonSpec();

        /**
         * The meta object literal for the '<em><b>Piston Area</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PISTON_SPEC__PISTON_AREA = eINSTANCE.getPistonSpec_PistonArea();
    }
} // Model2Package
