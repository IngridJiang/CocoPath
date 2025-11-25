/**
 */
package tools.vitruv.methodologisttemplate.model.model2.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry;
import tools.vitruv.methodologisttemplate.model.model2.CallSequence;
import tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry;
import tools.vitruv.methodologisttemplate.model.model2.ISR;
import tools.vitruv.methodologisttemplate.model.model2.Label;
import tools.vitruv.methodologisttemplate.model.model2.LabelSwitch;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry;
import tools.vitruv.methodologisttemplate.model.model2.Task;
import tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package
 * @generated
 */
public class Model2AdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static Model2Package modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Model2AdapterFactory() {
        if (modelPackage == null) {
            modelPackage = Model2Package.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Model2Switch<Adapter> modelSwitch = new Model2Switch<Adapter>() {
        @Override
        public Adapter caseComponentContainer(ComponentContainer object) {
            return createComponentContainerAdapter();
        }

        @Override
        public Adapter caseProcess(tools.vitruv.methodologisttemplate.model.model2.Process object) {
            return createProcessAdapter();
        }

        @Override
        public Adapter caseISR(ISR object) {
            return createISRAdapter();
        }

        @Override
        public Adapter caseTask(Task object) {
            return createTaskAdapter();
        }

        @Override
        public Adapter caseRunnable(tools.vitruv.methodologisttemplate.model.model2.Runnable object) {
            return createRunnableAdapter();
        }

        @Override
        public Adapter caseTaskRunnableCall(TaskRunnableCall object) {
            return createTaskRunnableCallAdapter();
        }

        @Override
        public Adapter caseLabelSwitch(LabelSwitch object) {
            return createLabelSwitchAdapter();
        }

        @Override
        public Adapter caseLabel(Label object) {
            return createLabelAdapter();
        }

        @Override
        public Adapter caseProbabilitySwitchEntry(ProbabilitySwitchEntry object) {
            return createProbabilitySwitchEntryAdapter();
        }

        @Override
        public Adapter caseCallGraphEntry(CallGraphEntry object) {
            return createCallGraphEntryAdapter();
        }

        @Override
        public Adapter caseGraphEntryEntry(GraphEntryEntry object) {
            return createGraphEntryEntryAdapter();
        }

        @Override
        public Adapter caseCallSequence(CallSequence object) {
            return createCallSequenceAdapter();
        }

        @Override
        public Adapter caseCallSequenceEntry(CallSequenceEntry object) {
            return createCallSequenceEntryAdapter();
        }

        @Override
        public Adapter defaultCase(EObject object) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer <em>Component Container</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.ComponentContainer
     * @generated
     */
    public Adapter createComponentContainerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.Process <em>Process</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.Process
     * @generated
     */
    public Adapter createProcessAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.ISR <em>ISR</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.ISR
     * @generated
     */
    public Adapter createISRAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.Task <em>Task</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.Task
     * @generated
     */
    public Adapter createTaskAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.Runnable <em>Runnable</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.Runnable
     * @generated
     */
    public Adapter createRunnableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall <em>Task Runnable Call</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall
     * @generated
     */
    public Adapter createTaskRunnableCallAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.LabelSwitch <em>Label Switch</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.LabelSwitch
     * @generated
     */
    public Adapter createLabelSwitchAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.Label <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.Label
     * @generated
     */
    public Adapter createLabelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry <em>Probability Switch Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry
     * @generated
     */
    public Adapter createProbabilitySwitchEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry <em>Call Graph Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry
     * @generated
     */
    public Adapter createCallGraphEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry <em>Graph Entry Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry
     * @generated
     */
    public Adapter createGraphEntryEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.CallSequence <em>Call Sequence</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallSequence
     * @generated
     */
    public Adapter createCallSequenceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry <em>Call Sequence Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry
     * @generated
     */
    public Adapter createCallSequenceEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }
} // Model2AdapterFactory
