/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;
import tools.vitruv.methodologisttemplate.model.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Brake System</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeSystemImpl#getBrakeDiscs <em>Brake Discs</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeSystemImpl#getCalipers <em>Calipers</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BrakeSystemImpl extends MinimalEObjectImpl.Container implements BrakeSystem {
    /**
     * The cached value of the '{@link #getBrakeDiscs() <em>Brake Discs</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBrakeDiscs()
     * @generated
     * @ordered
     */
    protected EList<BrakeDisc> brakeDiscs;

    /**
     * The cached value of the '{@link #getCalipers() <em>Calipers</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCalipers()
     * @generated
     * @ordered
     */
    protected EList<BrakeCaliper> calipers;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BrakeSystemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.BRAKE_SYSTEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<BrakeDisc> getBrakeDiscs() {
        if (brakeDiscs == null) {
            brakeDiscs = new EObjectContainmentEList<BrakeDisc>(
                    BrakeDisc.class, this, ModelPackage.BRAKE_SYSTEM__BRAKE_DISCS);
        }
        return brakeDiscs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<BrakeCaliper> getCalipers() {
        if (calipers == null) {
            calipers = new EObjectContainmentEList<BrakeCaliper>(
                    BrakeCaliper.class, this, ModelPackage.BRAKE_SYSTEM__CALIPERS);
        }
        return calipers;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case ModelPackage.BRAKE_SYSTEM__BRAKE_DISCS:
                return ((InternalEList<?>) getBrakeDiscs()).basicRemove(otherEnd, msgs);
            case ModelPackage.BRAKE_SYSTEM__CALIPERS:
                return ((InternalEList<?>) getCalipers()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.BRAKE_SYSTEM__BRAKE_DISCS:
                return getBrakeDiscs();
            case ModelPackage.BRAKE_SYSTEM__CALIPERS:
                return getCalipers();
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
            case ModelPackage.BRAKE_SYSTEM__BRAKE_DISCS:
                getBrakeDiscs().clear();
                getBrakeDiscs().addAll((Collection<? extends BrakeDisc>) newValue);
                return;
            case ModelPackage.BRAKE_SYSTEM__CALIPERS:
                getCalipers().clear();
                getCalipers().addAll((Collection<? extends BrakeCaliper>) newValue);
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
            case ModelPackage.BRAKE_SYSTEM__BRAKE_DISCS:
                getBrakeDiscs().clear();
                return;
            case ModelPackage.BRAKE_SYSTEM__CALIPERS:
                getCalipers().clear();
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
            case ModelPackage.BRAKE_SYSTEM__BRAKE_DISCS:
                return brakeDiscs != null && !brakeDiscs.isEmpty();
            case ModelPackage.BRAKE_SYSTEM__CALIPERS:
                return calipers != null && !calipers.isEmpty();
        }
        return super.eIsSet(featureID);
    }
} // BrakeSystemImpl
