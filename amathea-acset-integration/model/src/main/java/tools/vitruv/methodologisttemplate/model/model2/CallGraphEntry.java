/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Call Graph Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry#getGraphEntries <em>Graph Entries</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getCallGraphEntry()
 * @model
 * @generated
 */
public interface CallGraphEntry extends EObject {
    /**
     * Returns the value of the '<em><b>Graph Entries</b></em>' reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Graph Entries</em>' reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getCallGraphEntry_GraphEntries()
     * @model
     * @generated
     */
    EList<GraphEntryEntry> getGraphEntries();
} // CallGraphEntry
