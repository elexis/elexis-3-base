package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.Optional;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.browser.Browser;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class ContextMenuFunction extends AbstractBrowserFunction {
	
	private ISelectionProvider selectionProvider;
	private MPart part;
	
	public ContextMenuFunction(MPart part, Browser browser, String name){
		super(browser, name);
		this.part = part;
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 2) {
			LocalDateTime date = getDateTimeArg(arguments[0]);
			String resource = (String) arguments[1];
			
			Optional<SideBarComposite> activeSideBar = getActiveSideBar(part);
			activeSideBar.ifPresent(sideBar -> {
				sideBar.setMoveInformation(date, resource);
				getBrowser().getMenu().setVisible(true);
			});
		} else if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get()
				.load((String) arguments[0], IAppointment.class).orElse(null);
			if (selectionProvider != null) {
				selectionProvider.setSelection(new StructuredSelection(termin));
			}
			getBrowser().getMenu().setVisible(true);
		} else if (arguments.length == 0) {
			if (selectionProvider != null) {
				selectionProvider.setSelection(StructuredSelection.EMPTY);
			}
		}
		return null;
	}
	
	public void setSelectionProvider(ISelectionProvider selectionProvider){
		this.selectionProvider = selectionProvider;
	}
}
