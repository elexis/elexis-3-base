package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.*;

import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

import java.util.UUID;

public class MedicationResource {

	public Medication createMedication(String gtinCode, String pharmacode, String productNumber, String fullText) {
		Medication medication = new Medication();

		medication.getMeta().addProfile(FHIRConstants.PROFILE_MEDICATION);

		medication.setId(UUID.randomUUID().toString());

		if (gtinCode != null && !gtinCode.isEmpty()) {
			Coding gtinCoding = new Coding();
			gtinCoding.setSystem(FHIRConstants.GTIN_SYSTEM);
			gtinCoding.setCode(gtinCode);
			medication.getCode().addCoding(gtinCoding);
		}

		if (pharmacode != null && !pharmacode.isEmpty()) {
			Coding pharmacodeCoding = new Coding();
			pharmacodeCoding.setSystem(FHIRConstants.PHARMACODE_SYSTEM);
			pharmacodeCoding.setCode(pharmacode);
			medication.getCode().addCoding(pharmacodeCoding);
		}

		if (productNumber != null && !productNumber.isEmpty()) {
			Coding productNumberCoding = new Coding();
			productNumberCoding.setSystem(FHIRConstants.PRODUCT_NUMBER_SYSTEM);
			productNumberCoding.setCode(productNumber);
			medication.getCode().addCoding(productNumberCoding);
		}

		if (fullText != null && !fullText.isEmpty()) {
			medication.getCode().setText(fullText);
		}

		return medication;
	}
}
