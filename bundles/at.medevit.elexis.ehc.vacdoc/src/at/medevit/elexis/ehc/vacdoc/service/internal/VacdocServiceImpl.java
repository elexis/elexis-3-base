package at.medevit.elexis.ehc.vacdoc.service.internal;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Composition.CompositionStatus;
import org.hl7.fhir.r4.model.Composition.DocumentConfidentiality;
import org.hl7.fhir.r4.model.Composition.SectionComponent;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.core.EhcCoreService;
import at.medevit.elexis.ehc.vacdoc.service.VacdocService;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.findings.util.fhir.IFhirTransformer;
import ch.elexis.core.findings.util.fhir.IFhirTransformerRegistry;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IVaccination;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

@Component
public class VacdocServiceImpl implements VacdocService {

	private static Logger logger = LoggerFactory.getLogger(VacdocServiceImpl.class);

	@Reference
	private EhcCoreService ehcCoreService;

	@Reference
	private IFhirTransformerRegistry transformerRegistry;

	@Override
	public InputStream getXdmAsStream(Bundle document) throws Exception {
		return ehcCoreService.getXdmAsStream(document);
	}

	@Override
	public Bundle getVacdocDocument(Patient patient, Mandant mandant) {
		// Create eVACDOC (Header)
		Bundle bundle = new Bundle();
		String bundleUuid = UUID.randomUUID().toString();
		bundle.setId(bundleUuid);
		bundle.getMeta().addProfile(
				"http://fhir.ch/ig/ch-vacd/StructureDefinition/ch-vacd-document-immunization-administration");
		bundle.setIdentifier(new Identifier().setSystem("urn:ietf:rfc:3986").setValue(bundleUuid));
		bundle.setType(BundleType.DOCUMENT);
		bundle.setTimestamp(new Date());

		IFhirTransformer<org.hl7.fhir.r4.model.Patient, IPatient> patientTransformer = transformerRegistry
				.getTransformerFor(org.hl7.fhir.r4.model.Patient.class, IPatient.class);
		org.hl7.fhir.r4.model.Patient fhirPatient = patientTransformer.getFhirObject(patient.toIPatient())
				.orElseThrow(() -> new IllegalStateException("Coult not create FHIR patient"));
		bundle.addEntry().setResource(fhirPatient);

		IFhirTransformer<Practitioner, IMandator> practitionerTransformer = transformerRegistry
				.getTransformerFor(Practitioner.class, IMandator.class);
		Practitioner fhirPractitioner = practitionerTransformer
				.getFhirObject(CoreModelServiceHolder.get().load(mandant.getId(), IMandator.class).get())
				.orElseThrow(() -> new IllegalStateException("Coult not create FHIR practitioner"));
		bundle.addEntry().setResource(fhirPractitioner);
		
		Organization fhirCustodian = null;
		if(mandant.getRechnungssteller().istOrganisation()) {
			IFhirTransformer<Organization, IOrganization> organizationTransformer = transformerRegistry.getTransformerFor(Organization.class, IOrganization.class);
			fhirCustodian = organizationTransformer
					.getFhirObject(CoreModelServiceHolder.get()
							.load(mandant.getRechnungssteller().getId(), IOrganization.class).get())
					.orElseThrow(() -> new IllegalStateException("Coult not create FHIR organization"));
			bundle.addEntry().setResource(fhirCustodian);
		}
		
		Composition composition = new Composition();
		String compositionUuid = UUID.randomUUID().toString();
		composition.setId(compositionUuid);
		composition.getMeta().addProfile("http://fhir.ch/ig/ch-vacd/StructureDefinition/ch-vacd-composition-immunization-administration");
		composition.setLanguage(Locale.getDefault().getLanguage());
		composition.setIdentifier(new Identifier().setSystem("urn:ietf:rfc:3986").setValue(compositionUuid));
		composition.setStatus(CompositionStatus.FINAL);
		composition.setType(new CodeableConcept(new Coding("http://snomed.info/sct", "41000179103", "Immunization record")));
		composition.setConfidentiality(DocumentConfidentiality.N);
		
		composition.setTitle("Immunization Administration");
		composition.setDate(new Date());
		composition.setSubject(new org.hl7.fhir.r4.model.Reference(fhirPatient));
		composition.addAuthor(new org.hl7.fhir.r4.model.Reference(fhirPractitioner));
		if (fhirCustodian != null) {
			composition.setCustodian(new org.hl7.fhir.r4.model.Reference(fhirCustodian));
		}
		
		BundleEntryComponent compositionEntry = bundle.addEntry();
		compositionEntry.setResource(composition);

		return bundle;
	}

	/**
	 * Add all vaccinations of the patient referenced in the document.
	 *
	 * @param doc
	 * @param vaccinations
	 */
	@Override
	public void addAllVaccinations(Bundle bundle) {
		Patient elexisPatient = EhcCoreMapper.getElexisPatient(bundle, false);
		if (elexisPatient != null) {
			Query<Vaccination> query = new Query<Vaccination>(Vaccination.class);
			query.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, elexisPatient.getId());
			List<Vaccination> vaccinations = query.execute();
			addVaccinations(bundle, vaccinations);
		}
	}

	/**
	 * Add the vaccinations to the document.
	 *
	 * @param bundle
	 * @param vaccinations
	 */
	@Override
	public void addVaccinations(Bundle bundle, List<Vaccination> vaccinations) {
		if (!vaccinations.isEmpty()) {
			IFhirTransformer<Immunization, IVaccination> immunizationTransformer = transformerRegistry
					.getTransformerFor(Immunization.class, IVaccination.class);
			for (Vaccination vaccination : vaccinations) {
				Optional<Immunization> fhirImmunization = immunizationTransformer.getFhirObject(
						CoreModelServiceHolder.get().load(vaccination.getId(), IVaccination.class).get());
				if (fhirImmunization.isPresent()) {
					Optional<Composition> composition = bundle.getEntry().stream()
							.filter(e -> e.getResource() instanceof Composition).map(e -> (Composition)e.getResource())
							.findFirst();
					if(composition.isPresent()) {
						SectionComponent section = composition.get().getSectionFirstRep();
						if (!section.hasId()) {
							initImmunizationAdministrationSection(section);
						}
						BundleEntryComponent immunizationEntry = bundle.addEntry();
						immunizationEntry.setResource(fhirImmunization.get());
						section.addEntry(new org.hl7.fhir.r4.model.Reference(fhirImmunization.get()));
					}
				}
			}
		}
	}

	private void initImmunizationAdministrationSection(SectionComponent section) {
		section.setId("administration");
		section.setTitle("Immunization Administration");
		section.setCode(new CodeableConcept(new Coding("http://loinc.org", "11369-6", "Hx of Immunization")));
	}

	@Override
	public Optional<Bundle> loadVacdocDocument(InputStream document) throws Exception {
		try {
			String jsonString = IOUtils.toString(document, "UTF-8");
			IBaseResource bundleResource = ModelUtil.getAsResource(jsonString);
			if (bundleResource != null && bundleResource instanceof Bundle) {
				return Optional.of((Bundle) bundleResource);
			} else {
				logger.error("Provided json is not a bundle");
			}
		} catch (Exception e) {
			logger.error("problem loading json bundle", e); //$NON-NLS-1$
		}
		return Optional.empty();
	}

	@Override
	public void importImmunizations(Patient elexisPatient, List<Immunization> immunizations) {
		for (Immunization immunization : immunizations) {
			IFhirTransformer<Immunization, Vaccination> immunizationTransformer = transformerRegistry
					.getTransformerFor(Immunization.class, Vaccination.class);

			Optional<Vaccination> localObject = immunizationTransformer.createLocalObject(immunization);
			if (localObject.isEmpty()) {
				logger.error("Could not create local vaccintion object");
			}
		}
	}

	@Override
	public List<Immunization> getImmunizations(Bundle bundle) {
		Optional<Composition> composition = bundle.getEntry().stream()
				.filter(e -> e.getResource() instanceof Composition).map(e -> (Composition) e.getResource())
				.findFirst();
		if (composition.isPresent()) {
			return bundle.getEntry().stream().filter(e -> e.getResource() instanceof Immunization)
					.map(e -> (Immunization) e.getResource()).toList();
		}
		return Collections.emptyList();
	}

	@Override
	public Optional<Medication> getMedication(Immunization immunization) {
		if (immunization.hasExtension(
				"http://fhir.ch/ig/ch-vacd/StructureDefinition/ch-vacd-ext-immunization-medication-reference")) {
			Extension extension = immunization.getExtensionByUrl(
					"http://fhir.ch/ig/ch-vacd/StructureDefinition/ch-vacd-ext-immunization-medication-reference");
			if (extension.hasValue() && extension.getValue() instanceof org.hl7.fhir.r4.model.Reference) {
				IBaseResource resource = ((org.hl7.fhir.r4.model.Reference) extension.getValue()).getResource();
				if (resource instanceof Medication) {
					return Optional.of((Medication) resource);
				}
			}
		}
		return Optional.empty();
	}
}
