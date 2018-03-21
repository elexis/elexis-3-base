package ch.elexis.barcode.scanner.internal;

import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;

public class BarcodeScannerStartup implements IStartup {
	private static Logger logger = LoggerFactory.getLogger(BarcodeScannerStartup.class);
	
	@Override
	public void earlyStartup(){
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
