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

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IRelatedContact;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.AdjustBundleIdentifiers;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class BundleResource {

	private static java.util.Map<String, String> idToFullUrl;

	public static Bundle createPatientOverviewBundle(Patient patient, IPatient sourcePatient,
			List<IDocument> selectedDocuments, boolean isEpdSelected, FhirResourceFactory resourceFactory,
			IModelService coreModelService, IFindingsService findingsService) {
		idToFullUrl = new java.util.HashMap<>();
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.DOCUMENT);
		String bundleId = UUID.randomUUID().toString();
		bundle.setId(bundleId);
		bundle.getIdentifier().setSystem(FHIRConstants.IDENTIFIER_SYSTEM)
				.setValue(FHIRConstants.UUID_PREFIX + bundleId);
		bundle.setTimestamp(new Date());

		List<BundleEntryComponent> bundleEntries = new ArrayList<>();
		Composition composition = CompositionResource.createComposition(patient);
		addEntryAndIndex(bundleEntries, composition);
		String patientFullUrl = addEntryAndIndex(bundleEntries, patient);
		composition.setSubject(new Reference(patientFullUrl));
		List<IRelatedContact> relatedContacts = sourcePatient.getRelatedContacts();

		for (IRelatedContact relatedContact : relatedContacts) {
			IContact otherContact = relatedContact.getOtherContact();

			if (otherContact != null && otherContact.isOrganization()) {
				Organization organization = OrganizationResource.createOrganization(otherContact, resourceFactory);
				addEntryAndIndex(bundleEntries, organization);
			}
		}

		String gpId = (String) sourcePatient.getExtInfo(FHIRConstants.FHIRKeys.PRACTITIONER_GP_ID);
		if (gpId != null && !gpId.isEmpty()) {
			Optional<IPerson> gpMandatorOptional = coreModelService.load(gpId, IPerson.class);

			IPerson generalPractitionerMandator = gpMandatorOptional.orElse(null);
			if (generalPractitionerMandator != null) {
				Practitioner gpPractitioner = resourceFactory.getResource(generalPractitionerMandator, IPerson.class,
						Practitioner.class);
				if (gpPractitioner != null) {
					gpPractitioner = PractitionerResource.adjustPractitioner(gpPractitioner);
					addEntryAndIndex(bundleEntries, gpPractitioner);

					Optional<IUser> gpUserOptional = findMandatorUser(gpMandatorOptional);
					if (gpUserOptional.isPresent()) {
						IUser gpUser = gpUserOptional.get();
						PractitionerRole gpPractitionerRole = new PractitionerRoleResource()
								.createPractitionerRole(gpUser, gpPractitioner, resourceFactory);
						String gpPractitionerRoleFullUrl = addEntryAndIndex(bundleEntries, gpPractitionerRole);
						patient.addGeneralPractitioner(new Reference(gpPractitionerRoleFullUrl));
					}
				}
			}
		}

		Device device = DeviceResource.createDevice();
		String deviceFullUrl = addEntryAndIndex(bundleEntries, device);
		composition.addAuthor(new Reference(deviceFullUrl));

		// Allergies
		List<AllergyIntolerance> allergies = AllergyIntoleranceResource.createAllergies(patient, sourcePatient,
				resourceFactory);
		List<Reference> allergyReferences = new ArrayList<>();
		for (AllergyIntolerance allergy : allergies) {
			allergy.setPatient(new Reference(patientFullUrl));
			String allergyFullUrl = addEntryAndIndex(bundleEntries, allergy);
			allergyReferences.add(new Reference(allergyFullUrl));
		}

		Composition.SectionComponent allergySection = new Composition.SectionComponent();
		allergySection.setTitle(FHIRConstants.ALLERGIES_SECTION_TITLE);
		allergySection.setCode(new CodeableConcept().addCoding(
				new Coding(FHIRConstants.LOINC_SYSTEM, FHIRConstants.ALLERGIES_CODE, FHIRConstants.ALLERGIES_DISPLAY)));
		allergySection.setEntry(allergyReferences);
		composition.addSection(allergySection);

		Optional<IEncounter> encounterOpt = ContextServiceHolder.get().getTyped(IEncounter.class);
		if (encounterOpt.isPresent()) {
			ICoverage encounterCoverage = encounterOpt.get().getCoverage();
			if (encounterCoverage != null && encounterCoverage.isOpen()) {
				IContact coveragePayor = encounterCoverage.getCostBearer();
				if (coveragePayor != null && coveragePayor.isOrganization()) {
					Coverage coverage = resourceFactory.getResource(encounterCoverage, ICoverage.class, Coverage.class);
					CoverageResource.toMednet(coverage, patientFullUrl);
					BillingLaw law = encounterCoverage.getBillingSystem().getLaw();
					String lawCode = mapLawToCode(law); // s.u.
					coverage.setType(
							new CodeableConcept().addCoding(new Coding().setSystem(FHIRConstants.COVERAGE_TYPE_SYSTEM)
									.setCode(lawCode).setDisplay(AdjustBundleIdentifiers.getCoverageDisplay(lawCode))));
					String coverageFullUrl = addEntryAndIndex(bundleEntries, coverage);

					Organization organization = OrganizationResource.createOrganization(coveragePayor, resourceFactory);
					String orgFullUrl = addEntryAndIndex(bundleEntries, organization);

					coverage.setPayor(List.of(new Reference(orgFullUrl)));

					Composition.SectionComponent coverageSection = new Composition.SectionComponent();
					coverageSection.setTitle(FHIRConstants.INSURANCE_DATA_SECTION_TITLE);
					coverageSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
							FHIRConstants.PRIMARY_INSURANCE_CODE, FHIRConstants.PRIMARY_INSURANCE_DISPLAY)));
					coverageSection.addEntry(new Reference(coverageFullUrl));
					composition.addSection(coverageSection);
				}
			}
		}

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
				condition.setSubject(new Reference(patientFullUrl));
				String condFullUrl = addEntryAndIndex(bundleEntries, condition);
				conditionReferences.add(new Reference(condFullUrl));
			}
		} else {
			List<Condition> conditions = ConditionResource.createConditions(patient, sourcePatient);
			for (Condition condition : conditions) {
				String condFullUrl = addEntryAndIndex(bundleEntries, condition);
				conditionReferences.add(new Reference(condFullUrl));
			}
		}

		Composition.SectionComponent conditionSection = new Composition.SectionComponent();
		conditionSection.setTitle(FHIRConstants.CONDITIONS_SECTION_TITLE);
		conditionSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
				FHIRConstants.CONDITIONS_CODE, FHIRConstants.CONDITIONS_DISPLAY)));
		conditionSection.setEntry(conditionReferences);
		composition.addSection(conditionSection);

		List<IPrescription> prescriptions = sourcePatient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION,
				EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));

		MedicationStatementResource medicationStatementResource = new MedicationStatementResource();
		List<Medication> medications = new ArrayList<>();
		List<MedicationStatement> medicationStatements = medicationStatementResource
				.createMedicationStatementsFromPrescriptions(new Reference(patientFullUrl), prescriptions,
						resourceFactory);
		List<Reference> medicationReferences = new ArrayList<>();
		for (MedicationStatement ms : medicationStatements) {
			ms.setSubject(new Reference(patientFullUrl));
			String msFullUrl = addEntryAndIndex(bundleEntries, ms);
			medicationReferences.add(new Reference(msFullUrl));
		}

		for (Medication medication : medications) {
			String medFullUrl = addEntryAndIndex(bundleEntries, medication);
		}

		Composition.SectionComponent medicationSection = new Composition.SectionComponent();
		medicationSection.setTitle(FHIRConstants.MEDICATIONS_SECTION_TITLE);
		medicationSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
				FHIRConstants.MEDICATIONS_CODE, FHIRConstants.MEDICATIONS_DISPLAY)));
		medicationSection.setEntry(medicationReferences);
		composition.addSection(medicationSection);

		List<Procedure> procedures = ProcedureResource.createProcedures(new Reference(patientFullUrl), sourcePatient,
				resourceFactory);
		List<Reference> procedureReferences = new ArrayList<>();
		for (Procedure proc : procedures) {
			proc.setSubject(new Reference(patientFullUrl));
			String procFullUrl = addEntryAndIndex(bundleEntries, proc);
			procedureReferences.add(new Reference(procFullUrl));
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
			observation.setSubject(new Reference(patientFullUrl));
			String obsFullUrl = addEntryAndIndex(bundleEntries, observation);
			observationReferences.add(new Reference(obsFullUrl));
		}

		if (!observationReferences.isEmpty()) {
			Composition.SectionComponent resultsSection = new Composition.SectionComponent();
			resultsSection.setTitle(FHIRConstants.RESULTS_SECTION_TITLE);
			resultsSection.setCode(new CodeableConcept().addCoding(
					new Coding(FHIRConstants.LOINC_SYSTEM, FHIRConstants.RESULTS_CODE, FHIRConstants.RESULTS_DISPLAY)));
			resultsSection.setEntry(observationReferences);
			composition.addSection(resultsSection);
		}

		List<Observation> riskFactors = RiskFactorResource.createRiskFactors(new Reference(patientFullUrl),
				sourcePatient, resourceFactory);
		List<Reference> riskFactorReferences = new ArrayList<>();
		for (Observation riskFactor : riskFactors) {
			riskFactor.setSubject(new Reference(patientFullUrl));
			String rfFullUrl = addEntryAndIndex(bundleEntries, riskFactor);
			riskFactorReferences.add(new Reference(rfFullUrl));
		}

		List<FamilyMemberHistory> familyHistories = FamilyMemberHistoryResource.createFamilyMemberHistories(
				new Reference(patientFullUrl), sourcePatient, findingsService, resourceFactory);
		List<Reference> familyHistoryReferences = new ArrayList<>();
		for (FamilyMemberHistory familyHistory : familyHistories) {
			String fmhFullUrl = addEntryAndIndex(bundleEntries, familyHistory);
			familyHistoryReferences.add(new Reference(fmhFullUrl));
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
			annotation.setSubject(new Reference(patientFullUrl));
			String annFullUrl = addEntryAndIndex(bundleEntries, annotation);
			annotationReferences.add(new Reference(annFullUrl));
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
				if (!document.hasSubject())
					document.setSubject(new Reference(patientFullUrl));
				String drFullUrl = addEntryAndIndex(bundleEntries, document);
				documentReferences.add(new Reference(drFullUrl));
			}
			Composition.SectionComponent documentsSection = new Composition.SectionComponent();
			documentsSection.setTitle(FHIRConstants.DOCUMENTS_SECTION_TITLE);
			documentsSection.setCode(new CodeableConcept().addCoding(new Coding(FHIRConstants.LOINC_SYSTEM,
					FHIRConstants.DOCUMENTS_CODE, FHIRConstants.DOCUMENTS_DISPLAY)));
			documentsSection.setEntry(documentReferences);
			composition.addSection(documentsSection);
		}

		bundle.setEntry(bundleEntries);

		for (Bundle.BundleEntryComponent e : bundleEntries) {
			var r = e.getResource();
			if (r instanceof MedicationStatement ms) {
				fixRelativeReference(ms.getSubject());
				fixRelativeReference(ms.getInformationSource());
				if (ms.hasMedicationReference())
					fixRelativeReference(ms.getMedicationReference());
				if (ms.hasInformationSource()) {
					Reference inf = ms.getInformationSource();
					String v = inf.getReference();
					if (v != null && !v.startsWith(FHIRConstants.UUID_PREFIX) && !v.startsWith("#")) { //$NON-NLS-1$
						int s = v.indexOf('/');
						if (s > 0 && s < v.length() - 1) {
							String idPart = v.substring(s + 1);
							if (!idToFullUrl.containsKey(idPart)) {
								ms.setInformationSource((Reference) null);
							}
						}
					}
				}

			} else if (r instanceof Condition c) {
				fixRelativeReference(c.getSubject());
				fixRelativeReference(c.getAsserter());
			} else if (r instanceof Procedure p) {
				fixRelativeReference(p.getSubject());
				if (p.hasPerformer())
					p.getPerformer().forEach(perf -> fixRelativeReference(perf.getActor()));
			} else if (r instanceof Observation o) {
				fixRelativeReference(o.getSubject());
				if (o.hasPerformer())
					o.getPerformer().forEach(thisRef -> fixRelativeReference(thisRef));
			} else if (r instanceof FamilyMemberHistory fmh) {
				fixRelativeReference(fmh.getPatient());
			} else if (r instanceof DocumentReference dr) {
				fixRelativeReference(dr.getSubject());
				if (dr.hasAuthor())
					dr.getAuthor().forEach(a -> fixRelativeReference(a));
				fixRelativeReference(dr.getCustodian());
			} else if (r instanceof PractitionerRole pr) {
				fixRelativeReference(pr.getPractitioner());
				fixRelativeReference(pr.getOrganization());
			} else if (r instanceof Coverage cov) {
				fixRelativeReference(cov.getBeneficiary());
				fixRelativeReference(cov.getPolicyHolder());
				if (cov.hasPayor())
					cov.getPayor().forEach(p -> fixRelativeReference(p));
			} else if (r instanceof Composition comp) {
				fixRelativeReference(comp.getSubject());
				if (comp.hasAuthor())
					comp.getAuthor().forEach(a -> fixRelativeReference(a));
			}
		}

		return bundle;
	}

	private static Optional<IUser> findMandatorUser(Optional<IPerson> mandator) {
		if (mandator.isEmpty()) {
			return Optional.empty();
		}
		IQuery<IUser> userQuery = CoreModelServiceHolder.get().getQuery(IUser.class);
		userQuery.and(ModelPackage.Literals.IUSER__ASSIGNED_CONTACT, COMPARATOR.NOT_EQUALS, null);
		return userQuery.execute().stream().filter(
				u -> u.getAssignedContact() != null && u.getAssignedContact().getId().equals(mandator.get().getId()))
				.findFirst();
	}

	private static String addEntryAndIndex(java.util.List<Bundle.BundleEntryComponent> entries,
			org.hl7.fhir.r4.model.Resource r) {
		String fullUrl = normalizeIdAndGetFullUrl(r);
		Bundle.BundleEntryComponent e = new Bundle.BundleEntryComponent();
		e.setFullUrl(fullUrl);
		e.setResource(r);
		entries.add(e);
		idToFullUrl.put(r.getIdElement().getIdPart(), fullUrl);
		return fullUrl;
	}

	private static String normalizeIdAndGetFullUrl(org.hl7.fhir.r4.model.Resource r) {
		String idPart = r.getIdElement() != null ? r.getIdElement().getIdPart() : null;
		String uuid = (idPart == null || idPart.isBlank()) ? UUID.randomUUID().toString()
				: UUID.nameUUIDFromBytes(idPart.getBytes()).toString();
		r.setId(uuid);
		return FHIRConstants.UUID_PREFIX + uuid;
	}

	private static void fixRelativeReference(Reference ref) {
		if (ref == null || !ref.hasReference())
			return;
		String val = ref.getReference();
		if (val == null || val.startsWith(FHIRConstants.UUID_PREFIX) || val.startsWith("#")) //$NON-NLS-1$
			return;
		int slash = val.indexOf('/');
		if (slash > 0 && slash < val.length() - 1) {
			String idPart = val.substring(slash + 1);
			String mapped = idToFullUrl.get(idPart);
			if (mapped != null)
				ref.setReference(mapped);
		}
	}

	private static String mapLawToCode(BillingLaw law) {
		if (law == null) {
			return "Other";
		}
		return switch (law) {
		case KVG -> "KVG";
		case UVG -> "UVG";
		case IV -> "IVG";
		case MV -> "MVG";
		case VVG -> "VVG";
		case privat -> "Self";
		case ORG, NONE -> "Other";
		case OTHER -> "Other";
		};
	}
}