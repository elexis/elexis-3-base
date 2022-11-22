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
package ch.netzkonzept.elexis.medidata.receive;

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

	public String getTransmissionReference() {
		return transmissionReference;
	}

	public void setTransmissionReference(String transmissionReference) {
		this.transmissionReference = transmissionReference;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
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
