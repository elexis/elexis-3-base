/*******************************************************************************
 * Copyright (c) 2020-2022,  Fabian Schmid and Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Fabian <f.schmid@netzkonzept.ch> - initial implementation
 *    Olivier Debenath <olivier@debenath.ch>
 *
 *******************************************************************************/
package ch.netzkonzept.elexis.medidata.receive.messageLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MessageLogEntry {

	private String id;
	private LocalisedString subject;
	private LocalisedString message;
	private String severity;
	private boolean read;
	private String created;
	private String template;
	private String mode;
	private String errorCode;
	private LocalisedString potentialReasons;
	private LocalisedString possibleSolutions;
	private String technicalInformation;

	public String get(int columnNumber) {
		String returnString = new String();
		switch (columnNumber) {
		case 0:
			returnString = getCreated();
			break;
		case 1:
			returnString = getId();
			break;
		case 2:
			returnString = getSubject() != null ? getSubject().getDe().toString() : "--";
			break;
		case 3:
			returnString = getSeverity();
			break;
		case 4:
			returnString = Boolean.valueOf(isRead()).toString();
			break;
		case 5:
			returnString = getTemplate();
			break;
		case 6:
			returnString = getMode();
			break;
		case 7:
			returnString = getErrorCode();
			break;
		case 8:
			returnString = getPotentialReasons() != null ? getPotentialReasons().getDe().toString() : "--";
			break;
		case 9:
			returnString = getPossibleSolutions() != null ? getPossibleSolutions().getDe().toString() : "--";
			break;
		case 10:
			returnString = getTechnicalInformation();
		}
		return returnString;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalisedString getSubject() {
		return subject;
	}

	public void setSubject(LocalisedString subject) {
		this.subject = subject;
	}

	public LocalisedString getMessage() {
		return message;
	}

	public void setMessage(LocalisedString message) {
		this.message = message;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getCreated() {
		String returnValue = new String();
		String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		String outputPattern = "dd.MM.yyyy HH:mm:ss";
		SimpleDateFormat inputFormatter = new SimpleDateFormat(inputPattern);
		SimpleDateFormat outputFormatter = new SimpleDateFormat(outputPattern);

		try {
			returnValue = outputFormatter.format(inputFormatter.parse(created));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public LocalisedString getPotentialReasons() {
		return potentialReasons;
	}

	public void setPotentialReasons(LocalisedString potentialReasons) {
		this.potentialReasons = potentialReasons;
	}

	public LocalisedString getPossibleSolutions() {
		return possibleSolutions;
	}

	public void setPossibleSolutions(LocalisedString possibleSolutions) {
		this.possibleSolutions = possibleSolutions;
	}

	public String getTechnicalInformation() {
		return technicalInformation;
	}

	public void setTechnicalInformation(String technicalInformation) {
		this.technicalInformation = technicalInformation;
	}
}