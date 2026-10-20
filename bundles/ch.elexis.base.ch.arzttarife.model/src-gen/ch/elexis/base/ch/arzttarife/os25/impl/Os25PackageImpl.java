/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.os25.impl;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulatoryPackage;

import ch.elexis.base.ch.arzttarife.ambulatory.impl.AmbulatoryPackageImpl;

import ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage;

import ch.elexis.base.ch.arzttarife.complementary.impl.ComplementaryPackageImpl;

import ch.elexis.base.ch.arzttarife.nutrition.NutritionPackage;

import ch.elexis.base.ch.arzttarife.nutrition.impl.NutritionPackageImpl;

import ch.elexis.base.ch.arzttarife.occupational.OccupationalPackage;

import ch.elexis.base.ch.arzttarife.occupational.impl.OccupationalPackageImpl;

import ch.elexis.base.ch.arzttarife.os25.IOs25Leistung;
import ch.elexis.base.ch.arzttarife.os25.Os25Factory;
import ch.elexis.base.ch.arzttarife.os25.Os25Package;

import ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage;

import ch.elexis.base.ch.arzttarife.pandemie.impl.PandemiePackageImpl;

import ch.elexis.base.ch.arzttarife.physio.PhysioPackage;

import ch.elexis.base.ch.arzttarife.physio.impl.PhysioPackageImpl;

import ch.elexis.base.ch.arzttarife.ps25.Ps25Package;

import ch.elexis.base.ch.arzttarife.ps25.impl.Ps25PackageImpl;

import ch.elexis.base.ch.arzttarife.psycho.PsychoPackage;

import ch.elexis.base.ch.arzttarife.psycho.impl.PsychoPackageImpl;

import ch.elexis.base.ch.arzttarife.rfe.RfePackage;

import ch.elexis.base.ch.arzttarife.rfe.impl.RfePackageImpl;

import ch.elexis.base.ch.arzttarife.tardoc.TardocPackage;

import ch.elexis.base.ch.arzttarife.tardoc.impl.TardocPackageImpl;

import ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage;

import ch.elexis.base.ch.arzttarife.tarmed.impl.TarmedPackageImpl;

import ch.elexis.base.ch.arzttarife.tarmedallowance.TarmedallowancePackage;

import ch.elexis.base.ch.arzttarife.tarmedallowance.impl.TarmedallowancePackageImpl;

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
public class Os25PackageImpl extends EPackageImpl implements Os25Package {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iOs25LeistungEClass = null;

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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Os25PackageImpl() {
		super(eNS_URI, Os25Factory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link Os25Package#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static Os25Package init() {
		if (isInited) return (Os25Package)EPackage.Registry.INSTANCE.getEPackage(Os25Package.eNS_URI);

		// Obtain or create and register package
		Object registeredOs25Package = EPackage.Registry.INSTANCE.get(eNS_URI);
		Os25PackageImpl theOs25Package = registeredOs25Package instanceof Os25PackageImpl ? (Os25PackageImpl)registeredOs25Package : new Os25PackageImpl();

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
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(TarmedallowancePackage.eNS_URI);
		TarmedallowancePackageImpl theTarmedallowancePackage = (TarmedallowancePackageImpl)(registeredPackage instanceof TarmedallowancePackageImpl ? registeredPackage : TarmedallowancePackage.eINSTANCE);
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
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(Ps25Package.eNS_URI);
		Ps25PackageImpl thePs25Package = (Ps25PackageImpl)(registeredPackage instanceof Ps25PackageImpl ? registeredPackage : Ps25Package.eINSTANCE);

		// Create package meta-data objects
		theOs25Package.createPackageContents();
		theTarmedPackage.createPackageContents();
		thePhysioPackage.createPackageContents();
		theComplementaryPackage.createPackageContents();
		theRfePackage.createPackageContents();
		thePandemiePackage.createPackageContents();
		theTarmedallowancePackage.createPackageContents();
		theNutritionPackage.createPackageContents();
		thePsychoPackage.createPackageContents();
		theOccupationalPackage.createPackageContents();
		theTardocPackage.createPackageContents();
		theAmbulatoryPackage.createPackageContents();
		thePs25Package.createPackageContents();

		// Initialize created meta-data
		theOs25Package.initializePackageContents();
		theTarmedPackage.initializePackageContents();
		thePhysioPackage.initializePackageContents();
		theComplementaryPackage.initializePackageContents();
		theRfePackage.initializePackageContents();
		thePandemiePackage.initializePackageContents();
		theTarmedallowancePackage.initializePackageContents();
		theNutritionPackage.initializePackageContents();
		thePsychoPackage.initializePackageContents();
		theOccupationalPackage.initializePackageContents();
		theTardocPackage.initializePackageContents();
		theAmbulatoryPackage.initializePackageContents();
		thePs25Package.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theOs25Package.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(Os25Package.eNS_URI, theOs25Package);
		return theOs25Package;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIOs25Leistung() {
		return iOs25LeistungEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_ValidFrom() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_ValidTo() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_TP() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_SubChapter() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_Chapter() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_HonorarEmpfaenger() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_Leistung() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_Spezifikation() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_AnwendungsRegeln() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_Stufe() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_MoeglicheKombination() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIOs25Leistung_LeistungsTyp() {
		return (EAttribute)iOs25LeistungEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Os25Factory getOs25Factory() {
		return (Os25Factory)getEFactoryInstance();
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
		iOs25LeistungEClass = createEClass(IOS25_LEISTUNG);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__VALID_FROM);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__VALID_TO);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__TP);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__SUB_CHAPTER);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__CHAPTER);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__HONORAR_EMPFAENGER);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__LEISTUNG);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__SPEZIFIKATION);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__ANWENDUNGS_REGELN);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__STUFE);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__MOEGLICHE_KOMBINATION);
		createEAttribute(iOs25LeistungEClass, IOS25_LEISTUNG__LEISTUNGS_TYP);
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
		iOs25LeistungEClass.getESuperTypes().add(theModelPackage.getIBillable());

		// Initialize classes and features; add operations and parameters
		initEClass(iOs25LeistungEClass, IOs25Leistung.class, "IOs25Leistung", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIOs25Leistung_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_TP(), ecorePackage.getEString(), "TP", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_SubChapter(), ecorePackage.getEString(), "subChapter", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_Chapter(), ecorePackage.getEString(), "chapter", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_HonorarEmpfaenger(), ecorePackage.getEString(), "honorarEmpfaenger", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_Leistung(), ecorePackage.getEString(), "leistung", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_Spezifikation(), ecorePackage.getEString(), "spezifikation", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_AnwendungsRegeln(), ecorePackage.getEString(), "anwendungsRegeln", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_Stufe(), ecorePackage.getEString(), "stufe", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_MoeglicheKombination(), ecorePackage.getEString(), "moeglicheKombination", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIOs25Leistung_LeistungsTyp(), ecorePackage.getEString(), "leistungsTyp", null, 0, 1, IOs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //Os25PackageImpl
