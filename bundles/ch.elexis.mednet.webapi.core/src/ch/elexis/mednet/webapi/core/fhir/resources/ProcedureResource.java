package ch.elexis.mednet.webapi.core.fhir.resources;

import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;

public class ProcedureResource {

	public static List<Procedure> createProcedures(Reference patientReference, IPatient sourcePatient) {

		IEncounter lastEncounter = getLastEncounterForPatient(sourcePatient);
		List<Procedure> procedures = new ArrayList<>();
		Procedure procedure = new Procedure();
		procedure.setId(UUID.randomUUID().toString());
		procedure.getMeta().addProfile(FHIRConstants.PROFILE_PROCEDURE);
		procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);

		String encounterText = (lastEncounter != null) ? lastEncounter.getHeadVersionInPlaintext()
				: "No encounter data available";
		procedure
				.setCode(new CodeableConcept()
						.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM)
								.setCode(FHIRConstants.PROCEDURE_CODE).setDisplay(FHIRConstants.PROCEDURE_DISPLAY))
						.setText(encounterText));

		procedure.setSubject(patientReference);
		procedures.add(procedure);
		return procedures;
	}

	private static IEncounter getLastEncounterForPatient(IPatient patient) {

		List<IEncounter> encounters = EncounterServiceHolder.get().getAllEncountersForPatient(patient);

		return encounters.stream().max(Comparator.comparing(IEncounter::getTimeStamp)).orElse(null);
	}

}
