/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.ps25.impl;

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

import ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung;
import ch.elexis.base.ch.arzttarife.ps25.Ps25Factory;
import ch.elexis.base.ch.arzttarife.ps25.Ps25Package;

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
public class Ps25PackageImpl extends EPackageImpl implements Ps25Package {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iPs25LeistungEClass = null;

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
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Ps25PackageImpl() {
		super(eNS_URI, Ps25Factory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link Ps25Package#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static Ps25Package init() {
		if (isInited) return (Ps25Package)EPackage.Registry.INSTANCE.getEPackage(Ps25Package.eNS_URI);

		// Obtain or create and register package
		Object registeredPs25Package = EPackage.Registry.INSTANCE.get(eNS_URI);
		Ps25PackageImpl thePs25Package = registeredPs25Package instanceof Ps25PackageImpl ? (Ps25PackageImpl)registeredPs25Package : new Ps25PackageImpl();

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

		// Create package meta-data objects
		thePs25Package.createPackageContents();
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

		// Initialize created meta-data
		thePs25Package.initializePackageContents();
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

		// Mark meta-data to indicate it can't be changed
		thePs25Package.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(Ps25Package.eNS_URI, thePs25Package);
		return thePs25Package;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIPs25Leistung() {
		return iPs25LeistungEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_ValidFrom() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_ValidTo() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_TP() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_SubChapter() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_Chapter() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_HonorarEmpfaenger() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_MehrleistungBei() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_Spezifikation() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_AnwendungsRegeln() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_Stufe() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_MoeglicheKombination() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_MehrleistungsTyp() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPs25Leistung_Mehrleistung() {
		return (EAttribute)iPs25LeistungEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Ps25Factory getPs25Factory() {
		return (Ps25Factory)getEFactoryInstance();
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
		iPs25LeistungEClass = createEClass(IPS25_LEISTUNG);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__VALID_FROM);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__VALID_TO);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__TP);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__SUB_CHAPTER);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__CHAPTER);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__HONORAR_EMPFAENGER);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__MEHRLEISTUNG_BEI);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__SPEZIFIKATION);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__ANWENDUNGS_REGELN);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__STUFE);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__MOEGLICHE_KOMBINATION);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__MEHRLEISTUNGS_TYP);
		createEAttribute(iPs25LeistungEClass, IPS25_LEISTUNG__MEHRLEISTUNG);
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
		iPs25LeistungEClass.getESuperTypes().add(theModelPackage.getIService());

		// Initialize classes and features; add operations and parameters
		initEClass(iPs25LeistungEClass, IPs25Leistung.class, "IPs25Leistung", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIPs25Leistung_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_TP(), ecorePackage.getEString(), "TP", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_SubChapter(), ecorePackage.getEString(), "subChapter", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_Chapter(), ecorePackage.getEString(), "chapter", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_HonorarEmpfaenger(), ecorePackage.getEString(), "honorarEmpfaenger", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_MehrleistungBei(), ecorePackage.getEString(), "mehrleistungBei", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_Spezifikation(), ecorePackage.getEString(), "spezifikation", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_AnwendungsRegeln(), ecorePackage.getEString(), "anwendungsRegeln", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_Stufe(), ecorePackage.getEString(), "stufe", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_MoeglicheKombination(), ecorePackage.getEString(), "moeglicheKombination", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_MehrleistungsTyp(), ecorePackage.getEString(), "mehrleistungsTyp", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPs25Leistung_Mehrleistung(), ecorePackage.getEString(), "mehrleistung", null, 0, 1, IPs25Leistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //Ps25PackageImpl
