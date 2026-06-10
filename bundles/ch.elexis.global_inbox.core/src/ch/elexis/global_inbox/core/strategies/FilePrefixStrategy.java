package ch.elexis.global_inbox.core.strategies;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

/**
 * Strategy to import files based on a specific prefix pattern matching the
 * patient ID.
 */
public class FilePrefixStrategy implements IImportStrategy {
	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)");
	private final String deviceName;
	private final ImportOmnivoreInboxUtil inboxUtil;
	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Creates a new FilePrefixStrategy. * @param inboxUtil the utility to handle
	 * the actual import process
	 * 
	 * @param deviceName the name of the device providing the files
	 */
	public FilePrefixStrategy(ImportOmnivoreInboxUtil inboxUtil, String deviceName) {
		this.inboxUtil = inboxUtil;
		this.deviceName = deviceName;
	}

	/**
	 * Attempts to import the given file if its name matches the prefix pattern.
	 * * @param file the file to check and potentially import
	 * 
	 * @return true if the file was successfully imported, false otherwise
	 */
	@Override
	public boolean importFile(IVirtualFilesystemHandle file) {
		Matcher matcher = PATIENT_MATCH_PATTERN.matcher(file.getName());
		if (matcher.matches()) {
			String patientNo = matcher.group(1);
			String fileName = inboxUtil.formatDocumentName(matcher.group(2), deviceName);
			String documentId = inboxUtil.tryImportForPatient(file, patientNo, fileName);
			if (documentId != null) {
				log.info("Auto imported (FILE_PREFIX) file [{}], document id is [{}]", file, documentId);
				return true;
			}
		}
		return false;
	}
}