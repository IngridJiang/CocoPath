/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import tools.vitruv.methodologisttemplate.model.model.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelFactoryImpl extends EFactoryImpl implements ModelFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelFactory init() {
        try {
            ModelFactory theModelFactory = (ModelFactory) EPackage.Registry.INSTANCE.getEFactory(ModelPackage.eNS_URI);
            if (theModelFactory != null) {
                return theModelFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ModelFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case ModelPackage.ASCET_MODULE:
                return createAscetModule();
            case ModelPackage.TASK:
                return createTask();
            case ModelPackage.METHOD:
                return createMethod();
            case ModelPackage.INTERRUPT_TASK:
                return createInterruptTask();
            case ModelPackage.INIT_TASK:
                return createInitTask();
            case ModelPackage.PERIODIC_TASK:
                return createPeriodicTask();
            case ModelPackage.SOFTWARE_TASK:
                return createSoftwareTask();
            case ModelPackage.TIME_TABLE_TASK:
                return createTimeTableTask();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public AscetModule createAscetModule() {
        AscetModuleImpl ascetModule = new AscetModuleImpl();
        return ascetModule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Task createTask() {
        TaskImpl task = new TaskImpl();
        return task;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Method createMethod() {
        MethodImpl method = new MethodImpl();
        return method;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public InterruptTask createInterruptTask() {
        InterruptTaskImpl interruptTask = new InterruptTaskImpl();
        return interruptTask;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public InitTask createInitTask() {
        InitTaskImpl initTask = new InitTaskImpl();
        return initTask;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public PeriodicTask createPeriodicTask() {
        PeriodicTaskImpl periodicTask = new PeriodicTaskImpl();
        return periodicTask;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public SoftwareTask createSoftwareTask() {
        SoftwareTaskImpl softwareTask = new SoftwareTaskImpl();
        return softwareTask;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public TimeTableTask createTimeTableTask() {
        TimeTableTaskImpl timeTableTask = new TimeTableTaskImpl();
        return timeTableTask;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ModelPackage getModelPackage() {
        return (ModelPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ModelPackage getPackage() {
        return ModelPackage.eINSTANCE;
    }
} // ModelFactoryImpl
