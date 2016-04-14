/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.base.ch.diagnosecodes_schweiz;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.base.ch.diagnosecodes_schweiz.messages"; //$NON-NLS-1$
	
	public static String ICDImporter_createTable;
	public static String ICDImporter_enterDirectory;
	public static String ICDImporter_icdImport;
	public static String ICDImporter_readCodes;
	public static String ICDCodeSelectorFactory_errorLoading;
	public static String ICDCodeSelectorFactory_couldntCreate;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
