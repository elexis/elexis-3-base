package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.findings.ui.services.FindingsServiceComponent;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class RiskFactorResource {

	public static List<Observation> createRiskFactors(Reference patientReference, IPatient sourcePatient,
			FhirResourceFactory resourceFactory) {
		List<Observation> riskFactors = new ArrayList<>();
		boolean structuredRiskCheck = ConfigServiceHolder.getGlobal(IMigratorService.RISKFACTOR_SETTINGS_USE_STRUCTURED,
				false);
		if (structuredRiskCheck) {
			List<IObservation> structuredRisks = FindingsServiceComponent.getService()
					.getPatientsFindings(sourcePatient.getId(), IObservation.class);

			for (IObservation structuredRisk : structuredRisks) {
				Observation riskFactor = resourceFactory.getResource(structuredRisk, IObservation.class,
						Observation.class);
				if (riskFactor == null) {
					riskFactor = createDefaultObservation(patientReference, sourcePatient.getRisk());
				}
				if (riskFactor.getId() == null || riskFactor.getId().isEmpty()) {
					riskFactor.setId(UUID.randomUUID().toString());
				}
				applyStandardCodes(riskFactor, patientReference, structuredRisk.getText().orElse("Unknown Risk"));
				riskFactors.add(riskFactor);
			}
		} else {
			String risk = sourcePatient.getRisk();
			if (risk != null && !risk.isEmpty()) {
				Observation riskFactor = createDefaultObservation(patientReference, risk);
				riskFactors.add(riskFactor);
			}
		}
		return riskFactors;
	}

	private static Observation createDefaultObservation(Reference patientReference, String risk) {
		Observation riskFactor = new Observation();
		riskFactor.setId(UUID.randomUUID().toString());
		applyStandardCodes(riskFactor, patientReference, risk);
		return riskFactor;
	}

	private static void applyStandardCodes(Observation riskFactor, Reference patientReference, String risk) {
		riskFactor.getMeta().addProfile(FHIRConstants.PROFILE_RISK_FACTOR_OBSERVATION);
		riskFactor.setStatus(Observation.ObservationStatus.FINAL);

		riskFactor.addCategory(new CodeableConcept().addCoding(new Coding()
				.setSystem(FHIRConstants.OBSERVATION_CATEGORY_SYSTEM).setCode(FHIRConstants.SOCIAL_HISTORY_CODE_CAT)
				.setDisplay(FHIRConstants.SOCIAL_HISTORY_DISPLAY_CAT)));

		riskFactor.setCode(new CodeableConcept().addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM)
				.setCode(FHIRConstants.RISK_FACTOR_CODE).setDisplay(FHIRConstants.RISK_FACTOR_DISPLAY)));

		String riskDisplay = (risk == null || risk.isEmpty()) ? "" : risk;

		riskFactor.setValue(new CodeableConcept()
				.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM)
						.setCode(FHIRConstants.RISK_ASSESSMENT_CODE).setDisplay(FHIRConstants.RISK_ASSESSMENT_DISPLAY))
				.setText(riskDisplay));
		riskFactor.setSubject(patientReference);
	}
}
