/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tardoc;

import ch.elexis.core.model.ModelPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocFactory
 * @model kind="package"
 * @generated
 */
public interface TardocPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "tardoc";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/tardoc";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.arzttarife.ch.tarmed.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TardocPackage eINSTANCE = ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung <em>ITardoc Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocLeistung()
	 * @generated
	 */
	int ITARDOC_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__CODE = ModelPackage.ISERVICE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__TEXT = ModelPackage.ISERVICE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__LASTUPDATE = ModelPackage.ISERVICE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__DELETED = ModelPackage.ISERVICE__DELETED;

	/**
	 * The feature id for the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__PRICE = ModelPackage.ISERVICE__PRICE;

	/**
	 * The feature id for the '<em><b>Net Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__NET_PRICE = ModelPackage.ISERVICE__NET_PRICE;

	/**
	 * The feature id for the '<em><b>Minutes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__MINUTES = ModelPackage.ISERVICE__MINUTES;

	/**
	 * The feature id for the '<em><b>AL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__AL = ModelPackage.ISERVICE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>IPL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__IPL = ModelPackage.ISERVICE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__DIGNI_QUALI = ModelPackage.ISERVICE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Digni Quanti</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__DIGNI_QUANTI = ModelPackage.ISERVICE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__EXCLUSION = ModelPackage.ISERVICE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__EXTENSION = ModelPackage.ISERVICE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__PARENT = ModelPackage.ISERVICE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__VALID_FROM = ModelPackage.ISERVICE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__VALID_TO = ModelPackage.ISERVICE_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Service Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__SERVICE_TYP = ModelPackage.ISERVICE_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__LAW = ModelPackage.ISERVICE_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Sparte</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__SPARTE = ModelPackage.ISERVICE_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__CHAPTER = ModelPackage.ISERVICE_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Nickname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG__NICKNAME = ModelPackage.ISERVICE_FEATURE_COUNT + 13;

	/**
	 * The number of structural features of the '<em>ITardoc Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_LEISTUNG_FEATURE_COUNT = ModelPackage.ISERVICE_FEATURE_COUNT + 14;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension <em>ITardoc Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocExtension()
	 * @generated
	 */
	int ITARDOC_EXTENSION = 1;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_EXTENSION__LASTUPDATE = ModelPackage.IDENTIFIABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_EXTENSION__DELETED = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Limits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_EXTENSION__LIMITS = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Med Interpretation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_EXTENSION__MED_INTERPRETATION = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Tech Interpretation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_EXTENSION__TECH_INTERPRETATION = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>ITardoc Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_EXTENSION_FEATURE_COUNT = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup <em>ITardoc Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocGroup()
	 * @generated
	 */
	int ITARDOC_GROUP = 2;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__DELETED = ModelPackage.DELETEABLE__DELETED;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__LASTUPDATE = ModelPackage.DELETEABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__CODE = ModelPackage.DELETEABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Services</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__SERVICES = ModelPackage.DELETEABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__VALID_FROM = ModelPackage.DELETEABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__VALID_TO = ModelPackage.DELETEABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__LAW = ModelPackage.DELETEABLE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Limitations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__LIMITATIONS = ModelPackage.DELETEABLE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP__EXTENSION = ModelPackage.DELETEABLE_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>ITardoc Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_GROUP_FEATURE_COUNT = ModelPackage.DELETEABLE_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation <em>ITardoc Kumulation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocKumulation()
	 * @generated
	 */
	int ITARDOC_KUMULATION = 3;

	/**
	 * The feature id for the '<em><b>Slave Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__SLAVE_CODE = 0;

	/**
	 * The feature id for the '<em><b>Slave Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__SLAVE_ART = 1;

	/**
	 * The feature id for the '<em><b>Valid Side</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__VALID_SIDE = 2;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__VALID_FROM = 3;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__VALID_TO = 4;

	/**
	 * The feature id for the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__LAW = 5;

	/**
	 * The feature id for the '<em><b>Master Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__MASTER_CODE = 6;

	/**
	 * The feature id for the '<em><b>Master Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__MASTER_ART = 7;

	/**
	 * The feature id for the '<em><b>Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION__TYP = 8;

	/**
	 * The number of structural features of the '<em>ITardoc Kumulation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARDOC_KUMULATION_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tardoc.MandantType <em>Mandant Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.MandantType
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getMandantType()
	 * @generated
	 */
	int MANDANT_TYPE = 4;

	/**
	 * The meta object id for the '<em>Limitation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocLimitation()
	 * @generated
	 */
	int TARDOC_LIMITATION = 5;

	/**
	 * The meta object id for the '<em>Exclusion</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocExclusion()
	 * @generated
	 */
	int TARDOC_EXCLUSION = 6;

	/**
	 * The meta object id for the '<em>Kumulation Art</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocKumulationArt()
	 * @generated
	 */
	int TARDOC_KUMULATION_ART = 7;

	/**
	 * The meta object id for the '<em>Kumulation Typ</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp
	 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocKumulationTyp()
	 * @generated
	 */
	int TARDOC_KUMULATION_TYP = 8;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung <em>ITardoc Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITardoc Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung
	 * @generated
	 */
	EClass getITardocLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getAL <em>AL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>AL</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getAL()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_AL();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getIPL <em>IPL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>IPL</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getIPL()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_IPL();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getDigniQuali <em>Digni Quali</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Digni Quali</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getDigniQuali()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_DigniQuali();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getDigniQuanti <em>Digni Quanti</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Digni Quanti</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getDigniQuanti()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_DigniQuanti();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getExclusion <em>Exclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusion</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getExclusion()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_Exclusion();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getExtension <em>Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getExtension()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EReference getITardocLeistung_Extension();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getParent()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EReference getITardocLeistung_Parent();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getValidFrom()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getValidTo()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getServiceTyp <em>Service Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Service Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getServiceTyp()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_ServiceTyp();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getLaw <em>Law</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Law</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getLaw()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_Law();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getSparte <em>Sparte</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sparte</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getSparte()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_Sparte();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#isChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#isChapter()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getNickname <em>Nickname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nickname</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung#getNickname()
	 * @see #getITardocLeistung()
	 * @generated
	 */
	EAttribute getITardocLeistung_Nickname();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension <em>ITardoc Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITardoc Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension
	 * @generated
	 */
	EClass getITardocExtension();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension#getLimits <em>Limits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Limits</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension#getLimits()
	 * @see #getITardocExtension()
	 * @generated
	 */
	EAttribute getITardocExtension_Limits();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension#getMedInterpretation <em>Med Interpretation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Med Interpretation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension#getMedInterpretation()
	 * @see #getITardocExtension()
	 * @generated
	 */
	EAttribute getITardocExtension_MedInterpretation();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension#getTechInterpretation <em>Tech Interpretation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tech Interpretation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension#getTechInterpretation()
	 * @see #getITardocExtension()
	 * @generated
	 */
	EAttribute getITardocExtension_TechInterpretation();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup <em>ITardoc Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITardoc Group</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup
	 * @generated
	 */
	EClass getITardocGroup();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getCode()
	 * @see #getITardocGroup()
	 * @generated
	 */
	EAttribute getITardocGroup_Code();

	/**
	 * Returns the meta object for the attribute list '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getServices <em>Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Services</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getServices()
	 * @see #getITardocGroup()
	 * @generated
	 */
	EAttribute getITardocGroup_Services();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getValidFrom()
	 * @see #getITardocGroup()
	 * @generated
	 */
	EAttribute getITardocGroup_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getValidTo()
	 * @see #getITardocGroup()
	 * @generated
	 */
	EAttribute getITardocGroup_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getLaw <em>Law</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Law</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getLaw()
	 * @see #getITardocGroup()
	 * @generated
	 */
	EAttribute getITardocGroup_Law();

	/**
	 * Returns the meta object for the attribute list '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getLimitations <em>Limitations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Limitations</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getLimitations()
	 * @see #getITardocGroup()
	 * @generated
	 */
	EAttribute getITardocGroup_Limitations();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getExtension <em>Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup#getExtension()
	 * @see #getITardocGroup()
	 * @generated
	 */
	EReference getITardocGroup_Extension();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation <em>ITardoc Kumulation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITardoc Kumulation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation
	 * @generated
	 */
	EClass getITardocKumulation();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getSlaveCode <em>Slave Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Slave Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getSlaveCode()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_SlaveCode();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getSlaveArt <em>Slave Art</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Slave Art</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getSlaveArt()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_SlaveArt();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidSide <em>Valid Side</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid Side</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidSide()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_ValidSide();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidFrom()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getValidTo()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getLaw <em>Law</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Law</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getLaw()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_Law();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getMasterCode <em>Master Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Master Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getMasterCode()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_MasterCode();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getMasterArt <em>Master Art</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Master Art</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getMasterArt()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_MasterArt();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getTyp <em>Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation#getTyp()
	 * @see #getITardocKumulation()
	 * @generated
	 */
	EAttribute getITardocKumulation_Typ();

	/**
	 * Returns the meta object for enum '{@link ch.elexis.base.ch.arzttarife.tardoc.MandantType <em>Mandant Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Mandant Type</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.MandantType
	 * @generated
	 */
	EEnum getMandantType();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation <em>Limitation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Limitation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation"
	 * @generated
	 */
	EDataType getTardocLimitation();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion <em>Exclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Exclusion</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion"
	 * @generated
	 */
	EDataType getTardocExclusion();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt <em>Kumulation Art</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Kumulation Art</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt"
	 * @generated
	 */
	EDataType getTardocKumulationArt();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp <em>Kumulation Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Kumulation Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp"
	 * @generated
	 */
	EDataType getTardocKumulationTyp();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TardocFactory getTardocFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung <em>ITardoc Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocLeistung()
		 * @generated
		 */
		EClass ITARDOC_LEISTUNG = eINSTANCE.getITardocLeistung();

		/**
		 * The meta object literal for the '<em><b>AL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__AL = eINSTANCE.getITardocLeistung_AL();

		/**
		 * The meta object literal for the '<em><b>IPL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__IPL = eINSTANCE.getITardocLeistung_IPL();

		/**
		 * The meta object literal for the '<em><b>Digni Quali</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__DIGNI_QUALI = eINSTANCE.getITardocLeistung_DigniQuali();

		/**
		 * The meta object literal for the '<em><b>Digni Quanti</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__DIGNI_QUANTI = eINSTANCE.getITardocLeistung_DigniQuanti();

		/**
		 * The meta object literal for the '<em><b>Exclusion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__EXCLUSION = eINSTANCE.getITardocLeistung_Exclusion();

		/**
		 * The meta object literal for the '<em><b>Extension</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARDOC_LEISTUNG__EXTENSION = eINSTANCE.getITardocLeistung_Extension();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARDOC_LEISTUNG__PARENT = eINSTANCE.getITardocLeistung_Parent();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__VALID_FROM = eINSTANCE.getITardocLeistung_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__VALID_TO = eINSTANCE.getITardocLeistung_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Service Typ</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__SERVICE_TYP = eINSTANCE.getITardocLeistung_ServiceTyp();

		/**
		 * The meta object literal for the '<em><b>Law</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__LAW = eINSTANCE.getITardocLeistung_Law();

		/**
		 * The meta object literal for the '<em><b>Sparte</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__SPARTE = eINSTANCE.getITardocLeistung_Sparte();

		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__CHAPTER = eINSTANCE.getITardocLeistung_Chapter();

		/**
		 * The meta object literal for the '<em><b>Nickname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_LEISTUNG__NICKNAME = eINSTANCE.getITardocLeistung_Nickname();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension <em>ITardoc Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocExtension()
		 * @generated
		 */
		EClass ITARDOC_EXTENSION = eINSTANCE.getITardocExtension();

		/**
		 * The meta object literal for the '<em><b>Limits</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_EXTENSION__LIMITS = eINSTANCE.getITardocExtension_Limits();

		/**
		 * The meta object literal for the '<em><b>Med Interpretation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_EXTENSION__MED_INTERPRETATION = eINSTANCE.getITardocExtension_MedInterpretation();

		/**
		 * The meta object literal for the '<em><b>Tech Interpretation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_EXTENSION__TECH_INTERPRETATION = eINSTANCE.getITardocExtension_TechInterpretation();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup <em>ITardoc Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocGroup()
		 * @generated
		 */
		EClass ITARDOC_GROUP = eINSTANCE.getITardocGroup();

		/**
		 * The meta object literal for the '<em><b>Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_GROUP__CODE = eINSTANCE.getITardocGroup_Code();

		/**
		 * The meta object literal for the '<em><b>Services</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_GROUP__SERVICES = eINSTANCE.getITardocGroup_Services();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_GROUP__VALID_FROM = eINSTANCE.getITardocGroup_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_GROUP__VALID_TO = eINSTANCE.getITardocGroup_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Law</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_GROUP__LAW = eINSTANCE.getITardocGroup_Law();

		/**
		 * The meta object literal for the '<em><b>Limitations</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_GROUP__LIMITATIONS = eINSTANCE.getITardocGroup_Limitations();

		/**
		 * The meta object literal for the '<em><b>Extension</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARDOC_GROUP__EXTENSION = eINSTANCE.getITardocGroup_Extension();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation <em>ITardoc Kumulation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getITardocKumulation()
		 * @generated
		 */
		EClass ITARDOC_KUMULATION = eINSTANCE.getITardocKumulation();

		/**
		 * The meta object literal for the '<em><b>Slave Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__SLAVE_CODE = eINSTANCE.getITardocKumulation_SlaveCode();

		/**
		 * The meta object literal for the '<em><b>Slave Art</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__SLAVE_ART = eINSTANCE.getITardocKumulation_SlaveArt();

		/**
		 * The meta object literal for the '<em><b>Valid Side</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__VALID_SIDE = eINSTANCE.getITardocKumulation_ValidSide();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__VALID_FROM = eINSTANCE.getITardocKumulation_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__VALID_TO = eINSTANCE.getITardocKumulation_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Law</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__LAW = eINSTANCE.getITardocKumulation_Law();

		/**
		 * The meta object literal for the '<em><b>Master Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__MASTER_CODE = eINSTANCE.getITardocKumulation_MasterCode();

		/**
		 * The meta object literal for the '<em><b>Master Art</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__MASTER_ART = eINSTANCE.getITardocKumulation_MasterArt();

		/**
		 * The meta object literal for the '<em><b>Typ</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARDOC_KUMULATION__TYP = eINSTANCE.getITardocKumulation_Typ();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tardoc.MandantType <em>Mandant Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.MandantType
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getMandantType()
		 * @generated
		 */
		EEnum MANDANT_TYPE = eINSTANCE.getMandantType();

		/**
		 * The meta object literal for the '<em>Limitation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocLimitation()
		 * @generated
		 */
		EDataType TARDOC_LIMITATION = eINSTANCE.getTardocLimitation();

		/**
		 * The meta object literal for the '<em>Exclusion</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocExclusion()
		 * @generated
		 */
		EDataType TARDOC_EXCLUSION = eINSTANCE.getTardocExclusion();

		/**
		 * The meta object literal for the '<em>Kumulation Art</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocKumulationArt()
		 * @generated
		 */
		EDataType TARDOC_KUMULATION_ART = eINSTANCE.getTardocKumulationArt();

		/**
		 * The meta object literal for the '<em>Kumulation Typ</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp
		 * @see ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl#getTardocKumulationTyp()
		 * @generated
		 */
		EDataType TARDOC_KUMULATION_TYP = eINSTANCE.getTardocKumulationTyp();

	}

} //TardocPackage
