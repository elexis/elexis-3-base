package at.medevit.elexis.inbox.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.ui.part.InboxView;

public class AutoActivePatientHandler extends AbstractHandler implements IHandler {
	
	public static final String CMD_ID = "at.medevit.elexis.inbox.ui.autoSelectPatient";
	public static final String STATE_ID = "org.eclipse.ui.commands.toggleState";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		boolean state = !HandlerUtil.toggleCommandState(event.getCommand());
		
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof InboxView) {
			((InboxView) part).setAutoSelectPatientState(state);
		}
		return null;
	}
	
}
