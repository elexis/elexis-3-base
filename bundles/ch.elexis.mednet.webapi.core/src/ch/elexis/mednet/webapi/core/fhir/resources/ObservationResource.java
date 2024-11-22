package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObservationResource {

	public static List<Observation> createObservations(Reference patientReference) {
		List<Observation> observations = new ArrayList<>();

		Observation observation = new Observation();
		observation.setId(UUID.randomUUID().toString());
		observation.getMeta().addProfile("https://mednet.swiss/fhir/StructureDefinition/mni-obs-laboratory");
		observation.setStatus(Observation.ObservationStatus.FINAL);

		observation.addCategory(new CodeableConcept()
				.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
						.setCode("laboratory").setDisplay("Laboratory")));

		observation.setCode(new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org")
				.setCode("46418-0").setDisplay("INR in Capillary blood by Coagulation assay")));

		observation.setSubject(patientReference);

		observation.setValue(
				new Quantity().setValue(33).setUnit("min").setSystem("http://unitsofmeasure.org").setCode("min"));

		observations.add(observation);
		return observations;
	}
}
