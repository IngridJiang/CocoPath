/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry;
import tools.vitruv.methodologisttemplate.model.model2.CallSequence;
import tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry;
import tools.vitruv.methodologisttemplate.model.model2.Label;
import tools.vitruv.methodologisttemplate.model.model2.LabelSwitch;
import tools.vitruv.methodologisttemplate.model.model2.Model2Factory;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry;
import tools.vitruv.methodologisttemplate.model.model2.Task;
import tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall;

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
    private EClass componentContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass processEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass isrEClass = null;

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
    private EClass runnableEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass taskRunnableCallEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass labelSwitchEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass labelEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass probabilitySwitchEntryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass callGraphEntryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass graphEntryEntryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass callSequenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass callSequenceEntryEClass = null;

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
    public EClass getComponentContainer() {
        return componentContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getComponentContainer_Tasks() {
        return (EReference) componentContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getComponentContainer_Isrs() {
        return (EReference) componentContainerEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getComponentContainer_Runnables() {
        return (EReference) componentContainerEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getComponentContainer_Labels() {
        return (EReference) componentContainerEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getProcess() {
        return processEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getISR() {
        return isrEClass;
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
    public EAttribute getTask_Name() {
        return (EAttribute) taskEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTask_Preemption() {
        return (EAttribute) taskEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getTask_MultipleTaskActivationLimit() {
        return (EAttribute) taskEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getRunnable() {
        return runnableEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getTaskRunnableCall() {
        return taskRunnableCallEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getTaskRunnableCall_Runnable() {
        return (EReference) taskRunnableCallEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getLabelSwitch() {
        return labelSwitchEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getLabelSwitch_Label() {
        return (EReference) labelSwitchEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getLabel() {
        return labelEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getLabel_InitialValue() {
        return (EAttribute) labelEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getLabel_Constant() {
        return (EAttribute) labelEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getLabel_IsVolatile() {
        return (EAttribute) labelEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getProbabilitySwitchEntry() {
        return probabilitySwitchEntryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getProbabilitySwitchEntry_Probability() {
        return (EAttribute)
                probabilitySwitchEntryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getCallGraphEntry() {
        return callGraphEntryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getCallGraphEntry_GraphEntries() {
        return (EReference) callGraphEntryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getGraphEntryEntry() {
        return graphEntryEntryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getGraphEntryEntry_GraphEntries() {
        return (EReference) graphEntryEntryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getGraphEntryEntry_Entries() {
        return (EReference) graphEntryEntryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getCallSequence() {
        return callSequenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getCallSequence_Calls() {
        return (EReference) callSequenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getCallSequenceEntry() {
        return callSequenceEntryEClass;
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
        componentContainerEClass = createEClass(COMPONENT_CONTAINER);
        createEReference(componentContainerEClass, COMPONENT_CONTAINER__TASKS);
        createEReference(componentContainerEClass, COMPONENT_CONTAINER__ISRS);
        createEReference(componentContainerEClass, COMPONENT_CONTAINER__RUNNABLES);
        createEReference(componentContainerEClass, COMPONENT_CONTAINER__LABELS);

        processEClass = createEClass(PROCESS);

        isrEClass = createEClass(ISR);

        taskEClass = createEClass(TASK);
        createEAttribute(taskEClass, TASK__NAME);
        createEAttribute(taskEClass, TASK__PREEMPTION);
        createEAttribute(taskEClass, TASK__MULTIPLE_TASK_ACTIVATION_LIMIT);

        runnableEClass = createEClass(RUNNABLE);

        taskRunnableCallEClass = createEClass(TASK_RUNNABLE_CALL);
        createEReference(taskRunnableCallEClass, TASK_RUNNABLE_CALL__RUNNABLE);

        labelSwitchEClass = createEClass(LABEL_SWITCH);
        createEReference(labelSwitchEClass, LABEL_SWITCH__LABEL);

        labelEClass = createEClass(LABEL);
        createEAttribute(labelEClass, LABEL__INITIAL_VALUE);
        createEAttribute(labelEClass, LABEL__CONSTANT);
        createEAttribute(labelEClass, LABEL__IS_VOLATILE);

        probabilitySwitchEntryEClass = createEClass(PROBABILITY_SWITCH_ENTRY);
        createEAttribute(probabilitySwitchEntryEClass, PROBABILITY_SWITCH_ENTRY__PROBABILITY);

        callGraphEntryEClass = createEClass(CALL_GRAPH_ENTRY);
        createEReference(callGraphEntryEClass, CALL_GRAPH_ENTRY__GRAPH_ENTRIES);

        graphEntryEntryEClass = createEClass(GRAPH_ENTRY_ENTRY);
        createEReference(graphEntryEntryEClass, GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES);
        createEReference(graphEntryEntryEClass, GRAPH_ENTRY_ENTRY__ENTRIES);

        callSequenceEClass = createEClass(CALL_SEQUENCE);
        createEReference(callSequenceEClass, CALL_SEQUENCE__CALLS);

        callSequenceEntryEClass = createEClass(CALL_SEQUENCE_ENTRY);
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
        isrEClass.getESuperTypes().add(this.getProcess());
        taskEClass.getESuperTypes().add(this.getProcess());

        // Initialize classes, features, and operations; add parameters
        initEClass(
                componentContainerEClass,
                ComponentContainer.class,
                "ComponentContainer",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getComponentContainer_Tasks(),
                this.getTask(),
                null,
                "tasks",
                null,
                0,
                -1,
                ComponentContainer.class,
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
                getComponentContainer_Isrs(),
                this.getISR(),
                null,
                "isrs",
                null,
                0,
                -1,
                ComponentContainer.class,
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
                getComponentContainer_Runnables(),
                this.getRunnable(),
                null,
                "runnables",
                null,
                0,
                -1,
                ComponentContainer.class,
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
                getComponentContainer_Labels(),
                this.getLabel(),
                null,
                "labels",
                null,
                0,
                -1,
                ComponentContainer.class,
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
                processEClass,
                tools.vitruv.methodologisttemplate.model.model2.Process.class,
                "Process",
                IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);

        initEClass(
                isrEClass,
                tools.vitruv.methodologisttemplate.model.model2.ISR.class,
                "ISR",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);

        initEClass(taskEClass, Task.class, "Task", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getTask_Name(),
                ecorePackage.getEString(),
                "name",
                null,
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
        initEAttribute(
                getTask_Preemption(),
                ecorePackage.getEString(),
                "preemption",
                "cooperative",
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
        initEAttribute(
                getTask_MultipleTaskActivationLimit(),
                ecorePackage.getEInt(),
                "multipleTaskActivationLimit",
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

        initEClass(
                runnableEClass,
                tools.vitruv.methodologisttemplate.model.model2.Runnable.class,
                "Runnable",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);

        initEClass(
                taskRunnableCallEClass,
                TaskRunnableCall.class,
                "TaskRunnableCall",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getTaskRunnableCall_Runnable(),
                this.getRunnable(),
                null,
                "runnable",
                null,
                0,
                1,
                TaskRunnableCall.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_COMPOSITE,
                IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                labelSwitchEClass,
                LabelSwitch.class,
                "LabelSwitch",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getLabelSwitch_Label(),
                this.getLabel(),
                null,
                "label",
                null,
                0,
                1,
                LabelSwitch.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                IS_COMPOSITE,
                !IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(labelEClass, Label.class, "Label", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getLabel_InitialValue(),
                ecorePackage.getEInt(),
                "initialValue",
                null,
                0,
                1,
                Label.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getLabel_Constant(),
                ecorePackage.getEBoolean(),
                "constant",
                "false",
                0,
                1,
                Label.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEAttribute(
                getLabel_IsVolatile(),
                ecorePackage.getEBoolean(),
                "isVolatile",
                "false",
                0,
                1,
                Label.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                probabilitySwitchEntryEClass,
                ProbabilitySwitchEntry.class,
                "ProbabilitySwitchEntry",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(
                getProbabilitySwitchEntry_Probability(),
                ecorePackage.getEDouble(),
                "probability",
                "0.0",
                0,
                1,
                ProbabilitySwitchEntry.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_UNSETTABLE,
                !IS_ID,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                callGraphEntryEClass,
                CallGraphEntry.class,
                "CallGraphEntry",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getCallGraphEntry_GraphEntries(),
                this.getGraphEntryEntry(),
                null,
                "graphEntries",
                null,
                0,
                -1,
                CallGraphEntry.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_COMPOSITE,
                IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                graphEntryEntryEClass,
                GraphEntryEntry.class,
                "GraphEntryEntry",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getGraphEntryEntry_GraphEntries(),
                this.getGraphEntryEntry(),
                null,
                "graphEntries",
                null,
                0,
                -1,
                GraphEntryEntry.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_COMPOSITE,
                IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);
        initEReference(
                getGraphEntryEntry_Entries(),
                this.getCallSequence(),
                null,
                "entries",
                null,
                0,
                -1,
                GraphEntryEntry.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_COMPOSITE,
                IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                callSequenceEClass,
                CallSequence.class,
                "CallSequence",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);
        initEReference(
                getCallSequence_Calls(),
                this.getCallSequenceEntry(),
                null,
                "calls",
                null,
                0,
                -1,
                CallSequence.class,
                !IS_TRANSIENT,
                !IS_VOLATILE,
                IS_CHANGEABLE,
                !IS_COMPOSITE,
                IS_RESOLVE_PROXIES,
                !IS_UNSETTABLE,
                IS_UNIQUE,
                !IS_DERIVED,
                IS_ORDERED);

        initEClass(
                callSequenceEntryEClass,
                CallSequenceEntry.class,
                "CallSequenceEntry",
                !IS_ABSTRACT,
                !IS_INTERFACE,
                IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }
} // Model2PackageImpl
