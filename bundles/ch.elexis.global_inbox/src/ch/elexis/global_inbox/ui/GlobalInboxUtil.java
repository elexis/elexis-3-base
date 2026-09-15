package ch.elexis.global_inbox.ui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

public class GlobalInboxUtil {

	public static final String CATEGORY_INBOX_ROOT = "-"; //$NON-NLS-1$

	private static IConfigService configService;

	private Logger logger;

	public GlobalInboxUtil() {
		logger = LoggerFactory.getLogger(getClass());
	}

	/**
	 * Try to import the file for the patient, will delete <code>file</code> if
	 * import was successful
	 *
	 * @param file
	 * @param patientNo
	 * @param fileName
	 * @return the document id if import was successful, else <code>null</code>
	 */
	public @Nullable String tryImportForPatient(IVirtualFilesystemHandle file, String patientNo, String fileName,
			String category) {
		List<Patient> lPat = new Query(Patient.class, Patient.FLD_PATID, patientNo).execute();
		if (lPat.size() == 1) {
			if (!isFileOpened(file)) {
				Patient pat = lPat.get(0);
				String cat = CATEGORY_INBOX_ROOT.equals(category) ? null : category;
				IDocumentManager dm = (IDocumentManager) Extensions
						.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
				try {

					long heapSize = Runtime.getRuntime().totalMemory();
					long length = file.getContentLenght();
					if (length >= heapSize) {
						logger.warn("Skipping " + toLogString(file) + " as bigger than heap size. (#3652)"); //$NON-NLS-1$ //$NON-NLS-2$
						return null;
					}

					GenericDocument fd = new GenericDocument(pat, fileName, cat, file.readAllBytes(),
							new TimeTool().toString(TimeTool.DATE_GER), StringUtils.EMPTY, file.getName());
					file.delete();

					boolean automaticBilling = ConfigServiceHolder.get().getLocal(Preferences.PREF_AUTOBILLING, false);
					return dm.addDocument(fd, automaticBilling);

				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.alert(Messages.InboxView_error, ex.getMessage());
				}
			}
		}

		return null;

	}

	private boolean isFileOpened(IVirtualFilesystemHandle handle) {
		Optional<File> localFile = handle.toFile();
		if (localFile.isEmpty()) {
			return false;
		}
		return isFileOpened(localFile.get());
	}

	private boolean isFileOpened(File file) {
		try (FileChannel channel = new RandomAccessFile(file, "rw").getChannel();) { //$NON-NLS-1$
			// Get an exclusive lock on the whole file
			try (FileLock lock = channel.lock();) {
				// we got a lock so this file is not opened
				return false;
			} catch (OverlappingFileLockException e) {
				// default file is opened ...
			}
		} catch (IOException e) {
			// default file is opened ...
		}
		return true;
	}

	public static String getDirectory(String defaultValue, IConfigService configService) {
		if (GlobalInboxUtil.configService == null) {
			GlobalInboxUtil.configService = configService;
		}
		IConfigService cfg = GlobalInboxUtil.configService;
		if (cfg == null) {
			return defaultValue;
		}
		boolean isGlobal = cfg.get(Preferences.STOREFSGLOBAL, false);
		String value = read(cfg, isGlobal,
				PreferencesUtil.getOsSpecificPreferenceName(CoreUtil.getOperatingSystemType(), Preferences.PREF_DIR));
		if (StringUtils.isBlank(value)) {
			value = read(cfg, isGlobal, Preferences.PREF_DIR);
		}
		return StringUtils.isBlank(value) ? defaultValue : value;
	}

	private static String read(IConfigService cfg, boolean isGlobal, String preferenceName) {
		return isGlobal ? cfg.get(preferenceName, null) : cfg.getLocal(preferenceName, null);
	}

	/**
	 * Resolve the configured inbox directory as a handle. The configured value may
	 * be a filesystem URI (file, smb, dav, davs) or a legacy plain path, both are
	 * handled by the {@link IVirtualFilesystemService}.
	 *
	 * @return the inbox directory, or empty if not configured or not resolvable
	 */
	public static Optional<IVirtualFilesystemHandle> getDirectoryHandle() {
		String dir = getDirectory(null, null);
		if (StringUtils.isBlank(dir)) {
			return Optional.empty();
		}
		try {
			return Optional.of(asDirectoryHandle(dir));
		} catch (IOException e) {
			LoggerFactory.getLogger(GlobalInboxUtil.class).error("Could not resolve inbox directory [{}]", //$NON-NLS-1$
					IVirtualFilesystemService.hidePasswordInUrlString(dir), e);
			return Optional.empty();
		}
	}

	private static IVirtualFilesystemHandle asDirectoryHandle(String path) throws IOException {
		String directoryPath = (path.endsWith("/") || path.endsWith("\\")) ? path : path + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return VirtualFilesystemServiceHolder.get().of(directoryPath);
	}

	public static String toLogString(IVirtualFilesystemHandle handle) {
		return IVirtualFilesystemService.hidePasswordInUrlString(handle.getAbsolutePath());
	}

	public void removeFiles(GlobalInboxEntry globalInboxEntry) {
		delete(globalInboxEntry.getMainFile());
		for (IVirtualFilesystemHandle extensionFile : globalInboxEntry.getExtensionFiles()) {
			delete(extensionFile);
		}
	}

	private void delete(IVirtualFilesystemHandle handle) {
		try {
			handle.delete();
		} catch (IOException e) {
			logger.warn("Could not delete " + toLogString(handle), e); //$NON-NLS-1$
		}
	}
}
