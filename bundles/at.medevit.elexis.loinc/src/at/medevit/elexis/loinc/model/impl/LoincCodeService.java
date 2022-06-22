package at.medevit.elexis.loinc.model.impl;

import org.apache.commons.lang3.StringUtils;
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

	public static final VersionInfo TOP2000VERSION = new VersionInfo("1.1.0"); //$NON-NLS-1$
	public static final VersionInfo CLINICALVERSION = new VersionInfo("1.0.0"); //$NON-NLS-1$

	public LoincCode getByCode(String code) {
		Query<LoincCode> qbe = new Query<LoincCode>(LoincCode.class);
		qbe.add("ID", "!=", LoincCode.VERSIONID); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add("ID", "!=", LoincCode.VERSIONTOPID); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add("ID", "!=", LoincCode.VERSIONCLINICALID); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add(LoincCode.FLD_CODE, "=", code); //$NON-NLS-1$
		List<LoincCode> res = qbe.execute();
		if (res.isEmpty()) {
			logger.debug("Code [" + code + "] not found"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		} else {
			return res.get(0);
		}
	}

	public List<LoincCode> getAllCodes() {
		Query<LoincCode> qbe = new Query<LoincCode>(LoincCode.class);
		qbe.add("ID", "!=", LoincCode.VERSIONID); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add("ID", "!=", LoincCode.VERSIONTOPID); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add("ID", "!=", LoincCode.VERSIONCLINICALID); //$NON-NLS-1$ //$NON-NLS-2$
		return qbe.execute();
	}

	public void importFromCsv(InputStream csv, Map<Integer, String> fieldMapping) throws IOException {
		logger.info("Import from CSV stream " + csv); //$NON-NLS-1$

		initMapping(fieldMapping);
		BufferedReader reader = new BufferedReader(new InputStreamReader(csv));

		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(",", -1); //$NON-NLS-1$
			if (parts[codeMapping].matches("[0-9\\-]*")) { //$NON-NLS-1$
				LoincCode existing = getByCode(parts[0]);
				if (existing != null) {
					merge(existing, parts);
				} else {
					logger.debug("Creating object [" + parts[codeMapping] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
					create(parts);
				}
			} else {
				logger.warn("Import skipping object [" + parts[codeMapping] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private void create(String[] parts) {
		LoincCode code = new LoincCode(parts[codeMapping], null, null, null, null);

		for (int i = 0; i < fieldMapping.size(); i++) {
			if (i >= parts.length) {
				code.set(fieldMapping.get(i), StringUtils.EMPTY);
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
			throw new IllegalStateException("Fieldmapping is missing the code field."); //$NON-NLS-1$
		}
	}

	public void updateData() {
		logger.info("Update Top 2000 to version " + TOP2000VERSION.version()); //$NON-NLS-1$
		logger.info("Update Top 2000 from version " + LoincCode.getDataVersion(LoincCode.VERSIONTOPID).version()); //$NON-NLS-1$
		if (TOP2000VERSION.isNewer(LoincCode.getDataVersion(LoincCode.VERSIONTOPID))) {
			try {
				importFromCsv(loadTop2000(), getTop2000FieldMapping());
				LoincCode.setDataVersion(LoincCode.VERSIONTOPID, TOP2000VERSION.version());
			} catch (IOException e) {
				logger.error("Top 2000 import failed.", e); //$NON-NLS-1$
			}
		}

		logger.info("Update Clinical to version " + CLINICALVERSION.version()); //$NON-NLS-1$
		logger.info("Update Clinical from version " + LoincCode.getDataVersion(LoincCode.VERSIONCLINICALID).version()); //$NON-NLS-1$
		if (CLINICALVERSION.isNewer(LoincCode.getDataVersion(LoincCode.VERSIONCLINICALID))) {
			try {
				importFromCsv(loadClinical(), getClinicalFieldMapping());
				LoincCode.setDataVersion(LoincCode.VERSIONCLINICALID, CLINICALVERSION.version());
			} catch (IOException e) {
				logger.error("Clinical import failed.", e); //$NON-NLS-1$
			}
		}
	}

	private static InputStream loadClinical() {
		return LoincCodeService.class.getResourceAsStream("/rsc/LOINC_CLINICAL.CSV"); //$NON-NLS-1$
	}

	private static InputStream loadTop2000() {
		return LoincCodeService.class.getResourceAsStream("/rsc/TOP_2000_COMMON_LAB_RESULTS_SI_LOINC_V1-1.CSV"); //$NON-NLS-1$
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
