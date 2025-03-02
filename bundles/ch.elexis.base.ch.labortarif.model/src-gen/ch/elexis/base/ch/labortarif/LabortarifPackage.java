/**
 */
package ch.elexis.base.ch.labortarif;

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
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see ch.elexis.base.ch.labortarif.LabortarifFactory
 * @model kind="package"
 * @generated
 */
public interface LabortarifPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "labortarif";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/labortarif";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.base.ch.labortarif.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LabortarifPackage eINSTANCE = ch.elexis.base.ch.labortarif.impl.LabortarifPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.labortarif.ILaborLeistung <em>ILabor Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung
	 * @see ch.elexis.base.ch.labortarif.impl.LabortarifPackageImpl#getILaborLeistung()
	 * @generated
	 */
	int ILABOR_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Points</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__POINTS = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__CHAPTER = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Speciality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__SPECIALITY = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Limitation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG__LIMITATION = ModelPackage.IBILLABLE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>ILabor Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ILABOR_LEISTUNG_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 6;

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.labortarif.ILaborLeistung <em>ILabor Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ILabor Leistung</em>'.
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung
	 * @generated
	 */
	EClass getILaborLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getPoints <em>Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Points</em>'.
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung#getPoints()
	 * @see #getILaborLeistung()
	 * @generated
	 */
	EAttribute getILaborLeistung_Points();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung#getValidFrom()
	 * @see #getILaborLeistung()
	 * @generated
	 */
	EAttribute getILaborLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung#getValidTo()
	 * @see #getILaborLeistung()
	 * @generated
	 */
	EAttribute getILaborLeistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getChapter <em>Chapter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Chapter</em>'.
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung#getChapter()
	 * @see #getILaborLeistung()
	 * @generated
	 */
	EAttribute getILaborLeistung_Chapter();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getSpeciality <em>Speciality</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Speciality</em>'.
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung#getSpeciality()
	 * @see #getILaborLeistung()
	 * @generated
	 */
	EAttribute getILaborLeistung_Speciality();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getLimitation <em>Limitation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Limitation</em>'.
	 * @see ch.elexis.base.ch.labortarif.ILaborLeistung#getLimitation()
	 * @see #getILaborLeistung()
	 * @generated
	 */
	EAttribute getILaborLeistung_Limitation();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	LabortarifFactory getLabortarifFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.labortarif.ILaborLeistung <em>ILabor Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.labortarif.ILaborLeistung
		 * @see ch.elexis.base.ch.labortarif.impl.LabortarifPackageImpl#getILaborLeistung()
		 * @generated
		 */
		EClass ILABOR_LEISTUNG = eINSTANCE.getILaborLeistung();
		/**
		 * The meta object literal for the '<em><b>Points</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ILABOR_LEISTUNG__POINTS = eINSTANCE.getILaborLeistung_Points();
		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ILABOR_LEISTUNG__VALID_FROM = eINSTANCE.getILaborLeistung_ValidFrom();
		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ILABOR_LEISTUNG__VALID_TO = eINSTANCE.getILaborLeistung_ValidTo();
		/**
		 * The meta object literal for the '<em><b>Chapter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ILABOR_LEISTUNG__CHAPTER = eINSTANCE.getILaborLeistung_Chapter();
		/**
		 * The meta object literal for the '<em><b>Speciality</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ILABOR_LEISTUNG__SPECIALITY = eINSTANCE.getILaborLeistung_Speciality();
		/**
		 * The meta object literal for the '<em><b>Limitation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ILABOR_LEISTUNG__LIMITATION = eINSTANCE.getILaborLeistung_Limitation();

	}

} //LabortarifPackage
