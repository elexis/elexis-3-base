package ch.elexis.global_inbox.core.strategies;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class FilePrefixStrategy implements IImportStrategy {
	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)");
	private final ImportOmnivoreInboxUtil giutil;
	private final String deviceName;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public FilePrefixStrategy(ImportOmnivoreInboxUtil giutil, String deviceName) {
		this.giutil = giutil;
		this.deviceName = deviceName;
	}

	@Override
	public boolean importFile(IVirtualFilesystemHandle file) {
		Matcher matcher = PATIENT_MATCH_PATTERN.matcher(file.getName());
		if (matcher.matches()) {
			String patientNo = matcher.group(1);
			String fileName = deviceName + "_" + matcher.group(2);
			String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, fileName);
			if (tryImportForPatient != null) {
				log.info("Auto imported (FILE_PREFIX) file [{}], document id is [{}]", file, tryImportForPatient);
				return true;
			}
		}
		return false;
	}
}