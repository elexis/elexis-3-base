package ch.elexis.mednet.webapi.core.fhir.resources.util;

import java.io.File;

import ch.elexis.core.services.holder.ConfigServiceHolder;

public class OmnivoreUtil {

	private static final String STOREFSGLOBAL = "ch.elexis.omnivore/store_in_fs_global";

	private static final String STOREFS = "ch.elexis.omnivore/store_in_fs";

	private static final String BASEPATH = "ch.elexis.omnivore/basepath";

	/**
	 * reload the fs settings store
	 */
	private static boolean isGlobalConfig() {
		return ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false);
	}

	public static boolean storeInFilesystem() {
		return isGlobalConfig() ? ConfigServiceHolder.getGlobal(STOREFS, false)
				: ConfigServiceHolder.getLocal(STOREFS, false);
	}

	public static String getBasepath() {
		String ret = isGlobalConfig() ? ConfigServiceHolder.getGlobal(BASEPATH, "")
				: ConfigServiceHolder.getLocal(BASEPATH, "");
		if (ret.contains("no protocol: ")) {
			ret = ret.replaceAll("no protocol: ", "");
		}
		return ret;
	}

	public static boolean isBasePathSet() {
		return isGlobalConfig() ? ConfigServiceHolder.getGlobal(BASEPATH, null) != null
				: ConfigServiceHolder.getLocal(BASEPATH, null) != null;
	}

	public static boolean isValidDocumentsDirectory(File documentsDirectory) {
		return documentsDirectory.exists() && documentsDirectory.isDirectory()
				&& hasTextSubdirectory(documentsDirectory);
	}

	private static boolean hasTextSubdirectory(File documentsDirectory) {
		File[] subFiles = documentsDirectory.listFiles();
		if (subFiles != null) {
			for (File file : subFiles) {
				if (file != null && file.isDirectory()) {
					String lowerName = file.getName().toLowerCase();
					if (lowerName.contains("texte") && Character.isDigit(lowerName.charAt(0))) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
