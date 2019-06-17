package at.medevit.elexis.emediplan.core;

import java.io.File;

import ch.elexis.data.Patient;
import ch.rgw.tools.Result;

public interface BlueMedicationService {
	
	public Result<String> uploadDocument(Patient patient, File document);
	
	public Result<String> downloadEMediplan(String id);
}
