package at.medevit.ch.artikelstamm.extinfo;

import java.time.LocalDate;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import at.medevit.ch.artikelstamm.ARTIKELSTAMM.ITEMS.ITEM.ARTSL.ARTLIMS.ARTLIM;
import at.medevit.ch.artikelstamm.ARTIKELSTAMM.LIMITATIONS.LIMITATION;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class ArticleIndication {

	private String code;
	private String limcode;

	private String validFrom;
	private String validTo;

	private String limText;
	private String limTextF;

	public static ArticleIndication of(ARTLIM al, Map<String, LIMITATION> limitations) {
		ArticleIndication ret = new ArticleIndication();
		ret.code = al.getINDCD();
		ret.limcode = al.getLIMCD();
		if (al.getVDAT() != null) {
			ret.validFrom = getAsLocalDate(al.getVDAT()).toString();
		}
		if (al.getVTDAT() != null) {
			ret.validTo = getAsLocalDate(al.getVTDAT()).toString();
		}
		LIMITATION limitation = limitations.get(al.getLIMCD());
		if (limitation != null) {
			ret.limText = limitation.getDSCR();
			ret.limTextF = limitation.getDSCRF();
		}
		return ret;
	}

	private static LocalDate getAsLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
		return LocalDate.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(),
				xmlGregorianCalendar.getDay());
	}

	public String getCode() {
		return code;
	}

	public String getLimcode() {
		return limcode;
	}

	public String getLimText() {
		return limText;
	}

	public String getLimTextF() {
		return limTextF;
	}

	public String getLabel() {
		String lang = ConfigServiceHolder.get().getLocal(Preferences.ABL_LANGUAGE, "d");
		StringBuilder sb = new StringBuilder();
		sb.append(getCode()).append(":\n");
		if ("d".equals(lang)) {
			sb.append(getLimText());
		} else {
			sb.append(getLimTextF());
		}
		return sb.toString();
	}
}
