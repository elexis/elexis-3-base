package at.medevit.elexis.agenda.ui.function;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.view.AgendaView;

public class SwitchFunction extends BrowserFunction {
	
	public SwitchFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 1) {
			try {
				String to = (String) arguments[0];
				IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView("at.medevit.elexis.agenda.ui.view.agenda");
				if (view instanceof AgendaView) {
					((AgendaView) view).setTopControl(to);
				}
			} catch (PartInitException e) {
				LoggerFactory.getLogger(getClass()).error("Error switching agenda", e);
			}
		}
		return null;
	}
}
