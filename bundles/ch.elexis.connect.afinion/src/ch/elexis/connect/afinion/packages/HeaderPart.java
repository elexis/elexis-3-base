package ch.elexis.connect.afinion.packages;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import ch.rgw.tools.TimeTool;

public class HeaderPart extends AbstractPart {
	private int recordNum;
	private int runNr;
	private String lotNr;
	private String id;
	private Calendar cal;

	public HeaderPart(final byte[] bytes) {
		parse(bytes);
	}

	public static Calendar getUTCBaseCalendar() {
		Calendar cal0_utc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal0_utc.set(1970, 0, 1, 0, 0, 0);
		cal0_utc.set(Calendar.MILLISECOND, 0);
		return cal0_utc;
	}

	public void parse(final byte[] bytes) {
		recordNum = getInteger(bytes, 0);
		runNr = getInteger(bytes, 4);
		lotNr = getString(bytes, 25, 17);
		id = getString(bytes, 42, 17);

		int dateSeconds = getInteger(bytes, 60); // Seconds since 1.1.1970 00:00 UTC
		cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.set(1970, 0, 1, 0, 0, 0);
		cal.set(Calendar.SECOND, dateSeconds);
		cal.getTime();
	}

	@Override
	public int length() {
		return 68;
	}

	public int getRecordNum() {
		return recordNum;
	}

	public String getLotNr() {
		return lotNr;
	}

	public int getRunNr() {
		return runNr;
	}

	public String getId() {
		return id;
	}

	public Calendar getCalendar() {
		return cal;
	}

	public TimeTool getDate() {
		return new TimeTool(getCalendar().getTimeInMillis());
	}

	private static String toTimeStampString(Calendar cal) {
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);

		String dateStr = (date < 10 ? "0" : "") + Integer.valueOf(date).toString();
		String monthStr = (month < 10 ? "0" : "") + Integer.valueOf(month).toString();
		String yearStr = Integer.valueOf(year).toString();
		String hourStr = (hour < 10 ? "0" : "") + Integer.valueOf(hour).toString();
		String minuteStr = (minutes < 10 ? "0" : "") + Integer.valueOf(minutes).toString();
		String secondStr = (seconds < 10 ? "0" : "") + Integer.valueOf(seconds).toString();

		return dateStr + "." + monthStr + "." + yearStr + " " + hourStr + ":" + minuteStr + ":" + secondStr + " ("
				+ cal.getTimeZone().getID() + ")";
	}

	public String toString() {
		String str = "";
		str += "H-Record " + recordNum + ";";
		str += " " + toTimeStampString(getCalendar()) + ";";
		str += " Run#:" + runNr + ";";
		str += " ID:" + id + ";";
		str += " Lot#:" + lotNr + ";";

		return str;
	}
}
