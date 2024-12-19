package ch.netzkonzept.elexis.medidata.receive.transmissionLog;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class TransmissionLogFilter extends ViewerFilter {

	private String searchString;

	public void setSearchString(String s) {
		this.searchString = ".*" + s + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		TransmissionLogEntry tle = (TransmissionLogEntry) element;

		if (tle.getTransmissionReference() != null) {
			if (tle.getTransmissionReference().matches(searchString)) {
				return true;
			}
		}

		if (tle.getCreated() != null) {
			if (tle.getCreated().matches(searchString)) {
				return true;
			}
		}

		if (tle.getModified() != null) {
			if (tle.getModified().matches(searchString)) {
				return true;
			}
		}

		if (tle.getStatus() != null) {
			if (tle.getStatus().matches(searchString)) {
				return true;
			}
		}

		if (tle.getInvoiceReference() != null) {
			if (tle.getInvoiceReference().matches(searchString)) {
				return true;
			}
		}

		if (tle.getControlFile() != null) {
			if (tle.getControlFile().matches(searchString)) {
				return true;
			}
		}
		return false;
	}
}
