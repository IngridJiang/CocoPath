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
     * Returns a new object of class '<em>Ascet Module</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Ascet Module</em>'.
     * @generated
     */
    AscetModule createAscetModule();

    /**
     * Returns a new object of class '<em>Task</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Task</em>'.
     * @generated
     */
    Task createTask();

    /**
     * Returns a new object of class '<em>Method</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Method</em>'.
     * @generated
     */
    Method createMethod();

    /**
     * Returns a new object of class '<em>Interrupt Task</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Interrupt Task</em>'.
     * @generated
     */
    InterruptTask createInterruptTask();

    /**
     * Returns a new object of class '<em>Init Task</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Init Task</em>'.
     * @generated
     */
    InitTask createInitTask();

    /**
     * Returns a new object of class '<em>Periodic Task</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Periodic Task</em>'.
     * @generated
     */
    PeriodicTask createPeriodicTask();

    /**
     * Returns a new object of class '<em>Software Task</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Software Task</em>'.
     * @generated
     */
    SoftwareTask createSoftwareTask();

    /**
     * Returns a new object of class '<em>Time Table Task</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Time Table Task</em>'.
     * @generated
     */
    TimeTableTask createTimeTableTask();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ModelPackage getModelPackage();
} // ModelFactory
