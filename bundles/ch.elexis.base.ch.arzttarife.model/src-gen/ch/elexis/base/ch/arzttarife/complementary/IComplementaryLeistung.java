/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.complementary;

import java.time.LocalDate;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.WithAssignableId;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IComplementary Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getDescription <em>Description</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getFixedValue <em>Fixed Value</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#isFixedValueSet <em>Fixed Value Set</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidTo <em>Valid To</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage#getIComplementaryLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IComplementaryLeistung extends IBillable, Deleteable, WithAssignableId {

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage#getIComplementaryLeistung_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Chapter</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chapter</em>' attribute.
	 * @see #setChapter(String)
	 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage#getIComplementaryLeistung_Chapter()
	 * @model
	 * @generated
	 */
	String getChapter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getChapter <em>Chapter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Chapter</em>' attribute.
	 * @see #getChapter()
	 * @generated
	 */
	void setChapter(String value);

	/**
	 * Returns the value of the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fixed Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fixed Value</em>' attribute.
	 * @see #setFixedValue(int)
	 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage#getIComplementaryLeistung_FixedValue()
	 * @model
	 * @generated
	 */
	int getFixedValue();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getFixedValue <em>Fixed Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fixed Value</em>' attribute.
	 * @see #getFixedValue()
	 * @generated
	 */
	void setFixedValue(int value);

	/**
	 * Returns the value of the '<em><b>Fixed Value Set</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fixed Value Set</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fixed Value Set</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage#getIComplementaryLeistung_FixedValueSet()
	 * @model changeable="false"
	 * @generated
	 */
	boolean isFixedValueSet();

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see #setValidFrom(LocalDate)
	 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage#getIComplementaryLeistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidFrom <em>Valid From</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage#getIComplementaryLeistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidTo <em>Valid To</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Valid To</em>' attribute.
	 * @see #getValidTo()
	 * @generated
	 */
	void setValidTo(LocalDate value);
} // IComplementaryLeistung
