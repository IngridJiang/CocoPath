/**
 */
package tools.vitruv.methodologisttemplate.model.model;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Brake System</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeSystem#getBrakeDiscs <em>Brake Discs</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeSystem#getCalipers <em>Calipers</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeSystem()
 * @model
 * @generated
 */
public interface BrakeSystem extends EObject {
    /**
     * Returns the value of the '<em><b>Brake Discs</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Brake Discs</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeSystem_BrakeDiscs()
     * @model containment="true"
     * @generated
     */
    EList<BrakeDisc> getBrakeDiscs();

    /**
     * Returns the value of the '<em><b>Calipers</b></em>' containment reference list.
     * The list contents are of type {@link tools.vitruv.methodologisttemplate.model.model.BrakeCaliper}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Calipers</em>' containment reference list.
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeSystem_Calipers()
     * @model containment="true"
     * @generated
     */
    EList<BrakeCaliper> getCalipers();
} // BrakeSystem
