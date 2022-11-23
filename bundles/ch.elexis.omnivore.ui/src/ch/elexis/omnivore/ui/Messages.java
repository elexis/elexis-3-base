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

public class Messages {
	public static String DocHandle_cantReadCaption = ch.elexis.core.l10n.Messages.Core_Unable_to_read_file;
	public static String DocHandle_configErrorCaption = ch.elexis.core.l10n.Messages.DocHandle_configErrorCaption;
	public static String DocHandle_configErrorText = ch.elexis.core.l10n.Messages.Core_Save_request_but_path_not_valid;
	public static String DocHandle_documentErrorCaption = ch.elexis.core.l10n.Messages.Core_Error_with_document;
	public static String DocHandle_documentErrorText1 = ch.elexis.core.l10n.Messages.Core_Could_not_read_correctly_document;
	public static String DocHandle_documentErrorText2 = ch.elexis.core.l10n.Messages.Core_Could_not_write_document;
	public static String DocHandle_execErrorCaption = ch.elexis.core.l10n.Messages.Core_Unable_to_start_file;
	public static String DocHandle_importErrorCaption = ch.elexis.core.l10n.Messages.Core_Error_while_reading;
	public static String DocHandle_importErrorText1 = ch.elexis.core.l10n.Messages.Core_Filename_too_long;
	public static String DocHandle_importErrorText2 = ch.elexis.core.l10n.Messages.DocHandle_importErrorText2;
	public static String DocHandle_noPatientSelectedCaption = ch.elexis.core.l10n.Messages.Core_No_patient_selected;
	public static String DocHandle_noPatientSelectedText = ch.elexis.core.l10n.Messages.DocHandle_noPatientSelectedText;
	public static String DocHandle_readErrorCaption = ch.elexis.core.l10n.Messages.Core_Error_with_document;
	public static String FileImportDialog_categoryLabel = ch.elexis.core.l10n.Messages.FileImportDialog_categoryLabel;
	public static String FileImportDialog_dateLabel = ch.elexis.core.l10n.Messages.FileImportDialog_dateLabel;
	public static String FileImportDialog_dateOriginLabel = ch.elexis.core.l10n.Messages.Core_Date_of_original;
	public static String FileImportDialog_importCaption = ch.elexis.core.l10n.Messages.Core_Import_File;
	public static String FileImportDialog_importFileCaption = ch.elexis.core.l10n.Messages.Core_Import_File;
	public static String FileImportDialog_importFileText = ch.elexis.core.l10n.Messages.Core_Enter_Title_and_tags_for_document;
	public static String FileImportDialog_keywordsLabel = ch.elexis.core.l10n.Messages.Core_Keywords;
	public static String FileImportDialog_newCategoryCaption = ch.elexis.core.l10n.Messages.Core_New_Category;
	public static String FileImportDialog_newCategoryText = ch.elexis.core.l10n.Messages.FileImportDialog_newCategoryText;
	public static String FileImportDialog_titleLabel = ch.elexis.core.l10n.Messages.FileImportDialog_titleLabel;
	public static String OmnivoreView_categoryColumn = ch.elexis.core.l10n.Messages.OmnivoreView_categoryColumn;
	public static String OmnivoreView_configErrorCaption = ch.elexis.core.l10n.Messages.OmnivoreView_configErrorCaption;
	public static String OmnivoreView_configErrorText = ch.elexis.core.l10n.Messages.Core_Save_request_but_path_not_valid;
	public static String OmnivoreView_dataSources = ch.elexis.core.l10n.Messages.OmnivoreView_dataSources;
	public static String OmnivoreView_dateColumn = ch.elexis.core.l10n.Messages.OmnivoreView_dateColumn;
	public static String OmnivoreView_dateOriginColumn = ch.elexis.core.l10n.Messages.Core_Date_of_original;
	public static String OmnivoreView_deleteActionCaption = ch.elexis.core.l10n.Messages.OmnivoreView_deleteActionCaption;
	public static String OmnivoreView_deleteActionToolTip = ch.elexis.core.l10n.Messages.Core_Delete_Document;
	public static String OmnivoreView_editActionCaption = ch.elexis.core.l10n.Messages.OmnivoreView_editActionCaption;
	public static String OmnivoreView_editActionTooltip = ch.elexis.core.l10n.Messages.Core_Edit_Documentdescription;
	public static String OmnivoreView_exportActionCaption = ch.elexis.core.l10n.Messages.OmnivoreView_exportActionCaption;
	public static String OmnivoreView_exportActionTooltip = ch.elexis.core.l10n.Messages.Core_Export_to_file;
	public static String OmnivoreView_flatActionCaption = ch.elexis.core.l10n.Messages.OmnivoreView_flatActionCaption;
	public static String OmnivoreView_flatActionTooltip = ch.elexis.core.l10n.Messages.OmnivoreView_flatActionTooltip;
	public static String OmnivoreView_importActionCaption = ch.elexis.core.l10n.Messages.OmnivoreView_importActionCaption;
	public static String OmnivoreView_importActionToolTip = ch.elexis.core.l10n.Messages.OmnivoreView_importActionToolTip;
	public static String OmnivoreView_keywordsColumn = ch.elexis.core.l10n.Messages.Core_Keywords;
	public static String OmnivoreView_reallyDeleteCaption = ch.elexis.core.l10n.Messages.Core_Really_delete_caption;
	public static String OmnivoreView_reallyDeleteContents = ch.elexis.core.l10n.Messages.Core_Really_delete_0;
	public static String OmnivoreView_reallyDeleteTExt = ch.elexis.core.l10n.Messages.Core_Really_delete_0;
	public static String OmnivoreView_searchKeywordsLabel = ch.elexis.core.l10n.Messages.Core_Keywords;
	public static String OmnivoreView_searchTitleLabel = ch.elexis.core.l10n.Messages.OmnivoreView_searchTitleLabel;
	public static String OmnivoreView_titleColumn = ch.elexis.core.l10n.Messages.OmnivoreView_titleColumn;
	public static String Omnivore_ErrNoActivator = ch.elexis.core.l10n.Messages.Omnivore_ErrNoActivator;
	public static String Preferences_DEST_DIR = ch.elexis.core.l10n.Messages.Preferences_DEST_DIR;
	public static String Preferences_MAX_FILENAME_LENGTH = ch.elexis.core.l10n.Messages.Preferences_MAX_FILENAME_LENGTH;
	public static String Preferences_Rule = ch.elexis.core.l10n.Messages.Preferences_Rule;
	public static String Preferences_SRC_PATTERN = ch.elexis.core.l10n.Messages.Preferences_SRC_PATTERN;
	public static String Preferences_automatic_archiving_of_processed_files = ch.elexis.core.l10n.Messages.Preferences_automatic_archiving_of_processed_files;
	public static String Preferences_construction_of_temporary_filename = ch.elexis.core.l10n.Messages.Preferences_construction_of_temporary_filename;
	public static String Preferences_cotf_add_trail_char = ch.elexis.core.l10n.Messages.Preferences_cotf_add_trail_char;
	public static String Preferences_cotf_constant1 = ch.elexis.core.l10n.Messages.Preferences_cotf_constant1;
	public static String Preferences_cotf_constant2 = ch.elexis.core.l10n.Messages.Preferences_cotf_constant2;
	public static String Preferences_cotf_dguid = ch.elexis.core.l10n.Messages.Preferences_cotf_dguid;
	public static String Preferences_cotf_dk = ch.elexis.core.l10n.Messages.Preferences_cotf_dk;
	public static String Preferences_cotf_dob = ch.elexis.core.l10n.Messages.Core_Enter_Birthdate;
	public static String Preferences_cotf_dt = ch.elexis.core.l10n.Messages.Preferences_cotf_dt;
	public static String Preferences_cotf_fill_lead_char = ch.elexis.core.l10n.Messages.Preferences_cotf_fill_lead_char;
	public static String Preferences_cotf_fn = ch.elexis.core.l10n.Messages.Preferences_cotf_fn;
	public static String Preferences_cotf_gn = ch.elexis.core.l10n.Messages.Preferences_cotf_gn;
	public static String Preferences_cotf_num_digits = ch.elexis.core.l10n.Messages.Preferences_cotf_num_digits;
	public static String Preferences_cotf_pid = ch.elexis.core.l10n.Messages.Preferences_cotf_pid;
	public static String Preferences_cotf_random = ch.elexis.core.l10n.Messages.Preferences_cotf_random;
	public static String Preferences_dateModifiable = ch.elexis.core.l10n.Messages.Preferences_dateModifiable;
	public static String Preferences_omnivore = ch.elexis.core.l10n.Messages.Preferences_omnivore;
	public static String Preferences_pathForDocs = ch.elexis.core.l10n.Messages.Preferences_pathForDocs;
	public static String Preferences_storeInFS = ch.elexis.core.l10n.Messages.Preferences_storeInFS;
	public static String DocHandle_runErrorHeading = ch.elexis.core.l10n.Messages.Core_Unable_to_start_file;

}
