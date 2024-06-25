/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
