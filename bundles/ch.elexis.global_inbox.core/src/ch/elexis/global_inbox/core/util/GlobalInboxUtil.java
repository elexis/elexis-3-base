package ch.elexis.global_inbox.core.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.global_inbox.core.model.GlobalInboxEntry;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

public class GlobalInboxUtil {

	private static IConfigService configService = ConfigServiceHolder.get();

	public static final String PREFERENCE_BRANCH = "plugins/global_inbox_server/"; //$NON-NLS-1$
	public static final String PREF_DEVICE_DIR_PREFIX = PREFERENCE_BRANCH + "device_dir_"; //$NON-NLS-1$
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
	public @Nullable String tryImportForPatient(File file, String patientNo, String fileName) {
		List<Patient> lPat = new Query(Patient.class, Patient.FLD_PATID, patientNo).execute();
		if (lPat.size() == 1) {
			if (!isFileOpened(file)) {
				Patient pat = lPat.get(0);
				String cat = GlobalInboxUtil.getCategory(file);
				if (cat.equals("-") || cat.equals("??")) { //$NON-NLS-1$ //$NON-NLS-2$
					cat = null;
				}
				IDocumentManager dm = (IDocumentManager) Extensions
						.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
				try {

					long heapSize = Runtime.getRuntime().totalMemory();
					long length = file.length();
					if (length >= heapSize) {
						logger.warn("Skipping " + file.getAbsolutePath() + " as bigger than heap size. (#3652)"); //$NON-NLS-1$ //$NON-NLS-2$
						return null;
					}

					GenericDocument fd = new GenericDocument(pat, fileName, cat, file,
							new TimeTool().toString(TimeTool.DATE_GER), StringUtils.EMPTY, null);
					file.delete();

					boolean automaticBilling = CoreHub.localCfg.get(Constants.PREF_AUTOBILLING, false);
					return dm.addDocument(fd, automaticBilling);

				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.alert(Messages.InboxView_error, ex.getMessage());
				}
			}
		}

		return null;

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

	public static String getDirectory(String defaultValue, String deviceName) {
		String deviceDir = ConfigServiceHolder.get().getLocal(PREF_DEVICE_DIR_PREFIX + deviceName, defaultValue);
		return deviceDir != null ? deviceDir : defaultValue;
	}

	public static String getDirectory(String defaultValue) {
		boolean isGlobal = configService.get(Constants.STOREFSGLOBAL, false);
		if (isGlobal) {
			return configService.get(Constants.PREF_DIR, defaultValue);
		}
		return configService.getLocal(Constants.PREF_DIR, defaultValue);
	}

	public static String getCategory(File file) {
		String dir = getDirectory(Constants.PREF_DIR_DEFAULT, null);
		File parent = file.getParentFile();
		if (parent == null) {
			return Messages.Activator_noInbox;
		} else {
			String fname = parent.getAbsolutePath();
			if (fname.startsWith(dir)) {

				return parent.getName();
			} else {
				return "??"; //$NON-NLS-1$
			}
		}
	}

	public void removeFiles(GlobalInboxEntry globalInboxEntry) {
		File mainFile = globalInboxEntry.getMainFile();
		try {
			Files.delete(mainFile.toPath());
		} catch (IOException e) {
			logger.warn("Could not delete " + mainFile, e); //$NON-NLS-1$
		}
		File[] extensionFiles = globalInboxEntry.getExtensionFiles();
		for (File extensionFile : extensionFiles) {
			try {
				Files.delete(extensionFile.toPath());
			} catch (IOException e) {
				logger.warn("Could not delete " + extensionFile, e); //$NON-NLS-1$
			}
		}
	}

}
