package ch.elexis.global_inbox.core.util;

import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.cdi.PortableServiceLoader;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.MimeType;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;

public class ImportOmnivoreInboxUtil {

	private final Logger logger = LoggerFactory.getLogger(ImportOmnivoreInboxUtil.class);

	/**
	 * Tries to import a file for a specific patient. Deletes the file upon
	 * successful import.
	 *
	 * @param file      the virtual filesystem handle of the file to import
	 * @param patientNo the patient identification number
	 * @param fileName  the target name of the document
	 * @return the document ID if the import was successful, otherwise null
	 */
	public @Nullable String tryImportForPatient(IVirtualFilesystemHandle file, String patientNo, String fileName) {
		INamedQuery<IPatient> namedQuery = PortableServiceLoader.getCoreModelService().getNamedQuery(IPatient.class,
				"code");
		Optional<IPatient> loaded = namedQuery
				.executeWithParametersSingleResult(namedQuery.getParameterMap("code", patientNo));

		if (loaded.isPresent()) {
			IDocumentStore documentStore = OmnivoreDocumentStoreServiceHolder.get();
			IPatient patient = loaded.get();
			String category = getCategory(file);

			if (category.equals("-") || category.equals("??")) {
				category = null;
			}

			try {
				long heapSize = Runtime.getRuntime().totalMemory();
				long length = file.getContentLenght();
				if (length >= heapSize) {
					logger.warn("Skipping file [{}] as it is bigger than the available heap size.",
							file.getAbsolutePath());
					return null;
				}

				IDocument newDocument = documentStore.createDocument(patient.getId(), fileName, category);
				String extension = getFileExtension(file);

				if (extension != null && (newDocument.getMimeType() == null || newDocument.getMimeType().isEmpty())) {
					MimeType mimeType = MimeType.getByExtension(extension);
					if (mimeType != MimeType.undefined) {
						newDocument.setMimeType(mimeType.getContentType());
					} else {
						newDocument.setMimeType(file.getName());
					}
				}

				try (InputStream contentStream = file.openInputStream()) {
					documentStore.saveDocument(newDocument, contentStream);
				}
				file.delete();
				return newDocument.getId();
			} catch (Exception ex) {
				logger.error("An error occurred while trying to import the document for patient with ID [{}].",
						patientNo, ex);
			}
		}
		return null;
	}

	private String getFileExtension(IVirtualFilesystemHandle file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1 || lastIndexOf == 0) {
			return StringUtils.EMPTY;
		}
		return name.substring(lastIndexOf + 1);
	}

	/**
	 * Retrieves the directory configuration for a specific device.
	 *
	 * @param defaultValue the fallback directory if none is configured
	 * @param deviceName   the name of the device
	 * @return the configured directory or the default value
	 */
	public String getDirectory(String defaultValue, String deviceName) {
		try {
			String deviceDir = PortableServiceLoader.get(IConfigService.class)
					.getGlobal(Constants.PREF_DEVICE_DIR_PREFIX + deviceName, defaultValue);
			if (deviceDir == null) {
				logger.warn("Directory for device [{}] is null. Using default value [{}].", deviceName, defaultValue);
			}
			return deviceDir != null ? deviceDir : defaultValue;
		} catch (Exception ex) {
			logger.error("Error while fetching directory for device [{}].", deviceName, ex);
			return defaultValue;
		}
	}

	/**
	 * Determines the category for a given file based on its parent directory.
	 *
	 * @param file the file to check
	 * @return the resolved category name
	 */
	public String getCategory(IVirtualFilesystemHandle file) {
		try {
			String category = PortableServiceLoader.get(IConfigService.class)
					.getGlobal(Constants.PREF_LAST_SELECTED_CATEGORY, "default");
			IVirtualFilesystemHandle parent = file.getParent();
			if (parent == null) {
				logger.warn("Parent directory for file [{}] is null.", file.getAbsolutePath());
				return "Error in inbox path";
			}

			String folderName = parent.getAbsolutePath();
			if (folderName.startsWith(category)) {
				return parent.getName();
			} else {
				return category;
			}
		} catch (Exception ex) {
			logger.error("Error while determining category for file [{}].", file.getAbsolutePath(), ex);
			return "Error in category resolution";
		}
	}

	/**
	 * Formats the document name according to the device configuration.
	 *
	 * @param originalFileName the original name of the file
	 * @param deviceName       the name of the device
	 * @return the formatted document name
	 */
	public String formatDocumentName(String originalFileName, String deviceName) {
		boolean useSuffix = PortableServiceLoader.get(IConfigService.class)
				.getGlobal(Constants.PREF_SUFFIX_MODE_PREFIX + deviceName, false);

		int lastDotIndex = originalFileName.lastIndexOf('.');
		String nameWithoutExt = lastDotIndex > 0 ? originalFileName.substring(0, lastDotIndex) : originalFileName;
		String extension = lastDotIndex > 0 ? originalFileName.substring(lastDotIndex) : StringUtils.EMPTY;

		if (useSuffix) {
			return nameWithoutExt + "_" + deviceName + extension;
		} else {
			return originalFileName;
		}
	}
}