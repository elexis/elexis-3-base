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

import ch.elexis.core.tasks.model.ITaskService;

@Component
public class TaskServiceComponent {
	private static ITaskService service;

	public static ITaskService get() {
		return service;
	}

	@Reference
	public synchronized void setService(ITaskService service) {
		TaskServiceComponent.service = service;
	}

	public synchronized void unsetService(ITaskService service) {
		if (TaskServiceComponent.service == service) {
			TaskServiceComponent.service = null;
		}
	}
}
