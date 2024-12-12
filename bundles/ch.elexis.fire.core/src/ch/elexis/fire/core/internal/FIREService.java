package ch.elexis.fire.core.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Resource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.findings.util.fhir.IFhirTransformer;
import ch.elexis.core.findings.util.fhir.IFhirTransformerRegistry;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.ISickCertificate;
import ch.elexis.core.model.IVaccination;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.utils.CoreUtil;
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

	private IFhirTransformer<Immunization, IVaccination> vaccinationTransformer;

	@Activate
	public void activate() throws NoSuchAlgorithmException {
		sha256Digest = MessageDigest.getInstance("SHA-256");
	}
	
	private IFhirTransformer<Patient, IPatient> getPatientTransformer() {
		if (patientTransformer == null) {
			patientTransformer = transformerRegistry.getTransformerFor(Patient.class, IPatient.class);
		}
		return patientTransformer;
	}

	private IFhirTransformer<Encounter, IEncounter> getEncounterTransformer() {
		if(encounterTransformer == null) {
			encounterTransformer = transformerRegistry.getTransformerFor(Encounter.class, IEncounter.class);
		}
		return encounterTransformer;
	}

	private IFhirTransformer<Practitioner, IMandator> getMandatorTransformer() {
		if(mandatorTransformer == null) {
			mandatorTransformer = transformerRegistry.getTransformerFor(Practitioner.class, IMandator.class);
		}
		return mandatorTransformer;
	}

	private IFhirTransformer<Observation, ILabResult> getLabTransformer() {
		if(labTransformer == null) {
			labTransformer = transformerRegistry.getTransformerFor(Observation.class, ILabResult.class);
		}
		return labTransformer;
	}

	private IFhirTransformer<MedicationRequest, IPrescription> getPrescriptionTransformer() {
		if(prescriptionTransformer == null) {
			prescriptionTransformer = transformerRegistry.getTransformerFor(MedicationRequest.class, IPrescription.class);
		}
		return prescriptionTransformer;
	}

	private IFhirTransformer<Condition, ISickCertificate> getSickCertificateTransformer() {
		if(sickCertificateTransformer == null) {
			sickCertificateTransformer = transformerRegistry.getTransformerFor(Condition.class, ISickCertificate.class);
		}
		return sickCertificateTransformer;
	}

	private IFhirTransformer<Immunization, IVaccination> getVaccinationTransformer() {
		if(vaccinationTransformer == null) {
			vaccinationTransformer = transformerRegistry.getTransformerFor(Immunization.class, IVaccination.class);
		}
		return vaccinationTransformer;
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
	public List<File> initialExport(IProgressMonitor progressMonitor) {
		long timestamp = System.currentTimeMillis();
		List<File> ret = new ArrayList<>();

		progressMonitor.beginTask("FIRE initial export", IProgressMonitor.UNKNOWN);

		try {
			clearExportDirectory();
			BundleFile currentBundle = getBundleFile(true);
			try (IQueryCursor<IPatient> cursor = coreModelService.getQuery(IPatient.class).executeAsCursor()) {
				while (cursor.hasNext()) {
					IPatient patient = cursor.next();
					if (patient.getDateOfBirth() != null) {
						Optional<Patient> fhirPatient = getPatientTransformer().getFhirObject(patient);
						if (fhirPatient.isPresent()) {
							Bundle patientBundle = exportPatient(patient, fhirPatient.get(), currentBundle.getBundle());
							currentBundle.addEntry(patientBundle);
						}
						currentBundle = currentBundle.writeIfNecessary(ret);
						if (progressMonitor.isCanceled()) {
							LoggerFactory.getLogger(getClass()).warn("Cancelled initial export");
							return Collections.emptyList();
						}
					}
				}
			}
			currentBundle.write(ret);
			ret = moveExportToUploadDirectory(ret);
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Exception on initial export", e);
			return Collections.emptyList();
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
			Encounter fhirEncounter = getEncounterTransformer().getFhirObject(ie).orElse(null);
			if (fhirEncounter != null) {
				if (ie.getMandator() != null) {
					addMandatorToBundle(ie.getMandator(), ret);
					if (ie.getMandator().getBiller().isPerson() && ie.getMandator().getBiller().isMandator()
							&& !ie.getMandator().equals(ie.getMandator().getBiller())) {
						IContact biller = ie.getMandator().getBiller();
						addMandatorToBundle(coreModelService.load(biller.getId(), IMandator.class).get(), ret);
					}
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
			Observation labResult = getLabTransformer().getFhirObject(lr).orElse(null);
			if (labResult != null) {
				patientBundle.addEntry().setResource(labResult);
			}
		});

		List<IPrescription> prescriptions = patient.getMedication(null);
		prescriptions.forEach(lr -> {
			MedicationRequest medicationRequest = getPrescriptionTransformer().getFhirObject(lr).orElse(null);
			if (medicationRequest != null) {
				patientBundle.addEntry().setResource(medicationRequest);
			}
		});

		IQuery<ISickCertificate> sickQuery = coreModelService.getQuery(ISickCertificate.class);
		sickQuery.and(ModelPackage.Literals.ISICK_CERTIFICATE__PATIENT, COMPARATOR.EQUALS, patient);
		List<ISickCertificate> sickCertificates = sickQuery.execute();
		sickCertificates.forEach(sc -> {
			Condition condition = getSickCertificateTransformer().getFhirObject(sc).orElse(null);
			if (condition != null) {
				patientBundle.addEntry().setResource(condition);
			}
		});

		IQuery<IVaccination> vaccQuery = coreModelService.getQuery(IVaccination.class);
		vaccQuery.and(ModelPackage.Literals.IVACCINATION__PATIENT, COMPARATOR.EQUALS, patient);
		List<IVaccination> vaccinations = vaccQuery.execute();
		vaccinations.forEach(va -> {
			Immunization immunization = getVaccinationTransformer().getFhirObject(va).orElse(null);
			if (immunization != null) {
				patientBundle.addEntry().setResource(immunization);
			}
		});

		return patientBundle;
	}

	private void addMandatorToBundle(IMandator mandator, Bundle ret) {
		Optional<BundleEntryComponent> found = findBundleEntry(mandator.getId(), ret);
		if (found.isEmpty()) {
			Optional<Practitioner> fhirPractitioner = getMandatorTransformer().getFhirObject(mandator);
			fhirPractitioner.ifPresent(p -> ret.addEntry().setResource(toFIRE(p)));
		}
	}

	private Optional<BundleEntryComponent> findBundleEntry(String resourceId, Bundle bundle) {
		if (bundle != null) {
			Optional<BundleEntryComponent> found = bundle.getEntry().stream().filter(
					be -> be.getResource() != null && resourceId.equals(be.getResource().getIdElement().getIdPart()))
					.findFirst();
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

		// set month and day to 1
		if(fhirPatient.hasBirthDate()) {
			LocalDate birthDate = LocalDate.ofInstant(fhirPatient.getBirthDate().toInstant(), ZoneId.systemDefault());
			fhirPatient.setBirthDate(Date.from(birthDate.withDayOfMonth(1).withMonth(1).atStartOfDay()
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
		fhirPatient.getName().clear();
		fhirPatient.getTelecom().clear();
		fhirPatient.getAddress().clear();
		fhirPatient.setText(null);
		fhirPatient.setExtension(Collections.emptyList());

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
	public List<File> incrementalExport(Long lastExportTimestamp, IProgressMonitor progressMonitor) {
		long timestamp = System.currentTimeMillis();

		List<File> ret = new ArrayList<>();
		try {
			clearExportDirectory();
			BundleFile currentBundle = getBundleFile(false);

			List<IPatient> changedPatients = getChanged(lastExportTimestamp, IPatient.class);
			currentBundle = addIncrementalPatients(changedPatients, currentBundle, ret);
			if (progressMonitor.isCanceled()) {
				LoggerFactory.getLogger(getClass()).warn("Cancelled incremental export");
				return Collections.emptyList();
			}

			List<IEncounter> changedEncounters = getChanged(lastExportTimestamp, IEncounter.class);
			currentBundle = addIncrementalEncounters(changedEncounters, currentBundle, ret);
			if (progressMonitor.isCanceled()) {
				LoggerFactory.getLogger(getClass()).warn("Cancelled incremental export");
				return Collections.emptyList();
			}

			List<ICondition> changedConditions = getChangedFindings(lastExportTimestamp, ICondition.class);
			currentBundle = addIncrementalConditions(changedConditions, currentBundle, ret);
			if (progressMonitor.isCanceled()) {
				LoggerFactory.getLogger(getClass()).warn("Cancelled incremental export");
				return Collections.emptyList();
			}

			List<IPrescription> changedPrescriptions = getChanged(lastExportTimestamp, IPrescription.class);
			currentBundle = addIncrementalPrescriptions(changedPrescriptions, currentBundle, ret);
			if (progressMonitor.isCanceled()) {
				LoggerFactory.getLogger(getClass()).warn("Cancelled incremental export");
				return Collections.emptyList();
			}

			List<ILabResult> changedLabResults = getChanged(lastExportTimestamp, ILabResult.class);
			currentBundle = addIncrementalLabResult(changedLabResults, currentBundle, ret);
			if (progressMonitor.isCanceled()) {
				LoggerFactory.getLogger(getClass()).warn("Cancelled incremental export");
				return Collections.emptyList();
			}

			List<IVaccination> changedVaccinations = getChanged(lastExportTimestamp, IVaccination.class);
			currentBundle = addIncrementalVaccination(changedVaccinations, currentBundle, ret);
			if (progressMonitor.isCanceled()) {
				LoggerFactory.getLogger(getClass()).warn("Cancelled incremental export");
				return Collections.emptyList();
			}
			currentBundle.write(ret);
			ret = moveExportToUploadDirectory(ret);
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Exception on incremental export", e);
			return Collections.emptyList();
		}

		configService.set("fire.incrementalExport", Long.toString(timestamp));
		return ret;
	}

	private BundleFile addIncrementalPatients(List<IPatient> changedPatients, BundleFile currentBundle, List<File> ret)
			throws IOException {
		for (IPatient iPatient : changedPatients) {
			if (iPatient.getDateOfBirth() != null) {
				Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(iPatient.getId()),
						currentBundle.getBundle());
				Optional<Patient> fhirPatient = getPatientTransformer().getFhirObject(iPatient);
				if (fhirPatient.isPresent()) {
					toFIRE(fhirPatient.get());
					currentBundle.addResourceToBundle(patientBundle, fhirPatient.get());
				}
				currentBundle = currentBundle.writeIfNecessary(ret);
			}
		}
		return currentBundle;
	}

	private BundleFile addIncrementalEncounters(List<IEncounter> changedEncounters, BundleFile currentBundle,
			List<File> ret) throws IOException {
		for (IEncounter en : changedEncounters) {
			Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(en.getPatient().getId()),
					currentBundle.getBundle());
			Optional<Encounter> fhirEncounter = getEncounterTransformer().getFhirObject(en);
			if (fhirEncounter.isPresent()) {
				if (en.getMandator() != null) {
					addMandatorToBundle(en.getMandator(), currentBundle.getBundle());
					if (en.getMandator().getBiller().isPerson() && en.getMandator().getBiller().isMandator()
							&& !en.getMandator().equals(en.getMandator().getBiller())) {
						IContact biller = en.getMandator().getBiller();
						addMandatorToBundle(coreModelService.load(biller.getId(), IMandator.class).get(),
								currentBundle.getBundle());
					}
				}
				toFIRE(fhirEncounter.get());
				currentBundle.addResourceToBundle(patientBundle, fhirEncounter.get());
			}
			currentBundle = currentBundle.writeIfNecessary(ret);
		}
		return currentBundle;
	}

	private BundleFile addIncrementalConditions(List<ICondition> changedConditions, BundleFile currentBundle,
			List<File> ret) throws IOException {
		for (ICondition co : changedConditions) {
			Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(co.getPatientId()),
					currentBundle.getBundle());
			Condition fhirCondition = (Condition) ModelUtil.getAsResource(co.getRawContent());
			currentBundle.addResourceToBundle(patientBundle, fhirCondition);
			currentBundle = currentBundle.writeIfNecessary(ret);
		}
		return currentBundle;
	}

	private BundleFile addIncrementalPrescriptions(List<IPrescription> changedPrescriptions, BundleFile currentBundle,
			List<File> ret) throws IOException {
		for (IPrescription pr : changedPrescriptions) {
			Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(pr.getPatient().getId()),
					currentBundle.getBundle());
			Optional<MedicationRequest> mr = getPrescriptionTransformer().getFhirObject(pr);
			if (mr.isPresent()) {
				currentBundle.addResourceToBundle(patientBundle, mr.get());
				currentBundle = currentBundle.writeIfNecessary(ret);
			}
		}
		return currentBundle;
	}

	private BundleFile addIncrementalLabResult(List<ILabResult> changedLabResults, BundleFile currentBundle,
			List<File> ret) throws IOException {
		for (ILabResult lr : changedLabResults) {
			Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(lr.getPatient().getId()),
					currentBundle.getBundle());
			Optional<Observation> ob = getLabTransformer().getFhirObject(lr);
			if (ob.isPresent()) {
				currentBundle.addResourceToBundle(patientBundle, ob.get());
				currentBundle = currentBundle.writeIfNecessary(ret);
			}
		}
		return currentBundle;
	}

	private BundleFile addIncrementalVaccination(List<IVaccination> changedVaccinations, BundleFile currentBundle,
			List<File> ret) throws IOException {
		for (IVaccination va : changedVaccinations) {
			Bundle patientBundle = getOrCreatePatientBundle(getFIREPatientId(va.getPatient().getId()),
					currentBundle.getBundle());
			Optional<Immunization> im = getVaccinationTransformer().getFhirObject(va);
			if (im.isPresent()) {
				currentBundle.addResourceToBundle(patientBundle, im.get());
				currentBundle = currentBundle.writeIfNecessary(ret);
			}
		}
		return currentBundle;
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

	private BundleFile getBundleFile(boolean initial) throws UnsupportedEncodingException {
		Bundle bundle = new Bundle();
		bundle.setId(getPracticeIdentifier());
		bundle.setMeta(
				new Meta().setLastUpdated(new Date()).addTag("fire.export.practiceID", getPracticeIdentifier(), null)
						.addTag("fire.export.pmsName", "Elexis", null));
		bundle.setType(BundleType.COLLECTION);
		if (initial) {
			bundle.getMeta().addTag(new Coding("fire.export.type", "initial", null));
		} else {
			bundle.getMeta().addTag(new Coding("fire.export.type", "incremental", null));
		}

		return new BundleFile(bundle, getExportFile(), initial);
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

	@Override
	public boolean uploadBundle(File file) {
		try {
			FIREUploadBundle upload = new FIREUploadBundle(file);
			return CompletableFuture.supplyAsync(upload).get();
		} catch (InterruptedException | ExecutionException e) {
			LoggerFactory.getLogger(getClass()).error("Exception uploading bundle", e);
		}
		return Boolean.FALSE;
	}

	private void increaseBundleCount() {
		int value = configService.get("fire.export.bundle.count", 1);
		configService.set("fire.export.bundle.count", value + 1);
	}

	private int getBundleCount() {
		return configService.get("fire.export.bundle.count", 1);
	}

	private void clearExportDirectory() throws IOException {
		File exportDirectory = new File(CoreUtil.getWritableUserDir(), "fireexport");
		if (exportDirectory.exists()) {
			FileUtils.cleanDirectory(exportDirectory);
		}
	}

	private List<File> moveExportToUploadDirectory(List<File> exportFiles) throws IOException {
		if (exportFiles != null) {
			List<File> uploadFiles = new ArrayList<>();
			for (File exportFile : exportFiles) {
				File uploadDirectory = new File(exportFile.getParentFile(), "upload");
				if (!uploadDirectory.exists()) {
					uploadDirectory.mkdir();
				}
				File uploadFile = new File(uploadDirectory, exportFile.getName());
				FileUtils.moveFile(exportFile, uploadFile);
				uploadFiles.add(uploadFile);
			}
			return uploadFiles;
		}
		return Collections.emptyList();
	}

	private File getExportFile() {
		File exportDirectory = new File(CoreUtil.getWritableUserDir(), "fireexport");
		if (!exportDirectory.exists()) {
			exportDirectory.mkdir();
		}
		return new File(exportDirectory, getBundleName() + ".json");
	}

	private String getBundleText(Bundle bundle) {
		return ModelUtil.getFhirJson(bundle);
	}

	private String getResourceText(Resource resource) {
		return ModelUtil.getFhirJson(resource);
	}

	private void writeBundle(Bundle bundle, File currentFile) throws IOException {
		FileUtils.writeStringToFile(currentFile, getBundleText(bundle), Charset.forName("UTF-8"));
		increaseBundleCount();
	}

	private String getBundleName() {
		return "elexis_00" + getPracticeIdentifier() + "_"
				+ StringUtils.leftPad(Integer.toString(getBundleCount()), 6, "0")
				+ "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	@Override
	public Bundle readBundle(File file) {
		try {
			IBaseResource resource = ModelUtil.getAsResource(Files.readString(file.toPath()));
			if (resource instanceof Bundle) {
				return (Bundle) resource;
			} else {
				LoggerFactory.getLogger(getClass()).error("File contains [" + resource + "] is not an bundle");
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Exception reading bundle file", e);
		}
		return null;
	}

	private class BundleFile {
		private File file;

		private Bundle bundle;
		private int bundleSize;

		private boolean initial;

		public BundleFile(Bundle bundle, File file, boolean initial) throws UnsupportedEncodingException {
			this.initial = initial;
			this.file = file;
			this.bundle = bundle;
			bundleSize = getBundleSize(bundle);
		}

		public Bundle getBundle() {
			return bundle;
		}

		public boolean startNextBundle() throws UnsupportedEncodingException {
			return bundleSize > (25 * 1024 * 1024);
		}

		private void updateBundleSize(Bundle addedBundle) throws UnsupportedEncodingException {
			bundleSize += getBundleSize(addedBundle);
		}

		private void updateBundleSize(Resource resource) throws UnsupportedEncodingException {
			bundleSize += getResourceSize(resource);
		}

		private int getBundleSize(Bundle addedBundle) throws UnsupportedEncodingException {
			return getBundleText(addedBundle).getBytes("UTF-8").length;
		}

		private int getResourceSize(Resource resource) throws UnsupportedEncodingException {
			return getResourceText(resource).getBytes("UTF-8").length;
		}

		public void addEntry(Bundle addedBundle) throws UnsupportedEncodingException {
			bundle.addEntry().setResource(addedBundle);
			updateBundleSize(addedBundle);
		}

		public void addResourceToBundle(Bundle bundle, Resource resource) throws UnsupportedEncodingException {
			bundle.addEntry().setResource(resource);
			updateBundleSize(resource);
		}

		public BundleFile writeIfNecessary(List<File> ret) throws IOException {
			if (startNextBundle()) {
				writeBundle(bundle, file);
				ret.add(file);
				return getBundleFile(initial);
			}
			return this;
		}

		public void write(List<File> ret) throws IOException {
			writeBundle(bundle, file);
			ret.add(file);
		}
	}
}
