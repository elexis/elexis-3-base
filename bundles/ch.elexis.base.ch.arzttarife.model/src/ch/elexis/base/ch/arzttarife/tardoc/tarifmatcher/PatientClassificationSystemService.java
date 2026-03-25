package ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ch.oaat_otma.grouper.interpreter.PcsNode;
import ch.oaat_otma.grouper.interpreter.Table;

@Component(service = PatientClassificationSystemService.class)
public class PatientClassificationSystemService {

	private static final String LKAAT_FILENAME = "system_ambP_11c_251128_lkaat.json";
	
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

	public Map<String, List<String>> getIcdDiagnosisInfo(String triggerCode) {
		Map<String, List<String>> ret = new HashMap<>();
		// C08.GE.0010
		String code = triggerCode.replace(".", "");
		// nodes with code
		List<PcsNode> listNodes = classificationSystem.getNodes().stream().filter(n -> n.getLogicSource() != null
				&& n.getLogicSource().contains("in list") && n.getLogicSource().contains(triggerCode)).toList();
		for (PcsNode listNode : listNodes) {
			// main diagnosis nodes
			List<PcsNode> mainDiagnosisNodes = getDiagnosisNodes(listNode);
			for (PcsNode mainDiagnosisNode : mainDiagnosisNodes) {
				List<String> chapterDiagnosis = ret.get(mainDiagnosisNode.getChapterCode());
				if (chapterDiagnosis == null) {
					chapterDiagnosis = new ArrayList<String>();
				}
				chapterDiagnosis
						.addAll(classificationSystem.getTableMap().get(mainDiagnosisNode.getChapterCode()).codes);
				ret.put(mainDiagnosisNode.getChapterCode(), chapterDiagnosis);
			}
		}
		// tables with code
		List<Table> tables = classificationSystem.getTables().stream()
				.filter(t -> t.codes.contains(code) && t.name.startsWith(code.substring(0, 3)))
				.toList();
		List<String> tableNames = tables.stream().map(t -> t.name).toList();
		for (String tableName : tableNames) {
			// node with table
			List<PcsNode> tableNodes = classificationSystem.getNodes().stream().filter(n -> n.getLogicSource() != null
					&& n.getLogicSource().contains("in table") && n.getLogicSource().contains(tableName)).toList();
			for (PcsNode tableNode : tableNodes) {
				// main diagnosis nodes
				List<PcsNode> mainDiagnosisNodes = getDiagnosisNodes(tableNode);
				for (PcsNode mainDiagnosisNode : mainDiagnosisNodes) {
					List<String> chapterDiagnosis = ret.get(mainDiagnosisNode.getChapterCode());
					if (chapterDiagnosis == null) {
						chapterDiagnosis = new ArrayList<String>();
					}
					chapterDiagnosis
							.addAll(classificationSystem.getTableMap().get(mainDiagnosisNode.getChapterCode()).codes);
					ret.put(mainDiagnosisNode.getChapterCode(), chapterDiagnosis);
				}
			}
		}
		return ret;
	}

	private List<PcsNode> getDiagnosisNodes(PcsNode tableNode) {
		List<PcsNode> ret = new ArrayList<>();
		PcsNode parent = tableNode.getParent();
		while (parent != null) {
			if (parent.getLogicSource().contains("main_diagnosis")) {
				ret.add(parent);
			}
			parent = parent.getParent();
		}
		return ret;
	}
}
