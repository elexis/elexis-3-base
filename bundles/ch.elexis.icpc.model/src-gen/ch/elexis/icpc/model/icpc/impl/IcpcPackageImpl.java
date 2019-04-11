/**
 */
package ch.elexis.icpc.model.icpc.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.types.TypesPackage;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.icpc.IcpcFactory;
import ch.elexis.icpc.model.icpc.IcpcPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class IcpcPackageImpl extends EPackageImpl implements IcpcPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass icpcEncounterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass icpcEpisodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass icpcCodeEClass = null;

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
	 * @see ch.elexis.icpc.model.icpc.IcpcPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private IcpcPackageImpl() {
		super(eNS_URI, IcpcFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link IcpcPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static IcpcPackage init() {
		if (isInited) return (IcpcPackage)EPackage.Registry.INSTANCE.getEPackage(IcpcPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredIcpcPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		IcpcPackageImpl theIcpcPackage = registeredIcpcPackage instanceof IcpcPackageImpl ? (IcpcPackageImpl)registeredIcpcPackage : new IcpcPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		ModelPackage.eINSTANCE.eClass();
		TypesPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theIcpcPackage.createPackageContents();

		// Initialize created meta-data
		theIcpcPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theIcpcPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(IcpcPackage.eNS_URI, theIcpcPackage);
		return theIcpcPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIcpcEncounter() {
		return icpcEncounterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIcpcEncounter_Encounter() {
		return (EReference)icpcEncounterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIcpcEncounter_Episode() {
		return (EReference)icpcEncounterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIcpcEncounter_Proc() {
		return (EReference)icpcEncounterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIcpcEncounter_Diag() {
		return (EReference)icpcEncounterEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIcpcEncounter_Rfe() {
		return (EReference)icpcEncounterEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIcpcEpisode() {
		return icpcEpisodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcEpisode_Title() {
		return (EAttribute)icpcEpisodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcEpisode_Number() {
		return (EAttribute)icpcEpisodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcEpisode_StartDate() {
		return (EAttribute)icpcEpisodeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcEpisode_Status() {
		return (EAttribute)icpcEpisodeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIcpcEpisode_Patient() {
		return (EReference)icpcEpisodeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getIcpcEpisode_Diagnosis() {
		return (EReference)icpcEpisodeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIcpcCode() {
		return icpcCodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcCode_Icd10() {
		return (EAttribute)icpcCodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcCode_Criteria() {
		return (EAttribute)icpcCodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcCode_Inclusion() {
		return (EAttribute)icpcCodeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcCode_Exclusion() {
		return (EAttribute)icpcCodeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcCode_Note() {
		return (EAttribute)icpcCodeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIcpcCode_Consider() {
		return (EAttribute)icpcCodeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IcpcFactory getIcpcFactory() {
		return (IcpcFactory)getEFactoryInstance();
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
		icpcEncounterEClass = createEClass(ICPC_ENCOUNTER);
		createEReference(icpcEncounterEClass, ICPC_ENCOUNTER__ENCOUNTER);
		createEReference(icpcEncounterEClass, ICPC_ENCOUNTER__EPISODE);
		createEReference(icpcEncounterEClass, ICPC_ENCOUNTER__PROC);
		createEReference(icpcEncounterEClass, ICPC_ENCOUNTER__DIAG);
		createEReference(icpcEncounterEClass, ICPC_ENCOUNTER__RFE);

		icpcEpisodeEClass = createEClass(ICPC_EPISODE);
		createEAttribute(icpcEpisodeEClass, ICPC_EPISODE__TITLE);
		createEAttribute(icpcEpisodeEClass, ICPC_EPISODE__NUMBER);
		createEAttribute(icpcEpisodeEClass, ICPC_EPISODE__START_DATE);
		createEAttribute(icpcEpisodeEClass, ICPC_EPISODE__STATUS);
		createEReference(icpcEpisodeEClass, ICPC_EPISODE__PATIENT);
		createEReference(icpcEpisodeEClass, ICPC_EPISODE__DIAGNOSIS);

		icpcCodeEClass = createEClass(ICPC_CODE);
		createEAttribute(icpcCodeEClass, ICPC_CODE__ICD10);
		createEAttribute(icpcCodeEClass, ICPC_CODE__CRITERIA);
		createEAttribute(icpcCodeEClass, ICPC_CODE__INCLUSION);
		createEAttribute(icpcCodeEClass, ICPC_CODE__EXCLUSION);
		createEAttribute(icpcCodeEClass, ICPC_CODE__NOTE);
		createEAttribute(icpcCodeEClass, ICPC_CODE__CONSIDER);
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

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		icpcEncounterEClass.getESuperTypes().add(theModelPackage.getIdentifiable());
		icpcEncounterEClass.getESuperTypes().add(theModelPackage.getDeleteable());
		icpcEpisodeEClass.getESuperTypes().add(theModelPackage.getWithExtInfo());
		icpcEpisodeEClass.getESuperTypes().add(theModelPackage.getDeleteable());
		icpcEpisodeEClass.getESuperTypes().add(theModelPackage.getIdentifiable());
		icpcCodeEClass.getESuperTypes().add(theModelPackage.getIDiagnosisTree());

		// Initialize classes and features; add operations and parameters
		initEClass(icpcEncounterEClass, IcpcEncounter.class, "IcpcEncounter", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getIcpcEncounter_Encounter(), theModelPackage.getIEncounter(), null, "encounter", null, 0, 1, IcpcEncounter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIcpcEncounter_Episode(), this.getIcpcEpisode(), null, "episode", null, 0, 1, IcpcEncounter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIcpcEncounter_Proc(), this.getIcpcCode(), null, "proc", null, 0, 1, IcpcEncounter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIcpcEncounter_Diag(), this.getIcpcCode(), null, "diag", null, 0, 1, IcpcEncounter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIcpcEncounter_Rfe(), this.getIcpcCode(), null, "rfe", null, 0, 1, IcpcEncounter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(icpcEpisodeEClass, IcpcEpisode.class, "IcpcEpisode", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIcpcEpisode_Title(), ecorePackage.getEString(), "title", null, 0, 1, IcpcEpisode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcEpisode_Number(), ecorePackage.getEString(), "number", null, 0, 1, IcpcEpisode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcEpisode_StartDate(), ecorePackage.getEString(), "startDate", null, 0, 1, IcpcEpisode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcEpisode_Status(), ecorePackage.getEInt(), "status", null, 0, 1, IcpcEpisode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIcpcEpisode_Patient(), theModelPackage.getIPatient(), null, "patient", null, 0, 1, IcpcEpisode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIcpcEpisode_Diagnosis(), theModelPackage.getIDiagnosis(), null, "diagnosis", null, 0, -1, IcpcEpisode.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(icpcEpisodeEClass, null, "addDiagnosis", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theModelPackage.getIDiagnosis(), "diagnosis", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(icpcEpisodeEClass, null, "removeDiagnosis", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theModelPackage.getIDiagnosis(), "diagnosis", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(icpcCodeEClass, IcpcCode.class, "IcpcCode", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIcpcCode_Icd10(), ecorePackage.getEString(), "icd10", null, 0, 1, IcpcCode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcCode_Criteria(), ecorePackage.getEString(), "criteria", null, 0, 1, IcpcCode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcCode_Inclusion(), ecorePackage.getEString(), "inclusion", null, 0, 1, IcpcCode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcCode_Exclusion(), ecorePackage.getEString(), "exclusion", null, 0, 1, IcpcCode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcCode_Note(), ecorePackage.getEString(), "note", null, 0, 1, IcpcCode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIcpcCode_Consider(), ecorePackage.getEString(), "consider", null, 0, 1, IcpcCode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://elexis.info/jpa/entity/attribute/mapping
		createMappingAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://elexis.info/jpa/entity/attribute/mapping</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createMappingAnnotations() {
		String source = "http://elexis.info/jpa/entity/attribute/mapping";
		addAnnotation
		  (getIcpcEpisode_Patient(),
		   source,
		   new String[] {
			   "attributeName", "patientKontakt"
		   });
	}

} //IcpcPackageImpl
