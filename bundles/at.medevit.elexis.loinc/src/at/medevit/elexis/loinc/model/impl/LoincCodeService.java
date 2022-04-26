package at.medevit.elexis.loinc.model.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.loinc.model.ILoincCodeService;
import at.medevit.elexis.loinc.model.LoincCode;
import ch.elexis.data.Query;
import ch.rgw.tools.VersionInfo;

public class LoincCodeService implements ILoincCodeService {

	private static Logger logger = LoggerFactory.getLogger(LoincCodeService.class);

	private Map<Integer, String> fieldMapping;
	private Integer codeMapping = -1;

	public static final VersionInfo TOP2000VERSION = new VersionInfo("1.1.0");
	public static final VersionInfo CLINICALVERSION = new VersionInfo("1.0.0");

	public LoincCode getByCode(String code) {
		Query<LoincCode> qbe = new Query<LoincCode>(LoincCode.class);
		qbe.add("ID", "!=", LoincCode.VERSIONID);
		qbe.add("ID", "!=", LoincCode.VERSIONTOPID);
		qbe.add("ID", "!=", LoincCode.VERSIONCLINICALID);
		qbe.add(LoincCode.FLD_CODE, "=", code);
		List<LoincCode> res = qbe.execute();
		if (res.isEmpty()) {
			logger.info("Code [" + code + "] not found");
			return null;
		} else {
			return res.get(0);
		}
	}

	public List<LoincCode> getAllCodes() {
		Query<LoincCode> qbe = new Query<LoincCode>(LoincCode.class);
		qbe.add("ID", "!=", LoincCode.VERSIONID);
		qbe.add("ID", "!=", LoincCode.VERSIONTOPID);
		qbe.add("ID", "!=", LoincCode.VERSIONCLINICALID);
		return qbe.execute();
	}

	public void importFromCsv(InputStream csv, Map<Integer, String> fieldMapping) throws IOException {
		logger.info("Import from CSV stream " + csv);

		initMapping(fieldMapping);
		BufferedReader reader = new BufferedReader(new InputStreamReader(csv));

		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(",", -1);
			if (parts[codeMapping].matches("[0-9\\-]*")) {
				LoincCode existing = getByCode(parts[0]);
				if (existing != null) {
					merge(existing, parts);
				} else {
					logger.info("Creating object [" + parts[codeMapping] + "]");
					create(parts);
				}
			} else {
				logger.warn("Import skipping object [" + parts[codeMapping] + "]");
			}
		}
	}

	private void create(String[] parts) {
		LoincCode code = new LoincCode(parts[codeMapping], null, null, null, null);

		for (int i = 0; i < fieldMapping.size(); i++) {
			if (i >= parts.length) {
				code.set(fieldMapping.get(i), "");
			}
			code.set(fieldMapping.get(i), parts[i]);
		}
	}

	private void merge(LoincCode existing, String[] parts) {
		for (int i = 0; i < fieldMapping.size(); i++) {
			existing.set(fieldMapping.get(i), parts[i]);
		}
	}

	private void initMapping(Map<Integer, String> fieldMapping) {
		this.fieldMapping = fieldMapping;

		for (int i = 0; i < fieldMapping.size(); i++) {
			if (fieldMapping.get(i).equals(LoincCode.FLD_CODE)) {
				codeMapping = i;
				break;
			}
		}
		if (codeMapping == -1) {
			throw new IllegalStateException("Fieldmapping is missing the code field.");
		}
	}

	public void updateData() {
		logger.info("Update Top 2000 to version " + TOP2000VERSION.version());
		logger.info("Update Top 2000 from version " + LoincCode.getDataVersion(LoincCode.VERSIONTOPID).version());
		if (TOP2000VERSION.isNewer(LoincCode.getDataVersion(LoincCode.VERSIONTOPID))) {
			try {
				importFromCsv(loadTop2000(), getTop2000FieldMapping());
				LoincCode.setDataVersion(LoincCode.VERSIONTOPID, TOP2000VERSION.version());
			} catch (IOException e) {
				logger.error("Top 2000 import failed.", e);
			}
		}

		logger.info("Update Clinical to version " + CLINICALVERSION.version());
		logger.info("Update Clinical from version " + LoincCode.getDataVersion(LoincCode.VERSIONCLINICALID).version());
		if (CLINICALVERSION.isNewer(LoincCode.getDataVersion(LoincCode.VERSIONCLINICALID))) {
			try {
				importFromCsv(loadClinical(), getClinicalFieldMapping());
				LoincCode.setDataVersion(LoincCode.VERSIONCLINICALID, CLINICALVERSION.version());
			} catch (IOException e) {
				logger.error("Clinical import failed.", e);
			}
		}
	}

	private static InputStream loadClinical() {
		return LoincCodeService.class.getResourceAsStream("/rsc/LOINC_CLINICAL.CSV");
	}

	private static InputStream loadTop2000() {
		return LoincCodeService.class.getResourceAsStream("/rsc/TOP_2000_COMMON_LAB_RESULTS_SI_LOINC_V1-1.CSV");
	}

	private static Map<Integer, String> getClinicalFieldMapping() {
		HashMap<Integer, String> ret = new HashMap<Integer, String>();
		ret.put(0, LoincCode.FLD_CODE);
		ret.put(1, LoincCode.FLD_LONGNAME);
		ret.put(2, LoincCode.FLD_SHORTNAME);
		ret.put(3, LoincCode.FLD_CLASS);
		return ret;
	}

	private static Map<Integer, String> getTop2000FieldMapping() {
		HashMap<Integer, String> ret = new HashMap<Integer, String>();
		ret.put(0, LoincCode.FLD_CODE);
		ret.put(1, LoincCode.FLD_LONGNAME);
		ret.put(2, LoincCode.FLD_SHORTNAME);
		ret.put(3, LoincCode.FLD_CLASS);
		return ret;
	}
}
