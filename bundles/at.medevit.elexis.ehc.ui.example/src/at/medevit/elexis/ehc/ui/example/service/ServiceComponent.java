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
package at.medevit.elexis.ehc.ui.example.service;

import at.medevit.elexis.ehc.core.EhcCoreService;

public class ServiceComponent {
	private static EhcCoreService service;

	public synchronized void setService(EhcCoreService service) {
		ServiceComponent.service = service;
	}

	public synchronized void unsetService(EhcCoreService service) {
		if (ServiceComponent.service == service) {
			ServiceComponent.service = null;
		}
	}

	public static EhcCoreService getService() {
		return service;
	}
}
