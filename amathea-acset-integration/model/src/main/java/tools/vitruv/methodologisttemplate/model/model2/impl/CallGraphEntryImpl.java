/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import java.util.Collection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import tools.vitruv.methodologisttemplate.model.model2.CallGraphEntry;
import tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Call Graph Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallGraphEntryImpl#getGraphEntries <em>Graph Entries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CallGraphEntryImpl extends MinimalEObjectImpl.Container implements CallGraphEntry {
    /**
     * The cached value of the '{@link #getGraphEntries() <em>Graph Entries</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getGraphEntries()
     * @generated
     * @ordered
     */
    protected EList<GraphEntryEntry> graphEntries;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CallGraphEntryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.CALL_GRAPH_ENTRY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<GraphEntryEntry> getGraphEntries() {
        if (graphEntries == null) {
            graphEntries = new EObjectResolvingEList<GraphEntryEntry>(
                    GraphEntryEntry.class, this, Model2Package.CALL_GRAPH_ENTRY__GRAPH_ENTRIES);
        }
        return graphEntries;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.CALL_GRAPH_ENTRY__GRAPH_ENTRIES:
                return getGraphEntries();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case Model2Package.CALL_GRAPH_ENTRY__GRAPH_ENTRIES:
                getGraphEntries().clear();
                getGraphEntries().addAll((Collection<? extends GraphEntryEntry>) newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case Model2Package.CALL_GRAPH_ENTRY__GRAPH_ENTRIES:
                getGraphEntries().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case Model2Package.CALL_GRAPH_ENTRY__GRAPH_ENTRIES:
                return graphEntries != null && !graphEntries.isEmpty();
        }
        return super.eIsSet(featureID);
    }
} // CallGraphEntryImpl
