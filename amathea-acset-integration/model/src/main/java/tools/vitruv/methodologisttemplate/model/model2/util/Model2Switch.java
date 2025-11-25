/**
 */
package tools.vitruv.methodologisttemplate.model.model2.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
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
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package
 * @generated
 */
public class Model2Switch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static Model2Package modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Model2Switch() {
        if (modelPackage == null) {
            modelPackage = Model2Package.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case Model2Package.COMPONENT_CONTAINER: {
                ComponentContainer componentContainer = (ComponentContainer) theEObject;
                T result = caseComponentContainer(componentContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.PROCESS: {
                tools.vitruv.methodologisttemplate.model.model2.Process process =
                        (tools.vitruv.methodologisttemplate.model.model2.Process) theEObject;
                T result = caseProcess(process);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.ISR: {
                ISR isr = (ISR) theEObject;
                T result = caseISR(isr);
                if (result == null) result = caseProcess(isr);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.TASK: {
                Task task = (Task) theEObject;
                T result = caseTask(task);
                if (result == null) result = caseProcess(task);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.RUNNABLE: {
                tools.vitruv.methodologisttemplate.model.model2.Runnable runnable =
                        (tools.vitruv.methodologisttemplate.model.model2.Runnable) theEObject;
                T result = caseRunnable(runnable);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.TASK_RUNNABLE_CALL: {
                TaskRunnableCall taskRunnableCall = (TaskRunnableCall) theEObject;
                T result = caseTaskRunnableCall(taskRunnableCall);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.LABEL_SWITCH: {
                LabelSwitch labelSwitch = (LabelSwitch) theEObject;
                T result = caseLabelSwitch(labelSwitch);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.LABEL: {
                Label label = (Label) theEObject;
                T result = caseLabel(label);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.PROBABILITY_SWITCH_ENTRY: {
                ProbabilitySwitchEntry probabilitySwitchEntry = (ProbabilitySwitchEntry) theEObject;
                T result = caseProbabilitySwitchEntry(probabilitySwitchEntry);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.CALL_GRAPH_ENTRY: {
                CallGraphEntry callGraphEntry = (CallGraphEntry) theEObject;
                T result = caseCallGraphEntry(callGraphEntry);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.GRAPH_ENTRY_ENTRY: {
                GraphEntryEntry graphEntryEntry = (GraphEntryEntry) theEObject;
                T result = caseGraphEntryEntry(graphEntryEntry);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.CALL_SEQUENCE: {
                CallSequence callSequence = (CallSequence) theEObject;
                T result = caseCallSequence(callSequence);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case Model2Package.CALL_SEQUENCE_ENTRY: {
                CallSequenceEntry callSequenceEntry = (CallSequenceEntry) theEObject;
                T result = caseCallSequenceEntry(callSequenceEntry);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Component Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Component Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseComponentContainer(ComponentContainer object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Process</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Process</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseProcess(tools.vitruv.methodologisttemplate.model.model2.Process object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>ISR</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>ISR</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseISR(ISR object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Task</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Task</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTask(Task object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Runnable</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Runnable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseRunnable(tools.vitruv.methodologisttemplate.model.model2.Runnable object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Task Runnable Call</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Task Runnable Call</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTaskRunnableCall(TaskRunnableCall object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Label Switch</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Label Switch</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLabelSwitch(LabelSwitch object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Label</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Label</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLabel(Label object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Probability Switch Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Probability Switch Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseProbabilitySwitchEntry(ProbabilitySwitchEntry object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Call Graph Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Call Graph Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCallGraphEntry(CallGraphEntry object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Graph Entry Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Graph Entry Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseGraphEntryEntry(GraphEntryEntry object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Call Sequence</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Call Sequence</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCallSequence(CallSequence object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Call Sequence Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Call Sequence Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCallSequenceEntry(CallSequenceEntry object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }
} // Model2Switch
