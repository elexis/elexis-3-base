/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.rfe;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.Identifiable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IReason For Encounter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getEncounter <em>Encounter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getCode <em>Code</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getText <em>Text</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.rfe.RfePackage#getIReasonForEncounter()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IReasonForEncounter extends Deleteable, Identifiable {
	/**
	 * Returns the value of the '<em><b>Encounter</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Encounter</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Encounter</em>' reference.
	 * @see #setEncounter(IEncounter)
	 * @see ch.elexis.base.ch.arzttarife.rfe.RfePackage#getIReasonForEncounter_Encounter()
	 * @model
	 * @generated
	 */
	IEncounter getEncounter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getEncounter <em>Encounter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Encounter</em>' reference.
	 * @see #getEncounter()
	 * @generated
	 */
	void setEncounter(IEncounter value);

	/**
	 * Returns the value of the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code</em>' attribute.
	 * @see #setCode(String)
	 * @see ch.elexis.base.ch.arzttarife.rfe.RfePackage#getIReasonForEncounter_Code()
	 * @model
	 * @generated
	 */
	String getCode();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getCode <em>Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Code</em>' attribute.
	 * @see #getCode()
	 * @generated
	 */
	void setCode(String value);

	/**
	 * Returns the value of the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Text</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Text</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.rfe.RfePackage#getIReasonForEncounter_Text()
	 * @model changeable="false"
	 * @generated
	 */
	String getText();

} // IReasonForEncounter
