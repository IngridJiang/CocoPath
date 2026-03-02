/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

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
import tools.vitruv.methodologisttemplate.model.model.BrakeCaliper;
import tools.vitruv.methodologisttemplate.model.model.BrakePiston;
import tools.vitruv.methodologisttemplate.model.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Brake Caliper</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl#getPistonCount <em>Piston Count</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl#getClampOpeningWidth <em>Clamp Opening Width</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl#getMaxPressure <em>Max Pressure</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl#getPistons <em>Pistons</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BrakeCaliperImpl extends MinimalEObjectImpl.Container implements BrakeCaliper {
    /**
     * The default value of the '{@link #getPistonCount() <em>Piston Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPistonCount()
     * @generated
     * @ordered
     */
    protected static final int PISTON_COUNT_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getPistonCount() <em>Piston Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPistonCount()
     * @generated
     * @ordered
     */
    protected int pistonCount = PISTON_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getClampOpeningWidth() <em>Clamp Opening Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClampOpeningWidth()
     * @generated
     * @ordered
     */
    protected static final double CLAMP_OPENING_WIDTH_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getClampOpeningWidth() <em>Clamp Opening Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClampOpeningWidth()
     * @generated
     * @ordered
     */
    protected double clampOpeningWidth = CLAMP_OPENING_WIDTH_EDEFAULT;

    /**
     * The default value of the '{@link #getMaxPressure() <em>Max Pressure</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxPressure()
     * @generated
     * @ordered
     */
    protected static final double MAX_PRESSURE_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getMaxPressure() <em>Max Pressure</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxPressure()
     * @generated
     * @ordered
     */
    protected double maxPressure = MAX_PRESSURE_EDEFAULT;

    /**
     * The cached value of the '{@link #getPistons() <em>Pistons</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPistons()
     * @generated
     * @ordered
     */
    protected EList<BrakePiston> pistons;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BrakeCaliperImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.BRAKE_CALIPER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getPistonCount() {
        return pistonCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPistonCount(int newPistonCount) {
        int oldPistonCount = pistonCount;
        pistonCount = newPistonCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.BRAKE_CALIPER__PISTON_COUNT, oldPistonCount, pistonCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getClampOpeningWidth() {
        return clampOpeningWidth;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setClampOpeningWidth(double newClampOpeningWidth) {
        double oldClampOpeningWidth = clampOpeningWidth;
        clampOpeningWidth = newClampOpeningWidth;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    ModelPackage.BRAKE_CALIPER__CLAMP_OPENING_WIDTH,
                    oldClampOpeningWidth,
                    clampOpeningWidth));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getMaxPressure() {
        return maxPressure;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMaxPressure(double newMaxPressure) {
        double oldMaxPressure = maxPressure;
        maxPressure = newMaxPressure;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.BRAKE_CALIPER__MAX_PRESSURE, oldMaxPressure, maxPressure));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<BrakePiston> getPistons() {
        if (pistons == null) {
            pistons = new EObjectContainmentEList<BrakePiston>(
                    BrakePiston.class, this, ModelPackage.BRAKE_CALIPER__PISTONS);
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
            case ModelPackage.BRAKE_CALIPER__PISTONS:
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
            case ModelPackage.BRAKE_CALIPER__PISTON_COUNT:
                return getPistonCount();
            case ModelPackage.BRAKE_CALIPER__CLAMP_OPENING_WIDTH:
                return getClampOpeningWidth();
            case ModelPackage.BRAKE_CALIPER__MAX_PRESSURE:
                return getMaxPressure();
            case ModelPackage.BRAKE_CALIPER__PISTONS:
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
            case ModelPackage.BRAKE_CALIPER__PISTON_COUNT:
                setPistonCount((Integer) newValue);
                return;
            case ModelPackage.BRAKE_CALIPER__CLAMP_OPENING_WIDTH:
                setClampOpeningWidth((Double) newValue);
                return;
            case ModelPackage.BRAKE_CALIPER__MAX_PRESSURE:
                setMaxPressure((Double) newValue);
                return;
            case ModelPackage.BRAKE_CALIPER__PISTONS:
                getPistons().clear();
                getPistons().addAll((Collection<? extends BrakePiston>) newValue);
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
            case ModelPackage.BRAKE_CALIPER__PISTON_COUNT:
                setPistonCount(PISTON_COUNT_EDEFAULT);
                return;
            case ModelPackage.BRAKE_CALIPER__CLAMP_OPENING_WIDTH:
                setClampOpeningWidth(CLAMP_OPENING_WIDTH_EDEFAULT);
                return;
            case ModelPackage.BRAKE_CALIPER__MAX_PRESSURE:
                setMaxPressure(MAX_PRESSURE_EDEFAULT);
                return;
            case ModelPackage.BRAKE_CALIPER__PISTONS:
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
            case ModelPackage.BRAKE_CALIPER__PISTON_COUNT:
                return pistonCount != PISTON_COUNT_EDEFAULT;
            case ModelPackage.BRAKE_CALIPER__CLAMP_OPENING_WIDTH:
                return clampOpeningWidth != CLAMP_OPENING_WIDTH_EDEFAULT;
            case ModelPackage.BRAKE_CALIPER__MAX_PRESSURE:
                return maxPressure != MAX_PRESSURE_EDEFAULT;
            case ModelPackage.BRAKE_CALIPER__PISTONS:
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
        result.append(" (pistonCount: ");
        result.append(pistonCount);
        result.append(", clampOpeningWidth: ");
        result.append(clampOpeningWidth);
        result.append(", maxPressure: ");
        result.append(maxPressure);
        result.append(')');
        return result.toString();
    }
} // BrakeCaliperImpl
