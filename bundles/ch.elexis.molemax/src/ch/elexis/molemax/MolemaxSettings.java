package ch.elexis.molemax;

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

public class MolemaxSettings {

	public static final String BASEDIR = "molemax/imagebase"; //$NON-NLS-1$

	public static final String CUSTOM_BASEDIR = "molemax/custom_imagebase"; //$NON-NLS-1$

	public static final String CFG_PATHS_GLOBAL = "molemax/store_global"; //$NON-NLS-1$

	private MolemaxSettings() {
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

	public static String getPlain(String preference) {
		IConfigService configService = ConfigServiceHolder.get();
		String value = isStoreGlobal(configService) ? configService.get(preference, StringUtils.EMPTY)
				: configService.getLocal(preference, StringUtils.EMPTY);
		return StringUtils.defaultString(value);
	}

	public static String getImageBaseDirectory() {
		return resolveLocalFile(get(BASEDIR)).map(File::getAbsolutePath).orElse(StringUtils.EMPTY);
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
			LoggerFactory.getLogger(MolemaxSettings.class).debug(
					"Location [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
		}
		return Optional.of(new File(pathOrUri));
	}
}
