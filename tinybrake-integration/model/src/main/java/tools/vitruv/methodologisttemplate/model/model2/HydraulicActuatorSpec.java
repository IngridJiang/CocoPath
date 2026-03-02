/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hydraulic Actuator Spec</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getPressureResponse <em>Pressure Response</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getTotalPistonArea <em>Total Piston Area</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getMaxHydraulicForce <em>Max Hydraulic Force</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getPistons <em>Pistons</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getHydraulicActuatorSpec()
 * @model
 * @generated
 */
public interface HydraulicActuatorSpec extends EObject {
    /**
     * Returns the value of the '<em><b>Pressure Response</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pressure Response</em>' attribute.
     * @see #setPressureResponse(String)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getHydraulicActuatorSpec_PressureResponse()
     * @model
     * @generated
     */
    String getPressureResponse();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getPressureResponse <em>Pressure Response</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pressure Response</em>' attribute.
     * @see #getPressureResponse()
     * @generated
     */
    void setPressureResponse(String value);

    /**
     * Returns the value of the '<em><b>Total Piston Area</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Total Piston Area</em>' attribute.
     * @see #setTotalPistonArea(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getHydraulicActuatorSpec_TotalPistonArea()
     * @model required="true"
     * @generated
     */
    double getTotalPistonArea();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getTotalPistonArea <em>Total Piston Area</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Total Piston Area</em>' attribute.
     * @see #getTotalPistonArea()
     * @generated
     */
    void setTotalPistonArea(double value);

    /**
     * Returns the value of the '<em><b>Max Hydraulic Force</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Max Hydraulic Force</em>' attribute.
     * @see #setMaxHydraulicForce(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getHydraulicActuatorSpec_MaxHydraulicForce()
     * @model required="true"
     * @generated
     */
    double getMaxHydraulicForce();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec#getMaxHydraulicForce <em>Max Hydraulic Force</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Max Hydraulic Force</em>' attribute.
     * @see #getMaxHydraulicForce()
     * @generated
     */
    void setMaxHydraulicForce(double value);

    /**
     * Returns the value of the '<em><b>Pistons</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.PistonSpec}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pistons</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getHydraulicActuatorSpec_Pistons()
     * @model containment="true"
     * @generated
     */
    EList<PistonSpec> getPistons();
} // HydraulicActuatorSpec
