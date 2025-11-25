/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import java.util.Collection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import tools.vitruv.methodologisttemplate.model.model2.CallSequence;
import tools.vitruv.methodologisttemplate.model.model2.CallSequenceEntry;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Call Sequence</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.CallSequenceImpl#getCalls <em>Calls</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CallSequenceImpl extends MinimalEObjectImpl.Container implements CallSequence {
    /**
     * The cached value of the '{@link #getCalls() <em>Calls</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCalls()
     * @generated
     * @ordered
     */
    protected EList<CallSequenceEntry> calls;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CallSequenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.CALL_SEQUENCE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<CallSequenceEntry> getCalls() {
        if (calls == null) {
            calls = new EObjectResolvingEList<CallSequenceEntry>(
                    CallSequenceEntry.class, this, Model2Package.CALL_SEQUENCE__CALLS);
        }
        return calls;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.CALL_SEQUENCE__CALLS:
                return getCalls();
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
            case Model2Package.CALL_SEQUENCE__CALLS:
                getCalls().clear();
                getCalls().addAll((Collection<? extends CallSequenceEntry>) newValue);
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
            case Model2Package.CALL_SEQUENCE__CALLS:
                getCalls().clear();
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
            case Model2Package.CALL_SEQUENCE__CALLS:
                return calls != null && !calls.isEmpty();
        }
        return super.eIsSet(featureID);
    }
} // CallSequenceImpl
