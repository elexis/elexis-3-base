/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.base.ch.diagnosecodes;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "ch.elexis.base.ch.diagnosecodes.messages";
    public static String ICDCodeSelectorFactory_couldntCreate;
    public static String ICDCodeSelectorFactory_errorLoading;
    public static String ICDImporter_createTable;
    public static String ICDImporter_enterDirectory;
    public static String ICDImporter_icdImport;
    public static String ICDImporter_readCodes;

	public static String TICodeDetail_fulltext;
	
    static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
