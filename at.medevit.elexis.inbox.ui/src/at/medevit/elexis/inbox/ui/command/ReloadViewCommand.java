/*******************************************************************************
 * Copyright (c) 2013, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.elexis.inbox.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.inbox.ui.part.InboxView;

public class ReloadViewCommand extends AbstractHandler implements IHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException{
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof InboxView) {
			InboxView view = (InboxView) part;
			view.reload();
		}
		return null;
	}
}
