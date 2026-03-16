/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Axle Control Unit</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl#getAbsDecelThreshold <em>Abs Decel Threshold</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl#getMaxBrakingTorque <em>Max Braking Torque</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl#getControlProfile <em>Control Profile</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl#getCalibrationOffset <em>Calibration Offset</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.AxleControlUnitImpl#getEffectiveBrakeGain <em>Effective Brake Gain</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AxleControlUnitImpl extends MinimalEObjectImpl.Container implements AxleControlUnit {
    /**
     * The default value of the '{@link #getAbsDecelThreshold() <em>Abs Decel Threshold</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAbsDecelThreshold()
     * @generated
     * @ordered
     */
    protected static final double ABS_DECEL_THRESHOLD_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getAbsDecelThreshold() <em>Abs Decel Threshold</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAbsDecelThreshold()
     * @generated
     * @ordered
     */
    protected double absDecelThreshold = ABS_DECEL_THRESHOLD_EDEFAULT;

    /**
     * The default value of the '{@link #getMaxBrakingTorque() <em>Max Braking Torque</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxBrakingTorque()
     * @generated
     * @ordered
     */
    protected static final double MAX_BRAKING_TORQUE_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getMaxBrakingTorque() <em>Max Braking Torque</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxBrakingTorque()
     * @generated
     * @ordered
     */
    protected double maxBrakingTorque = MAX_BRAKING_TORQUE_EDEFAULT;

    /**
     * The default value of the '{@link #getControlProfile() <em>Control Profile</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getControlProfile()
     * @generated
     * @ordered
     */
    protected static final String CONTROL_PROFILE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getControlProfile() <em>Control Profile</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getControlProfile()
     * @generated
     * @ordered
     */
    protected String controlProfile = CONTROL_PROFILE_EDEFAULT;

    /**
     * The default value of the '{@link #getCalibrationOffset() <em>Calibration Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCalibrationOffset()
     * @generated
     * @ordered
     */
    protected static final double CALIBRATION_OFFSET_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getCalibrationOffset() <em>Calibration Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCalibrationOffset()
     * @generated
     * @ordered
     */
    protected double calibrationOffset = CALIBRATION_OFFSET_EDEFAULT;

    /**
     * The default value of the '{@link #getEffectiveBrakeGain() <em>Effective Brake Gain</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEffectiveBrakeGain()
     * @generated
     * @ordered
     */
    protected static final double EFFECTIVE_BRAKE_GAIN_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getEffectiveBrakeGain() <em>Effective Brake Gain</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEffectiveBrakeGain()
     * @generated
     * @ordered
     */
    protected double effectiveBrakeGain = EFFECTIVE_BRAKE_GAIN_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AxleControlUnitImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.AXLE_CONTROL_UNIT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getAbsDecelThreshold() {
        return absDecelThreshold;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setAbsDecelThreshold(double newAbsDecelThreshold) {
        double oldAbsDecelThreshold = absDecelThreshold;
        absDecelThreshold = newAbsDecelThreshold;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD,
                    oldAbsDecelThreshold,
                    absDecelThreshold));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getMaxBrakingTorque() {
        return maxBrakingTorque;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMaxBrakingTorque(double newMaxBrakingTorque) {
        double oldMaxBrakingTorque = maxBrakingTorque;
        maxBrakingTorque = newMaxBrakingTorque;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE,
                    oldMaxBrakingTorque,
                    maxBrakingTorque));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getControlProfile() {
        return controlProfile;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setControlProfile(String newControlProfile) {
        String oldControlProfile = controlProfile;
        controlProfile = newControlProfile;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.AXLE_CONTROL_UNIT__CONTROL_PROFILE,
                    oldControlProfile,
                    controlProfile));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getCalibrationOffset() {
        return calibrationOffset;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setCalibrationOffset(double newCalibrationOffset) {
        double oldCalibrationOffset = calibrationOffset;
        calibrationOffset = newCalibrationOffset;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.AXLE_CONTROL_UNIT__CALIBRATION_OFFSET,
                    oldCalibrationOffset,
                    calibrationOffset));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getEffectiveBrakeGain() {
        return effectiveBrakeGain;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setEffectiveBrakeGain(double newEffectiveBrakeGain) {
        double oldEffectiveBrakeGain = effectiveBrakeGain;
        effectiveBrakeGain = newEffectiveBrakeGain;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN,
                    oldEffectiveBrakeGain,
                    effectiveBrakeGain));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD:
                return getAbsDecelThreshold();
            case Model2Package.AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE:
                return getMaxBrakingTorque();
            case Model2Package.AXLE_CONTROL_UNIT__CONTROL_PROFILE:
                return getControlProfile();
            case Model2Package.AXLE_CONTROL_UNIT__CALIBRATION_OFFSET:
                return getCalibrationOffset();
            case Model2Package.AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN:
                return getEffectiveBrakeGain();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case Model2Package.AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD:
                setAbsDecelThreshold((Double) newValue);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE:
                setMaxBrakingTorque((Double) newValue);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__CONTROL_PROFILE:
                setControlProfile((String) newValue);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__CALIBRATION_OFFSET:
                setCalibrationOffset((Double) newValue);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN:
                setEffectiveBrakeGain((Double) newValue);
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
            case Model2Package.AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD:
                setAbsDecelThreshold(ABS_DECEL_THRESHOLD_EDEFAULT);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE:
                setMaxBrakingTorque(MAX_BRAKING_TORQUE_EDEFAULT);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__CONTROL_PROFILE:
                setControlProfile(CONTROL_PROFILE_EDEFAULT);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__CALIBRATION_OFFSET:
                setCalibrationOffset(CALIBRATION_OFFSET_EDEFAULT);
                return;
            case Model2Package.AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN:
                setEffectiveBrakeGain(EFFECTIVE_BRAKE_GAIN_EDEFAULT);
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
            case Model2Package.AXLE_CONTROL_UNIT__ABS_DECEL_THRESHOLD:
                return absDecelThreshold != ABS_DECEL_THRESHOLD_EDEFAULT;
            case Model2Package.AXLE_CONTROL_UNIT__MAX_BRAKING_TORQUE:
                return maxBrakingTorque != MAX_BRAKING_TORQUE_EDEFAULT;
            case Model2Package.AXLE_CONTROL_UNIT__CONTROL_PROFILE:
                return CONTROL_PROFILE_EDEFAULT == null
                        ? controlProfile != null
                        : !CONTROL_PROFILE_EDEFAULT.equals(controlProfile);
            case Model2Package.AXLE_CONTROL_UNIT__CALIBRATION_OFFSET:
                return calibrationOffset != CALIBRATION_OFFSET_EDEFAULT;
            case Model2Package.AXLE_CONTROL_UNIT__EFFECTIVE_BRAKE_GAIN:
                return effectiveBrakeGain != EFFECTIVE_BRAKE_GAIN_EDEFAULT;
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
        result.append(" (absDecelThreshold: ");
        result.append(absDecelThreshold);
        result.append(", maxBrakingTorque: ");
        result.append(maxBrakingTorque);
        result.append(", controlProfile: ");
        result.append(controlProfile);
        result.append(", calibrationOffset: ");
        result.append(calibrationOffset);
        result.append(", effectiveBrakeGain: ");
        result.append(effectiveBrakeGain);
        result.append(')');
        return result.toString();
    }
} // AxleControlUnitImpl
