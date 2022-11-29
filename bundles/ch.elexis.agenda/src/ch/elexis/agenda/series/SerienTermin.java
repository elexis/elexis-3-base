package ch.elexis.agenda.series;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.data.IPlannable;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class SerienTermin {
	//@formatter:off
	/**
	 * configuration string syntax
	 *
	 * BEGINTIME,ENDTIME;SERIES_TYPE;[SERIES_PATTERN];BEGINDATE;[ENDING_TYPE];[ENDING_PATTERN]
	 *
	 * [SERIES_TYPE]
	 * D aily
	 * W eekly
	 * M onthly
	 * Y early
	 *
	 * [SERIES_PATTERN]
	 * daily		""
	 * weekly		Number_of_weeks_between, day { day } .
	 * monthly		day_of_month
	 * yearly		ddMM
	 *
	 * [ENDING_TYPE]
	 * O ends after n occurences -> requires number of occurences
	 * D ends on date -> requires date
	 *
	 * [ENDING_PATTERN]
	 * if EA: number
	 * if EO: date
	 */
	//@formatter:on

	public static DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
	public static DateFormat timeFormat = new SimpleDateFormat("HHmm");
	public static DecimalFormat decimalFormat = new DecimalFormat("00");

	private Date beginTime;
	private Date endTime;
	private Date seriesStartDate;

	private Date endsOnDate;
	private String endsAfterNDates;

	private SeriesType seriesType;
	private EndingType endingType;
	private String seriesPatternString;
	private String endingPatternString;

	private Kontakt contact;
	private String freeText; // if contact == null may contain freetext
	private String reason;
	private String status;

	// persistence information
	private String groupId;
	private Termin rootTermin;
	// ------------------

	public final static long SECOND_MILLIS = 1000;
	public final static long MINUTE_MILLIS = SECOND_MILLIS * 60;
	public final static long HOUR_MILLIS = MINUTE_MILLIS * 60;
	public final static long DAY_MILLIS = HOUR_MILLIS * 24;
	public final static long YEAR_MILLIS = DAY_MILLIS * 365;

	private static Logger logger = LoggerFactory.getLogger(SerienTermin.class);

	public SerienTermin() {
		beginTime = new Date();
		Calendar endTimeCalendar = Calendar.getInstance();
		endTimeCalendar.add(Calendar.MINUTE, 30);
		endTime = endTimeCalendar.getTime();

		Calendar startDateMidnight = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		startDateMidnight.set(Calendar.HOUR_OF_DAY, 0);
		startDateMidnight.set(Calendar.MINUTE, 0);
		startDateMidnight.set(Calendar.SECOND, 0);
		startDateMidnight.set(Calendar.MILLISECOND, 0);
		seriesStartDate = startDateMidnight.getTime();

		seriesType = SeriesType.WEEKLY;
		seriesPatternString = "1," + Calendar.MONDAY;
		endingType = EndingType.ON_SPECIFIC_DATE;

		Calendar nextWeek = Calendar.getInstance();
		nextWeek.add(Calendar.DAY_OF_YEAR, 7);
		endsOnDate = nextWeek.getTime();

		contact = ElexisEventDispatcher.getSelectedPatient();
		if (contact == null)
			freeText = "";
	}

	public SerienTermin(IPlannable pl) {
		Termin t = (Termin) pl;
		groupId = t.get(Termin.FLD_LINKGROUP);
		rootTermin = Termin.load(groupId);
		contact = rootTermin.getKontakt();
		if (contact == null)
			setFreeText(rootTermin.get(Termin.FLD_PATIENT));
		reason = rootTermin.getGrund();
		status = rootTermin.getStatus();
		parseSerienTerminConfigurationString(rootTermin.get(Termin.FLD_EXTENSION));
	}

	/**
	 * Initialize a {@link SerienTermin} according to a <i>serientermin
	 * configuration string</i> such as for example
	 * <code>1200,1230;W,1,3|4;04042008,EA,10</code> for the syntax see the
	 * documentation in. the {@link SerienTermin} class <br>
	 * <br>
	 * Use with care, malformed strings will not be treated defensively!
	 *
	 * Care about thread safety!
	 *
	 * @param serienTerminConfigurationString
	 */
	private void parseSerienTerminConfigurationString(String serienTerminConfigurationString) {
		String[] terms = serienTerminConfigurationString.split(";");
		String[] termin = terms[0].split(",");
		SimpleDateFormat timeDf = new SimpleDateFormat("HHmm");
		SimpleDateFormat dateDf = new SimpleDateFormat("ddMMyyyy");

		try {
			beginTime = timeDf.parse(termin[0]);
			endTime = timeDf.parse(termin[1]);
			seriesStartDate = dateDf.parse(terms[3]);
		} catch (Exception e) {
			logger.error("unexpected exception", e);
		}

		char seriesTypeCharacter = terms[1].toUpperCase().charAt(0);
		setSeriesType(SeriesType.getForCharacter(seriesTypeCharacter));
		seriesPatternString = terms[2];

		char endingTypeCharacter = terms[4].toUpperCase().charAt(0);
		endingType = EndingType.getForCharacter(endingTypeCharacter);
		endingPatternString = terms[5];

		switch (endingType) {
		case ON_SPECIFIC_DATE:
			try {
				endsOnDate = dateDf.parse(endingPatternString);
			} catch (Exception e) {
				logger.error("unexpected exception", e);
			}
			break;
		case AFTER_N_OCCURENCES:
			endsAfterNDates = endingPatternString;
			break;
		default:
			break;
		}

	}

	/**
	 * persist the recurring date into the database; this creates a series of
	 * {@link Termin} entries according to the pattern
	 */
	public void persist() {
		if (groupId != null) {
			delete(false);
		}
		createRootDate();
		createSubSequentDates();
	}

	/**
	 * Deletes the entire {@link SerienTermin}
	 *
	 * @param askForConfirmation
	 */
	public void delete(boolean askForConfirmation) {
		rootTermin.delete(askForConfirmation);
	}

	private void createSubSequentDates() {
		TimeTool dateIncrementer = rootTermin.getStartTime();

		int occurences = 0;
		TimeTool endingDate = null;
		if (endingType.equals(EndingType.AFTER_N_OCCURENCES)) {
			occurences = (Integer.parseInt(endsAfterNDates) - 1);
		} else {
			endingDate = new TimeTool(endsOnDate);
		}

		switch (seriesType) {
		case DAILY:
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE)) {
				occurences = dateIncrementer.daysTo(endingDate) + 1;
			}
			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.DAY_OF_YEAR, 1);
				writeSubsequentDateEntry(dateIncrementer);
			}
			break;
		case WEEKLY:
			String[] separatedSeriesPattern = getSeriesPatternString().split(",");
			int weekStepSize = Integer.parseInt(separatedSeriesPattern[0]);
			System.out.println("week step size =" + weekStepSize);
			// handle week 1
			for (int i = 1; i < separatedSeriesPattern[1].length(); i++) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateIncrementer.getTime());
				int dayValue = Integer.parseInt(separatedSeriesPattern[1].charAt(i) + "");
				cal.set(Calendar.DAY_OF_WEEK, dayValue);
				writeSubsequentDateEntry(new TimeTool(cal.getTime()));
			}
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE)) {
				long milisecondsDiff = 0;
				if (endingDate != null) {
					milisecondsDiff = endingDate.getTime().getTime() - dateIncrementer.getTime().getTime();
				}

				int days = (int) (milisecondsDiff / (1000 * 60 * 60 * 24));
				int weeks = days / 7;
				occurences = weeks / weekStepSize;
			}
			// handle subsequent weeks
			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.WEEK_OF_YEAR, weekStepSize);
				for (int j = 0; j < separatedSeriesPattern[1].length(); j++) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dateIncrementer.getTime());
					int dayValue = Integer.parseInt(separatedSeriesPattern[1].charAt(j) + "");
					cal.set(Calendar.DAY_OF_WEEK, dayValue);
					writeSubsequentDateEntry(new TimeTool(cal.getTime()));
				}
			}
			break;
		case MONTHLY:
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE) && endingDate != null) {
				occurences = (endingDate.get(Calendar.YEAR) - dateIncrementer.get(Calendar.YEAR)) * 12
						+ (endingDate.get(Calendar.MONTH) - dateIncrementer.get(Calendar.MONTH))
						+ (endingDate.get(Calendar.DAY_OF_MONTH) >= dateIncrementer.get(Calendar.DAY_OF_MONTH) ? 0
								: -1);
			}
			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.MONTH, 1);
				writeSubsequentDateEntry(dateIncrementer);
			}
			break;
		case YEARLY:
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE) && endingDate != null) {
				int monthOccurences = (endingDate.get(Calendar.YEAR) - dateIncrementer.get(Calendar.YEAR)) * 12
						+ (endingDate.get(Calendar.MONTH) - dateIncrementer.get(Calendar.MONTH))
						+ (endingDate.get(Calendar.DAY_OF_MONTH) >= dateIncrementer.get(Calendar.DAY_OF_MONTH) ? 0
								: -1);
				occurences = (monthOccurences / 12);
			}
			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.YEAR, 1);
				writeSubsequentDateEntry(dateIncrementer);
			}
			break;
		default:
			break;
		}

	}

	private void writeSubsequentDateEntry(TimeTool dateIncrementer) {
		TimeTool endTime = new TimeTool(dateIncrementer);
		endTime.addMinutes(getAppointmentDuration());

		TimeSpan ts = new TimeSpan(dateIncrementer, endTime);
		Termin t = new Termin(Activator.getDefault().getActResource(), ts, "series");
		t.set(Termin.FLD_LINKGROUP, groupId);
		if (StringUtils.isNotBlank(status)) {
			t.setStatus(status);
		}
		if (StringUtils.isNotBlank(rootTermin.getGrund())) {
			t.setGrund(rootTermin.getGrund());
		}

		System.out.println("writing subsequent date entry " + endTime.dump());
	}

	private void createRootDate() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(seriesStartDate);
		cal.add(Calendar.HOUR, beginTime.getHours());
		cal.add(Calendar.MINUTE, beginTime.getMinutes());

		TimeTool startTime = getRootTerminStartTime(cal);

		TimeTool endTime = new TimeTool(startTime);
		endTime.addMinutes(getAppointmentDuration());

		TimeSpan ts = new TimeSpan(startTime, endTime);
		rootTermin = new Termin(Activator.getDefault().getActResource(), ts, "series");

		groupId = rootTermin.getId();
		rootTermin.set(Termin.FLD_LINKGROUP, groupId);
		if (contact != null) {
			rootTermin.setKontakt(contact);
		} else {
			rootTermin.set(Termin.FLD_PATIENT, getFreeText());
		}
		if (StringUtils.isNotBlank(status)) {
			rootTermin.setStatus(status);
		}

		rootTermin.setGrund(reason);
		rootTermin.set(Termin.FLD_CREATOR, ContextServiceHolder.get().getActiveUser().get().getLabel());
		rootTermin.set(Termin.FLD_EXTENSION, this.toString());
	}

	private TimeTool getRootTerminStartTime(Calendar cal) {
		TimeTool tt = new TimeTool(cal.getTime());

		switch (seriesType) {
		case DAILY:
			return tt;

		case WEEKLY:
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(cal.getTime());
			int firstDay = Integer.parseInt(getSeriesPatternString().split(",")[1].charAt(0) + "");
			cal2.set(Calendar.DAY_OF_WEEK, firstDay);
			TimeTool ret = new TimeTool(cal2.getTime());
			return ret;

		case MONTHLY:
			int monthDay = Integer.parseInt(seriesPatternString);
			Calendar calendarMonth = Calendar.getInstance();
			calendarMonth.clear();
			calendarMonth.set(Calendar.YEAR, tt.get(TimeTool.YEAR));
			if (tt.get(Calendar.DAY_OF_MONTH) <= monthDay) {
				calendarMonth.set(Calendar.MONTH, tt.get(Calendar.MONTH));
			} else {
				calendarMonth.set(Calendar.MONTH, tt.get(Calendar.MONTH));
				calendarMonth.add(Calendar.MONTH, 1);
			}
			calendarMonth.set(Calendar.DAY_OF_MONTH, monthDay);
			return new TimeTool(calendarMonth.getTime());

		case YEARLY:
			Calendar targetCal = Calendar.getInstance();
			targetCal.clear();
			targetCal.set(Calendar.YEAR, tt.get(TimeTool.YEAR));
			int day = Integer.parseInt(seriesPatternString.substring(0, 2));
			int month = Integer.parseInt(seriesPatternString.substring(2, 4));
			targetCal.set(Calendar.DAY_OF_MONTH, day);
			targetCal.set(Calendar.MONTH, month - 1);
			TimeTool target = new TimeTool(targetCal.getTime());
			if (tt.isBefore(target))
				return target;
			target.add(TimeTool.YEAR, 1);
			return target;
		}
		return tt;
	}

	public boolean collidesWithLockTimes() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(seriesStartDate);
		cal.add(Calendar.HOUR, beginTime.getHours());
		cal.add(Calendar.MINUTE, beginTime.getMinutes());
		TimeTool startTime = getRootTerminStartTime(cal);

		TimeTool endTime = new TimeTool(startTime);
		endTime.addMinutes(getAppointmentDuration());

		TimeSpan ts = new TimeSpan(startTime, endTime);
		rootTermin = new Termin(Activator.getDefault().getActResource(), ts, "series");
		TimeTool dateIncrementer = rootTermin.getStartTime();

		List<TimeTool> seriesTimesList = getAllTimesOfSeries(dateIncrementer);
		String bereich = Activator.getDefault().getActResource();

		for (TimeTool sTime : seriesTimesList) {
			TimeTool eTime = new TimeTool(sTime);
			eTime.addMinutes(getAppointmentDuration());
			TimeSpan span = new TimeSpan(sTime, eTime);

			// get all appointments where type=locked and day=X and bereich=y
			Query<Termin> qbe = new Query<Termin>(Termin.class);
			qbe.add(Termin.FLD_TERMINTYP, Query.EQUALS, ch.elexis.agenda.Messages.Termin_range_locked);
			qbe.add(Termin.FLD_TAG, Query.EQUALS, sTime.toString(TimeTool.DATE_COMPACT));
			qbe.add(Termin.FLD_BEREICH, Query.EQUALS, bereich);
			qbe.add(Termin.FLD_DELETED, Query.EQUALS, "0");
			List<Termin> locks = qbe.execute();

			for (Termin lockTermin : locks) {
				TimeSpan lockSpan = lockTermin.getTimeSpan();

				if (lockSpan.overlap(span) != null) {
					rootTermin.delete(false);
					return true;
				}
			}
		}

		// clean up
		rootTermin.delete(false);
		return false;
	}

	private List<TimeTool> getAllTimesOfSeries(TimeTool dateIncrementer) {
		List<TimeTool> seriesTimesList = new ArrayList<TimeTool>();

		// calculate occurrences
		int occurences = 0;
		TimeTool endingDate = null;
		if (endingType.equals(EndingType.AFTER_N_OCCURENCES)) {
			occurences = (Integer.parseInt(endsAfterNDates) - 1);
		} else {
			endingDate = new TimeTool(endsOnDate);
		}

		switch (seriesType) {
		case DAILY:
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE)) {
				occurences = dateIncrementer.daysTo(endingDate) + 1;
			}

			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.DAY_OF_YEAR, 1);
				seriesTimesList.add(dateIncrementer);
			}
			break;

		case WEEKLY:
			String[] seriesPattern = getSeriesPatternString().split(",");
			int weekStepSize = Integer.parseInt(seriesPattern[0]);
			// handle start week
			for (int i = 1; i < seriesPattern[1].length(); i++) {
				Calendar calWeekOne = Calendar.getInstance();
				calWeekOne.setTime(dateIncrementer.getTime());
				int dayValue = Integer.parseInt(seriesPattern[1].charAt(i) + "");
				calWeekOne.set(Calendar.DAY_OF_WEEK, dayValue);
				seriesTimesList.add(new TimeTool(calWeekOne.getTime()));
			}

			// calculate occurrences per week
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE)) {
				long milisecondsDiff = 0;
				if (endingDate != null) {
					milisecondsDiff = endingDate.getTime().getTime() - dateIncrementer.getTime().getTime();
				}
				int days = (int) (milisecondsDiff / (1000 * 60 * 60 * 24));
				int weeks = days / 7;
				occurences = weeks / weekStepSize;
			}
			// handle subsequent weeks
			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.WEEK_OF_YEAR, weekStepSize);
				for (int j = 0; j < seriesPattern[1].length(); j++) {
					Calendar calSub = Calendar.getInstance();
					calSub.setTime(dateIncrementer.getTime());
					int dayValue = Integer.parseInt(seriesPattern[1].charAt(j) + "");
					calSub.set(Calendar.DAY_OF_WEEK, dayValue);
					seriesTimesList.add(new TimeTool(calSub.getTime()));
				}
			}
			break;
		case MONTHLY:
			// calculate occurrences per month
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE) && endingDate != null) {
				occurences = (endingDate.get(Calendar.YEAR) - dateIncrementer.get(Calendar.YEAR)) * 12
						+ (endingDate.get(Calendar.MONTH) - dateIncrementer.get(Calendar.MONTH))
						+ (endingDate.get(Calendar.DAY_OF_MONTH) >= dateIncrementer.get(Calendar.DAY_OF_MONTH) ? 0
								: -1);
			}

			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.MONTH, 1);
				seriesTimesList.add(dateIncrementer);
			}
			break;
		case YEARLY:
			// calculate occurrences per year
			if (endingType.equals(EndingType.ON_SPECIFIC_DATE) && endingDate != null) {
				int monthOccurences = (endingDate.get(Calendar.YEAR) - dateIncrementer.get(Calendar.YEAR)) * 12
						+ (endingDate.get(Calendar.MONTH) - dateIncrementer.get(Calendar.MONTH))
						+ (endingDate.get(Calendar.DAY_OF_MONTH) >= dateIncrementer.get(Calendar.DAY_OF_MONTH) ? 0
								: -1);
				occurences = (monthOccurences / 12);
			}

			for (int i = 0; i < occurences; i++) {
				dateIncrementer.add(Calendar.YEAR, 1);
				seriesTimesList.add(dateIncrementer);
			}
			break;
		default:
			break;
		}
		return seriesTimesList;
	}

	@Override
	public String toString() {
		// BEGINTIME,ENDTIME;SERIES_TYPE;[SERIES_PATTERN];BEGINDATE;[ENDING_TYPE];[ENDING_PATTERN]
		StringBuilder sb = new StringBuilder();
		try {
			SimpleDateFormat timeDf = new SimpleDateFormat("HHmm");
			SimpleDateFormat dateDf = new SimpleDateFormat("ddMMyyyy");

			sb.append(timeDf.format(beginTime));
			sb.append(",");
			sb.append(timeDf.format(endTime));
			sb.append(";");
			sb.append(getSeriesType().getSeriesTypeCharacter());
			sb.append(";");
			sb.append(seriesPatternString);
			sb.append(";");
			sb.append(dateDf.format(seriesStartDate));
			sb.append(";");
			sb.append(endingType.getEndingTypeChar());
			sb.append(";");

			switch (getEndingType()) {
			case AFTER_N_OCCURENCES:
				sb.append(endsAfterNDates);
				break;
			case ON_SPECIFIC_DATE:
				sb.append(dateDf.format(endsOnDate));
				break;
			default:
				break;
			}
		} catch (NullPointerException npe) {
			sb.append("incomplete configuration string: " + npe.getMessage());
		}
		return sb.toString();
	}

	/**
	 * @return the duration of the appointment (endTime - beginTime); if < 0 returns
	 *         0
	 */
	public int getAppointmentDuration() {
		int result = (int) ((endTime.getTime() / MINUTE_MILLIS) - (beginTime.getTime() / MINUTE_MILLIS));
		if (result < 0)
			return 0;
		return result;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getSeriesStartDate() {
		return seriesStartDate;
	}

	public void setSeriesStartDate(Date seriesStartDate) {
		this.seriesStartDate = seriesStartDate;
	}

	public EndingType getEndingType() {
		return endingType;
	}

	public void setEndingType(EndingType endingType) {
		this.endingType = endingType;
	}

	public String getSeriesPatternString() {
		return seriesPatternString;
	}

	public void setSeriesPatternString(String seriesPatternString) {
		this.seriesPatternString = seriesPatternString;
	}

	public String getEndingPatternString() {
		return endingPatternString;
	}

	public void setEndingPatternString(String endingPatternString) {
		this.endingPatternString = endingPatternString;
	}

	public Kontakt getContact() {
		return contact;
	}

	public void setContact(Kontakt contact) {
		this.contact = contact;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public SeriesType getSeriesType() {
		return seriesType;
	}

	public void setSeriesType(SeriesType seriesType) {
		this.seriesType = seriesType;
	}

	public Date getEndsOnDate() {
		return endsOnDate;
	}

	public void setEndsOnDate(Date endsOnDate) {
		this.endsOnDate = endsOnDate;
	}

	public String getEndsAfterNDates() {
		return endsAfterNDates;
	}

	public void setEndsAfterNDates(String endsAfterNDates) {
		this.endsAfterNDates = endsAfterNDates;
	}

	public Termin getRootTermin() {
		return rootTermin;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
