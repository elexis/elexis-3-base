/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.os25;

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
 * @see ch.elexis.base.ch.arzttarife.os25.Os25Factory
 * @model kind="package"
 * @generated
 */
public interface Os25Package extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "os25";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/os25";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.arzttarife.ch.os25.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Os25Package eINSTANCE = ch.elexis.base.ch.arzttarife.os25.impl.Os25PackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung <em>IOs25 Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung
	 * @see ch.elexis.base.ch.arzttarife.os25.impl.Os25PackageImpl#getIOs25Leistung()
	 * @generated
	 */
	int IOS25_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__LASTUPDATE = ModelPackage.IBILLABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__TP = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Sub Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__SUB_CHAPTER = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__CHAPTER = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Honorar Empfaenger</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__HONORAR_EMPFAENGER = ModelPackage.IBILLABLE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Leistung</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__LEISTUNG = ModelPackage.IBILLABLE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Spezifikation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__SPEZIFIKATION = ModelPackage.IBILLABLE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Anwendungs Regeln</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__ANWENDUNGS_REGELN = ModelPackage.IBILLABLE_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Stufe</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__STUFE = ModelPackage.IBILLABLE_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Moegliche Kombination</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__MOEGLICHE_KOMBINATION = ModelPackage.IBILLABLE_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Leistungs Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG__LEISTUNGS_TYP = ModelPackage.IBILLABLE_FEATURE_COUNT + 11;

	/**
	 * The number of structural features of the '<em>IOs25 Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOS25_LEISTUNG_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 12;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung <em>IOs25 Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IOs25 Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung
	 * @generated
	 */
	EClass getIOs25Leistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getValidFrom()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getValidTo()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getTP <em>TP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TP</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getTP()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_TP();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSubChapter <em>Sub Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sub Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSubChapter()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_SubChapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getChapter()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getHonorarEmpfaenger <em>Honorar Empfaenger</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Honorar Empfaenger</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getHonorarEmpfaenger()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_HonorarEmpfaenger();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistung <em>Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistung()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_Leistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSpezifikation <em>Spezifikation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Spezifikation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSpezifikation()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_Spezifikation();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getAnwendungsRegeln <em>Anwendungs Regeln</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Anwendungs Regeln</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getAnwendungsRegeln()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_AnwendungsRegeln();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getStufe <em>Stufe</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Stufe</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getStufe()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_Stufe();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getMoeglicheKombination <em>Moegliche Kombination</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Moegliche Kombination</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getMoeglicheKombination()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_MoeglicheKombination();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistungsTyp <em>Leistungs Typ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Leistungs Typ</em>'.
	 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistungsTyp()
	 * @see #getIOs25Leistung()
	 * @generated
	 */
	EAttribute getIOs25Leistung_LeistungsTyp();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	Os25Factory getOs25Factory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung <em>IOs25 Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.os25.IOs25Leistung
		 * @see ch.elexis.base.ch.arzttarife.os25.impl.Os25PackageImpl#getIOs25Leistung()
		 * @generated
		 */
		EClass IOS25_LEISTUNG = eINSTANCE.getIOs25Leistung();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__VALID_FROM = eINSTANCE.getIOs25Leistung_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__VALID_TO = eINSTANCE.getIOs25Leistung_ValidTo();

		/**
		 * The meta object literal for the '<em><b>TP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__TP = eINSTANCE.getIOs25Leistung_TP();

		/**
		 * The meta object literal for the '<em><b>Sub Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__SUB_CHAPTER = eINSTANCE.getIOs25Leistung_SubChapter();

		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__CHAPTER = eINSTANCE.getIOs25Leistung_Chapter();

		/**
		 * The meta object literal for the '<em><b>Honorar Empfaenger</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__HONORAR_EMPFAENGER = eINSTANCE.getIOs25Leistung_HonorarEmpfaenger();

		/**
		 * The meta object literal for the '<em><b>Leistung</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__LEISTUNG = eINSTANCE.getIOs25Leistung_Leistung();

		/**
		 * The meta object literal for the '<em><b>Spezifikation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__SPEZIFIKATION = eINSTANCE.getIOs25Leistung_Spezifikation();

		/**
		 * The meta object literal for the '<em><b>Anwendungs Regeln</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__ANWENDUNGS_REGELN = eINSTANCE.getIOs25Leistung_AnwendungsRegeln();

		/**
		 * The meta object literal for the '<em><b>Stufe</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__STUFE = eINSTANCE.getIOs25Leistung_Stufe();

		/**
		 * The meta object literal for the '<em><b>Moegliche Kombination</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__MOEGLICHE_KOMBINATION = eINSTANCE.getIOs25Leistung_MoeglicheKombination();

		/**
		 * The meta object literal for the '<em><b>Leistungs Typ</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOS25_LEISTUNG__LEISTUNGS_TYP = eINSTANCE.getIOs25Leistung_LeistungsTyp();

	}

} //Os25Package
