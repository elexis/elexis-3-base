package ch.elexis.omnivore.model.internal;

import static ch.elexis.omnivore.PreferenceConstants.BASEPATH;
import static ch.elexis.omnivore.PreferenceConstants.STOREFS;
import static ch.elexis.omnivore.PreferenceConstants.STOREFSGLOBAL;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.services.holder.ConfigServiceHolder;

public class Preferences {

	public static boolean storeInFilesystem() {
		if (ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false)) {
			return ConfigServiceHolder.getGlobal(STOREFS, false);
		} else {
			return ConfigServiceHolder.getLocal(STOREFS, false);
		}
	}

	public static String getBasepath() {
		String ret = StringUtils.EMPTY;
		if (ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false)) {
			ret = ConfigServiceHolder.getGlobal(BASEPATH, null);
		} else {
			ret = ConfigServiceHolder.getLocal(BASEPATH, null);
		}
		if (ret.contains("no protocol: ")) { //$NON-NLS-1$
			ret = ret.replaceAll("no protocol: ", StringUtils.EMPTY); //$NON-NLS-1$
		}
		return ret;
	}
}
