/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package
 * @generated
 */
public interface Model2Factory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    Model2Factory eINSTANCE = tools.vitruv.methodologisttemplate.model.model2.impl.Model2FactoryImpl.init();

    /**
     * Returns a new object of class '<em>Control System</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Control System</em>'.
     * @generated
     */
    ControlSystem createControlSystem();

    /**
     * Returns a new object of class '<em>Axle Control Unit</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Axle Control Unit</em>'.
     * @generated
     */
    AxleControlUnit createAxleControlUnit();

    /**
     * Returns a new object of class '<em>Hydraulic Actuator Spec</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Hydraulic Actuator Spec</em>'.
     * @generated
     */
    HydraulicActuatorSpec createHydraulicActuatorSpec();

    /**
     * Returns a new object of class '<em>Piston Spec</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Piston Spec</em>'.
     * @generated
     */
    PistonSpec createPistonSpec();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    Model2Package getModel2Package();
} // Model2Factory
