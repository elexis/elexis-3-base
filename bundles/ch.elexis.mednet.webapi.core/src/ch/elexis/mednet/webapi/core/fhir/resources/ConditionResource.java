package ch.elexis.mednet.webapi.core.fhir.resources;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.util.FindingsServiceHolder;
import ch.elexis.core.model.IPatient;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

public class ConditionResource {

	/**
	 * Hilfsmethode zum Hinzufügen einer Condition zur Bundle-Liste.
	 * 
	 * @param condition           Die Condition-Ressource, die hinzugefügt werden
	 *                            soll.
	 * @param bundleEntries       Die Liste der Bundle-Einträge.
	 * @param conditionReferences Die Liste der Condition-Referenzen.
	 */
	static void addConditionToBundle(Condition condition, List<BundleEntryComponent> bundleEntries,
			List<Reference> conditionReferences) {
		if (condition != null) {
			String conditionFullUrl = FHIRConstants.UUID_PREFIX + condition.getId();
			BundleEntryComponent conditionEntry = new BundleEntryComponent();
			conditionEntry.setFullUrl(conditionFullUrl);
			conditionEntry.setResource(condition);
			bundleEntries.add(conditionEntry);
			conditionReferences.add(new Reference(conditionFullUrl));
		}
	}

	static List<ICondition> getLocalConditions(IPatient sourcePatient) {
		return FindingsServiceHolder.getiFindingsService().getPatientsFindings(sourcePatient.getId(), ICondition.class)
				.stream().filter(finding -> finding instanceof ICondition).map(finding -> finding)
				.collect(Collectors.toList());
	}

	public static List<Condition> createConditions(Patient patient, IPatient sourcePatient) {
		List<Condition> conditions = new ArrayList<>();
		String patientReference = FHIRConstants.UUID_PREFIX + patient.getId();
			String diagnosisString = sourcePatient.getDiagnosen();
			if (diagnosisString != null && !diagnosisString.isEmpty()) {
				String[] diagnosisArray = diagnosisString.split(",");

				for (String diagnosisText : diagnosisArray) {
					diagnosisText = diagnosisText.trim();
					Condition condition = new Condition();
					condition.setId(UUID.randomUUID().toString());

					condition.getMeta().addProfile(FHIRConstants.PROFILE_CONDITION);

					condition.setClinicalStatus(new CodeableConcept()
							.addCoding(new Coding().setSystem(FHIRConstants.CONDITION_CLINICAL_SYSTEM)
									.setCode(FHIRConstants.CLINICAL_STATUS_ACTIVE_CODE)
									.setDisplay(FHIRConstants.CLINICAL_STATUS_ACTIVE_DISPLAY)));

					condition.setSeverity(new CodeableConcept().addCoding(new Coding()
							.setSystem(FHIRConstants.SNOMED_SYSTEM).setCode("255604002").setDisplay("Mild")));

					condition
							.setCode(
									new CodeableConcept()
											.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM)
													.setCode("404684003").setDisplay("Clinical finding"))
											.setText(diagnosisText));
					condition.setSubject(new Reference(patientReference));
					conditions.add(condition);
				}
			}

		return conditions;
	}

	public static Condition createConditionFallback(ICondition localCondition, Patient patient) {
		Condition condition = new Condition();
		condition.setId(UUID.randomUUID().toString());
		condition.getMeta().addProfile(FHIRConstants.PROFILE_CONDITION);

		condition.setClinicalStatus(new CodeableConcept().addCoding(new Coding()
				.setSystem(FHIRConstants.CONDITION_CLINICAL_SYSTEM).setCode(FHIRConstants.CLINICAL_STATUS_ACTIVE_CODE)
				.setDisplay(FHIRConstants.CLINICAL_STATUS_ACTIVE_DISPLAY)));

		StringBuilder displayText = new StringBuilder();
		displayText
				.append(localCondition.getStatus() == ICondition.ConditionStatus.ACTIVE ? "Aktiv"
						: localCondition.getStatus().name())
				.append(" (").append(localCondition.getDateRecorded().orElse(LocalDate.now())).append(" ")
				.append(localCondition.getStart().orElse("")).append(" - ").append(localCondition.getEnd().orElse(""))
				.append(")").append(System.lineSeparator());

		Optional<String> diagnosisText = localCondition.getText();
		displayText.append(diagnosisText.orElse("Unbekannte Diagnose")).append(System.lineSeparator());

		List<String> notes = localCondition.getNotes();
		if (!notes.isEmpty()) {
			displayText.append("(Notizen: ").append(String.join(", ", notes)).append(")");
		}

		condition.setCode(new CodeableConcept().addCoding(
				new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM).setCode("404684003").setDisplay("Clinical finding"))
				.setText(displayText.toString()));

		// Set subject
		condition.setSubject(new Reference(FHIRConstants.UUID_PREFIX + patient.getId()));

		return condition;
	}
}
