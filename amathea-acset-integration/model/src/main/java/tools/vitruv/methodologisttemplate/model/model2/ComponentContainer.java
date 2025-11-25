/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getTasks <em>Tasks</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getIsrs <em>Isrs</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getRunnables <em>Runnables</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.ComponentContainer#getLabels <em>Labels</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getComponentContainer()
 * @model
 * @generated
 */
public interface ComponentContainer extends EObject {
    /**
     * Returns the value of the '<em><b>Tasks</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.Task}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tasks</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getComponentContainer_Tasks()
     * @model containment="true"
     * @generated
     */
    EList<Task> getTasks();

    /**
     * Returns the value of the '<em><b>Isrs</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.ISR}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Isrs</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getComponentContainer_Isrs()
     * @model containment="true"
     * @generated
     */
    EList<ISR> getIsrs();

    /**
     * Returns the value of the '<em><b>Runnables</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.Runnable}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Runnables</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getComponentContainer_Runnables()
     * @model containment="true"
     * @generated
     */
    EList<tools.vitruv.methodologisttemplate.model.model2.Runnable> getRunnables();

    /**
     * Returns the value of the '<em><b>Labels</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.Label}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Labels</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getComponentContainer_Labels()
     * @model containment="true"
     * @generated
     */
    EList<Label> getLabels();
} // ComponentContainer
