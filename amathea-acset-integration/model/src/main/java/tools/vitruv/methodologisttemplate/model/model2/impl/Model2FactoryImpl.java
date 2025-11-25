/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry;
import tools.vitruv.methodologisttemplate.model.model2.CallSequence;
import tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry;
import tools.vitruv.methodologisttemplate.model.model2.ISR;
import tools.vitruv.methodologisttemplate.model.model2.Label;
import tools.vitruv.methodologisttemplate.model.model2.LabelSwitch;
import tools.vitruv.methodologisttemplate.model.model2.Model2Factory;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry;
import tools.vitruv.methodologisttemplate.model.model2.Task;
import tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Model2FactoryImpl extends EFactoryImpl implements Model2Factory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Model2Factory init() {
        try {
            Model2Factory theModel2Factory =
                    (Model2Factory) EPackage.Registry.INSTANCE.getEFactory(Model2Package.eNS_URI);
            if (theModel2Factory != null) {
                return theModel2Factory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new Model2FactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Model2FactoryImpl() {
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
            case Model2Package.COMPONENT_CONTAINER:
                return createComponentContainer();
            case Model2Package.ISR:
                return createISR();
            case Model2Package.TASK:
                return createTask();
            case Model2Package.RUNNABLE:
                return createRunnable();
            case Model2Package.TASK_RUNNABLE_CALL:
                return createTaskRunnableCall();
            case Model2Package.LABEL_SWITCH:
                return createLabelSwitch();
            case Model2Package.LABEL:
                return createLabel();
            case Model2Package.PROBABILITY_SWITCH_ENTRY:
                return createProbabilitySwitchEntry();
            case Model2Package.CALL_GRAPH_ENTRY:
                return createCallGraphEntry();
            case Model2Package.GRAPH_ENTRY_ENTRY:
                return createGraphEntryEntry();
            case Model2Package.CALL_SEQUENCE:
                return createCallSequence();
            case Model2Package.CALL_SEQUENCE_ENTRY:
                return createCallSequenceEntry();
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
    public ComponentContainer createComponentContainer() {
        ComponentContainerImpl componentContainer = new ComponentContainerImpl();
        return componentContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ISR createISR() {
        ISRImpl isr = new ISRImpl();
        return isr;
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
    public tools.vitruv.methodologisttemplate.model.model2.Runnable createRunnable() {
        RunnableImpl runnable = new RunnableImpl();
        return runnable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public TaskRunnableCall createTaskRunnableCall() {
        TaskRunnableCallImpl taskRunnableCall = new TaskRunnableCallImpl();
        return taskRunnableCall;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public LabelSwitch createLabelSwitch() {
        LabelSwitchImpl labelSwitch = new LabelSwitchImpl();
        return labelSwitch;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Label createLabel() {
        LabelImpl label = new LabelImpl();
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ProbabilitySwitchEntry createProbabilitySwitchEntry() {
        ProbabilitySwitchEntryImpl probabilitySwitchEntry = new ProbabilitySwitchEntryImpl();
        return probabilitySwitchEntry;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public CallGraphEntry createCallGraphEntry() {
        CallGraphEntryImpl callGraphEntry = new CallGraphEntryImpl();
        return callGraphEntry;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public GraphEntryEntry createGraphEntryEntry() {
        GraphEntryEntryImpl graphEntryEntry = new GraphEntryEntryImpl();
        return graphEntryEntry;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public CallSequence createCallSequence() {
        CallSequenceImpl callSequence = new CallSequenceImpl();
        return callSequence;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public CallSequenceEntry createCallSequenceEntry() {
        CallSequenceEntryImpl callSequenceEntry = new CallSequenceEntryImpl();
        return callSequenceEntry;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Model2Package getModel2Package() {
        return (Model2Package) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static Model2Package getPackage() {
        return Model2Package.eINSTANCE;
    }
} // Model2FactoryImpl
