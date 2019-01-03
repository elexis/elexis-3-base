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

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import at.medevit.elexis.inbox.model.IInboxElementService;

public class ServiceComponent {
	
	private static IInboxElementService service;
	
	private static ServiceReference<IInboxElementService> serviceRef;
	
	public synchronized static IInboxElementService getService(){
		if(service == null) {
			BundleContext context =
				FrameworkUtil.getBundle(IInboxElementService.class).getBundleContext();
			if (context != null) {
				serviceRef = context.getServiceReference(IInboxElementService.class);
				if (serviceRef != null) {
					service = context.getService(serviceRef);
				}
			}
		}
		return service;
	}
}
