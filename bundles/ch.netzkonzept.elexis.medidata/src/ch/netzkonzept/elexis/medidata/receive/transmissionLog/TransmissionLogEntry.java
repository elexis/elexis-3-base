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
package ch.netzkonzept.elexis.medidata.receive.transmissionLog;

import java.text.SimpleDateFormat;

public class TransmissionLogEntry {

	public static final String DONE = "DONE";
	public static final String PROCESSING = "PROCESSING";
	public static final String ERROR = "ERROR";

	private String transmissionReference;
	private String created;
	private String modified;
	private String status;
	private String invoiceReference;
	private String controlFile;

	public String get(int columnNumber) {
		String returnString = new String();
		switch (columnNumber) {
		case 0:
			returnString = getTransmissionReference();
			break;
		case 1:
			returnString = getCreated();
			break;
		case 2:
			returnString = getModified();
			break;
		case 3:
			returnString = getStatus();
			break;
		case 4:
			returnString = getInvoiceReference();
			break;
		case 5:
			returnString = getControlFile();
			break;
		}
		return returnString;
	}

	public String getTransmissionReference() {
		return transmissionReference;
	}

	public void setTransmissionReference(String transmissionReference) {
		this.transmissionReference = transmissionReference;
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

	public String getModified() {
		String returnValue = new String();
		String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		String outputPattern = "dd.MM.yyyy HH:mm:ss";
		SimpleDateFormat inputFormatter = new SimpleDateFormat(inputPattern);
		SimpleDateFormat outputFormatter = new SimpleDateFormat(outputPattern);
		try {
			returnValue = outputFormatter.format(inputFormatter.parse(modified));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInvoiceReference() {
		return invoiceReference;
	}

	public void setInvoiceReference(String invoiceReference) {
		this.invoiceReference = invoiceReference;
	}

	public String getControlFile() {
		return controlFile;
	}

	public void setControlFile(String controlFile) {
		this.controlFile = controlFile;
	}
}
