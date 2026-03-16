/**
 */
package tools.vitruv.methodologisttemplate.model.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Piston Spec</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.methodologisttemplate.model.model2.PistonSpec#getPistonArea <em>Piston Area</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getPistonSpec()
 * @model
 * @generated
 */
public interface PistonSpec extends EObject {
    /**
     * Returns the value of the '<em><b>Piston Area</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Piston Area</em>' attribute.
     * @see #setPistonArea(double)
     * @see tools.vitruv.methodologisttemplate.model.model2.Model2Package#getPistonSpec_PistonArea()
     * @model required="true"
     * @generated
     */
    double getPistonArea();

    /**
     * Sets the value of the '{@link tools.vitruv.methodologisttemplate.model.model2.PistonSpec#getPistonArea <em>Piston Area</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Piston Area</em>' attribute.
     * @see #getPistonArea()
     * @generated
     */
    void setPistonArea(double value);
} // PistonSpec
