/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import ch.elexis.core.model.IBillable;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITarmed Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getMinutes <em>Minutes</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getAL <em>AL</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getTL <em>TL</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuali <em>Digni Quali</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuanti <em>Digni Quanti</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExclusion <em>Exclusion</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITarmedLeistung extends IBillable {
	/**
	 * Returns the value of the '<em><b>Minutes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Minutes</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Minutes</em>' attribute.
	 * @see #setMinutes(int)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Minutes()
	 * @model
	 * @generated
	 */
	int getMinutes();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getMinutes <em>Minutes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Minutes</em>' attribute.
	 * @see #getMinutes()
	 * @generated
	 */
	void setMinutes(int value);

	/**
	 * Returns the value of the '<em><b>AL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>AL</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>AL</em>' attribute.
	 * @see #setAL(int)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_AL()
	 * @model
	 * @generated
	 */
	int getAL();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getAL <em>AL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>AL</em>' attribute.
	 * @see #getAL()
	 * @generated
	 */
	void setAL(int value);

	/**
	 * Returns the value of the '<em><b>TL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>TL</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>TL</em>' attribute.
	 * @see #setTL(int)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_TL()
	 * @model
	 * @generated
	 */
	int getTL();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getTL <em>TL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>TL</em>' attribute.
	 * @see #getTL()
	 * @generated
	 */
	void setTL(int value);

	/**
	 * Returns the value of the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Digni Quali</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Digni Quali</em>' attribute.
	 * @see #setDigniQuali(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_DigniQuali()
	 * @model
	 * @generated
	 */
	String getDigniQuali();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuali <em>Digni Quali</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Digni Quali</em>' attribute.
	 * @see #getDigniQuali()
	 * @generated
	 */
	void setDigniQuali(String value);

	/**
	 * Returns the value of the '<em><b>Digni Quanti</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Digni Quanti</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Digni Quanti</em>' attribute.
	 * @see #setDigniQuanti(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_DigniQuanti()
	 * @model
	 * @generated
	 */
	String getDigniQuanti();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuanti <em>Digni Quanti</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Digni Quanti</em>' attribute.
	 * @see #getDigniQuanti()
	 * @generated
	 */
	void setDigniQuanti(String value);

	/**
	 * Returns the value of the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclusion</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusion</em>' attribute.
	 * @see #setExclusion(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Exclusion()
	 * @model
	 * @generated
	 */
	String getExclusion();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExclusion <em>Exclusion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusion</em>' attribute.
	 * @see #getExclusion()
	 * @generated
	 */
	void setExclusion(String value);

} // ITarmedLeistung
