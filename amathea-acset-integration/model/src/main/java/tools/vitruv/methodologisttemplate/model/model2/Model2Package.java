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
    String eNS_URI = "http://vitruv.tools/reactionsparser/model2";

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
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl <em>Component Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getComponentContainer()
     * @generated
     */
    int COMPONENT_CONTAINER = 0;

    /**
     * The feature id for the '<em><b>Tasks</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_CONTAINER__TASKS = 0;

    /**
     * The feature id for the '<em><b>Isrs</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_CONTAINER__ISRS = 1;

    /**
     * The feature id for the '<em><b>Runnables</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_CONTAINER__RUNNABLES = 2;

    /**
     * The feature id for the '<em><b>Labels</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_CONTAINER__LABELS = 3;

    /**
     * The number of structural features of the '<em>Component Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_CONTAINER_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Component Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_CONTAINER_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ProcessImpl <em>Process</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.ProcessImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getProcess()
     * @generated
     */
    int PROCESS = 1;

    /**
     * The number of structural features of the '<em>Process</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESS_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Process</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESS_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ISRImpl <em>ISR</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.ISRImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getISR()
     * @generated
     */
    int ISR = 2;

    /**
     * The number of structural features of the '<em>ISR</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ISR_FEATURE_COUNT = PROCESS_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>ISR</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ISR_OPERATION_COUNT = PROCESS_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskImpl <em>Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.TaskImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getTask()
     * @generated
     */
    int TASK = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK__NAME = PROCESS_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Preemption</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK__PREEMPTION = PROCESS_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Multiple Task Activation Limit</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK__MULTIPLE_TASK_ACTIVATION_LIMIT = PROCESS_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK_FEATURE_COUNT = PROCESS_FEATURE_COUNT + 3;

    /**
     * The number of operations of the '<em>Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK_OPERATION_COUNT = PROCESS_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.RunnableImpl <em>Runnable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.RunnableImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getRunnable()
     * @generated
     */
    int RUNNABLE = 4;

    /**
     * The number of structural features of the '<em>Runnable</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RUNNABLE_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Runnable</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RUNNABLE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskRunnableCallImpl <em>Task Runnable Call</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.TaskRunnableCallImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getTaskRunnableCall()
     * @generated
     */
    int TASK_RUNNABLE_CALL = 5;

    /**
     * The feature id for the '<em><b>Runnable</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK_RUNNABLE_CALL__RUNNABLE = 0;

    /**
     * The number of structural features of the '<em>Task Runnable Call</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK_RUNNABLE_CALL_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Task Runnable Call</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK_RUNNABLE_CALL_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.LabelSwitchImpl <em>Label Switch</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.LabelSwitchImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getLabelSwitch()
     * @generated
     */
    int LABEL_SWITCH = 6;

    /**
     * The feature id for the '<em><b>Label</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_SWITCH__LABEL = 0;

    /**
     * The number of structural features of the '<em>Label Switch</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_SWITCH_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Label Switch</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_SWITCH_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.LabelImpl <em>Label</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.LabelImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getLabel()
     * @generated
     */
    int LABEL = 7;

    /**
     * The feature id for the '<em><b>Initial Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__INITIAL_VALUE = 0;

    /**
     * The feature id for the '<em><b>Constant</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__CONSTANT = 1;

    /**
     * The feature id for the '<em><b>Is Volatile</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__IS_VOLATILE = 2;

    /**
     * The number of structural features of the '<em>Label</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Label</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ProbabilitySwitchEntryImpl <em>Probability Switch Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.ProbabilitySwitchEntryImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getProbabilitySwitchEntry()
     * @generated
     */
    int PROBABILITY_SWITCH_ENTRY = 8;

    /**
     * The feature id for the '<em><b>Probability</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBABILITY_SWITCH_ENTRY__PROBABILITY = 0;

    /**
     * The number of structural features of the '<em>Probability Switch Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBABILITY_SWITCH_ENTRY_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Probability Switch Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBABILITY_SWITCH_ENTRY_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallGraphEntryImpl <em>Call Graph Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.CallGraphEntryImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getCallGraphEntry()
     * @generated
     */
    int CALL_GRAPH_ENTRY = 9;

    /**
     * The feature id for the '<em><b>Graph Entries</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_GRAPH_ENTRY__GRAPH_ENTRIES = 0;

    /**
     * The number of structural features of the '<em>Call Graph Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_GRAPH_ENTRY_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Call Graph Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_GRAPH_ENTRY_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.GraphEntryEntryImpl <em>Graph Entry Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.GraphEntryEntryImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getGraphEntryEntry()
     * @generated
     */
    int GRAPH_ENTRY_ENTRY = 10;

    /**
     * The feature id for the '<em><b>Graph Entries</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES = 0;

    /**
     * The feature id for the '<em><b>Entries</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GRAPH_ENTRY_ENTRY__ENTRIES = 1;

    /**
     * The number of structural features of the '<em>Graph Entry Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GRAPH_ENTRY_ENTRY_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Graph Entry Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GRAPH_ENTRY_ENTRY_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceImpl <em>Call Sequence</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getCallSequence()
     * @generated
     */
    int CALL_SEQUENCE = 11;

    /**
     * The feature id for the '<em><b>Calls</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_SEQUENCE__CALLS = 0;

    /**
     * The number of structural features of the '<em>Call Sequence</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_SEQUENCE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Call Sequence</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_SEQUENCE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceEntryImpl <em>Call Sequence Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceEntryImpl
     * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getCallSequenceEntry()
     * @generated
     */
    int CALL_SEQUENCE_ENTRY = 12;

    /**
     * The number of structural features of the '<em>Call Sequence Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_SEQUENCE_ENTRY_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Call Sequence Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CALL_SEQUENCE_ENTRY_OPERATION_COUNT = 0;

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer <em>Component Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Component Container</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ComponentContainer
     * @generated
     */
    EClass getComponentContainer();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getTasks <em>Tasks</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Tasks</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getTasks()
     * @see #getComponentContainer()
     * @generated
     */
    EReference getComponentContainer_Tasks();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getIsrs <em>Isrs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Isrs</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getIsrs()
     * @see #getComponentContainer()
     * @generated
     */
    EReference getComponentContainer_Isrs();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getRunnables <em>Runnables</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Runnables</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getRunnables()
     * @see #getComponentContainer()
     * @generated
     */
    EReference getComponentContainer_Runnables();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getLabels <em>Labels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Labels</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getLabels()
     * @see #getComponentContainer()
     * @generated
     */
    EReference getComponentContainer_Labels();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.Process <em>Process</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Process</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Process
     * @generated
     */
    EClass getProcess();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.ISR <em>ISR</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>ISR</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ISR
     * @generated
     */
    EClass getISR();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.Task <em>Task</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Task</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Task
     * @generated
     */
    EClass getTask();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.Task#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Task#getName()
     * @see #getTask()
     * @generated
     */
    EAttribute getTask_Name();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.Task#getPreemption <em>Preemption</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Preemption</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Task#getPreemption()
     * @see #getTask()
     * @generated
     */
    EAttribute getTask_Preemption();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.Task#getMultipleTaskActivationLimit <em>Multiple Task Activation Limit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Multiple Task Activation Limit</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Task#getMultipleTaskActivationLimit()
     * @see #getTask()
     * @generated
     */
    EAttribute getTask_MultipleTaskActivationLimit();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.Runnable <em>Runnable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Runnable</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Runnable
     * @generated
     */
    EClass getRunnable();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall <em>Task Runnable Call</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Task Runnable Call</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall
     * @generated
     */
    EClass getTaskRunnableCall();

    /**
     * Returns the meta object for the reference '{@link tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall#getRunnable <em>Runnable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Runnable</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall#getRunnable()
     * @see #getTaskRunnableCall()
     * @generated
     */
    EReference getTaskRunnableCall_Runnable();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.LabelSwitch <em>Label Switch</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Label Switch</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.LabelSwitch
     * @generated
     */
    EClass getLabelSwitch();

    /**
     * Returns the meta object for the containment reference '{@link tools.vitruv.methodologisttemplate.model.model2.LabelSwitch#getLabel <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Label</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.LabelSwitch#getLabel()
     * @see #getLabelSwitch()
     * @generated
     */
    EReference getLabelSwitch_Label();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.Label <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Label</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Label
     * @generated
     */
    EClass getLabel();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.Label#getInitialValue <em>Initial Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Initial Value</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Label#getInitialValue()
     * @see #getLabel()
     * @generated
     */
    EAttribute getLabel_InitialValue();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.Label#isConstant <em>Constant</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Constant</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Label#isConstant()
     * @see #getLabel()
     * @generated
     */
    EAttribute getLabel_Constant();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.Label#isIsVolatile <em>Is Volatile</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Is Volatile</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.Label#isIsVolatile()
     * @see #getLabel()
     * @generated
     */
    EAttribute getLabel_IsVolatile();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry <em>Probability Switch Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Probability Switch Entry</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry
     * @generated
     */
    EClass getProbabilitySwitchEntry();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry#getProbability <em>Probability</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Probability</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry#getProbability()
     * @see #getProbabilitySwitchEntry()
     * @generated
     */
    EAttribute getProbabilitySwitchEntry_Probability();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry <em>Call Graph Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Call Graph Entry</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry
     * @generated
     */
    EClass getCallGraphEntry();

    /**
     * Returns the meta object for the reference list '{@link tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry#getGraphEntries <em>Graph Entries</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Graph Entries</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry#getGraphEntries()
     * @see #getCallGraphEntry()
     * @generated
     */
    EReference getCallGraphEntry_GraphEntries();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry <em>Graph Entry Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Graph Entry Entry</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry
     * @generated
     */
    EClass getGraphEntryEntry();

    /**
     * Returns the meta object for the reference list '{@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry#getGraphEntries <em>Graph Entries</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Graph Entries</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry#getGraphEntries()
     * @see #getGraphEntryEntry()
     * @generated
     */
    EReference getGraphEntryEntry_GraphEntries();

    /**
     * Returns the meta object for the reference list '{@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry#getEntries <em>Entries</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Entries</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry#getEntries()
     * @see #getGraphEntryEntry()
     * @generated
     */
    EReference getGraphEntryEntry_Entries();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.CallSequence <em>Call Sequence</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Call Sequence</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallSequence
     * @generated
     */
    EClass getCallSequence();

    /**
     * Returns the meta object for the reference list '{@link tools.vitruv.methodologisttemplate.model.model2.CallSequence#getCalls <em>Calls</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Calls</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallSequence#getCalls()
     * @see #getCallSequence()
     * @generated
     */
    EReference getCallSequence_Calls();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry <em>Call Sequence Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Call Sequence Entry</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry
     * @generated
     */
    EClass getCallSequenceEntry();

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
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl <em>Component Container</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getComponentContainer()
         * @generated
         */
        EClass COMPONENT_CONTAINER = eINSTANCE.getComponentContainer();

        /**
         * The meta object literal for the '<em><b>Tasks</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPONENT_CONTAINER__TASKS = eINSTANCE.getComponentContainer_Tasks();

        /**
         * The meta object literal for the '<em><b>Isrs</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPONENT_CONTAINER__ISRS = eINSTANCE.getComponentContainer_Isrs();

        /**
         * The meta object literal for the '<em><b>Runnables</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPONENT_CONTAINER__RUNNABLES = eINSTANCE.getComponentContainer_Runnables();

        /**
         * The meta object literal for the '<em><b>Labels</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPONENT_CONTAINER__LABELS = eINSTANCE.getComponentContainer_Labels();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ProcessImpl <em>Process</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.ProcessImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getProcess()
         * @generated
         */
        EClass PROCESS = eINSTANCE.getProcess();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ISRImpl <em>ISR</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.ISRImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getISR()
         * @generated
         */
        EClass ISR = eINSTANCE.getISR();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskImpl <em>Task</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.TaskImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getTask()
         * @generated
         */
        EClass TASK = eINSTANCE.getTask();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TASK__NAME = eINSTANCE.getTask_Name();

        /**
         * The meta object literal for the '<em><b>Preemption</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TASK__PREEMPTION = eINSTANCE.getTask_Preemption();

        /**
         * The meta object literal for the '<em><b>Multiple Task Activation Limit</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TASK__MULTIPLE_TASK_ACTIVATION_LIMIT = eINSTANCE.getTask_MultipleTaskActivationLimit();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.RunnableImpl <em>Runnable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.RunnableImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getRunnable()
         * @generated
         */
        EClass RUNNABLE = eINSTANCE.getRunnable();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskRunnableCallImpl <em>Task Runnable Call</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.TaskRunnableCallImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getTaskRunnableCall()
         * @generated
         */
        EClass TASK_RUNNABLE_CALL = eINSTANCE.getTaskRunnableCall();

        /**
         * The meta object literal for the '<em><b>Runnable</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TASK_RUNNABLE_CALL__RUNNABLE = eINSTANCE.getTaskRunnableCall_Runnable();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.LabelSwitchImpl <em>Label Switch</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.LabelSwitchImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getLabelSwitch()
         * @generated
         */
        EClass LABEL_SWITCH = eINSTANCE.getLabelSwitch();

        /**
         * The meta object literal for the '<em><b>Label</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LABEL_SWITCH__LABEL = eINSTANCE.getLabelSwitch_Label();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.LabelImpl <em>Label</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.LabelImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getLabel()
         * @generated
         */
        EClass LABEL = eINSTANCE.getLabel();

        /**
         * The meta object literal for the '<em><b>Initial Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LABEL__INITIAL_VALUE = eINSTANCE.getLabel_InitialValue();

        /**
         * The meta object literal for the '<em><b>Constant</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LABEL__CONSTANT = eINSTANCE.getLabel_Constant();

        /**
         * The meta object literal for the '<em><b>Is Volatile</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute LABEL__IS_VOLATILE = eINSTANCE.getLabel_IsVolatile();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.ProbabilitySwitchEntryImpl <em>Probability Switch Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.ProbabilitySwitchEntryImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getProbabilitySwitchEntry()
         * @generated
         */
        EClass PROBABILITY_SWITCH_ENTRY = eINSTANCE.getProbabilitySwitchEntry();

        /**
         * The meta object literal for the '<em><b>Probability</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PROBABILITY_SWITCH_ENTRY__PROBABILITY = eINSTANCE.getProbabilitySwitchEntry_Probability();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallGraphEntryImpl <em>Call Graph Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.CallGraphEntryImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getCallGraphEntry()
         * @generated
         */
        EClass CALL_GRAPH_ENTRY = eINSTANCE.getCallGraphEntry();

        /**
         * The meta object literal for the '<em><b>Graph Entries</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CALL_GRAPH_ENTRY__GRAPH_ENTRIES = eINSTANCE.getCallGraphEntry_GraphEntries();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.GraphEntryEntryImpl <em>Graph Entry Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.GraphEntryEntryImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getGraphEntryEntry()
         * @generated
         */
        EClass GRAPH_ENTRY_ENTRY = eINSTANCE.getGraphEntryEntry();

        /**
         * The meta object literal for the '<em><b>Graph Entries</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES = eINSTANCE.getGraphEntryEntry_GraphEntries();

        /**
         * The meta object literal for the '<em><b>Entries</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference GRAPH_ENTRY_ENTRY__ENTRIES = eINSTANCE.getGraphEntryEntry_Entries();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceImpl <em>Call Sequence</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getCallSequence()
         * @generated
         */
        EClass CALL_SEQUENCE = eINSTANCE.getCallSequence();

        /**
         * The meta object literal for the '<em><b>Calls</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CALL_SEQUENCE__CALLS = eINSTANCE.getCallSequence_Calls();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceEntryImpl <em>Call Sequence Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceEntryImpl
         * @see tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl#getCallSequenceEntry()
         * @generated
         */
        EClass CALL_SEQUENCE_ENTRY = eINSTANCE.getCallSequenceEntry();
    }
} // Model2Package
