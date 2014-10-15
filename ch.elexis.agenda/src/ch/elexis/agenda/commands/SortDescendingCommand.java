package ch.elexis.agenda.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import ch.elexis.agenda.ui.TerminListeView;

public class SortDescendingCommand extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		IViewPart vp =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(TerminListeView.ID);
		TerminListeView terminListView = (TerminListeView) vp;
		terminListView.sort(SWT.DOWN);
		return null;
	}
	
}
