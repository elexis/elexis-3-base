package org.iatrix.bestellung.rose;

import java.util.Optional;

import at.medevit.elexis.hin.auth.core.IHinAuthService;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;

public class HinAuthServiceHolder {

	private static Optional<IHinAuthService> hinAuthService;
	
	public static Optional<IHinAuthService> get() {
		if(hinAuthService == null) {
			hinAuthService = OsgiServiceUtil.getService(IHinAuthService.class);
		}
		return hinAuthService;
	}
}
