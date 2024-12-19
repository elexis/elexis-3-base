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

import java.nio.file.Path;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import ch.netzkonzept.elexis.medidata.receive.messageLog.MessageLogEntry;
import ch.netzkonzept.elexis.medidata.receive.transmissionLog.TransmissionLogEntry;

public class TableColumnSorter extends ViewerComparator {
	private int colIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public TableColumnSorter() {
		this.colIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.colIndex) {
			direction = 1 - direction;
		} else {
			this.colIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int rc = 0;

		if (e1 instanceof Path) {
			Path o1 = (Path) e1;
			Path o2 = (Path) e2;
			switch (colIndex) {
			case 0:
				rc = ((o1.toFile().lastModified() > o2.toFile().lastModified()) ? 1 : -1);
				break;
			case 1:
				rc = o1.getFileName().compareTo(o2.getFileName());
				break;
			case 2:
				rc = o1.toAbsolutePath().compareTo(o2.toAbsolutePath());
				break;
			}
		}

		if (e1 instanceof MessageLogEntry) {
			MessageLogEntry o1 = (MessageLogEntry) e1;
			MessageLogEntry o2 = (MessageLogEntry) e2;
			switch (colIndex) {
			case 0:
				rc = o1.getCreated().compareTo(o2.getCreated());
				break;
			case 1:
				rc = o1.getId().compareTo(o2.getId());
				break;
			case 2:
				rc = o1.getErrorCode().compareTo(o2.getErrorCode());
				break;
			case 3:
				rc = o1.getMessage().getDe().compareTo(o2.getMessage().getDe());
				break;
			}
		}

		if (e1 instanceof TransmissionLogEntry) {
			TransmissionLogEntry o1 = (TransmissionLogEntry) e1;
			TransmissionLogEntry o2 = (TransmissionLogEntry) e2;
			switch (colIndex) {
			case 0:
				rc = o1.getCreated().compareTo(o2.getCreated());
				break;
			case 1:
				rc = o1.getModified().compareTo(o2.getModified());
				break;
			case 2:
				rc = o1.getStatus().compareTo(o2.getStatus());
				break;
			case 3:
				rc = o1.getInvoiceReference().compareTo(o2.getInvoiceReference());
				break;
			case 4:
				rc = o1.getTransmissionReference().compareTo(o2.getTransmissionReference());
				break;
			}
		}

		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

}
