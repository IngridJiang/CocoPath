/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Label</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.Label#getInitialValue <em>Initial Value</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.Label#isConstant <em>Constant</em>}</li>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.Label#isIsVolatile <em>Is Volatile</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getLabel()
 * @model
 * @generated
 */
public interface Label extends EObject {
    /**
     * Returns the value of the '<em><b>Initial Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Initial Value</em>' attribute.
     * @see #setInitialValue(int)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getLabel_InitialValue()
     * @model
     * @generated
     */
    int getInitialValue();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.Label#getInitialValue <em>Initial Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Initial Value</em>' attribute.
     * @see #getInitialValue()
     * @generated
     */
    void setInitialValue(int value);

    /**
     * Returns the value of the '<em><b>Constant</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Constant</em>' attribute.
     * @see #setConstant(boolean)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getLabel_Constant()
     * @model default="false"
     * @generated
     */
    boolean isConstant();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.Label#isConstant <em>Constant</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Constant</em>' attribute.
     * @see #isConstant()
     * @generated
     */
    void setConstant(boolean value);

    /**
     * Returns the value of the '<em><b>Is Volatile</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Is Volatile</em>' attribute.
     * @see #setIsVolatile(boolean)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getLabel_IsVolatile()
     * @model default="false"
     * @generated
     */
    boolean isIsVolatile();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.Label#isIsVolatile <em>Is Volatile</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Volatile</em>' attribute.
     * @see #isIsVolatile()
     * @generated
     */
    void setIsVolatile(boolean value);
} // Label
