package at.medevit.elexis.inbox.ui.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.InboxModelServiceHolder;
import at.medevit.elexis.inbox.ui.part.InboxView;

public class IgnoreCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof InboxView) {
			InboxView view = (InboxView) part;

			final List<IInboxElement> selectedElements = getSelectedInboxElements(view);
			if (selectedElements.isEmpty()) {
				MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Als gelesen markieren",
						"Bitte wählen Sie zuerst die zu markierenden Inbox Einträge aus.");
				return null;
			}

			if (MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Als gelesen markieren",
					"Wollen Sie die ausgewählten Inbox Einträge wirklich als gelesen markieren?")) {

				Shell activeshell = HandlerUtil.getActiveShell(event);
				ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(activeshell);
				try {
					progressDialog.run(true, true, new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							// only mark the selected elements
							monitor.beginTask("Inbox Einträge als gelesen markieren", selectedElements.size());

							for (IInboxElement ie : selectedElements) {
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
		}
		return null;
	}

	/**
	 * Collect the {@link IInboxElement}s from the current viewer selection.
	 *
	 * @param view
	 * @return the selected inbox elements (never <code>null</code>)
	 */
	private List<IInboxElement> getSelectedInboxElements(InboxView view) {
		List<IInboxElement> result = new ArrayList<>();
		if (view.getViewer() != null && view.getViewer().getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) view.getViewer().getSelection();
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				Object selected = it.next();
				if (selected instanceof IInboxElement) {
					IInboxElement element = (IInboxElement) selected;
					if (!result.contains(element)) {
						result.add(element);
					}
				}
			}
		}
		return result;
	}
}
