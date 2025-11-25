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
     * Returns a new object of class '<em>Component Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Component Container</em>'.
     * @generated
     */
    ComponentContainer createComponentContainer();

    /**
     * Returns a new object of class '<em>ISR</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>ISR</em>'.
     * @generated
     */
    ISR createISR();

    /**
     * Returns a new object of class '<em>Task</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Task</em>'.
     * @generated
     */
    Task createTask();

    /**
     * Returns a new object of class '<em>Runnable</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Runnable</em>'.
     * @generated
     */
    Runnable createRunnable();

    /**
     * Returns a new object of class '<em>Task Runnable Call</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Task Runnable Call</em>'.
     * @generated
     */
    TaskRunnableCall createTaskRunnableCall();

    /**
     * Returns a new object of class '<em>Label Switch</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Label Switch</em>'.
     * @generated
     */
    LabelSwitch createLabelSwitch();

    /**
     * Returns a new object of class '<em>Label</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Label</em>'.
     * @generated
     */
    Label createLabel();

    /**
     * Returns a new object of class '<em>Probability Switch Entry</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Probability Switch Entry</em>'.
     * @generated
     */
    ProbabilitySwitchEntry createProbabilitySwitchEntry();

    /**
     * Returns a new object of class '<em>Call Graph Entry</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Call Graph Entry</em>'.
     * @generated
     */
    CallGraphEntry createCallGraphEntry();

    /**
     * Returns a new object of class '<em>Graph Entry Entry</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Graph Entry Entry</em>'.
     * @generated
     */
    GraphEntryEntry createGraphEntryEntry();

    /**
     * Returns a new object of class '<em>Call Sequence</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Call Sequence</em>'.
     * @generated
     */
    CallSequence createCallSequence();

    /**
     * Returns a new object of class '<em>Call Sequence Entry</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Call Sequence Entry</em>'.
     * @generated
     */
    CallSequenceEntry createCallSequenceEntry();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    Model2Package getModel2Package();
} // Model2Factory
