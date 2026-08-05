package ch.elexis.laborimport.hl7.universal;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;

public class HL7ImportDirectory {

	public static final String CFG_DIRECTORY = "hl7/downloaddir"; //$NON-NLS-1$
	public static final String CFG_DIRECTORY_GLOBAL = "hl7/downloaddir_global"; //$NON-NLS-1$

	private HL7ImportDirectory() {
	}

	public static boolean isStoreGlobal() {
		return isStoreGlobal(ConfigServiceHolder.get());
	}

	public static boolean isStoreGlobal(IConfigService configService) {
		return configService.get(CFG_DIRECTORY_GLOBAL, false);
	}

	public static String getDirectory() {
		return getDirectory(ConfigServiceHolder.get());
	}

	public static String getDirectory(IConfigService configService) {
		return PreferencesUtil.getOsSpecificPreference(CFG_DIRECTORY, isStoreGlobal(configService), configService);
	}

	public static Optional<IVirtualFilesystemHandle> getDirectoryHandle() {
		return getDirectoryHandle(VirtualFilesystemServiceHolder.get(), getDirectory());
	}

	public static Optional<IVirtualFilesystemHandle> getDirectoryHandle(IVirtualFilesystemService vfsService,
			String directory) {
		if (StringUtils.isBlank(directory)) {
			return Optional.empty();
		}
		try {
			IVirtualFilesystemHandle handle = vfsService.of(directory);
			if (handle.exists() && handle.isDirectory()) {
				return Optional.of(handle);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(HL7ImportDirectory.class).warn("Could not access HL7 import directory [{}]",
					IVirtualFilesystemService.hidePasswordInUrlString(directory), e);
		}
		return Optional.empty();
	}

	public static void migrateLegacySetting() {
		IConfigService configService = ConfigServiceHolder.get();
		PreferencesUtil.migrateToOsSpecificPreference(CFG_DIRECTORY, isStoreGlobal(configService), configService);
	}
}
