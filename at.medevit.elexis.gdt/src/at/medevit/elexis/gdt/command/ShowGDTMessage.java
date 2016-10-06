/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.gdt.data.GDTProtokoll;
import at.medevit.elexis.gdt.ui.SimpleTextViewer;

public class ShowGDTMessage extends AbstractHandler {
	
	public static final String ID = "at.medevit.elexis.gdt.command.showGDTMessage";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			GDTProtokoll gdtpEntry = (GDTProtokoll) strucSelection.getFirstElement();
			
			StringBuilder title = new StringBuilder();
			title.append(gdtpEntry.getEntryRelatedPatient().getLabel());
			title.append(" - ");
			title.append(gdtpEntry.getMessageDirection()+" "+gdtpEntry.getMessageType());
			
			new SimpleTextViewer(title.toString(), gdtpEntry.getMessage());
			
		}
		return null;
	}
	
}
