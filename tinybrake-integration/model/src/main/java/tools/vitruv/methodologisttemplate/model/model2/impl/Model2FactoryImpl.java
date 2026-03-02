/**
 */
package tools.vitruv.methodologisttemplate.model.model2.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import tools.vitruv.methodologisttemplate.model.model2.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Model2FactoryImpl extends EFactoryImpl implements Model2Factory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Model2Factory init() {
        try {
            Model2Factory theModel2Factory =
                    (Model2Factory) EPackage.Registry.INSTANCE.getEFactory(Model2Package.eNS_URI);
            if (theModel2Factory != null) {
                return theModel2Factory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new Model2FactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Model2FactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case Model2Package.CONTROL_SYSTEM:
                return createControlSystem();
            case Model2Package.AXLE_CONTROL_UNIT:
                return createAxleControlUnit();
            case Model2Package.HYDRAULIC_ACTUATOR_SPEC:
                return createHydraulicActuatorSpec();
            case Model2Package.PISTON_SPEC:
                return createPistonSpec();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ControlSystem createControlSystem() {
        ControlSystemImpl controlSystem = new ControlSystemImpl();
        return controlSystem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public AxleControlUnit createAxleControlUnit() {
        AxleControlUnitImpl axleControlUnit = new AxleControlUnitImpl();
        return axleControlUnit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public HydraulicActuatorSpec createHydraulicActuatorSpec() {
        HydraulicActuatorSpecImpl hydraulicActuatorSpec = new HydraulicActuatorSpecImpl();
        return hydraulicActuatorSpec;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public PistonSpec createPistonSpec() {
        PistonSpecImpl pistonSpec = new PistonSpecImpl();
        return pistonSpec;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Model2Package getModel2Package() {
        return (Model2Package) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static Model2Package getPackage() {
        return Model2Package.eINSTANCE;
    }
} // Model2FactoryImpl
