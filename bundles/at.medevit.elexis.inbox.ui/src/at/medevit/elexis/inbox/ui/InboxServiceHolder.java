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
package at.medevit.elexis.inbox.ui;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.inbox.model.IInboxElementService;
import ch.elexis.core.services.IModelService;

@Component
public class InboxServiceHolder {

	private static IModelService modelService;

	private static IInboxElementService service;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=at.medevit.elexis.inbox.model)")
	public void setModelService(IModelService modelService) {
		InboxServiceHolder.modelService = modelService;
	}

	@Reference
	public void setService(IInboxElementService service) {
		InboxServiceHolder.service = service;
	}

	public static IInboxElementService get() {
		return service;
	}

	public static IModelService getModelService() {
		return modelService;
	}
}
