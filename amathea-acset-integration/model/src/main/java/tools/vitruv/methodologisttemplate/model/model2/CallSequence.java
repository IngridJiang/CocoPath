/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Call Sequence</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.CallSequence#getCalls <em>Calls</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getCallSequence()
 * @model
 * @generated
 */
public interface CallSequence extends EObject {
    /**
     * Returns the value of the '<em><b>Calls</b></em>' reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Calls</em>' reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getCallSequence_Calls()
     * @model
     * @generated
     */
    EList<CallSequenceEntry> getCalls();
} // CallSequence
