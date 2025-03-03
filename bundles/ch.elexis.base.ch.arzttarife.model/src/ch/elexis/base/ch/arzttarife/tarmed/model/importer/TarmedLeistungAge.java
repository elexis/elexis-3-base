package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class TarmedLeistungAge {

	private LocalDate validFromDate;
	private LocalDate validToDate;

	private long fromDays;
	private long toDays;
	private String fromText;
	private String toText;

	private TarmedLeistungAge(String datesString, String ageString, LocalDateTime consDate) {
		parseDatesString(datesString);
		parseAgeString(ageString, consDate);
	}

	private void parseAgeString(String ageString, LocalDateTime consDate) {
		String[] parts = ageString.split("\\|");
		if (parts.length == 5) {
			fromDays = getAsDays(parts[0], parts[1], parts[4], consDate, false);
			fromText = getAsText(parts[0], parts[1], parts[4]);
			toDays = getAsDays(parts[2], parts[3], parts[4], consDate, true);
			toText = getAsText(parts[2], parts[3], parts[4]);
		} else {
			fromDays = -1;
			toDays = -1;
		}
	}

	private String getAsText(String age, String tolerance, String unit) {
		if (age.equals("-1")) {
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		try {
			int ageInt = Integer.parseInt(age);
			if (unit.equals("26")) {
				sb.append(ageInt).append(" Jahre");
			} else if (unit.equals("23")) {
				sb.append(ageInt).append(" Monate");
			}
			int toleanceInt = Integer.parseInt(tolerance);
			sb.append(", ").append(toleanceInt).append(" Tage Toleranz");
		} catch (NumberFormatException ne) {
			// ignore
		}
		return sb.toString();
	}

	private long getAsDays(String age, String tolerance, String unit, LocalDateTime consDate,
			boolean positiveTolerance) {
		if (age.equals("-1")) {
			return -1;
		}
		try {
			int ageInt = Integer.parseInt(age);
			if (unit.equals("26")) {
				LocalDateTime beforeCons = consDate.minus(ageInt, ChronoUnit.YEARS);
				ageInt = (int) ChronoUnit.DAYS.between(beforeCons, consDate);
			} else if (unit.equals("23")) {
				LocalDateTime beforeCons = consDate.minus(ageInt, ChronoUnit.MONTHS);
				ageInt = (int) ChronoUnit.DAYS.between(beforeCons, consDate);
			}
			int toleanceInt = Integer.parseInt(tolerance);
			if (positiveTolerance) {
				return ageInt + toleanceInt;
			} else {
				return ageInt - toleanceInt;
			}
		} catch (NumberFormatException ne) {
			// ignore
		}
		return -1;
	}

	private void parseDatesString(String datesString) {
		String[] parts = datesString.split("\\|");
		if (parts.length == 2) {
			validFromDate = LocalDate.parse(parts[0]);
			validToDate = LocalDate.parse(parts[1]);
		}
	}

	public boolean isValid() {
		return fromDays != -1 || toDays != -1;
	}

	public boolean isValidOn(LocalDate localDate) {
		return (validFromDate.isBefore(localDate) || validFromDate.isEqual(localDate))
				&& (validToDate.isAfter(localDate) || validToDate.isEqual(localDate));
	}

	/**
	 * Create {@link TarmedLeistungAge} objects for the ages String, relative to the
	 * consDate.
	 *
	 * @param ages
	 * @param consDate
	 * @return
	 */
	public static List<TarmedLeistungAge> of(String ages, LocalDateTime consDate) {
		List<TarmedLeistungAge> ret = new ArrayList<>();
		if (ages != null && !ages.isEmpty()) {
			String[] singleAge = ages.split(", ");
			for (String string : singleAge) {
				String[] validParts = isValidAgeString(string);
				if (validParts != null && validParts.length == 2) {
					ret.add(new TarmedLeistungAge(validParts[0], validParts[1], consDate));
				} else {
					LoggerFactory.getLogger(TarmedLeistungAge.class)
							.warn("Could not parse age string [" + string + "]");
				}
			}
		}
		return ret;
	}

	private static String[] isValidAgeString(String ageDefinition) {
		int dateStart = ageDefinition.indexOf('[');
		String datesString = ageDefinition.substring(dateStart + 1, ageDefinition.length() - 1);
		String ageString = ageDefinition.substring(0, dateStart);
		if (datesString != null && !datesString.isEmpty() && ageString != null && !ageString.isEmpty()) {
			return new String[] { datesString, ageString };
		}
		return null;
	}

	public long getFromDays() {
		return fromDays;
	}

	public long getToDays() {
		return toDays;
	}

	public String getFromText() {
		return fromText;
	}

	public String getToText() {
		return toText;
	}

	public String getText() {
		return "ab " + getFromText() + " bis " + getToText();
	}
}
