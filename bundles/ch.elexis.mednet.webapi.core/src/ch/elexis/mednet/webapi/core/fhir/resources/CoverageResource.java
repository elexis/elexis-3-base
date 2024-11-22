package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.findings.util.fhir.transformer.mapper.ICoverageCoverageAttributeMapper;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.services.IModelService;
import ca.uhn.fhir.rest.api.SummaryEnum;

public class CoverageResource {

	@org.osgi.service.component.annotations.Reference(target = "(" + IModelService.SERVICEMODELNAME
			+ "=ch.elexis.core.model)")
	private static IModelService coreModelService;
	private static ICoverageCoverageAttributeMapper coverageMapper = new ICoverageCoverageAttributeMapper(
			coreModelService);

	public static Coverage createCoverage(ICoverage activeCoverage, String patientFullUrl) {
		Coverage coverage = new Coverage();

		coverageMapper.elexisToFhir(activeCoverage, coverage, SummaryEnum.DATA, null);

		coverage.setBeneficiary(new Reference(patientFullUrl));
		coverage.setPolicyHolder(new Reference(patientFullUrl));

		return coverage;
	}
}
