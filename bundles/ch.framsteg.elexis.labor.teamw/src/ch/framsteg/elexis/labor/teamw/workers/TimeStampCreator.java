package ch.framsteg.elexis.labor.teamw.workers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.Properties;

public class TimeStampCreator {

	private final static String DATE_TIME_PATTERN_SIMPLE = "props.app.date.time.pattern.simple";
	private final static String DATE_TIME_PATTERN_UTC = "props.app.date.time.pattern.utc";
	
	private Properties applicationProperties;

	private ZonedDateTime dateTime = ZonedDateTime.now();
	
	public TimeStampCreator(Properties applicationProperties) {
		setApplicationProperties(applicationProperties);
	}

	public String getSimpleTimeStamp() {
		ZonedDateTime dateTimeCorrected;
		ZonedDateTime now = ZonedDateTime.now( ZoneId.of( "Europe/Zurich" ) );
		ZoneId z = now.getZone();
		ZoneRules zoneRules = z.getRules();
		Boolean isDst = zoneRules.isDaylightSavings( now.toInstant());
		// If wintertime
		if (!isDst) {
			dateTimeCorrected = dateTime.minusHours(1);
		// If summertime
		} else {
			dateTimeCorrected = dateTime.minusHours(2);		}
		return dateTimeCorrected.format(DateTimeFormatter.ofPattern(getApplicationProperties().getProperty(DATE_TIME_PATTERN_SIMPLE)));
	}

	public String getUTCTimeStamp() {
		return dateTime.format(DateTimeFormatter.ofPattern(getApplicationProperties().getProperty(DATE_TIME_PATTERN_UTC)));
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
}
