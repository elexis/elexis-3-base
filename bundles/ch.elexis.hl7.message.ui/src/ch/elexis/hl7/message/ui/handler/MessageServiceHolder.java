package ch.elexis.hl7.message.ui.handler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.hl7.message.core.IHL7MessageService;

@Component
public class MessageServiceHolder {
	
	private static IHL7MessageService service;
	
	@Reference
	public void setHL7MessageService(IHL7MessageService service){
		MessageServiceHolder.service = service;
	}
	
	/**
	 * Get the {@link IHL7MessageService} implementation.
	 * 
	 * @return
	 */
	public static IHL7MessageService getService(){
		return service;
	}
}
