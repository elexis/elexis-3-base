/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package ch.elexis.labor.viollier.v2;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Activator für das Viollier Labor Importer Plug-In
 */
public class ViollierActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "ch.elexis.laborimport.viollier.v2"; //$NON-NLS-1$
	public static String TEXT_ENCODING = "ISO-8859-1"; //$NON-NLS-1$
	private static ViollierActivator plugin;

	/**
	 * Standard Constructor
	 */
	public ViollierActivator() {
		plugin = this;
	}

	/**
	 * Gibt die aktive Instanz zurück
	 *
	 * @return ViollierActivator Instanz
	 */
	public static ViollierActivator getInstance() {
		return plugin;
	}
}
