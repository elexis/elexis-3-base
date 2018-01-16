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

import org.eclipse.ui.IStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class used to initialize the FormWatcher
 */
public class FormWatcherStartup implements IStartup {

	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(FormWatcherStartup.class.getName());
	
	@Override
	public void earlyStartup(){
		String logPrefix = "earlyStartup() - ";//$NON-NLS-1$
		
		try {
			new FormWatcher().processEvents();
		} catch (IOException e) {
			LOGGER.error(logPrefix+"IOException initializing FormWatcher",e);//$NON-NLS-1$
		}
	}
}
