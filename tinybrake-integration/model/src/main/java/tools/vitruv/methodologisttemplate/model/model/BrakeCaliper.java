/**
 */
package tools.vitruv.methodologisttemplate.model.model;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Brake Caliper</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getPistonCount <em>Piston Count</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getClampOpeningWidth <em>Clamp Opening Width</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getMaxPressure <em>Max Pressure</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getPistons <em>Pistons</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeCaliper()
 * @model
 * @generated
 */
public interface BrakeCaliper extends EObject {
    /**
     * Returns the value of the '<em><b>Piston Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Piston Count</em>' attribute.
     * @see #setPistonCount(int)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeCaliper_PistonCount()
     * @model required="true"
     * @generated
     */
    int getPistonCount();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getPistonCount <em>Piston Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Piston Count</em>' attribute.
     * @see #getPistonCount()
     * @generated
     */
    void setPistonCount(int value);

    /**
     * Returns the value of the '<em><b>Clamp Opening Width</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Clamp Opening Width</em>' attribute.
     * @see #setClampOpeningWidth(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeCaliper_ClampOpeningWidth()
     * @model required="true"
     * @generated
     */
    double getClampOpeningWidth();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getClampOpeningWidth <em>Clamp Opening Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Clamp Opening Width</em>' attribute.
     * @see #getClampOpeningWidth()
     * @generated
     */
    void setClampOpeningWidth(double value);

    /**
     * Returns the value of the '<em><b>Max Pressure</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Max Pressure</em>' attribute.
     * @see #setMaxPressure(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeCaliper_MaxPressure()
     * @model required="true"
     * @generated
     */
    double getMaxPressure();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getMaxPressure <em>Max Pressure</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Max Pressure</em>' attribute.
     * @see #getMaxPressure()
     * @generated
     */
    void setMaxPressure(double value);

    /**
     * Returns the value of the '<em><b>Pistons</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model.BrakePiston}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pistons</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeCaliper_Pistons()
     * @model containment="true"
     * @generated
     */
    EList<BrakePiston> getPistons();
} // BrakeCaliper
