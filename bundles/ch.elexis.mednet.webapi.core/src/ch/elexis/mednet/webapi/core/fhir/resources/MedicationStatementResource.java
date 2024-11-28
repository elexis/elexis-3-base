package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IPrescription;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class MedicationStatementResource {

	public List<MedicationStatement> createMedicationStatementsFromPrescriptions(Reference patientReference,
			List<IPrescription> prescriptions, FhirResourceFactory resourceFactory) {
		List<MedicationStatement> medicationStatements = new ArrayList<>();

		for (IPrescription prescription : prescriptions) {
			IArticle article = prescription.getArticle();
			if (article != null) {
				Medication medication = resourceFactory.getResource(article, IArticle.class, Medication.class);
				if (medication == null) {
					medication = new Medication();
					medication.setId("unknown");
				}
				if (medication.getCode() != null
						&& (medication.getCode().getText() == null || medication.getCode().getText().isEmpty())) {
					String defaultText = article.getName() != null ? article.getName() : "Undefined Medication";
					medication.getCode().setText(defaultText);
				}

				String medicationId = medication.getId();
				if (medicationId != null && medicationId.startsWith("Medication/")) {
					medicationId = medicationId.substring("Medication/".length());
				}
				MedicationStatement medicationStatement = new MedicationStatement();
				medicationStatement.setId(UUID.nameUUIDFromBytes(article.getId().getBytes()).toString());
				medicationStatement.getMeta().addProfile(FHIRConstants.PROFILE_MEDICATION_STATEMENT);
				medicationStatement.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);
				medication.setId(medicationStatement.getId());
				medicationStatement.addContained(medication);
				Reference medicationReference = new Reference(medication.getId());
				medicationStatement.setMedication(medicationReference);
				medicationStatement.setSubject(patientReference);
				Dosage dosage = new Dosage();
				String dosageInstruction = prescription.getDosageInstruction();
				if (dosageInstruction != null && !dosageInstruction.isEmpty()) {
					dosage.setText(dosageInstruction);
				}
				medicationStatement.addDosage(dosage);
				medicationStatements.add(medicationStatement);
			}
		}
		return medicationStatements;
	}
}
