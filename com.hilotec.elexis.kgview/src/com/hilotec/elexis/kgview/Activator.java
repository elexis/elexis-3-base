/*******************************************************************************
 * Copyright (c) 2007-2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    
 * $Id: Activator.java 5386 2009-06-23 11:34:17Z rgw_ch $
 *******************************************************************************/

package com.hilotec.elexis.kgview;

import java.util.List;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	// Einstellung fuer Plugin-Versionsinfos
	private static final String VERSION_SETTING = "hilotec/kgview/pluginversion";
	
	// The plug-in ID
	public static final String PLUGIN_ID = "com.hilotec.elexis.kgview";
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator(){}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception{
		super.start(context);
		plugin = this;
		
		// Falls noetig Dosierungen von Verschreibungen konvertieren
		int ver = CoreHub.globalCfg.get(VERSION_SETTING, 0);
		if (ver < 1) {
			System.out.println("Update");
			updateDosierungen();
			CoreHub.globalCfg.set(VERSION_SETTING, 1);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception{
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault(){
		return plugin;
	}
	
	private void updateDosierungen(){
		Query<Prescription> q = new Query<Prescription>(Prescription.class);
		List<Prescription> pl = q.execute();
		for (Prescription p : pl) {
			try {
				String[] parts = p.getDosis().split("-");
				
				if (parts == null || parts.length != 4) {
					// System.out.println("skip");
					continue;
				}
				for (int i = 0; i < 4; i++) {
					String d = parts[i];
					if (d.equals("x")) {
						parts[i] = "X";
						break;
					}
					
					// Brueche kuerzen
					if (d.matches("[0-9]+/[0-9]+")) {
						String[] dp = d.split("/");
						int z = Integer.parseInt(dp[0]);
						int n = Integer.parseInt(dp[1]);
						if (z > n) {
							int g = z / n;
							z %= n;
							parts[i] = g + " " + z + "/" + n;
						}
						
					}
				}
				p.setDosis(parts[0] + "-" + parts[1] + "-" + parts[2] + "-" + parts[3]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
