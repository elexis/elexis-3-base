/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IService;
import java.time.LocalDate;
import java.util.List;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ITarmed Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getAL <em>AL</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getTL <em>TL</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuali <em>Digni Quali</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuanti <em>Digni Quanti</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExclusion <em>Exclusion</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExtension <em>Extension</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getParent <em>Parent</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getServiceTyp <em>Service Typ</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getLaw <em>Law</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getSparte <em>Sparte</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#isChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getNickname <em>Nickname</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ITarmedLeistung extends IService {
	/**
	 * Returns the value of the '<em><b>AL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>AL</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>AL</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_AL()
	 * @model changeable="false"
	 * @generated
	 */
	int getAL();

	/**
	 * Returns the value of the '<em><b>TL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>TL</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>TL</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_TL()
	 * @model changeable="false"
	 * @generated
	 */
	int getTL();

	/**
	 * Returns the value of the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Digni Quali</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Digni Quali</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_DigniQuali()
	 * @model changeable="false"
	 * @generated
	 */
	String getDigniQuali();

	/**
	 * Returns the value of the '<em><b>Digni Quanti</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Digni Quanti</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Digni Quanti</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_DigniQuanti()
	 * @model changeable="false"
	 * @generated
	 */
	String getDigniQuanti();

	/**
	 * Returns the value of the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclusion</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusion</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Exclusion()
	 * @model changeable="false"
	 * @generated
	 */
	String getExclusion();

	/**
	 * Returns the value of the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Extension</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extension</em>' reference.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Extension()
	 * @model changeable="false"
	 * @generated
	 */
	ITarmedExtension getExtension();

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' reference.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Parent()
	 * @model changeable="false"
	 * @generated
	 */
	ITarmedLeistung getParent();

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid From</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_ValidFrom()
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
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Returns the value of the '<em><b>Service Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Service Typ</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Service Typ</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_ServiceTyp()
	 * @model changeable="false"
	 * @generated
	 */
	String getServiceTyp();

	/**
	 * Returns the value of the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Law</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Law</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Law()
	 * @model changeable="false"
	 * @generated
	 */
	String getLaw();

	/**
	 * Returns the value of the '<em><b>Sparte</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sparte</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sparte</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Sparte()
	 * @model changeable="false"
	 * @generated
	 */
	String getSparte();

	/**
	 * Returns the value of the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Chapter</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chapter</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Chapter()
	 * @model changeable="false"
	 * @generated
	 */
	boolean isChapter();

	/**
	 * Returns the value of the '<em><b>Nickname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nickname</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nickname</em>' attribute.
	 * @see #setNickname(String)
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getITarmedLeistung_Nickname()
	 * @model
	 * @generated
	 */
	String getNickname();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getNickname <em>Nickname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nickname</em>' attribute.
	 * @see #getNickname()
	 * @generated
	 */
	void setNickname(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * Leistung ist beinhaltet in retournierten Leistungsgruppen
	 * </p>
	 * <!-- end-user-doc -->
	 * @model dateDataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	List<String> getServiceGroups(LocalDate date);

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * Leistung ist Teil der retournierten Leistungsblöcke
	 * </p>
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
	 * @model type="ch.elexis.core.types.List&lt;ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation&gt;" many="false" typeDataType="ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt"
	 * @generated
	 */
	List<ITarmedKumulation> getKumulations(TarmedKumulationArt type);

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

} // ITarmedLeistung
