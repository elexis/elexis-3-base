package ch.elexis.global_inbox.core.strategies;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class PatientFolderStrategy implements IImportStrategy {
	private final Pattern PATIENT_DIR_PATTERN = Pattern.compile("([0-9]+)(?:_[^0-9].*)?");
	private final ImportOmnivoreInboxUtil giutil;
	private final String deviceName;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public PatientFolderStrategy(ImportOmnivoreInboxUtil giutil, String deviceName) {
		this.giutil = giutil;
		this.deviceName = deviceName;
	}

	@Override
	public boolean importFile(IVirtualFilesystemHandle file) {
		try {
			IVirtualFilesystemHandle parent = file.getParent();
			if (parent == null)
				return false;

			Matcher m = PATIENT_DIR_PATTERN.matcher(parent.getName());
			if (!m.matches())
				return false;

			String patientNo = m.group(1);
			String documentName = deviceName + "_" + file.getName();

			String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, documentName);
			if (tryImportForPatient != null) {
				log.info("Auto imported (FOLDER_WITH_NAME) file [{}], document id is [{}]", file, tryImportForPatient);
				return true;
			}
		} catch (IOException e) {
			log.error("Error accessing parent folder", e);
		}
		return false;
	}
}