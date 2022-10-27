/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.physio.impl;

import ch.elexis.base.ch.arzttarife.complementary.ComplementaryPackage;
import ch.elexis.base.ch.arzttarife.complementary.impl.ComplementaryPackageImpl;
import ch.elexis.base.ch.arzttarife.nutrition.NutritionPackage;
import ch.elexis.base.ch.arzttarife.nutrition.impl.NutritionPackageImpl;
import ch.elexis.base.ch.arzttarife.pandemie.PandemiePackage;
import ch.elexis.base.ch.arzttarife.pandemie.impl.PandemiePackageImpl;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.physio.PhysioFactory;
import ch.elexis.base.ch.arzttarife.physio.PhysioPackage;

import ch.elexis.base.ch.arzttarife.psycho.PsychoPackage;
import ch.elexis.base.ch.arzttarife.psycho.impl.PsychoPackageImpl;
import ch.elexis.base.ch.arzttarife.rfe.RfePackage;
import ch.elexis.base.ch.arzttarife.rfe.impl.RfePackageImpl;
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
public class PhysioPackageImpl extends EPackageImpl implements PhysioPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iPhysioLeistungEClass = null;

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
	 * @see ch.elexis.base.ch.arzttarife.physio.PhysioPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private PhysioPackageImpl() {
		super(eNS_URI, PhysioFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link PhysioPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static PhysioPackage init() {
		if (isInited) return (PhysioPackage)EPackage.Registry.INSTANCE.getEPackage(PhysioPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredPhysioPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		PhysioPackageImpl thePhysioPackage = registeredPhysioPackage instanceof PhysioPackageImpl ? (PhysioPackageImpl)registeredPhysioPackage : new PhysioPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		ModelPackage.eINSTANCE.eClass();
		TypesPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(TarmedPackage.eNS_URI);
		TarmedPackageImpl theTarmedPackage = (TarmedPackageImpl)(registeredPackage instanceof TarmedPackageImpl ? registeredPackage : TarmedPackage.eINSTANCE);
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

		// Create package meta-data objects
		thePhysioPackage.createPackageContents();
		theTarmedPackage.createPackageContents();
		theComplementaryPackage.createPackageContents();
		theRfePackage.createPackageContents();
		thePandemiePackage.createPackageContents();
		theTarmedallowancePackage.createPackageContents();
		theNutritionPackage.createPackageContents();
		thePsychoPackage.createPackageContents();

		// Initialize created meta-data
		thePhysioPackage.initializePackageContents();
		theTarmedPackage.initializePackageContents();
		theComplementaryPackage.initializePackageContents();
		theRfePackage.initializePackageContents();
		thePandemiePackage.initializePackageContents();
		theTarmedallowancePackage.initializePackageContents();
		theNutritionPackage.initializePackageContents();
		thePsychoPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		thePhysioPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(PhysioPackage.eNS_URI, thePhysioPackage);
		return thePhysioPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIPhysioLeistung() {
		return iPhysioLeistungEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPhysioLeistung_ValidFrom() {
		return (EAttribute)iPhysioLeistungEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPhysioLeistung_ValidTo() {
		return (EAttribute)iPhysioLeistungEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPhysioLeistung_TP() {
		return (EAttribute)iPhysioLeistungEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPhysioLeistung_Ziffer() {
		return (EAttribute)iPhysioLeistungEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPhysioLeistung_Description() {
		return (EAttribute)iPhysioLeistungEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PhysioFactory getPhysioFactory() {
		return (PhysioFactory)getEFactoryInstance();
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
		iPhysioLeistungEClass = createEClass(IPHYSIO_LEISTUNG);
		createEAttribute(iPhysioLeistungEClass, IPHYSIO_LEISTUNG__VALID_FROM);
		createEAttribute(iPhysioLeistungEClass, IPHYSIO_LEISTUNG__VALID_TO);
		createEAttribute(iPhysioLeistungEClass, IPHYSIO_LEISTUNG__TP);
		createEAttribute(iPhysioLeistungEClass, IPHYSIO_LEISTUNG__ZIFFER);
		createEAttribute(iPhysioLeistungEClass, IPHYSIO_LEISTUNG__DESCRIPTION);
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
		iPhysioLeistungEClass.getESuperTypes().add(theModelPackage.getIBillable());

		// Initialize classes and features; add operations and parameters
		initEClass(iPhysioLeistungEClass, IPhysioLeistung.class, "IPhysioLeistung", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIPhysioLeistung_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, IPhysioLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPhysioLeistung_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, IPhysioLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPhysioLeistung_TP(), ecorePackage.getEString(), "TP", null, 0, 1, IPhysioLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPhysioLeistung_Ziffer(), ecorePackage.getEString(), "ziffer", null, 0, 1, IPhysioLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPhysioLeistung_Description(), ecorePackage.getEString(), "description", null, 0, 1, IPhysioLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //PhysioPackageImpl
