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

import ch.elexis.core.findings.IAllergyIntolerance;
import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.findings.ui.services.FindingsServiceComponent;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class AllergyIntoleranceResource {

	public static List<AllergyIntolerance> createAllergies(Patient patient, IPatient sourcePatient,
			FhirResourceFactory resourceFactory) {
		List<AllergyIntolerance> allergies = new ArrayList<>();
		String patientFullUrl = "urn:uuid:" + UUID.nameUUIDFromBytes(patient.getId().getBytes());
		boolean strukturAllergy = ConfigServiceHolder
				.getGlobal(IMigratorService.ALLERGYINTOLERANCE_SETTINGS_USE_STRUCTURED, false);
		if (strukturAllergy) {
		List<IAllergyIntolerance> structuredAllergies = FindingsServiceComponent.getService()
				.getPatientsFindings(sourcePatient.getId(), IAllergyIntolerance.class);
		for (IAllergyIntolerance structuredAllergy : structuredAllergies) {
			AllergyIntolerance allergy = resourceFactory.getResource(structuredAllergy, IAllergyIntolerance.class,
					AllergyIntolerance.class);
			if (allergy == null) {
				allergy = new AllergyIntolerance();
				allergy.setId(UUID.randomUUID().toString());
			}
			if (allergy.getId() == null || allergy.getId().isEmpty()) {
				allergy.setId(UUID.randomUUID().toString());
			}
			allergy.getMeta().addProfile(FHIRConstants.PROFILE_ALLERGY_INTOLERANCE);
			if (allergy.getClinicalStatus() == null) {
				CodeableConcept clinicalStatus = new CodeableConcept();
				clinicalStatus.addCoding(new Coding(FHIRConstants.CLINICAL_STATUS_SYSTEM,
						FHIRConstants.CLINICAL_STATUS_ACTIVE_CODE, FHIRConstants.CLINICAL_STATUS_ACTIVE_DISPLAY));
				allergy.setClinicalStatus(clinicalStatus);
			}
			if (allergy.getCode() == null || allergy.getCode().getCoding().isEmpty()) {
				allergy.setCode(new CodeableConcept().setText(structuredAllergy.getText().orElse("Unknown Allergy")));
			}
			allergy.setPatient(new Reference(patientFullUrl));
			allergies.add(allergy);
		}
	} else {
		String patientAllergies = sourcePatient.getAllergies();

		if (patientAllergies != null && !patientAllergies.isEmpty()) {
			List<String> allergyList = Arrays.asList(patientAllergies.split(","));
			for (String allergyText : allergyList) {
				IAllergyIntolerance sourceAllergy = FindingsServiceComponent.getService()
						.create(IAllergyIntolerance.class);
				sourceAllergy.setPatientId(sourcePatient.getId());
				sourceAllergy.setText(allergyText.trim());
				AllergyIntolerance allergy = resourceFactory.getResource(sourceAllergy, IAllergyIntolerance.class,
						AllergyIntolerance.class);
				if (allergy == null) {
					allergy = new AllergyIntolerance();
					allergy.setId(UUID.randomUUID().toString());
				}
				if (allergy.getId() == null || allergy.getId().isEmpty()) {
					allergy.setId(UUID.randomUUID().toString());
				}
				allergy.getMeta().addProfile(FHIRConstants.PROFILE_ALLERGY_INTOLERANCE);
				if (allergy.getClinicalStatus() == null) {
					CodeableConcept clinicalStatus = new CodeableConcept();
					clinicalStatus.addCoding(new Coding(FHIRConstants.CLINICAL_STATUS_SYSTEM,
							FHIRConstants.CLINICAL_STATUS_ACTIVE_CODE, FHIRConstants.CLINICAL_STATUS_ACTIVE_DISPLAY));
					allergy.setClinicalStatus(clinicalStatus);
				}
				if (allergy.getCode() == null || allergy.getCode().getCoding().isEmpty()) {
					CodeableConcept code = new CodeableConcept();
					code.addCoding(new Coding(FHIRConstants.SNOMED_SYSTEM, FHIRConstants.ALLERGIC_DISORDER_CODE,
							FHIRConstants.ALLERGIC_DISORDER_DISPLAY));
					code.setText(allergyText.trim());
					allergy.setCode(code);
				}
				allergy.setPatient(new Reference(patientFullUrl));
				allergies.add(allergy);
			}
		}
	}
		return allergies;
	}
}
