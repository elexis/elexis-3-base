/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.Identifiable;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITarmed Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getCode <em>Code</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedGroup()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITarmedGroup extends Deleteable, Identifiable {

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
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedGroup_Code()
	 * @model
	 * @generated
	 */
	String getCode();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getCode <em>Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Code</em>' attribute.
	 * @see #getCode()
	 * @generated
	 */
	void setCode(String value);
} // ITarmedGroup
