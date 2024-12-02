package ch.elexis.mednet.webapi.ui.handler;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.MimeType;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.constants.TransientCategory;
import ch.elexis.mednet.webapi.core.service.OmnivoreModelServiceHolder;


@Component
public class ImportOmnivoreUtil {

		private static IDocumentStore omnivoreDocumentStore;

		@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
		public void setDocumentStore(IDocumentStore documentStore) {
			ImportOmnivoreUtil.omnivoreDocumentStore = documentStore;
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
			List<Patient> lPat = new Query(Patient.class, Patient.FLD_PATID, patientNo).execute();
			if (lPat.size() == 1) {
				if (!isFileOpened(file)) {
					Patient pat = lPat.get(0);
					String category = ImportOmnivoreUtil.getCategory(file);
					if (category != null && !category.equals("-") && !category.equals("??")) { //$NON-NLS-1$ //$NON-NLS-2$
						ensureCategoryExists(category);
					}
					try {
						long heapSize = Runtime.getRuntime().totalMemory();
						long length = file.length();
						if (length >= heapSize) {
							logger.warn("Skipping " + file.getAbsolutePath() + " as bigger than heap size. (#3652)"); //$NON-NLS-1$ //$NON-NLS-2$
							return null;
						}

						IDocument newDocument = omnivoreDocumentStore.createDocument(pat.getId(), fileName, category);
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

		public static String getDirectory(String defaultValue, IConfigService configService) {
			String pluginId = PreferenceConstants.MEDNET_PLUGIN_STRING;
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode(pluginId);
			String dir = node.get(PreferenceConstants.MEDNET_DOWNLOAD_PATH, "");
			return dir;
		}

		public static String getCategory(File file) {

			File parent = file.getParentFile();

			if (parent == null) {
				return "Error in Mednet path";
			} else {
				String category = parent.getName();
				return category;
			}
		}

		public void ensureCategoryExists(String categoryName) {
			List<ICategory> existingCategories = omnivoreDocumentStore.getCategories();
			boolean categoryExists = existingCategories.stream()
					.anyMatch(category -> categoryName.equalsIgnoreCase(category.getName()));
			if (!categoryExists) {
				createCategory(categoryName);
			}
		}

		private void createCategory(String categoryName) {
			ICategory newCategory = omnivoreDocumentStore.createCategory(categoryName);
			LoggerFactory.getLogger(getClass()).info("Kategorie erstellt: " + categoryName);
		}
	}