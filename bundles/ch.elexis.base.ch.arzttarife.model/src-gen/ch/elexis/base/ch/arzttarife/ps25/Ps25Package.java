/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.ps25;

import ch.elexis.core.model.ModelPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

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
 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Factory
 * @model kind="package"
 * @generated
 */
public interface Ps25Package extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ps25";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/ps25";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.arzttarife.ch.ps25.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Ps25Package eINSTANCE = ch.elexis.base.ch.arzttarife.ps25.impl.Ps25PackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung <em>IPs25 Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung
	 * @see ch.elexis.base.ch.arzttarife.ps25.impl.Ps25PackageImpl#getIPs25Leistung()
	 * @generated
	 */
	int IPS25_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__CODE = ModelPackage.ISERVICE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__TEXT = ModelPackage.ISERVICE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__LASTUPDATE = ModelPackage.ISERVICE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__DELETED = ModelPackage.ISERVICE__DELETED;

	/**
	 * The feature id for the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__PRICE = ModelPackage.ISERVICE__PRICE;

	/**
	 * The feature id for the '<em><b>Net Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__NET_PRICE = ModelPackage.ISERVICE__NET_PRICE;

	/**
	 * The feature id for the '<em><b>Minutes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__MINUTES = ModelPackage.ISERVICE__MINUTES;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__VALID_FROM = ModelPackage.ISERVICE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__VALID_TO = ModelPackage.ISERVICE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__TP = ModelPackage.ISERVICE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Sub Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__SUB_CHAPTER = ModelPackage.ISERVICE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__CHAPTER = ModelPackage.ISERVICE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Honorar Empfaenger</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__HONORAR_EMPFAENGER = ModelPackage.ISERVICE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Mehrleistung Bei</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__MEHRLEISTUNG_BEI = ModelPackage.ISERVICE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Spezifikation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__SPEZIFIKATION = ModelPackage.ISERVICE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Anwendungs Regeln</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__ANWENDUNGS_REGELN = ModelPackage.ISERVICE_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Stufe</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__STUFE = ModelPackage.ISERVICE_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Moegliche Kombination</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__MOEGLICHE_KOMBINATION = ModelPackage.ISERVICE_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Mehrleistungs Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__MEHRLEISTUNGS_TYP = ModelPackage.ISERVICE_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Mehrleistung</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG__MEHRLEISTUNG = ModelPackage.ISERVICE_FEATURE_COUNT + 12;

	/**
	 * The number of structural features of the '<em>IPs25 Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPS25_LEISTUNG_FEATURE_COUNT = ModelPackage.ISERVICE_FEATURE_COUNT + 13;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung <em>IPs25 Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IPs25 Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung
	 * @generated
	 */
	EClass getIPs25Leistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getValidFrom()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getValidTo()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getTP <em>TP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TP</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getTP()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_TP();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSubChapter <em>Sub Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sub Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSubChapter()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_SubChapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getChapter()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getHonorarEmpfaenger <em>Honorar Empfaenger</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Honorar Empfaenger</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getHonorarEmpfaenger()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_HonorarEmpfaenger();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungBei <em>Mehrleistung Bei</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mehrleistung Bei</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungBei()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_MehrleistungBei();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSpezifikation <em>Spezifikation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Spezifikation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSpezifikation()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_Spezifikation();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getAnwendungsRegeln <em>Anwendungs Regeln</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Anwendungs Regeln</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getAnwendungsRegeln()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_AnwendungsRegeln();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getStufe <em>Stufe</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Stufe</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getStufe()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_Stufe();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMoeglicheKombination <em>Moegliche Kombination</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Moegliche Kombination</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMoeglicheKombination()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_MoeglicheKombination();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungsTyp <em>Mehrleistungs Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mehrleistungs Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungsTyp()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_MehrleistungsTyp();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistung <em>Mehrleistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mehrleistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistung()
	 * @see #getIPs25Leistung()
	 * @generated
	 */
	EAttribute getIPs25Leistung_Mehrleistung();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	Ps25Factory getPs25Factory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung <em>IPs25 Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung
		 * @see ch.elexis.base.ch.arzttarife.ps25.impl.Ps25PackageImpl#getIPs25Leistung()
		 * @generated
		 */
		EClass IPS25_LEISTUNG = eINSTANCE.getIPs25Leistung();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__VALID_FROM = eINSTANCE.getIPs25Leistung_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__VALID_TO = eINSTANCE.getIPs25Leistung_ValidTo();

		/**
		 * The meta object literal for the '<em><b>TP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__TP = eINSTANCE.getIPs25Leistung_TP();

		/**
		 * The meta object literal for the '<em><b>Sub Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__SUB_CHAPTER = eINSTANCE.getIPs25Leistung_SubChapter();

		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__CHAPTER = eINSTANCE.getIPs25Leistung_Chapter();

		/**
		 * The meta object literal for the '<em><b>Honorar Empfaenger</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__HONORAR_EMPFAENGER = eINSTANCE.getIPs25Leistung_HonorarEmpfaenger();

		/**
		 * The meta object literal for the '<em><b>Mehrleistung Bei</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__MEHRLEISTUNG_BEI = eINSTANCE.getIPs25Leistung_MehrleistungBei();

		/**
		 * The meta object literal for the '<em><b>Spezifikation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__SPEZIFIKATION = eINSTANCE.getIPs25Leistung_Spezifikation();

		/**
		 * The meta object literal for the '<em><b>Anwendungs Regeln</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__ANWENDUNGS_REGELN = eINSTANCE.getIPs25Leistung_AnwendungsRegeln();

		/**
		 * The meta object literal for the '<em><b>Stufe</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__STUFE = eINSTANCE.getIPs25Leistung_Stufe();

		/**
		 * The meta object literal for the '<em><b>Moegliche Kombination</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__MOEGLICHE_KOMBINATION = eINSTANCE.getIPs25Leistung_MoeglicheKombination();

		/**
		 * The meta object literal for the '<em><b>Mehrleistungs Typ</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__MEHRLEISTUNGS_TYP = eINSTANCE.getIPs25Leistung_MehrleistungsTyp();

		/**
		 * The meta object literal for the '<em><b>Mehrleistung</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPS25_LEISTUNG__MEHRLEISTUNG = eINSTANCE.getIPs25Leistung_Mehrleistung();

	}

} //Ps25Package
