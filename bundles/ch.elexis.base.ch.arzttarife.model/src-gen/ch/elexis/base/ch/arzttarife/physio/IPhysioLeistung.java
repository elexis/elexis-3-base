/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.physio;

import java.time.LocalDate;

import ch.elexis.core.model.IBillable;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IPhysio Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getTP <em>TP</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getZiffer <em>Ziffer</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getDescription <em>Description</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getLaw <em>Law</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#getIPhysioLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IPhysioLeistung extends IBillable {

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid From</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see #setValidFrom(LocalDate)
	 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#getIPhysioLeistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidFrom <em>Valid From</em>}' attribute.
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
	 * <p>
	 * If the meaning of the '<em>Valid To</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see #setValidTo(LocalDate)
	 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#getIPhysioLeistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidTo <em>Valid To</em>}' attribute.
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
	 * <p>
	 * If the meaning of the '<em>TP</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>TP</em>' attribute.
	 * @see #setTP(String)
	 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#getIPhysioLeistung_TP()
	 * @model
	 * @generated
	 */
	String getTP();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getTP <em>TP</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>TP</em>' attribute.
	 * @see #getTP()
	 * @generated
	 */
	void setTP(String value);

	/**
	 * Returns the value of the '<em><b>Ziffer</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ziffer</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ziffer</em>' attribute.
	 * @see #setZiffer(String)
	 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#getIPhysioLeistung_Ziffer()
	 * @model
	 * @generated
	 */
	String getZiffer();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getZiffer <em>Ziffer</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ziffer</em>' attribute.
	 * @see #getZiffer()
	 * @generated
	 */
	void setZiffer(String value);

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
	 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#getIPhysioLeistung_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Law</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#getIPhysioLeistung_Law()
	 * @model changeable="false"
	 * @generated
	 */
	String getLaw();
} // IPhysioLeistung
