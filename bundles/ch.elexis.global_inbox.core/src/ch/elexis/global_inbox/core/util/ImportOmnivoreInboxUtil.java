package ch.elexis.global_inbox.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.MimeType;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

@Component
public class ImportOmnivoreInboxUtil {

	private static IDocumentStore omnivoreDocumentStore;

	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	public void setDocumentStore(IDocumentStore documentStore) {
		ImportOmnivoreInboxUtil.omnivoreDocumentStore = documentStore;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

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
		INamedQuery<IPatient> namedQuery = CoreModelServiceHolder.get().getNamedQuery(IPatient.class, "code");
		Optional<IPatient> loaded = namedQuery
				.executeWithParametersSingleResult(namedQuery.getParameterMap("code", patientNo));
		if (loaded.isPresent()) {
			if (!isFileOpened(file)) {
				IPatient pat = loaded.get();
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
						MimeType mimetyp = MimeType.getByExtension(extension);
						if (mimetyp != MimeType.undefined) {
							newDocument.setMimeType(mimetyp.getContentType());
						} else {
							newDocument.setMimeType(file.getName());
						}
					}
					try (InputStream contentStream = new FileInputStream(file)) {
						omnivoreDocumentStore.saveDocument(newDocument, contentStream);
					}
					file.delete();

					return newDocument.getId();
				} catch (Exception ex) {
					logger.error("An error occurred while trying to import the document for patient with ID {}.",
							patientNo, ex);
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