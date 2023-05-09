package at.medevit.elexis.inbox.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.ui.part.InboxView;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.data.Patient;

public class ActivatePatientCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		if (part instanceof InboxView) {
			InboxView view = (InboxView) part;
			IStructuredSelection sel = (IStructuredSelection) view.getViewer().getSelection();
			Object element = sel.getFirstElement();

			if (element instanceof PatientInboxElements) {
				PatientInboxElements patElement = (PatientInboxElements) element;
				Patient patient = (Patient) NoPoUtil.loadAsPersistentObject(patElement.getPatient());
				ElexisEventDispatcher.fireSelectionEvent(patient);
			}
		}
		return null;
	}
}
