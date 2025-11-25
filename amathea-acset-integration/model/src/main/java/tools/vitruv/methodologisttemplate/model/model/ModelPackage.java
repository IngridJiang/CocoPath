/**
 */
package tools.vitruv.methodologisttemplate.model.model;

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
 * @see tools.vitruv.methodologisttemplate.model.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "model";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://vitruv.tools/reactionsparser/model";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "model";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelPackage eINSTANCE = tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl.init();

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.AscetModuleImpl <em>Ascet Module</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.AscetModuleImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getAscetModule()
     * @generated
     */
    int ASCET_MODULE = 0;

    /**
     * The feature id for the '<em><b>Tasks</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ASCET_MODULE__TASKS = 0;

    /**
     * The number of structural features of the '<em>Ascet Module</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ASCET_MODULE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Ascet Module</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ASCET_MODULE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.NamedImpl <em>Named</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.NamedImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getNamed()
     * @generated
     */
    int NAMED = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED__NAME = 0;

    /**
     * The number of structural features of the '<em>Named</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Named</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.TaskImpl <em>Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.TaskImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getTask()
     * @generated
     */
    int TASK = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK__NAME = NAMED__NAME;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK__PRIORITY = NAMED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Processes</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK__PROCESSES = NAMED_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK_FEATURE_COUNT = NAMED_FEATURE_COUNT + 2;

    /**
     * The number of operations of the '<em>Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TASK_OPERATION_COUNT = NAMED_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.MethodImpl <em>Method</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.MethodImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getMethod()
     * @generated
     */
    int METHOD = 3;

    /**
     * The number of structural features of the '<em>Method</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int METHOD_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Method</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int METHOD_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.InterruptTaskImpl <em>Interrupt Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.InterruptTaskImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getInterruptTask()
     * @generated
     */
    int INTERRUPT_TASK = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERRUPT_TASK__NAME = TASK__NAME;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERRUPT_TASK__PRIORITY = TASK__PRIORITY;

    /**
     * The feature id for the '<em><b>Processes</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERRUPT_TASK__PROCESSES = TASK__PROCESSES;

    /**
     * The number of structural features of the '<em>Interrupt Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERRUPT_TASK_FEATURE_COUNT = TASK_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Interrupt Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERRUPT_TASK_OPERATION_COUNT = TASK_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.InitTaskImpl <em>Init Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.InitTaskImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getInitTask()
     * @generated
     */
    int INIT_TASK = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_TASK__NAME = TASK__NAME;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_TASK__PRIORITY = TASK__PRIORITY;

    /**
     * The feature id for the '<em><b>Processes</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_TASK__PROCESSES = TASK__PROCESSES;

    /**
     * The number of structural features of the '<em>Init Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_TASK_FEATURE_COUNT = TASK_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Init Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_TASK_OPERATION_COUNT = TASK_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.PeriodicTaskImpl <em>Periodic Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.PeriodicTaskImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getPeriodicTask()
     * @generated
     */
    int PERIODIC_TASK = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERIODIC_TASK__NAME = TASK__NAME;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERIODIC_TASK__PRIORITY = TASK__PRIORITY;

    /**
     * The feature id for the '<em><b>Processes</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERIODIC_TASK__PROCESSES = TASK__PROCESSES;

    /**
     * The feature id for the '<em><b>Period</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERIODIC_TASK__PERIOD = TASK_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Delay</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERIODIC_TASK__DELAY = TASK_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Periodic Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERIODIC_TASK_FEATURE_COUNT = TASK_FEATURE_COUNT + 2;

    /**
     * The number of operations of the '<em>Periodic Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PERIODIC_TASK_OPERATION_COUNT = TASK_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.SoftwareTaskImpl <em>Software Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.SoftwareTaskImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getSoftwareTask()
     * @generated
     */
    int SOFTWARE_TASK = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOFTWARE_TASK__NAME = TASK__NAME;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOFTWARE_TASK__PRIORITY = TASK__PRIORITY;

    /**
     * The feature id for the '<em><b>Processes</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOFTWARE_TASK__PROCESSES = TASK__PROCESSES;

    /**
     * The number of structural features of the '<em>Software Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOFTWARE_TASK_FEATURE_COUNT = TASK_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Software Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOFTWARE_TASK_OPERATION_COUNT = TASK_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.TimeTableTaskImpl <em>Time Table Task</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.TimeTableTaskImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getTimeTableTask()
     * @generated
     */
    int TIME_TABLE_TASK = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME_TABLE_TASK__NAME = TASK__NAME;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME_TABLE_TASK__PRIORITY = TASK__PRIORITY;

    /**
     * The feature id for the '<em><b>Processes</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME_TABLE_TASK__PROCESSES = TASK__PROCESSES;

    /**
     * The number of structural features of the '<em>Time Table Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME_TABLE_TASK_FEATURE_COUNT = TASK_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Time Table Task</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME_TABLE_TASK_OPERATION_COUNT = TASK_OPERATION_COUNT + 0;

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.AscetModule <em>Ascet Module</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Ascet Module</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.AscetModule
     * @generated
     */
    EClass getAscetModule();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model.AscetModule#getTasks <em>Tasks</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Tasks</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.AscetModule#getTasks()
     * @see #getAscetModule()
     * @generated
     */
    EReference getAscetModule_Tasks();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.Named <em>Named</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Named</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.Named
     * @generated
     */
    EClass getNamed();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.Named#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.Named#getName()
     * @see #getNamed()
     * @generated
     */
    EAttribute getNamed_Name();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.Task <em>Task</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Task</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.Task
     * @generated
     */
    EClass getTask();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.Task#getPriority <em>Priority</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Priority</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.Task#getPriority()
     * @see #getTask()
     * @generated
     */
    EAttribute getTask_Priority();

    /**
     * Returns the meta object for the reference '{@link tools.vitruv.methodologisttemplate.model.model.Task#getProcesses <em>Processes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Processes</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.Task#getProcesses()
     * @see #getTask()
     * @generated
     */
    EReference getTask_Processes();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.Method <em>Method</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Method</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.Method
     * @generated
     */
    EClass getMethod();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.InterruptTask <em>Interrupt Task</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Interrupt Task</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.InterruptTask
     * @generated
     */
    EClass getInterruptTask();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.InitTask <em>Init Task</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Init Task</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.InitTask
     * @generated
     */
    EClass getInitTask();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask <em>Periodic Task</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Periodic Task</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.PeriodicTask
     * @generated
     */
    EClass getPeriodicTask();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getPeriod <em>Period</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Period</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getPeriod()
     * @see #getPeriodicTask()
     * @generated
     */
    EAttribute getPeriodicTask_Period();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getDelay <em>Delay</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Delay</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getDelay()
     * @see #getPeriodicTask()
     * @generated
     */
    EAttribute getPeriodicTask_Delay();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.SoftwareTask <em>Software Task</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Software Task</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.SoftwareTask
     * @generated
     */
    EClass getSoftwareTask();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.TimeTableTask <em>Time Table Task</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Time Table Task</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.TimeTableTask
     * @generated
     */
    EClass getTimeTableTask();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ModelFactory getModelFactory();

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
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.AscetModuleImpl <em>Ascet Module</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.AscetModuleImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getAscetModule()
         * @generated
         */
        EClass ASCET_MODULE = eINSTANCE.getAscetModule();

        /**
         * The meta object literal for the '<em><b>Tasks</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ASCET_MODULE__TASKS = eINSTANCE.getAscetModule_Tasks();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.NamedImpl <em>Named</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.NamedImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getNamed()
         * @generated
         */
        EClass NAMED = eINSTANCE.getNamed();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute NAMED__NAME = eINSTANCE.getNamed_Name();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.TaskImpl <em>Task</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.TaskImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getTask()
         * @generated
         */
        EClass TASK = eINSTANCE.getTask();

        /**
         * The meta object literal for the '<em><b>Priority</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TASK__PRIORITY = eINSTANCE.getTask_Priority();

        /**
         * The meta object literal for the '<em><b>Processes</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TASK__PROCESSES = eINSTANCE.getTask_Processes();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.MethodImpl <em>Method</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.MethodImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getMethod()
         * @generated
         */
        EClass METHOD = eINSTANCE.getMethod();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.InterruptTaskImpl <em>Interrupt Task</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.InterruptTaskImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getInterruptTask()
         * @generated
         */
        EClass INTERRUPT_TASK = eINSTANCE.getInterruptTask();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.InitTaskImpl <em>Init Task</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.InitTaskImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getInitTask()
         * @generated
         */
        EClass INIT_TASK = eINSTANCE.getInitTask();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.PeriodicTaskImpl <em>Periodic Task</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.PeriodicTaskImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getPeriodicTask()
         * @generated
         */
        EClass PERIODIC_TASK = eINSTANCE.getPeriodicTask();

        /**
         * The meta object literal for the '<em><b>Period</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PERIODIC_TASK__PERIOD = eINSTANCE.getPeriodicTask_Period();

        /**
         * The meta object literal for the '<em><b>Delay</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PERIODIC_TASK__DELAY = eINSTANCE.getPeriodicTask_Delay();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.SoftwareTaskImpl <em>Software Task</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.SoftwareTaskImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getSoftwareTask()
         * @generated
         */
        EClass SOFTWARE_TASK = eINSTANCE.getSoftwareTask();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.TimeTableTaskImpl <em>Time Table Task</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.TimeTableTaskImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getTimeTableTask()
         * @generated
         */
        EClass TIME_TABLE_TASK = eINSTANCE.getTimeTableTask();
    }
} // ModelPackage
