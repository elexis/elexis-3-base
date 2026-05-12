package at.medevit.elexis.inbox.ui.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.InboxServiceHolder;
import at.medevit.elexis.inbox.ui.part.InboxView;
import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import jakarta.inject.Inject;

public class PatientAllSeenCommand extends AbstractHandler implements IHandler {

	@Inject
	private IInboxElementService inboxElementService;

	public PatientAllSeenCommand() {
		CoreUiUtil.injectServices(this);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof InboxView) {
			InboxView view = (InboxView) part;
			StructuredViewer viewer = view.getViewer();
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			if (selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof IInboxElement) {
				IInboxElement selectionElement = (IInboxElement) selection.getFirstElement();
				if (selectionElement.getPatient() != null) {
					List<IInboxElement> patientSeenElements = new ArrayList<>();
					if (view.getSelectedMandators() == null || view.getSelectedMandators().isEmpty()) {
						patientSeenElements.addAll(InboxServiceHolder.get().getInboxElements(
								ContextServiceHolder.get().getActiveMandator().orElse(null),
								selectionElement.getPatient(), IInboxElementService.State.NEW)) ;
					} else {
						for (IMandator mandator : view.getSelectedMandators()) {
							patientSeenElements.addAll(InboxServiceHolder.get().getInboxElements(mandator,
									selectionElement.getPatient(),
									IInboxElementService.State.NEW));
						}
					}
					patientSeenElements.forEach(ie -> {
						// use service will save and update ui element
						inboxElementService.changeInboxElementState(ie, State.SEEN);
					});
					// send event to update seen grouped elements
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, GroupedInboxElements.class);
				}
			}
		}
		return null;
	}
}
