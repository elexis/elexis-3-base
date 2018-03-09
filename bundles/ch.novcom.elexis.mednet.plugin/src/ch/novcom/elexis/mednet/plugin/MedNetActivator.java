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

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Activator for the MedNet plugin
 */
public class MedNetActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "ch.novcom.elexis.mednet.plugin";
	private static MedNetActivator plugin;
	
	/**
	 * Standard Constructor
	 */
	public MedNetActivator(){
		plugin = this;
	}
	
	/**
	 * Return the active Instance
	 * 
	 * @return MedNetActivator instance
	 */
	public static MedNetActivator getInstance(){
		return plugin;
	}
}
