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

import ch.elexis.core.services.IModelService;

@Component
public class InboxModelServiceHolder {
	private static IModelService service;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=at.medevit.elexis.inbox.model)")
	public void setService(IModelService service){
		InboxModelServiceHolder.service = service;
	}
	
	public static IModelService get(){
		return service;
	}
}
