/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.impfplan.model;

import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.TimeTool;

public class Vaccination extends PersistentObject {

	public static final String OBSERVATIONS = "observations"; //$NON-NLS-1$
	public static final String DATE = "date"; //$NON-NLS-1$
	public static final String VACCINATION_TYPE = "vaccinationType"; //$NON-NLS-1$
	public static final String PATIENT_ID = "patientID"; //$NON-NLS-1$
	private static final String TABLENAME = "CH_ELEXIS_IMPFPLAN_VACCINATIONS"; //$NON-NLS-1$
	private static final String VERSION = "0.1.0"; //$NON-NLS-1$
	private static final String createDB = "CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
			"ID VARCHAR(25) primary key, deleted CHAR(1) default '0', lastupdate bigint," + //$NON-NLS-1$
			"patientID VARCHAR(25), vaccinationType VARCHAR(25), date CHAR(8), observations TEXT);" + //$NON-NLS-1$
			"CREATE INDEX " + TABLENAME + "_IDX1 on " + TABLENAME + " (patientID);" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"INSERT INTO " + TABLENAME + "(ID,observations) VALUES('VERSION','" + VERSION + "');"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	static {
		addMapping(TABLENAME, PATIENT_ID, VACCINATION_TYPE, DATE, OBSERVATIONS);
		// getConnection().exec("DROP TABLE "+TABLENAME);
		Vaccination ver = load("VERSION"); //$NON-NLS-1$
		if (!ver.exists()) {
			createOrModifyTable(createDB);
		}
	}

	public Vaccination(VaccinationType vt, Patient pat, TimeTool date, boolean bUnexact) {
		create(null);
		String dat = date.toString(TimeTool.DATE_COMPACT);
		if (bUnexact) {
			dat = dat.substring(0, 4) + "0000"; //$NON-NLS-1$
		}
		set(new String[] { VACCINATION_TYPE, PATIENT_ID, DATE }, vt.getId(), pat.getId(), dat);
	}

	public Vaccination(VaccinationType vt, Patient pat) {
		this(vt, pat, new TimeTool(), false);
	}

	@Override
	public String getLabel() {
		Patient pat = Patient.load(get(PATIENT_ID));
		VaccinationType type = VaccinationType.load(get(VACCINATION_TYPE));
		return new StringBuilder().append(pat.getLabel()).append(" : ").append(type.getLabel()).toString(); //$NON-NLS-1$
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public VaccinationType getVaccinationType() {
		VaccinationType vt = VaccinationType.load(get(VACCINATION_TYPE));
		if (vt.exists()) {
			return vt;
		}
		return null;
	}

	public String getDateAsString() {
		String dat = get(Vaccination.DATE);
		if (dat.endsWith("0000")) { //$NON-NLS-1$
			return "( ~" + dat.substring(0, 4) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return getDate().toString(TimeTool.DATE_GER);
		}
	}

	public String getRawDateString() {
		String dat = get(Vaccination.DATE);
		if (dat.endsWith("0000")) { //$NON-NLS-1$
			return dat;
		} else {
			return getDate().toString(TimeTool.DATE_COMPACT);
		}
	}

	public TimeTool getDate() {
		String dRaw = get(DATE);
		if (dRaw.endsWith("0000")) { //$NON-NLS-1$
			dRaw = dRaw.substring(0, 4) + "0101"; //$NON-NLS-1$
		}
		return new TimeTool(dRaw);
	}

	public void setDate(TimeTool date, boolean bIsUnexact) {
		String val = date.toString(TimeTool.DATE_COMPACT);
		if (bIsUnexact) {
			val = val.substring(0, 4) + "0000"; //$NON-NLS-1$
		}
		set(DATE, val);
	}

	public String getPatientId() {
		return checkNull(get(PATIENT_ID));
	}

	public static Vaccination load(String id) {
		return new Vaccination(id);
	}

	protected Vaccination(String id) {
		super(id);
	}

	protected Vaccination() {
	}

}
