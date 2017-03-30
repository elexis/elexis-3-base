package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.Browser;

public class DayClickFunction extends AbstractBrowserFunction {
	
	public DayClickFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 1) {
			LocalDateTime date = getDateTimeArg(arguments[0]);
			MessageDialog.openInformation(getBrowser().getShell(), "Day click",
				date.toString());
		} else {
			throw new IllegalArgumentException("Unexpected arguments");
		}
		return null;
	}
}