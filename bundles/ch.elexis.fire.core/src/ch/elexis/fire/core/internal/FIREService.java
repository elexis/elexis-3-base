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
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.fire.core.IFIREService;

@Component
public class FIREService implements IFIREService {

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService coreModelService;

	@Reference
	private IFhirTransformerRegistry transformerRegistry;

	@Reference
	private IFindingsService findingsService;

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
	public Bundle initialExport() {
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
		
		return ret;
	}

	private Bundle exportPatient(IPatient patient, Patient fhirPatient, Bundle ret) {
		fhirPatient = toFIRE(fhirPatient);
		Bundle patientBundle = new Bundle();
		patientBundle.setId(fhirPatient.getIdentifier().stream().filter(i -> "fire.export.patID".equals(i.getSystem()))
				.findFirst().get().getValue());
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

		fhirPatient.addIdentifier(
				new Identifier().setSystem("fire.export.patID").setValue(getFIREPatientId(fhirPatient.getId())));

		return fhirPatient;
	}

	private String getFIREPatientId(String id) {
		String originalString = getPracticeIdentifier() + "." + id;
		byte[] encodedhash = sha256Digest.digest(
		  originalString.getBytes(StandardCharsets.UTF_8));
		return Hex.encodeHexString(encodedhash);
	}

	@Override
	public Bundle incrementalExport() {
		Bundle ret = getBundle();
		ret.getMeta().addTag(new Coding("fire.export.type", "incremental", null));

		return ret;
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
		return ConfigServiceHolder.get().get(ch.elexis.core.constants.Preferences.INSTALLATION_TIMESTAMP,
				"defaultElexisInstallationId");
	}

	private boolean isOidMedelexisProjectAvailable() {
		return ConfigServiceHolder.get().getLocal(ch.elexis.core.constants.Preferences.SOFTWARE_OID, null) != null
				&& ConfigServiceHolder.get().getLocal("medelexis/projectid", null) != null;
	}

	private String getOidMedelexisProject() {
		String oid = ConfigServiceHolder.get().getLocal(ch.elexis.core.constants.Preferences.SOFTWARE_OID, null);
		String projectId = ConfigServiceHolder.get().getLocal("medelexis/projectid", null);
		return oid + "." + ch.elexis.core.constants.Preferences.OID_SUBDOMAIN_PATIENTMASTERDATA + "." + projectId;
	}
}
