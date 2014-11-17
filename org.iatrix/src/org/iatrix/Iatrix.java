/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     D. Lutz - initial API and implementation
 * 
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix;

/**
 * 
 * 
 * @author danlutz
 */
public class Iatrix {
	public static final String IMG_ACTIVE = IatrixActivator.PLUGIN_ID + "_active";
	public static final String IMG_INACTIVE = IatrixActivator.PLUGIN_ID + "_inactive";
	
	public static final String SHOW_ALL_CHARGES_COMMAND = "org.iatrix.commands.show_all_charges";
	public static final String SHOW_ALL_CONSULTATIONS_COMMAND =
		"org.iatrix.commands.show_all_consultations";
	
	public static final String CFG_MAX_SHOWN_CHARGES = "org.iatrix/max_shown_charges";
	public static final int CFG_MAX_SHOWN_CHARGES_DEFAULT = 2;
	public static final String CFG_MAX_SHOWN_CONSULTATIONS = "org.iatrix/max_shown_consultations";
	public static final int CFG_MAX_SHOWN_CONSULTATIONS_DEFAULT = 5;
	
	// cons text auto save, in seconds (0 = disabled)
	public static final String CFG_AUTO_SAVE_PERIOD = "org.iatrix/auto_save_period";
	public static final int CFG_AUTO_SAVE_PERIOD_DEFAULT = 120;
	
	// close code selection window (leistungen, diagnosen) after selection (true), or let it open
	// (false)
	public static final String CFG_CODE_SELECTION_AUTOCLOSE = "org.iatrix/code_selection_autoclose";
	public static final boolean CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT = false;
	
	// Use konstext locking defaults to true
	public static final String CFG_USE_KONSTEXT_LOCKING = "org.iatrix/use_konstext_locking";
	public static final boolean CFG_USE_KONSTEXT_LOCKING_DEFAULT = true;
	
	// Use konstext tracing defaults to false
	public static final String CFG_USE_KONSTEXT_TRACE = "org.iatrix/use_konstext_trace";
	public static final boolean CFG_USE_KONSTEXT_TRACE_DEFAULT = false;
}
