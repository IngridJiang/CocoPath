/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import java.util.Collection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import tools.vitruv.methodologisttemplate.model.model2.CallSequence;
import tools.vitruv.methodologisttemplate.model.model2.GraphEntryEntry;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Graph Entry Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.GraphEntryEntryImpl#getGraphEntries <em>Graph Entries</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.GraphEntryEntryImpl#getEntries <em>Entries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GraphEntryEntryImpl extends MinimalEObjectImpl.Container implements GraphEntryEntry {
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
     * The cached value of the '{@link #getEntries() <em>Entries</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEntries()
     * @generated
     * @ordered
     */
    protected EList<CallSequence> entries;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected GraphEntryEntryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.GRAPH_ENTRY_ENTRY;
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
                    GraphEntryEntry.class, this, Model2Package.GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES);
        }
        return graphEntries;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<CallSequence> getEntries() {
        if (entries == null) {
            entries = new EObjectResolvingEList<CallSequence>(
                    CallSequence.class, this, Model2Package.GRAPH_ENTRY_ENTRY__ENTRIES);
        }
        return entries;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES:
                return getGraphEntries();
            case Model2Package.GRAPH_ENTRY_ENTRY__ENTRIES:
                return getEntries();
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
            case Model2Package.GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES:
                getGraphEntries().clear();
                getGraphEntries().addAll((Collection<? extends GraphEntryEntry>) newValue);
                return;
            case Model2Package.GRAPH_ENTRY_ENTRY__ENTRIES:
                getEntries().clear();
                getEntries().addAll((Collection<? extends CallSequence>) newValue);
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
            case Model2Package.GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES:
                getGraphEntries().clear();
                return;
            case Model2Package.GRAPH_ENTRY_ENTRY__ENTRIES:
                getEntries().clear();
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
            case Model2Package.GRAPH_ENTRY_ENTRY__GRAPH_ENTRIES:
                return graphEntries != null && !graphEntries.isEmpty();
            case Model2Package.GRAPH_ENTRY_ENTRY__ENTRIES:
                return entries != null && !entries.isEmpty();
        }
        return super.eIsSet(featureID);
    }
} // GraphEntryEntryImpl
