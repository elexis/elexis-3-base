/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.gruber.elexis.mythic22.command;

import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.State;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.menus.IMenuStateIds;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.UIElement;

import at.gruber.elexis.mythic22.Messages;
import at.gruber.elexis.mythic22.netlistener.NetListener;
import at.gruber.elexis.mythic22.ui.Preferences;
import at.medevit.elexis.mythic22.EarlyStartup;
import ch.elexis.core.data.activator.CoreHub;
import de.ralfebert.rcputils.handler.ToggleHandler;

/**
 * The command, which is used in the Lab toolbar to start and stop the listener for incoming
 * mythic22 results
 * 
 * @author Christian, modified by M. Descher to use a ToggleHandler
 * 
 */
public class ServerControl extends ToggleHandler {
	
	public static final String ID = "at.gruber.elexis.mythic22.ui.ServerControl";
	
	private NetListener m_netlistener;
	private boolean earlyStartup;
	
	@Override
	protected void executeToggle(ExecutionEvent event, boolean checked){
		
		earlyStartup = Boolean.parseBoolean(event.getParameter(EarlyStartup.PARAM_EARLYSTARTUP));
		
		if (checked) {
			
			if (m_netlistener == null)
				m_netlistener =
					new NetListener(Integer.parseInt(CoreHub.localCfg.get(Preferences.CFG_PORT,
						"1200")));
			m_netlistener.startContinousRead();
			
			if (!earlyStartup)
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell(), Messages.ServerControl_1, Messages.ServerControl_3);
			
		} else {
			if (m_netlistener != null) {
				m_netlistener.requestThreadToStop();
				
				if (!earlyStartup)
					MessageDialog.openInformation(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), Messages.ServerControl_1,
						Messages.ServerControl_5);
			}
		}
	}
	
	@Override
	public void updateElement(final UIElement element, Map parameters){
		ICommandService commandService =
			(ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand(ID);
		final State state = command.getState(IMenuStateIds.STYLE);
		if (state != null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run(){
					element.setChecked((Boolean) state.getValue());
				}
			});
			
		}
		
	}
	
}
