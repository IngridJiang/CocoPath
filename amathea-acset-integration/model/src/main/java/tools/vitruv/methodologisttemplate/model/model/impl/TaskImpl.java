/**
 */
package tools.vitruv.methodologisttemplate.model.model.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import tools.vitruv.methodologisttemplate.model.model.Method;
import tools.vitruv.methodologisttemplate.model.model.ModelPackage;
import tools.vitruv.methodologisttemplate.model.model.Task;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.TaskImpl#getPriority <em>Priority</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.impl.TaskImpl#getProcesses <em>Processes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TaskImpl extends NamedImpl implements Task {
    /**
     * The default value of the '{@link #getPriority() <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPriority()
     * @generated
     * @ordered
     */
    protected static final int PRIORITY_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getPriority() <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPriority()
     * @generated
     * @ordered
     */
    protected int priority = PRIORITY_EDEFAULT;

    /**
     * The cached value of the '{@link #getProcesses() <em>Processes</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProcesses()
     * @generated
     * @ordered
     */
    protected Method processes;

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
        return ModelPackage.Literals.TASK;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPriority(int newPriority) {
        int oldPriority = priority;
        priority = newPriority;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.TASK__PRIORITY, oldPriority, priority));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Method getProcesses() {
        if (processes != null && processes.eIsProxy()) {
            InternalEObject oldProcesses = (InternalEObject) processes;
            processes = (Method) eResolveProxy(oldProcesses);
            if (processes != oldProcesses) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(
                            this, Notification.RESOLVE, ModelPackage.TASK__PROCESSES, oldProcesses, processes));
            }
        }
        return processes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Method basicGetProcesses() {
        return processes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setProcesses(Method newProcesses) {
        Method oldProcesses = processes;
        processes = newProcesses;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, ModelPackage.TASK__PROCESSES, oldProcesses, processes));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.TASK__PRIORITY:
                return getPriority();
            case ModelPackage.TASK__PROCESSES:
                if (resolve) return getProcesses();
                return basicGetProcesses();
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
            case ModelPackage.TASK__PRIORITY:
                setPriority((Integer) newValue);
                return;
            case ModelPackage.TASK__PROCESSES:
                setProcesses((Method) newValue);
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
            case ModelPackage.TASK__PRIORITY:
                setPriority(PRIORITY_EDEFAULT);
                return;
            case ModelPackage.TASK__PROCESSES:
                setProcesses((Method) null);
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
            case ModelPackage.TASK__PRIORITY:
                return priority != PRIORITY_EDEFAULT;
            case ModelPackage.TASK__PROCESSES:
                return processes != null;
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
        result.append(" (priority: ");
        result.append(priority);
        result.append(')');
        return result.toString();
    }
} // TaskImpl
