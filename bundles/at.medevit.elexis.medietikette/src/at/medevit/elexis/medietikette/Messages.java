/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.medietikette;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "at.medevit.elexis.medietikette.messages"; //$NON-NLS-1$
	public static String DataAccessor_Description;
	public static String DataAccessor_Name;
	public static String PrintMediEtiketteUi_DialogMessage;
	public static String PrintMediEtiketteUi_DialogTitel;
	public static String PrintMediEtiketteUi_PrintError;
	public static String PrintMediEtiketteUi_TemplateName;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
