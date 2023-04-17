/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.ebanking.esr;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.base.ch.ebanking.model.service.holder.ModelServiceHolder;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.ebanking.parser.Camt054Record;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

/**
 * Ein ESRRecord ist eine einzelne Buchung aus einem ESR-File-
 *
 * @author Gerry
 *
 */
public class ESRRecord extends PersistentObject {
	public static final String FLD_REJECT_CODE = "RejectCode"; //$NON-NLS-1$
	public static final String FLD_DATE = "Datum"; //$NON-NLS-1$
	public static final String FLD_BOOKING_DATE = "Gebucht"; //$NON-NLS-1$
	public static final String MANDANT_ID = "MandantID"; //$NON-NLS-1$
	public static final String PATIENT_ID = "PatientID"; //$NON-NLS-1$
	public static final String RECHNUNGS_ID = "RechnungsID"; //$NON-NLS-1$
	public static final String CODE = "Code"; //$NON-NLS-1$
	private static final String VERSION = "2"; //$NON-NLS-1$
	public static final String TABLENAME = "ESRRECORDS"; //$NON-NLS-1$
	private static final int POSITION_PAT_NR = 11;
	private static final int POSITION_RN_NR = 20;

	public static enum MODE {
		Gutschrift_edv, Storno_edv, Korrektur_edv, Gutschrift_Schalter, Storno_Schalter, Korrektur_Schalter,
		Summenrecord, Unbekannt
	};

	public static enum REJECT {
		OK, ESRREJECT, MASSENREJECT, BETRAG, MANDANT, RN_NUMMER, PAT_NUMMER, DUPLIKAT, ANDERE, PAT_FALSCH
	};

	private static final String createDB = "DROP TABLE " + TABLENAME + ";" + //$NON-NLS-1$ //$NON-NLS-2$
			"DROP INDEX ESR1;" + //$NON-NLS-1$
			"DROP INDEX ESR2;" + //$NON-NLS-1$
			"DROP INDEX ESR3;" + //$NON-NLS-1$
			"CREATE TABLE " + TABLENAME + "(" + //$NON-NLS-1$ //$NON-NLS-2$
			"ID			VARCHAR(25) PRIMARY KEY," + //$NON-NLS-1$
			"lastupdate BIGINT," + //$NON-NLS-1$
			"deleted	CHAR(1) default '0'," + //$NON-NLS-1$
			FLD_DATE + " CHAR(8)," + //$NON-NLS-1$
			"EINGELESEN		CHAR(8)," + //$NON-NLS-1$
			"VERARBEITET	CHAR(8)," + //$NON-NLS-1$
			"GUTSCHRIFT		CHAR(8)," + //$NON-NLS-1$
			"BETRAGINRP		CHAR(8)," + //$NON-NLS-1$
			"CODE			CHAR(3)," + //$NON-NLS-1$
			"RECHNUNGSID VARCHAR(25)," + //$NON-NLS-1$
			"PATIENTID	 VARCHAR(25)," + //$NON-NLS-1$
			"MANDANTID	 VARCHAR(25)," + //$NON-NLS-1$
			"REJECTCODE	 CHAR(3)," + //$NON-NLS-1$
			"KOSTEN		 CHAR(4)," + //$NON-NLS-1$
			FLD_BOOKING_DATE + " CHAR(8)," + //$NON-NLS-1$
			"FILE		 VARCHAR(80));" + //$NON-NLS-1$
			"CREATE INDEX ESR1 ON " + TABLENAME + " (DATUM);" + //$NON-NLS-1$ //$NON-NLS-2$
			"CREATE INDEX ESR2 ON " + TABLENAME + " (PATIENTID);" + //$NON-NLS-1$ //$NON-NLS-2$
			"CREATE INDEX ESR3 ON " + TABLENAME + " (REJECTCODE);" + //$NON-NLS-1$ //$NON-NLS-2$
			"INSERT INTO " + TABLENAME + " (ID,FILE) VALUES ('1','" + VERSION + "');"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String upd2 = "ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;"; //$NON-NLS-1$ //$NON-NLS-2$
	static {
		addMapping(TABLENAME, FLD_ID, PersistentObject.DATE_COMPOUND, "Eingelesen=S:D:EINGELESEN", //$NON-NLS-1$
				"Verarbeitet=S:D:VERARBEITET", //$NON-NLS-1$
				"Gutgeschrieben=S:D:GUTSCHRIFT", //$NON-NLS-1$
				"BetragInRp=BETRAGINRP", //$NON-NLS-1$
				CODE, RECHNUNGS_ID, PATIENT_ID, MANDANT_ID, FLD_REJECT_CODE, "Gebucht=S:D:GEBUCHT", "File" //$NON-NLS-1$ //$NON-NLS-2$
		);
		ESRRecord init = load("1"); //$NON-NLS-1$
		if (init == null) {
			createOrModifyTable(createDB);
		} else {
			String v = init.get("File"); //$NON-NLS-1$
			if (StringTool.isNothing(v)) { // < version 1
				getConnection().exec("ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';"); //$NON-NLS-1$ //$NON-NLS-2$
				init.set("File", VERSION); //$NON-NLS-1$
			} else {
				VersionInfo vi = new VersionInfo(v);
				if (vi.isOlder("2.0.0")) { //$NON-NLS-1$
					createOrModifyTable(upd2);

				}
				init.set("File", VERSION); //$NON-NLS-1$
			}
		}
	}

	@Override
	public String getLabel() {

		return null;
	}

	public Rechnung getRechnung() {
		String rnid = get(RECHNUNGS_ID);
		return Rechnung.load(rnid);
	}

	public Money getBetrag() {
		return new Money(checkZero(get("BetragInRp"))); //$NON-NLS-1$
	}

	public MODE getTyp() {
		int m = getInt(CODE);
		MODE ret = MODE.values()[m];
		return ret;
	}

	public REJECT getRejectCode() {
		int code = getInt(FLD_REJECT_CODE);
		return REJECT.values()[code];
	}

	public void setGebucht(TimeTool date) {
		if (date == null) {
			date = new TimeTool();
		}
		set(FLD_BOOKING_DATE, date.toString(TimeTool.DATE_GER));
		set(FLD_REJECT_CODE, StringConstants.ZERO);
	}

	/**
	 * Der Konstruktor liest eine ESR-Zeile ein und konstruiert daraus den
	 * Datensatz.
	 */
	public ESRRecord(final String file, final String codeline) {
		super.create(null);
		Mandant m;
		Rechnung rn = null;
		String mandantID;
		REJECT rejectCode;

		String[] vals = new String[11];
		vals[0] = new TimeTool().toString(TimeTool.DATE_COMPACT);
		vals[10] = file;

		rejectCode = REJECT.OK;

		// Code/Modus.
		MODE mode = MODE.Unbekannt;
		String smd = codeline.substring(0, 3);
		if (smd.equals("002")) { //$NON-NLS-1$
			mode = MODE.Gutschrift_edv;
		} else if (smd.equals("012")) { //$NON-NLS-1$
			mode = MODE.Gutschrift_Schalter;
		} else if (smd.equals("005")) { //$NON-NLS-1$
			mode = MODE.Storno_edv;
		} else if (smd.equals("015")) { //$NON-NLS-1$
			mode = MODE.Storno_Schalter;
		} else if (smd.equals("008")) { //$NON-NLS-1$
			mode = MODE.Korrektur_edv;
		} else if (smd.equals("018")) { //$NON-NLS-1$
			mode = MODE.Korrektur_Schalter;
		} else if (smd.equals("999")) { //$NON-NLS-1$
			mode = MODE.Summenrecord;
		}
		vals[5] = Integer.toString(mode.ordinal());

		// Daten parsen. Der ESR-Record liefert 6-stellige Daten, wir wollen 8-stellige
		String prefix = vals[0].substring(0, 2);
		// TODO Das funktioniert nur bis ins Jahr 2099 :-)
		TimeTool dat = new TimeTool(prefix + codeline.substring(59, 65));
		vals[1] = dat.toString(TimeTool.DATE_GER);
		dat.set(prefix + codeline.substring(65, 71));
		vals[2] = dat.toString(TimeTool.DATE_GER);
		dat.set(prefix + codeline.substring(71, 77));
		vals[3] = (dat.toString(TimeTool.DATE_GER));

		if (mode.equals(MODE.Summenrecord)) {
			// Betrag (führende Nullen entfernen)
			vals[4] = Integer.toString(Integer.parseInt(codeline.substring(39, 51).trim())); // Totalbetrag
			// 12-stellig
		} else {
			vals[4] = Integer.toString(Integer.parseInt(codeline.substring(39, 49).trim())); // Zeilenbetrag
			// 10-stellig
			String esrline = codeline.substring(12, 39);

			// Von der RechnungsNummer führende Nullen wegbringen
			int rnnr = Integer.parseInt(esrline.substring(POSITION_RN_NR, 26));
			Query<Rechnung> qbe_r = new Query<Rechnung>(Rechnung.class);
			String rnid = qbe_r.findSingle("RnNummer", "=", Integer.toString(rnnr)); //$NON-NLS-1$ //$NON-NLS-2$
			if (rnid == null) {
				rejectCode = REJECT.RN_NUMMER;
				vals[6] = StringUtils.EMPTY;
				mandantID = StringUtils.EMPTY;
			} else {
				vals[6] = rnid;
				rn = Rechnung.load(rnid);
				if (rn == null) {
					rejectCode = REJECT.RN_NUMMER;
					vals[6] = StringUtils.EMPTY;
					mandantID = StringUtils.EMPTY;
				} else {
					m = rn.getMandant();
					if (m == null) {
						rejectCode = REJECT.MANDANT;
						vals[6] = StringUtils.EMPTY;
						mandantID = StringUtils.EMPTY;
					} else {
						mandantID = m.getId();
					}
				}

			}
			String PatNr = esrline.substring(POSITION_PAT_NR, POSITION_RN_NR);
			long patnr = Long.parseLong(PatNr); // führende Nullen wegbringen
			String PatID = new Query<Patient>(Patient.class).findSingle("PatientNr", "=", Long.toString(patnr)); //$NON-NLS-1$ //$NON-NLS-2$
			if (PatID == null) {
				if (rejectCode == REJECT.OK) {
					rejectCode = REJECT.PAT_NUMMER;
				}
				vals[7] = StringUtils.EMPTY;
			} else if ((rn != null) && (!rn.getFall().getPatient().getId().equals(PatID))) {
				if (rejectCode == REJECT.OK) {
					rejectCode = REJECT.PAT_FALSCH;
				}
				vals[7] = StringUtils.EMPTY;
			} else {

				vals[7] = PatID;

			}
			vals[8] = mandantID;
		}
		vals[9] = Integer.toString(rejectCode.ordinal());
		set(new String[] { FLD_DATE, "Eingelesen", "Verarbeitet", "Gutgeschrieben", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
																					// //$NON-NLS-4$
				"BetragInRp", CODE, RECHNUNGS_ID, PATIENT_ID, MANDANT_ID, FLD_REJECT_CODE, "File" }, vals); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * Creates a {@link ESRRecord} from a {@link Camt054Record}
	 *
	 * @param file
	 * @param camt054Record
	 */
	public ESRRecord(String file, Camt054Record camt054Record) {
		super.create(null);
		Mandant m;
		Rechnung rn = null;
		String mandantID;
		REJECT rejectCode;

		// Code/Modus.
		MODE mode = MODE.Unbekannt;
		String smd = camt054Record.getMode();
		if (smd != null) {
			if (smd.equals("002")) { //$NON-NLS-1$
				mode = MODE.Gutschrift_edv;
			} else if (smd.equals("005")) { //$NON-NLS-1$
				mode = MODE.Storno_edv;
			} else if (smd.equals("999")) { //$NON-NLS-1$
				mode = MODE.Summenrecord;
			}
		}

		String[] vals = new String[11];
		vals[0] = new TimeTool().toString(TimeTool.DATE_COMPACT);
		vals[1] = camt054Record.getReadDate() != null
				? new TimeTool(camt054Record.getReadDate()).toString(TimeTool.DATE_GER)
				: StringUtils.EMPTY;
		vals[2] = camt054Record.getBookingDate() != null
				? new TimeTool(camt054Record.getBookingDate()).toString(TimeTool.DATE_GER)
				: StringUtils.EMPTY;
		vals[3] = camt054Record.getValuDate() != null
				? new TimeTool(camt054Record.getValuDate()).toString(TimeTool.DATE_GER)
				: StringUtils.EMPTY;
		vals[10] = file;

		rejectCode = REJECT.OK;

		vals[5] = Integer.toString(mode.ordinal());
		vals[4] = camt054Record.getAmount(); // betrag

		if (mode.equals(MODE.Summenrecord)) {
			// nothing to do
		} else {
			// Von der RechnungsNummer führende Nullen wegbringen
			int rnnr = Integer.parseInt(camt054Record.getReference().substring(POSITION_RN_NR, 26));
			Query<Rechnung> qbe_r = new Query<Rechnung>(Rechnung.class);
			String rnid = qbe_r.findSingle("RnNummer", "=", Integer.toString(rnnr)); //$NON-NLS-1$ //$NON-NLS-2$
			if (rnid == null) {
				rejectCode = REJECT.RN_NUMMER;
				vals[6] = StringUtils.EMPTY;
				mandantID = StringUtils.EMPTY;
			} else {
				vals[6] = rnid;
				rn = Rechnung.load(rnid);
				if (rn == null) {
					rejectCode = REJECT.RN_NUMMER;
					vals[6] = StringUtils.EMPTY;
					mandantID = StringUtils.EMPTY;
				} else {
					m = rn.getMandant();
					if (m == null) {
						rejectCode = REJECT.MANDANT;
						vals[6] = StringUtils.EMPTY;
						mandantID = StringUtils.EMPTY;
					} else {
						mandantID = m.getId();
					}
				}

			}
			String PatNr = camt054Record.getReference().substring(POSITION_PAT_NR, POSITION_RN_NR);
			long patnr = Long.parseLong(PatNr); // führende Nullen wegbringen
			String PatID = new Query<Patient>(Patient.class).findSingle("PatientNr", "=", //$NON-NLS-1$//$NON-NLS-2$
					Long.toString(patnr));
			if (PatID == null) {
				if (rejectCode == REJECT.OK) {
					rejectCode = REJECT.PAT_NUMMER;
				}
				vals[7] = StringUtils.EMPTY;
			} else if ((rn != null) && (!rn.getFall().getPatient().getId().equals(PatID))) {
				if (rejectCode == REJECT.OK) {
					rejectCode = REJECT.PAT_FALSCH;
				}
				vals[7] = StringUtils.EMPTY;
			} else {

				vals[7] = PatID;

			}
			vals[8] = mandantID;
		}
		vals[9] = Integer.toString(rejectCode.ordinal());
		set(new String[] { FLD_DATE, "Eingelesen", "Verarbeitet", "Gutgeschrieben", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
																					// //$NON-NLS-4$
				"BetragInRp", CODE, RECHNUNGS_ID, PATIENT_ID, MANDANT_ID, FLD_REJECT_CODE, "File" //$NON-NLS-1$//$NON-NLS-2$
		}, vals);

	}

	@Override
	protected String getTableName() {
		return "ESRRECORDS"; //$NON-NLS-1$
	}

	public static ESRRecord load(final String id) {
		ESRRecord ret = new ESRRecord(id);
		if (ret.exists()) {
			return ret;
		}
		return null;
	}

	protected ESRRecord(final String id) {
		super(id);
	}

	public ESRRecord() {
	}

	public String getEinlesedatatum() {
		return get("Eingelesen"); //$NON-NLS-1$
	}

	public String getVerarbeitungsdatum() {
		return get("Verarbeitet"); //$NON-NLS-1$
	}

	public String getValuta() {
		return get("Gutgeschrieben"); //$NON-NLS-1$
	}

	public Patient getPatient() {
		String pid = get(PATIENT_ID);
		return Patient.load(pid);
	}

	public String getGebucht() {
		return get(FLD_BOOKING_DATE); // $NON-NLS-1$
	}

	public String getFile() {
		return checkNull(get("File")); //$NON-NLS-1$
	}

	public String getESRCode() {
		return MODE.values()[checkZero(get(CODE))].toString();
	}

	public IEsrRecord toIEsrRecord() {
		return ModelServiceHolder.get().load(getId(), IEsrRecord.class).get();
	}
}