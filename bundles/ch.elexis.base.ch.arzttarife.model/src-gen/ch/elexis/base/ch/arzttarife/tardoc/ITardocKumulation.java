/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tardoc;

import java.time.LocalDate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITardoc Kumulation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getSlaveCode <em>Slave Code</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getSlaveArt <em>Slave Art</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidSide <em>Valid Side</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getLaw <em>Law</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getMasterCode <em>Master Code</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getMasterArt <em>Master Art</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getTyp <em>Typ</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITardocKumulation {
	/**
	 * Returns the value of the '<em><b>Slave Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Slave Code</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_SlaveCode()
	 * @model changeable="false"
	 * @generated
	 */
	String getSlaveCode();

	/**
	 * Returns the value of the '<em><b>Slave Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Slave Art</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_SlaveArt()
	 * @model dataType="ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt" changeable="false"
	 * @generated
	 */
	TardocKumulationArt getSlaveArt();

	/**
	 * Returns the value of the '<em><b>Valid Side</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid Side</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_ValidSide()
	 * @model changeable="false"
	 * @generated
	 */
	String getValidSide();

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Returns the value of the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Law</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_Law()
	 * @model changeable="false"
	 * @generated
	 */
	String getLaw();

	/**
	 * Returns the value of the '<em><b>Master Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Master Code</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_MasterCode()
	 * @model changeable="false"
	 * @generated
	 */
	String getMasterCode();

	/**
	 * Returns the value of the '<em><b>Master Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Master Art</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_MasterArt()
	 * @model dataType="ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt" changeable="false"
	 * @generated
	 */
	TardocKumulationArt getMasterArt();

	/**
	 * Returns the value of the '<em><b>Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Typ</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocKumulation_Typ()
	 * @model dataType="ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp" changeable="false"
	 * @generated
	 */
	TardocKumulationTyp getTyp();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model referenceDataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	boolean isValidKumulation(LocalDate reference);

} // ITardocKumulation
