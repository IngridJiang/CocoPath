/**
 */
package tools.vitruv.methodologisttemplate.model.model;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ascet Module</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.AscetModule#getTasks <em>Tasks</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getAscetModule()
 * @model
 * @generated
 */
public interface AscetModule extends EObject {
    /**
     * Returns the value of the '<em><b>Tasks</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model.Task}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tasks</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getAscetModule_Tasks()
     * @model containment="true"
     * @generated
     */
    EList<Task> getTasks();
} // AscetModule
