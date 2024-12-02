package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.Date;
import java.util.UUID;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.mednet.webapi.core.constants.FHIRConstants;


public class CompositionResource {

	public static Composition createComposition(Patient patient) {
        Composition composition = new Composition();
        composition.setId(UUID.randomUUID().toString());
        composition.getMeta()
				.addProfile(FHIRConstants.PROFILE_PATIENT_OVERVIEW_COMPOSITION);
        composition.setStatus(Composition.CompositionStatus.FINAL);
        composition.setType(
				new CodeableConcept()
						.addCoding(new Coding(FHIRConstants.LOINC_SYSTEM, "60591-5", "Patient summary Document")));
		composition.setTitle(FHIRConstants.PATIENT_OVERVIEW_SECTION_TITLE);
        composition.setDate(new Date());
		String patientReference = FHIRConstants.UUID_PREFIX + patient.getId();

        composition.setSubject(new Reference(patientReference));
        composition.setConfidentiality(Composition.DocumentConfidentiality.N);

        return composition;
    }
}

