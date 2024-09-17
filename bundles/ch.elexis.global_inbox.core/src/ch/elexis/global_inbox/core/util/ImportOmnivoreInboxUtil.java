package ch.elexis.global_inbox.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.util.List;

import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.importer.div.service.holder.OmnivoreDocumentStoreServiceHolder;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ModelPackage;

import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.IQuery;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class ImportOmnivoreInboxUtil {

	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	private IDocumentStore omnivoreDocumentStore = OmnivoreDocumentStoreServiceHolder.get();

	public static final String PREFERENCE_BRANCH = "plugins/global_inbox_server/"; //$NON-NLS-1$
	public static final String PREF_DEVICE_DIR_PREFIX = PREFERENCE_BRANCH + "device_dir_"; //$NON-NLS-1$
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
		IQuery<IContact> query = CoreModelServiceHolder.get().getQuery(IContact.class);
		query.and(ModelPackage.Literals.ICONTACT__CODE, IQuery.COMPARATOR.EQUALS, patientNo);
		List<IContact> contacts = query.execute();
		if (contacts.size() == 1) {
			IContact contact = contacts.get(0);
			IPatient pat = contact.asIPatient();
			if (pat != null) {
				if (!isFileOpened(file)) {
					String cat = ImportOmnivoreInboxUtil.getCategory(file);
					if (cat.equals("-") || cat.equals("??")) {
						cat = null;
					}
					try {
						long heapSize = Runtime.getRuntime().totalMemory();
						long length = file.length();
						if (length >= heapSize) {
							logger.warn("Skipping " + file.getAbsolutePath() + " as bigger than heap size.");
							return null;
						}
						IDocument newDocument = omnivoreDocumentStore.createDocument(pat.getId(), fileName, cat);
						try (InputStream fileInputStream = Files.newInputStream(file.toPath())) {
							omnivoreDocumentStore.saveDocument(newDocument, fileInputStream);
						}
						file.delete();
						return newDocument.getId();
					} catch (Exception ex) {
						logger.warn("Fehler " + ex.getMessage());
					}
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

	public static String getCategory(File file) {
		String dir = getDirectory(Constants.PREF_DIR_DEFAULT, null);
		File parent = file.getParentFile();
		if (parent == null) {
			return "Error in inbox path";
		} else {
			String fname = parent.getAbsolutePath();
			if (fname.startsWith(dir)) {

				return parent.getName();
			} else {
				return "??"; //$NON-NLS-1$
			}
		}
	}
}