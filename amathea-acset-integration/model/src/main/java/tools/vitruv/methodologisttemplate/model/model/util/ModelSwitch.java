/**
 */
package tools.vitruv.methodologisttemplate.model.model.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import tools.vitruv.methodologisttemplate.model.model.*;

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
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage
 * @generated
 */
public class ModelSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ModelPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelSwitch() {
        if (modelPackage == null) {
            modelPackage = ModelPackage.eINSTANCE;
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
            case ModelPackage.ASCET_MODULE: {
                AscetModule ascetModule = (AscetModule) theEObject;
                T result = caseAscetModule(ascetModule);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.NAMED: {
                Named named = (Named) theEObject;
                T result = caseNamed(named);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.TASK: {
                Task task = (Task) theEObject;
                T result = caseTask(task);
                if (result == null) result = caseNamed(task);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.METHOD: {
                Method method = (Method) theEObject;
                T result = caseMethod(method);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.INTERRUPT_TASK: {
                InterruptTask interruptTask = (InterruptTask) theEObject;
                T result = caseInterruptTask(interruptTask);
                if (result == null) result = caseTask(interruptTask);
                if (result == null) result = caseNamed(interruptTask);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.INIT_TASK: {
                InitTask initTask = (InitTask) theEObject;
                T result = caseInitTask(initTask);
                if (result == null) result = caseTask(initTask);
                if (result == null) result = caseNamed(initTask);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.PERIODIC_TASK: {
                PeriodicTask periodicTask = (PeriodicTask) theEObject;
                T result = casePeriodicTask(periodicTask);
                if (result == null) result = caseTask(periodicTask);
                if (result == null) result = caseNamed(periodicTask);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.SOFTWARE_TASK: {
                SoftwareTask softwareTask = (SoftwareTask) theEObject;
                T result = caseSoftwareTask(softwareTask);
                if (result == null) result = caseTask(softwareTask);
                if (result == null) result = caseNamed(softwareTask);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.TIME_TABLE_TASK: {
                TimeTableTask timeTableTask = (TimeTableTask) theEObject;
                T result = caseTimeTableTask(timeTableTask);
                if (result == null) result = caseTask(timeTableTask);
                if (result == null) result = caseNamed(timeTableTask);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Ascet Module</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Ascet Module</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseAscetModule(AscetModule object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Named</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Named</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseNamed(Named object) {
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
     * Returns the result of interpreting the object as an instance of '<em>Method</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Method</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseMethod(Method object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Interrupt Task</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Interrupt Task</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseInterruptTask(InterruptTask object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Init Task</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Init Task</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseInitTask(InitTask object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Periodic Task</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Periodic Task</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T casePeriodicTask(PeriodicTask object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Software Task</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Software Task</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseSoftwareTask(SoftwareTask object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Time Table Task</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Time Table Task</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTimeTableTask(TimeTableTask object) {
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
} // ModelSwitch
