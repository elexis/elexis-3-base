package at.medevit.elexis.agenda.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.view.AgendaView;

public class SetTopControlHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String controlId =
			event.getParameter("at.medevit.elexis.agenda.ui.command.parameter.controlId");
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.showView("at.medevit.elexis.agenda.ui.view.agenda");
			if (view instanceof AgendaView) {
				((AgendaView) view).setTopControl(controlId);
			}
		} catch (PartInitException e) {
			LoggerFactory.getLogger(getClass()).error("Error switching top control", e);
		}
		return null;
	}
}
