/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import java.time.LocalDate;


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
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getLaw <em>Law</em>}</li>
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
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_SlaveCode()
	 * @model changeable="false"
	 * @generated
	 */
	String getSlaveCode();

	/**
	 * Returns the value of the '<em><b>Slave Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Slave Art</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Slave Art</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_SlaveArt()
	 * @model changeable="false"
	 * @generated
	 */
	String getSlaveArt();

	/**
	 * Returns the value of the '<em><b>Valid Side</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid Side</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid Side</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_ValidSide()
	 * @model changeable="false"
	 * @generated
	 */
	String getValidSide();

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid From</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid To</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Returns the value of the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Law</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Law</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedKumulation_Law()
	 * @model changeable="false"
	 * @generated
	 */
	String getLaw();

	/**
	 * Checks if the kumulation is still/already valid on the given date
	 * 
	 * @param date
	 *            on which it should be valid
	 * @return true if valid, false otherwise
	 */
	boolean isValidKumulation(LocalDate reference);
} // ITarmedKumulation
