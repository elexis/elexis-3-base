/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
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
import ch.elexis.base.ch.arzttarife.psycho.PsychoPackage;
import ch.elexis.base.ch.arzttarife.psycho.impl.PsychoPackageImpl;
import ch.elexis.base.ch.arzttarife.rfe.RfePackage;
import ch.elexis.base.ch.arzttarife.rfe.impl.RfePackageImpl;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.MandantType;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedFactory;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation;
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
public class TarmedPackageImpl extends EPackageImpl implements TarmedPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTarmedLeistungEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTarmedExtensionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTarmedGroupEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTarmedKumulationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum mandantTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tarmedLimitationEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tarmedExclusionEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tarmedKumulationArtEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tarmedKumulationTypEDataType = null;

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
	 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private TarmedPackageImpl() {
		super(eNS_URI, TarmedFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link TarmedPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static TarmedPackage init() {
		if (isInited) return (TarmedPackage)EPackage.Registry.INSTANCE.getEPackage(TarmedPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredTarmedPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		TarmedPackageImpl theTarmedPackage = registeredTarmedPackage instanceof TarmedPackageImpl ? (TarmedPackageImpl)registeredTarmedPackage : new TarmedPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		ModelPackage.eINSTANCE.eClass();
		TypesPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(PhysioPackage.eNS_URI);
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

		// Create package meta-data objects
		theTarmedPackage.createPackageContents();
		thePhysioPackage.createPackageContents();
		theComplementaryPackage.createPackageContents();
		theRfePackage.createPackageContents();
		thePandemiePackage.createPackageContents();
		theTarmedallowancePackage.createPackageContents();
		theNutritionPackage.createPackageContents();
		thePsychoPackage.createPackageContents();
		theOccupationalPackage.createPackageContents();

		// Initialize created meta-data
		theTarmedPackage.initializePackageContents();
		thePhysioPackage.initializePackageContents();
		theComplementaryPackage.initializePackageContents();
		theRfePackage.initializePackageContents();
		thePandemiePackage.initializePackageContents();
		theTarmedallowancePackage.initializePackageContents();
		theNutritionPackage.initializePackageContents();
		thePsychoPackage.initializePackageContents();
		theOccupationalPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theTarmedPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(TarmedPackage.eNS_URI, theTarmedPackage);
		return theTarmedPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITarmedLeistung() {
		return iTarmedLeistungEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_AL() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_TL() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_DigniQuali() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_DigniQuanti() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_Exclusion() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getITarmedLeistung_Extension() {
		return (EReference)iTarmedLeistungEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getITarmedLeistung_Parent() {
		return (EReference)iTarmedLeistungEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_ValidFrom() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_ValidTo() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_ServiceTyp() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_Law() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_Sparte() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_Chapter() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedLeistung_Nickname() {
		return (EAttribute)iTarmedLeistungEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITarmedExtension() {
		return iTarmedExtensionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedExtension_Limits() {
		return (EAttribute)iTarmedExtensionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedExtension_MedInterpretation() {
		return (EAttribute)iTarmedExtensionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedExtension_TechInterpretation() {
		return (EAttribute)iTarmedExtensionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITarmedGroup() {
		return iTarmedGroupEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedGroup_Code() {
		return (EAttribute)iTarmedGroupEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedGroup_Services() {
		return (EAttribute)iTarmedGroupEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedGroup_ValidFrom() {
		return (EAttribute)iTarmedGroupEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedGroup_ValidTo() {
		return (EAttribute)iTarmedGroupEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedGroup_Law() {
		return (EAttribute)iTarmedGroupEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedGroup_Limitations() {
		return (EAttribute)iTarmedGroupEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getITarmedGroup_Extension() {
		return (EReference)iTarmedGroupEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITarmedKumulation() {
		return iTarmedKumulationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_SlaveCode() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_SlaveArt() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_ValidSide() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_ValidFrom() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_ValidTo() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_Law() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_MasterCode() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_MasterArt() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITarmedKumulation_Typ() {
		return (EAttribute)iTarmedKumulationEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getMandantType() {
		return mandantTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTarmedLimitation() {
		return tarmedLimitationEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTarmedExclusion() {
		return tarmedExclusionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTarmedKumulationArt() {
		return tarmedKumulationArtEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTarmedKumulationTyp() {
		return tarmedKumulationTypEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TarmedFactory getTarmedFactory() {
		return (TarmedFactory)getEFactoryInstance();
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
		iTarmedLeistungEClass = createEClass(ITARMED_LEISTUNG);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__AL);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__TL);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__DIGNI_QUALI);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__DIGNI_QUANTI);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__EXCLUSION);
		createEReference(iTarmedLeistungEClass, ITARMED_LEISTUNG__EXTENSION);
		createEReference(iTarmedLeistungEClass, ITARMED_LEISTUNG__PARENT);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__VALID_FROM);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__VALID_TO);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__SERVICE_TYP);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__LAW);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__SPARTE);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__CHAPTER);
		createEAttribute(iTarmedLeistungEClass, ITARMED_LEISTUNG__NICKNAME);

		iTarmedExtensionEClass = createEClass(ITARMED_EXTENSION);
		createEAttribute(iTarmedExtensionEClass, ITARMED_EXTENSION__LIMITS);
		createEAttribute(iTarmedExtensionEClass, ITARMED_EXTENSION__MED_INTERPRETATION);
		createEAttribute(iTarmedExtensionEClass, ITARMED_EXTENSION__TECH_INTERPRETATION);

		iTarmedGroupEClass = createEClass(ITARMED_GROUP);
		createEAttribute(iTarmedGroupEClass, ITARMED_GROUP__CODE);
		createEAttribute(iTarmedGroupEClass, ITARMED_GROUP__SERVICES);
		createEAttribute(iTarmedGroupEClass, ITARMED_GROUP__VALID_FROM);
		createEAttribute(iTarmedGroupEClass, ITARMED_GROUP__VALID_TO);
		createEAttribute(iTarmedGroupEClass, ITARMED_GROUP__LAW);
		createEAttribute(iTarmedGroupEClass, ITARMED_GROUP__LIMITATIONS);
		createEReference(iTarmedGroupEClass, ITARMED_GROUP__EXTENSION);

		iTarmedKumulationEClass = createEClass(ITARMED_KUMULATION);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__SLAVE_CODE);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__SLAVE_ART);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__VALID_SIDE);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__VALID_FROM);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__VALID_TO);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__LAW);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__MASTER_CODE);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__MASTER_ART);
		createEAttribute(iTarmedKumulationEClass, ITARMED_KUMULATION__TYP);

		// Create enums
		mandantTypeEEnum = createEEnum(MANDANT_TYPE);

		// Create data types
		tarmedLimitationEDataType = createEDataType(TARMED_LIMITATION);
		tarmedExclusionEDataType = createEDataType(TARMED_EXCLUSION);
		tarmedKumulationArtEDataType = createEDataType(TARMED_KUMULATION_ART);
		tarmedKumulationTypEDataType = createEDataType(TARMED_KUMULATION_TYP);
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
		iTarmedLeistungEClass.getESuperTypes().add(theModelPackage.getIService());
		iTarmedExtensionEClass.getESuperTypes().add(theModelPackage.getIdentifiable());
		iTarmedExtensionEClass.getESuperTypes().add(theModelPackage.getDeleteable());
		iTarmedExtensionEClass.getESuperTypes().add(theModelPackage.getWithExtInfo());
		iTarmedGroupEClass.getESuperTypes().add(theModelPackage.getDeleteable());
		iTarmedGroupEClass.getESuperTypes().add(theModelPackage.getIdentifiable());

		// Initialize classes and features; add operations and parameters
		initEClass(iTarmedLeistungEClass, ITarmedLeistung.class, "ITarmedLeistung", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getITarmedLeistung_AL(), ecorePackage.getEInt(), "AL", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_TL(), ecorePackage.getEInt(), "TL", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_DigniQuali(), ecorePackage.getEString(), "digniQuali", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_DigniQuanti(), ecorePackage.getEString(), "digniQuanti", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_Exclusion(), ecorePackage.getEString(), "exclusion", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getITarmedLeistung_Extension(), this.getITarmedExtension(), null, "extension", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getITarmedLeistung_Parent(), this.getITarmedLeistung(), null, "parent", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_ServiceTyp(), ecorePackage.getEString(), "serviceTyp", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_Law(), ecorePackage.getEString(), "law", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_Sparte(), ecorePackage.getEString(), "sparte", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_Chapter(), ecorePackage.getEBoolean(), "chapter", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedLeistung_Nickname(), ecorePackage.getEString(), "nickname", null, 0, 1, ITarmedLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(iTarmedLeistungEClass, ecorePackage.getEString(), "getServiceGroups", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "date", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTarmedLeistungEClass, ecorePackage.getEString(), "getServiceBlocks", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "date", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(iTarmedLeistungEClass, ecorePackage.getEBoolean(), "requiresSide", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTarmedLeistungEClass, ecorePackage.getEInt(), "getAL", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theModelPackage.getIMandator(), "mandator", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTarmedLeistungEClass, null, "getKumulations", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getTarmedKumulationArt(), "type", 0, 1, IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(theTypesPackage.getList());
		EGenericType g2 = createEGenericType(this.getITarmedKumulation());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		op = addEOperation(iTarmedLeistungEClass, null, "getHierarchy", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "date", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theTypesPackage.getList());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		addEOperation(iTarmedLeistungEClass, ecorePackage.getEBoolean(), "isZuschlagsleistung", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(iTarmedExtensionEClass, ITarmedExtension.class, "ITarmedExtension", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		g1 = createEGenericType(ecorePackage.getEMap());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		initEAttribute(getITarmedExtension_Limits(), g1, "limits", null, 0, 1, ITarmedExtension.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedExtension_MedInterpretation(), ecorePackage.getEString(), "medInterpretation", null, 0, 1, ITarmedExtension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedExtension_TechInterpretation(), ecorePackage.getEString(), "techInterpretation", null, 0, 1, ITarmedExtension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(iTarmedGroupEClass, ITarmedGroup.class, "ITarmedGroup", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getITarmedGroup_Code(), ecorePackage.getEString(), "code", null, 0, 1, ITarmedGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedGroup_Services(), ecorePackage.getEString(), "services", null, 0, -1, ITarmedGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedGroup_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ITarmedGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedGroup_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ITarmedGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedGroup_Law(), ecorePackage.getEString(), "law", null, 0, 1, ITarmedGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedGroup_Limitations(), this.getTarmedLimitation(), "limitations", null, 0, -1, ITarmedGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getITarmedGroup_Extension(), this.getITarmedExtension(), null, "extension", null, 0, 1, ITarmedGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(iTarmedGroupEClass, ecorePackage.getEBoolean(), "validAt", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "reference", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTarmedGroupEClass, null, "getExclusions", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theModelPackage.getIEncounter(), "encounter", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theTypesPackage.getList());
		g2 = createEGenericType(this.getTarmedExclusion());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		initEClass(iTarmedKumulationEClass, ITarmedKumulation.class, "ITarmedKumulation", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getITarmedKumulation_SlaveCode(), ecorePackage.getEString(), "slaveCode", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_SlaveArt(), this.getTarmedKumulationArt(), "slaveArt", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_ValidSide(), ecorePackage.getEString(), "validSide", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_Law(), ecorePackage.getEString(), "law", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_MasterCode(), ecorePackage.getEString(), "masterCode", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_MasterArt(), this.getTarmedKumulationArt(), "masterArt", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITarmedKumulation_Typ(), this.getTarmedKumulationTyp(), "typ", null, 0, 1, ITarmedKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(iTarmedKumulationEClass, ecorePackage.getEBoolean(), "isValidKumulation", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "reference", 0, 1, IS_UNIQUE, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(mandantTypeEEnum, MandantType.class, "MandantType");
		addEEnumLiteral(mandantTypeEEnum, MandantType.SPECIALIST);
		addEEnumLiteral(mandantTypeEEnum, MandantType.PRACTITIONER);

		// Initialize data types
		initEDataType(tarmedLimitationEDataType, TarmedLimitation.class, "TarmedLimitation", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(tarmedExclusionEDataType, TarmedExclusion.class, "TarmedExclusion", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(tarmedKumulationArtEDataType, TarmedKumulationArt.class, "TarmedKumulationArt", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(tarmedKumulationTypEDataType, TarmedKumulationTyp.class, "TarmedKumulationTyp", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //TarmedPackageImpl
