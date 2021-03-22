package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.Optional;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.chromium.Browser;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.rcprap.SingleSourceUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class ContextMenuFunction extends AbstractBrowserFunction {
	
	private ISelectionProvider selectionProvider;
	private MPart part;
	
	public ContextMenuFunction(MPart part, Browser browser, String name){
		super(browser, name);
		this.part = part;
	}
	
	@Override
	public Object function(Object[] arguments){
		if (arguments.length == 4) {
			LocalDateTime date = getDateTimeArg(arguments[2]);
			String resource = (String) arguments[3];
			
			Optional<SideBarComposite> activeSideBar = getActiveSideBar(part);
			activeSideBar.ifPresent(sideBar -> {
				sideBar.setMoveInformation(date, resource);
				
				if(SingleSourceUtil.isRap()) {
					// in Rap Browser Mouse Location is not connected to SWT
					// hence the menu does not open at the correct location (but would on the last entry
					// point to the SWT browser widget)
					// see https://www.eclipse.org/forums/index.php?t=msg&th=1100113&goto=1810544&#msg_1810544
					// relative if sidebar is opened
					Double argX = (Double) arguments[0];
					Double argY = (Double) arguments[1];
					getBrowser().getMenu().setLocation(argX.intValue()+100, argY.intValue());
				}
				getBrowser().getMenu().setVisible(true);
			});
		} else if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get()
				.load((String) arguments[0], IAppointment.class).orElse(null);
			if (selectionProvider != null) {
				selectionProvider.setSelection(new StructuredSelection(termin));
			}
			// setting menu visibility needs to be executed in separate
			// trigger rebuild of menu with new selection by setting visible false first
			Display.getDefault().asyncExec(() -> {
				getBrowser().getMenu().setVisible(false);
				
			});
			Display.getDefault().asyncExec(() -> {
				getBrowser().getMenu().setVisible(true);
			});
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
