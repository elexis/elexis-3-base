package ch.elexis.global_inbox.core.util;

import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.MimeType;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class ImportOmnivoreInboxUtil {

	private static Logger logger = LoggerFactory.getLogger(ImportOmnivoreInboxUtil.class);

	/**
	 * Try to import the file for the patient, will delete <code>file</code> if
	 * import was successful
	 *
	 * @param file
	 * @param patientNo
	 * @param fileName
	 * @return the document id if import was successful, else <code>null</code>
	 */
	public @Nullable String tryImportForPatient(IVirtualFilesystemHandle file, String patientNo, String fileName) {
		INamedQuery<IPatient> namedQuery = CoreModelServiceHolder.get().getNamedQuery(IPatient.class, "code");
		Optional<IPatient> loaded = namedQuery
				.executeWithParametersSingleResult(namedQuery.getParameterMap("code", patientNo));
		if (loaded.isPresent()) {

			IDocumentStore iDocumentStore = OmnivoreDocumentStoreServiceHolder.get();

			IPatient pat = loaded.get();
			String cat = ImportOmnivoreInboxUtil.getCategory(file);
			if (cat.equals("-") || cat.equals("??")) { //$NON-NLS-1$ //$NON-NLS-2$
				cat = null;
			}
			try {
				long heapSize = Runtime.getRuntime().totalMemory();
				long length = file.getContentLenght();
				if (length >= heapSize) {
					logger.warn("Skipping " + file.getAbsolutePath() + " as bigger than heap size. (#3652)"); //$NON-NLS-1$ //$NON-NLS-2$
					return null;
				}
				IDocument newDocument = iDocumentStore.createDocument(pat.getId(), fileName, cat);
				String extension = getFileExtension(file);
				if (extension != null && (newDocument.getMimeType() == null || newDocument.getMimeType().isEmpty())) {
					MimeType mimetyp = MimeType.getByExtension(extension);
					if (mimetyp != MimeType.undefined) {
						newDocument.setMimeType(mimetyp.getContentType());
					} else {
						newDocument.setMimeType(file.getName());
					}
				}
				try (InputStream contentStream = file.openInputStream()) {
					iDocumentStore.saveDocument(newDocument, contentStream);
				}
				file.delete();
				return newDocument.getId();
			} catch (Exception ex) {
				logger.error("An error occurred while trying to import the document for patient with ID {}.", patientNo,
						ex);
			}
		}
		return null;
	}

	private String getFileExtension(IVirtualFilesystemHandle file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1 || lastIndexOf == 0) {
			return "";
		}
		return name.substring(lastIndexOf + 1);
	}

	public static String getDirectory(String defaultValue, String deviceName) {
		try {
			String deviceDir = ConfigServiceHolder.getGlobal(Constants.PREF_DEVICE_DIR_PREFIX + deviceName,
					defaultValue);
			if (deviceDir == null) {
				logger.warn("Directory for device [{}] is null. Using default value [{}].", deviceName, defaultValue);
			}
			return deviceDir != null ? deviceDir : defaultValue;
		} catch (Exception ex) {
			logger.error("Error while fetching directory for device [{}].", deviceName, ex);
			return defaultValue;
		}
	}

	public static String getCategory(IVirtualFilesystemHandle file) {
		try {
			String category = ConfigServiceHolder.getGlobal(Constants.PREF_LAST_SELECTED_CATEGORY, "default");
			IVirtualFilesystemHandle parent = file.getParent();
			if (parent == null) {
				logger.warn("Parent directory for file [{}] is null.", file.getAbsolutePath());
				return "Error in inbox path";
			}

			String fname = parent.getAbsolutePath();
			if (fname.startsWith(category)) {
				return parent.getName();
			} else {
				return category;
			}
		} catch (Exception ex) {
			logger.error("Error while determining category for file [{}].", file.getAbsolutePath(), ex);
			return "Error in category resolution";
		}
	}
}
