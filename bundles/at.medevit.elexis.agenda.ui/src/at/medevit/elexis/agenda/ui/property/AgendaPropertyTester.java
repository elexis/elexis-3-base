package at.medevit.elexis.agenda.ui.property;

import java.util.Optional;

import org.eclipse.core.expressions.PropertyTester;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.composite.SideBarComposite.MoveInformation;
import at.medevit.elexis.agenda.ui.function.AbstractBrowserFunction;

public class AgendaPropertyTester extends PropertyTester {
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("moveAvailable".equals(property)) { //$NON-NLS-1$
			try {
				Optional<SideBarComposite> activeSideBar =
					AbstractBrowserFunction.getActiveSideBar();
				if (activeSideBar.isPresent()) {
					Optional<MoveInformation> moveInformation =
						activeSideBar.get().getMoveInformation();
					if (moveInformation.isPresent()) {
						return !moveInformation.get().getMoveablePeriods().isEmpty();
					}
				}
			} catch (IllegalStateException ise) {
				// do nothing, false is returned
			}
		}
		return false;
	}
	
}
