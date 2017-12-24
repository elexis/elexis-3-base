/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.base.ch.artikel;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "ch.elexis.base.ch.artikel.messages";
    public static String MedikamentDetailDisplay_Title;
    public static String MedikamentImporter_BadArticleEntry;
    public static String MedikamentImporter_BadFileFormat;
    public static String MedikamentImporter_BadPharmaCode;
    public static String MedikamentImporter_MedikamentImportTitle;
    public static String MedikamentImporter_ModeOfImport;
    public static String MedikamentImporter_OnlyIGM10AndIGM11;
    public static String MedikamentImporter_PleaseChoseFile;
    public static String MedikamentImporter_SuccessContent;
    public static String MedikamentImporter_SuccessTitel;
    public static String MedikamentImporter_WindowTitleMedicaments;

    static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
