/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import tools.vitruv.methodologisttemplate.model.model.BrakeDisc;
import tools.vitruv.methodologisttemplate.model.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Brake Disc</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl#getDiameter <em>Diameter</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl#getMaterial <em>Material</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl#getCoolingVanes <em>Cooling Vanes</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl#getThickness <em>Thickness</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BrakeDiscImpl extends MinimalEObjectImpl.Container implements BrakeDisc {
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
     * The default value of the '{@link #getCoolingVanes() <em>Cooling Vanes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCoolingVanes()
     * @generated
     * @ordered
     */
    protected static final int COOLING_VANES_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getCoolingVanes() <em>Cooling Vanes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCoolingVanes()
     * @generated
     * @ordered
     */
    protected int coolingVanes = COOLING_VANES_EDEFAULT;

    /**
     * The default value of the '{@link #getThickness() <em>Thickness</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getThickness()
     * @generated
     * @ordered
     */
    protected static final double THICKNESS_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getThickness() <em>Thickness</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getThickness()
     * @generated
     * @ordered
     */
    protected double thickness = THICKNESS_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BrakeDiscImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.BRAKE_DISC;
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
                    this, Notification.SET, ModelPackage.BRAKE_DISC__DIAMETER, oldDiameter, diameter));
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
                    this, Notification.SET, ModelPackage.BRAKE_DISC__MATERIAL, oldMaterial, material));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getCoolingVanes() {
        return coolingVanes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setCoolingVanes(int newCoolingVanes) {
        int oldCoolingVanes = coolingVanes;
        coolingVanes = newCoolingVanes;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.BRAKE_DISC__COOLING_VANES, oldCoolingVanes, coolingVanes));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getThickness() {
        return thickness;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setThickness(double newThickness) {
        double oldThickness = thickness;
        thickness = newThickness;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.BRAKE_DISC__THICKNESS, oldThickness, thickness));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.BRAKE_DISC__DIAMETER:
                return getDiameter();
            case ModelPackage.BRAKE_DISC__MATERIAL:
                return getMaterial();
            case ModelPackage.BRAKE_DISC__COOLING_VANES:
                return getCoolingVanes();
            case ModelPackage.BRAKE_DISC__THICKNESS:
                return getThickness();
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
            case ModelPackage.BRAKE_DISC__DIAMETER:
                setDiameter((Double) newValue);
                return;
            case ModelPackage.BRAKE_DISC__MATERIAL:
                setMaterial((String) newValue);
                return;
            case ModelPackage.BRAKE_DISC__COOLING_VANES:
                setCoolingVanes((Integer) newValue);
                return;
            case ModelPackage.BRAKE_DISC__THICKNESS:
                setThickness((Double) newValue);
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
            case ModelPackage.BRAKE_DISC__DIAMETER:
                setDiameter(DIAMETER_EDEFAULT);
                return;
            case ModelPackage.BRAKE_DISC__MATERIAL:
                setMaterial(MATERIAL_EDEFAULT);
                return;
            case ModelPackage.BRAKE_DISC__COOLING_VANES:
                setCoolingVanes(COOLING_VANES_EDEFAULT);
                return;
            case ModelPackage.BRAKE_DISC__THICKNESS:
                setThickness(THICKNESS_EDEFAULT);
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
            case ModelPackage.BRAKE_DISC__DIAMETER:
                return diameter != DIAMETER_EDEFAULT;
            case ModelPackage.BRAKE_DISC__MATERIAL:
                return MATERIAL_EDEFAULT == null ? material != null : !MATERIAL_EDEFAULT.equals(material);
            case ModelPackage.BRAKE_DISC__COOLING_VANES:
                return coolingVanes != COOLING_VANES_EDEFAULT;
            case ModelPackage.BRAKE_DISC__THICKNESS:
                return thickness != THICKNESS_EDEFAULT;
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
        result.append(", material: ");
        result.append(material);
        result.append(", coolingVanes: ");
        result.append(coolingVanes);
        result.append(", thickness: ");
        result.append(thickness);
        result.append(')');
        return result.toString();
    }
} // BrakeDiscImpl
