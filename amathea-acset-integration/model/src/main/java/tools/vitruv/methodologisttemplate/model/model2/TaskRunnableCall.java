/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Task Runnable Call</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall#getRunnable <em>Runnable</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getTaskRunnableCall()
 * @model
 * @generated
 */
public interface TaskRunnableCall extends EObject {
    /**
     * Returns the value of the '<em><b>Runnable</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Runnable</em>' reference.
     * @see #setRunnable(tools.vitruv.methodologisttemplate.model.model2.Runnable)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getTaskRunnableCall_Runnable()
     * @model
     * @generated
     */
    tools.vitruv.methodologisttemplate.model.model2.Runnable getRunnable();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall#getRunnable <em>Runnable</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Runnable</em>' reference.
     * @see #getRunnable()
     * @generated
     */
    void setRunnable(tools.vitruv.methodologisttemplate.model.model2.Runnable value);
} // TaskRunnableCall
