package at.medevit.elexis.emediplan.core;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

@Component
public class EMediplanServiceHolder {
	
	private static EMediplanService eMediplanService;
	
	@Reference
	private static EventAdmin eventAdmin;

	public EMediplanServiceHolder(){
	}
	
	@Reference
	public void setReference(EMediplanService eMediplanService){
		EMediplanServiceHolder.eMediplanService = eMediplanService;
	}
	
	public void unsetReference(EMediplanService eMediplanService){
		EMediplanServiceHolder.eMediplanService = null;
	}
	
	@Reference
	public void setReference(EventAdmin eventAdmin) {
		EMediplanServiceHolder.eventAdmin = eventAdmin;
	}

	public void unsetReference(EventAdmin eventAdmin) {
		EMediplanServiceHolder.eventAdmin = null;
	}

	public static EMediplanService getService(){
		return eMediplanService;
	}

	public static void postEvent(String topic, Object object) {
		if (eventAdmin != null) {
			Map<String, Object> properites = new HashMap<>();
			properites.put("org.eclipse.e4.data", object);
			Event event = new Event(topic, properites);
			eventAdmin.postEvent(event);
		} else {
			throw new IllegalStateException("No EventAdmin available");
		}
	}
}
