package at.medevit.elexis.loinc.model;

import java.util.List;

import ch.elexis.core.data.interfaces.ICodeElement;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.CodingSystem;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.VersionInfo;

public class LoincCode extends PersistentObject implements ICodeElement, ICoding {
	public static final String TABLENAME = "at_medevit_elexis_loinc"; //$NON-NLS-1$
	public static final String VERSION = "1.0.0"; //$NON-NLS-1$

	public static final String VERSIONTOPID = "TOP2000VERSION"; //$NON-NLS-1$
	public static final String VERSIONCLINICALID = "CLINICALVERSION"; //$NON-NLS-1$
	public static final String VERSIONID = "VERSION"; //$NON-NLS-1$

	public static final String FLD_CODE = "code"; //$NON-NLS-1$
	public static final String FLD_LONGNAME = "longname"; //$NON-NLS-1$
	public static final String FLD_SHORTNAME = "shortname"; //$NON-NLS-1$
	public static final String FLD_CLASS = "class"; //$NON-NLS-1$
	public static final String FLD_UNIT = "unit"; //$NON-NLS-1$

	static {
		addMapping(TABLENAME, FLD_CODE, FLD_LONGNAME, FLD_SHORTNAME, FLD_CLASS, FLD_UNIT);
	}

	public LoincCode(String id) {
		super(id);
	}

	public LoincCode(String code, String longname, String shortname, String clazz, String unit) {
		create(null);
		set(FLD_CODE, code);
		set(FLD_LONGNAME, longname);
		set(FLD_SHORTNAME, shortname);
		set(FLD_CLASS, clazz);
		set(FLD_UNIT, unit);
	}

	public LoincCode() {
		// TODO Auto-generated constructor stub
	}

	public static LoincCode load(final String id) {
		return new LoincCode(id);
	}

	@Override
	public String getLabel() {
		String[] vals = get(true, FLD_CODE, FLD_LONGNAME);

		if (vals[1].trim().length() > 0)
			return vals[0] + " - " + vals[1].trim(); //$NON-NLS-1$
		else
			return vals[0];
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public String getCodeSystemName() {
		return "LOINC"; //$NON-NLS-1$
	}

	public String getCodeSystemCode() {
		return "999"; //$NON-NLS-1$
	}

	public String getCode() {
		return get(FLD_CODE);
	}

	public String getText() {
		return get(FLD_LONGNAME);
	}

	public static VersionInfo getDataVersion(String versionId) {
		LoincCode dataVersion = load(versionId);
		return new VersionInfo(dataVersion.get(FLD_CODE));
	}

	public static void setDataVersion(String versionId, String version) {
		LoincCode dataVersion = load(versionId);
		if (!dataVersion.exists()) {
			dataVersion.create(versionId);
		}
		dataVersion.set(dataVersion.FLD_CODE, version);
	}

	public List<Object> getActions(Object context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSystem() {
		return CodingSystem.LOINC_CODESYSTEM.getSystem();
	}

	@Override
	public String getDisplay() {
		return getText();
	}
}
