package ch.elexis.mednet.webapi.core.constants;

public class FHIRConstants {

	// Profile
	public static final String PROFILE_ALLERGY_INTOLERANCE = "https://mednet.swiss/fhir/StructureDefinition/mni-allergyIntolerance"; //$NON-NLS-1$

	public static final String PROFILE_OBSERVATION_ANNOTATION = "https://mednet.swiss/fhir/StructureDefinition/mni-obs-annotation"; //$NON-NLS-1$

	public static final String PROFILE_COMPOSITION = "https://mednet.swiss/fhir/StructureDefinition/mni-composition"; //$NON-NLS-1$

	public static final String PROFILE_PATIENT_OVERVIEW_COMPOSITION = "https://mednet.swiss/fhir/StructureDefinition/mni-patientOverview-composition"; //$NON-NLS-1$

	public static final String PROFILE_PATIENT = "https://mednet.swiss/fhir/StructureDefinition/mni-patient"; //$NON-NLS-1$

	public static final String PROFILE_PRACTITIONER = "https://mednet.swiss/fhir/StructureDefinition/mni-practitioner"; //$NON-NLS-1$

	public static final String PROFILE_PRACTITIONER_ROLE = "https://mednet.swiss/fhir/StructureDefinition/mni-practitionerRole"; //$NON-NLS-1$

	public static final String PROFILE_ORGANIZATION = "https://mednet.swiss/fhir/StructureDefinition/mni-organization"; //$NON-NLS-1$

	public static final String PROFILE_DEVICE = "https://mednet.swiss/fhir/StructureDefinition/mni-device"; //$NON-NLS-1$

	public static final String PROFILE_COVERAGE = "https://mednet.swiss/fhir/StructureDefinition/mni-coverage"; //$NON-NLS-1$

	public static final String PROFILE_DOCUMENT_REFERENCE = "https://mednet.swiss/fhir/StructureDefinition/mni-documentReference"; //$NON-NLS-1$

	public static final String PROFILE_CONDITION = "https://mednet.swiss/fhir/StructureDefinition/mni-condition"; //$NON-NLS-1$

	public static final String PROFILE_FAMALY_MEMBER_HISTORY = "https://mednet.swiss/fhir/StructureDefinition/mni-familyMemberHistory"; //$NON-NLS-1$

	public static final String PROFILE_MEDICATION = "https://mednet.swiss/fhir/StructureDefinition/mni-medication"; //$NON-NLS-1$

	public static final String PROFILE_MEDICATION_STATEMENT = "https://mednet.swiss/fhir/StructureDefinition/mni-medicationStatement"; //$NON-NLS-1$

	public static final String PROFILE_PROCEDURE = "https://mednet.swiss/fhir/StructureDefinition/mni-procedure"; //$NON-NLS-1$

	public static final String PROFILE_RISK_FACTOR_OBSERVATION = "https://mednet.swiss/fhir/StructureDefinition/mni-obs-riskFactor"; //$NON-NLS-1$

	// Coding systems
	public static final String GTIN_SYSTEM = "urn:oid:2.51.1.1"; //$NON-NLS-1$
	public static final String PHARMACODE_SYSTEM = "urn:oid:2.16.756.5.30.2.6.1"; //$NON-NLS-1$
	public static final String PRODUCT_NUMBER_SYSTEM = "https://mednet.swiss/fhir/productNumber"; //$NON-NLS-1$
	public static final String CLINICAL_STATUS_SYSTEM = "http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical"; //$NON-NLS-1$
	public static final String SNOMED_SYSTEM = "http://snomed.info/sct"; //$NON-NLS-1$
	public static final String LOINC_SYSTEM = "http://loinc.org"; //$NON-NLS-1$
	public static final String ESANITA_SYSTEM = "https://www.esanita.ch"; //$NON-NLS-1$
	public static final String CONDITION_CLINICAL_SYSTEM = "http://terminology.hl7.org/CodeSystem/condition-clinical"; //$NON-NLS-1$
	public static final String ROLE_CODE_SYSTEM = "http://terminology.hl7.org/CodeSystem/v3-RoleCode"; //$NON-NLS-1$
	public static final String ROLE_CODE_SYSTEM_V2 = "http://terminology.hl7.org/CodeSystem/v2-0131"; //$NON-NLS-1$
	public static final String GLN_SYSTEM = "www.xid.ch/id/ean"; //$NON-NLS-1$
	public static final String GLN_IDENTIFIER = "urn:oid:2.51.1.3"; //$NON-NLS-1$
	public static final String ZSR_SYSTEM = "www.xid.ch/id/ksk"; //$NON-NLS-1$
	public static final String ZSR_IDENTIFIER = "urn:oid:2.16.756.5.30.1.123.100.2.1.1"; //$NON-NLS-1$

	// Identifier systems
	public static final String IDENTIFIER_SYSTEM = "urn:ietf:rfc:3986"; //$NON-NLS-1$
	public static final String UUID_PREFIX = "urn:uuid:"; //$NON-NLS-1$

	// Clinical status codes
	public static final String CLINICAL_STATUS_ACTIVE_CODE = "active"; //$NON-NLS-1$
	public static final String CLINICAL_STATUS_ACTIVE_DISPLAY = "Active"; //$NON-NLS-1$

	// Annotation constants
	public static final String ANNOTATION_COMMENT_CODE = "48767-8"; //$NON-NLS-1$
	public static final String ANNOTATION_COMMENT_DISPLAY = "Annotation comment [Interpretation] Narrative"; //$NON-NLS-1$
	public static final String CATEGORY_CONSULTATION = "Consultation"; //$NON-NLS-1$

	// Allergy intolerance types
	public static final String ALLERGIC_DISORDER_CODE = "781474001"; //$NON-NLS-1$
	public static final String ALLERGIC_DISORDER_DISPLAY = "Allergic disorder (disorder)"; //$NON-NLS-1$

	// Observation Category system and codes
	public static final String OBSERVATION_CATEGORY_SYSTEM = "http://terminology.hl7.org/CodeSystem/observation-category"; //$NON-NLS-1$
	public static final String SOCIAL_HISTORY_CODE_CAT = "social-history"; //$NON-NLS-1$
	public static final String SOCIAL_HISTORY_DISPLAY_CAT = "Social History"; //$NON-NLS-1$

	// Composition section titles
	public static final String ALLERGIES_SECTION_TITLE = "Allergies section"; //$NON-NLS-1$
	public static final String INSURANCE_DATA_SECTION_TITLE = "Insurance data section"; //$NON-NLS-1$
	public static final String CONDITIONS_SECTION_TITLE = "Problems section"; //$NON-NLS-1$
	public static final String MEDICATIONS_SECTION_TITLE = "Medication Summary section"; //$NON-NLS-1$
	public static final String PROCEDURES_SECTION_TITLE = "Procedures section"; //$NON-NLS-1$
	public static final String RESULTS_SECTION_TITLE = "Results section"; //$NON-NLS-1$
	public static final String SOCIAL_HISTORY_SECTION_TITLE = "Social history section"; //$NON-NLS-1$
	public static final String ANNOTATIONS_SECTION_TITLE = "Comment"; //$NON-NLS-1$
	public static final String DOCUMENTS_SECTION_TITLE = "Documents section"; //$NON-NLS-1$
	public static final String PATIENT_OVERVIEW_SECTION_TITLE = "PatientOverview"; //$NON-NLS-1$

	// LOINC codes and displays
	public static final String ALLERGIES_CODE = "48765-2"; //$NON-NLS-1$
	public static final String ALLERGIES_DISPLAY = "Allergies and adverse reactions Document"; //$NON-NLS-1$

	public static final String PRIMARY_INSURANCE_CODE = "76437-3"; //$NON-NLS-1$
	public static final String PRIMARY_INSURANCE_DISPLAY = "Primary insurance"; //$NON-NLS-1$

	public static final String CONDITIONS_CODE = "11450-4"; //$NON-NLS-1$
	public static final String CONDITIONS_DISPLAY = "Problem list - Reported"; //$NON-NLS-1$

	public static final String MEDICATIONS_CODE = "10160-0"; //$NON-NLS-1$
	public static final String MEDICATIONS_DISPLAY = "History of Medication use Narrative"; //$NON-NLS-1$

	public static final String PROCEDURES_CODE = "47519-4"; //$NON-NLS-1$
	public static final String PROCEDURES_DISPLAY = "History of Procedures Document"; //$NON-NLS-1$

	public static final String PROCEDURE_CODE = "71388002"; //$NON-NLS-1$
	public static final String PROCEDURE_DISPLAY = "Procedure"; //$NON-NLS-1$

	public static final String RESULTS_CODE = "30954-2"; //$NON-NLS-1$
	public static final String RESULTS_DISPLAY = "Relevant diagnostic tests/laboratory data Narrative"; //$NON-NLS-1$

	public static final String SOCIAL_HISTORY_CODE = "29762-2"; //$NON-NLS-1$
	public static final String SOCIAL_HISTORY_DISPLAY = "Social history Narrative"; //$NON-NLS-1$

	public static final String ANNOTATIONS_CODE = "48767-8"; //$NON-NLS-1$
	public static final String ANNOTATIONS_DISPLAY = "Annotation comment [Interpretation] Narrative"; //$NON-NLS-1$

	public static final String RISK_FACTOR_CODE = "80943009"; //$NON-NLS-1$
	public static final String RISK_FACTOR_DISPLAY = "Risk Factor"; //$NON-NLS-1$

	public static final String RISK_ASSESSMENT_CODE = "225338004"; //$NON-NLS-1$
	public static final String RISK_ASSESSMENT_DISPLAY = "Risk assessment"; //$NON-NLS-1$

	public static final String DOCUMENTS_CODE = "51899-3"; //$NON-NLS-1$
	public static final String DOCUMENTS_DISPLAY = "Details Document"; //$NON-NLS-1$

	public static final String DEVICE_MANUFACTURER = "Medelexis.ch"; //$NON-NLS-1$
	public static final String DEVICE_MANUFACTURER_NAME = "Medelexis"; //$NON-NLS-1$

	public static final String AHV_IDENTIFIER = "urn:oid:2.16.756.5.32"; //$NON-NLS-1$
	public static final String COVERAGE_TYPE_SYSTEM = "http://fhir.ch/ig/ch-orf/CodeSystem/ch-orf-cs-coveragetype"; //$NON-NLS-1$

	// Nested class for keys related to FHIR requests
	public static class FHIRKeys {
		public static final String APPLIKATION_TYP = "application/pdf"; //$NON-NLS-1$
		public static final String CUSTOMER_ID = "customerId"; //$NON-NLS-1$
		public static final String PROVIDER_ID = "providerId"; //$NON-NLS-1$
		public static final String FORM_ID = "formId"; //$NON-NLS-1$
		public static final String ONLY_ONE_TAB = "onlyOneTab"; //$NON-NLS-1$
		public static final String PRACTITIONER_GP_ID = "Stammarzt_"; //$NON-NLS-1$
	}
}
