/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Label Switch</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.LabelSwitch#getLabel <em>Label</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getLabelSwitch()
 * @model
 * @generated
 */
public interface LabelSwitch extends EObject {
    /**
     * Returns the value of the '<em><b>Label</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Label</em>' containment reference.
     * @see #setLabel(Label)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getLabelSwitch_Label()
     * @model containment="true"
     * @generated
     */
    Label getLabel();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.LabelSwitch#getLabel <em>Label</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Label</em>' containment reference.
     * @see #getLabel()
     * @generated
     */
    void setLabel(Label value);
} // LabelSwitch
