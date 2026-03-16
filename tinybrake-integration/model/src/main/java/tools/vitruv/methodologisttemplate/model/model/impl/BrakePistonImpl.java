/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import tools.vitruv.methodologisttemplate.model.model.BrakePiston;
import tools.vitruv.methodologisttemplate.model.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Brake Piston</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakePistonImpl#getDiameter <em>Diameter</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakePistonImpl#getStrokeLength <em>Stroke Length</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakePistonImpl#getMaterial <em>Material</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BrakePistonImpl extends MinimalEObjectImpl.Container implements BrakePiston {
    /**
     * The default value of the '{@link #getDiameter() <em>Diameter</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDiameter()
     * @generated
     * @ordered
     */
    protected static final double DIAMETER_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getDiameter() <em>Diameter</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDiameter()
     * @generated
     * @ordered
     */
    protected double diameter = DIAMETER_EDEFAULT;

    /**
     * The default value of the '{@link #getStrokeLength() <em>Stroke Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStrokeLength()
     * @generated
     * @ordered
     */
    protected static final double STROKE_LENGTH_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getStrokeLength() <em>Stroke Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStrokeLength()
     * @generated
     * @ordered
     */
    protected double strokeLength = STROKE_LENGTH_EDEFAULT;

    /**
     * The default value of the '{@link #getMaterial() <em>Material</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaterial()
     * @generated
     * @ordered
     */
    protected static final String MATERIAL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMaterial() <em>Material</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaterial()
     * @generated
     * @ordered
     */
    protected String material = MATERIAL_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BrakePistonImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.BRAKE_PISTON;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getDiameter() {
        return diameter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setDiameter(double newDiameter) {
        double oldDiameter = diameter;
        diameter = newDiameter;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.BRAKE_PISTON__DIAMETER, oldDiameter, diameter));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getStrokeLength() {
        return strokeLength;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setStrokeLength(double newStrokeLength) {
        double oldStrokeLength = strokeLength;
        strokeLength = newStrokeLength;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.BRAKE_PISTON__STROKE_LENGTH, oldStrokeLength, strokeLength));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getMaterial() {
        return material;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMaterial(String newMaterial) {
        String oldMaterial = material;
        material = newMaterial;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.BRAKE_PISTON__MATERIAL, oldMaterial, material));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.BRAKE_PISTON__DIAMETER:
                return getDiameter();
            case ModelPackage.BRAKE_PISTON__STROKE_LENGTH:
                return getStrokeLength();
            case ModelPackage.BRAKE_PISTON__MATERIAL:
                return getMaterial();
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
            case ModelPackage.BRAKE_PISTON__DIAMETER:
                setDiameter((Double) newValue);
                return;
            case ModelPackage.BRAKE_PISTON__STROKE_LENGTH:
                setStrokeLength((Double) newValue);
                return;
            case ModelPackage.BRAKE_PISTON__MATERIAL:
                setMaterial((String) newValue);
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
            case ModelPackage.BRAKE_PISTON__DIAMETER:
                setDiameter(DIAMETER_EDEFAULT);
                return;
            case ModelPackage.BRAKE_PISTON__STROKE_LENGTH:
                setStrokeLength(STROKE_LENGTH_EDEFAULT);
                return;
            case ModelPackage.BRAKE_PISTON__MATERIAL:
                setMaterial(MATERIAL_EDEFAULT);
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
            case ModelPackage.BRAKE_PISTON__DIAMETER:
                return diameter != DIAMETER_EDEFAULT;
            case ModelPackage.BRAKE_PISTON__STROKE_LENGTH:
                return strokeLength != STROKE_LENGTH_EDEFAULT;
            case ModelPackage.BRAKE_PISTON__MATERIAL:
                return MATERIAL_EDEFAULT == null ? material != null : !MATERIAL_EDEFAULT.equals(material);
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
        result.append(" (diameter: ");
        result.append(diameter);
        result.append(", strokeLength: ");
        result.append(strokeLength);
        result.append(", material: ");
        result.append(material);
        result.append(')');
        return result.toString();
    }
} // BrakePistonImpl
