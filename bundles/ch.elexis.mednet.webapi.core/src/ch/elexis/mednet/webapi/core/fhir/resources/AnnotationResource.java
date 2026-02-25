package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

public class AnnotationResource {

	public static List<Observation> createAnnotations(Reference patientReference) {
		List<Observation> annotations = new ArrayList<>();

		Observation annotation = new Observation();
		annotation.setId(UUID.randomUUID().toString());
		annotation.getMeta().addProfile(FHIRConstants.PROFILE_OBSERVATION_ANNOTATION);
		annotation.setStatus(Observation.ObservationStatus.FINAL);

		annotation.addCategory(new CodeableConcept().setText(FHIRConstants.CATEGORY_CONSULTATION));

		annotation.setCode(new CodeableConcept().addCoding(new Coding().setSystem(FHIRConstants.LOINC_SYSTEM)
				.setCode(FHIRConstants.ANNOTATION_COMMENT_CODE).setDisplay(FHIRConstants.ANNOTATION_COMMENT_DISPLAY)));

		annotation.setSubject(patientReference);

		annotation.setValue(new org.hl7.fhir.r4.model.StringType("The patient's skin is dry"));

		annotations.add(annotation);

		return annotations;
	}
}
