/*******************************************************************************
 * Copyright (c) 2006-2017, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *
 *******************************************************************************/
package ch.elexis.base.ch.migel;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.base.ch.migel.messages"; //$NON-NLS-1$
	public static String MiGelImporter_ClearAllData;
	public static String MiGelImporter_ModeCreateNew;
	public static String MiGelImporter_ModeUpdateAdd;
	public static String MiGelImporter_PleaseSelectFile;
	public static String MiGelImporter_ReadMigel;
	public static String MiGelDetailDisplay_Price;
	public static String MiGelDetailDisplay_Unit;
	public static String MiGelDetailDisplay_Amount;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
