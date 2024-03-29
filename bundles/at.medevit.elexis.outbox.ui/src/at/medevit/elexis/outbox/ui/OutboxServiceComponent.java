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
package at.medevit.elexis.outbox.ui;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.outbox.model.IOutboxElementService;

@Component
public class OutboxServiceComponent {
	private static IOutboxElementService service;

	public static IOutboxElementService get() {
		return service;
	}

	@Reference
	public synchronized void setService(IOutboxElementService service) {
		OutboxServiceComponent.service = service;
	}

	public synchronized void unsetService(IOutboxElementService service) {
		if (OutboxServiceComponent.service == service) {
			OutboxServiceComponent.service = null;
		}
	}
}
