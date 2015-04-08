package at.medevit.elexis.inbox.ui.command;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.InboxServiceComponent;
import at.medevit.elexis.inbox.ui.part.InboxView;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;

public class IgnoreCommand extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		// check all open elements
		List<InboxElement> openElements =
			InboxServiceComponent.getService().getInboxElements( ElexisEventDispatcher.getSelectedMandator(), null,
				IInboxElementService.State.NEW);
		
		for (InboxElement ie : openElements) {
			ie.setState(State.SEEN);
		}
		
		// update view
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof InboxView) {
			InboxView view = (InboxView) part;
			view.reload();
		}
		return null;
	}
}
