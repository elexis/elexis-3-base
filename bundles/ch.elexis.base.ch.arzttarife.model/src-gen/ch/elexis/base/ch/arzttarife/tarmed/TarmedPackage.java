/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import ch.elexis.core.model.ModelPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedFactory
 * @model kind="package"
 * @generated
 */
public interface TarmedPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "tarmed";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ch.elexis.base/model/arzttarife/tarmed";

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
	TarmedPackage eINSTANCE = ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl.init();

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung <em>ITarmed Leistung</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedLeistung()
	 * @generated
	 */
	int ITARMED_LEISTUNG = 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__CODE = ModelPackage.IBILLABLE__CODE;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__TEXT = ModelPackage.IBILLABLE__TEXT;

	/**
	 * The feature id for the '<em><b>Minutes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__MINUTES = ModelPackage.IBILLABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>AL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__AL = ModelPackage.IBILLABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>TL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__TL = ModelPackage.IBILLABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Digni Quali</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__DIGNI_QUALI = ModelPackage.IBILLABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Digni Quanti</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__DIGNI_QUANTI = ModelPackage.IBILLABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Exclusion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__EXCLUSION = ModelPackage.IBILLABLE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Extension</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__EXTENSION = ModelPackage.IBILLABLE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG__PARENT = ModelPackage.IBILLABLE_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>ITarmed Leistung</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_LEISTUNG_FEATURE_COUNT = ModelPackage.IBILLABLE_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension <em>ITarmed Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedExtension()
	 * @generated
	 */
	int ITARMED_EXTENSION = 1;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION__DELETED = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>ITarmed Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_EXTENSION_FEATURE_COUNT = ModelPackage.IDENTIFIABLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup <em>ITarmed Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedGroup()
	 * @generated
	 */
	int ITARMED_GROUP = 2;

	/**
	 * The feature id for the '<em><b>Deleted</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__DELETED = ModelPackage.DELETEABLE__DELETED;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP__CODE = ModelPackage.DELETEABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>ITarmed Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_GROUP_FEATURE_COUNT = ModelPackage.DELETEABLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation <em>ITarmed Kumulation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation
	 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedKumulation()
	 * @generated
	 */
	int ITARMED_KUMULATION = 3;

	/**
	 * The feature id for the '<em><b>Slave Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__SLAVE_CODE = 0;

	/**
	 * The feature id for the '<em><b>Slave Art</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__SLAVE_ART = 1;

	/**
	 * The feature id for the '<em><b>Valid Side</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION__VALID_SIDE = 2;

	/**
	 * The number of structural features of the '<em>ITarmed Kumulation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITARMED_KUMULATION_FEATURE_COUNT = 3;


	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung <em>ITarmed Leistung</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Leistung</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung
	 * @generated
	 */
	EClass getITarmedLeistung();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getMinutes <em>Minutes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Minutes</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getMinutes()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_Minutes();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getAL <em>AL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>AL</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getAL()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_AL();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getTL <em>TL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>TL</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getTL()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_TL();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuali <em>Digni Quali</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Digni Quali</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuali()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_DigniQuali();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuanti <em>Digni Quanti</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Digni Quanti</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getDigniQuanti()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_DigniQuanti();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExclusion <em>Exclusion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusion</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExclusion()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EAttribute getITarmedLeistung_Exclusion();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExtension <em>Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getExtension()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EReference getITarmedLeistung_Extension();

	/**
	 * Returns the meta object for the reference '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung#getParent()
	 * @see #getITarmedLeistung()
	 * @generated
	 */
	EReference getITarmedLeistung_Parent();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension <em>ITarmed Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Extension</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension
	 * @generated
	 */
	EClass getITarmedExtension();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup <em>ITarmed Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Group</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup
	 * @generated
	 */
	EClass getITarmedGroup();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup#getCode()
	 * @see #getITarmedGroup()
	 * @generated
	 */
	EAttribute getITarmedGroup_Code();

	/**
	 * Returns the meta object for class '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation <em>ITarmed Kumulation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ITarmed Kumulation</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation
	 * @generated
	 */
	EClass getITarmedKumulation();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveCode <em>Slave Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Slave Code</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveCode()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_SlaveCode();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveArt <em>Slave Art</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Slave Art</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getSlaveArt()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_SlaveArt();

	/**
	 * Returns the meta object for the attribute '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidSide <em>Valid Side</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid Side</em>'.
	 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation#getValidSide()
	 * @see #getITarmedKumulation()
	 * @generated
	 */
	EAttribute getITarmedKumulation_ValidSide();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TarmedFactory getTarmedFactory();

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
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung <em>ITarmed Leistung</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedLeistung()
		 * @generated
		 */
		EClass ITARMED_LEISTUNG = eINSTANCE.getITarmedLeistung();

		/**
		 * The meta object literal for the '<em><b>Minutes</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__MINUTES = eINSTANCE.getITarmedLeistung_Minutes();

		/**
		 * The meta object literal for the '<em><b>AL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__AL = eINSTANCE.getITarmedLeistung_AL();

		/**
		 * The meta object literal for the '<em><b>TL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__TL = eINSTANCE.getITarmedLeistung_TL();

		/**
		 * The meta object literal for the '<em><b>Digni Quali</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__DIGNI_QUALI = eINSTANCE.getITarmedLeistung_DigniQuali();

		/**
		 * The meta object literal for the '<em><b>Digni Quanti</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__DIGNI_QUANTI = eINSTANCE.getITarmedLeistung_DigniQuanti();

		/**
		 * The meta object literal for the '<em><b>Exclusion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_LEISTUNG__EXCLUSION = eINSTANCE.getITarmedLeistung_Exclusion();

		/**
		 * The meta object literal for the '<em><b>Extension</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARMED_LEISTUNG__EXTENSION = eINSTANCE.getITarmedLeistung_Extension();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITARMED_LEISTUNG__PARENT = eINSTANCE.getITarmedLeistung_Parent();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension <em>ITarmed Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedExtension()
		 * @generated
		 */
		EClass ITARMED_EXTENSION = eINSTANCE.getITarmedExtension();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup <em>ITarmed Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedGroup()
		 * @generated
		 */
		EClass ITARMED_GROUP = eINSTANCE.getITarmedGroup();

		/**
		 * The meta object literal for the '<em><b>Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_GROUP__CODE = eINSTANCE.getITarmedGroup_Code();

		/**
		 * The meta object literal for the '{@link ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation <em>ITarmed Kumulation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation
		 * @see ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl#getITarmedKumulation()
		 * @generated
		 */
		EClass ITARMED_KUMULATION = eINSTANCE.getITarmedKumulation();

		/**
		 * The meta object literal for the '<em><b>Slave Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__SLAVE_CODE = eINSTANCE.getITarmedKumulation_SlaveCode();

		/**
		 * The meta object literal for the '<em><b>Slave Art</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__SLAVE_ART = eINSTANCE.getITarmedKumulation_SlaveArt();

		/**
		 * The meta object literal for the '<em><b>Valid Side</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITARMED_KUMULATION__VALID_SIDE = eINSTANCE.getITarmedKumulation_ValidSide();

	}

} //TarmedPackage
