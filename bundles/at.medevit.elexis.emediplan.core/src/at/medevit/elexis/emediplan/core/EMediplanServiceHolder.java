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
	
	private static EventAdmin eventAdmin;

	public EMediplanServiceHolder(){
	}
	
	@Reference
	public void setEMediplanService(EMediplanService eMediplanService){
		EMediplanServiceHolder.eMediplanService = eMediplanService;
	}
	
	public void unsetEMediplanService(EMediplanService eMediplanService){
		EMediplanServiceHolder.eMediplanService = null;
	}
	
	@Reference
	public void setEventAdmin(EventAdmin eventAdmin){
		EMediplanServiceHolder.eventAdmin = eventAdmin;
	}

	public void unsetEventAdmin(EventAdmin eventAdmin){
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
