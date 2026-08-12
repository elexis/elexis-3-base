package at.medevit.elexis.ehc.ui.preference;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IContact;
import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;

public class EhcSettings {

	public static final String CFG_PATHS_GLOBAL = "at.medevit.elexis.ehc.ui.paths_global"; //$NON-NLS-1$

	private EhcSettings() {
	}

	public static boolean isStoreGlobal() {
		return isStoreGlobal(ConfigServiceHolder.get());
	}

	public static boolean isStoreGlobal(IConfigService configService) {
		return configService.get(CFG_PATHS_GLOBAL, false);
	}

	public static String getOutputDir() {
		return resolvePath(get(PreferencePage.EHC_OUTPUTDIR), PreferencePage.getDefaultOutputDir());
	}

	public static String getInputDir() {
		return resolvePath(get(PreferencePage.EHC_INPUTDIR), PreferencePage.getDefaultInputDir());
	}

	public static String get(String preference) {
		IConfigService configService = ConfigServiceHolder.get();
		if (isStoreGlobal(configService)) {
			return PreferencesUtil.getOsSpecificGlobalPreference(preference, configService);
		}
		IContact activeUser = ContextServiceHolder.get().getActiveUserContact().orElse(null);
		if (activeUser == null) {
			return PreferencesUtil.getOsSpecificGlobalPreference(preference, configService);
		}
		return PreferencesUtil.getOsSpecificContactPreference(preference, activeUser, configService);
	}

	private static String resolvePath(String configured, String defaultPath) {
		if (StringUtils.isBlank(configured)) {
			return defaultPath;
		}
		return resolveLocalFile(configured).map(File::getAbsolutePath).orElse(configured);
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
			LoggerFactory.getLogger(EhcSettings.class).debug(
					"Location [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
		}
		return Optional.of(new File(pathOrUri));
	}
}
