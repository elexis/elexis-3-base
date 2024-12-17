package ch.netzkonzept.elexis.medidata.receive.transmissionLog;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class TransmissionLogComparator extends ViewerComparator {

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public TransmissionLogComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	public int compare(Viewer viewer, Object e1, Object e2) {

		TransmissionLogEntry tle1 = (TransmissionLogEntry) e1;
		TransmissionLogEntry tle2 = (TransmissionLogEntry) e2;
		int rc = 0;

		switch (propertyIndex) {
		case 0:
			if (tle1.getTransmissionReference() != null) {
				rc = tle1.getTransmissionReference().compareTo(tle2.getTransmissionReference());
			}
			break;
		case 1:
			if (tle1.getCreated() != null) {
				rc = tle1.getCreated().compareTo(tle2.getCreated());
			}
			break;
		case 2:
			if (tle1.getModified() != null) {
				rc = tle1.getModified().compareTo(tle2.getModified());
			}
			break;
		case 3:
			if (tle1.getStatus() != null) {
				rc = tle1.getStatus().compareTo(tle2.getStatus());
			}
			break;
		case 4:
			if (tle1.getInvoiceReference() != null) {
				rc = tle1.getInvoiceReference().compareTo(tle2.getInvoiceReference());
			}
			break;
		/**
		 * case 5: if (tle1.getControlFile() != null && tle2.getControlFile() != null) {
		 * rc = tle1.getControlFile().compareTo(tle2.getControlFile()); } break;
		 */
		}

		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
}
