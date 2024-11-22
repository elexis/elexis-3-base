package ch.elexis.mednet.webapi.core.constants;

public class FHIRConstants {

	// Profile
	public static final String PROFILE_ALLERGY_INTOLERANCE = "https://mednet.swiss/fhir/StructureDefinition/mni-allergyIntolerance";

	public static final String PROFILE_OBSERVATION_ANNOTATION = "https://mednet.swiss/fhir/StructureDefinition/mni-obs-annotation";

	public static final String PROFILE_COMPOSITION = "https://mednet.swiss/fhir/StructureDefinition/mni-composition";

	public static final String PROFILE_PATIENT_OVERVIEW_COMPOSITION = "https://mednet.swiss/fhir/StructureDefinition/mni-patientOverview-composition";

	public static final String PROFILE_PATIENT = "https://mednet.swiss/fhir/StructureDefinition/mni-patient";

	public static final String PROFILE_PRACTITIONER = "https://mednet.swiss/fhir/StructureDefinition/mni-practitioner";

	public static final String PROFILE_PRACTITIONER_ROLE = "https://mednet.swiss/fhir/StructureDefinition/mni-practitionerRole";

	public static final String PROFILE_ORGANIZATION = "https://mednet.swiss/fhir/StructureDefinition/mni-organization";

	public static final String PROFILE_DEVICE = "https://mednet.swiss/fhir/StructureDefinition/mni-device";

	public static final String PROFILE_COVERAGE = "https://mednet.swiss/fhir/StructureDefinition/mni-coverage";

	public static final String PROFILE_DOCUMENT_REFERENCE = "https://mednet.swiss/fhir/StructureDefinition/mni-documentReference";

	public static final String PROFILE_CONDITION = "https://mednet.swiss/fhir/StructureDefinition/mni-condition";

	public static final String PROFILE_FAMALY_MEMBER_HISTORY = "https://mednet.swiss/fhir/StructureDefinition/mni-familyMemberHistory";

	public static final String PROFILE_MEDICATION = "https://mednet.swiss/fhir/StructureDefinition/mni-medication";

	public static final String PROFILE_MEDICATION_STATEMENT = "https://mednet.swiss/fhir/StructureDefinition/mni-medicationStatement";

	public static final String PROFILE_PROCEDURE = "https://mednet.swiss/fhir/StructureDefinition/mni-procedure";

	public static final String PROFILE_RISK_FACTOR_OBSERVATION = "https://mednet.swiss/fhir/StructureDefinition/mni-obs-riskFactor";

	// Coding systems
	public static final String GTIN_SYSTEM = "urn:oid:2.51.1.1";
	public static final String PHARMACODE_SYSTEM = "urn:oid:2.16.756.5.30.2.6.1";
	public static final String PRODUCT_NUMBER_SYSTEM = "https://mednet.swiss/fhir/productNumber";
	public static final String CLINICAL_STATUS_SYSTEM = "http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical";
	public static final String SNOMED_SYSTEM = "http://snomed.info/sct";
	public static final String LOINC_SYSTEM = "http://loinc.org";
	public static final String ESANITA_SYSTEM = "https://www.esanita.ch";
	public static final String CONDITION_CLINICAL_SYSTEM = "http://terminology.hl7.org/CodeSystem/condition-clinical";
	public static final String ROLE_CODE_SYSTEM = "http://terminology.hl7.org/CodeSystem/v3-RoleCode";
	public static final String ROLE_CODE_SYSTEM_V2 = "http://terminology.hl7.org/CodeSystem/v2-0131";
	public static final String GLN_SYSTEM = "www.xid.ch/id/ean";
	public static final String GLN_IDENTIFIER = "urn:oid:2.51.1.3";
	public static final String ZSR_SYSTEM = "www.xid.ch/id/ksk";
	public static final String ZSR_IDENTIFIER = "urn:oid:2.16.756.5.30.1.123.100.2.1.1";

	// Identifier systems
	public static final String IDENTIFIER_SYSTEM = "urn:ietf:rfc:3986";
	public static final String UUID_PREFIX = "urn:uuid:";

	// Clinical status codes
	public static final String CLINICAL_STATUS_ACTIVE_CODE = "active";
	public static final String CLINICAL_STATUS_ACTIVE_DISPLAY = "Active";

	// Annotation constants
	public static final String ANNOTATION_COMMENT_CODE = "48767-8";
	public static final String ANNOTATION_COMMENT_DISPLAY = "Annotation comment [Interpretation] Narrative";
	public static final String CATEGORY_CONSULTATION = "Consultation";

	// Allergy intolerance types
	public static final String ALLERGIC_DISORDER_CODE = "781474001";
	public static final String ALLERGIC_DISORDER_DISPLAY = "Allergic disorder (disorder)";

	// Observation Category system and codes
	public static final String OBSERVATION_CATEGORY_SYSTEM = "http://terminology.hl7.org/CodeSystem/observation-category";
	public static final String SOCIAL_HISTORY_CODE_CAT = "social-history";
	public static final String SOCIAL_HISTORY_DISPLAY_CAT = "Social History";

	// Composition section titles
	public static final String ALLERGIES_SECTION_TITLE = "Allergies section";
	public static final String INSURANCE_DATA_SECTION_TITLE = "Insurance data section";
	public static final String CONDITIONS_SECTION_TITLE = "Problems section";
	public static final String MEDICATIONS_SECTION_TITLE = "Medication Summary section";
	public static final String PROCEDURES_SECTION_TITLE = "Procedures section";
	public static final String RESULTS_SECTION_TITLE = "Results section";
	public static final String SOCIAL_HISTORY_SECTION_TITLE = "Social history section";
	public static final String ANNOTATIONS_SECTION_TITLE = "Comment";
	public static final String DOCUMENTS_SECTION_TITLE = "Documents section";
	public static final String PATIENT_OVERVIEW_SECTION_TITLE = "PatientOverview";

	// LOINC codes and displays
	public static final String ALLERGIES_CODE = "48765-2";
	public static final String ALLERGIES_DISPLAY = "Allergies and adverse reactions Document";

	public static final String PRIMARY_INSURANCE_CODE = "76437-3";
	public static final String PRIMARY_INSURANCE_DISPLAY = "Primary insurance";

	public static final String CONDITIONS_CODE = "11450-4";
	public static final String CONDITIONS_DISPLAY = "Problem list - Reported";

	public static final String MEDICATIONS_CODE = "10160-0";
	public static final String MEDICATIONS_DISPLAY = "History of Medication use Narrative";

	public static final String PROCEDURES_CODE = "47519-4";
	public static final String PROCEDURES_DISPLAY = "History of Procedures Document";

	public static final String PROCEDURE_CODE = "71388002";
	public static final String PROCEDURE_DISPLAY = "Procedure";

	public static final String RESULTS_CODE = "30954-2";
	public static final String RESULTS_DISPLAY = "Relevant diagnostic tests/laboratory data Narrative";

	public static final String SOCIAL_HISTORY_CODE = "29762-2";
	public static final String SOCIAL_HISTORY_DISPLAY = "Social history Narrative";

	public static final String ANNOTATIONS_CODE = "48767-8";
	public static final String ANNOTATIONS_DISPLAY = "Annotation comment [Interpretation] Narrative";

	public static final String RISK_FACTOR_CODE = "80943009";
	public static final String RISK_FACTOR_DISPLAY = "Risk Factor";

	public static final String RISK_ASSESSMENT_CODE = "225338004";
	public static final String RISK_ASSESSMENT_DISPLAY = "Risk assessment";

	public static final String DOCUMENTS_CODE = "51899-3";
	public static final String DOCUMENTS_DISPLAY = "Details Document";

	public static final String DEVICE_MANUFACTURER = "Medelexis.ch";
	public static final String DEVICE_MANUFACTURER_NAME = "Medelexis";

	// Nested class for keys related to FHIR requests
	public static class FHIRKeys {
		public static final String APPLIKATION_TYP = "application/pdf";
		public static final String CUSTOMER_ID = "customerId";
		public static final String PROVIDER_ID = "providerId";
		public static final String FORM_ID = "formId";
		public static final String ONLY_ONE_TAB = "onlyOneTab";
		public static final String PRACTITIONER_GP_ID = "Stammarzt_";
	}
}
