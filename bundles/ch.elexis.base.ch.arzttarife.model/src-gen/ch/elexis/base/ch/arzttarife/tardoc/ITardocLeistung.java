/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tardoc;

import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IService;

import java.time.LocalDate;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITardoc Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getAL <em>AL</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getIPL <em>IPL</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getDigniQuali <em>Digni Quali</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getDigniQuanti <em>Digni Quanti</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getExclusion <em>Exclusion</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getExtension <em>Extension</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getParent <em>Parent</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getServiceTyp <em>Service Typ</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getLaw <em>Law</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getSparte <em>Sparte</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#isChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getNickname <em>Nickname</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITardocLeistung extends IService {
	/**
	 * Returns the value of the '<em><b>AL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>AL</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_AL()
	 * @model changeable="false"
	 * @generated
	 */
	int getAL();

	/**
	 * Returns the value of the '<em><b>IPL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>IPL</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_IPL()
	 * @model changeable="false"
	 * @generated
	 */
	int getIPL();

	/**
	 * Returns the value of the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Digni Quali</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_DigniQuali()
	 * @model changeable="false"
	 * @generated
	 */
	String getDigniQuali();

	/**
	 * Returns the value of the '<em><b>Digni Quanti</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Digni Quanti</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_DigniQuanti()
	 * @model changeable="false"
	 * @generated
	 */
	String getDigniQuanti();

	/**
	 * Returns the value of the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusion</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_Exclusion()
	 * @model changeable="false"
	 * @generated
	 */
	String getExclusion();

	/**
	 * Returns the value of the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extension</em>' reference.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_Extension()
	 * @model changeable="false"
	 * @generated
	 */
	ITardocExtension getExtension();

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' reference.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_Parent()
	 * @model changeable="false"
	 * @generated
	 */
	ITardocLeistung getParent();

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Returns the value of the '<em><b>Service Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Service Typ</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_ServiceTyp()
	 * @model changeable="false"
	 * @generated
	 */
	String getServiceTyp();

	/**
	 * Returns the value of the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Law</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_Law()
	 * @model changeable="false"
	 * @generated
	 */
	String getLaw();

	/**
	 * Returns the value of the '<em><b>Sparte</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sparte</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_Sparte()
	 * @model changeable="false"
	 * @generated
	 */
	String getSparte();

	/**
	 * Returns the value of the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chapter</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_Chapter()
	 * @model changeable="false"
	 * @generated
	 */
	boolean isChapter();

	/**
	 * Returns the value of the '<em><b>Nickname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nickname</em>' attribute.
	 * @see #setNickname(String)
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#getITardocLeistung_Nickname()
	 * @model
	 * @generated
	 */
	String getNickname();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getNickname <em>Nickname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nickname</em>' attribute.
	 * @see #getNickname()
	 * @generated
	 */
	void setNickname(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dateDataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	List<String> getServiceGroups(LocalDate date);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dateDataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	List<String> getServiceBlocks(LocalDate date);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	boolean requiresSide();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	int getAL(IMandator mandator);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model type="ch.elexis.core.types.List&lt;ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation&gt;" many="false" typeDataType="ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt"
	 * @generated
	 */
	List<ITardocKumulation> getKumulations(TardocKumulationArt type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model type="ch.elexis.core.types.List&lt;org.eclipse.emf.ecore.EString&gt;" many="false" dateDataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	List<String> getHierarchy(LocalDate date);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isZuschlagsleistung();

} // ITardocLeistung
