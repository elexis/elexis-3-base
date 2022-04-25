/*******************************************************************************
 * Copyright (c) 2006-2010, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.agenda.ui.messages"; //$NON-NLS-1$
	public static String AgendaParallel_dayBack;
	public static String AgendaParallel_dayForward;
	public static String AgendaParallel_selectDay;
	public static String AgendaParallel_setZoomFactor;
	public static String AgendaParallel_showCalendarForSelcetion;
	public static String AgendaParallel_showNextDay;
	public static String AgendaParallel_showPreviousDay;
	public static String AgendaParallel_zoom;
	public static String ColumnHeader_Mandantors;
	public static String ColumnHeader_mandatorsForParallelView;
	public static String ColumnHeader_selectMandators;
	public static String ColumnHeader_selectMandatorToShow;
	public static String TerminListView_noPatientSelected;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
