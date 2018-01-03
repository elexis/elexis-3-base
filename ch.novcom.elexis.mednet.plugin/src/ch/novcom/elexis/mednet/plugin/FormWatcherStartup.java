/*******************************************************************************
 * Copyright (c) 2017 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht
 *******************************************************************************/

package ch.novcom.elexis.mednet.plugin;

import java.io.IOException;

import org.eclipse.ui.IStartup;

public class FormWatcherStartup implements IStartup {
	
	@Override
	public void earlyStartup(){
		
		try {
			new FormWatcher().processEvents();
		} catch (IOException e) {
			MedNet.getLogger().error("earlyStartup() IOException initializing FormWatcher",e);
		}
	}
}
