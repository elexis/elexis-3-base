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
package at.medevit.elexis.inbox.core.elements;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.ILabResult;

@Component(property = { EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.EVENT_CREATE,
		EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.PERSISTENCE_EVENT_COMPATIBILITY_CREATE }, immediate = true)
public class LabResultCreateEventHandler implements EventHandler {
	private ExecutorService executor;

	private boolean active = false;

	public LabResultCreateEventHandler() {
		CoreElements.setLabResultCreateEventHandler(this);
	}

	public void setActive(boolean value) {
		this.active = value;
		if (value) {
			if (executor == null) {
				executor = Executors.newCachedThreadPool();
			}
		} else {
			if (executor != null) {
				executor.shutdown();
				executor = null;
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		if (active) {
			if (event.getProperty(ElexisEventTopics.ECLIPSE_E4_DATA) instanceof ILabResult) {
				executor.execute(
						new AddLabInboxElement((ILabResult) event.getProperty(ElexisEventTopics.ECLIPSE_E4_DATA)));
			}
		}
	}
}
