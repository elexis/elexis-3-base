package at.medevit.elexis.outbox.ui.command;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.outbox.model.OutboxElementType;
import at.medevit.elexis.outbox.ui.OutboxServiceComponent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.documents.DocumentStore;
import ch.elexis.core.model.IDocument;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class CreateOutboxElementHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Patient elexisPatient = ElexisEventDispatcher.getSelectedPatient();
		Mandant elexisMandant = ElexisEventDispatcher.getSelectedMandator();
		
		if (elexisPatient != null && elexisMandant != null && elexisPatient.exists()
			&& elexisMandant.exists()) {
			Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection instanceof StructuredSelection
				&& !((StructuredSelection) selection).isEmpty()) {
				List<?> selected = ((StructuredSelection) selection).toList();
				
				int size = 0;
				for (Object documentToExport : selected) {
					// TODO add handler for other types
					if (documentToExport instanceof IDocument) {
						IDocument iDocument = (IDocument) documentToExport;
						createOutboxElement(elexisPatient, elexisMandant, iDocument);
						size++;
					}
				}
				if (size > 0) {
					MessageDialog.openInformation(shell, "Dokumente",
						size == 1 ? "Das Dokument wurde erfolgreich in die Outbox abgelegt."
								: size + " Dokumente wurden erfolgreich in die Outbox abgelegt.");
				}
			}
		}
		return null;
	}
	
	private boolean createOutboxElement(Patient patient, Mandant mandant, IDocument document){
		OutboxServiceComponent.getService().createOutboxElement(patient, mandant,
			OutboxElementType.DOC.getPrefix() + document.getId()
				+ DocumentStore.ID_WITH_STOREID_SPLIT + document.getStoreId());
		return true;
	}
}
