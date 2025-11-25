/**
 */
package tools.vitruv.methodologisttemplate.model.model;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.Task#getPriority <em>Priority</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.Task#getProcesses <em>Processes</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getTask()
 * @model
 * @generated
 */
public interface Task extends Named {
    /**
     * Returns the value of the '<em><b>Priority</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Priority</em>' attribute.
     * @see #setPriority(int)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getTask_Priority()
     * @model default="0"
     * @generated
     */
    int getPriority();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.Task#getPriority <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Priority</em>' attribute.
     * @see #getPriority()
     * @generated
     */
    void setPriority(int value);

    /**
     * Returns the value of the '<em><b>Processes</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Processes</em>' reference.
     * @see #setProcesses(Method)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getTask_Processes()
     * @model
     * @generated
     */
    Method getProcesses();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.Task#getProcesses <em>Processes</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Processes</em>' reference.
     * @see #getProcesses()
     * @generated
     */
    void setProcesses(Method value);
} // Task
