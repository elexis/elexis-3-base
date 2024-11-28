package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class ProcedureResource {

	public static List<Procedure> createProcedures(Reference patientReference, IPatient sourcePatient,
			FhirResourceFactory resourceFactory) {
		IEncounter lastEncounter = getLastEncounterForPatient(sourcePatient);

		List<Procedure> procedures = new ArrayList<>();
		if (lastEncounter != null) {
			Optional<org.hl7.fhir.r4.model.Encounter> encounterResource = Optional.ofNullable(resourceFactory
					.getResource(lastEncounter, IEncounter.class, org.hl7.fhir.r4.model.Encounter.class));
			String encounterText = encounterResource
					.map(encounter -> encounter.getText() != null ? encounter.getText().getDivAsString()
							: "No text available")
					.orElse("No encounter data available");
			Procedure procedure = new Procedure();
			procedure.setId(UUID.randomUUID().toString());
			procedure.getMeta().addProfile(FHIRConstants.PROFILE_PROCEDURE);
			procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
			procedure
					.setCode(new CodeableConcept()
							.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM)
									.setCode(FHIRConstants.PROCEDURE_CODE).setDisplay(FHIRConstants.PROCEDURE_DISPLAY))
							.setText(encounterText));

			procedure.setSubject(patientReference);
			procedures.add(procedure);
		} else {
			// Fallback für den Fall, dass kein Encounter verfügbar ist
			Procedure procedure = new Procedure();
			procedure.setId(UUID.randomUUID().toString());
			procedure.getMeta().addProfile(FHIRConstants.PROFILE_PROCEDURE);
			procedure.setStatus(Procedure.ProcedureStatus.UNKNOWN);

			procedure
					.setCode(new CodeableConcept()
							.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM)
									.setCode(FHIRConstants.PROCEDURE_CODE).setDisplay("No procedure data available"))
							.setText("No encounter data available"));

			procedure.setSubject(patientReference);
			procedures.add(procedure);
		}

		return procedures;
	}

	private static IEncounter getLastEncounterForPatient(IPatient patient) {
		List<IEncounter> encounters = EncounterServiceHolder.get().getAllEncountersForPatient(patient);
		return encounters.stream().max(Comparator.comparing(IEncounter::getTimeStamp)).orElse(null);
	}
}
