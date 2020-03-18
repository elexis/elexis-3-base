package at.medevit.elexis.agenda.ui.property;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.composite.SideBarComposite.MoveInformation;
import at.medevit.elexis.agenda.ui.function.AbstractBrowserFunction;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class AgendaPropertyTester extends PropertyTester {
	
	@Inject
	private EPartService partService;
	
	public AgendaPropertyTester(){
		CoreUiUtil.injectServicesWithContext(this);
	}
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("moveAvailable".equals(property)) { //$NON-NLS-1$
			try {
				Optional<SideBarComposite> activeSideBar =
					AbstractBrowserFunction.getActiveSideBar(partService.getActivePart());
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
