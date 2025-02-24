/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmedallowance;

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
 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.TarmedallowanceFactory
 * @model kind="package"
 * @generated
 */
public interface TarmedallowancePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "tarmedallowance";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/tarmedallowance";

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
	TarmedallowancePackage eINSTANCE = ch.elexis.base.ch.arzttarife.tarmedallowance.impl.TarmedallowancePackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance <em>ITarmed Allowance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.impl.TarmedallowancePackageImpl#getITarmedAllowance()
	 * @generated
	 */
	int ITARMED_ALLOWANCE = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE__LASTUPDATE = ModelPackage.IBILLABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE__CHAPTER = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE__TP = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>ITarmed Allowance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_ALLOWANCE_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance <em>ITarmed Allowance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Allowance</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance
	 * @generated
	 */
	EClass getITarmedAllowance();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getValidFrom()
	 * @see #getITarmedAllowance()
	 * @generated
	 */
	EAttribute getITarmedAllowance_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getValidTo()
	 * @see #getITarmedAllowance()
	 * @generated
	 */
	EAttribute getITarmedAllowance_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getChapter()
	 * @see #getITarmedAllowance()
	 * @generated
	 */
	EAttribute getITarmedAllowance_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getTP <em>TP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TP</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance#getTP()
	 * @see #getITarmedAllowance()
	 * @generated
	 */
	EAttribute getITarmedAllowance_TP();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TarmedallowanceFactory getTarmedallowanceFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance <em>ITarmed Allowance</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance
		 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.impl.TarmedallowancePackageImpl#getITarmedAllowance()
		 * @generated
		 */
		EClass ITARMED_ALLOWANCE = eINSTANCE.getITarmedAllowance();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_ALLOWANCE__VALID_FROM = eINSTANCE.getITarmedAllowance_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_ALLOWANCE__VALID_TO = eINSTANCE.getITarmedAllowance_ValidTo();

		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_ALLOWANCE__CHAPTER = eINSTANCE.getITarmedAllowance_Chapter();

		/**
		 * The meta object literal for the '<em><b>TP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_ALLOWANCE__TP = eINSTANCE.getITarmedAllowance_TP();

	}

} //TarmedallowancePackage
