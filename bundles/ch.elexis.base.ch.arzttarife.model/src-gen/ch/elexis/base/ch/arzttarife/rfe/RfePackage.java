/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.rfe;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see ch.elexis.base.ch.arzttarife.rfe.RfeFactory
 * @model kind="package"
 * @generated
 */
public interface RfePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "rfe";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/rfe";

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
	RfePackage eINSTANCE = ch.elexis.base.ch.arzttarife.rfe.impl.RfePackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter <em>IReason For Encounter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter
	 * @see ch.elexis.base.ch.arzttarife.rfe.impl.RfePackageImpl#getIReasonForEncounter()
	 * @generated
	 */
	int IREASON_FOR_ENCOUNTER = 0;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IREASON_FOR_ENCOUNTER__DELETED = ModelPackage.DELETEABLE__DELETED;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IREASON_FOR_ENCOUNTER__LASTUPDATE = ModelPackage.DELETEABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Encounter</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IREASON_FOR_ENCOUNTER__ENCOUNTER = ModelPackage.DELETEABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IREASON_FOR_ENCOUNTER__CODE = ModelPackage.DELETEABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IREASON_FOR_ENCOUNTER__TEXT = ModelPackage.DELETEABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>IReason For Encounter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IREASON_FOR_ENCOUNTER_FEATURE_COUNT = ModelPackage.DELETEABLE_FEATURE_COUNT + 4;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter <em>IReason For Encounter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IReason For Encounter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter
	 * @generated
	 */
	EClass getIReasonForEncounter();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getEncounter <em>Encounter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Encounter</em>'.
	 * @see ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getEncounter()
	 * @see #getIReasonForEncounter()
	 * @generated
	 */
	EReference getIReasonForEncounter_Encounter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getCode()
	 * @see #getIReasonForEncounter()
	 * @generated
	 */
	EAttribute getIReasonForEncounter_Code();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter#getText()
	 * @see #getIReasonForEncounter()
	 * @generated
	 */
	EAttribute getIReasonForEncounter_Text();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RfeFactory getRfeFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter <em>IReason For Encounter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter
		 * @see ch.elexis.base.ch.arzttarife.rfe.impl.RfePackageImpl#getIReasonForEncounter()
		 * @generated
		 */
		EClass IREASON_FOR_ENCOUNTER = eINSTANCE.getIReasonForEncounter();

		/**
		 * The meta object literal for the '<em><b>Encounter</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IREASON_FOR_ENCOUNTER__ENCOUNTER = eINSTANCE.getIReasonForEncounter_Encounter();

		/**
		 * The meta object literal for the '<em><b>Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IREASON_FOR_ENCOUNTER__CODE = eINSTANCE.getIReasonForEncounter_Code();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IREASON_FOR_ENCOUNTER__TEXT = eINSTANCE.getIReasonForEncounter_Text();

	}

} //RfePackage
