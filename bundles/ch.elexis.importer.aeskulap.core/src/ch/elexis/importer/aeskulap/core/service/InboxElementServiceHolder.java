package ch.elexis.importer.aeskulap.core.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import at.medevit.elexis.inbox.model.IInboxElementService;

@Component
public class InboxElementServiceHolder {
	private static IInboxElementService inboxElementService;
	
	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	public void setDocumentStore(IInboxElementService inboxElementService){
		InboxElementServiceHolder.inboxElementService = inboxElementService;
	}
	
	public static boolean isSet(){
		return inboxElementService != null;
	}
	
	public static IInboxElementService get(){
		if (inboxElementService == null) {
			throw new IllegalStateException("No IInboxElementService implementation available");
		}
		return inboxElementService;
	}
}
