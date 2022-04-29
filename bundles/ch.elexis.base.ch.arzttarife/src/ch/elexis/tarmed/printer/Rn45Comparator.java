package ch.elexis.tarmed.printer;

import org.apache.commons.lang3.StringUtils;
import java.util.Comparator;
import java.util.GregorianCalendar;

import ch.fd.invoice450.request.ServiceExType;
import ch.fd.invoice450.request.ServiceType;
import ch.rgw.tools.TimeTool;

public class Rn45Comparator implements Comparator<Object> {
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

	public Rn45Comparator() {
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
		if (object instanceof ServiceType) {
			if (((ServiceType) object).getDateBegin() != null) {
				cal1 = ((ServiceType) object).getDateBegin().toGregorianCalendar();
			}
			tarifType1 = ((ServiceType) object).getTariffType();
			code1 = ((ServiceType) object).getCode();
			name1 = ((ServiceType) object).getName();
		} else if (object instanceof ServiceExType) {
			if (((ServiceExType) object).getDateBegin() != null) {
				cal1 = ((ServiceExType) object).getDateBegin().toGregorianCalendar();
			}
			tarifType1 = ((ServiceExType) object).getTariffType();
			code1 = ((ServiceExType) object).getCode();
			name1 = ((ServiceExType) object).getName();
		}
	}

	private void clearValues() {
		cal1 = null;
		cal2 = null;
		tarifType1 = StringUtils.EMPTY;
		tarifType2 = StringUtils.EMPTY;
		code1 = StringUtils.EMPTY;
		code2 = StringUtils.EMPTY;
		name1 = StringUtils.EMPTY;
		name2 = StringUtils.EMPTY;
	}

	private void initRecordValues2(Object object) {
		if (object instanceof ServiceType) {
			if (((ServiceType) object).getDateBegin() != null) {
				cal2 = ((ServiceType) object).getDateBegin().toGregorianCalendar();
			}
			tarifType2 = ((ServiceType) object).getTariffType();
			code2 = ((ServiceType) object).getCode();
			name2 = ((ServiceType) object).getName();
		} else if (object instanceof ServiceExType) {
			if (((ServiceExType) object).getDateBegin() != null) {
				cal2 = ((ServiceExType) object).getDateBegin().toGregorianCalendar();
			}
			tarifType2 = ((ServiceExType) object).getTariffType();
			code2 = ((ServiceExType) object).getCode();
			name2 = ((ServiceExType) object).getName();
		}
	}
}
