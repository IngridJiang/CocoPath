/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.ISR;
import tools.vitruv.methodologisttemplate.model.model2.Label;
import tools.vitruv.methodologisttemplate.model.model2.Model2Package;
import tools.vitruv.methodologisttemplate.model.model2.Task;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl#getTasks <em>Tasks</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl#getIsrs <em>Isrs</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl#getRunnables <em>Runnables</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.impl.ComponentContainerImpl#getLabels <em>Labels</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ComponentContainerImpl extends MinimalEObjectImpl.Container implements ComponentContainer {
    /**
     * The cached value of the '{@link #getTasks() <em>Tasks</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTasks()
     * @generated
     * @ordered
     */
    protected EList<Task> tasks;

    /**
     * The cached value of the '{@link #getIsrs() <em>Isrs</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIsrs()
     * @generated
     * @ordered
     */
    protected EList<ISR> isrs;

    /**
     * The cached value of the '{@link #getRunnables() <em>Runnables</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRunnables()
     * @generated
     * @ordered
     */
    protected EList<tools.vitruv.methodologisttemplate.model.model2.Runnable> runnables;

    /**
     * The cached value of the '{@link #getLabels() <em>Labels</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabels()
     * @generated
     * @ordered
     */
    protected EList<Label> labels;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ComponentContainerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return Model2Package.Literals.COMPONENT_CONTAINER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<Task> getTasks() {
        if (tasks == null) {
            tasks = new EObjectContainmentEList<Task>(Task.class, this, Model2Package.COMPONENT_CONTAINER__TASKS);
        }
        return tasks;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<ISR> getIsrs() {
        if (isrs == null) {
            isrs = new EObjectContainmentEList<ISR>(ISR.class, this, Model2Package.COMPONENT_CONTAINER__ISRS);
        }
        return isrs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<tools.vitruv.methodologisttemplate.model.model2.Runnable> getRunnables() {
        if (runnables == null) {
            runnables = new EObjectContainmentEList<tools.vitruv.methodologisttemplate.model.model2.Runnable>(
                    tools.vitruv.methodologisttemplate.model.model2.Runnable.class,
                    this,
                    Model2Package.COMPONENT_CONTAINER__RUNNABLES);
        }
        return runnables;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<Label> getLabels() {
        if (labels == null) {
            labels = new EObjectContainmentEList<Label>(Label.class, this, Model2Package.COMPONENT_CONTAINER__LABELS);
        }
        return labels;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case Model2Package.COMPONENT_CONTAINER__TASKS:
                return ((InternalEList<?>) getTasks()).basicRemove(otherEnd, msgs);
            case Model2Package.COMPONENT_CONTAINER__ISRS:
                return ((InternalEList<?>) getIsrs()).basicRemove(otherEnd, msgs);
            case Model2Package.COMPONENT_CONTAINER__RUNNABLES:
                return ((InternalEList<?>) getRunnables()).basicRemove(otherEnd, msgs);
            case Model2Package.COMPONENT_CONTAINER__LABELS:
                return ((InternalEList<?>) getLabels()).basicRemove(otherEnd, msgs);
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
            case Model2Package.COMPONENT_CONTAINER__TASKS:
                return getTasks();
            case Model2Package.COMPONENT_CONTAINER__ISRS:
                return getIsrs();
            case Model2Package.COMPONENT_CONTAINER__RUNNABLES:
                return getRunnables();
            case Model2Package.COMPONENT_CONTAINER__LABELS:
                return getLabels();
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
            case Model2Package.COMPONENT_CONTAINER__TASKS:
                getTasks().clear();
                getTasks().addAll((Collection<? extends Task>) newValue);
                return;
            case Model2Package.COMPONENT_CONTAINER__ISRS:
                getIsrs().clear();
                getIsrs().addAll((Collection<? extends ISR>) newValue);
                return;
            case Model2Package.COMPONENT_CONTAINER__RUNNABLES:
                getRunnables().clear();
                getRunnables().addAll((Collection<? extends tools.vitruv.methodologisttemplate.model.model2.Runnable>)
                        newValue);
                return;
            case Model2Package.COMPONENT_CONTAINER__LABELS:
                getLabels().clear();
                getLabels().addAll((Collection<? extends Label>) newValue);
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
            case Model2Package.COMPONENT_CONTAINER__TASKS:
                getTasks().clear();
                return;
            case Model2Package.COMPONENT_CONTAINER__ISRS:
                getIsrs().clear();
                return;
            case Model2Package.COMPONENT_CONTAINER__RUNNABLES:
                getRunnables().clear();
                return;
            case Model2Package.COMPONENT_CONTAINER__LABELS:
                getLabels().clear();
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
            case Model2Package.COMPONENT_CONTAINER__TASKS:
                return tasks != null && !tasks.isEmpty();
            case Model2Package.COMPONENT_CONTAINER__ISRS:
                return isrs != null && !isrs.isEmpty();
            case Model2Package.COMPONENT_CONTAINER__RUNNABLES:
                return runnables != null && !runnables.isEmpty();
            case Model2Package.COMPONENT_CONTAINER__LABELS:
                return labels != null && !labels.isEmpty();
        }
        return super.eIsSet(featureID);
    }
} // ComponentContainerImpl
