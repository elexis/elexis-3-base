package at.medevit.elexis.inbox.ui.command;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.dialog.MandantSelectorDialog;
import at.medevit.elexis.inbox.ui.part.InboxView;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementContentProvider;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Mandant;

public class ChangeMandantCommand extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		MandantSelectorDialog msDialog = new MandantSelectorDialog(UiDesk.getTopShell());
		if (msDialog.open() == TitleAreaDialog.OK) {
			Mandant mandant = msDialog.getSelectedMandant();
			
			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			if (part instanceof InboxView) {
				InboxView view = (InboxView) part;
				CheckboxTreeViewer viewer = view.getCheckboxTreeViewer();
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				
				if (selection != null && !selection.isEmpty()) {
					List<?> selectionList = selection.toList();
					for (Object selObj : selectionList) {
						if (selObj instanceof InboxElement) {
							InboxElement inboxElement = (InboxElement) selObj;
							inboxElement.setMandant(mandant);
							refreshView(viewer, inboxElement);
						} else if (selObj instanceof PatientInboxElements) {
							PatientInboxElements patInboxElements = (PatientInboxElements) selObj;
							for (InboxElement inboxElement : patInboxElements.getElements()) {
								inboxElement.setMandant(mandant);
							}
							refreshView(viewer, patInboxElements);
						}
					}
				}
			}
		}
		return null;
	}
	
	private void refreshView(CheckboxTreeViewer viewer, PatientInboxElements patInboxElements){
		InboxElementContentProvider contentProvider =
			(InboxElementContentProvider) viewer.getContentProvider();
		contentProvider.refreshElement(patInboxElements);
		viewer.refresh(false);
	}
	
	private void refreshView(CheckboxTreeViewer viewer, InboxElement inboxElement){
		InboxElementContentProvider contentProvider =
			(InboxElementContentProvider) viewer.getContentProvider();
		contentProvider.refreshElement(inboxElement);
		viewer.refresh(false);
	}
}
