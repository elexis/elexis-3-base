package ch.elexis.omnivore.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.ui.util.UiUtils;

public class OpenDocumentHandler extends AbstractHandler {

	public static final String COMMAND_ID = "ch.elexis.omnivore.ui.command.open"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) {
		if (HandlerUtil.getCurrentSelection(event) instanceof IStructuredSelection selection
				&& selection.getFirstElement() instanceof IDocumentHandle handle && !handle.isCategory()) {
			UiUtils.open(handle);
		}
		return null;
	}
}
