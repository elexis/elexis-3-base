package ch.elexis.global_inbox.core.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class HierarchyStrategy implements IImportStrategy {

	private final Pattern PATIENT_DIR_PATTERN = Pattern.compile("([0-9]+)(?:_[^0-9].*)?");

	private final ImportOmnivoreInboxUtil giutil;
	private final String deviceName;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public HierarchyStrategy(ImportOmnivoreInboxUtil giutil, String deviceName) {
		this.giutil = giutil;
		this.deviceName = deviceName;
	}

	@Override
	public boolean importFile(IVirtualFilesystemHandle file) {
		try {
			IVirtualFilesystemHandle current = file.getParent();
			if (current == null) {
				return false;
			}

			List<String> segmentNames = new ArrayList<>();
			String rawFileName = file.getName();
			String baseFileName = rawFileName.startsWith("_") ? rawFileName.substring(1) : rawFileName;

			while (current != null) {
				String name = current.getName();
				Matcher m = PATIENT_DIR_PATTERN.matcher(name);
				if (m.matches()) {
					String patientCandidate = m.group(1);
					String documentName = buildDocumentName(baseFileName, segmentNames);
					String importedId = giutil.tryImportForPatient(file, patientCandidate, documentName);

					if (importedId != null) {
						log.info("Auto imported (HIERARCHY) file [{}], document id is [{}]", file, importedId);
						return true;
					}
				}

				if (!name.startsWith(".")) {
					segmentNames.add(name);
				}

				current = current.getParent();
			}

		} catch (Exception e) {
			log.warn("Failed to import file via hierarchy for [{}]", file, e);
		}

		return false;
	}

	private String buildDocumentName(String baseFileName, List<String> segments) {
		StringBuilder docNameBuilder = new StringBuilder();
		docNameBuilder.append(deviceName).append('_');

		if (!segments.isEmpty()) {
			// Segments are collected in reverse order (from bottom to top),
			// Example structure: Patient -> Laboratory -> 2024 -> File
			// Loop first collects 2024, then Laboratory.
			// Builder creates: Device_Laboratory_2024_File.
			for (int i = segments.size() - 1; i >= 0; i--) {
				docNameBuilder.append(segments.get(i)).append('_');
			}
		}

		docNameBuilder.append(baseFileName);
		return docNameBuilder.toString();
	}
}