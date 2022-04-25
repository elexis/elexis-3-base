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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	private ExecutorService executor;

	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger logger = LoggerFactory.getLogger(StartupHandler.class);

	public StartupHandler() {
		executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void handleEvent(Event event) {
		logger.info("APPLICATION STARTUP COMPLETE");
		String logPrefix = "earlyStartup() - ";//$NON-NLS-1$
		// do not block event handling, execute in different thread
		executor.execute(() -> {
			try {
				new FormWatcher().processEvents();
			} catch (IOException e) {
				logger.error(logPrefix + "IOException initializing FormWatcher", e);//$NON-NLS-1$
			}
		});
		executor.shutdown();
	}
}
