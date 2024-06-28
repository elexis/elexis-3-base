package ch.elexis.fire.core.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.findings.util.fhir.IFhirTransformer;
import ch.elexis.core.findings.util.fhir.IFhirTransformerRegistry;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.ISickCertificate;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.fire.core.IFIREService;

@Component
public class FIREService implements IFIREService {

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService coreModelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.findings.model)")
	private IModelService findingsModelService;

	@Reference
	private IFhirTransformerRegistry transformerRegistry;

	@Reference
	private IFindingsService findingsService;

	@Reference
	private IConfigService configService;

	private MessageDigest sha256Digest;

	private IFhirTransformer<Patient, IPatient> patientTransformer;

	private IFhirTransformer<Encounter, IEncounter> encounterTransformer;

	private IFhirTransformer<Practitioner, IMandator> mandatorTransformer;

	private IFhirTransformer<Observation, ILabResult> labTransformer;

	private IFhirTransformer<MedicationRequest, IPrescription> prescriptionTransformer;

	private IFhirTransformer<Condition, ISickCertificate> sickCertificateTransformer;

	@Activate
	public void activate() throws NoSuchAlgorithmException {
		sha256Digest = MessageDigest.getInstance("SHA-256");

		patientTransformer = transformerRegistry.getTransformerFor(Patient.class, IPatient.class);
		encounterTransformer = transformerRegistry.getTransformerFor(Encounter.class, IEncounter.class);
		mandatorTransformer = transformerRegistry.getTransformerFor(Practitioner.class, IMandator.class);
		labTransformer = transformerRegistry.getTransformerFor(Observation.class, ILabResult.class);
		prescriptionTransformer = transformerRegistry.getTransformerFor(MedicationRequest.class, IPrescription.class);
		sickCertificateTransformer = transformerRegistry.getTransformerFor(Condition.class, ISickCertificate.class);
	}
	
	@Override
	public Long getInitialTimestamp() {
		return Long.valueOf(configService.get("fire.intialExport", "-1"));
	}

	@Override
	public Long getIncrementalTimestamp() {
		return Long.valueOf(configService.get("fire.incrementalExport", "-1"));
	}

	@Override
	public Bundle initialExport() {
		long timestamp = System.currentTimeMillis();
		Bundle ret = getBundle();
		ret.getMeta().addTag(new Coding("fire.export.type", "initial", null));

		try (IQueryCursor<IPatient> cursor = coreModelService.getQuery(IPatient.class).executeAsCursor()) {
			while (cursor.hasNext()) {
				IPatient patient = cursor.next();
				patientTransformer.getFhirObject(patient).ifPresent(fhirPatient -> {
					ret.addEntry().setResource(exportPatient(patient, fhirPatient, ret));
				});
			}
		}
		
		configService.set("fire.intialExport", Long.toString(timestamp));
		return ret;
	}

	private Bundle exportPatient(IPatient patient, Patient fhirPatient, Bundle ret) {
		String firePatientId = getFIREPatientId(patient.getId());

		fhirPatient = toFIRE(fhirPatient);
		fhirPatient.addIdentifier(
				new Identifier().setSystem("fire.export.patID").setValue(firePatientId));

		Bundle patientBundle = new Bundle();
		patientBundle.setId(firePatientId);
		patientBundle.setType(BundleType.COLLECTION);
		patientBundle.addEntry().setResource(fhirPatient);

		List<IEncounter> encounters = patient.getCoverages().stream().flatMap(c -> c.getEncounters().stream())
				.collect(Collectors.toList());
		encounters.stream().forEach(ie -> {
			Encounter fhirEncounter = encounterTransformer.getFhirObject(ie).orElse(null);
			if (fhirEncounter != null) {
				addMandatorToBundle(ie.getMandator(), ret);
				if (ie.getMandator().getBiller().isPerson() && ie.getMandator().getBiller().isMandator()
						&& !ie.getMandator().equals(ie.getMandator().getBiller())) {
					addMandatorToBundle((IMandator) ie.getMandator().getBiller(), ret);
				}
				toFIRE(fhirEncounter);
				patientBundle.addEntry().setResource(fhirEncounter);
			}
		});
		
		List<ICondition> conditions = findingsService.getPatientsFindings(patient.getId(), ICondition.class);
		conditions.forEach(c -> {
			Condition fhirCondition = (Condition) ModelUtil.getAsResource(c.getRawContent());
			patientBundle.addEntry().setResource(fhirCondition);
		});

		IQuery<ILabResult> resultQuery = coreModelService.getQuery(ILabResult.class);
		resultQuery.and(ModelPackage.Literals.ILAB_RESULT__PATIENT, COMPARATOR.EQUALS, patient);
		List<ILabResult> labObservation = resultQuery.execute();
		labObservation.forEach(lr -> {
			Observation labResult = labTransformer.getFhirObject(lr).orElse(null);
			if (labResult != null) {
				patientBundle.addEntry().setResource(labResult);
			}
		});

		List<IPrescription> prescriptions = patient.getMedication(null);
		prescriptions.forEach(lr -> {
			MedicationRequest medicationRequest = prescriptionTransformer.getFhirObject(lr).orElse(null);
			if (medicationRequest != null) {
				patientBundle.addEntry().setResource(medicationRequest);
			}
		});

		IQuery<ISickCertificate> query = coreModelService.getQuery(ISickCertificate.class);
		query.and(ModelPackage.Literals.ISICK_CERTIFICATE__PATIENT, COMPARATOR.EQUALS, patient);
		List<ISickCertificate> sickCertificates = query.execute();
		sickCertificates.forEach(sc -> {
			Condition condition = sickCertificateTransformer.getFhirObject(sc).orElse(null);
			if (condition != null) {
				patientBundle.addEntry().setResource(condition);
			}
		});

		return patientBundle;
	}

	private void addMandatorToBundle(IMandator mandator, Bundle ret) {
		Optional<BundleEntryComponent> found = findBundleEntry(mandator.getId(), ret);
		if (found.isEmpty()) {
			Optional<Practitioner> fhirPractitioner = mandatorTransformer.getFhirObject(mandator);
			fhirPractitioner.ifPresent(p -> ret.addEntry().setResource(toFIRE(p)));
		}
	}

	private Optional<BundleEntryComponent> findBundleEntry(String resourceId, Bundle bundle) {
		if (bundle != null) {
			Optional<BundleEntryComponent> found = bundle.getEntry().stream()
					.filter(be -> be.getResource() != null && resourceId.equals(be.getResource().getId())).findFirst();
			return found;
		}
		return Optional.empty();
	}

	private Practitioner toFIRE(Practitioner fhirPractitioner) {
		fhirPractitioner.getName().clear();
		fhirPractitioner.getTelecom().clear();
		fhirPractitioner.getAddress().clear();
		fhirPractitioner.setText(null);

		return fhirPractitioner;
	}

	private Encounter toFIRE(Encounter fhirEncounter) {

		fhirEncounter.setText(null);

		return fhirEncounter;
	}

	private Patient toFIRE(Patient fhirPatient) {

		fhirPatient.getName().clear();
		fhirPatient.getTelecom().clear();
		fhirPatient.getAddress().clear();
		fhirPatient.setText(null);

		// filter all external ids
		fhirPatient.setIdentifier(new ArrayList<Identifier>(
				fhirPatient.getIdentifier().stream().filter(i -> i.getSystem().startsWith("www.elexis")).toList()));

		return fhirPatient;
	}

	protected String getFIREPatientId(String id) {
		String originalString = getPracticeIdentifier() + "." + id;
		byte[] encodedhash = sha256Digest.digest(
		  originalString.getBytes(StandardCharsets.UTF_8));
		return Hex.encodeHexString(encodedhash);
	}

	@Override
	public Bundle incrementalExport(Long lastExportTimestamp) {
		long timestamp = System.currentTimeMillis();
		Bundle ret = getBundle();
		ret.getMeta().addTag(new Coding("fire.export.type", "incremental", null));

		List<IPatient> changedPatients = getChanged(lastExportTimestamp, IPatient.class);
		addIncrementalPatients(changedPatients, ret);
		List<IEncounter> changedEncounters = getChanged(lastExportTimestamp, IEncounter.class);
		addIncrementalEncounters(changedEncounters, ret);
		List<ICondition> changedConditions = getChangedFindings(lastExportTimestamp, ICondition.class);
		List<IPrescription> changedPrescriptions = getChanged(lastExportTimestamp, IPrescription.class);

		configService.set("fire.incrementalExport", Long.toString(timestamp));
		return ret;
	}

	private void addIncrementalEncounters(List<IEncounter> changedEncounters, Bundle ret) {
		for (IEncounter iEncounter : changedEncounters) {
			Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(iEncounter.getPatient().getId()), ret);
			encounterTransformer.getFhirObject(iEncounter).ifPresent(fhirEncounter -> {
				addMandatorToBundle(iEncounter.getMandator(), ret);
				if (iEncounter.getMandator().getBiller().isPerson() && iEncounter.getMandator().getBiller().isMandator()
						&& !iEncounter.getMandator().equals(iEncounter.getMandator().getBiller())) {
					addMandatorToBundle((IMandator) iEncounter.getMandator().getBiller(), ret);
				}
				toFIRE(fhirEncounter);
				patientBundle.addEntry().setResource(fhirEncounter);
			});
		}

	}

	protected Bundle getPatientBundle(String firePatientId, Bundle exportBundle) {
		for (BundleEntryComponent entry : exportBundle.getEntry()) {
			if (entry.getResource() != null && firePatientId.equals(entry.getResource().getId())) {
				return (Bundle) entry.getResource();
			}
		}
		return null;
	}

	protected Bundle getOrCreatePatientBundle(String firePatientId, Bundle exportBundle) {
		Bundle ret = getPatientBundle(firePatientId, exportBundle);
		if (ret == null) {
			ret = new Bundle();
			ret.setId(firePatientId);
			ret.setType(BundleType.COLLECTION);
			exportBundle.addEntry().setResource(ret);
		}
		return ret;
	}

	private void addIncrementalPatients(List<IPatient> changedPatients, Bundle ret) {
		for (IPatient iPatient : changedPatients) {
			Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(iPatient.getId()), ret);
			patientTransformer.getFhirObject(iPatient).ifPresent(fhirPatient -> {
				toFIRE(fhirPatient);
				patientBundle.addEntry().setResource(fhirPatient);
			});
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getChanged(Long lastExportTimestamp, Class<T> clazz) {
		IQuery<T> query = coreModelService.getQuery(clazz);
		query.and("lastupdate", COMPARATOR.GREATER, Long.valueOf(lastExportTimestamp)); //$NON-NLS-1$
		return (List<T>) (List<?>) query.execute();
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getChangedFindings(Long lastExportTimestamp, Class<T> clazz) {
		IQuery<T> query = findingsModelService.getQuery(clazz);
		query.and("lastupdate", COMPARATOR.GREATER, Long.valueOf(lastExportTimestamp)); //$NON-NLS-1$
		return (List<T>) (List<?>) query.execute();
	}

	private Bundle getBundle() {
		Bundle ret = new Bundle();
		ret.setId(getPracticeIdentifier());
		ret.setMeta(
				new Meta().setLastUpdated(new Date()).addTag("fire.export.practiceID", getPracticeIdentifier(), null)
						.addTag("fire.export.pmsName", "Elexis", null));
		ret.setType(BundleType.COLLECTION);

		return ret;
	}

	private String getPracticeIdentifier() {
		if (isOidMedelexisProjectAvailable()) {
			return getOidMedelexisProject();
		} else {
			return getElexisInstallationId();
		}
	}

	private String getElexisInstallationId() {
		return configService.get(ch.elexis.core.constants.Preferences.INSTALLATION_TIMESTAMP,
				"defaultElexisInstallationId");
	}

	private boolean isOidMedelexisProjectAvailable() {
		return configService.getLocal(ch.elexis.core.constants.Preferences.SOFTWARE_OID, null) != null
				&& configService.getLocal("medelexis/projectid", null) != null;
	}

	private String getOidMedelexisProject() {
		String oid = configService.getLocal(ch.elexis.core.constants.Preferences.SOFTWARE_OID, null);
		String projectId = configService.getLocal("medelexis/projectid", null);
		return oid + "." + ch.elexis.core.constants.Preferences.OID_SUBDOMAIN_PATIENTMASTERDATA + "." + projectId;
	}
}
