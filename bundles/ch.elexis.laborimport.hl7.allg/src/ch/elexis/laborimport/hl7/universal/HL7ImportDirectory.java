package ch.elexis.laborimport.hl7.universal;

import java.io.File;
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
		return isStoreGlobal(configService)
				? PreferencesUtil.getOsSpecificGlobalPreference(CFG_DIRECTORY, configService)
				: PreferencesUtil.getOsSpecificLocalPreference(CFG_DIRECTORY, configService);
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
			return toDirectoryHandle(vfsService.of(directory));
		} catch (IOException e) {
			LoggerFactory.getLogger(HL7ImportDirectory.class).debug(
					"HL7 import directory [{}] is not a valid URI, falling back to plain file resolution",
					IVirtualFilesystemService.hidePasswordInUrlString(directory));
			return getLegacyDirectoryHandle(vfsService, directory);
		}
	}

	private static Optional<IVirtualFilesystemHandle> getLegacyDirectoryHandle(IVirtualFilesystemService vfsService,
			String directory) {
		try {
			return toDirectoryHandle(vfsService.of(new File(directory)));
		} catch (IOException e) {
			LoggerFactory.getLogger(HL7ImportDirectory.class).warn("Could not access HL7 import directory [{}]",
					directory, e);
			return Optional.empty();
		}
	}

	private static Optional<IVirtualFilesystemHandle> toDirectoryHandle(IVirtualFilesystemHandle handle)
			throws IOException {
		if (handle != null && handle.exists() && handle.isDirectory()) {
			return Optional.of(handle);
		}
		return Optional.empty();
	}
}
