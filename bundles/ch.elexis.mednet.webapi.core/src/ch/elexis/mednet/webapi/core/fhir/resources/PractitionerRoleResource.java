package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.UUID;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

public class PractitionerRoleResource {

	public static PractitionerRole createPractitionerRole(Practitioner practitioner, Organization organization) {
		PractitionerRole practitionerRole = new PractitionerRole();
		practitionerRole.setId(UUID.randomUUID().toString());
		practitionerRole.getMeta().addProfile(FHIRConstants.PROFILE_PRACTITIONER_ROLE);

		practitionerRole.setPractitioner(new Reference(FHIRConstants.UUID_PREFIX + practitioner.getId()));
		if (organization != null) {
			practitionerRole.setOrganization(new Reference(FHIRConstants.UUID_PREFIX + organization.getId()));
		}

		return practitionerRole;
	}
}
