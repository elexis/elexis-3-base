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
package ch.elexis.artikel_ch.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.artikel_ch.data.messages"; //$NON-NLS-1$
	public static String MedikamentImporter_BadFileFormat;
	public static String MedikamentImporter_BadPharmaCode;
	public static String MedikamentImporter_MedikamentImportTitle;
	public static String MedikamentImporter_ModeOfImport;
	public static String MedikamentImporter_OnlyIGM10AndIGM11;
	public static String MedikamentImporter_PleaseChoseFile;
	public static String MedikamentImporter_WindowTitleMedicaments;
	public static String MedikamentImporter_SuccessTitel;
	public static String MedikamentImporter_SuccessContent;
	public static String MedikamentImporter_BadArticleEntry;
	public static String MiGelImporter_ClearAllData;
	public static String MiGelImporter_ModeCreateNew;
	public static String MiGelImporter_ModeUpdateAdd;
	public static String MiGelImporter_PleaseSelectFile;
	public static String MiGelImporter_ReadMigel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
