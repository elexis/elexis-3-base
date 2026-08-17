package ch.elexis.base.messages;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;

/**
 * Access to the notification sound path. The path is stored per operating
 * system, either for the active user or globally for the whole installation.
 */
public class MessageSoundSettings {

	/**
	 * Marker for the sound shipped with this bundle, resolved as a classpath
	 * resource and never as a file system location.
	 */
	public static final String DEF_SOUND_PATH = "/sounds/notify_sound.wav"; //$NON-NLS-1$

	public static final String CFG_SOUND_PATH = Preferences.USR_MESSAGES_SOUND_PATH;
	public static final String CFG_SOUND_PATH_GLOBAL = "messages/soundpath_global"; //$NON-NLS-1$

	private MessageSoundSettings() {
	}

	public static boolean isStoreGlobal() {
		return isStoreGlobal(ConfigServiceHolder.get());
	}

	public static boolean isStoreGlobal(IConfigService configService) {
		return configService.get(CFG_SOUND_PATH_GLOBAL, false);
	}

	/**
	 * @return the configured sound path, {@link #DEF_SOUND_PATH} if nothing is
	 *         configured
	 */
	public static String getSoundPath() {
		IConfigService configService = ConfigServiceHolder.get();
		String value = isStoreGlobal(configService)
				? PreferencesUtil.getOsSpecificGlobalPreference(CFG_SOUND_PATH, configService)
				: getUserPreference(configService);
		return StringUtils.isBlank(value) ? DEF_SOUND_PATH : value;
	}

	private static String getUserPreference(IConfigService configService) {
		return ContextServiceHolder.get().getActiveUserContact()
				.map(contact -> PreferencesUtil.getOsSpecificContactPreference(CFG_SOUND_PATH, contact, configService))
				.orElseGet(() -> PreferencesUtil.getOsSpecificGlobalPreference(CFG_SOUND_PATH, configService));
	}

	/**
	 * Resolve the configured location to a {@link IVirtualFilesystemHandle}, so
	 * locations that are not backed by the local file system, e.g. smb, also work.
	 *
	 * @param pathOrUri
	 * @return the handle if it exists, {@link Optional#empty()} otherwise
	 */
	public static Optional<IVirtualFilesystemHandle> resolveHandle(String pathOrUri) {
		if (StringUtils.isBlank(pathOrUri)) {
			return Optional.empty();
		}
		IVirtualFilesystemService vfsService = VirtualFilesystemServiceHolder.get();
		try {
			return toExistingHandle(vfsService.of(pathOrUri));
		} catch (IOException e) {
			// an existing configuration may hold a relative path, which is not a valid URI
			// - resolve it the way it was resolved before the URI support was added
			LoggerFactory.getLogger(MessageSoundSettings.class).debug(
					"Sound path [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
			return resolveLegacyHandle(vfsService, pathOrUri);
		}
	}

	private static Optional<IVirtualFilesystemHandle> resolveLegacyHandle(IVirtualFilesystemService vfsService,
			String path) {
		try {
			return toExistingHandle(vfsService.of(new File(path)));
		} catch (IOException e) {
			LoggerFactory.getLogger(MessageSoundSettings.class).warn("Could not access sound file [{}]", path, e); //$NON-NLS-1$
			return Optional.empty();
		}
	}

	private static Optional<IVirtualFilesystemHandle> toExistingHandle(IVirtualFilesystemHandle handle)
			throws IOException {
		if (handle != null && handle.exists()) {
			return Optional.of(handle);
		}
		return Optional.empty();
	}
}
