package ch.elexis.mednet.webapi.ui.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.MimeType;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.mednet.webapi.core.config.MedNetConfig;
import ch.elexis.mednet.webapi.core.vfs.MedNetVfsHandler;

/**
 * Utility class to handle the import of documents into the Omnivore document
 * store. Fully supports local, SMB, and WebDAV protocols through the Virtual
 * Filesystem Service.
 */
@Component
public class ImportOmnivoreUtil {

	private static final Logger log = LoggerFactory.getLogger(ImportOmnivoreUtil.class);

	private static final String CATEGORY_NONE = "-"; //$NON-NLS-1$
	private static final String CATEGORY_UNKNOWN = "??"; //$NON-NLS-1$
	private static final String ERROR_MEDNET_PATH = "Error in Mednet path"; //$NON-NLS-1$

	private static IDocumentStore omnivoreDocumentStore;

	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	public void setDocumentStore(IDocumentStore documentStore) {
		ImportOmnivoreUtil.omnivoreDocumentStore = documentStore;
	}

	private IDocumentStore getDocumentStore() {
		return ImportOmnivoreUtil.omnivoreDocumentStore;
	}

	/**
	 * Tries to import the file for the specified patient. The file will be deleted
	 * if the import was successful.
	 *
	 * @param fileHandle the virtual file handle to import
	 * @param patientNo  the patient identification number
	 * @param fileName   the name of the file
	 * @return the document ID if import was successful, else null
	 */
	public @Nullable String tryImportForPatient(IVirtualFilesystemHandle fileHandle, String patientNo,
			String fileName) {
		IDocumentStore docStore = getDocumentStore();
		if (docStore == null) {
			log.error("Cannot proceed with import: Omnivore Document Store service is unavailable.");
			return null;
		}

		IQuery<IPatient> query = CoreModelServiceHolder.get().getQuery(IPatient.class);
		query.and(ModelPackage.Literals.ICONTACT__CODE, COMPARATOR.EQUALS, patientNo);
		List<IPatient> patients = query.execute();

		if (patients.size() == 1) {
			if (!MedNetVfsHandler.isFileLocked(fileHandle)) {
				IPatient patient = patients.get(0);
				String category = getCategory(fileHandle);

				if (category != null && !category.equals(CATEGORY_NONE) && !category.equals(CATEGORY_UNKNOWN)) {
					ensureCategoryExists(category);
				}

				try {
					long heapSize = Runtime.getRuntime().totalMemory();
					long length = fileHandle.getContentLenght(); // Correct interface spelling

					if (length >= heapSize) {
						log.warn("Skipping [{}] as it is bigger than current heap size. (#3652)",
								fileHandle.getAbsolutePath());
						return null;
					}

					IDocument newDocument = docStore.createDocument(patient.getId(), fileName, category);
					String extension = fileHandle.getExtension();

					if (extension != null
							&& (newDocument.getMimeType() == null || newDocument.getMimeType().isEmpty())) {
						MimeType mimeType = MimeType.getByExtension(extension);
						if (mimeType != MimeType.undefined) {
							newDocument.setMimeType(mimeType.getContentType());
						} else {
							newDocument.setMimeType(fileHandle.getName());
						}
					}

					try (InputStream contentStream = fileHandle.openInputStream()) {
						docStore.saveDocument(newDocument, contentStream);
					}

					try {
						fileHandle.delete();
					} catch (IOException e) {
						log.warn("Could not delete file [{}] after successful import.", fileHandle.getAbsolutePath(),
								e);
					}

					return newDocument.getId();

				} catch (Exception exception) {
					log.error("An error occurred while trying to import the document for patient with ID [{}].",
							patientNo, exception);
				}
			}
		} else {
			log.warn("Patient lookup for ID [{}] returned [{}] results. Expected exactly 1.", patientNo,
					patients.size());
		}

		return null;
	}

	/**
	 * Retrieves the download directory from the MedNet configuration.
	 *
	 * @return the configured download directory path
	 */
	public static String getDirectory() {
		MedNetConfig config = MedNetConfig.load();
		return config.getDownloadPath();
	}

	/**
	 * Derives the document category from the file's parent folder name using the
	 * VFS API.
	 *
	 * @param fileHandle the file handle to categorize
	 * @return the category name
	 */
	public static String getCategory(IVirtualFilesystemHandle fileHandle) {
		try {
			IVirtualFilesystemHandle parent = fileHandle.getParent();
			if (parent == null) {
				return ERROR_MEDNET_PATH;
			} else {
				return parent.getName();
			}
		} catch (IOException e) {
			log.warn("Could not determine category (parent folder) for file [{}].", fileHandle.getName(), e);
			return ERROR_MEDNET_PATH;
		}
	}

	/**
	 * Ensures that a category with the specified name exists in the document store.
	 *
	 * @param categoryName the name of the category to check or create
	 */
	public void ensureCategoryExists(String categoryName) {
		IDocumentStore docStore = getDocumentStore();
		if (docStore == null)
			return;

		List<ICategory> existingCategories = docStore.getCategories();
		boolean categoryExists = existingCategories.stream()
				.anyMatch(category -> categoryName.equalsIgnoreCase(category.getName()));

		if (!categoryExists) {
			createCategory(categoryName);
		}
	}

	/**
	 * Creates a new category in the document store.
	 *
	 * @param categoryName the name of the new category
	 */
	private void createCategory(String categoryName) {
		IDocumentStore docStore = getDocumentStore();
		if (docStore == null)
			return;

		ICategory newCategory = docStore.createCategory(categoryName);
		if (newCategory != null) {
			log.info("Category created successfully: [{}]", categoryName);
		} else {
			log.error("Failed to create category: [{}]", categoryName);
		}
	}
}