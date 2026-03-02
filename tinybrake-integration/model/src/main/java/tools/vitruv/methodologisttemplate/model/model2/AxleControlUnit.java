/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Axle Control Unit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getAbsDecelThreshold <em>Abs Decel Threshold</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getMaxBrakingTorque <em>Max Braking Torque</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getControlProfile <em>Control Profile</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getCalibrationOffset <em>Calibration Offset</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getEffectiveBrakeGain <em>Effective Brake Gain</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getAxleControlUnit()
 * @model
 * @generated
 */
public interface AxleControlUnit extends EObject {
    /**
     * Returns the value of the '<em><b>Abs Decel Threshold</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Abs Decel Threshold</em>' attribute.
     * @see #setAbsDecelThreshold(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getAxleControlUnit_AbsDecelThreshold()
     * @model required="true"
     * @generated
     */
    double getAbsDecelThreshold();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getAbsDecelThreshold <em>Abs Decel Threshold</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Abs Decel Threshold</em>' attribute.
     * @see #getAbsDecelThreshold()
     * @generated
     */
    void setAbsDecelThreshold(double value);

    /**
     * Returns the value of the '<em><b>Max Braking Torque</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Max Braking Torque</em>' attribute.
     * @see #setMaxBrakingTorque(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getAxleControlUnit_MaxBrakingTorque()
     * @model required="true"
     * @generated
     */
    double getMaxBrakingTorque();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getMaxBrakingTorque <em>Max Braking Torque</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Max Braking Torque</em>' attribute.
     * @see #getMaxBrakingTorque()
     * @generated
     */
    void setMaxBrakingTorque(double value);

    /**
     * Returns the value of the '<em><b>Control Profile</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Control Profile</em>' attribute.
     * @see #setControlProfile(String)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getAxleControlUnit_ControlProfile()
     * @model
     * @generated
     */
    String getControlProfile();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getControlProfile <em>Control Profile</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Control Profile</em>' attribute.
     * @see #getControlProfile()
     * @generated
     */
    void setControlProfile(String value);

    /**
     * Returns the value of the '<em><b>Calibration Offset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Calibration Offset</em>' attribute.
     * @see #setCalibrationOffset(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getAxleControlUnit_CalibrationOffset()
     * @model required="true"
     * @generated
     */
    double getCalibrationOffset();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getCalibrationOffset <em>Calibration Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Calibration Offset</em>' attribute.
     * @see #getCalibrationOffset()
     * @generated
     */
    void setCalibrationOffset(double value);

    /**
     * Returns the value of the '<em><b>Effective Brake Gain</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Effective Brake Gain</em>' attribute.
     * @see #setEffectiveBrakeGain(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getAxleControlUnit_EffectiveBrakeGain()
     * @model required="true"
     * @generated
     */
    double getEffectiveBrakeGain();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.AxleControlUnit#getEffectiveBrakeGain <em>Effective Brake Gain</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Effective Brake Gain</em>' attribute.
     * @see #getEffectiveBrakeGain()
     * @generated
     */
    void setEffectiveBrakeGain(double value);
} // AxleControlUnit
