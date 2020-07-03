/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/

package ch.novcom.elexis.mednet.plugin;

import java.io.IOException;

import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class used to initialize the FormWatcher
 */
@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {
	
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(StartupHandler.class);
	
	@Override
	public void handleEvent(Event event){
		String logPrefix = "earlyStartup() - ";//$NON-NLS-1$
		
		try {
			new FormWatcher().processEvents();
		} catch (IOException e) {
			LOGGER.error(logPrefix+"IOException initializing FormWatcher",e);//$NON-NLS-1$
		}
	}
}
