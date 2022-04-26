package at.medevit.elexis.outbox.ui.part.provider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.ui.part.model.PatientOutboxElements;

public class OutboxViewerComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof PatientOutboxElements && e2 instanceof PatientOutboxElements) {
			return ((PatientOutboxElements) e2).getHighestLastupdate()
					.compareTo(((PatientOutboxElements) e1).getHighestLastupdate());
		}
		return ((IOutboxElement) e2).getLastupdate().compareTo(((IOutboxElement) e1).getLastupdate());
	}
}
