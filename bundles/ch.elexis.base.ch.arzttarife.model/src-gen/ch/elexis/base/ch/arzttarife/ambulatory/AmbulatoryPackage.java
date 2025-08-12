/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.ambulatory;

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
 * @see ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryFactory
 * @model kind="package"
 * @generated
 */
public interface AmbulatoryPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ambulatory";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/amulatory";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.arzttarife.ch.ambulatory.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AmbulatoryPackage eINSTANCE = ch.elexis.base.ch.arzttarife.ambulatory.impl.AmbulatoryPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance <em>IAmbulatory Allowance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.impl.AmbulatoryPackageImpl#getIAmbulatoryAllowance()
	 * @generated
	 */
	int IAMBULATORY_ALLOWANCE = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__LASTUPDATE = ModelPackage.IBILLABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__CHAPTER = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__TP = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE__DIGNI_QUALI = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>IAmbulatory Allowance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IAMBULATORY_ALLOWANCE_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 5;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance <em>IAmbulatory Allowance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IAmbulatory Allowance</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance
	 * @generated
	 */
	EClass getIAmbulatoryAllowance();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidFrom()
	 * @see #getIAmbulatoryAllowance()
	 * @generated
	 */
	EAttribute getIAmbulatoryAllowance_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getValidTo()
	 * @see #getIAmbulatoryAllowance()
	 * @generated
	 */
	EAttribute getIAmbulatoryAllowance_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getChapter()
	 * @see #getIAmbulatoryAllowance()
	 * @generated
	 */
	EAttribute getIAmbulatoryAllowance_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getTP <em>TP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TP</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getTP()
	 * @see #getIAmbulatoryAllowance()
	 * @generated
	 */
	EAttribute getIAmbulatoryAllowance_TP();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getDigniQuali <em>Digni Quali</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Digni Quali</em>'.
	 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance#getDigniQuali()
	 * @see #getIAmbulatoryAllowance()
	 * @generated
	 */
	EAttribute getIAmbulatoryAllowance_DigniQuali();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AmbulatoryFactory getAmbulatoryFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance <em>IAmbulatory Allowance</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance
		 * @see ch.elexis.base.ch.arzttarife.ambulatory.impl.AmbulatoryPackageImpl#getIAmbulatoryAllowance()
		 * @generated
		 */
		EClass IAMBULATORY_ALLOWANCE = eINSTANCE.getIAmbulatoryAllowance();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IAMBULATORY_ALLOWANCE__VALID_FROM = eINSTANCE.getIAmbulatoryAllowance_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IAMBULATORY_ALLOWANCE__VALID_TO = eINSTANCE.getIAmbulatoryAllowance_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IAMBULATORY_ALLOWANCE__CHAPTER = eINSTANCE.getIAmbulatoryAllowance_Chapter();

		/**
		 * The meta object literal for the '<em><b>TP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IAMBULATORY_ALLOWANCE__TP = eINSTANCE.getIAmbulatoryAllowance_TP();

		/**
		 * The meta object literal for the '<em><b>Digni Quali</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IAMBULATORY_ALLOWANCE__DIGNI_QUALI = eINSTANCE.getIAmbulatoryAllowance_DigniQuali();

	}

} //AmbulatoryPackage
