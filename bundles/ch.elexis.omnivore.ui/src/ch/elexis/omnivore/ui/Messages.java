/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.omnivore.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "ch.elexis.omnivore.ui.messages";
    public static String DocHandle_cantReadCaption;
    public static String DocHandle_configErrorCaption;
    public static String DocHandle_configErrorText;
    public static String DocHandle_documentErrorCaption;
    public static String DocHandle_documentErrorText1;
    public static String DocHandle_documentErrorText2;
    public static String DocHandle_execErrorCaption;
    public static String DocHandle_importErrorCaption;
    public static String DocHandle_importErrorText1;
    public static String DocHandle_importErrorText2;
    public static String DocHandle_noPatientSelectedCaption;
    public static String DocHandle_noPatientSelectedText;
    public static String DocHandle_readErrorCaption;
    public static String FileImportDialog_categoryLabel;
    public static String FileImportDialog_dateLabel;
	public static String FileImportDialog_dateOriginLabel;
    public static String FileImportDialog_importCaption;
    public static String FileImportDialog_importFileCaption;
    public static String FileImportDialog_importFileText;
    public static String FileImportDialog_keywordsLabel;
    public static String FileImportDialog_newCategoryCaption;
    public static String FileImportDialog_newCategoryText;
    public static String FileImportDialog_titleLabel;
    public static String OmnivoreView_categoryColumn;
    public static String OmnivoreView_configErrorCaption;
    public static String OmnivoreView_configErrorText;
    public static String OmnivoreView_dataSources;
    public static String OmnivoreView_dateColumn;
	public static String OmnivoreView_dateOriginColumn;
    public static String OmnivoreView_deleteActionCaption;
    public static String OmnivoreView_deleteActionToolTip;
    public static String OmnivoreView_editActionCaption;
    public static String OmnivoreView_editActionTooltip;
    public static String OmnivoreView_exportActionCaption;
    public static String OmnivoreView_exportActionTooltip;
    public static String OmnivoreView_flatActionCaption;
    public static String OmnivoreView_flatActionTooltip;
    public static String OmnivoreView_importActionCaption;
    public static String OmnivoreView_importActionToolTip;
    public static String OmnivoreView_keywordsColumn;
    public static String OmnivoreView_reallyDeleteCaption;
    public static String OmnivoreView_reallyDeleteContents;
    public static String OmnivoreView_reallyDeleteTExt;
    public static String OmnivoreView_searchKeywordsLabel;
    public static String OmnivoreView_searchTitleLabel;
    public static String OmnivoreView_titleColumn;
    public static String Omnivore_ErrNoActivator;
    public static String Preferences_DEST_DIR;
    public static String Preferences_MAX_FILENAME_LENGTH;
    public static String Preferences_Rule;
    public static String Preferences_SRC_PATTERN;
    public static String Preferences_automatic_archiving_of_processed_files;
    public static String Preferences_construction_of_temporary_filename;
    public static String Preferences_cotf_add_trail_char;
    public static String Preferences_cotf_constant1;
    public static String Preferences_cotf_constant2;
    public static String Preferences_cotf_dguid;
    public static String Preferences_cotf_dk;
    public static String Preferences_cotf_dob;
    public static String Preferences_cotf_dt;
    public static String Preferences_cotf_fill_lead_char;
    public static String Preferences_cotf_fn;
    public static String Preferences_cotf_gn;
    public static String Preferences_cotf_num_digits;
    public static String Preferences_cotf_pid;
    public static String Preferences_cotf_random;
    public static String Preferences_dateModifiable;
    public static String Preferences_omnivore;
    public static String Preferences_pathForDocs;
    public static String Preferences_storeInFS;
	public static String DocHandle_runErrorHeading;

    static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
