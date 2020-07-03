/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.mythic22;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gruber.elexis.mythic22.command.ServerControl;
import at.gruber.elexis.mythic22.ui.Preferences;
import ch.elexis.core.data.activator.CoreHub;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {
	
	public static final String PARAM_EARLYSTARTUP = "earlyStartup";
	private static Logger logger = LoggerFactory.getLogger(StartupHandler.class);
	
	@Override
	public void handleEvent(Event event){
		boolean autostart = CoreHub.localCfg.get(Preferences.CFG_AUTOSTART, false);
		
		if (autostart) {
			try {
				ICommandService commandService =
					(ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class); // NPE
				
				Command cmd = commandService.getCommand(ServerControl.ID);
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(PARAM_EARLYSTARTUP, "true");
				
				ExecutionEvent ee = new ExecutionEvent(cmd, map, null, null);
				cmd.executeWithChecks(ee);
			} catch (Exception exception) {
				logger.error("Error on autostart", exception);
			}
		}
	}
}
