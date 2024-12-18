package ch.elexis.global_inbox.core.handler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.global_inbox.core.util.Constants;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class ImportOmnivore {

	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)"); //$NON-NLS-1$

	private Logger log;
	private ImportOmnivoreInboxUtil giutil;
	private String deviceName;

	public ImportOmnivore(String deviceName) {
		log = LoggerFactory.getLogger(getClass());
		giutil = new ImportOmnivoreInboxUtil();
		this.deviceName = deviceName;
	}

	protected IStatus run(IProgressMonitor monitor) {
			String filepath = ImportOmnivoreInboxUtil.getDirectory(Constants.PREF_DIR_DEFAULT, deviceName);
			IVirtualFilesystemHandle dir = null;
			if (filepath == null) {
				filepath = Constants.PREF_DIR_DEFAULT;
				ConfigServiceHolder.get().set(Constants.PREF_DIR, Constants.PREF_DIR_DEFAULT);
			}
			try {
				dir = VirtualFilesystemServiceHolder.get().of(filepath);
				addFilesInDirRecursive(dir);
			} catch (Exception e) {
				log.error("Failed to convert filepath to directory. Filepath: {}", filepath, e);
				return Status.CANCEL_STATUS;
			}

		return Status.OK_STATUS;
	}

	private void addFilesInDirRecursive(IVirtualFilesystemHandle dir) throws IOException {
		IVirtualFilesystemHandle[] files = dir.listHandles();
		if (files == null) {
			return;
		}
		for (IVirtualFilesystemHandle file : files) {
			if (!file.exists() || file.getName().startsWith(".")) {
				continue;
			}
			if (file.isDirectory()) {
				addFilesInDirRecursive(file);
			} else {
				Matcher matcher = PATIENT_MATCH_PATTERN.matcher(file.getName());
				if (matcher.matches()) {
					String patientNo = matcher.group(1);
					String fileName = matcher.group(2);
					String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, fileName);
					if (tryImportForPatient != null) {
						log.info("Auto imported file [{}], document id is [{}]", file, tryImportForPatient);
						continue;
					}
				}
			}
		}
	}
}