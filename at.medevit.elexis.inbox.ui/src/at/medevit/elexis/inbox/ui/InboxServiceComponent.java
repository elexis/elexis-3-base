/*******************************************************************************
 * Copyright (c) 2013, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.elexis.inbox.ui;

import at.medevit.elexis.inbox.model.IInboxElementService;

public class InboxServiceComponent {
	private static IInboxElementService service;
	
	public static IInboxElementService getService(){
		return service;
	}
	
	// Method will be used by DS to set the quote service
	public synchronized void setService(IInboxElementService service){
		InboxServiceComponent.service = service;
	}
	
	// Method will be used by DS to unset the quote service
	public synchronized void unsetService(IInboxElementService service){
		if (InboxServiceComponent.service == service) {
			InboxServiceComponent.service = null;
		}
	}
}
