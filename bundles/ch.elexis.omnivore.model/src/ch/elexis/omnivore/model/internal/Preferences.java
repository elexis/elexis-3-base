package ch.elexis.omnivore.model.internal;

import static ch.elexis.omnivore.PreferenceConstants.BASEPATH;
import static ch.elexis.omnivore.PreferenceConstants.STOREFS;
import static ch.elexis.omnivore.PreferenceConstants.STOREFSGLOBAL;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.omnivore.model.util.Utils;

public class Preferences {

	public static boolean storeInFilesystem() {
		if (StringUtils.isNotEmpty(System.getProperty(Utils.DEMO_DOCUMENTS))) {
			return true;
		}
		if (ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false)) {
			return ConfigServiceHolder.getGlobal(STOREFS, false);
		} else {
			return ConfigServiceHolder.getLocal(STOREFS, false);
		}
	}

	public static String getBasepath() {
		String demoPath = System.getProperty(Utils.DEMO_DOCUMENTS);
		if (StringUtils.isNotEmpty(demoPath)) {
			File f = new File(demoPath);
			if (!f.isAbsolute()) {
				f = new File(System.getProperty("user.dir"), demoPath);
			}
			try {
				f = f.getCanonicalFile();
			} catch (IOException e) {
				f = f.getAbsoluteFile();
			}

			if (!f.exists()) {
				if (f.mkdirs()) {
					LoggerFactory.getLogger(Preferences.class).info("Created missing demo path for Dokumente: {}",
							f.getAbsolutePath());
				} else {
					LoggerFactory.getLogger(Preferences.class).warn("Could not create demo path for Dokumente: {}",
							f.getAbsolutePath());
				}
			}
			LoggerFactory.getLogger(Preferences.class).info("Demo mode enabled – documents are saved under: {}",
					f.getAbsolutePath());
			return f.getAbsolutePath();
		}

		String ret = StringUtils.EMPTY;
		if (ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false)) {
			ret = ConfigServiceHolder.getGlobal(BASEPATH, null);
		} else {
			ret = ConfigServiceHolder.getLocal(BASEPATH, null);
		}
		if (StringUtils.contains(ret, "no protocol: ")) { //$NON-NLS-1$
			ret = ret.replaceAll("no protocol: ", StringUtils.EMPTY); //$NON-NLS-1$
		}
		return ret;
	}
}
