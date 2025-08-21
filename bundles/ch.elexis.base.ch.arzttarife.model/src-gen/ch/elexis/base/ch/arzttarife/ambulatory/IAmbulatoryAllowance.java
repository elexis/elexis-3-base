/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.ambulatory;

import java.time.LocalDate;

import ch.elexis.core.model.IBillable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IAmbulatory Allowance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getTP <em>TP</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getDigniQuali <em>Digni Quali</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getTyp <em>Typ</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage#getIAmbulatoryAllowance()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IAmbulatoryAllowance extends IBillable {
	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see #setValidFrom(LocalDate)
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage#getIAmbulatoryAllowance_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidFrom <em>Valid From</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Valid From</em>' attribute.
	 * @see #getValidFrom()
	 * @generated
	 */
	void setValidFrom(LocalDate value);

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see #setValidTo(LocalDate)
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage#getIAmbulatoryAllowance_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidTo <em>Valid To</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Valid To</em>' attribute.
	 * @see #getValidTo()
	 * @generated
	 */
	void setValidTo(LocalDate value);

	/**
	 * Returns the value of the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chapter</em>' attribute.
	 * @see #setChapter(String)
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage#getIAmbulatoryAllowance_Chapter()
	 * @model
	 * @generated
	 */
	String getChapter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getChapter <em>Chapter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Chapter</em>' attribute.
	 * @see #getChapter()
	 * @generated
	 */
	void setChapter(String value);

	/**
	 * Returns the value of the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>TP</em>' attribute.
	 * @see #setTP(String)
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage#getIAmbulatoryAllowance_TP()
	 * @model
	 * @generated
	 */
	String getTP();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getTP <em>TP</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>TP</em>' attribute.
	 * @see #getTP()
	 * @generated
	 */
	void setTP(String value);

	/**
	 * Returns the value of the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Digni Quali</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage#getIAmbulatoryAllowance_DigniQuali()
	 * @model changeable="false"
	 * @generated
	 */
	String getDigniQuali();

	/**
	 * Returns the value of the '<em><b>Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Typ</em>' attribute.
	 * @see #setTyp(AmbulantePauschalenTyp)
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage#getIAmbulatoryAllowance_Typ()
	 * @model dataType="ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp"
	 * @generated
	 */
	AmbulantePauschalenTyp getTyp();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getTyp <em>Typ</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Typ</em>' attribute.
	 * @see #getTyp()
	 * @generated
	 */
	void setTyp(AmbulantePauschalenTyp value);

} // IAmbulatoryAllowance
