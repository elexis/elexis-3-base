package at.medevit.elexis.outbox.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.outbox.ui.part.OutboxView;

public class ExpandAllCommand extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof OutboxView) {
			OutboxView view = (OutboxView) part;
			view.getTreeViewer().expandAll();
		}
		return null;
	}
}
