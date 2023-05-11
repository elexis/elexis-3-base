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
			if (getFirstElement().getObject() instanceof ILabResult
					&& ((ILabResult) getFirstElement().getObject()).getObservationTime() != null) {
				return getElements().size() + " Laborresultate am " + ((ILabResult) getFirstElement().getObject())
						.getObservationTime().toLocalDate().format(defaultDateFormatter);
			} else {
				return getElements().size() + " Laborresultate";
			}
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
}
