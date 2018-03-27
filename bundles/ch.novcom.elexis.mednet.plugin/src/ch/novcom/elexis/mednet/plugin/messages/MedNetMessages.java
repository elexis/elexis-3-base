/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.messages;

import org.eclipse.osgi.util.NLS;

public class MedNetMessages  {
		
	public static String FormWatcher_FormCategory = ch.elexis.base.l10n.Messages.FormWatcher_FormCategory;
	
	public static String DocumentImporterPage_callMedNet = ch.elexis.base.l10n.Messages.DocumentImporterPage_callMedNet;
	public static String DocumentImporterPage_checkInstitution = ch.elexis.base.l10n.Messages.DocumentImporterPage_checkInstitution;
	public static String DocumentImporterPage_parseFile = ch.elexis.base.l10n.Messages.DocumentImporterPage_parseFile;
	public static String DocumentImporterPage_ErrorWhileParsingFile = ch.elexis.base.l10n.Messages.DocumentImporterPage_ErrorWhileParsingFile;
	public static String DocumentImporterPage_errorTitle = ch.elexis.base.l10n.Messages.DocumentImporterPage_errorTitle;
	public static String DocumentImporterPage_ImportCompletedTitle = ch.elexis.base.l10n.Messages.DocumentImporterPage_ImportCompletedTitle;
	public static String DocumentImporterPage_ImportCompletedSSuccessText = ch.elexis.base.l10n.Messages.DocumentImporterPage_ImportCompletedSSuccessText;
	public static String DocumentImporterPage_titleImport = ch.elexis.base.l10n.Messages.DocumentImporterPage_titleImport;
	public static String DocumentImporterPage_descriptionImport = ch.elexis.base.l10n.Messages.DocumentImporterPage_descriptionImport;
	public static String DocumentImporterPage_FileFailure = ch.elexis.base.l10n.Messages.DocumentImporterPage_FileFailure;
	public static String DocumentImporterPage_FileSuccess = ch.elexis.base.l10n.Messages.DocumentImporterPage_FileSuccess;
	public static String DocumentImporterPage_ImportError = ch.elexis.base.l10n.Messages.DocumentImporterPage_ImportError;
	
	public static String DocumentImporter_SelectPatient = ch.elexis.base.l10n.Messages.DocumentImporter_SelectPatient;
	public static String DocumentImporter_WhoIs = ch.elexis.base.l10n.Messages.DocumentImporter_WhoIs;
	
	public static String DocumentSettingRecord_Label = ch.elexis.base.l10n.Messages.DocumentSettingRecord_Label;
	
	public static String PatientDocumentManager_omnivoreNotInitialized = ch.elexis.base.l10n.Messages.PatientDocumentManager_omnivoreNotInitialized;
	public static String PatientDocumentManager_LabResultTitle = ch.elexis.base.l10n.Messages.PatientDocumentManager_LabResultTitle;
	public static String PatientDocumentManager_LabResultTitleTransactionFormat = ch.elexis.base.l10n.Messages.PatientDocumentManager_LabResultTitleTransactionFormat;
	public static String PatientDocumentManager_documentId = ch.elexis.base.l10n.Messages.PatientDocumentManager_documentId;
	public static String PatientDocumentManager_documentTitel = ch.elexis.base.l10n.Messages.PatientDocumentManager_documentTitel;
	public static String PatientDocumentManager_FormTitle = ch.elexis.base.l10n.Messages.PatientDocumentManager_FormTitle;
	
	public static String PatientLabor_errorAddingDocumentToOmnivore = ch.elexis.base.l10n.Messages.PatientLabor_errorAddingDocumentToOmnivore;
	
	public static String DocumentSettingRecordEditDialog_shellTitle = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_shellTitle;
	public static String DocumentSettingRecordEditDialog_title = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_title;
	public static String DocumentSettingRecordEditDialog_message = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_message;
	public static String DocumentSettingRecordEditDialog_labelInstitution = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_labelInstitution;
	public static String DocumentSettingRecordEditDialog_labelCategory = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_labelCategory;
	public static String DocumentSettingRecordEditDialog_labelDocumentPath = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_labelDocumentPath;
	public static String DocumentSettingRecordEditDialog_labelErrorPath = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_labelErrorPath;
	public static String DocumentSettingRecordEditDialog_labelArchivingPath = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_labelArchivingPath;
	public static String DocumentSettingRecordEditDialog_labelPurgeInterval = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_labelPurgeInterval;
	public static String DocumentSettingRecordEditDialog_labelXIDDomain = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_labelXIDDomain;
	public static String DocumentSettingRecordEditDialog_NoInstitution = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_NoInstitution;
	public static String DocumentSettingRecordEditDialog_NoCategory = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_NoCategory;
	public static String DocumentSettingRecordEditDialog_NoPath = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_NoPath;
	public static String DocumentSettingRecordEditDialog_NoErrorPath = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_NoErrorPath;
	public static String DocumentSettingRecordEditDialog_NoArchivingPath = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_NoArchivingPath;
	public static String DocumentSettingRecordEditDialog_NotValidPath = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_NotValidPath;
	public static String DocumentSettingRecordEditDialog_NoPurgeInterval = ch.elexis.base.l10n.Messages.DocumentSettingRecordEditDialog_NoPurgeInterval;
	
	public static String DocumentPreferences_institutionName = ch.elexis.base.l10n.Messages.DocumentPreferences_institutionName;
	public static String DocumentPreferences_category = ch.elexis.base.l10n.Messages.DocumentPreferences_category;
	public static String DocumentPreferences_path = ch.elexis.base.l10n.Messages.DocumentPreferences_path;
	public static String DocumentPreferences_errorPath = ch.elexis.base.l10n.Messages.DocumentPreferences_errorPath;
	public static String DocumentPreferences_archivingPath = ch.elexis.base.l10n.Messages.DocumentPreferences_archivingPath; 
	public static String DocumentPreferences_archivingPurgeInterval = ch.elexis.base.l10n.Messages.DocumentPreferences_archivingPurgeInterval;
	public static String DocumentPreferences_title = ch.elexis.base.l10n.Messages.DocumentPreferences_title;
	public static String DocumentPreferences_new = ch.elexis.base.l10n.Messages.DocumentPreferences_new;
	public static String DocumentPreferences_delete = ch.elexis.base.l10n.Messages.DocumentPreferences_delete;
	public static String DocumentPreferences_reallyDelete = ch.elexis.base.l10n.Messages.DocumentPreferences_reallyDelete;
	public static String DocumentPreferences_deleteFailed = ch.elexis.base.l10n.Messages.DocumentPreferences_deleteFailed;
	public static String DocumentPreferences_deleteAll = ch.elexis.base.l10n.Messages.DocumentPreferences_deleteAll;
	public static String DocumentPreferences_reallyDeleteAll = ch.elexis.base.l10n.Messages.DocumentPreferences_reallyDeleteAll;
	public static String DocumentPreferences_deleteAllExplain = ch.elexis.base.l10n.Messages.DocumentPreferences_deleteAllExplain;
	public static String DocumentPreferences_deleteAllFailed = ch.elexis.base.l10n.Messages.DocumentPreferences_deleteAllFailed;
			
	public static String FormPreferences_labelFormsPath = ch.elexis.base.l10n.Messages.FormPreferences_labelFormsPath;
	public static String FormPreferences_labelErrorPath = ch.elexis.base.l10n.Messages.FormPreferences_labelErrorPath;
	public static String FormPreferences_labelArchivePath = ch.elexis.base.l10n.Messages.FormPreferences_labelArchivePath;
	public static String FormPreferences_labelPurgeInterval = ch.elexis.base.l10n.Messages.FormPreferences_labelPurgeInterval;
	public static String FormPreferences_NoPath = ch.elexis.base.l10n.Messages.FormPreferences_NoPath;
	public static String FormPreferences_NoErrorPath = ch.elexis.base.l10n.Messages.FormPreferences_NoErrorPath;
	public static String FormPreferences_NoArchivingPath = ch.elexis.base.l10n.Messages.FormPreferences_NoArchivingPath;
	public static String FormPreferences_NotValidPath = ch.elexis.base.l10n.Messages.FormPreferences_NotValidPath;
	
	public static String MainPreferences_labelExePath = ch.elexis.base.l10n.Messages.MainPreferences_labelExePath;
	public static String MainPreferences_labelLogsPath = ch.elexis.base.l10n.Messages.MainPreferences_labelLogsPath;
	public static String MainPreferences_labelLogsLevel = ch.elexis.base.l10n.Messages.MainPreferences_labelLogsLevel;
	
	
}
