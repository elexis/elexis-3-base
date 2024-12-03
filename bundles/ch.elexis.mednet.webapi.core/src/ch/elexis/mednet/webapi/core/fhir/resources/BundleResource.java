package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IRelatedContact;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class BundleResource {

	private static final Logger logger = LoggerFactory.getLogger(BundleResource.class);

	public static Bundle createPatientOverviewBundle(Patient patient, IPatient sourcePatient,
			List<IDocument> selectedDocuments, boolean isEpdSelected, FhirResourceFactory resourceFactory,
			IModelService coreModelService, IFindingsService findingsService) {
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.DOCUMENT);
		String bundleId = UUID.randomUUID().toString();
		bundle.setId(bundleId);
		bundle.getIdentifier().setSystem(FHIRConstants.IDENTIFIER_SYSTEM)
				.setValue(FHIRConstants.UUID_PREFIX + bundleId);
		bundle.setTimestamp(new Date());

		List<BundleEntryComponent> bundleEntries = new ArrayList<>();

		List<Reference> organizationReferences = new ArrayList<>();
		List<BundleEntryComponent> organizationEntries = new ArrayList<>();
		List<IRelatedContact> relatedContacts = sourcePatient.getRelatedContacts();

		for (IRelatedContact relatedContact : relatedContacts) {
			IContact otherContact = relatedContact.getOtherContact();

			if (otherContact != null && otherContact.isOrganization()) {
				String organizationId = UUID.nameUUIDFromBytes(otherContact.getId().getBytes()).toString();
				String organizationFullUrl = FHIRConstants.UUID_PREFIX + organizationId;

				Organization organization = OrganizationResource.createOrganization(otherContact, resourceFactory);
				organization.setId(organizationId);

				BundleEntryComponent organizationEntry = new BundleEntryComponent();
				organizationEntry.setFullUrl(organizationFullUrl);
				organizationEntry.setResource(organization);
				organizationEntries.add(organizationEntry);

				organizationReferences.add(new Reference(organizationFullUrl));
			}
		}

		Composition composition = CompositionResource.createComposition(patient);
		String compositionFullUrl = FHIRConstants.UUID_PREFIX + composition.getId();

		BundleEntryComponent compositionEntry = new BundleEntryComponent();
		compositionEntry.setFullUrl(compositionFullUrl);
		compositionEntry.setResource(composition);
		bundleEntries.add(compositionEntry);

		bundleEntries.addAll(organizationEntries);

		String patientFullUrl = FHIRConstants.UUID_PREFIX + patient.getIdElement().getIdPart();
		BundleEntryComponent patientEntry = new BundleEntryComponent();
		patientEntry.setFullUrl(patientFullUrl);
		patientEntry.setResource(patient);
		bundleEntries.add(patientEntry);

		String gpId = (String) sourcePatient.getExtInfo(FHIRConstants.FHIRKeys.PRACTITIONER_GP_ID);
		if (gpId != null && !gpId.isEmpty()) {
			Optional<IMandator> gpMandatorOptional = coreModelService.load(gpId, IMandator.class);

			IMandator generalPractitionerMandator = gpMandatorOptional.orElse(null);
			if (generalPractitionerMandator != null) {
				Practitioner gpPractitioner = resourceFactory.getResource(generalPractitionerMandator, IMandator.class,
						Practitioner.class);
				if (gpPractitioner != null) {
					gpPractitioner = PractitionerResource.adjustPractitioner(gpPractitioner);
					String gpPractitionerFullUrl = FHIRConstants.UUID_PREFIX + gpPractitioner.getId();
					BundleEntryComponent practitionerEntry = new BundleEntryComponent();
					practitionerEntry.setFullUrl(gpPractitionerFullUrl);
					practitionerEntry.setResource(gpPractitioner);
					bundleEntries.add(practitionerEntry);
					Optional<IUser> gpUserOptional = findMandatorUser(gpMandatorOptional);
					if (gpUserOptional.isPresent()) {
						IUser gpUser = gpUserOptional.get();
						PractitionerRole gpPractitionerRole = new PractitionerRoleResource()
								.createPractitionerRole(gpUser, gpPractitioner, resourceFactory);
						String gpPractitionerRoleFullUrl = FHIRConstants.UUID_PREFIX + gpPractitionerRole.getId();
						BundleEntryComponent practitionerRoleEntry = new BundleEntryComponent();
						practitionerRoleEntry.setFullUrl(gpPractitionerRoleFullUrl);
						practitionerRoleEntry.setResource(gpPractitionerRole);
						bundleEntries.add(practitionerRoleEntry);
						patient.addGeneralPractitioner(new Reference(gpPractitionerRoleFullUrl));
					}
				}
			}
		}

		Device device = DeviceResource.createDevice();
		String deviceFullUrl = FHIRConstants.UUID_PREFIX + device.getId();
		BundleEntryComponent deviceEntry = new BundleEntryComponent();
		deviceEntry.setFullUrl(deviceFullUrl);
		deviceEntry.setResource(device);
		bundleEntries.add(deviceEntry);
		composition.addAuthor(new Reference(deviceFullUrl));

		// Allergies
		List<AllergyIntolerance> allergies = AllergyIntoleranceResource.createAllergies(patient, sourcePatient,
				resourceFactory);
		List<Reference> allergyReferences = new ArrayList<>();
		for (AllergyIntolerance allergy : allergies) {
			String allergyFullUrl = FHIRConstants.UUID_PREFIX + allergy.getId();
			BundleEntryComponent allergyEntry = new BundleEntryComponent();
			allergyEntry.setFullUrl(allergyFullUrl);
			allergyEntry.setResource(allergy);
			bundleEntries.add(allergyEntry);

			allergyReferences.add(new Reference(allergyFullUrl));
			allergy.setPatient(new Reference(patientFullUrl));
		}

		Composition.SectionComponent allergySection = new Composition.SectionComponent();
		allergySection.setTitle(FHIRConstants.ALLERGIES_SECTION_TITLE);
		allergySection.setCode(new CodeableConcept().addCoding(
				new Coding(FHIRConstants.LOINC_SYSTEM, FHIRConstants.ALLERGIES_CODE, FHIRConstants.ALLERGIES_DISPLAY)));
		allergySection.setEntry(allergyReferences);
		composition.addSection(allergySection);

		// Coverage (Insurance)
		ICoverage activeCoverage = getActiveCoverage(sourcePatient);
		if (activeCoverage != null) {
			IContact coveragePayor = activeCoverage.getCostBearer();

			if (coveragePayor != null && coveragePayor.isOrganization()) {
				Coverage coverage = resourceFactory.getResource(activeCoverage, ICoverage.class, Coverage.class);
				CoverageResource.toMednet(coverage, patientFullUrl);

				String coverageId = coverage.getIdElement().getIdPart();
				String coverageUUID = UUID.nameUUIDFromBytes(coverageId.getBytes()).toString();
				String coverageFullUrl = FHIRConstants.UUID_PREFIX + coverageUUID;
				coverage.setId(coverageUUID);

				BundleEntryComponent coverageEntry = new BundleEntryComponent();
				coverageEntry.setFullUrl(coverageFullUrl);
				coverageEntry.setResource(coverage);
				bundleEntries.add(coverageEntry);

				Organization organization = OrganizationResource.createOrganization(coveragePayor, resourceFactory);
				String organizationId = UUID.nameUUIDFromBytes(coveragePayor.getId().getBytes()).toString();
				String organizationFullUrl = FHIRConstants.UUID_PREFIX + organizationId;
				organization.setId(organizationId);

				BundleEntryComponent payorOrganizationEntry = new BundleEntryComponent();
				payorOrganizationEntry.setFullUrl(organizationFullUrl);
				payorOrganizationEntry.setResource(organization);
				bundleEntries.add(payorOrganizationEntry);

				List<Reference> payorList = new ArrayList<>();
				payorList.add(new Reference(organizationFullUrl));
				coverage.setPayor(payorList);

				Composition.SectionComponent coverageSection = new Composition.SectionComponent();
				coverageSection.setTitle(FHIRConstants.INSURANCE_DATA_SECTION_TITLE);
				coverageSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
						FHIRConstants.PRIMARY_INSURANCE_CODE, FHIRConstants.PRIMARY_INSURANCE_DISPLAY)));
				coverageSection.addEntry(new Reference(coverageFullUrl));

				composition.addSection(coverageSection);
			}
		}

		// Conditions
		boolean strukturDiagnose = ConfigServiceHolder.getGlobal(IMigratorService.DIAGNOSE_SETTINGS_USE_STRUCTURED,
				false);

		List<ICondition> localConditions = ConditionResource.getLocalConditions(sourcePatient);
		List<Reference> conditionReferences = new ArrayList<>();

		if (strukturDiagnose) {
			for (ICondition localCondition : localConditions) {
				Condition condition = resourceFactory.getResource(localCondition, ICondition.class, Condition.class);
				if (condition == null || !condition.hasClinicalStatus() || !condition.hasCode()) {
					condition = ConditionResource.createConditionFallback(localCondition, patient);
				}
				ConditionResource.addConditionToBundle(condition, bundleEntries, conditionReferences);
			}
		} else {
			List<Condition> conditions = ConditionResource.createConditions(patient, sourcePatient);
			for (Condition condition : conditions) {
				ConditionResource.addConditionToBundle(condition, bundleEntries, conditionReferences);
			}
		}

		Composition.SectionComponent conditionSection = new Composition.SectionComponent();
		conditionSection.setTitle(FHIRConstants.CONDITIONS_SECTION_TITLE);
		conditionSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
				FHIRConstants.CONDITIONS_CODE, FHIRConstants.CONDITIONS_DISPLAY)));
		conditionSection.setEntry(conditionReferences);
		composition.addSection(conditionSection);

		// Medications
		List<IPrescription> prescriptions = sourcePatient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION,
				EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));

		MedicationStatementResource medicationStatementResource = new MedicationStatementResource();
		List<Medication> medications = new ArrayList<>();
		List<MedicationStatement> medicationStatements = medicationStatementResource
				.createMedicationStatementsFromPrescriptions(new Reference(patientFullUrl), prescriptions,
						resourceFactory);
		List<Reference> medicationReferences = new ArrayList<>();
		for (MedicationStatement medicationStatement : medicationStatements) {
			String medicationStatementFullUrl = FHIRConstants.UUID_PREFIX + medicationStatement.getId();
			BundleEntryComponent medicationStatementEntry = new BundleEntryComponent();
			medicationStatementEntry.setFullUrl(medicationStatementFullUrl);
			medicationStatementEntry.setResource(medicationStatement);
			bundleEntries.add(medicationStatementEntry);

			medicationReferences.add(new Reference(medicationStatementFullUrl));
		}

		for (Medication medication : medications) {
			String medicationFullUrl = FHIRConstants.UUID_PREFIX + medication.getId();
			BundleEntryComponent medicationEntry = new BundleEntryComponent();
			medicationEntry.setFullUrl(medicationFullUrl);
			medicationEntry.setResource(medication);
			bundleEntries.add(medicationEntry);
		}

		Composition.SectionComponent medicationSection = new Composition.SectionComponent();
		medicationSection.setTitle(FHIRConstants.MEDICATIONS_SECTION_TITLE);
		medicationSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
				FHIRConstants.MEDICATIONS_CODE, FHIRConstants.MEDICATIONS_DISPLAY)));
		medicationSection.setEntry(medicationReferences);
		composition.addSection(medicationSection);

		// Procedures
		List<Procedure> procedures = ProcedureResource.createProcedures(new Reference(patientFullUrl), sourcePatient,
				resourceFactory);
		List<Reference> procedureReferences = new ArrayList<>();
		for (Procedure procedure : procedures) {
			String procedureFullUrl = FHIRConstants.UUID_PREFIX + procedure.getId();
			BundleEntryComponent procedureEntry = new BundleEntryComponent();
			procedureEntry.setFullUrl(procedureFullUrl);
			procedureEntry.setResource(procedure);
			bundleEntries.add(procedureEntry);

			procedureReferences.add(new Reference(procedureFullUrl));
		}

		Composition.SectionComponent procedureSection = new Composition.SectionComponent();
		procedureSection.setTitle(FHIRConstants.PROCEDURES_SECTION_TITLE);
		procedureSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
				FHIRConstants.PROCEDURES_CODE, FHIRConstants.PROCEDURES_DISPLAY)));
		procedureSection.setEntry(procedureReferences);
		composition.addSection(procedureSection);

		// Observations
		List<Observation> observations = ObservationResource.createObservations(new Reference(patientFullUrl),
				resourceFactory, findingsService);
		List<Reference> observationReferences = new ArrayList<>();
		for (Observation observation : observations) {
			String observationFullUrl = FHIRConstants.UUID_PREFIX + observation.getId();
			BundleEntryComponent observationEntry = new BundleEntryComponent();
			observationEntry.setFullUrl(observationFullUrl);
			observationEntry.setResource(observation);
			bundleEntries.add(observationEntry);

			observationReferences.add(new Reference(observationFullUrl));
		}

		Composition.SectionComponent resultsSection = new Composition.SectionComponent();
		resultsSection.setTitle(FHIRConstants.RESULTS_SECTION_TITLE);
		resultsSection.setCode(new CodeableConcept().addCoding(
				new Coding(FHIRConstants.LOINC_SYSTEM, FHIRConstants.RESULTS_CODE, FHIRConstants.RESULTS_DISPLAY)));
		resultsSection.setEntry(observationReferences);
		composition.addSection(resultsSection);

		// Risk Factors (as Observations)
		List<Observation> riskFactors = RiskFactorResource.createRiskFactors(new Reference(patientFullUrl),
				sourcePatient, resourceFactory);
		List<Reference> riskFactorReferences = new ArrayList<>();
		for (Observation riskFactor : riskFactors) {
			String riskFactorFullUrl = FHIRConstants.UUID_PREFIX + riskFactor.getId();
			BundleEntryComponent riskFactorEntry = new BundleEntryComponent();
			riskFactorEntry.setFullUrl(riskFactorFullUrl);
			riskFactorEntry.setResource(riskFactor);
			bundleEntries.add(riskFactorEntry);

			riskFactorReferences.add(new Reference(riskFactorFullUrl));
		}

		List<FamilyMemberHistory> familyHistories = FamilyMemberHistoryResource
				.createFamilyMemberHistories(new Reference(patientFullUrl), sourcePatient, findingsService,
						resourceFactory);
		List<Reference> familyHistoryReferences = new ArrayList<>();
		for (FamilyMemberHistory familyHistory : familyHistories) {
			String familyHistoryFullUrl = FHIRConstants.UUID_PREFIX + familyHistory.getId();
			BundleEntryComponent familyHistoryEntry = new BundleEntryComponent();
			familyHistoryEntry.setFullUrl(familyHistoryFullUrl);
			familyHistoryEntry.setResource(familyHistory);
			bundleEntries.add(familyHistoryEntry);

			familyHistoryReferences.add(new Reference(familyHistoryFullUrl));
		}

		Composition.SectionComponent socialHistorySection = new Composition.SectionComponent();
		socialHistorySection.setTitle(FHIRConstants.SOCIAL_HISTORY_SECTION_TITLE);
		socialHistorySection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
				FHIRConstants.SOCIAL_HISTORY_CODE, FHIRConstants.SOCIAL_HISTORY_DISPLAY)));
		socialHistorySection.setEntry(riskFactorReferences);
		socialHistorySection.getEntry().addAll(familyHistoryReferences);
		composition.addSection(socialHistorySection);

		// Annotations
		List<Observation> annotations = AnnotationResource.createAnnotations(new Reference(patientFullUrl));
		List<Reference> annotationReferences = new ArrayList<>();
		for (Observation annotation : annotations) {
			String annotationFullUrl = FHIRConstants.UUID_PREFIX + annotation.getId();
			BundleEntryComponent annotationEntry = new BundleEntryComponent();
			annotationEntry.setFullUrl(annotationFullUrl);
			annotationEntry.setResource(annotation);
			bundleEntries.add(annotationEntry);

			annotationReferences.add(new Reference(annotationFullUrl));
		}

		Composition.SectionComponent annotationsSection = new Composition.SectionComponent();
		annotationsSection.setTitle(FHIRConstants.ANNOTATIONS_SECTION_TITLE);
		annotationsSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
				FHIRConstants.ANNOTATIONS_CODE, FHIRConstants.ANNOTATIONS_DISPLAY)));
		annotationsSection.setEntry(annotationReferences);
		composition.addSection(annotationsSection);

		// Documents
		if (selectedDocuments != null) {
			List<DocumentReference> documents = DocumentResource.createDocuments(sourcePatient, patientFullUrl,
					selectedDocuments, isEpdSelected);

			List<Reference> documentReferences = new ArrayList<>();
			for (DocumentReference document : documents) {
				String documentFullUrl = FHIRConstants.UUID_PREFIX + document.getId();
				BundleEntryComponent documentEntry = new BundleEntryComponent();
				documentEntry.setFullUrl(documentFullUrl);
				documentEntry.setResource(document);
				bundleEntries.add(documentEntry);

				documentReferences.add(new Reference(documentFullUrl));
			}
			Composition.SectionComponent documentsSection = new Composition.SectionComponent();
			documentsSection.setTitle(FHIRConstants.DOCUMENTS_SECTION_TITLE);
			documentsSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
					FHIRConstants.DOCUMENTS_CODE, FHIRConstants.DOCUMENTS_DISPLAY)));
			documentsSection.setEntry(documentReferences);
			composition.addSection(documentsSection);
		}

		bundle.setEntry(bundleEntries);

		return bundle;
	}

	private static ICoverage getActiveCoverage(IPatient patient) {
		for (ICoverage coverage : patient.getCoverages()) {
			if (coverage.isOpen()) {
				return coverage;
			}
		}
		return null;
	}

	private static Optional<IUser> findMandatorUser(Optional<IMandator> mandator) {
		if (mandator.isEmpty()) {
			return Optional.empty();
		}
		IQuery<IUser> userQuery = CoreModelServiceHolder.get().getQuery(IUser.class);
		userQuery.and(ModelPackage.Literals.IUSER__ASSIGNED_CONTACT, COMPARATOR.NOT_EQUALS, null);
		return userQuery.execute().stream().filter(
				u -> u.getAssignedContact() != null && u.getAssignedContact().getId().equals(mandator.get().getId()))
				.findFirst();
	}
}