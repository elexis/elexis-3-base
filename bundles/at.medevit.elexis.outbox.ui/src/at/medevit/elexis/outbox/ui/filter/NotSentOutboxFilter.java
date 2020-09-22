package at.medevit.elexis.outbox.ui.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.IOutboxElementService.State;

public class NotSentOutboxFilter extends ViewerFilter {
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		if (element instanceof IOutboxElement) {
			return ((IOutboxElement) element).getState() != State.SENT;
		}
		return true;
	}
}
