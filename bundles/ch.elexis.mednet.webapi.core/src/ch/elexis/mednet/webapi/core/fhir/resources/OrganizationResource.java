package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.Organization;

import ch.elexis.core.findings.util.fhir.transformer.mapper.IOrganizationOrganizationAttributeMapper;
import ch.elexis.core.model.IContact;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IXidService;
import ch.elexis.core.utils.OsgiServiceUtil;
import ca.uhn.fhir.rest.api.SummaryEnum;

public class OrganizationResource {
	@org.osgi.service.component.annotations.Reference
	private static IXidService xidService = OsgiServiceUtil.getService(IXidService.class).get();

	@org.osgi.service.component.annotations.Reference(target = "(" + IModelService.SERVICEMODELNAME
			+ "=ch.elexis.core.model)")
	private static IModelService coreModelService;
	private static IOrganizationOrganizationAttributeMapper organizationMapper = new IOrganizationOrganizationAttributeMapper(
			xidService);

	public static Organization createOrganization(IContact coveragePayor) {
		Organization organization = new Organization();

		organizationMapper.elexisToFhir(coveragePayor.asIOrganization(), organization, SummaryEnum.DATA, null);

		return organization;
	}
}
