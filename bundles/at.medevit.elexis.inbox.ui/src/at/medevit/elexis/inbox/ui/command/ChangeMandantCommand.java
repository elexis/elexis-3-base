package at.medevit.elexis.inbox.ui.command;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.InboxModelServiceHolder;
import at.medevit.elexis.inbox.ui.part.InboxView;
import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementContentProvider;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.dialog.MandantSelectorDialog;

public class ChangeMandantCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MandantSelectorDialog msDialog = new MandantSelectorDialog(UiDesk.getTopShell(), false);
		if (msDialog.open() == TitleAreaDialog.OK) {
			IMandator mandator = msDialog.getSelectedMandator();

			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			if (part instanceof InboxView) {
				InboxView view = (InboxView) part;
				StructuredViewer viewer = view.getViewer();
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

				if (selection != null && !selection.isEmpty()) {
					List<?> selectionList = selection.toList();
					for (Object selObj : selectionList) {
						if (selObj instanceof IInboxElement) {
							IInboxElement inboxElement = (IInboxElement) selObj;
							inboxElement.setMandator(mandator);
							if (!(inboxElement instanceof GroupedInboxElements)) {
								InboxModelServiceHolder.get().save(inboxElement);
							}
							refreshView(viewer, inboxElement);
						}
					}
				}
			}
		}
		return null;
	}

	private void refreshView(StructuredViewer viewer, IInboxElement inboxElement) {
		InboxElementContentProvider contentProvider = (InboxElementContentProvider) viewer.getContentProvider();
		contentProvider.refreshElement(inboxElement);
		viewer.refresh(false);
	}
}
