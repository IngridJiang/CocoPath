/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import tools.vitruv.methodologisttemplate.model.model.AscetModule;
import tools.vitruv.methodologisttemplate.model.model.InitTask;
import tools.vitruv.methodologisttemplate.model.model.InterruptTask;
import tools.vitruv.methodologisttemplate.model.model.Method;
import tools.vitruv.methodologisttemplate.model.model.ModelFactory;
import tools.vitruv.methodologisttemplate.model.model.ModelPackage;
import tools.vitruv.methodologisttemplate.model.model.Named;
import tools.vitruv.methodologisttemplate.model.model.PeriodicTask;
import tools.vitruv.methodologisttemplate.model.model.SoftwareTask;
import tools.vitruv.methodologisttemplate.model.model.Task;
import tools.vitruv.methodologisttemplate.model.model.TimeTableTask;

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
    private EClass ascetModuleEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass namedEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass taskEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass methodEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass interruptTaskEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass initTaskEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass periodicTaskEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass softwareTaskEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass timeTableTaskEClass = null;

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
    public EClass getAscetModule() {
        return ascetModuleEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getAscetModule_Tasks() {
        return (EReference) ascetModuleEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getNamed() {
        return namedEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getNamed_Name() {
        return (EAttribute) namedEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getTask() {
        return taskEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTask_Priority() {
        return (EAttribute) taskEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getTask_Processes() {
        return (EReference) taskEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getMethod() {
        return methodEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getInterruptTask() {
        return interruptTaskEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getInitTask() {
        return initTaskEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getPeriodicTask() {
        return periodicTaskEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getPeriodicTask_Period() {
        return (EAttribute) periodicTaskEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getPeriodicTask_Delay() {
        return (EAttribute) periodicTaskEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getSoftwareTask() {
        return softwareTaskEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getTimeTableTask() {
        return timeTableTaskEClass;
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
        ascetModuleEClass = createEClass(ASCET_MODULE);
        createEReference(ascetModuleEClass, ASCET_MODULE__TASKS);

        namedEClass = createEClass(NAMED);
        createEAttribute(namedEClass, NAMED__NAME);

        taskEClass = createEClass(TASK);
        createEAttribute(taskEClass, TASK__PRIORITY);
        createEReference(taskEClass, TASK__PROCESSES);

        methodEClass = createEClass(METHOD);

        interruptTaskEClass = createEClass(INTERRUPT_TASK);

        initTaskEClass = createEClass(INIT_TASK);

        periodicTaskEClass = createEClass(PERIODIC_TASK);
        createEAttribute(periodicTaskEClass, PERIODIC_TASK__PERIOD);
        createEAttribute(periodicTaskEClass, PERIODIC_TASK__DELAY);

        softwareTaskEClass = createEClass(SOFTWARE_TASK);

        timeTableTaskEClass = createEClass(TIME_TABLE_TASK);
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
        taskEClass.getESuperTypes().add(this.getNamed());
        interruptTaskEClass.getESuperTypes().add(this.getTask());
        initTaskEClass.getESuperTypes().add(this.getTask());
        periodicTaskEClass.getESuperTypes().add(this.getTask());
        softwareTaskEClass.getESuperTypes().add(this.getTask());
        timeTableTaskEClass.getESuperTypes().add(this.getTask());

        // Initialize classes, features, and operations; add parameters
        initEClass(
                ascetModuleEClass,
                AscetModule.class,
                "AscetModule",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getAscetModule_Tasks(),
                this.getTask(),
                null,
                "tasks",
                null,
                0,
                -1,
                AscetModule.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                IS_COMPOSITE,
                !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(namedEClass, Named.class, "Named", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getNamed_Name(),
                ecorePackage.getEString(),
                "name",
                null,
                0,
                1,
                Named.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(taskEClass, Task.class, "Task", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getTask_Priority(),
                ecorePackage.getEInt(),
                "priority",
                "0",
                0,
                1,
                Task.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEReference(
                getTask_Processes(),
                this.getMethod(),
                null,
                "processes",
                null,
                0,
                1,
                Task.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_COMPOSITE,
                IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(methodEClass, Method.class, "Method", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(
                interruptTaskEClass,
                InterruptTask.class,
                "InterruptTask",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);

        initEClass(
                initTaskEClass, InitTask.class, "InitTask", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(
                periodicTaskEClass,
                PeriodicTask.class,
                "PeriodicTask",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getPeriodicTask_Period(),
                ecorePackage.getEDouble(),
                "period",
                "0.0",
                0,
                1,
                PeriodicTask.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getPeriodicTask_Delay(),
                ecorePackage.getEDouble(),
                "delay",
                "0.0",
                0,
                1,
                PeriodicTask.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                softwareTaskEClass,
                SoftwareTask.class,
                "SoftwareTask",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);

        initEClass(
                timeTableTaskEClass,
                TimeTableTask.class,
                "TimeTableTask",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }
} // ModelPackageImpl
