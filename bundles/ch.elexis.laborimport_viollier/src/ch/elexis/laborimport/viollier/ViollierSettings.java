package ch.elexis.laborimport.viollier;

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

public class ViollierSettings {

	public static final String JAR_PATH = "viollier/jar_path"; //$NON-NLS-1$
	public static final String INI_PATH = "viollier/ini_path"; //$NON-NLS-1$
	public static final String DL_DIR = "viollier/downloaddir"; //$NON-NLS-1$
	public static final String CFG_PATHS_GLOBAL = "viollier/paths_global"; //$NON-NLS-1$

	private ViollierSettings() {
	}

	public static boolean isStoreGlobal() {
		return isStoreGlobal(ConfigServiceHolder.get());
	}

	public static boolean isStoreGlobal(IConfigService configService) {
		return configService.get(CFG_PATHS_GLOBAL, false);
	}

	public static String get(String preference) {
		IConfigService configService = ConfigServiceHolder.get();
		return isStoreGlobal(configService) ? PreferencesUtil.getOsSpecificGlobalPreference(preference, configService)
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
			Optional<File> resolved = vfsService.of(pathOrUri).toFile();
			if (resolved.isPresent()) {
				return resolved;
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(ViollierSettings.class).debug(
					"Location [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
		}
		// no file system location, e.g. a relative path of an existing installation
		return Optional.of(new File(pathOrUri));
	}
}
