package ch.elexis.global_inbox.core.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.List;
import java.io.InputStream;
import java.io.FileInputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;

import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;

@Component
public class ImportOmnivoreInboxUtil {

	private static IDocumentStore omnivoreDocumentStore;

	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	public void setDocumentStore(IDocumentStore documentStore) {
		ImportOmnivoreInboxUtil.omnivoreDocumentStore = documentStore;
	}

	private Logger logger;

	public ImportOmnivoreInboxUtil() {
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
				String cat = ImportOmnivoreInboxUtil.getCategory(file);
				if (cat.equals("-") || cat.equals("??")) { //$NON-NLS-1$ //$NON-NLS-2$
					cat = null;
				}
				try {
					long heapSize = Runtime.getRuntime().totalMemory();
					long length = file.length();
					if (length >= heapSize) {
						logger.warn("Skipping " + file.getAbsolutePath() + " as bigger than heap size. (#3652)"); //$NON-NLS-1$ //$NON-NLS-2$
						return null;
					}
					IDocument newDocument = omnivoreDocumentStore.createDocument(pat.getId(), fileName, cat);
					String extension = getFileExtension(file);
					if (extension != null
							&& (newDocument.getMimeType() == null || newDocument.getMimeType().isEmpty())) {
						newDocument.setMimeType("." + extension);
					}
					try (InputStream contentStream = new FileInputStream(file)) {
						omnivoreDocumentStore.saveDocument(newDocument, contentStream);
					}
					file.delete();
					return newDocument.getStoreId();
				} catch (Exception ex) {
					ExHandler.handle(ex);
				}
			}
		}
		return null;
	}

	private String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1 || lastIndexOf == 0) {
			return "";
		}
		return name.substring(lastIndexOf + 1);
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
		String deviceDir = ConfigServiceHolder.get().getLocal(Constants.PREF_DEVICE_DIR_PREFIX + deviceName,
				defaultValue);
		return deviceDir != null ? deviceDir : defaultValue;
	}

	public static String getCategory(File file) {
		String category = LocalConfigService.get(Constants.PREF_LAST_SELECTED_CATEGORY, "default");// $NON-NLS-1$
		File parent = file.getParentFile();
		if (parent == null) {
			return "Error in inbox path";
		} else {
			String fname = parent.getAbsolutePath();
			if (fname.startsWith(category)) {
				return parent.getName();
			} else {
				return category;
			}
		}
	}
}