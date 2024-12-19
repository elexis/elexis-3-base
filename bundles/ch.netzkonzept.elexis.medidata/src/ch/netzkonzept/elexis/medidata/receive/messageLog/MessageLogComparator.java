package ch.netzkonzept.elexis.medidata.receive.messageLog;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class MessageLogComparator extends ViewerComparator {

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public MessageLogComparator() {
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

		MessageLogEntry mle1 = (MessageLogEntry) e1;
		MessageLogEntry mle2 = (MessageLogEntry) e2;
		int rc = 0;

		switch (propertyIndex) {
		case 0:
			if (mle1.getCreated() != null) {
				rc = mle1.getCreated().compareTo(mle2.getCreated());
			}
			break;
		case 1:
			if (mle1.getId() != null) {
				Integer i1 = Integer.parseInt(mle1.getId());
				Integer i2 = Integer.parseInt(mle2.getId());
				// rc = mle1.getId().compareTo(mle2.getId());
				rc = i1.compareTo(i2);

			}
			break;
		case 2:
			if (mle1.getSubject() != null) {
				rc = mle1.getSubject().getDe().toString().compareTo(mle2.getSubject().getDe().toString());
			}
			break;
		case 3:
			if (mle1.getSeverity() != null) {
				rc = mle1.getSeverity().compareTo(mle2.getSeverity());
			}
			break;
		case 4:
			rc = Boolean.valueOf(mle1.isRead()).compareTo(Boolean.valueOf(mle2.isRead()));
			break;

		case 5:
			if (mle1.getTemplate() != null) {
				rc = mle1.getTemplate().compareTo(mle2.getTemplate());
			}
			break;
		case 6:
			if (mle1.getMode() != null) {
				rc = mle1.getMode().compareTo(mle2.getMode());
			}
			break;
		case 7:
			if (mle1.getErrorCode() != null) {
				rc = mle1.getErrorCode().compareTo(mle2.getErrorCode());
			}
			break;
		case 8:
			if (mle1.getPotentialReasons() != null) {
				rc = mle1.getPotentialReasons().getDe().toString()
						.compareTo(mle2.getPotentialReasons().getDe().toString());
			}
			break;
		case 9:
			if (mle1.getPossibleSolutions() != null) {
				rc = mle1.getPossibleSolutions().getDe().toString()
						.compareTo(mle2.getPossibleSolutions().getDe().toString());
			}
			break;
		case 10:
			if (mle1.getTechnicalInformation() != null) {
				rc = mle1.getTechnicalInformation().compareTo(mle2.getTechnicalInformation());
			}
			break;
		default:
			rc = 0;
		}

		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
}
