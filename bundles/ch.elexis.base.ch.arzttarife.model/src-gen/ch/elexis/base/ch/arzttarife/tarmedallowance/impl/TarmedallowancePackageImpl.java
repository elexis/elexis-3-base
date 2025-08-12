/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmedallowance.impl;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage;
import ch.elexis.base.ch.arzttarife.ambulatory.impl.AmbulatoryPackageImpl;
import ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage;

import ch.elexis.base.ch.arzttarife.complementary.impl.ComplementaryPackageImpl;

import ch.elexis.base.ch.arzttarife.nutrition.NutritionPackage;
import ch.elexis.base.ch.arzttarife.nutrition.impl.NutritionPackageImpl;
import ch.elexis.base.ch.arzttarife.occupational.OccupationalPackage;
import ch.elexis.base.ch.arzttarife.occupational.impl.OccupationalPackageImpl;
import ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage;

import ch.elexis.base.ch.arzttarife.pandemie.impl.PandemiePackageImpl;

import ch.elexis.base.ch.arzttarife.physio.PhysioPackage;

import ch.elexis.base.ch.arzttarife.physio.impl.PhysioPackageImpl;

import ch.elexis.base.ch.arzttarife.psycho.PsychoPackage;
import ch.elexis.base.ch.arzttarife.psycho.impl.PsychoPackageImpl;
import ch.elexis.base.ch.arzttarife.rfe.RfePackage;

import ch.elexis.base.ch.arzttarife.rfe.impl.RfePackageImpl;

import ch.elexis.base.ch.arzttarife.tardoc.TardocPackage;
import ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage;

import ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl;

import ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance;
import ch.elexis.base.ch.arzttarife.tarmedallowance.TarmedallowanceFactory;
import ch.elexis.base.ch.arzttarife.tarmedallowance.TarmedallowancePackage;

import ch.elexis.core.model.ModelPackage;

import ch.elexis.core.types.TypesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TarmedallowancePackageImpl extends EPackageImpl implements TarmedallowancePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTarmedAllowanceEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see ch.elexis.base.ch.arzttarife.tarmedallowance.TarmedallowancePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private TarmedallowancePackageImpl() {
		super(eNS_URI, TarmedallowanceFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link TarmedallowancePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static TarmedallowancePackage init() {
		if (isInited) return (TarmedallowancePackage)EPackage.Registry.INSTANCE.getEPackage(TarmedallowancePackage.eNS_URI);

		// Obtain or create and register package
		Object registeredTarmedallowancePackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		TarmedallowancePackageImpl theTarmedallowancePackage = registeredTarmedallowancePackage instanceof TarmedallowancePackageImpl ? (TarmedallowancePackageImpl)registeredTarmedallowancePackage : new TarmedallowancePackageImpl();

		isInited = true;

		// Initialize simple dependencies
		ModelPackage.eINSTANCE.eClass();
		TypesPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(TarmedPackage.eNS_URI);
		TarmedPackageImpl theTarmedPackage = (TarmedPackageImpl)(registeredPackage instanceof TarmedPackageImpl ? registeredPackage : TarmedPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(PhysioPackage.eNS_URI);
		PhysioPackageImpl thePhysioPackage = (PhysioPackageImpl)(registeredPackage instanceof PhysioPackageImpl ? registeredPackage : PhysioPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ComplementaryPackage.eNS_URI);
		ComplementaryPackageImpl theComplementaryPackage = (ComplementaryPackageImpl)(registeredPackage instanceof ComplementaryPackageImpl ? registeredPackage : ComplementaryPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(RfePackage.eNS_URI);
		RfePackageImpl theRfePackage = (RfePackageImpl)(registeredPackage instanceof RfePackageImpl ? registeredPackage : RfePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(PandemiePackage.eNS_URI);
		PandemiePackageImpl thePandemiePackage = (PandemiePackageImpl)(registeredPackage instanceof PandemiePackageImpl ? registeredPackage : PandemiePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(NutritionPackage.eNS_URI);
		NutritionPackageImpl theNutritionPackage = (NutritionPackageImpl)(registeredPackage instanceof NutritionPackageImpl ? registeredPackage : NutritionPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(PsychoPackage.eNS_URI);
		PsychoPackageImpl thePsychoPackage = (PsychoPackageImpl)(registeredPackage instanceof PsychoPackageImpl ? registeredPackage : PsychoPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(OccupationalPackage.eNS_URI);
		OccupationalPackageImpl theOccupationalPackage = (OccupationalPackageImpl)(registeredPackage instanceof OccupationalPackageImpl ? registeredPackage : OccupationalPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(TardocPackage.eNS_URI);
		TardocPackageImpl theTardocPackage = (TardocPackageImpl)(registeredPackage instanceof TardocPackageImpl ? registeredPackage : TardocPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(AmbulatoryPackage.eNS_URI);
		AmbulatoryPackageImpl theAmbulatoryPackage = (AmbulatoryPackageImpl)(registeredPackage instanceof AmbulatoryPackageImpl ? registeredPackage : AmbulatoryPackage.eINSTANCE);

		// Create package meta-data objects
		theTarmedallowancePackage.createPackageContents();
		theTarmedPackage.createPackageContents();
		thePhysioPackage.createPackageContents();
		theComplementaryPackage.createPackageContents();
		theRfePackage.createPackageContents();
		thePandemiePackage.createPackageContents();
		theNutritionPackage.createPackageContents();
		thePsychoPackage.createPackageContents();
		theOccupationalPackage.createPackageContents();
		theTardocPackage.createPackageContents();
		theAmbulatoryPackage.createPackageContents();

		// Initialize created meta-data
		theTarmedallowancePackage.initializePackageContents();
		theTarmedPackage.initializePackageContents();
		thePhysioPackage.initializePackageContents();
		theComplementaryPackage.initializePackageContents();
		theRfePackage.initializePackageContents();
		thePandemiePackage.initializePackageContents();
		theNutritionPackage.initializePackageContents();
		thePsychoPackage.initializePackageContents();
		theOccupationalPackage.initializePackageContents();
		theTardocPackage.initializePackageContents();
		theAmbulatoryPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theTarmedallowancePackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(TarmedallowancePackage.eNS_URI, theTarmedallowancePackage);
		return theTarmedallowancePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITarmedAllowance() {
		return iTarmedAllowanceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedAllowance_ValidFrom() {
		return (EAttribute)iTarmedAllowanceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedAllowance_ValidTo() {
		return (EAttribute)iTarmedAllowanceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedAllowance_Chapter() {
		return (EAttribute)iTarmedAllowanceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedAllowance_TP() {
		return (EAttribute)iTarmedAllowanceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TarmedallowanceFactory getTarmedallowanceFactory() {
		return (TarmedallowanceFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		iTarmedAllowanceEClass = createEClass(ITARMED_ALLOWANCE);
		createEAttribute(iTarmedAllowanceEClass, ITARMED_ALLOWANCE__VALID_FROM);
		createEAttribute(iTarmedAllowanceEClass, ITARMED_ALLOWANCE__VALID_TO);
		createEAttribute(iTarmedAllowanceEClass, ITARMED_ALLOWANCE__CHAPTER);
		createEAttribute(iTarmedAllowanceEClass, ITARMED_ALLOWANCE__TP);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		ModelPackage theModelPackage = (ModelPackage)EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);
		TypesPackage theTypesPackage = (TypesPackage)EPackage.Registry.INSTANCE.getEPackage(TypesPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		iTarmedAllowanceEClass.getESuperTypes().add(theModelPackage.getIBillable());

		// Initialize classes and features; add operations and parameters
		initEClass(iTarmedAllowanceEClass, ITarmedAllowance.class, "ITarmedAllowance", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getITarmedAllowance_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ITarmedAllowance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedAllowance_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ITarmedAllowance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedAllowance_Chapter(), ecorePackage.getEString(), "chapter", null, 0, 1, ITarmedAllowance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedAllowance_TP(), ecorePackage.getEString(), "TP", null, 0, 1, ITarmedAllowance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //TarmedallowancePackageImpl
