package ch.netzkonzept.elexis.medidata.receive.responseDoc;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class ResponseDocComparator extends ViewerComparator {

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public ResponseDocComparator() {
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

		ResponseDocEntry rde1 = (ResponseDocEntry) e1;
		ResponseDocEntry rde2 = (ResponseDocEntry) e2;

		int rc = 0;

		switch (propertyIndex) {
		case 0:
			if (rde1.getCreated() != null) {
				rc = rde1.getCreated().compareTo(rde2.getCreated());
			}
			break;
		case 1:
			if (rde1.getFilename() != null) {
				rc = rde1.getFilename().compareTo(rde2.getFilename());
			}
			break;
		case 2:
			if (rde1.getPath() != null) {
				rc = rde1.getPath().compareTo(rde2.getPath());
				break;
			}
		default:
			rc = 0;

		}
		if (direction == DESCENDING) {
			rc = -rc;

		}
		return rc;
	}

}
