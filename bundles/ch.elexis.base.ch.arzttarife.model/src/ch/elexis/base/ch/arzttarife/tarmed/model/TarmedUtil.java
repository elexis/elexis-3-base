package ch.elexis.base.ch.arzttarife.tarmed.model;

import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.ContextServiceHolder;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;

public class TarmedUtil {
	
	public static boolean getConfigValue(Class<?> requestor, Class<?> configSource,
		String parameter, boolean fallback){
		IContextService contextService = ContextServiceHolder.get().orElse(null);
		if (contextService != null) {
			IContact contact;
			if (configSource.equals(IMandator.class)) {
				contact = contextService.getActiveMandator().orElse(null);
			} else {
				contact =
					contextService.getActiveUser().map(u -> u.getAssignedContact()).orElse(null);
			}
			if (contact != null) {
				IConfigService configService = ConfigServiceHolder.get().orElse(null);
				if (configService != null) {
					boolean value =
						configService.get(contact, Preferences.LEISTUNGSCODES_OPTIFY, true);
					if (value != fallback) {
						LoggerFactory.getLogger(requestor).info("[{}] Overriden [{}] with [{}]",
							contact.getDescription3(), parameter, value);
					}
					return value;
				}
			}
		}
		return fallback;
	}
}
