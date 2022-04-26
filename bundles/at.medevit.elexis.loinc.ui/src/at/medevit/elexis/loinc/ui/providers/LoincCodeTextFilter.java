package at.medevit.elexis.loinc.ui.providers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.loinc.model.LoincCode;

public class LoincCodeTextFilter extends ViewerFilter {

	private String searchString;

	public void setSearchText(String s) {
		if (s == null || s.length() == 0)
			searchString = s;
		else
			searchString = ".*" + s.toLowerCase() + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}
		LoincCode code = (LoincCode) element;

		String codeStr = code.getCode().toLowerCase();
		if (codeStr != null && codeStr.matches(searchString)) {
			return true;
		}

		String text = code.getText().toLowerCase();
		if (text != null && text.matches(searchString)) {
			return true;
		}

		return false;
	}
}
