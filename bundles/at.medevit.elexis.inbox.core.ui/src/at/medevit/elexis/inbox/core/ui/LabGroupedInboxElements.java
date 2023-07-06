package at.medevit.elexis.inbox.core.ui;

import java.time.format.DateTimeFormatter;

import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;

public class LabGroupedInboxElements extends GroupedInboxElements {

	private static DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); //$NON-NLS-1$

	@Override
	public String getUri() {
		if (getPatient() != null) {
			return "lab://" + StoreToStringServiceHolder.getStoreToString(getPatient()); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public String getLabel() {
		if (!isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(getElements().size() + " Laborresultate");
			int pathologicCount = getPathologicCount();
			if (pathologicCount > 0) {
				sb.append(" davon " + pathologicCount + " pathologisch");
			}
			return sb.toString();
		}
		return "Keine Laborresultate";
	}

	public boolean isMatching(ILabResult labResult) {
		if (!isEmpty()) {
			if (((ILabResult) getFirstElement().getObject()).getObservationTime() != null
					&& labResult.getObservationTime() != null) {
				return ((ILabResult) getFirstElement().getObject()).getObservationTime().toLocalDate()
						.equals(labResult.getObservationTime().toLocalDate());
			} else if (((ILabResult) getFirstElement().getObject()).getDate() != null && labResult.getDate() != null) {
				return ((ILabResult) getFirstElement().getObject()).getDate().equals(labResult.getDate());
			}
		}
		// if empty match any
		return isEmpty();
	}

	private int getPathologicCount() {
		return (int) getElements().stream().map(ie -> (ILabResult) ie.getObject()).filter(lr -> lr.isPathologic())
				.count();
	}

	public boolean isPathologic() {
		return getElements().stream().map(ie -> (ILabResult) ie.getObject()).anyMatch(lr -> lr.isPathologic());
	}
}
