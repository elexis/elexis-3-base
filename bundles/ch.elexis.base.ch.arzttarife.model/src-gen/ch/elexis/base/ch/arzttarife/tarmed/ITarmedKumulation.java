/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITarmed Kumulation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveCode <em>Slave Code</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveArt <em>Slave Art</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidSide <em>Valid Side</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITarmedKumulation {

	/**
	 * Returns the value of the '<em><b>Slave Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Slave Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Slave Code</em>' attribute.
	 * @see #setSlaveCode(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_SlaveCode()
	 * @model
	 * @generated
	 */
	String getSlaveCode();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveCode <em>Slave Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Slave Code</em>' attribute.
	 * @see #getSlaveCode()
	 * @generated
	 */
	void setSlaveCode(String value);

	/**
	 * Returns the value of the '<em><b>Slave Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Slave Art</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Slave Art</em>' attribute.
	 * @see #setSlaveArt(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_SlaveArt()
	 * @model
	 * @generated
	 */
	String getSlaveArt();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveArt <em>Slave Art</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Slave Art</em>' attribute.
	 * @see #getSlaveArt()
	 * @generated
	 */
	void setSlaveArt(String value);

	/**
	 * Returns the value of the '<em><b>Valid Side</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid Side</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid Side</em>' attribute.
	 * @see #setValidSide(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_ValidSide()
	 * @model
	 * @generated
	 */
	String getValidSide();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidSide <em>Valid Side</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Valid Side</em>' attribute.
	 * @see #getValidSide()
	 * @generated
	 */
	void setValidSide(String value);
} // ITarmedKumulation
