/**
 */
package tools.vitruv.methodologisttemplate.model.model;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Periodic Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getPeriod <em>Period</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getDelay <em>Delay</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getPeriodicTask()
 * @model
 * @generated
 */
public interface PeriodicTask extends Task {
    /**
     * Returns the value of the '<em><b>Period</b></em>' attribute.
     * The default value is <code>"0.0"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Period</em>' attribute.
     * @see #setPeriod(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getPeriodicTask_Period()
     * @model default="0.0"
     * @generated
     */
    double getPeriod();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getPeriod <em>Period</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Period</em>' attribute.
     * @see #getPeriod()
     * @generated
     */
    void setPeriod(double value);

    /**
     * Returns the value of the '<em><b>Delay</b></em>' attribute.
     * The default value is <code>"0.0"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Delay</em>' attribute.
     * @see #setDelay(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getPeriodicTask_Delay()
     * @model default="0.0"
     * @generated
     */
    double getDelay();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask#getDelay <em>Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Delay</em>' attribute.
     * @see #getDelay()
     * @generated
     */
    void setDelay(double value);
} // PeriodicTask
