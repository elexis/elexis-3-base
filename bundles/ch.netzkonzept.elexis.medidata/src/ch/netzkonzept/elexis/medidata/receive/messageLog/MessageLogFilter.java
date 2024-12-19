package ch.netzkonzept.elexis.medidata.receive.messageLog;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class MessageLogFilter extends ViewerFilter {

	private String searchString;

	public void setSearchString(String s) {
		this.searchString = ".*" + s + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		MessageLogEntry mle = (MessageLogEntry) element;

		if (mle.getId() != null) {
			if (mle.getId().matches(searchString)) {
				return true;
			}
		}
		if (mle.getSubject() != null) {
			if (mle.getSubject().getDe().toString().matches(searchString)) {
				return true;
			}
		}
		if (mle.getSeverity() != null) {
			if (mle.getSeverity().matches(searchString)) {
				return true;
			}
		}
		if (Boolean.valueOf(mle.isRead()).toString().matches(searchString)) {
			return true;
		}
		if (mle.getCreated() != null) {
			if (mle.getCreated().matches(searchString)) {
				return true;
			}
		}
		if (mle.getTemplate() != null) {
			if (mle.getTemplate().matches(searchString)) {
				return true;
			}
		}
		if (mle.getMode() != null) {
			if (mle.getMode().matches(searchString)) {
				return true;
			}
		}
		if (mle.getErrorCode() != null) {
			if (mle.getErrorCode().matches(searchString)) {
				return true;
			}
		}
		if (mle.getPotentialReasons() != null) {
			if (mle.getPotentialReasons().getDe().toString().matches(searchString)) {
				return true;
			}
		}
		if (mle.getPossibleSolutions() != null) {
			if (mle.getPossibleSolutions().getDe().toString().matches(searchString)) {
				return true;
			}
		}
		if (mle.getTechnicalInformation() != null) {
			if (mle.getTechnicalInformation().matches(searchString)) {
				return true;
			}
		}
		return false;
	}
}
