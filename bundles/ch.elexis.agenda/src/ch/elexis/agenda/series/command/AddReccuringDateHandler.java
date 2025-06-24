package ch.elexis.agenda.series.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import ch.elexis.agenda.series.ui.SerienTerminDialog;

public class AddReccuringDateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		SerienTerminDialog std = new SerienTerminDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				null);
		std.open();
		return null;
	}
}
