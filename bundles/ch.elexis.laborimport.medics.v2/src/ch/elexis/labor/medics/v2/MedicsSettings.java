package ch.elexis.labor.medics.v2;

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

/**
 * Access to the configured paths of the Medics lab connector. The values are
 * stored per operating system, either locally or globally, see
 * {@link MedicsSettings#isStoreGlobal()}.
 */
public class MedicsSettings {

	public static final String DOWNLOAD_DIR = "medics/download"; //$NON-NLS-1$
	public static final String UPLOAD_DIR = "medics/upload"; //$NON-NLS-1$
	public static final String IMED_DIR = "medics/uploadimed"; //$NON-NLS-1$
	public static final String ARCHIV_DIR = "medics/archiv"; //$NON-NLS-1$
	public static final String ERROR_DIR = "medics/error"; //$NON-NLS-1$
	public static final String CFG_PATHS_GLOBAL = "medics/paths_global"; //$NON-NLS-1$

	private MedicsSettings() {
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

	public static String getDownloadDirectory() {
		return StringUtils.defaultString(get(DOWNLOAD_DIR));
	}

	public static String getUploadDirectory() {
		return StringUtils.defaultString(get(UPLOAD_DIR));
	}

	public static String getImedUploadDirectory() {
		return StringUtils.defaultString(get(IMED_DIR));
	}

	public static String getArchivDirectory() {
		return StringUtils.defaultString(get(ARCHIV_DIR));
	}

	public static String getErrorDirectory() {
		return StringUtils.defaultString(get(ERROR_DIR));
	}

	/**
	 * Resolve the configured location. Falls back to plain file resolution, if the
	 * value is not a valid URI, as existing installations may still hold a relative
	 * path.
	 *
	 * @param pathOrUri
	 * @return the handle if the location exists
	 */
	public static Optional<IVirtualFilesystemHandle> resolveHandle(String pathOrUri) {
		if (StringUtils.isBlank(pathOrUri)) {
			return Optional.empty();
		}
		IVirtualFilesystemService vfsService = VirtualFilesystemServiceHolder.get();
		try {
			IVirtualFilesystemHandle handle = vfsService.of(pathOrUri);
			if (handle.exists()) {
				return Optional.of(handle);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(MedicsSettings.class).debug(
					"Location [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
			return resolveLegacyHandle(vfsService, pathOrUri);
		}
		return Optional.empty();
	}

	private static Optional<IVirtualFilesystemHandle> resolveLegacyHandle(IVirtualFilesystemService vfsService,
			String path) {
		try {
			IVirtualFilesystemHandle handle = vfsService.of(new File(path));
			if (handle.exists()) {
				return Optional.of(handle);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(MedicsSettings.class).warn("Could not access location [{}]", path, e); //$NON-NLS-1$
		}
		return Optional.empty();
	}

	/**
	 * Write the content to a file of the configured directory.
	 *
	 * @param directoryPathOrUri
	 * @param filename
	 * @param content
	 * @return the path of the written file
	 * @throws IOException if the directory could not be resolved or the file could
	 *                     not be written
	 */
	public static String writeToDirectory(String directoryPathOrUri, String filename, byte[] content)
			throws IOException {
		IVirtualFilesystemHandle directory = resolveHandle(directoryPathOrUri)
				.orElseThrow(() -> new IOException("Verzeichnis [" //$NON-NLS-1$
						+ IVirtualFilesystemService.hidePasswordInUrlString(directoryPathOrUri)
						+ "] nicht gefunden")); //$NON-NLS-1$
		IVirtualFilesystemHandle file = directory.subFile(filename);
		file.writeAllBytes(content);
		return file.getAbsolutePath();
	}

	/**
	 * Resolve the configured location as local file, empty if it is not a local
	 * file system location.
	 *
	 * @param pathOrUri
	 * @return
	 */
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
			LoggerFactory.getLogger(MedicsSettings.class).debug(
					"Location [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
			return Optional.of(new File(pathOrUri));
		}
	}
}
