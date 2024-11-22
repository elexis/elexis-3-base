package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.*;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IPrescription;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MedicationStatementResource {

	private final MedicationResource medicationResource;

	public MedicationStatementResource() {
		this.medicationResource = new MedicationResource();
	}

	public List<MedicationStatement> createMedicationStatementsFromPrescriptions(Reference patientReference,
			List<IPrescription> prescriptions, List<Medication> medicationList) {
		List<MedicationStatement> medicationStatements = new ArrayList<>();

		for (IPrescription prescription : prescriptions) {
			IArticle article = prescription.getArticle();
			if (article != null) {
				IArtikelstammItem test = (IArtikelstammItem) article;
				String gtinCode = article.getGtin();
				String pharmacode = test.getPHAR();
				String productNumber = test.getProductId();

				Medication medication = medicationResource.createMedication(gtinCode, pharmacode, productNumber,
						article.getName());
				medicationList.add(medication);

				MedicationStatement medicationStatement = new MedicationStatement();
				medicationStatement.setId(UUID.nameUUIDFromBytes(article.getId().getBytes()).toString());

				medicationStatement.getMeta().addProfile(FHIRConstants.PROFILE_MEDICATION_STATEMENT);

				medicationStatement.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);

				Reference medicationReference = new Reference(FHIRConstants.UUID_PREFIX + medication.getId());
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
