/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.Task;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskImpl#getName <em>Name</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskImpl#getPreemption <em>Preemption</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskImpl#getMultipleTaskActivationLimit <em>Multiple Task Activation Limit</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TaskImpl extends ProcessImpl implements Task {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getPreemption() <em>Preemption</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPreemption()
     * @generated
     * @ordered
     */
    protected static final String PREEMPTION_EDEFAULT = "cooperative";

    /**
     * The cached value of the '{@link #getPreemption() <em>Preemption</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPreemption()
     * @generated
     * @ordered
     */
    protected String preemption = PREEMPTION_EDEFAULT;

    /**
     * The default value of the '{@link #getMultipleTaskActivationLimit() <em>Multiple Task Activation Limit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMultipleTaskActivationLimit()
     * @generated
     * @ordered
     */
    protected static final int MULTIPLE_TASK_ACTIVATION_LIMIT_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getMultipleTaskActivationLimit() <em>Multiple Task Activation Limit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMultipleTaskActivationLimit()
     * @generated
     * @ordered
     */
    protected int multipleTaskActivationLimit = MULTIPLE_TASK_ACTIVATION_LIMIT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TaskImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.TASK;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.TASK__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getPreemption() {
        return preemption;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPreemption(String newPreemption) {
        String oldPreemption = preemption;
        preemption = newPreemption;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, Model2Package.TASK__PREEMPTION, oldPreemption, preemption));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getMultipleTaskActivationLimit() {
        return multipleTaskActivationLimit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMultipleTaskActivationLimit(int newMultipleTaskActivationLimit) {
        int oldMultipleTaskActivationLimit = multipleTaskActivationLimit;
        multipleTaskActivationLimit = newMultipleTaskActivationLimit;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this,
                    Notification.SET,
                    Model2Package.TASK__MULTIPLE_TASK_ACTIVATION_LIMIT,
                    oldMultipleTaskActivationLimit,
                    multipleTaskActivationLimit));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.TASK__NAME:
                return getName();
            case Model2Package.TASK__PREEMPTION:
                return getPreemption();
            case Model2Package.TASK__MULTIPLE_TASK_ACTIVATION_LIMIT:
                return getMultipleTaskActivationLimit();
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
            case Model2Package.TASK__NAME:
                setName((String) newValue);
                return;
            case Model2Package.TASK__PREEMPTION:
                setPreemption((String) newValue);
                return;
            case Model2Package.TASK__MULTIPLE_TASK_ACTIVATION_LIMIT:
                setMultipleTaskActivationLimit((Integer) newValue);
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
            case Model2Package.TASK__NAME:
                setName(NAME_EDEFAULT);
                return;
            case Model2Package.TASK__PREEMPTION:
                setPreemption(PREEMPTION_EDEFAULT);
                return;
            case Model2Package.TASK__MULTIPLE_TASK_ACTIVATION_LIMIT:
                setMultipleTaskActivationLimit(MULTIPLE_TASK_ACTIVATION_LIMIT_EDEFAULT);
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
            case Model2Package.TASK__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case Model2Package.TASK__PREEMPTION:
                return PREEMPTION_EDEFAULT == null ? preemption != null : !PREEMPTION_EDEFAULT.equals(preemption);
            case Model2Package.TASK__MULTIPLE_TASK_ACTIVATION_LIMIT:
                return multipleTaskActivationLimit != MULTIPLE_TASK_ACTIVATION_LIMIT_EDEFAULT;
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
        result.append(" (name: ");
        result.append(name);
        result.append(", preemption: ");
        result.append(preemption);
        result.append(", multipleTaskActivationLimit: ");
        result.append(multipleTaskActivationLimit);
        result.append(')');
        return result.toString();
    }
} // TaskImpl
