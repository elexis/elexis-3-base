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
package at.medevit.elexis.gdt.handler;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import at.medevit.elexis.gdt.Activator;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.data.GDTProtokoll;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht;
import at.medevit.elexis.gdt.tools.GDTFileHelper;
import at.medevit.elexis.gdt.ui.GDTProtokollView;
import ch.elexis.core.ui.util.Log;

public class GDTOutputHandler {
	
	private static Log logger = Log.get(GDTOutputHandler.class.getName());
	
	public static void handleOutput(GDTSatzNachricht gdtSatzNachricht, IGDTCommunicationPartner cp){
		int connectionType = cp.getConnectionType();
		
		switch (connectionType) {
		case SystemConstants.FILE_COMMUNICATION:
			boolean success = GDTFileHelper.writeGDTSatzNachricht(gdtSatzNachricht, cp);
			if (success) {
				GDTProtokoll.addEntry(GDTProtokoll.MESSAGE_DIRECTION_OUT, cp, gdtSatzNachricht);
			} else {
				String message =
					"Fehler beim Schreiben der GDT Satznachricht "
						+ gdtSatzNachricht.getValue(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION)
						+ " auf " + cp.getLabel();
				Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
				logger.log(message, Log.WARNINGS);
			}
			
			// Update the protokoll view
			final IViewPart protokoll =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(GDTProtokollView.ID);
			Display display = PlatformUI.getWorkbench().getDisplay();
			display.asyncExec(new Runnable() {
				@Override
				public void run(){
					if (protokoll != null)
						protokoll.setFocus();
				}
			});
			
			// Call the external program to care for the output (if applicable)
			String handlerProgram = cp.getExternalHandlerProgram();
			if (handlerProgram != null) {
				CommandLine cmdLine = CommandLine.parse(handlerProgram);
				try {
					DefaultExecutor executor = new DefaultExecutor();
					executor.setExitValues(null); // Ignore the exit value
					int exitValue = executor.execute(cmdLine);
					logger.log("Return value of " + cmdLine + ": " + exitValue, Log.DEBUGMSG);
				} catch (ExecuteException e) {
					String message = "Fehler beim Ausführen von " + cmdLine;
					Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
					logger.log(e, message, Log.ERRORS);
				} catch (IOException e) {
					String message = "Fehler beim Ausführen von " + cmdLine;
					Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
					logger.log(e, message, Log.ERRORS);
				}
			}
			break;
		case SystemConstants.SERIAL_COMMUNICATION:
			// TODO Serial output implementation
			break;
		default:
			break;
		}
	}
}
