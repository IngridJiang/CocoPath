/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.Task#getName <em>Name</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.Task#getPreemption <em>Preemption</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.Task#getMultipleTaskActivationLimit <em>Multiple Task Activation Limit</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getTask()
 * @model
 * @generated
 */
public interface Task extends tools.vitruv.methodologisttemplate.model.model2.Process {
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getTask_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.Task#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Preemption</b></em>' attribute.
     * The default value is <code>"cooperative"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Preemption</em>' attribute.
     * @see #setPreemption(String)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getTask_Preemption()
     * @model default="cooperative"
     * @generated
     */
    String getPreemption();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.Task#getPreemption <em>Preemption</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Preemption</em>' attribute.
     * @see #getPreemption()
     * @generated
     */
    void setPreemption(String value);

    /**
     * Returns the value of the '<em><b>Multiple Task Activation Limit</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Multiple Task Activation Limit</em>' attribute.
     * @see #setMultipleTaskActivationLimit(int)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getTask_MultipleTaskActivationLimit()
     * @model default="0"
     * @generated
     */
    int getMultipleTaskActivationLimit();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.Task#getMultipleTaskActivationLimit <em>Multiple Task Activation Limit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Multiple Task Activation Limit</em>' attribute.
     * @see #getMultipleTaskActivationLimit()
     * @generated
     */
    void setMultipleTaskActivationLimit(int value);
} // Task
