package ch.elexis.mednet.webapi.ui.handler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.mednet.webapi.core.vfs.MedNetVfsHandler;

public class ImportOmnivore {

	private static final Logger log = LoggerFactory.getLogger(ImportOmnivore.class);
	private static final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)"); //$NON-NLS-1$

	private ImportOmnivoreUtil giutil;

	public ImportOmnivore() {
		giutil = new ImportOmnivoreUtil();
	}

	/**
	 * Executes the import process for Omnivore using the active MedNet
	 * configuration. Resolves the directory using the Virtual Filesystem Service.
	 *
	 * @return the status of the execution (OK or CANCEL)
	 */
	public IStatus run() {
		try {
			IVirtualFilesystemHandle dirHandle = MedNetVfsHandler.getDownloadDirectory();

			if (dirHandle == null) {
				return Status.CANCEL_STATUS;
			}

			List<IVirtualFilesystemHandle> filesToProcess = MedNetVfsHandler.listFilesRecursively(dirHandle);

			for (IVirtualFilesystemHandle fileHandle : filesToProcess) {
				handleFileImport(fileHandle);
			}

			return Status.OK_STATUS;

		} catch (Exception exception) {
			log.error("ImportOmnivore run failed due to a filesystem error.", exception);
			return Status.CANCEL_STATUS;
		}
	}

	private void handleFileImport(IVirtualFilesystemHandle fileHandle) {
		Matcher matcher = PATIENT_MATCH_PATTERN.matcher(fileHandle.getName());

		if (matcher.matches()) {
			String patientNo = matcher.group(1);
			String fileName = matcher.group(2);

			try {
				String documentId = giutil.tryImportForPatient(fileHandle, patientNo, fileName);

				if (documentId != null) {
					log.info("Successfully auto-imported file [{}]. Assigned document ID: [{}]",
							fileHandle.getAbsolutePath(), documentId);
				}
			} catch (Exception exception) {
				log.error("Failed to import file [{}] for patient [{}]", fileHandle.getAbsolutePath(), patientNo,
						exception);
			}
		}
	}
}