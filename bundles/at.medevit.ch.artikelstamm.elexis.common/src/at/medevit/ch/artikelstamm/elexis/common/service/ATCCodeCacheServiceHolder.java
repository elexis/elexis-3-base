package at.medevit.ch.artikelstamm.elexis.common.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.atc_codes.ATCCode;
import at.medevit.ch.artikelstamm.ATCCodeCacheService;

@Component
public class ATCCodeCacheServiceHolder {
	private static ATCCodeCacheService atcCodeCacheService = null;

	@Reference
	public void setATCCodeService(ATCCodeCacheService atcCodeCacheService) {
		ATCCodeCacheServiceHolder.atcCodeCacheService = atcCodeCacheService;
	}

	public static int getAvailableArticlesByATCCode(ATCCode atcCode) {
		return atcCodeCacheService != null ? atcCodeCacheService.getAvailableArticlesByATCCode(atcCode) : 0;
	}
}
