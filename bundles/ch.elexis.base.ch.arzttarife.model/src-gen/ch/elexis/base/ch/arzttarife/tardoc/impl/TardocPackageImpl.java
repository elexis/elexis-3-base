/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tardoc.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

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
import ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tardoc.MandantType;
import ch.elexis.base.ch.arzttarife.tardoc.TardocFactory;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp;
import ch.elexis.base.ch.arzttarife.tardoc.TardocPackage;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocExclusion;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLimitation;
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
public class TardocPackageImpl extends EPackageImpl implements TardocPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTardocLeistungEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTardocExtensionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTardocGroupEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iTardocKumulationEClass = null;

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
	private EDataType tardocLimitationEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tardocExclusionEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tardocKumulationArtEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tardocKumulationTypEDataType = null;

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
	 * @see ch.elexis.base.ch.arzttarife.tardoc.TardocPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private TardocPackageImpl() {
		super(eNS_URI, TardocFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link TardocPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static TardocPackage init() {
		if (isInited) return (TardocPackage)EPackage.Registry.INSTANCE.getEPackage(TardocPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredTardocPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		TardocPackageImpl theTardocPackage = registeredTardocPackage instanceof TardocPackageImpl ? (TardocPackageImpl)registeredTardocPackage : new TardocPackageImpl();

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
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(AmbulatoryPackage.eNS_URI);
		AmbulatoryPackageImpl theAmbulatoryPackage = (AmbulatoryPackageImpl)(registeredPackage instanceof AmbulatoryPackageImpl ? registeredPackage : AmbulatoryPackage.eINSTANCE);

		// Create package meta-data objects
		theTardocPackage.createPackageContents();
		theTarmedPackage.createPackageContents();
		thePhysioPackage.createPackageContents();
		theComplementaryPackage.createPackageContents();
		theRfePackage.createPackageContents();
		thePandemiePackage.createPackageContents();
		theTarmedallowancePackage.createPackageContents();
		theNutritionPackage.createPackageContents();
		thePsychoPackage.createPackageContents();
		theOccupationalPackage.createPackageContents();
		theAmbulatoryPackage.createPackageContents();

		// Initialize created meta-data
		theTardocPackage.initializePackageContents();
		theTarmedPackage.initializePackageContents();
		thePhysioPackage.initializePackageContents();
		theComplementaryPackage.initializePackageContents();
		theRfePackage.initializePackageContents();
		thePandemiePackage.initializePackageContents();
		theTarmedallowancePackage.initializePackageContents();
		theNutritionPackage.initializePackageContents();
		thePsychoPackage.initializePackageContents();
		theOccupationalPackage.initializePackageContents();
		theAmbulatoryPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theTardocPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(TardocPackage.eNS_URI, theTardocPackage);
		return theTardocPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITardocLeistung() {
		return iTardocLeistungEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_AL() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_IPL() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_DigniQuali() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_DigniQuanti() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_Exclusion() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getITardocLeistung_Extension() {
		return (EReference)iTardocLeistungEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getITardocLeistung_Parent() {
		return (EReference)iTardocLeistungEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_ValidFrom() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_ValidTo() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_ServiceTyp() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_Law() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_Sparte() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_Chapter() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocLeistung_Nickname() {
		return (EAttribute)iTardocLeistungEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITardocExtension() {
		return iTardocExtensionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocExtension_Limits() {
		return (EAttribute)iTardocExtensionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocExtension_MedInterpretation() {
		return (EAttribute)iTardocExtensionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocExtension_TechInterpretation() {
		return (EAttribute)iTardocExtensionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITardocGroup() {
		return iTardocGroupEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocGroup_Code() {
		return (EAttribute)iTardocGroupEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocGroup_Services() {
		return (EAttribute)iTardocGroupEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocGroup_ValidFrom() {
		return (EAttribute)iTardocGroupEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocGroup_ValidTo() {
		return (EAttribute)iTardocGroupEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocGroup_Law() {
		return (EAttribute)iTardocGroupEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocGroup_Limitations() {
		return (EAttribute)iTardocGroupEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getITardocGroup_Extension() {
		return (EReference)iTardocGroupEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getITardocKumulation() {
		return iTardocKumulationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_SlaveCode() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_SlaveArt() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_ValidSide() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_ValidFrom() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_ValidTo() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_Law() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_MasterCode() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_MasterArt() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getITardocKumulation_Typ() {
		return (EAttribute)iTardocKumulationEClass.getEStructuralFeatures().get(8);
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
	public EDataType getTardocLimitation() {
		return tardocLimitationEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTardocExclusion() {
		return tardocExclusionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTardocKumulationArt() {
		return tardocKumulationArtEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTardocKumulationTyp() {
		return tardocKumulationTypEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TardocFactory getTardocFactory() {
		return (TardocFactory)getEFactoryInstance();
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
		iTardocLeistungEClass = createEClass(ITARDOC_LEISTUNG);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__AL);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__IPL);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__DIGNI_QUALI);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__DIGNI_QUANTI);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__EXCLUSION);
		createEReference(iTardocLeistungEClass, ITARDOC_LEISTUNG__EXTENSION);
		createEReference(iTardocLeistungEClass, ITARDOC_LEISTUNG__PARENT);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__VALID_FROM);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__VALID_TO);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__SERVICE_TYP);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__LAW);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__SPARTE);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__CHAPTER);
		createEAttribute(iTardocLeistungEClass, ITARDOC_LEISTUNG__NICKNAME);

		iTardocExtensionEClass = createEClass(ITARDOC_EXTENSION);
		createEAttribute(iTardocExtensionEClass, ITARDOC_EXTENSION__LIMITS);
		createEAttribute(iTardocExtensionEClass, ITARDOC_EXTENSION__MED_INTERPRETATION);
		createEAttribute(iTardocExtensionEClass, ITARDOC_EXTENSION__TECH_INTERPRETATION);

		iTardocGroupEClass = createEClass(ITARDOC_GROUP);
		createEAttribute(iTardocGroupEClass, ITARDOC_GROUP__CODE);
		createEAttribute(iTardocGroupEClass, ITARDOC_GROUP__SERVICES);
		createEAttribute(iTardocGroupEClass, ITARDOC_GROUP__VALID_FROM);
		createEAttribute(iTardocGroupEClass, ITARDOC_GROUP__VALID_TO);
		createEAttribute(iTardocGroupEClass, ITARDOC_GROUP__LAW);
		createEAttribute(iTardocGroupEClass, ITARDOC_GROUP__LIMITATIONS);
		createEReference(iTardocGroupEClass, ITARDOC_GROUP__EXTENSION);

		iTardocKumulationEClass = createEClass(ITARDOC_KUMULATION);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__SLAVE_CODE);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__SLAVE_ART);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__VALID_SIDE);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__VALID_FROM);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__VALID_TO);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__LAW);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__MASTER_CODE);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__MASTER_ART);
		createEAttribute(iTardocKumulationEClass, ITARDOC_KUMULATION__TYP);

		// Create enums
		mandantTypeEEnum = createEEnum(MANDANT_TYPE);

		// Create data types
		tardocLimitationEDataType = createEDataType(TARDOC_LIMITATION);
		tardocExclusionEDataType = createEDataType(TARDOC_EXCLUSION);
		tardocKumulationArtEDataType = createEDataType(TARDOC_KUMULATION_ART);
		tardocKumulationTypEDataType = createEDataType(TARDOC_KUMULATION_TYP);
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
		iTardocLeistungEClass.getESuperTypes().add(theModelPackage.getIService());
		iTardocExtensionEClass.getESuperTypes().add(theModelPackage.getIdentifiable());
		iTardocExtensionEClass.getESuperTypes().add(theModelPackage.getDeleteable());
		iTardocExtensionEClass.getESuperTypes().add(theModelPackage.getWithExtInfo());
		iTardocGroupEClass.getESuperTypes().add(theModelPackage.getDeleteable());
		iTardocGroupEClass.getESuperTypes().add(theModelPackage.getIdentifiable());

		// Initialize classes and features; add operations and parameters
		initEClass(iTardocLeistungEClass, ITardocLeistung.class, "ITardocLeistung", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getITardocLeistung_AL(), ecorePackage.getEInt(), "AL", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_IPL(), ecorePackage.getEInt(), "IPL", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_DigniQuali(), ecorePackage.getEString(), "digniQuali", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_DigniQuanti(), ecorePackage.getEString(), "digniQuanti", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_Exclusion(), ecorePackage.getEString(), "exclusion", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getITardocLeistung_Extension(), this.getITardocExtension(), null, "extension", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getITardocLeistung_Parent(), this.getITardocLeistung(), null, "parent", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_ServiceTyp(), ecorePackage.getEString(), "serviceTyp", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_Law(), ecorePackage.getEString(), "law", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_Sparte(), ecorePackage.getEString(), "sparte", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_Chapter(), ecorePackage.getEBoolean(), "chapter", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocLeistung_Nickname(), ecorePackage.getEString(), "nickname", null, 0, 1, ITardocLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(iTardocLeistungEClass, ecorePackage.getEString(), "getServiceGroups", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "date", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTardocLeistungEClass, ecorePackage.getEString(), "getServiceBlocks", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "date", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(iTardocLeistungEClass, ecorePackage.getEBoolean(), "requiresSide", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTardocLeistungEClass, ecorePackage.getEInt(), "getAL", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theModelPackage.getIMandator(), "mandator", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTardocLeistungEClass, null, "getKumulations", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getTardocKumulationArt(), "type", 0, 1, IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(theTypesPackage.getList());
		EGenericType g2 = createEGenericType(this.getITardocKumulation());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		op = addEOperation(iTardocLeistungEClass, null, "getHierarchy", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "date", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theTypesPackage.getList());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		addEOperation(iTardocLeistungEClass, ecorePackage.getEBoolean(), "isZuschlagsleistung", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(iTardocExtensionEClass, ITardocExtension.class, "ITardocExtension", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		g1 = createEGenericType(ecorePackage.getEMap());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		initEAttribute(getITardocExtension_Limits(), g1, "limits", null, 0, 1, ITardocExtension.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocExtension_MedInterpretation(), ecorePackage.getEString(), "medInterpretation", null, 0, 1, ITardocExtension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocExtension_TechInterpretation(), ecorePackage.getEString(), "techInterpretation", null, 0, 1, ITardocExtension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(iTardocGroupEClass, ITardocGroup.class, "ITardocGroup", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getITardocGroup_Code(), ecorePackage.getEString(), "code", null, 0, 1, ITardocGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocGroup_Services(), ecorePackage.getEString(), "services", null, 0, -1, ITardocGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocGroup_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ITardocGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocGroup_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ITardocGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocGroup_Law(), ecorePackage.getEString(), "law", null, 0, 1, ITardocGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocGroup_Limitations(), this.getTardocLimitation(), "limitations", null, 0, -1, ITardocGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getITardocGroup_Extension(), this.getITardocExtension(), null, "extension", null, 0, 1, ITardocGroup.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(iTardocGroupEClass, ecorePackage.getEBoolean(), "validAt", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "reference", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(iTardocGroupEClass, null, "getExclusions", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theModelPackage.getIEncounter(), "encounter", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theTypesPackage.getList());
		g2 = createEGenericType(this.getTardocExclusion());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		initEClass(iTardocKumulationEClass, ITardocKumulation.class, "ITardocKumulation", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getITardocKumulation_SlaveCode(), ecorePackage.getEString(), "slaveCode", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_SlaveArt(), this.getTardocKumulationArt(), "slaveArt", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_ValidSide(), ecorePackage.getEString(), "validSide", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_Law(), ecorePackage.getEString(), "law", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_MasterCode(), ecorePackage.getEString(), "masterCode", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_MasterArt(), this.getTardocKumulationArt(), "masterArt", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getITardocKumulation_Typ(), this.getTardocKumulationTyp(), "typ", null, 0, 1, ITardocKumulation.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(iTardocKumulationEClass, ecorePackage.getEBoolean(), "isValidKumulation", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "reference", 0, 1, IS_UNIQUE, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(mandantTypeEEnum, MandantType.class, "MandantType");
		addEEnumLiteral(mandantTypeEEnum, MandantType.SPECIALIST);
		addEEnumLiteral(mandantTypeEEnum, MandantType.PRACTITIONER);
		addEEnumLiteral(mandantTypeEEnum, MandantType.TARPSYAPPRENTICE);

		// Initialize data types
		initEDataType(tardocLimitationEDataType, TardocLimitation.class, "TardocLimitation", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(tardocExclusionEDataType, TardocExclusion.class, "TardocExclusion", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(tardocKumulationArtEDataType, TardocKumulationArt.class, "TardocKumulationArt", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(tardocKumulationTypEDataType, TardocKumulationTyp.class, "TardocKumulationTyp", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //TardocPackageImpl
