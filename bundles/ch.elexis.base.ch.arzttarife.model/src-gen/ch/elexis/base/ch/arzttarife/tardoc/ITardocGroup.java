/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tardoc;

import java.time.LocalDate;
import java.util.List;

import ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation;
import ch.elexis.core.model.Deleteable;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.Identifiable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITardoc Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getCode <em>Code</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getServices <em>Services</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getLaw <em>Law</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getLimitations <em>Limitations</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getExtension <em>Extension</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITardocGroup extends Deleteable, Identifiable {
	/**
	 * Returns the value of the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup_Code()
	 * @model changeable="false"
	 * @generated
	 */
	String getCode();

	/**
	 * Returns the value of the '<em><b>Services</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Services</em>' attribute list.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup_Services()
	 * @model
	 * @generated
	 */
	List<String> getServices();

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Returns the value of the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Law</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup_Law()
	 * @model changeable="false"
	 * @generated
	 */
	String getLaw();

	/**
	 * Returns the value of the '<em><b>Limitations</b></em>' attribute list.
	 * The list contents are of type {@link ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Limitations</em>' attribute list.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup_Limitations()
	 * @model dataType="ch.elexis.base.ch.arzttarife.tardoc.TardocLimitation" changeable="false"
	 * @generated
	 */
	List<TardocLimitation> getLimitations();

	/**
	 * Returns the value of the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extension</em>' reference.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocGroup_Extension()
	 * @model changeable="false"
	 * @generated
	 */
	ITardocExtension getExtension();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model referenceDataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	boolean validAt(LocalDate reference);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model type="ch.elexis.core.types.List&lt;ch.elexis.base.ch.arzttarife.tardoc.TardocExclusion&gt;" many="false"
	 * @generated
	 */
	List<TardocExclusion> getExclusions(IEncounter encounter);

} // ITardocGroup
