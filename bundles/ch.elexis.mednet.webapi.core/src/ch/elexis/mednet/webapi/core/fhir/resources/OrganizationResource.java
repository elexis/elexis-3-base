package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.Organization;
import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrganization;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

@Component
public class OrganizationResource {

	public static Organization createOrganization(IContact coveragePayor, FhirResourceFactory resourceFactory) {
		IOrganization localOrganization = coveragePayor.asIOrganization();

		if (localOrganization == null) {
			throw new IllegalArgumentException("Provided contact is not an IOrganization.");
		}

		Organization organization = resourceFactory.getResource(localOrganization, IOrganization.class,
				Organization.class);

		return organization;
	}
}
