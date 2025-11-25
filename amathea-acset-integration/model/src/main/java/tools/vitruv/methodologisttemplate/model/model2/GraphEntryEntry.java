/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Graph Entry Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry#getGraphEntries <em>Graph Entries</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry#getEntries <em>Entries</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getGraphEntryEntry()
 * @model
 * @generated
 */
public interface GraphEntryEntry extends EObject {
    /**
     * Returns the value of the '<em><b>Graph Entries</b></em>' reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Graph Entries</em>' reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getGraphEntryEntry_GraphEntries()
     * @model
     * @generated
     */
    EList<GraphEntryEntry> getGraphEntries();

    /**
     * Returns the value of the '<em><b>Entries</b></em>' reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model2.CallSequence}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Entries</em>' reference list.
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getGraphEntryEntry_Entries()
     * @model
     * @generated
     */
    EList<CallSequence> getEntries();
} // GraphEntryEntry
