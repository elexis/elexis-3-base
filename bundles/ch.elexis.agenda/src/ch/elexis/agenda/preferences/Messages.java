/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.agenda.preferences.messages"; //$NON-NLS-1$
	public static String AgendaDruck_printDirectly;
	public static String AgendaDruck_printerForCards;
	public static String AgendaDruck_settingsForPrint;
	public static String AgendaDruck_templateForCards;
	public static String AgendaDruck_TrayForCards;
	public static String PreferenceConstants_appointmentCard;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
