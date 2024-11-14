package ch.elexis.tarmedprefs;

import static ch.elexis.core.constants.XidConstants.DOMAIN_RECIPIENT_EAN;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IXidService;
import ch.elexis.data.Xid;

@Component
public class XidInitialization {

	@Reference
	private IXidService xidService;

	@Activate
	private void activate() {
		xidService.localRegisterXIDDomainIfNotExists(TarmedRequirements.DOMAIN_KSK, Messages.TarmedRequirements_kskName,
				Xid.ASSIGNMENT_REGIONAL);
		xidService.localRegisterXIDDomainIfNotExists(TarmedRequirements.DOMAIN_NIF, Messages.TarmedRequirements_NifName,
				Xid.ASSIGNMENT_REGIONAL);
		xidService.localRegisterXIDDomainIfNotExists(DOMAIN_RECIPIENT_EAN, "rEAN", //$NON-NLS-1$
				Xid.ASSIGNMENT_REGIONAL);
		xidService.localRegisterXIDDomainIfNotExists(TarmedRequirements.DOMAIN_SUVA, "Suva-Nr",
				Xid.ASSIGNMENT_REGIONAL);
	}
}
