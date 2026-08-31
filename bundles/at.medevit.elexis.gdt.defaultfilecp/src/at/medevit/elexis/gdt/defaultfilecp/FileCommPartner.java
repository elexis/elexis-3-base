package at.medevit.elexis.gdt.defaultfilecp;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.gdt.constants.GDTConstants;
import ch.elexis.core.data.activator.CoreHubHelper;
import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class FileCommPartner {

	public static final String DEFAULT_COMM_PARTNER_ID = "DEFAULT"; //$NON-NLS-1$
	private static final String CFG_GDT = "GDT"; //$NON-NLS-1$
	private static final String FILETRANSFER_NAME = "fileTransferName"; //$NON-NLS-1$
	private static final String FILETRANSFER_USED_TYPE = "fileTransferUsedType"; //$NON-NLS-1$
	private static final String FILETRANSFER_DIRECTORY = "fileTransferDirectory"; //$NON-NLS-1$
	private static final String FILETRANSFER_IN_DIRECTORY = "fileTransferInDirectory"; //$NON-NLS-1$
	private static final String FILETRANSFER_OUT_DIRECTORY = "fileTransferOutDirectory"; //$NON-NLS-1$
	private static final String FILETRANSFER_LONG_ID_RECEIVER = "longIDReceiver"; //$NON-NLS-1$
	private static final String FILETRANSFER_SHORT_ID_RECEIVER = "longIDReceiver"; //$NON-NLS-1$
	private static final String FILETRANSFER_EXECUTABLE = "executable"; //$NON-NLS-1$
	private static final String FILETRANSFER_EXECUTABLE_WAIT = "executableWait"; //$NON-NLS-1$
	private static final String FILETRANSFER_VIEWEREXECUTABLE = "viewerexecutable"; //$NON-NLS-1$
	private static final String FILETRANSFER_ADDITIONAL_PARAMS = "additionalParams"; //$NON-NLS-1$
	private static final String FILETRANSFER_GUVK_DEFAULT = "guvkDefault"; //$NON-NLS-1$

	public static final String CFG_GDT_FILETRANSFER_IDS = CFG_GDT + "/fileTransferTypes"; //$NON-NLS-1$
	private static final String CFG_GDT_FILETRANSFER_GLOBAL = CFG_GDT + "fileTransferSettingsGlobal"; //$NON-NLS-1$

	public static final String COMM_PARTNER_SEPERATOR = ",;,"; //$NON-NLS-1$

	private String id;
	private final ConfigServicePreferenceStore preferenceStore;

	public FileCommPartner() {
		this(DEFAULT_COMM_PARTNER_ID);

	}

	public FileCommPartner(String id) {
		this.id = id;
		preferenceStore = new ConfigServicePreferenceStore(
				FileCommPartner.isFileTransferGlobalConfigured() ? Scope.GLOBAL : Scope.LOCAL);

		// in the past v3.1 the default key was locally configured as an other key
		// we need to transfer the old key to the new one
		if (DEFAULT_COMM_PARTNER_ID.equals(id) && !isFileTransferGlobalConfigured()) {
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferDirectory", getFileTransferDirectory(), //$NON-NLS-1$
					false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferDirectory", getFileTransferInDirectory(), //$NON-NLS-1$
					false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferDirectory", getFileTransferOutDirectory(), //$NON-NLS-1$
					false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferUsedType", getFileTransferUsedType(), //$NON-NLS-1$
					false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/longIDReceiver", getFileTransferIdReceiver(), false); //$NON-NLS-1$
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/executable", getFileTransferExecuteable(), false); //$NON-NLS-1$
		}

		getSettings().setDefault(getFileTransferExecuteableWait(), Boolean.TRUE);
	}

	public static String[][] comboCharsetSelektor = new String[][] {
			{ "7Bit", GDTConstants.ZEICHENSATZ_7BIT_CHARSET_STRING }, //$NON-NLS-1$
			{ "IBM (Standard) CP 437", GDTConstants.ZEICHENSATZ_IBM_CP_437_CHARSET_STRING }, //$NON-NLS-1$
			{ "ISO8859-1 (ANSI) CP 1252", GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING } }; //$NON-NLS-1$

	public String getFileTransferName() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_NAME; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferUsedType() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_USED_TYPE; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferDirectory() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_DIRECTORY; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferInDirectory() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_IN_DIRECTORY; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferOutDirectory() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_OUT_DIRECTORY; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferIdReceiver() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_LONG_ID_RECEIVER; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferShortIdReceiver() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_SHORT_ID_RECEIVER; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferExecuteable() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_EXECUTABLE; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferViewerExecuteable() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_VIEWEREXECUTABLE; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileAdditionalParams() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_ADDITIONAL_PARAMS; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFileTransferExecuteableWait() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_EXECUTABLE_WAIT; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getGuvkDefault() {
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_GUVK_DEFAULT; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getId() {
		return id;
	}

	public static boolean isFileTransferGlobalConfigured() {
		return ConfigServiceHolder.getGlobal(FileCommPartner.CFG_GDT_FILETRANSFER_GLOBAL, false);
	}

	public static void setFileTransferConfiguration(boolean global) {
		ConfigServiceHolder.setGlobal(FileCommPartner.CFG_GDT_FILETRANSFER_GLOBAL, global);
	}

	public static String[] getAllFileCommPartnersArray() {
		if (isFileTransferGlobalConfigured()) {
			return ConfigServiceHolder
					.getGlobal(FileCommPartner.CFG_GDT_FILETRANSFER_IDS, FileCommPartner.DEFAULT_COMM_PARTNER_ID)
					.split(FileCommPartner.COMM_PARTNER_SEPERATOR);
		} else {
			return LocalConfigService
					.get(FileCommPartner.CFG_GDT_FILETRANSFER_IDS, FileCommPartner.DEFAULT_COMM_PARTNER_ID)
					.split(FileCommPartner.COMM_PARTNER_SEPERATOR);
		}
	}

	/**
	 * Read a path of this communication partner. The value is read operating system
	 * specific from the scope currently in use, falling back to the key without
	 * operating system suffix, so existing installations keep working.
	 *
	 * @param preferenceName the base key, without the operating system suffix
	 * @return the configured location as local path, empty String if there is none
	 */
	public String getPath(String preferenceName) {
		IConfigService configService = ConfigServiceHolder.get();
		String value = isFileTransferGlobalConfigured()
				? PreferencesUtil.getOsSpecificGlobalPreference(preferenceName, configService)
				: PreferencesUtil.getOsSpecificLocalPreference(preferenceName, configService);
		return resolveLocalFile(value).map(File::getAbsolutePath).orElse(StringUtils.EMPTY);
	}

	/**
	 * Resolve a stored value to a local {@link File}. Handles plain paths, file
	 * URIs and relative paths.
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
			Optional<File> resolved = vfsService.of(pathOrUri).toFile();
			if (resolved.isPresent()) {
				return resolved;
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(FileCommPartner.class).debug(
					"Location [{}] is not a valid URI, falling back to plain file resolution", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(pathOrUri));
		}
		return Optional.of(new File(pathOrUri));
	}

	public IPreferenceStore getSettings() {
		return preferenceStore;
	}
}
