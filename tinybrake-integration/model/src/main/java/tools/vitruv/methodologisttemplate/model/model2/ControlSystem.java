/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Control System</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.ControlSystem#getAxleUnits <em>Axle Units</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.ControlSystem#getActuators <em>Actuators</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getControlSystem()
 * @model
 * @generated
 */
public interface ControlSystem extends EObject {
    /**
     * Returns the value of the '<em><b>Axle Units</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Axle Units</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getControlSystem_AxleUnits()
     * @model containment="true"
     * @generated
     */
    EList<AxleControlUnit> getAxleUnits();

    /**
     * Returns the value of the '<em><b>Actuators</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Actuators</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getControlSystem_Actuators()
     * @model containment="true"
     * @generated
     */
    EList<HydraulicActuatorSpec> getActuators();
} // ControlSystem
