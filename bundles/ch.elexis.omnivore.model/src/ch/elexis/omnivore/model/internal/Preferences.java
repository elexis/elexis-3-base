package ch.elexis.omnivore.model.internal;

import static ch.elexis.omnivore.PreferenceConstants.BASEPATH;
import static ch.elexis.omnivore.PreferenceConstants.STOREFS;
import static ch.elexis.omnivore.PreferenceConstants.STOREFSGLOBAL;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.services.holder.ConfigServiceHolder;

public class Preferences {

	private static final String DEMO_DOCUMENTS = "ch.elexis.documents"; //$NON-NLS-1$

	public static boolean storeInFilesystem() {
		if (StringUtils.isNotEmpty(System.getProperty(DEMO_DOCUMENTS))) {
			return true;
		}
		if (ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false)) {
			return ConfigServiceHolder.getGlobal(STOREFS, false);
		} else {
			return ConfigServiceHolder.getLocal(STOREFS, false);
		}
	}

	public static String getBasepath() {
		String demoPath = System.getProperty(DEMO_DOCUMENTS);
		if (StringUtils.isNotEmpty(demoPath)) {
			File f = new File(demoPath);
			if (!f.isAbsolute()) {
				f = new File(System.getProperty("user.dir"), demoPath);
			}
			try {
				return f.getCanonicalPath();
			} catch (IOException e) {
				return f.getAbsolutePath();
			}
		}

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
