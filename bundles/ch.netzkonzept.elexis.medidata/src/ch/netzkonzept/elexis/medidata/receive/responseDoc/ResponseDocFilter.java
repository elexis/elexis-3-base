package ch.netzkonzept.elexis.medidata.receive.responseDoc;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ResponseDocFilter extends ViewerFilter {

	private String searchString;

	public void setSearchString(String s) {
		this.searchString = ".*" + s + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		ResponseDocEntry rde = (ResponseDocEntry) element;

		if (rde.getCreated() != null) {
			if (rde.getCreated().matches(searchString)) {
				return true;
			}
		}

		if (rde.getFilename() != null) {
			if (rde.getFilename().matches(searchString)) {
				return true;
			}
		}

		if (rde.getPath() != null) {
			if (rde.getPath().matches(searchString)) {
				return true;
			}
		}
		return false;
	}
}
