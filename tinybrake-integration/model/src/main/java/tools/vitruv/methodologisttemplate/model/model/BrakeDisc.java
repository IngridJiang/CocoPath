/**
 */
package tools.vitruv.methodologisttemplate.model.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Brake Disc</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getDiameter <em>Diameter</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getMaterial <em>Material</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getCoolingVanes <em>Cooling Vanes</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getThickness <em>Thickness</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeDisc()
 * @model
 * @generated
 */
public interface BrakeDisc extends EObject {
    /**
     * Returns the value of the '<em><b>Diameter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diameter</em>' attribute.
     * @see #setDiameter(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeDisc_Diameter()
     * @model required="true"
     * @generated
     */
    double getDiameter();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getDiameter <em>Diameter</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diameter</em>' attribute.
     * @see #getDiameter()
     * @generated
     */
    void setDiameter(double value);

    /**
     * Returns the value of the '<em><b>Material</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Material</em>' attribute.
     * @see #setMaterial(String)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeDisc_Material()
     * @model required="true"
     * @generated
     */
    String getMaterial();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getMaterial <em>Material</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Material</em>' attribute.
     * @see #getMaterial()
     * @generated
     */
    void setMaterial(String value);

    /**
     * Returns the value of the '<em><b>Cooling Vanes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Cooling Vanes</em>' attribute.
     * @see #setCoolingVanes(int)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeDisc_CoolingVanes()
     * @model required="true"
     * @generated
     */
    int getCoolingVanes();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getCoolingVanes <em>Cooling Vanes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Cooling Vanes</em>' attribute.
     * @see #getCoolingVanes()
     * @generated
     */
    void setCoolingVanes(int value);

    /**
     * Returns the value of the '<em><b>Thickness</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Thickness</em>' attribute.
     * @see #setThickness(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakeDisc_Thickness()
     * @model required="true"
     * @generated
     */
    double getThickness();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakeDisc#getThickness <em>Thickness</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Thickness</em>' attribute.
     * @see #getThickness()
     * @generated
     */
    void setThickness(double value);
} // BrakeDisc
