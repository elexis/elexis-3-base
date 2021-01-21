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
package at.medevit.elexis.ehc.ui.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.ehc.core.EhcCoreService;
import at.medevit.elexis.inbox.model.IInboxElementService;

@Component
public class ServiceComponent {
	private static EhcCoreService ehcService;
	private static IInboxElementService inboxService;
	
	public static IInboxElementService getInboxService(){
		return inboxService;
	}
	
	@Reference
	public synchronized void setEhcService(EhcCoreService service){
		ServiceComponent.ehcService = service;
	}
	
	@Reference
	public synchronized void setInboxService(IInboxElementService service){
		ServiceComponent.inboxService = service;
	}
	
	public static EhcCoreService getEhcService(){
		return ehcService;
	}
}
