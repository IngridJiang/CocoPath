/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit;
import tools.vitruv.methodologisttemplate.model.model2.ControlSystem;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Control System</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.ControlSystemImpl#getAxleUnits <em>Axle Units</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.ControlSystemImpl#getActuators <em>Actuators</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ControlSystemImpl extends MinimalEObjectImpl.Container implements ControlSystem {
    /**
     * The cached value of the '{@link #getAxleUnits() <em>Axle Units</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAxleUnits()
     * @generated
     * @ordered
     */
    protected EList<AxleControlUnit> axleUnits;

    /**
     * The cached value of the '{@link #getActuators() <em>Actuators</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getActuators()
     * @generated
     * @ordered
     */
    protected EList<HydraulicActuatorSpec> actuators;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ControlSystemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.CONTROL_SYSTEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<AxleControlUnit> getAxleUnits() {
        if (axleUnits == null) {
            axleUnits = new EObjectContainmentEList<AxleControlUnit>(
                    AxleControlUnit.class, this, Model2Package.CONTROL_SYSTEM__AXLE_UNITS);
        }
        return axleUnits;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<HydraulicActuatorSpec> getActuators() {
        if (actuators == null) {
            actuators = new EObjectContainmentEList<HydraulicActuatorSpec>(
                    HydraulicActuatorSpec.class, this, Model2Package.CONTROL_SYSTEM__ACTUATORS);
        }
        return actuators;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case Model2Package.CONTROL_SYSTEM__AXLE_UNITS:
                return ((InternalEList<?>) getAxleUnits()).basicRemove(otherEnd, msgs);
            case Model2Package.CONTROL_SYSTEM__ACTUATORS:
                return ((InternalEList<?>) getActuators()).basicRemove(otherEnd, msgs);
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
            case Model2Package.CONTROL_SYSTEM__AXLE_UNITS:
                return getAxleUnits();
            case Model2Package.CONTROL_SYSTEM__ACTUATORS:
                return getActuators();
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
            case Model2Package.CONTROL_SYSTEM__AXLE_UNITS:
                getAxleUnits().clear();
                getAxleUnits().addAll((Collection<? extends AxleControlUnit>) newValue);
                return;
            case Model2Package.CONTROL_SYSTEM__ACTUATORS:
                getActuators().clear();
                getActuators().addAll((Collection<? extends HydraulicActuatorSpec>) newValue);
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
            case Model2Package.CONTROL_SYSTEM__AXLE_UNITS:
                getAxleUnits().clear();
                return;
            case Model2Package.CONTROL_SYSTEM__ACTUATORS:
                getActuators().clear();
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
            case Model2Package.CONTROL_SYSTEM__AXLE_UNITS:
                return axleUnits != null && !axleUnits.isEmpty();
            case Model2Package.CONTROL_SYSTEM__ACTUATORS:
                return actuators != null && !actuators.isEmpty();
        }
        return super.eIsSet(featureID);
    }
} // ControlSystemImpl
