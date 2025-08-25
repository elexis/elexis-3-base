package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.util.FindingsServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class ObservationResource {

	public static List<Observation> createObservations(Reference patientReference, FhirResourceFactory resourceFactory,
			IFindingsService findingsService) {
		List<Observation> observations = new ArrayList<>();

		String patientIdPart = patientReference.getReferenceElement().getIdPart();

		List<IObservation> localObservations = FindingsServiceHolder.getiFindingsService()
				.getPatientsFindings(patientIdPart, IObservation.class);

		for (IObservation local : localObservations) {
			Observation obs = resourceFactory.getResource(local, IObservation.class, Observation.class);

			if (obs == null) {
				obs = new Observation();
				obs.setId(UUID.randomUUID().toString());
			}
			if (!obs.hasId() || obs.getIdElement().getIdPart() == null || obs.getIdElement().getIdPart().isBlank()) {
				obs.setId(UUID.randomUUID().toString());
			}

			obs.setSubject(new Reference(patientReference.getReference()));

			if (!obs.hasStatus()) {
				obs.setStatus(ObservationStatus.FINAL);
			}

			if (obs.getCategory() == null || obs.getCategory().isEmpty()) {
				obs.addCategory(new CodeableConcept().addCoding(new Coding(
						FHIRConstants.OBSERVATION_CATEGORY_SYSTEM, "laboratory", "Laboratory"))); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (!obs.hasCode() || obs.getCode().getCoding().isEmpty()) {
				CodeableConcept cc = new CodeableConcept();

				Optional<String> maybeText = local.getText();
				if (maybeText.isPresent() && !maybeText.get().isBlank()) {
					cc.setText(maybeText.get().trim());
				} else {

					cc.setText("Observation"); //$NON-NLS-1$
				}
				obs.setCode(cc);
			}
			observations.add(obs);
		}

		return observations;
	}
}
