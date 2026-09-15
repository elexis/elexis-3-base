package at.medevit.elexis.impfplan.ui.preferences;

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

public class ImpfplanSettings {

	public static final String OUTPUT_DIR = PreferencePage.PREFBASE + "outputdir"; //$NON-NLS-1$

	public static final String CFG_PATHS_GLOBAL = PreferencePage.PREFBASE + "paths_global"; //$NON-NLS-1$

	private ImpfplanSettings() {
	}

	public static boolean isStoreGlobal() {
		return isStoreGlobal(ConfigServiceHolder.get());
	}

	public static boolean isStoreGlobal(IConfigService configService) {
		return configService.get(CFG_PATHS_GLOBAL, false);
	}

	public static String get(String preference) {
		IConfigService configService = ConfigServiceHolder.get();
		if (isStoreGlobal(configService)) {
			return PreferencesUtil.getOsSpecificGlobalPreference(preference, configService);
		}
		IContact userContact = ContextServiceHolder.get().getActiveUserContact().orElse(null);
		return PreferencesUtil.getOsSpecificContactPreference(preference, userContact, configService);
	}

	public static String getOutputDirectory() {
		return resolveLocalFile(get(OUTPUT_DIR)).map(File::getAbsolutePath).orElse(StringUtils.EMPTY);
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
			LoggerFactory.getLogger(ImpfplanSettings.class).debug(
					"Location [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
		}
		return Optional.of(new File(pathOrUri));
	}
}
