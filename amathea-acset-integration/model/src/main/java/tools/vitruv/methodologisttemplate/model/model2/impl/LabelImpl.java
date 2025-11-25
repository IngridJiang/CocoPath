/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import tools.vitruv.methodologisttemplate.model.model2.Label;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Label</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.LabelImpl#getInitialValue <em>Initial Value</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.LabelImpl#isConstant <em>Constant</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.LabelImpl#isIsVolatile <em>Is Volatile</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LabelImpl extends MinimalEObjectImpl.Container implements Label {
    /**
     * The default value of the '{@link #getInitialValue() <em>Initial Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInitialValue()
     * @generated
     * @ordered
     */
    protected static final int INITIAL_VALUE_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getInitialValue() <em>Initial Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInitialValue()
     * @generated
     * @ordered
     */
    protected int initialValue = INITIAL_VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #isConstant() <em>Constant</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isConstant()
     * @generated
     * @ordered
     */
    protected static final boolean CONSTANT_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isConstant() <em>Constant</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isConstant()
     * @generated
     * @ordered
     */
    protected boolean constant = CONSTANT_EDEFAULT;

    /**
     * The default value of the '{@link #isIsVolatile() <em>Is Volatile</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsVolatile()
     * @generated
     * @ordered
     */
    protected static final boolean IS_VOLATILE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIsVolatile() <em>Is Volatile</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsVolatile()
     * @generated
     * @ordered
     */
    protected boolean isVolatile = IS_VOLATILE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected LabelImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.LABEL;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getInitialValue() {
        return initialValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setInitialValue(int newInitialValue) {
        int oldInitialValue = initialValue;
        initialValue = newInitialValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, Model2Package.LABEL__INITIAL_VALUE, oldInitialValue, initialValue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isConstant() {
        return constant;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setConstant(boolean newConstant) {
        boolean oldConstant = constant;
        constant = newConstant;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, Model2Package.LABEL__CONSTANT, oldConstant, constant));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isIsVolatile() {
        return isVolatile;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setIsVolatile(boolean newIsVolatile) {
        boolean oldIsVolatile = isVolatile;
        isVolatile = newIsVolatile;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, Model2Package.LABEL__IS_VOLATILE, oldIsVolatile, isVolatile));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.LABEL__INITIAL_VALUE:
                return getInitialValue();
            case Model2Package.LABEL__CONSTANT:
                return isConstant();
            case Model2Package.LABEL__IS_VOLATILE:
                return isIsVolatile();
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
            case Model2Package.LABEL__INITIAL_VALUE:
                setInitialValue((Integer) newValue);
                return;
            case Model2Package.LABEL__CONSTANT:
                setConstant((Boolean) newValue);
                return;
            case Model2Package.LABEL__IS_VOLATILE:
                setIsVolatile((Boolean) newValue);
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
            case Model2Package.LABEL__INITIAL_VALUE:
                setInitialValue(INITIAL_VALUE_EDEFAULT);
                return;
            case Model2Package.LABEL__CONSTANT:
                setConstant(CONSTANT_EDEFAULT);
                return;
            case Model2Package.LABEL__IS_VOLATILE:
                setIsVolatile(IS_VOLATILE_EDEFAULT);
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
            case Model2Package.LABEL__INITIAL_VALUE:
                return initialValue != INITIAL_VALUE_EDEFAULT;
            case Model2Package.LABEL__CONSTANT:
                return constant != CONSTANT_EDEFAULT;
            case Model2Package.LABEL__IS_VOLATILE:
                return isVolatile != IS_VOLATILE_EDEFAULT;
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
        result.append(" (initialValue: ");
        result.append(initialValue);
        result.append(", constant: ");
        result.append(constant);
        result.append(", isVolatile: ");
        result.append(isVolatile);
        result.append(')');
        return result.toString();
    }
} // LabelImpl
