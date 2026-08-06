package ch.elexis.laborimport.bioanalytica;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;

public class BioanalyticaSettings {

	public static final String JAR_PATH = "bioanalytica/jar_path"; //$NON-NLS-1$
	public static final String INI_PATH = "bioanalytica/ini_path"; //$NON-NLS-1$
	public static final String DL_DIR = "bioanalytica/downloaddir"; //$NON-NLS-1$
	public static final String CFG_PATHS_GLOBAL = "bioanalytica/paths_global"; //$NON-NLS-1$

	private BioanalyticaSettings() {
	}

	public static boolean isStoreGlobal() {
		return isStoreGlobal(ConfigServiceHolder.get());
	}

	public static boolean isStoreGlobal(IConfigService configService) {
		return configService.get(CFG_PATHS_GLOBAL, false);
	}

	public static String get(String preference) {
		IConfigService configService = ConfigServiceHolder.get();
		return isStoreGlobal(configService)
				? PreferencesUtil.getOsSpecificGlobalPreference(preference, configService)
				: PreferencesUtil.getOsSpecificLocalPreference(preference, configService);
	}

	public static Optional<File> getJarFile() {
		return resolveLocalFile(get(JAR_PATH));
	}

	public static Optional<File> getIniFile() {
		return resolveLocalFile(get(INI_PATH));
	}

	public static Optional<File> getDownloadDirectory() {
		return resolveLocalFile(get(DL_DIR));
	}

	public static Optional<File> resolveLocalFile(String pathOrUri) {
		if (StringUtils.isBlank(pathOrUri)) {
			return Optional.empty();
		}
		IVirtualFilesystemService vfsService = VirtualFilesystemServiceHolder.get();
		if (vfsService == null) {
			return Optional.of(new File(pathOrUri));
		}
		try {
			return vfsService.of(pathOrUri).toFile();
		} catch (IOException e) {
			LoggerFactory.getLogger(BioanalyticaSettings.class).warn("Could not resolve location [{}]", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri), e);
			return Optional.empty();
		}
	}
}
