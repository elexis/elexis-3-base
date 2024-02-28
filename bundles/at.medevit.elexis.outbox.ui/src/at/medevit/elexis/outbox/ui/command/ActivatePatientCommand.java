package at.medevit.elexis.outbox.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.outbox.ui.part.OutboxView;
import at.medevit.elexis.outbox.ui.part.model.PatientOutboxElements;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class ActivatePatientCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		if (part instanceof OutboxView) {
			OutboxView view = (OutboxView) part;
			IStructuredSelection sel = (IStructuredSelection) view.getTreeViewer().getSelection();
			Object element = sel.getFirstElement();

			if (element instanceof PatientOutboxElements) {
				PatientOutboxElements patElement = (PatientOutboxElements) element;
				ContextServiceHolder.get().getRootContext().setNamed(IContextService.SELECTIONFALLBACK,
						patElement.getPatient());
			}
		}
		return null;
	}
}
