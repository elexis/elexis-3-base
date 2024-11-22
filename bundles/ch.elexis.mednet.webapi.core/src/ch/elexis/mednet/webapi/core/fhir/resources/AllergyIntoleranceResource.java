package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.model.IPatient;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

public class AllergyIntoleranceResource {

	public static List<AllergyIntolerance> createAllergies(Patient patient, IPatient sourcePatient) {
		List<AllergyIntolerance> allergies = new ArrayList<>();
		String patientFullUrl = "urn:uuid:" + UUID.nameUUIDFromBytes(patient.getId().getBytes());
		String patientAllergies = sourcePatient.getAllergies();
		List<String> allergyList = Arrays.asList(patientAllergies.split(","));

		for (String allergyText : allergyList) {
			AllergyIntolerance allergy = new AllergyIntolerance();
			allergy.setId(UUID.randomUUID().toString());

			// Using constants from FHIRConstants
			allergy.getMeta().addProfile(FHIRConstants.PROFILE_ALLERGY_INTOLERANCE);

			CodeableConcept clinicalStatus = new CodeableConcept();
			clinicalStatus.addCoding(new Coding(FHIRConstants.CLINICAL_STATUS_SYSTEM,
					FHIRConstants.CLINICAL_STATUS_ACTIVE_CODE, FHIRConstants.CLINICAL_STATUS_ACTIVE_DISPLAY));
			allergy.setClinicalStatus(clinicalStatus);
			allergy.setType(AllergyIntolerance.AllergyIntoleranceType.ALLERGY);
			allergy.addCategory(AllergyIntolerance.AllergyIntoleranceCategory.FOOD);
			allergy.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.HIGH);

			CodeableConcept code = new CodeableConcept();
			code.addCoding(new Coding(FHIRConstants.SNOMED_SYSTEM, FHIRConstants.ALLERGIC_DISORDER_CODE,
					FHIRConstants.ALLERGIC_DISORDER_DISPLAY));
			code.setText(allergyText.trim());
			allergy.setCode(code);
			allergy.setPatient(new Reference(patientFullUrl));

			allergies.add(allergy);
		}
		return allergies;
	}
}
