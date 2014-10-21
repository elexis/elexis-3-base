package at.medevit.elexis.impfplan.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.impfplan.ui.VaccinationView;
import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;

public class SortVaccinationsHandler extends AbstractHandler {
	
	// descending = false;
	// ascending = true;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		boolean toggled = HandlerUtil.toggleCommandState(event.getCommand());
		CoreHub.userCfg.set(PreferencePage.VAC_SORT_ORDER, toggled);
		
		VaccinationView vaccView = (VaccinationView) HandlerUtil.getActivePart(event);
		vaccView.updateUi(true); // as query needs to be ordered
		return null;
	}
	
}
