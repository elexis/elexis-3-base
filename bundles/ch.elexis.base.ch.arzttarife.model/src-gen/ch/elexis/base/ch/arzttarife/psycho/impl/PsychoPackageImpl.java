/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.psycho.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

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
import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.base.ch.arzttarife.psycho.PsychoFactory;
import ch.elexis.base.ch.arzttarife.psycho.PsychoPackage;
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

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PsychoPackageImpl extends EPackageImpl implements PsychoPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iPsychoLeistungEClass = null;

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
	 * @see ch.elexis.base.ch.arzttarife.psycho.PsychoPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private PsychoPackageImpl() {
		super(eNS_URI, PsychoFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link PsychoPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static PsychoPackage init() {
		if (isInited) return (PsychoPackage)EPackage.Registry.INSTANCE.getEPackage(PsychoPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredPsychoPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		PsychoPackageImpl thePsychoPackage = registeredPsychoPackage instanceof PsychoPackageImpl ? (PsychoPackageImpl)registeredPsychoPackage : new PsychoPackageImpl();

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
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(OccupationalPackage.eNS_URI);
		OccupationalPackageImpl theOccupationalPackage = (OccupationalPackageImpl)(registeredPackage instanceof OccupationalPackageImpl ? registeredPackage : OccupationalPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(TardocPackage.eNS_URI);
		TardocPackageImpl theTardocPackage = (TardocPackageImpl)(registeredPackage instanceof TardocPackageImpl ? registeredPackage : TardocPackage.eINSTANCE);

		// Create package meta-data objects
		thePsychoPackage.createPackageContents();
		theTarmedPackage.createPackageContents();
		thePhysioPackage.createPackageContents();
		theComplementaryPackage.createPackageContents();
		theRfePackage.createPackageContents();
		thePandemiePackage.createPackageContents();
		theTarmedallowancePackage.createPackageContents();
		theNutritionPackage.createPackageContents();
		theOccupationalPackage.createPackageContents();
		theTardocPackage.createPackageContents();

		// Initialize created meta-data
		thePsychoPackage.initializePackageContents();
		theTarmedPackage.initializePackageContents();
		thePhysioPackage.initializePackageContents();
		theComplementaryPackage.initializePackageContents();
		theRfePackage.initializePackageContents();
		thePandemiePackage.initializePackageContents();
		theTarmedallowancePackage.initializePackageContents();
		theNutritionPackage.initializePackageContents();
		theOccupationalPackage.initializePackageContents();
		theTardocPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		thePsychoPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(PsychoPackage.eNS_URI, thePsychoPackage);
		return thePsychoPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIPsychoLeistung() {
		return iPsychoLeistungEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPsychoLeistung_ValidFrom() {
		return (EAttribute)iPsychoLeistungEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPsychoLeistung_ValidTo() {
		return (EAttribute)iPsychoLeistungEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPsychoLeistung_TP() {
		return (EAttribute)iPsychoLeistungEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPsychoLeistung_Description() {
		return (EAttribute)iPsychoLeistungEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPsychoLeistung_Limitations() {
		return (EAttribute)iPsychoLeistungEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIPsychoLeistung_Exclusions() {
		return (EAttribute)iPsychoLeistungEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PsychoFactory getPsychoFactory() {
		return (PsychoFactory)getEFactoryInstance();
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
		iPsychoLeistungEClass = createEClass(IPSYCHO_LEISTUNG);
		createEAttribute(iPsychoLeistungEClass, IPSYCHO_LEISTUNG__VALID_FROM);
		createEAttribute(iPsychoLeistungEClass, IPSYCHO_LEISTUNG__VALID_TO);
		createEAttribute(iPsychoLeistungEClass, IPSYCHO_LEISTUNG__TP);
		createEAttribute(iPsychoLeistungEClass, IPSYCHO_LEISTUNG__DESCRIPTION);
		createEAttribute(iPsychoLeistungEClass, IPSYCHO_LEISTUNG__LIMITATIONS);
		createEAttribute(iPsychoLeistungEClass, IPSYCHO_LEISTUNG__EXCLUSIONS);
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
		iPsychoLeistungEClass.getESuperTypes().add(theModelPackage.getIBillable());

		// Initialize classes and features; add operations and parameters
		initEClass(iPsychoLeistungEClass, IPsychoLeistung.class, "IPsychoLeistung", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIPsychoLeistung_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, IPsychoLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPsychoLeistung_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, IPsychoLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPsychoLeistung_TP(), ecorePackage.getEString(), "TP", null, 0, 1, IPsychoLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPsychoLeistung_Description(), ecorePackage.getEString(), "description", null, 0, 1, IPsychoLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPsychoLeistung_Limitations(), ecorePackage.getEString(), "limitations", null, 0, 1, IPsychoLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIPsychoLeistung_Exclusions(), ecorePackage.getEString(), "exclusions", null, 0, 1, IPsychoLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //PsychoPackageImpl
