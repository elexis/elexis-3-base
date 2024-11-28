package at.medevit.elexis.inbox.ui.command;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.ui.part.InboxView;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.dialog.MandantSelectorDialog;

public class SelectMandatorCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof InboxView) {
			InboxView view = (InboxView) part;
			MandantSelectorDialog msDialog = new MandantSelectorDialog(UiDesk.getTopShell(), true);
			if (msDialog.open() == MandantSelectorDialog.OK) {
				List<IMandator> selectedMandators = msDialog.getSelectedMandators();
				view.setSelectedMandators(selectedMandators);
			} else {
				view.setSelectedMandators(Collections.emptyList());
			}
		}
		return null;
	}
}
