package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.model.IPatient;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RiskFactorResource {

	public static List<Observation> createRiskFactors(Reference patientReference, IPatient sourcePatient) {
		List<Observation> riskFactors = new ArrayList<>();
		Observation riskFactor = new Observation();
		riskFactor.setId(UUID.randomUUID().toString());
		riskFactor.getMeta().addProfile(FHIRConstants.PROFILE_RISK_FACTOR_OBSERVATION);
		riskFactor.setStatus(Observation.ObservationStatus.FINAL);

		riskFactor.addCategory(new CodeableConcept()
				.addCoding(new Coding().setSystem(FHIRConstants.OBSERVATION_CATEGORY_SYSTEM)
						.setCode(FHIRConstants.SOCIAL_HISTORY_CODE_CAT)
						.setDisplay(FHIRConstants.SOCIAL_HISTORY_DISPLAY_CAT)));

		riskFactor.setCode(new CodeableConcept().addCoding(
				new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM).setCode(FHIRConstants.RISK_FACTOR_CODE)
						.setDisplay(FHIRConstants.RISK_FACTOR_DISPLAY)));

		String risk = sourcePatient.getRisk();
		String riskDisplay = (risk == null || risk.isEmpty()) ? "" : risk;

		riskFactor.setValue(new CodeableConcept()
				.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM)
						.setCode(FHIRConstants.RISK_ASSESSMENT_CODE).setDisplay(FHIRConstants.RISK_ASSESSMENT_DISPLAY))
				.setText(riskDisplay));

		riskFactor.setSubject(patientReference);
		riskFactors.add(riskFactor);

		return riskFactors;
	}
}
