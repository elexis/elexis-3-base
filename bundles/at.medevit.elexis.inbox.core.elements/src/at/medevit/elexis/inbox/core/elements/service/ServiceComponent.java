/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.core.elements.service;

import at.medevit.elexis.inbox.model.IInboxElementService;

public class ServiceComponent {
	private static IInboxElementService service;
	
	public static IInboxElementService getService(){
		return service;
	}
	
	// Method will be used by DS to set the quote service
	public synchronized void setService(IInboxElementService service){
		ServiceComponent.service = service;
	}
	
	// Method will be used by DS to unset the quote service
	public synchronized void unsetService(IInboxElementService service){
		if (ServiceComponent.service == service) {
			ServiceComponent.service = null;
		}
	}
}
