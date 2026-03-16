/**
 */
package tools.vitruv.methodologisttemplate.model.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage
 * @generated
 */
public interface ModelFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelFactory eINSTANCE = tools.vitruv.methodologisttemplate.model.model.impl.ModelFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Brake System</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Brake System</em>'.
     * @generated
     */
    BrakeSystem createBrakeSystem();

    /**
     * Returns a new object of class '<em>Brake Disc</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Brake Disc</em>'.
     * @generated
     */
    BrakeDisc createBrakeDisc();

    /**
     * Returns a new object of class '<em>Brake Piston</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Brake Piston</em>'.
     * @generated
     */
    BrakePiston createBrakePiston();

    /**
     * Returns a new object of class '<em>Brake Caliper</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Brake Caliper</em>'.
     * @generated
     */
    BrakeCaliper createBrakeCaliper();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ModelPackage getModelPackage();
} // ModelFactory
