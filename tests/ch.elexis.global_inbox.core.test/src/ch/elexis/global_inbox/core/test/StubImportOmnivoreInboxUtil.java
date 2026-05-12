package ch.elexis.global_inbox.core.test;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class StubImportOmnivoreInboxUtil extends ImportOmnivoreInboxUtil {

	public String capturedPatientId;
	public String capturedDocumentName;

	@Override
	public String tryImportForPatient(IVirtualFilesystemHandle file, String patientNo, String fileName) {
		if (patientNo.length() > 5 || patientNo.startsWith("20")) {
			return null;
		}

		this.capturedPatientId = patientNo;
		this.capturedDocumentName = fileName;
		return "test-document-id";
	}
}