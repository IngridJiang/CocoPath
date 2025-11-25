/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Probability Switch Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry#getProbability <em>Probability</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getProbabilitySwitchEntry()
 * @model
 * @generated
 */
public interface ProbabilitySwitchEntry extends EObject {
    /**
     * Returns the value of the '<em><b>Probability</b></em>' attribute.
     * The default value is <code>"0.0"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Probability</em>' attribute.
     * @see #setProbability(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getProbabilitySwitchEntry_Probability()
     * @model default="0.0"
     * @generated
     */
    double getProbability();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.ProbabilitySwitchEntry#getProbability <em>Probability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Probability</em>' attribute.
     * @see #getProbability()
     * @generated
     */
    void setProbability(double value);
} // ProbabilitySwitchEntry
