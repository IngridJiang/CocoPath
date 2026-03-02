/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit;
import tools.vitruv.methodologisttemplate.model.model2.ControlSystem;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;
import tools.vitruv.methodologisttemplate.model.model2.Model2Factory;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.PistonSpec;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Model2PackageImpl extends EPackageImpl implements Model2Package {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass controlSystemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass axleControlUnitEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass hydraulicActuatorSpecEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass pistonSpecEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#eNS_URI
     * @see #init()
     * @generated
     */
    private Model2PackageImpl() {
        super(eNS_URI, Model2Factory.eINSTANCE);
    }
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     *
     * <p>This method is used to initialize {@link Model2Package#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static Model2Package init() {
        if (isInited) return (Model2Package) EPackage.Registry.INSTANCE.getEPackage(Model2Package.eNS_URI);

        // Obtain or create and register package
        Object registeredModel2Package = EPackage.Registry.INSTANCE.get(eNS_URI);
        Model2PackageImpl theModel2Package = registeredModel2Package instanceof Model2PackageImpl
                ? (Model2PackageImpl) registeredModel2Package
                : new Model2PackageImpl();

        isInited = true;

        // Create package meta-data objects
        theModel2Package.createPackageContents();

        // Initialize created meta-data
        theModel2Package.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theModel2Package.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(Model2Package.eNS_URI, theModel2Package);
        return theModel2Package;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getControlSystem() {
        return controlSystemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getControlSystem_AxleUnits() {
        return (EReference) controlSystemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getControlSystem_Actuators() {
        return (EReference) controlSystemEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getAxleControlUnit() {
        return axleControlUnitEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getAxleControlUnit_AbsDecelThreshold() {
        return (EAttribute) axleControlUnitEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getAxleControlUnit_MaxBrakingTorque() {
        return (EAttribute) axleControlUnitEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getAxleControlUnit_ControlProfile() {
        return (EAttribute) axleControlUnitEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getAxleControlUnit_CalibrationOffset() {
        return (EAttribute) axleControlUnitEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getAxleControlUnit_EffectiveBrakeGain() {
        return (EAttribute) axleControlUnitEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getHydraulicActuatorSpec() {
        return hydraulicActuatorSpecEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getHydraulicActuatorSpec_PressureResponse() {
        return (EAttribute) hydraulicActuatorSpecEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getHydraulicActuatorSpec_TotalPistonArea() {
        return (EAttribute) hydraulicActuatorSpecEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getHydraulicActuatorSpec_MaxHydraulicForce() {
        return (EAttribute) hydraulicActuatorSpecEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getHydraulicActuatorSpec_Pistons() {
        return (EReference) hydraulicActuatorSpecEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getPistonSpec() {
        return pistonSpecEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getPistonSpec_PistonArea() {
        return (EAttribute) pistonSpecEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Model2Factory getModel2Factory() {
        return (Model2Factory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        controlSystemEClass = createEClass(CONTROL_SYSTEM);
        createEReference(controlSystemEClass, CONTROL_SYSTEM__AXLE_UNITS);
        createEReference(controlSystemEClass, CONTROL_SYSTEM__ACTUATORS);

        axleControlUnitEClass = createEClass(AXLE_CONTROL_UNIT);
        createEAttribute(axleControlUnitEClass, AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD);
        createEAttribute(axleControlUnitEClass, AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE);
        createEAttribute(axleControlUnitEClass, AXLE_CONTROL_UNIT__CONTROL_PROFILE);
        createEAttribute(axleControlUnitEClass, AXLE_CONTROL_UNIT__CALIBRATION_OFFSET);
        createEAttribute(axleControlUnitEClass, AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN);

        hydraulicActuatorSpecEClass = createEClass(HYDRAULIC_ACTUATOR_SPEC);
        createEAttribute(hydraulicActuatorSpecEClass, HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE);
        createEAttribute(hydraulicActuatorSpecEClass, HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA);
        createEAttribute(hydraulicActuatorSpecEClass, HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE);
        createEReference(hydraulicActuatorSpecEClass, HYDRAULIC_ACTUATOR_SPEC__PISTONS);

        pistonSpecEClass = createEClass(PISTON_SPEC);
        createEAttribute(pistonSpecEClass, PISTON_SPEC__PISTON_AREA);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass(
                controlSystemEClass,
                ControlSystem.class,
                "ControlSystem",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getControlSystem_AxleUnits(),
                this.getAxleControlUnit(),
                null,
                "axleUnits",
                null,
                0,
                -1,
                ControlSystem.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                IS_COMPOSITE,
                !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEReference(
                getControlSystem_Actuators(),
                this.getHydraulicActuatorSpec(),
                null,
                "actuators",
                null,
                0,
                -1,
                ControlSystem.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                IS_COMPOSITE,
                !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                axleControlUnitEClass,
                AxleControlUnit.class,
                "AxleControlUnit",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getAxleControlUnit_AbsDecelThreshold(),
                ecorePackage.getEDouble(),
                "absDecelThreshold",
                null,
                1,
                1,
                AxleControlUnit.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getAxleControlUnit_MaxBrakingTorque(),
                ecorePackage.getEDouble(),
                "maxBrakingTorque",
                null,
                1,
                1,
                AxleControlUnit.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getAxleControlUnit_ControlProfile(),
                ecorePackage.getEString(),
                "controlProfile",
                null,
                0,
                1,
                AxleControlUnit.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getAxleControlUnit_CalibrationOffset(),
                ecorePackage.getEDouble(),
                "calibrationOffset",
                null,
                1,
                1,
                AxleControlUnit.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getAxleControlUnit_EffectiveBrakeGain(),
                ecorePackage.getEDouble(),
                "effectiveBrakeGain",
                null,
                1,
                1,
                AxleControlUnit.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                hydraulicActuatorSpecEClass,
                HydraulicActuatorSpec.class,
                "HydraulicActuatorSpec",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getHydraulicActuatorSpec_PressureResponse(),
                ecorePackage.getEString(),
                "pressureResponse",
                null,
                0,
                1,
                HydraulicActuatorSpec.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getHydraulicActuatorSpec_TotalPistonArea(),
                ecorePackage.getEDouble(),
                "totalPistonArea",
                null,
                1,
                1,
                HydraulicActuatorSpec.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getHydraulicActuatorSpec_MaxHydraulicForce(),
                ecorePackage.getEDouble(),
                "maxHydraulicForce",
                null,
                1,
                1,
                HydraulicActuatorSpec.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEReference(
                getHydraulicActuatorSpec_Pistons(),
                this.getPistonSpec(),
                null,
                "pistons",
                null,
                0,
                -1,
                HydraulicActuatorSpec.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                IS_COMPOSITE,
                !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                pistonSpecEClass,
                PistonSpec.class,
                "PistonSpec",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getPistonSpec_PistonArea(),
                ecorePackage.getEDouble(),
                "pistonArea",
                null,
                1,
                1,
                PistonSpec.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }
} // Model2PackageImpl
