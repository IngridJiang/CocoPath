/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.TaskRunnableCall;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Task Runnable Call</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.TaskRunnableCallImpl#getRunnable <em>Runnable</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TaskRunnableCallImpl extends MinimalEObjectImpl.Container implements TaskRunnableCall {
    /**
     * The cached value of the '{@link #getRunnable() <em>Runnable</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRunnable()
     * @generated
     * @ordered
     */
    protected tools.vitruv.methodologisttemplate.model.model2.Runnable runnable;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TaskRunnableCallImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.TASK_RUNNABLE_CALL;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public tools.vitruv.methodologisttemplate.model.model2.Runnable getRunnable() {
        if (runnable != null && runnable.eIsProxy()) {
            InternalEObject oldRunnable = (InternalEObject) runnable;
            runnable = (tools.vitruv.methodologisttemplate.model.model2.Runnable) eResolveProxy(oldRunnable);
            if (runnable != oldRunnable) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(
                            this,
                            Notification.RESOLVE,
                            Model2Package.TASK_RUNNABLE_CALL__RUNNABLE,
                            oldRunnable,
                            runnable));
            }
        }
        return runnable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public tools.vitruv.methodologisttemplate.model.model2.Runnable basicGetRunnable() {
        return runnable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setRunnable(tools.vitruv.methodologisttemplate.model.model2.Runnable newRunnable) {
        tools.vitruv.methodologisttemplate.model.model2.Runnable oldRunnable = runnable;
        runnable = newRunnable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(
                    this, Notification.SET, Model2Package.TASK_RUNNABLE_CALL__RUNNABLE, oldRunnable, runnable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case Model2Package.TASK_RUNNABLE_CALL__RUNNABLE:
                if (resolve) return getRunnable();
                return basicGetRunnable();
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
            case Model2Package.TASK_RUNNABLE_CALL__RUNNABLE:
                setRunnable((tools.vitruv.methodologisttemplate.model.model2.Runnable) newValue);
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
            case Model2Package.TASK_RUNNABLE_CALL__RUNNABLE:
                setRunnable((tools.vitruv.methodologisttemplate.model.model2.Runnable) null);
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
            case Model2Package.TASK_RUNNABLE_CALL__RUNNABLE:
                return runnable != null;
        }
        return super.eIsSet(featureID);
    }
} // TaskRunnableCallImpl
