/**
 */
package tools.vitruv.methodologisttemplate.model.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see tools.vitruv.methodologisttemplate.model.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "model";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://vitruv.tools/methodologisttemplate/model";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "model";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelPackage eINSTANCE = tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl.init();

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeSystemImpl <em>Brake System</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakeSystemImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakeSystem()
     * @generated
     */
    int BRAKE_SYSTEM = 0;

    /**
     * The feature id for the '<em><b>Brake Discs</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_SYSTEM__BRAKE_DISCS = 0;

    /**
     * The feature id for the '<em><b>Calipers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_SYSTEM__CALIPERS = 1;

    /**
     * The number of structural features of the '<em>Brake System</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_SYSTEM_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Brake System</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_SYSTEM_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl <em>Brake Disc</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakeDisc()
     * @generated
     */
    int BRAKE_DISC = 1;

    /**
     * The feature id for the '<em><b>Diameter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_DISC__DIAMETER = 0;

    /**
     * The feature id for the '<em><b>Material</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_DISC__MATERIAL = 1;

    /**
     * The feature id for the '<em><b>Cooling Vanes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_DISC__COOLING_VANES = 2;

    /**
     * The feature id for the '<em><b>Thickness</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_DISC__THICKNESS = 3;

    /**
     * The number of structural features of the '<em>Brake Disc</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_DISC_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Brake Disc</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_DISC_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakePistonImpl <em>Brake Piston</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakePistonImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakePiston()
     * @generated
     */
    int BRAKE_PISTON = 2;

    /**
     * The feature id for the '<em><b>Diameter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_PISTON__DIAMETER = 0;

    /**
     * The feature id for the '<em><b>Stroke Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_PISTON__STROKE_LENGTH = 1;

    /**
     * The feature id for the '<em><b>Material</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_PISTON__MATERIAL = 2;

    /**
     * The number of structural features of the '<em>Brake Piston</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_PISTON_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Brake Piston</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_PISTON_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl <em>Brake Caliper</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl
     * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakeCaliper()
     * @generated
     */
    int BRAKE_CALIPER = 3;

    /**
     * The feature id for the '<em><b>Piston Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_CALIPER__PISTON_COUNT = 0;

    /**
     * The feature id for the '<em><b>Clamp Opening Width</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_CALIPER__CLAMP_OPENING_WIDTH = 1;

    /**
     * The feature id for the '<em><b>Max Pressure</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_CALIPER__MAX_PRESSURE = 2;

    /**
     * The feature id for the '<em><b>Pistons</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_CALIPER__PISTONS = 3;

    /**
     * The number of structural features of the '<em>Brake Caliper</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_CALIPER_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Brake Caliper</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRAKE_CALIPER_OPERATION_COUNT = 0;

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.BrakeSystem <em>Brake System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Brake System</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeSystem
     * @generated
     */
    EClass getBrakeSystem();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model.BrakeSystem#getBrakeDiscs <em>Brake Discs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Brake Discs</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeSystem#getBrakeDiscs()
     * @see #getBrakeSystem()
     * @generated
     */
    EReference getBrakeSystem_BrakeDiscs();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model.BrakeSystem#getCalipers <em>Calipers</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Calipers</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeSystem#getCalipers()
     * @see #getBrakeSystem()
     * @generated
     */
    EReference getBrakeSystem_Calipers();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc <em>Brake Disc</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Brake Disc</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeDisc
     * @generated
     */
    EClass getBrakeDisc();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getDiameter <em>Diameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Diameter</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getDiameter()
     * @see #getBrakeDisc()
     * @generated
     */
    EAttribute getBrakeDisc_Diameter();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getMaterial <em>Material</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Material</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getMaterial()
     * @see #getBrakeDisc()
     * @generated
     */
    EAttribute getBrakeDisc_Material();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getCoolingVanes <em>Cooling Vanes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Cooling Vanes</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getCoolingVanes()
     * @see #getBrakeDisc()
     * @generated
     */
    EAttribute getBrakeDisc_CoolingVanes();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getThickness <em>Thickness</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Thickness</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getThickness()
     * @see #getBrakeDisc()
     * @generated
     */
    EAttribute getBrakeDisc_Thickness();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston <em>Brake Piston</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Brake Piston</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakePiston
     * @generated
     */
    EClass getBrakePiston();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getDiameter <em>Diameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Diameter</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakePiston#getDiameter()
     * @see #getBrakePiston()
     * @generated
     */
    EAttribute getBrakePiston_Diameter();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getStrokeLength <em>Stroke Length</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Stroke Length</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakePiston#getStrokeLength()
     * @see #getBrakePiston()
     * @generated
     */
    EAttribute getBrakePiston_StrokeLength();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getMaterial <em>Material</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Material</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakePiston#getMaterial()
     * @see #getBrakePiston()
     * @generated
     */
    EAttribute getBrakePiston_Material();

    /**
     * Returns the meta object for class '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper <em>Brake Caliper</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Brake Caliper</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeCaliper
     * @generated
     */
    EClass getBrakeCaliper();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getPistonCount <em>Piston Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Piston Count</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getPistonCount()
     * @see #getBrakeCaliper()
     * @generated
     */
    EAttribute getBrakeCaliper_PistonCount();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getClampOpeningWidth <em>Clamp Opening Width</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Clamp Opening Width</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getClampOpeningWidth()
     * @see #getBrakeCaliper()
     * @generated
     */
    EAttribute getBrakeCaliper_ClampOpeningWidth();

    /**
     * Returns the meta object for the attribute '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getMaxPressure <em>Max Pressure</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Pressure</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getMaxPressure()
     * @see #getBrakeCaliper()
     * @generated
     */
    EAttribute getBrakeCaliper_MaxPressure();

    /**
     * Returns the meta object for the containment reference list '{@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getPistons <em>Pistons</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Pistons</em>'.
     * @see tools.vitruv.methodologisttemplate.model.model.BrakeCaliper#getPistons()
     * @see #getBrakeCaliper()
     * @generated
     */
    EReference getBrakeCaliper_Pistons();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ModelFactory getModelFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeSystemImpl <em>Brake System</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakeSystemImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakeSystem()
         * @generated
         */
        EClass BRAKE_SYSTEM = eINSTANCE.getBrakeSystem();

        /**
         * The meta object literal for the '<em><b>Brake Discs</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BRAKE_SYSTEM__BRAKE_DISCS = eINSTANCE.getBrakeSystem_BrakeDiscs();

        /**
         * The meta object literal for the '<em><b>Calipers</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BRAKE_SYSTEM__CALIPERS = eINSTANCE.getBrakeSystem_Calipers();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl <em>Brake Disc</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakeDiscImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakeDisc()
         * @generated
         */
        EClass BRAKE_DISC = eINSTANCE.getBrakeDisc();

        /**
         * The meta object literal for the '<em><b>Diameter</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_DISC__DIAMETER = eINSTANCE.getBrakeDisc_Diameter();

        /**
         * The meta object literal for the '<em><b>Material</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_DISC__MATERIAL = eINSTANCE.getBrakeDisc_Material();

        /**
         * The meta object literal for the '<em><b>Cooling Vanes</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_DISC__COOLING_VANES = eINSTANCE.getBrakeDisc_CoolingVanes();

        /**
         * The meta object literal for the '<em><b>Thickness</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_DISC__THICKNESS = eINSTANCE.getBrakeDisc_Thickness();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakePistonImpl <em>Brake Piston</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakePistonImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakePiston()
         * @generated
         */
        EClass BRAKE_PISTON = eINSTANCE.getBrakePiston();

        /**
         * The meta object literal for the '<em><b>Diameter</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_PISTON__DIAMETER = eINSTANCE.getBrakePiston_Diameter();

        /**
         * The meta object literal for the '<em><b>Stroke Length</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_PISTON__STROKE_LENGTH = eINSTANCE.getBrakePiston_StrokeLength();

        /**
         * The meta object literal for the '<em><b>Material</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_PISTON__MATERIAL = eINSTANCE.getBrakePiston_Material();

        /**
         * The meta object literal for the '{@link tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl <em>Brake Caliper</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tools.vitruv.methodologisttemplate.model.model.impl.BrakeCaliperImpl
         * @see tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl#getBrakeCaliper()
         * @generated
         */
        EClass BRAKE_CALIPER = eINSTANCE.getBrakeCaliper();

        /**
         * The meta object literal for the '<em><b>Piston Count</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_CALIPER__PISTON_COUNT = eINSTANCE.getBrakeCaliper_PistonCount();

        /**
         * The meta object literal for the '<em><b>Clamp Opening Width</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_CALIPER__CLAMP_OPENING_WIDTH = eINSTANCE.getBrakeCaliper_ClampOpeningWidth();

        /**
         * The meta object literal for the '<em><b>Max Pressure</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BRAKE_CALIPER__MAX_PRESSURE = eINSTANCE.getBrakeCaliper_MaxPressure();

        /**
         * The meta object literal for the '<em><b>Pistons</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BRAKE_CALIPER__PISTONS = eINSTANCE.getBrakeCaliper_Pistons();
    }
} // ModelPackage
