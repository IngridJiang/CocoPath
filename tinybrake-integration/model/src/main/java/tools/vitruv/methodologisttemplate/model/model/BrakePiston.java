/**
 */
package tools.vitruv.methodologisttemplate.model.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Brake Piston</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getDiameter <em>Diameter</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getStrokeLength <em>Stroke Length</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getMaterial <em>Material</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakePiston()
 * @model
 * @generated
 */
public interface BrakePiston extends EObject {
    /**
     * Returns the value of the '<em><b>Diameter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diameter</em>' attribute.
     * @see #setDiameter(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakePiston_Diameter()
     * @model required="true"
     * @generated
     */
    double getDiameter();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getDiameter <em>Diameter</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diameter</em>' attribute.
     * @see #getDiameter()
     * @generated
     */
    void setDiameter(double value);

    /**
     * Returns the value of the '<em><b>Stroke Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Stroke Length</em>' attribute.
     * @see #setStrokeLength(double)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakePiston_StrokeLength()
     * @model required="true"
     * @generated
     */
    double getStrokeLength();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getStrokeLength <em>Stroke Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Stroke Length</em>' attribute.
     * @see #getStrokeLength()
     * @generated
     */
    void setStrokeLength(double value);

    /**
     * Returns the value of the '<em><b>Material</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Material</em>' attribute.
     * @see #setMaterial(String)
     * @see tools.vitruv.methodologisttemplate.model.model.ModelPackage#getBrakePiston_Material()
     * @model required="true"
     * @generated
     */
    String getMaterial();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model.BrakePiston#getMaterial <em>Material</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Material</em>' attribute.
     * @see #getMaterial()
     * @generated
     */
    void setMaterial(String value);
} // BrakePiston
