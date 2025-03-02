/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.occupational;

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
 * @see ch.elexis.base.ch.arzttarife.occupational.OccupationalFactory
 * @model kind="package"
 * @generated
 */
public interface OccupationalPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "occupational";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/occupational";

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
	OccupationalPackage eINSTANCE = ch.elexis.base.ch.arzttarife.occupational.impl.OccupationalPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung <em>IOccupational Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung
	 * @see ch.elexis.base.ch.arzttarife.occupational.impl.OccupationalPackageImpl#getIOccupationalLeistung()
	 * @generated
	 */
	int IOCCUPATIONAL_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG__LASTUPDATE = ModelPackage.IBILLABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG__TP = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG__DESCRIPTION = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>IOccupational Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IOCCUPATIONAL_LEISTUNG_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung <em>IOccupational Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IOccupational Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung
	 * @generated
	 */
	EClass getIOccupationalLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getValidFrom()
	 * @see #getIOccupationalLeistung()
	 * @generated
	 */
	EAttribute getIOccupationalLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getValidTo()
	 * @see #getIOccupationalLeistung()
	 * @generated
	 */
	EAttribute getIOccupationalLeistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getTP <em>TP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TP</em>'.
	 * @see ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getTP()
	 * @see #getIOccupationalLeistung()
	 * @generated
	 */
	EAttribute getIOccupationalLeistung_TP();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung#getDescription()
	 * @see #getIOccupationalLeistung()
	 * @generated
	 */
	EAttribute getIOccupationalLeistung_Description();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	OccupationalFactory getOccupationalFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung <em>IOccupational Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung
		 * @see ch.elexis.base.ch.arzttarife.occupational.impl.OccupationalPackageImpl#getIOccupationalLeistung()
		 * @generated
		 */
		EClass IOCCUPATIONAL_LEISTUNG = eINSTANCE.getIOccupationalLeistung();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOCCUPATIONAL_LEISTUNG__VALID_FROM = eINSTANCE.getIOccupationalLeistung_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOCCUPATIONAL_LEISTUNG__VALID_TO = eINSTANCE.getIOccupationalLeistung_ValidTo();

		/**
		 * The meta object literal for the '<em><b>TP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOCCUPATIONAL_LEISTUNG__TP = eINSTANCE.getIOccupationalLeistung_TP();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IOCCUPATIONAL_LEISTUNG__DESCRIPTION = eINSTANCE.getIOccupationalLeistung_Description();

	}

} //OccupationalPackage
