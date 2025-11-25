/**
 */
package tools.vitruv.methodologisttemplate.model.model.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.methodologisttemplate.model.model.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage
 * @generated
 */
public class ModelAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ModelPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ModelPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModelSwitch<Adapter> modelSwitch = new ModelSwitch<Adapter>() {
        @Override
        public Adapter caseAscetModule(AscetModule object) {
            return createAscetModuleAdapter();
        }

        @Override
        public Adapter caseNamed(Named object) {
            return createNamedAdapter();
        }

        @Override
        public Adapter caseTask(Task object) {
            return createTaskAdapter();
        }

        @Override
        public Adapter caseMethod(Method object) {
            return createMethodAdapter();
        }

        @Override
        public Adapter caseInterruptTask(InterruptTask object) {
            return createInterruptTaskAdapter();
        }

        @Override
        public Adapter caseInitTask(InitTask object) {
            return createInitTaskAdapter();
        }

        @Override
        public Adapter casePeriodicTask(PeriodicTask object) {
            return createPeriodicTaskAdapter();
        }

        @Override
        public Adapter caseSoftwareTask(SoftwareTask object) {
            return createSoftwareTaskAdapter();
        }

        @Override
        public Adapter caseTimeTableTask(TimeTableTask object) {
            return createTimeTableTaskAdapter();
        }

        @Override
        public Adapter defaultCase(EObject object) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.AscetModule <em>Ascet Module</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.AscetModule
     * @generated
     */
    public Adapter createAscetModuleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.Named <em>Named</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.Named
     * @generated
     */
    public Adapter createNamedAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.Task <em>Task</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.Task
     * @generated
     */
    public Adapter createTaskAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.Method <em>Method</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.Method
     * @generated
     */
    public Adapter createMethodAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.InterruptTask <em>Interrupt Task</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.InterruptTask
     * @generated
     */
    public Adapter createInterruptTaskAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.InitTask <em>Init Task</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.InitTask
     * @generated
     */
    public Adapter createInitTaskAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.PeriodicTask <em>Periodic Task</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.PeriodicTask
     * @generated
     */
    public Adapter createPeriodicTaskAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.SoftwareTask <em>Software Task</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.SoftwareTask
     * @generated
     */
    public Adapter createSoftwareTaskAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link tools.vitruv.methodologisttemplate.model.model.TimeTableTask <em>Time Table Task</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see tools.vitruv.methodologisttemplate.model.model.TimeTableTask
     * @generated
     */
    public Adapter createTimeTableTaskAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }
} // ModelAdapterFactory
