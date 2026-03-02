/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import tools.vitruv.methodologisttemplate.model.model2.HydraulicActuatorSpec;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.PistonSpec;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hydraulic Actuator Spec</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl#getPressureResponse <em>Pressure Response</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl#getTotalPistonArea <em>Total Piston Area</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl#getMaxHydraulicForce <em>Max Hydraulic Force</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.HydraulicActuatorSpecImpl#getPistons <em>Pistons</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HydraulicActuatorSpecImpl extends MinimalEObjectImpl.Container implements HydraulicActuatorSpec {
    /**
     * The default value of the '{@link #getPressureResponse() <em>Pressure Response</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPressureResponse()
     * @generated
     * @ordered
     */
    protected static final String PRESSURE_RESPONSE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPressureResponse() <em>Pressure Response</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPressureResponse()
     * @generated
     * @ordered
     */
    protected String pressureResponse = PRESSURE_RESPONSE_EDEFAULT;

    /**
     * The default value of the '{@link #getTotalPistonArea() <em>Total Piston Area</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalPistonArea()
     * @generated
     * @ordered
     */
    protected static final double TOTAL_PISTON_AREA_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getTotalPistonArea() <em>Total Piston Area</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalPistonArea()
     * @generated
     * @ordered
     */
    protected double totalPistonArea = TOTAL_PISTON_AREA_EDEFAULT;

    /**
     * The default value of the '{@link #getMaxHydraulicForce() <em>Max Hydraulic Force</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxHydraulicForce()
     * @generated
     * @ordered
     */
    protected static final double MAX_HYDRAULIC_FORCE_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getMaxHydraulicForce() <em>Max Hydraulic Force</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxHydraulicForce()
     * @generated
     * @ordered
     */
    protected double maxHydraulicForce = MAX_HYDRAULIC_FORCE_EDEFAULT;

    /**
     * The cached value of the '{@link #getPistons() <em>Pistons</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPistons()
     * @generated
     * @ordered
     */
    protected EList<PistonSpec> pistons;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected HydraulicActuatorSpecImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.HYDRAULIC_ACTUATOR_SPEC;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getPressureResponse() {
        return pressureResponse;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPressureResponse(String newPressureResponse) {
        String oldPressureResponse = pressureResponse;
        pressureResponse = newPressureResponse;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE,
                    oldPressureResponse,
                    pressureResponse));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getTotalPistonArea() {
        return totalPistonArea;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setTotalPistonArea(double newTotalPistonArea) {
        double oldTotalPistonArea = totalPistonArea;
        totalPistonArea = newTotalPistonArea;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA,
                    oldTotalPistonArea,
                    totalPistonArea));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getMaxHydraulicForce() {
        return maxHydraulicForce;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMaxHydraulicForce(double newMaxHydraulicForce) {
        double oldMaxHydraulicForce = maxHydraulicForce;
        maxHydraulicForce = newMaxHydraulicForce;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE,
                    oldMaxHydraulicForce,
                    maxHydraulicForce));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<PistonSpec> getPistons() {
        if (pistons == null) {
            pistons = new EObjectContainmentEList<PistonSpec>(
                    PistonSpec.class, this, Model2Package.HYDRAULIC_ACTUATOR_SPEC__PISTONS);
        }
        return pistons;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PISTONS:
                return ((InternalEList<?>) getPistons()).basicRemove(otherEnd, msgs);
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
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE:
                return getPressureResponse();
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA:
                return getTotalPistonArea();
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE:
                return getMaxHydraulicForce();
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PISTONS:
                return getPistons();
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
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE:
                setPressureResponse((String) newValue);
                return;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA:
                setTotalPistonArea((Double) newValue);
                return;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE:
                setMaxHydraulicForce((Double) newValue);
                return;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PISTONS:
                getPistons().clear();
                getPistons().addAll((Collection<? extends PistonSpec>) newValue);
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
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE:
                setPressureResponse(PRESSURE_RESPONSE_EDEFAULT);
                return;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA:
                setTotalPistonArea(TOTAL_PISTON_AREA_EDEFAULT);
                return;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE:
                setMaxHydraulicForce(MAX_HYDRAULIC_FORCE_EDEFAULT);
                return;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PISTONS:
                getPistons().clear();
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
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PRESSURE_RESPONSE:
                return PRESSURE_RESPONSE_EDEFAULT == null
                        ? pressureResponse != null
                        : !PRESSURE_RESPONSE_EDEFAULT.equals(pressureResponse);
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__TOTAL_PISTON_AREA:
                return totalPistonArea != TOTAL_PISTON_AREA_EDEFAULT;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__MAX_HYDRAULIC_FORCE:
                return maxHydraulicForce != MAX_HYDRAULIC_FORCE_EDEFAULT;
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC__PISTONS:
                return pistons != null && !pistons.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuilder result = new StringBuilder(super.toString());
        result.append(" (pressureResponse: ");
        result.append(pressureResponse);
        result.append(", totalPistonArea: ");
        result.append(totalPistonArea);
        result.append(", maxHydraulicForce: ");
        result.append(maxHydraulicForce);
        result.append(')');
        return result.toString();
    }
} // HydraulicActuatorSpecImpl
