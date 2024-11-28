package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.UUID;

import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.model.IUser;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class PractitionerRoleResource {

	public PractitionerRole createPractitionerRole(IUser user, Practitioner practitioner,
			FhirResourceFactory resourceFactory) {
		PractitionerRole practitionerRole = resourceFactory.getResource(user, IUser.class, PractitionerRole.class);
		if (practitionerRole == null) {
			practitionerRole = new PractitionerRole();
			practitionerRole.setId(UUID.randomUUID().toString());
		}
		practitionerRole.getMeta().addProfile(FHIRConstants.PROFILE_PRACTITIONER_ROLE);
		if (practitioner != null) {
			practitionerRole.setPractitioner(new Reference(FHIRConstants.UUID_PREFIX + practitioner.getId()));
		}
		return practitionerRole;
	}
}
