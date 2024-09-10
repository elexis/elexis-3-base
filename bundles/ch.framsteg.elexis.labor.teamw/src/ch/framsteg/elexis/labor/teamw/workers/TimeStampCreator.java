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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class TimeStampCreator {

	private final static String DATE_TIME_PATTERN_SIMPLE = "props.app.date.time.pattern.simple";
	private final static String DATE_TIME_PATTERN_UTC = "props.app.date.time.pattern.utc";
	private static final String TXT_HOUR_TO_UTC_KEY = "key.time.shift";

	private Properties applicationProperties;

	private ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.UTC);
	private ZonedDateTime oldDateTime = dateTime;

	@Inject
	private IConfigService configService;

	Logger logger = LoggerFactory.getLogger(TimeStampCreator.class);

	public TimeStampCreator(Properties applicationProperties) {
		setApplicationProperties(applicationProperties);
		CoreUiUtil.injectServices(this);
		adjustDateTime();
	}

	// In case the system time differs from UTC
	private void adjustDateTime() {
		String timeShift = configService.get(TXT_HOUR_TO_UTC_KEY, "");
		if (!timeShift.isEmpty()) {
			Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
			if (pattern.matcher(timeShift).matches()) {
				logger.info("Valid time shift parameter detected: " + timeShift);
				Integer number = Integer.valueOf(timeShift);
				if (number > 0) {
					moveForward(number);
				} else if (number < 0) {
					moveBackward(number);
				} else {
					logger.info(number + " hour difference. No time shift performed");
				}
			}
		}
	}

	private void moveForward(int hour) {
		dateTime = dateTime.plusHours(hour);
		logger.info("Move " + oldDateTime + " " + hour + " hour forward --> " + dateTime);
	}

	private void moveBackward(int hour) {
		dateTime = dateTime.minusHours(hour);
		logger.info("Move " + oldDateTime + " " + hour + " hour backward --> " + dateTime);
	}

	public String getSimpleTimeStamp() {
		return dateTime
				.format(DateTimeFormatter.ofPattern(getApplicationProperties().getProperty(DATE_TIME_PATTERN_SIMPLE)));
	}

	public String getUTCTimeStamp() {
		return dateTime
				.format(DateTimeFormatter.ofPattern(getApplicationProperties().getProperty(DATE_TIME_PATTERN_UTC)));
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
}
