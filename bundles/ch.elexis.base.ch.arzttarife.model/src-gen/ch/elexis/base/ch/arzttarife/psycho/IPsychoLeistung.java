/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.psycho;

import java.time.LocalDate;

import ch.elexis.core.model.IBillable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IPsycho Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getTP <em>TP</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getDescription <em>Description</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getLimitations <em>Limitations</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getExclusions <em>Exclusions</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#getIPsychoLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IPsychoLeistung extends IBillable {
	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see #setValidFrom(LocalDate)
	 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#getIPsychoLeistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidFrom <em>Valid From</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#getIPsychoLeistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidTo <em>Valid To</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Valid To</em>' attribute.
	 * @see #getValidTo()
	 * @generated
	 */
	void setValidTo(LocalDate value);

	/**
	 * Returns the value of the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>TP</em>' attribute.
	 * @see #setTP(String)
	 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#getIPsychoLeistung_TP()
	 * @model
	 * @generated
	 */
	String getTP();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getTP <em>TP</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>TP</em>' attribute.
	 * @see #getTP()
	 * @generated
	 */
	void setTP(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#getIPsychoLeistung_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Limitations</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Limitations</em>' attribute.
	 * @see #setLimitations(String)
	 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#getIPsychoLeistung_Limitations()
	 * @model
	 * @generated
	 */
	String getLimitations();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getLimitations <em>Limitations</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Limitations</em>' attribute.
	 * @see #getLimitations()
	 * @generated
	 */
	void setLimitations(String value);

	/**
	 * Returns the value of the '<em><b>Exclusions</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusions</em>' attribute.
	 * @see #setExclusions(String)
	 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#getIPsychoLeistung_Exclusions()
	 * @model
	 * @generated
	 */
	String getExclusions();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getExclusions <em>Exclusions</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusions</em>' attribute.
	 * @see #getExclusions()
	 * @generated
	 */
	void setExclusions(String value);

} // IPsychoLeistung
