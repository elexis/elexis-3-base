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
package at.medevit.elexis.ehc.vacdoc.service;

import at.medevit.elexis.ehc.core.EhcCoreService;

public class EhcServiceComponent {
	private static EhcCoreService service;
	
	public synchronized void setService(EhcCoreService service){
		EhcServiceComponent.service = service;
	}
	
	public synchronized void unsetService(EhcCoreService service){
		if (EhcServiceComponent.service == service) {
			EhcServiceComponent.service = null;
		}
	}
	
	public static EhcCoreService getService(){
		return service;
	}
}
