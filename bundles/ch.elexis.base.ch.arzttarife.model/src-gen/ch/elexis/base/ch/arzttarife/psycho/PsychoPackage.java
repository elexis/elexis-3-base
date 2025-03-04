/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.psycho;

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
 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoFactory
 * @model kind="package"
 * @generated
 */
public interface PsychoPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "psycho";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/psycho";

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
	PsychoPackage eINSTANCE = ch.elexis.base.ch.arzttarife.psycho.impl.PsychoPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung <em>IPsycho Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung
	 * @see ch.elexis.base.ch.arzttarife.psycho.impl.PsychoPackageImpl#getIPsychoLeistung()
	 * @generated
	 */
	int IPSYCHO_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__LASTUPDATE = ModelPackage.IBILLABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__TP = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__DESCRIPTION = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Limitations</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__LIMITATIONS = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Exclusions</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG__EXCLUSIONS = ModelPackage.IBILLABLE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>IPsycho Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPSYCHO_LEISTUNG_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 6;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung <em>IPsycho Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IPsycho Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung
	 * @generated
	 */
	EClass getIPsychoLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidFrom()
	 * @see #getIPsychoLeistung()
	 * @generated
	 */
	EAttribute getIPsychoLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getValidTo()
	 * @see #getIPsychoLeistung()
	 * @generated
	 */
	EAttribute getIPsychoLeistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getTP <em>TP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TP</em>'.
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getTP()
	 * @see #getIPsychoLeistung()
	 * @generated
	 */
	EAttribute getIPsychoLeistung_TP();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getDescription()
	 * @see #getIPsychoLeistung()
	 * @generated
	 */
	EAttribute getIPsychoLeistung_Description();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getLimitations <em>Limitations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Limitations</em>'.
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getLimitations()
	 * @see #getIPsychoLeistung()
	 * @generated
	 */
	EAttribute getIPsychoLeistung_Limitations();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getExclusions <em>Exclusions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusions</em>'.
	 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung#getExclusions()
	 * @see #getIPsychoLeistung()
	 * @generated
	 */
	EAttribute getIPsychoLeistung_Exclusions();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PsychoFactory getPsychoFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung <em>IPsycho Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung
		 * @see ch.elexis.base.ch.arzttarife.psycho.impl.PsychoPackageImpl#getIPsychoLeistung()
		 * @generated
		 */
		EClass IPSYCHO_LEISTUNG = eINSTANCE.getIPsychoLeistung();

		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPSYCHO_LEISTUNG__VALID_FROM = eINSTANCE.getIPsychoLeistung_ValidFrom();

		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPSYCHO_LEISTUNG__VALID_TO = eINSTANCE.getIPsychoLeistung_ValidTo();

		/**
		 * The meta object literal for the '<em><b>TP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPSYCHO_LEISTUNG__TP = eINSTANCE.getIPsychoLeistung_TP();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPSYCHO_LEISTUNG__DESCRIPTION = eINSTANCE.getIPsychoLeistung_Description();

		/**
		 * The meta object literal for the '<em><b>Limitations</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPSYCHO_LEISTUNG__LIMITATIONS = eINSTANCE.getIPsychoLeistung_Limitations();

		/**
		 * The meta object literal for the '<em><b>Exclusions</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPSYCHO_LEISTUNG__EXCLUSIONS = eINSTANCE.getIPsychoLeistung_Exclusions();

	}

} //PsychoPackage
