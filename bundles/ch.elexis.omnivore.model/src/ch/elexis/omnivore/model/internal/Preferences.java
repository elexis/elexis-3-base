package ch.elexis.omnivore.model.internal;

import static ch.elexis.omnivore.PreferenceConstants.BASEPATH;
import static ch.elexis.omnivore.PreferenceConstants.STOREFS;

import ch.elexis.core.services.holder.ConfigServiceHolder;

public class Preferences {

	public static boolean storeInFilesystem() {
		return ConfigServiceHolder.get().get(STOREFS, false);
	}

	public static String getBasepath() {
		return ConfigServiceHolder.get().get(BASEPATH, null);
	}

}
