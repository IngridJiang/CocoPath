/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.PistonSpec;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Piston Spec</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.PistonSpecImpl#getPistonArea <em>Piston Area</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PistonSpecImpl extends MinimalEObjectImpl.Container implements PistonSpec {
    /**
     * The default value of the '{@link #getPistonArea() <em>Piston Area</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPistonArea()
     * @generated
     * @ordered
     */
    protected static final double PISTON_AREA_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getPistonArea() <em>Piston Area</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPistonArea()
     * @generated
     * @ordered
     */
    protected double pistonArea = PISTON_AREA_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected PistonSpecImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.PISTON_SPEC;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public double getPistonArea() {
        return pistonArea;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPistonArea(double newPistonArea) {
        double oldPistonArea = pistonArea;
        pistonArea = newPistonArea;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, Model2Package.PISTON_SPEC__PISTON_AREA, oldPistonArea, pistonArea));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.PISTON_SPEC__PISTON_AREA:
                return getPistonArea();
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
            case Model2Package.PISTON_SPEC__PISTON_AREA:
                setPistonArea((Double) newValue);
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
            case Model2Package.PISTON_SPEC__PISTON_AREA:
                setPistonArea(PISTON_AREA_EDEFAULT);
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
            case Model2Package.PISTON_SPEC__PISTON_AREA:
                return pistonArea != PISTON_AREA_EDEFAULT;
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
        result.append(" (pistonArea: ");
        result.append(pistonArea);
        result.append(')');
        return result.toString();
    }
} // PistonSpecImpl
