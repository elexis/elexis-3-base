package ch.elexis.barcode.scanner.internal;

import java.util.HashMap;

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

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {
	private static Logger logger = LoggerFactory.getLogger(StartupHandler.class);
	
	@Override
	public void handleEvent(Event event){
		logger.info("APPLICATION STARTUP COMPLETE");
		ICommandService commandService =
			(ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		boolean settings = CoreHub.localCfg.get(PreferencePage.BarcodeScanner_AUTOSTART, false);
		if (settings) {
			UiDesk.getDisplay().syncExec(new Runnable() {
				public void run(){
					try {
						Command cmd = commandService.getCommand(ToggleHandler.COMMAND_ID);
						cmd.executeWithChecks(new ExecutionEvent(cmd, new HashMap<>(), null, null));
					} catch (Exception e) {
						logger.warn("cannot load barcode scanner on startup", e);
					}
				}
			});
		}
	}
}
