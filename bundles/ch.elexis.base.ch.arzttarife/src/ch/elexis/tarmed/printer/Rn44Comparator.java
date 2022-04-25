package ch.elexis.tarmed.printer;

import java.util.Comparator;
import java.util.GregorianCalendar;

import ch.fd.invoice440.request.RecordDRGType;
import ch.fd.invoice440.request.RecordDrugType;
import ch.fd.invoice440.request.RecordLabType;
import ch.fd.invoice440.request.RecordMigelType;
import ch.fd.invoice440.request.RecordOtherType;
import ch.fd.invoice440.request.RecordParamedType;
import ch.fd.invoice440.request.RecordServiceType;
import ch.fd.invoice440.request.RecordTarmedType;
import ch.rgw.tools.TimeTool;

public class Rn44Comparator implements Comparator<Object> {
	private static final String TARMED_TARIF = "001";

	TimeTool time1 = new TimeTool();
	TimeTool time2 = new TimeTool();

	private GregorianCalendar cal1;
	private GregorianCalendar cal2;
	private String tarifType1;
	private String tarifType2;
	private String code1;
	private String code2;
	private String name1;
	private String name2;

	public Rn44Comparator() {
	}

	@Override
	public int compare(Object o1, Object o2) {
		initRecordValues1(o1);
		initRecordValues2(o2);

		if (cal1 == null) {
			return 1;
		}
		if (cal2 == null) {
			return -1;
		}

		time1.set(cal1);
		time2.set(cal2);
		int dat = time1.compareTo(time2);
		if (dat != 0) {
			return dat;
		}

		if (TARMED_TARIF.equals(tarifType1)) { // tarmed-tarmed: nach code sortieren
			if (TARMED_TARIF.equals(tarifType2)) {
				return code1.compareTo(code2);
			} else {
				return -1; // tarmed immer oberhab nicht-tarmed
			}
		} else if (TARMED_TARIF.equals(tarifType2)) {
			return 1; // nicht-tarmed immer unterhalb tarmed
		} else { // nicht-tarmed - nicht-tarmed: alphabetisch
			int diffc = tarifType1.compareTo(tarifType2);
			if (diffc == 0) {
				diffc = name1.compareToIgnoreCase(name2);
			}
			return diffc;
		}
	}

	private void initRecordValues1(Object object) {
		clearValues();
		if (object instanceof RecordServiceType) {
			RecordServiceType rec = (RecordServiceType) object;
			if (rec.getDateBegin() != null) {
				cal1 = rec.getDateBegin().toGregorianCalendar();
			}

			tarifType1 = getTarifType(rec);
			code1 = rec.getCode();
			name1 = rec.getName();
		} else if (object instanceof RecordTarmedType) {
			RecordTarmedType tarmed = (RecordTarmedType) object;
			if (tarmed.getDateBegin() != null) {
				cal1 = tarmed.getDateBegin().toGregorianCalendar();
			}

			tarifType1 = tarmed.getTariffType();
			code1 = tarmed.getCode();
			name1 = tarmed.getName();
		}
	}

	private void clearValues() {
		cal1 = null;
		cal2 = null;
		tarifType1 = "";
		tarifType2 = "";
		code1 = "";
		code2 = "";
		name1 = "";
		name2 = "";
	}

	private void initRecordValues2(Object object) {
		if (object instanceof RecordServiceType) {
			RecordServiceType rec = (RecordServiceType) object;
			if (rec.getDateBegin() != null) {
				cal2 = rec.getDateBegin().toGregorianCalendar();
			}

			tarifType2 = getTarifType(rec);
			code2 = rec.getCode();
			name2 = rec.getName();
		} else if (object instanceof RecordTarmedType) {
			RecordTarmedType tarmed = (RecordTarmedType) object;
			if (tarmed.getDateBegin() != null) {
				cal2 = tarmed.getDateBegin().toGregorianCalendar();
			}

			tarifType2 = tarmed.getTariffType();
			code2 = tarmed.getCode();
			name2 = tarmed.getName();
		}
	}

	private String getTarifType(RecordServiceType rec) {
		if (rec instanceof RecordOtherType) {
			RecordOtherType other = (RecordOtherType) rec;
			return other.getTariffType();
		} else if (rec instanceof RecordDrugType) {
			RecordDrugType drug = (RecordDrugType) rec;
			return drug.getTariffType();
		} else if (rec instanceof RecordDRGType) {
			RecordDRGType drg = (RecordDRGType) rec;
			return drg.getTariffType();
		} else if (rec instanceof RecordMigelType) {
			RecordMigelType migel = (RecordMigelType) rec;
			return migel.getTariffType();
		} else if (rec instanceof RecordLabType) {
			RecordLabType lab = (RecordLabType) rec;
			return lab.getTariffType();
		} else if (rec instanceof RecordParamedType) {
			RecordParamedType param = (RecordParamedType) rec;
			return param.getTariffType();
		}
		return "";
	}

}
