package ch.elexis.base.messages;

import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {
	MsgHeartListener heartListener;

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.elexis.base.messages"; //$NON-NLS-1$

	@Override
	public void handleEvent(Event event) {
		LoggerFactory.getLogger(getClass()).info("APPLICATION STARTUP COMPLETE"); //$NON-NLS-1$
		heartListener = new MsgHeartListener();
		CoreHub.heart.addListener(heartListener);
	}
}
