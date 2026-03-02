/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model.BrakePiston;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;
import tools.vitruv.methodologisttemplate.model.model.ModelFactory;
import tools.vitruv.methodologisttemplate.model.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelPackageImpl extends EPackageImpl implements ModelPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass brakeSystemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass brakeDiscEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass brakePistonEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass brakeCaliperEClass = null;

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
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ModelPackageImpl() {
        super(eNS_URI, ModelFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link ModelPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static ModelPackage init() {
        if (isInited) return (ModelPackage) EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);

        // Obtain or create and register package
        Object registeredModelPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
        ModelPackageImpl theModelPackage = registeredModelPackage instanceof ModelPackageImpl
                ? (ModelPackageImpl) registeredModelPackage
                : new ModelPackageImpl();

        isInited = true;

        // Create package meta-data objects
        theModelPackage.createPackageContents();

        // Initialize created meta-data
        theModelPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theModelPackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(ModelPackage.eNS_URI, theModelPackage);
        return theModelPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getBrakeSystem() {
        return brakeSystemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getBrakeSystem_BrakeDiscs() {
        return (EReference) brakeSystemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getBrakeSystem_Calipers() {
        return (EReference) brakeSystemEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getBrakeDisc() {
        return brakeDiscEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakeDisc_Diameter() {
        return (EAttribute) brakeDiscEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakeDisc_Material() {
        return (EAttribute) brakeDiscEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakeDisc_CoolingVanes() {
        return (EAttribute) brakeDiscEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakeDisc_Thickness() {
        return (EAttribute) brakeDiscEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getBrakePiston() {
        return brakePistonEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakePiston_Diameter() {
        return (EAttribute) brakePistonEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakePiston_StrokeLength() {
        return (EAttribute) brakePistonEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakePiston_Material() {
        return (EAttribute) brakePistonEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getBrakeCaliper() {
        return brakeCaliperEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakeCaliper_PistonCount() {
        return (EAttribute) brakeCaliperEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakeCaliper_ClampOpeningWidth() {
        return (EAttribute) brakeCaliperEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getBrakeCaliper_MaxPressure() {
        return (EAttribute) brakeCaliperEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getBrakeCaliper_Pistons() {
        return (EReference) brakeCaliperEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ModelFactory getModelFactory() {
        return (ModelFactory) getEFactoryInstance();
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
        brakeSystemEClass = createEClass(BRAKE_SYSTEM);
        createEReference(brakeSystemEClass, BRAKE_SYSTEM__BRAKE_DISCS);
        createEReference(brakeSystemEClass, BRAKE_SYSTEM__CALIPERS);

        brakeDiscEClass = createEClass(BRAKE_DISC);
        createEAttribute(brakeDiscEClass, BRAKE_DISC__DIAMETER);
        createEAttribute(brakeDiscEClass, BRAKE_DISC__MATERIAL);
        createEAttribute(brakeDiscEClass, BRAKE_DISC__COOLING_VANES);
        createEAttribute(brakeDiscEClass, BRAKE_DISC__THICKNESS);

        brakePistonEClass = createEClass(BRAKE_PISTON);
        createEAttribute(brakePistonEClass, BRAKE_PISTON__DIAMETER);
        createEAttribute(brakePistonEClass, BRAKE_PISTON__STROKE_LENGTH);
        createEAttribute(brakePistonEClass, BRAKE_PISTON__MATERIAL);

        brakeCaliperEClass = createEClass(BRAKE_CALIPER);
        createEAttribute(brakeCaliperEClass, BRAKE_CALIPER__PISTON_COUNT);
        createEAttribute(brakeCaliperEClass, BRAKE_CALIPER__CLAMP_OPENING_WIDTH);
        createEAttribute(brakeCaliperEClass, BRAKE_CALIPER__MAX_PRESSURE);
        createEReference(brakeCaliperEClass, BRAKE_CALIPER__PISTONS);
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
                brakeSystemEClass,
                BrakeSystem.class,
                "BrakeSystem",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getBrakeSystem_BrakeDiscs(),
                this.getBrakeDisc(),
                null,
                "brakeDiscs",
                null,
                0,
                -1,
                BrakeSystem.class,
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
                getBrakeSystem_Calipers(),
                this.getBrakeCaliper(),
                null,
                "calipers",
                null,
                0,
                -1,
                BrakeSystem.class,
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
                brakeDiscEClass,
                BrakeDisc.class,
                "BrakeDisc",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getBrakeDisc_Diameter(),
                ecorePackage.getEDouble(),
                "diameter",
                null,
                1,
                1,
                BrakeDisc.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getBrakeDisc_Material(),
                ecorePackage.getEString(),
                "material",
                null,
                1,
                1,
                BrakeDisc.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getBrakeDisc_CoolingVanes(),
                ecorePackage.getEInt(),
                "coolingVanes",
                null,
                1,
                1,
                BrakeDisc.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getBrakeDisc_Thickness(),
                ecorePackage.getEDouble(),
                "thickness",
                null,
                1,
                1,
                BrakeDisc.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                brakePistonEClass,
                BrakePiston.class,
                "BrakePiston",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getBrakePiston_Diameter(),
                ecorePackage.getEDouble(),
                "diameter",
                null,
                1,
                1,
                BrakePiston.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getBrakePiston_StrokeLength(),
                ecorePackage.getEDouble(),
                "strokeLength",
                null,
                1,
                1,
                BrakePiston.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getBrakePiston_Material(),
                ecorePackage.getEString(),
                "material",
                null,
                1,
                1,
                BrakePiston.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                brakeCaliperEClass,
                BrakeCaliper.class,
                "BrakeCaliper",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getBrakeCaliper_PistonCount(),
                ecorePackage.getEInt(),
                "pistonCount",
                null,
                1,
                1,
                BrakeCaliper.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getBrakeCaliper_ClampOpeningWidth(),
                ecorePackage.getEDouble(),
                "clampOpeningWidth",
                null,
                1,
                1,
                BrakeCaliper.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getBrakeCaliper_MaxPressure(),
                ecorePackage.getEDouble(),
                "maxPressure",
                null,
                1,
                1,
                BrakeCaliper.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEReference(
                getBrakeCaliper_Pistons(),
                this.getBrakePiston(),
                null,
                "pistons",
                null,
                0,
                -1,
                BrakeCaliper.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                IS_COMPOSITE,
                !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }
} // ModelPackageImpl
