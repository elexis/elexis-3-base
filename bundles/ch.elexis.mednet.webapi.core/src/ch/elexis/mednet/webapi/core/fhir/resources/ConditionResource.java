package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.findings.ui.services.FindingsServiceComponent;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConditionResource {


	public static List<Condition> createConditions(Patient patient, IPatient sourcePatient) {
		List<Condition> conditions = new ArrayList<>();
		String patientReference = FHIRConstants.UUID_PREFIX + patient.getId();
		boolean strukturDiagnose = ConfigServiceHolder.getGlobal(IMigratorService.DIAGNOSE_SETTINGS_USE_STRUCTURED,
				false);
		if (strukturDiagnose) {
			List<ICondition> structuredConditions = FindingsServiceComponent.getService()
					.getPatientsFindings(sourcePatient.getId(), ICondition.class).stream()
					.filter(finding -> finding instanceof ICondition).map(finding -> (ICondition) finding)
					.collect(Collectors.toList());

			for (ICondition conditionFinding : structuredConditions) {
				Condition condition = new Condition();
				condition.setId(UUID.randomUUID().toString());

				condition.getMeta().addProfile(FHIRConstants.PROFILE_CONDITION);

				condition.setClinicalStatus(
						new CodeableConcept().addCoding(new Coding().setSystem(FHIRConstants.CONDITION_CLINICAL_SYSTEM)
								.setCode(FHIRConstants.CLINICAL_STATUS_ACTIVE_CODE)
								.setDisplay(FHIRConstants.CLINICAL_STATUS_ACTIVE_DISPLAY)));

				condition.setSeverity(new CodeableConcept().addCoding(
						new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM).setCode("255604002").setDisplay("Mild")));
				StringBuilder displayText = new StringBuilder();
				String statusText = conditionFinding.getStatus() == ICondition.ConditionStatus.ACTIVE ? "Aktiv"
						: conditionFinding.getStatus().name();


				displayText.append(statusText).append(" (")
						.append(conditionFinding.getDateRecorded().orElse(LocalDate.now())).append(" ")
						.append(conditionFinding.getStart().orElse("")).append(" - ")
						.append(conditionFinding.getEnd().orElse("")).append(")")
						.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator());


				Optional<String> diagnosisText = conditionFinding.getText();
				displayText.append(diagnosisText.orElse("Unbekannte Diagnose")).append(System.lineSeparator());

				List<String> notes = conditionFinding.getNotes();
				if (!notes.isEmpty()) {
					displayText.append("(Notizen: ").append(String.join(", ", notes)).append(")");
				}
				condition
						.setCode(new CodeableConcept()
								.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM).setCode("404684003")
										.setDisplay("Clinical finding"))
								.setText(displayText.toString()));
				condition.setSubject(new Reference(patientReference));
				conditions.add(condition);
			}
		} else {
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
		}
		return conditions;
	}
}
