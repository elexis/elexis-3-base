/**
 */
package ch.elexis.base.ch.labortarif.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif.LabortarifFactory;
import ch.elexis.base.ch.labortarif.LabortarifPackage;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.types.TypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LabortarifPackageImpl extends EPackageImpl implements LabortarifPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iLaborLeistungEClass = null;

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
	 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private LabortarifPackageImpl() {
		super(eNS_URI, LabortarifFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link LabortarifPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static LabortarifPackage init() {
		if (isInited) return (LabortarifPackage)EPackage.Registry.INSTANCE.getEPackage(LabortarifPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredLabortarifPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		LabortarifPackageImpl theLabortarifPackage = registeredLabortarifPackage instanceof LabortarifPackageImpl ? (LabortarifPackageImpl)registeredLabortarifPackage : new LabortarifPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		ModelPackage.eINSTANCE.eClass();
		TypesPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theLabortarifPackage.createPackageContents();

		// Initialize created meta-data
		theLabortarifPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theLabortarifPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(LabortarifPackage.eNS_URI, theLabortarifPackage);
		return theLabortarifPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getILaborLeistung() {
		return iLaborLeistungEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getILaborLeistung_Points() {
		return (EAttribute)iLaborLeistungEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getILaborLeistung_ValidFrom() {
		return (EAttribute)iLaborLeistungEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getILaborLeistung_ValidTo() {
		return (EAttribute)iLaborLeistungEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getILaborLeistung_Chapter() {
		return (EAttribute)iLaborLeistungEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getILaborLeistung_Speciality() {
		return (EAttribute)iLaborLeistungEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getILaborLeistung_Limitation() {
		return (EAttribute)iLaborLeistungEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LabortarifFactory getLabortarifFactory() {
		return (LabortarifFactory)getEFactoryInstance();
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
		iLaborLeistungEClass = createEClass(ILABOR_LEISTUNG);
		createEAttribute(iLaborLeistungEClass, ILABOR_LEISTUNG__POINTS);
		createEAttribute(iLaborLeistungEClass, ILABOR_LEISTUNG__VALID_FROM);
		createEAttribute(iLaborLeistungEClass, ILABOR_LEISTUNG__VALID_TO);
		createEAttribute(iLaborLeistungEClass, ILABOR_LEISTUNG__CHAPTER);
		createEAttribute(iLaborLeistungEClass, ILABOR_LEISTUNG__SPECIALITY);
		createEAttribute(iLaborLeistungEClass, ILABOR_LEISTUNG__LIMITATION);
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
		iLaborLeistungEClass.getESuperTypes().add(theModelPackage.getIBillable());

		// Initialize classes and features; add operations and parameters
		initEClass(iLaborLeistungEClass, ILaborLeistung.class, "ILaborLeistung", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getILaborLeistung_Points(), ecorePackage.getEInt(), "points", null, 0, 1, ILaborLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getILaborLeistung_ValidFrom(), theTypesPackage.getLocalDate(), "validFrom", null, 0, 1, ILaborLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getILaborLeistung_ValidTo(), theTypesPackage.getLocalDate(), "validTo", null, 0, 1, ILaborLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getILaborLeistung_Chapter(), ecorePackage.getEString(), "chapter", null, 0, 1, ILaborLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getILaborLeistung_Speciality(), ecorePackage.getEString(), "speciality", null, 0, 1, ILaborLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getILaborLeistung_Limitation(), ecorePackage.getEString(), "limitation", null, 0, 1, ILaborLeistung.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(iLaborLeistungEClass, ecorePackage.getEBoolean(), "isValidOn", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theTypesPackage.getLocalDate(), "date", 0, 1, IS_UNIQUE, IS_ORDERED);

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
		  (getILaborLeistung_Points(),
		   source,
		   new String[] {
			   "attributeName", "tp"
		   });
		addAnnotation
		  (getILaborLeistung_ValidFrom(),
		   source,
		   new String[] {
			   "attributeName", "gueltigVon"
		   });
		addAnnotation
		  (getILaborLeistung_ValidTo(),
		   source,
		   new String[] {
			   "attributeName", "gueltigBis"
		   });
		addAnnotation
		  (getILaborLeistung_Speciality(),
		   source,
		   new String[] {
			   "attributeName", "fachbereich"
		   });
		addAnnotation
		  (getILaborLeistung_Limitation(),
		   source,
		   new String[] {
			   "attributeName", "limitatio"
		   });
	}

} //LabortarifPackageImpl
