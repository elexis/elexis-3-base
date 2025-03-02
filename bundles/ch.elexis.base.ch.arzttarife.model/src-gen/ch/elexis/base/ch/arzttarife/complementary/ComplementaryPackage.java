/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.complementary;

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
 * @see ch.elexis.base.ch.arzttarife.complementary.ComplementaryFactory
 * @model kind="package"
 * @generated
 */
public interface ComplementaryPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "complementary";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/complementary";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.arzttarife.ch.complementary.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ComplementaryPackage eINSTANCE = ch.elexis.base.ch.arzttarife.complementary.impl.ComplementaryPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung <em>IComplementary Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung
	 * @see ch.elexis.base.ch.arzttarife.complementary.impl.ComplementaryPackageImpl#getIComplementaryLeistung()
	 * @generated
	 */
	int ICOMPLEMENTARY_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__LASTUPDATE = ModelPackage.IBILLABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__DELETED = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__DESCRIPTION = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__CHAPTER = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__FIXED_VALUE = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Fixed Value Set</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__FIXED_VALUE_SET = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>IComplementary Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ICOMPLEMENTARY_LEISTUNG_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 7;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung <em>IComplementary Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IComplementary Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung
	 * @generated
	 */
	EClass getIComplementaryLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getDescription()
	 * @see #getIComplementaryLeistung()
	 * @generated
	 */
	EAttribute getIComplementaryLeistung_Description();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getChapter()
	 * @see #getIComplementaryLeistung()
	 * @generated
	 */
	EAttribute getIComplementaryLeistung_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getFixedValue <em>Fixed Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fixed Value</em>'.
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getFixedValue()
	 * @see #getIComplementaryLeistung()
	 * @generated
	 */
	EAttribute getIComplementaryLeistung_FixedValue();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#isFixedValueSet <em>Fixed Value Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fixed Value Set</em>'.
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#isFixedValueSet()
	 * @see #getIComplementaryLeistung()
	 * @generated
	 */
	EAttribute getIComplementaryLeistung_FixedValueSet();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidFrom()
	 * @see #getIComplementaryLeistung()
	 * @generated
	 */
	EAttribute getIComplementaryLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung#getValidTo()
	 * @see #getIComplementaryLeistung()
	 * @generated
	 */
	EAttribute getIComplementaryLeistung_ValidTo();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ComplementaryFactory getComplementaryFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung <em>IComplementary Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung
		 * @see ch.elexis.base.ch.arzttarife.complementary.impl.ComplementaryPackageImpl#getIComplementaryLeistung()
		 * @generated
		 */
		EClass ICOMPLEMENTARY_LEISTUNG = eINSTANCE.getIComplementaryLeistung();
		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICOMPLEMENTARY_LEISTUNG__DESCRIPTION = eINSTANCE.getIComplementaryLeistung_Description();
		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICOMPLEMENTARY_LEISTUNG__CHAPTER = eINSTANCE.getIComplementaryLeistung_Chapter();
		/**
		 * The meta object literal for the '<em><b>Fixed Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICOMPLEMENTARY_LEISTUNG__FIXED_VALUE = eINSTANCE.getIComplementaryLeistung_FixedValue();
		/**
		 * The meta object literal for the '<em><b>Fixed Value Set</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICOMPLEMENTARY_LEISTUNG__FIXED_VALUE_SET = eINSTANCE.getIComplementaryLeistung_FixedValueSet();
		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICOMPLEMENTARY_LEISTUNG__VALID_FROM = eINSTANCE.getIComplementaryLeistung_ValidFrom();
		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ICOMPLEMENTARY_LEISTUNG__VALID_TO = eINSTANCE.getIComplementaryLeistung_ValidTo();

	}

} //ComplementaryPackage
