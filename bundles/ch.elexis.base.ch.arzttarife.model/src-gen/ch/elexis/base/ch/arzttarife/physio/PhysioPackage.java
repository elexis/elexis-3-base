/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.physio;

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
 * @see ch.elexis.base.ch.arzttarife.physio.PhysioFactory
 * @model kind="package"
 * @generated
 */
public interface PhysioPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "physio";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/physio";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ch.elexis.arzttarife.ch.physio.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PhysioPackage eINSTANCE = ch.elexis.base.ch.arzttarife.physio.impl.PhysioPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung <em>IPhysio Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung
	 * @see ch.elexis.base.ch.arzttarife.physio.impl.PhysioPackageImpl#getIPhysioLeistung()
	 * @generated
	 */
	int IPHYSIO_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Lastupdate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__LASTUPDATE = ModelPackage.IBILLABLE__LASTUPDATE;

	/**
	 * The feature id for the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__VALID_FROM = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__VALID_TO = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__TP = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Ziffer</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__ZIFFER = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__DESCRIPTION = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Law</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG__LAW = ModelPackage.IBILLABLE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>IPhysio Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IPHYSIO_LEISTUNG_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 6;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung <em>IPhysio Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IPhysio Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung
	 * @generated
	 */
	EClass getIPhysioLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidFrom <em>Valid From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid From</em>'.
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidFrom()
	 * @see #getIPhysioLeistung()
	 * @generated
	 */
	EAttribute getIPhysioLeistung_ValidFrom();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidTo <em>Valid To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid To</em>'.
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getValidTo()
	 * @see #getIPhysioLeistung()
	 * @generated
	 */
	EAttribute getIPhysioLeistung_ValidTo();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getTP <em>TP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TP</em>'.
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getTP()
	 * @see #getIPhysioLeistung()
	 * @generated
	 */
	EAttribute getIPhysioLeistung_TP();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getZiffer <em>Ziffer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ziffer</em>'.
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getZiffer()
	 * @see #getIPhysioLeistung()
	 * @generated
	 */
	EAttribute getIPhysioLeistung_Ziffer();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getDescription()
	 * @see #getIPhysioLeistung()
	 * @generated
	 */
	EAttribute getIPhysioLeistung_Description();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getLaw <em>Law</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Law</em>'.
	 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung#getLaw()
	 * @see #getIPhysioLeistung()
	 * @generated
	 */
	EAttribute getIPhysioLeistung_Law();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PhysioFactory getPhysioFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung <em>IPhysio Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung
		 * @see ch.elexis.base.ch.arzttarife.physio.impl.PhysioPackageImpl#getIPhysioLeistung()
		 * @generated
		 */
		EClass IPHYSIO_LEISTUNG = eINSTANCE.getIPhysioLeistung();
		/**
		 * The meta object literal for the '<em><b>Valid From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPHYSIO_LEISTUNG__VALID_FROM = eINSTANCE.getIPhysioLeistung_ValidFrom();
		/**
		 * The meta object literal for the '<em><b>Valid To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPHYSIO_LEISTUNG__VALID_TO = eINSTANCE.getIPhysioLeistung_ValidTo();
		/**
		 * The meta object literal for the '<em><b>TP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPHYSIO_LEISTUNG__TP = eINSTANCE.getIPhysioLeistung_TP();
		/**
		 * The meta object literal for the '<em><b>Ziffer</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPHYSIO_LEISTUNG__ZIFFER = eINSTANCE.getIPhysioLeistung_Ziffer();
		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPHYSIO_LEISTUNG__DESCRIPTION = eINSTANCE.getIPhysioLeistung_Description();
		/**
		 * The meta object literal for the '<em><b>Law</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IPHYSIO_LEISTUNG__LAW = eINSTANCE.getIPhysioLeistung_Law();

	}

} //PhysioPackage
