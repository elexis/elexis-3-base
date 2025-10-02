package ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import ch.elexis.core.utils.CoreUtil;
import ch.oaat_otma.PatientCase;
import ch.oaat_otma.grouper.ClassificationSystemReader;
import ch.oaat_otma.grouper.GrouperResult;
import ch.oaat_otma.grouper.PCSError;
import ch.oaat_otma.grouper.PatientClassificationSystem;

@Component(service = PatientClassificationSystemService.class)
public class PatientClassificationSystemService {

	private static final String LKAAT_FILENAME = "system_ambP_11c_lkaat.json";
	
	private PatientClassificationSystem classificationSystem;

	@Activate
	public void activate() {
		File rootDir = CoreUtil.getWritableUserDir();
		File tarifmatcherdir = new File(rootDir, "tarifmatcher");
		if (!tarifmatcherdir.exists()) {
			tarifmatcherdir.mkdir();
		}
		File lkaatFile = new File(tarifmatcherdir, LKAAT_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, LKAAT_FILENAME))) {
			IOUtils.copy(PatientClassificationSystemService.class.getResourceAsStream("/rsc/grouper/" + LKAAT_FILENAME),
					out);
			ClassificationSystemReader reader = new ClassificationSystemReader();
			classificationSystem = reader.readFromFile(lkaatFile);

			List<PCSError> errors = classificationSystem.check();
			assert (errors.size() == 0);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Casemaster", e);
		}
	}

	public GrouperResult getResult(PatientCase patientCase) {
		return classificationSystem.evaluate(patientCase);
	}
}
