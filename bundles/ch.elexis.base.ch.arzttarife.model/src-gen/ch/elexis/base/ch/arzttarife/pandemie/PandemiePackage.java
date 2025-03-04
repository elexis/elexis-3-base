/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.pandemie;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

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
 * @see ch.elexis.base.ch.arzttarife.pandemie.PandemieFactory
 * @model kind="package"
 * @generated
 */
public interface PandemiePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "pandemie";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/pandemie";

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
	PandemiePackage eINSTANCE = ch.elexis.base.ch.arzttarife.pandemie.impl.PandemiePackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung <em>IPandemie Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung
	 * @see ch.elexis.base.ch.arzttarife.pandemie.impl.PandemiePackageImpl#getIPandemieLeistung()
	 * @generated
	 */
	int IPANDEMIE_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__CODE = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__TEXT = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__LASTUPDATE = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__DELETED = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__DESCRIPTION = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__CHAPTER = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Cents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__CENTS = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__VALID_FROM = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__VALID_TO = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Taxpoints</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG__TAXPOINTS = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 9;

	/**
	 * The number of structural features of the '<em>IPandemie Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPANDEMIE_LEISTUNG_FEATURE_COUNT = ModelPackage.WITH_ASSIGNABLE_ID_FEATURE_COUNT + 10;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung <em>IPandemie Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IPandemie Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung
	 * @generated
	 */
	EClass getIPandemieLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getDescription()
	 * @see #getIPandemieLeistung()
	 * @generated
	 */
	EAttribute getIPandemieLeistung_Description();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getChapter()
	 * @see #getIPandemieLeistung()
	 * @generated
	 */
	EAttribute getIPandemieLeistung_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getCents <em>Cents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cents</em>'.
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getCents()
	 * @see #getIPandemieLeistung()
	 * @generated
	 */
	EAttribute getIPandemieLeistung_Cents();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidFrom()
	 * @see #getIPandemieLeistung()
	 * @generated
	 */
	EAttribute getIPandemieLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getValidTo()
	 * @see #getIPandemieLeistung()
	 * @generated
	 */
	EAttribute getIPandemieLeistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getTaxpoints <em>Taxpoints</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Taxpoints</em>'.
	 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung#getTaxpoints()
	 * @see #getIPandemieLeistung()
	 * @generated
	 */
	EAttribute getIPandemieLeistung_Taxpoints();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PandemieFactory getPandemieFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung <em>IPandemie Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung
		 * @see ch.elexis.base.ch.arzttarife.pandemie.impl.PandemiePackageImpl#getIPandemieLeistung()
		 * @generated
		 */
		EClass IPANDEMIE_LEISTUNG = eINSTANCE.getIPandemieLeistung();
		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPANDEMIE_LEISTUNG__DESCRIPTION = eINSTANCE.getIPandemieLeistung_Description();
		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPANDEMIE_LEISTUNG__CHAPTER = eINSTANCE.getIPandemieLeistung_Chapter();
		/**
		 * The meta object literal for the '<em><b>Cents</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPANDEMIE_LEISTUNG__CENTS = eINSTANCE.getIPandemieLeistung_Cents();
		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPANDEMIE_LEISTUNG__VALID_FROM = eINSTANCE.getIPandemieLeistung_ValidFrom();
		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPANDEMIE_LEISTUNG__VALID_TO = eINSTANCE.getIPandemieLeistung_ValidTo();
		/**
		 * The meta object literal for the '<em><b>Taxpoints</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPANDEMIE_LEISTUNG__TAXPOINTS = eINSTANCE.getIPandemieLeistung_Taxpoints();

	}

} //PandemiePackage
