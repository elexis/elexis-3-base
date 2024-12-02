package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.IObservation;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class ObservationResource {

	public static List<Observation> createObservations(Reference patientReference,
			FhirResourceFactory resourceFactory, IFindingsService findingsService) {
		List<Observation> observations = new ArrayList<>();

		IObservation localObservation = findingsService.create(IObservation.class);
		localObservation.setPatientId(patientReference.getReferenceElement().getIdPart());

		Observation observation = resourceFactory.getResource(localObservation, IObservation.class, Observation.class);

		observations.add(observation);
		return observations;
	}
}
