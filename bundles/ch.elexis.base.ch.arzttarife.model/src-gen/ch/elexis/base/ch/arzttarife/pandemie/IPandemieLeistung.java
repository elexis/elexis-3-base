/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.pandemie;

import java.time.LocalDate;

import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.WithAssignableId;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IPandemie Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getDescription <em>Description</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getCents <em>Cents</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getTaxpoints <em>Taxpoints</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage#getIPandemieLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IPandemieLeistung extends WithAssignableId, IBillable, Deleteable {

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage#getIPandemieLeistung_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getDescription <em>Description</em>}' attribute.
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
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chapter</em>' attribute.
	 * @see #setChapter(String)
	 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage#getIPandemieLeistung_Chapter()
	 * @model
	 * @generated
	 */
	String getChapter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getChapter <em>Chapter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Chapter</em>' attribute.
	 * @see #getChapter()
	 * @generated
	 */
	void setChapter(String value);

	/**
	 * Returns the value of the '<em><b>Cents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cents</em>' attribute.
	 * @see #setCents(int)
	 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage#getIPandemieLeistung_Cents()
	 * @model
	 * @generated
	 */
	int getCents();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getCents <em>Cents</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cents</em>' attribute.
	 * @see #getCents()
	 * @generated
	 */
	void setCents(int value);

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see #setValidFrom(LocalDate)
	 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage#getIPandemieLeistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidFrom <em>Valid From</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage#getIPandemieLeistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidTo <em>Valid To</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Valid To</em>' attribute.
	 * @see #getValidTo()
	 * @generated
	 */
	void setValidTo(LocalDate value);

	/**
	 * Returns the value of the '<em><b>Taxpoints</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Taxpoints</em>' attribute.
	 * @see #setTaxpoints(int)
	 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage#getIPandemieLeistung_Taxpoints()
	 * @model
	 * @generated
	 */
	int getTaxpoints();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getTaxpoints <em>Taxpoints</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Taxpoints</em>' attribute.
	 * @see #getTaxpoints()
	 * @generated
	 */
	void setTaxpoints(int value);
} // IPandemieLeistung
