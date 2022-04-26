package ch.medshare.mediport;

import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.LoggerFactory;

import ch.medshare.mediport.gui.ShowErrorInvoices;
import ch.medshare.mediport.util.MediPortHelper;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		LoggerFactory.getLogger(getClass()).info("APPLICATION STARTUP COMPLETE");
		int count = MediPortHelper.getReturnFiles();
		if (count > 0) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// Verzeichnisse Ueberpruefen
					ShowErrorInvoices dialog = new ShowErrorInvoices(null, MediPortHelper.getCurrentClient());
					dialog.open();
				}
			});
		}
	}
}
