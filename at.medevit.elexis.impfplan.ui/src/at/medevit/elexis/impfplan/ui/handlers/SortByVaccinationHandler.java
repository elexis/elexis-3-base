package at.medevit.elexis.impfplan.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.impfplan.ui.VaccinationView;

/**
 * Sorts the vaccinations based on their name
 * 
 * @author Lucia
 *
 */
public class SortByVaccinationHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		boolean toggled = HandlerUtil.toggleCommandState(event.getCommand());
		VaccinationView vaccView = (VaccinationView) HandlerUtil.getActivePart(event);
		
		if (toggled) {
			vaccView.setSortByVaccinationName(false);
		} else {
			vaccView.setSortByVaccinationName(true);
		}
		
		return null;
	}
	
}
