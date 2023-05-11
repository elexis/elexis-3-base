package at.medevit.elexis.inbox.ui.command;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.InboxModelServiceHolder;
import at.medevit.elexis.inbox.ui.InboxServiceHolder;
import at.medevit.elexis.inbox.ui.part.InboxView;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class IgnoreCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Als gelesen markieren",
				"Wollen Sie alle Inbox Einträge wirklich als gelesen markieren?")) {

			Shell activeshell = HandlerUtil.getActiveShell(event);
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(activeshell);
			try {
				progressDialog.run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						// check all open elements
						List<IInboxElement> openElements = InboxServiceHolder.get().getInboxElements(
								ContextServiceHolder.get().getActiveMandator().orElse(null), null,
								IInboxElementService.State.NEW);
						monitor.beginTask("Inbox Einträge als gelesen markieren", openElements.size());

						for (IInboxElement ie : openElements) {
							ie.setState(State.SEEN);
							InboxModelServiceHolder.get().save(ie);
							monitor.worked(1);
							if (monitor.isCanceled()) {
								break;
							}
						}

						Display.getDefault().asyncExec(() -> {
							// update view
							IWorkbenchPart part = HandlerUtil.getActivePart(event);
							if (part instanceof InboxView) {
								InboxView view = (InboxView) part;
								view.reload();
							}
						});
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				LoggerFactory.getLogger(getClass()).error("Error ignoring inbox entries", e);
			}
		}
		return null;
	}
}
