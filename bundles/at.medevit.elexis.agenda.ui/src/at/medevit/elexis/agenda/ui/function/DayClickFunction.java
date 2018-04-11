package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.Browser;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.dialogs.TerminDialog;
import ch.elexis.dialogs.TerminDialog.CollisionErrorLevel;
import ch.rgw.tools.TimeTool;

public class DayClickFunction extends AbstractBrowserFunction {
	
	private List<String> selectedResources;
	
	public DayClickFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 1) {
			LocalDateTime date = getDateTimeArg(arguments[0]);
			if (selectedResources != null && !selectedResources.isEmpty()) {
				TerminDialog dlg = new TerminDialog(new TimeTool(date), selectedResources.get(0),
					ElexisEventDispatcher.getSelectedPatient());
				dlg.setCollisionErrorLevel(CollisionErrorLevel.WARNING);
				dlg.open();
			} else {
				MessageDialog.openInformation(getBrowser().getShell(), "Info",
					"Keine Resource selektiert.");
			}
		} else if (arguments.length == 2) {
			LocalDateTime date = getDateTimeArg(arguments[0]);
			String resource = (String) arguments[1];
			TerminDialog dlg = new TerminDialog(new TimeTool(date), resource,
				ElexisEventDispatcher.getSelectedPatient());
			dlg.setCollisionErrorLevel(CollisionErrorLevel.WARNING);
			dlg.open();
		}
		return null;
	}
	
	public void setSelectedResources(List<String> selectedResources){
		this.selectedResources = selectedResources;
	}
}