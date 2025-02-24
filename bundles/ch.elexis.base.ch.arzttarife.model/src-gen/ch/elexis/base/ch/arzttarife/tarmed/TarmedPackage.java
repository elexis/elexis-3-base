/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import ch.elexis.core.model.ModelPackage;

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
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedFactory
 * @model kind="package"
 * @generated
 */
public interface TarmedPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "tarmed";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/tarmed";

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
	TarmedPackage eINSTANCE = ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung <em>ITarmed Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedLeistung()
	 * @generated
	 */
	int ITARMED_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__CODE = ModelPackage.ISERVICE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__TEXT = ModelPackage.ISERVICE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__LASTUPDATE = ModelPackage.ISERVICE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__DELETED = ModelPackage.ISERVICE__DELETED;

	/**
	 * The feature id for the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__PRICE = ModelPackage.ISERVICE__PRICE;

	/**
	 * The feature id for the '<em><b>Net Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__NET_PRICE = ModelPackage.ISERVICE__NET_PRICE;

	/**
	 * The feature id for the '<em><b>Minutes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__MINUTES = ModelPackage.ISERVICE__MINUTES;

	/**
	 * The feature id for the '<em><b>AL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__AL = ModelPackage.ISERVICE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>TL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__TL = ModelPackage.ISERVICE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__DIGNI_QUALI = ModelPackage.ISERVICE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Digni Quanti</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__DIGNI_QUANTI = ModelPackage.ISERVICE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__EXCLUSION = ModelPackage.ISERVICE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__EXTENSION = ModelPackage.ISERVICE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__PARENT = ModelPackage.ISERVICE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__VALID_FROM = ModelPackage.ISERVICE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__VALID_TO = ModelPackage.ISERVICE_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Service Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__SERVICE_TYP = ModelPackage.ISERVICE_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__LAW = ModelPackage.ISERVICE_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Sparte</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__SPARTE = ModelPackage.ISERVICE_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__CHAPTER = ModelPackage.ISERVICE_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Nickname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__NICKNAME = ModelPackage.ISERVICE_FEATURE_COUNT + 13;

	/**
	 * The number of structural features of the '<em>ITarmed Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG_FEATURE_COUNT = ModelPackage.ISERVICE_FEATURE_COUNT + 14;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension <em>ITarmed Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedExtension()
	 * @generated
	 */
	int ITARMED_EXTENSION = 1;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION__LASTUPDATE = ModelPackage.IDENTIFIABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION__DELETED = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Limits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION__LIMITS = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Med Interpretation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION__MED_INTERPRETATION = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Tech Interpretation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION__TECH_INTERPRETATION = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>ITarmed Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION_FEATURE_COUNT = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup <em>ITarmed Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedGroup()
	 * @generated
	 */
	int ITARMED_GROUP = 2;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__DELETED = ModelPackage.DELETEABLE__DELETED;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__LASTUPDATE = ModelPackage.DELETEABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__CODE = ModelPackage.DELETEABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Services</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__SERVICES = ModelPackage.DELETEABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__VALID_FROM = ModelPackage.DELETEABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__VALID_TO = ModelPackage.DELETEABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__LAW = ModelPackage.DELETEABLE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Limitations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__LIMITATIONS = ModelPackage.DELETEABLE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__EXTENSION = ModelPackage.DELETEABLE_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>ITarmed Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP_FEATURE_COUNT = ModelPackage.DELETEABLE_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation <em>ITarmed Kumulation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedKumulation()
	 * @generated
	 */
	int ITARMED_KUMULATION = 3;

	/**
	 * The feature id for the '<em><b>Slave Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__SLAVE_CODE = 0;

	/**
	 * The feature id for the '<em><b>Slave Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__SLAVE_ART = 1;

	/**
	 * The feature id for the '<em><b>Valid Side</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__VALID_SIDE = 2;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__VALID_FROM = 3;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__VALID_TO = 4;

	/**
	 * The feature id for the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__LAW = 5;

	/**
	 * The feature id for the '<em><b>Master Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__MASTER_CODE = 6;

	/**
	 * The feature id for the '<em><b>Master Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__MASTER_ART = 7;

	/**
	 * The feature id for the '<em><b>Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__TYP = 8;

	/**
	 * The number of structural features of the '<em>ITarmed Kumulation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION_FEATURE_COUNT = 9;


	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.MandantType <em>Mandant Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.MandantType
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getMandantType()
	 * @generated
	 */
	int MANDANT_TYPE = 4;

	/**
	 * The meta object id for the '<em>Limitation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedLimitation()
	 * @generated
	 */
	int TARMED_LIMITATION = 5;


	/**
	 * The meta object id for the '<em>Exclusion</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedExclusion()
	 * @generated
	 */
	int TARMED_EXCLUSION = 6;


	/**
	 * The meta object id for the '<em>Kumulation Art</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedKumulationArt()
	 * @generated
	 */
	int TARMED_KUMULATION_ART = 7;

	/**
	 * The meta object id for the '<em>Kumulation Typ</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedKumulationTyp()
	 * @generated
	 */
	int TARMED_KUMULATION_TYP = 8;

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung <em>ITarmed Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung
	 * @generated
	 */
	EClass getITarmedLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getAL <em>AL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>AL</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getAL()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_AL();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getTL <em>TL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TL</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getTL()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_TL();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuali <em>Digni Quali</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Digni Quali</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuali()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_DigniQuali();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuanti <em>Digni Quanti</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Digni Quanti</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuanti()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_DigniQuanti();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExclusion <em>Exclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusion</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExclusion()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_Exclusion();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExtension <em>Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExtension()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EReference getITarmedLeistung_Extension();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getParent()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EReference getITarmedLeistung_Parent();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getValidFrom()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getValidTo()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getServiceTyp <em>Service Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Service Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getServiceTyp()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_ServiceTyp();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getLaw <em>Law</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Law</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getLaw()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_Law();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getSparte <em>Sparte</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sparte</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getSparte()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_Sparte();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#isChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#isChapter()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getNickname <em>Nickname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nickname</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getNickname()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_Nickname();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension <em>ITarmed Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension
	 * @generated
	 */
	EClass getITarmedExtension();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getLimits <em>Limits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Limits</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getLimits()
	 * @see #getITarmedExtension()
	 * @generated
	 */
	EAttribute getITarmedExtension_Limits();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getMedInterpretation <em>Med Interpretation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Med Interpretation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getMedInterpretation()
	 * @see #getITarmedExtension()
	 * @generated
	 */
	EAttribute getITarmedExtension_MedInterpretation();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getTechInterpretation <em>Tech Interpretation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tech Interpretation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension#getTechInterpretation()
	 * @see #getITarmedExtension()
	 * @generated
	 */
	EAttribute getITarmedExtension_TechInterpretation();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup <em>ITarmed Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Group</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup
	 * @generated
	 */
	EClass getITarmedGroup();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getCode()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EAttribute getITarmedGroup_Code();

	/**
	 * Returns the meta object for the attribute list '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getServices <em>Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Services</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getServices()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EAttribute getITarmedGroup_Services();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getValidFrom()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EAttribute getITarmedGroup_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getValidTo()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EAttribute getITarmedGroup_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getLaw <em>Law</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Law</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getLaw()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EAttribute getITarmedGroup_Law();

	/**
	 * Returns the meta object for the attribute list '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getLimitations <em>Limitations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Limitations</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getLimitations()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EAttribute getITarmedGroup_Limitations();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getExtension <em>Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getExtension()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EReference getITarmedGroup_Extension();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation <em>ITarmed Kumulation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Kumulation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation
	 * @generated
	 */
	EClass getITarmedKumulation();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveCode <em>Slave Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Slave Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveCode()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_SlaveCode();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveArt <em>Slave Art</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Slave Art</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveArt()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_SlaveArt();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidSide <em>Valid Side</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid Side</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidSide()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_ValidSide();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidFrom()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidTo()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getLaw <em>Law</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Law</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getLaw()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_Law();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getMasterCode <em>Master Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Master Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getMasterCode()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_MasterCode();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getMasterArt <em>Master Art</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Master Art</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getMasterArt()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_MasterArt();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getTyp <em>Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getTyp()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_Typ();

	/**
	 * Returns the meta object for enum '{@link ch.elexis.base.ch.arzttarife.tarmed.MandantType <em>Mandant Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Mandant Type</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.MandantType
	 * @generated
	 */
	EEnum getMandantType();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation <em>Limitation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Limitation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation"
	 * @generated
	 */
	EDataType getTarmedLimitation();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion <em>Exclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Exclusion</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion"
	 * @generated
	 */
	EDataType getTarmedExclusion();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt <em>Kumulation Art</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Kumulation Art</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt"
	 * @generated
	 */
	EDataType getTarmedKumulationArt();

	/**
	 * Returns the meta object for data type '{@link ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp <em>Kumulation Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Kumulation Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp
	 * @model instanceClass="ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp"
	 * @generated
	 */
	EDataType getTarmedKumulationTyp();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TarmedFactory getTarmedFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung <em>ITarmed Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedLeistung()
		 * @generated
		 */
		EClass ITARMED_LEISTUNG = eINSTANCE.getITarmedLeistung();

		/**
		 * The meta object literal for the '<em><b>AL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__AL = eINSTANCE.getITarmedLeistung_AL();

		/**
		 * The meta object literal for the '<em><b>TL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__TL = eINSTANCE.getITarmedLeistung_TL();

		/**
		 * The meta object literal for the '<em><b>Digni Quali</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__DIGNI_QUALI = eINSTANCE.getITarmedLeistung_DigniQuali();

		/**
		 * The meta object literal for the '<em><b>Digni Quanti</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__DIGNI_QUANTI = eINSTANCE.getITarmedLeistung_DigniQuanti();

		/**
		 * The meta object literal for the '<em><b>Exclusion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__EXCLUSION = eINSTANCE.getITarmedLeistung_Exclusion();

		/**
		 * The meta object literal for the '<em><b>Extension</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARMED_LEISTUNG__EXTENSION = eINSTANCE.getITarmedLeistung_Extension();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARMED_LEISTUNG__PARENT = eINSTANCE.getITarmedLeistung_Parent();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__VALID_FROM = eINSTANCE.getITarmedLeistung_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__VALID_TO = eINSTANCE.getITarmedLeistung_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Service Typ</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__SERVICE_TYP = eINSTANCE.getITarmedLeistung_ServiceTyp();

		/**
		 * The meta object literal for the '<em><b>Law</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__LAW = eINSTANCE.getITarmedLeistung_Law();

		/**
		 * The meta object literal for the '<em><b>Sparte</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__SPARTE = eINSTANCE.getITarmedLeistung_Sparte();

		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__CHAPTER = eINSTANCE.getITarmedLeistung_Chapter();

		/**
		 * The meta object literal for the '<em><b>Nickname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__NICKNAME = eINSTANCE.getITarmedLeistung_Nickname();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension <em>ITarmed Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedExtension()
		 * @generated
		 */
		EClass ITARMED_EXTENSION = eINSTANCE.getITarmedExtension();

		/**
		 * The meta object literal for the '<em><b>Limits</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_EXTENSION__LIMITS = eINSTANCE.getITarmedExtension_Limits();

		/**
		 * The meta object literal for the '<em><b>Med Interpretation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_EXTENSION__MED_INTERPRETATION = eINSTANCE.getITarmedExtension_MedInterpretation();

		/**
		 * The meta object literal for the '<em><b>Tech Interpretation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_EXTENSION__TECH_INTERPRETATION = eINSTANCE.getITarmedExtension_TechInterpretation();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup <em>ITarmed Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedGroup()
		 * @generated
		 */
		EClass ITARMED_GROUP = eINSTANCE.getITarmedGroup();

		/**
		 * The meta object literal for the '<em><b>Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_GROUP__CODE = eINSTANCE.getITarmedGroup_Code();

		/**
		 * The meta object literal for the '<em><b>Services</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_GROUP__SERVICES = eINSTANCE.getITarmedGroup_Services();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_GROUP__VALID_FROM = eINSTANCE.getITarmedGroup_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_GROUP__VALID_TO = eINSTANCE.getITarmedGroup_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Law</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_GROUP__LAW = eINSTANCE.getITarmedGroup_Law();

		/**
		 * The meta object literal for the '<em><b>Limitations</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_GROUP__LIMITATIONS = eINSTANCE.getITarmedGroup_Limitations();

		/**
		 * The meta object literal for the '<em><b>Extension</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARMED_GROUP__EXTENSION = eINSTANCE.getITarmedGroup_Extension();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation <em>ITarmed Kumulation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedKumulation()
		 * @generated
		 */
		EClass ITARMED_KUMULATION = eINSTANCE.getITarmedKumulation();

		/**
		 * The meta object literal for the '<em><b>Slave Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__SLAVE_CODE = eINSTANCE.getITarmedKumulation_SlaveCode();

		/**
		 * The meta object literal for the '<em><b>Slave Art</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__SLAVE_ART = eINSTANCE.getITarmedKumulation_SlaveArt();

		/**
		 * The meta object literal for the '<em><b>Valid Side</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__VALID_SIDE = eINSTANCE.getITarmedKumulation_ValidSide();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__VALID_FROM = eINSTANCE.getITarmedKumulation_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__VALID_TO = eINSTANCE.getITarmedKumulation_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Law</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__LAW = eINSTANCE.getITarmedKumulation_Law();

		/**
		 * The meta object literal for the '<em><b>Master Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__MASTER_CODE = eINSTANCE.getITarmedKumulation_MasterCode();

		/**
		 * The meta object literal for the '<em><b>Master Art</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__MASTER_ART = eINSTANCE.getITarmedKumulation_MasterArt();

		/**
		 * The meta object literal for the '<em><b>Typ</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__TYP = eINSTANCE.getITarmedKumulation_Typ();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.MandantType <em>Mandant Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.MandantType
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getMandantType()
		 * @generated
		 */
		EEnum MANDANT_TYPE = eINSTANCE.getMandantType();

		/**
		 * The meta object literal for the '<em>Limitation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedLimitation()
		 * @generated
		 */
		EDataType TARMED_LIMITATION = eINSTANCE.getTarmedLimitation();

		/**
		 * The meta object literal for the '<em>Exclusion</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedExclusion()
		 * @generated
		 */
		EDataType TARMED_EXCLUSION = eINSTANCE.getTarmedExclusion();

		/**
		 * The meta object literal for the '<em>Kumulation Art</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedKumulationArt()
		 * @generated
		 */
		EDataType TARMED_KUMULATION_ART = eINSTANCE.getTarmedKumulationArt();

		/**
		 * The meta object literal for the '<em>Kumulation Typ</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getTarmedKumulationTyp()
		 * @generated
		 */
		EDataType TARMED_KUMULATION_TYP = eINSTANCE.getTarmedKumulationTyp();

	}

} //TarmedPackage
