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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.gdt.data.GDTProtokoll;
import at.medevit.elexis.gdt.handler.GDTOutputHandler;
import at.medevit.elexis.gdt.handler.response.GDTResponseIn6310Out6311;
import at.medevit.elexis.gdt.interfaces.HandlerProgramType;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6310;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6311;
import at.medevit.elexis.gdt.tools.GDTCommPartnerCollector;

public class DatenEinerUntersuchungAnzeigen extends AbstractHandler {
	
	private Logger log = LoggerFactory.getLogger(DatenEinerUntersuchungAnzeigen.class);
	
	public static final String ID = "at.medevit.elexis.gdt.command.DatenEinerUntersuchungAnzeigen";
	public static final String PARAM_ID =
		"at.medevit.elexis.gdt.command.DatenEinerUntersuchungAnzeigen.gdtProtokollSource";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		GDTProtokoll gdtpEntry = null;
		
		String gdtProtokollSource = event.getParameter(PARAM_ID);
		
		if (gdtProtokollSource == null) {
			ISelection selection =
				HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				gdtpEntry = (GDTProtokoll) strucSelection.getFirstElement();
			}
		} else {
			gdtpEntry = GDTProtokoll.load(gdtProtokollSource);
		}
		
		if (gdtpEntry == null) {
			log.error("gdtpEntry is null");
			return null;
		}
		
		String[] message = gdtpEntry.getMessage().split("\r\n");
		GDTSatzNachricht6310 incoming = GDTSatzNachricht6310.createfromStringArray(message);
		GDTSatzNachricht6311 outgoing = GDTResponseIn6310Out6311.createResponse(incoming);
		IGDTCommunicationPartner cp = GDTCommPartnerCollector.identifyCommunicationPartnerByLabel(gdtpEntry.getGegenstelle());
		if (cp != null) {
			GDTOutputHandler.handleOutput(outgoing, cp, HandlerProgramType.VIEWER);
		} else {
			log.error("No communication partner found for [" + gdtpEntry.getGegenstelle() + "]");
		}
		
		return null;
	}
	
}
