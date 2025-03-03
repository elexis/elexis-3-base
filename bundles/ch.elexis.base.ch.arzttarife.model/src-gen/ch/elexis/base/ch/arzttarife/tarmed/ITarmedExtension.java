/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import java.util.Map;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.WithExtInfo;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITarmed Extension</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getLimits <em>Limits</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getMedInterpretation <em>Med Interpretation</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getTechInterpretation <em>Tech Interpretation</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedExtension()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITarmedExtension extends Identifiable, Deleteable, WithExtInfo {

	/**
	 * Returns the value of the '<em><b>Limits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Limits</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Limits</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedExtension_Limits()
	 * @model transient="true" changeable="false"
	 * @generated
	 */
	Map<String, String> getLimits();

	/**
	 * Returns the value of the '<em><b>Med Interpretation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Med Interpretation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Med Interpretation</em>' attribute.
	 * @see #setMedInterpretation(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedExtension_MedInterpretation()
	 * @model
	 * @generated
	 */
	String getMedInterpretation();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getMedInterpretation <em>Med Interpretation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Med Interpretation</em>' attribute.
	 * @see #getMedInterpretation()
	 * @generated
	 */
	void setMedInterpretation(String value);

	/**
	 * Returns the value of the '<em><b>Tech Interpretation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tech Interpretation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tech Interpretation</em>' attribute.
	 * @see #setTechInterpretation(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedExtension_TechInterpretation()
	 * @model
	 * @generated
	 */
	String getTechInterpretation();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getTechInterpretation <em>Tech Interpretation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tech Interpretation</em>' attribute.
	 * @see #getTechInterpretation()
	 * @generated
	 */
	void setTechInterpretation(String value);
} // ITarmedExtension
