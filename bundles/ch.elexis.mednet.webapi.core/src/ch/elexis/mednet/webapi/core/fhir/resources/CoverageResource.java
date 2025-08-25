package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Reference;

public class CoverageResource {

	public static Coverage toMednet(Coverage coverage, String patientFullUrl) {
		    coverage.setBeneficiary(new Reference(patientFullUrl));
		    coverage.setPolicyHolder(new Reference(patientFullUrl));
		    if (!coverage.hasStatus()) {
		      coverage.setStatus(Coverage.CoverageStatus.ACTIVE);
		    }
		    return coverage;
		  }
}
