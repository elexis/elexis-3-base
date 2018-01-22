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

public class MedNetMessages extends NLS {
	private static final String BUNDLE_NAME = "ch.novcom.elexis.mednet.plugin.messages.messages";
		
	public static String FormWatcher_FormCategory;
	
	public static String DocumentImporterPage_callMedNet;
	public static String DocumentImporterPage_checkInstitution;
	public static String DocumentImporterPage_parseFile;
	public static String DocumentImporterPage_ErrorWhileParsingFile;
	public static String DocumentImporterPage_errorTitle;
	public static String DocumentImporterPage_ImportCompletedTitle;
	public static String DocumentImporterPage_ImportCompletedSSuccessText;
	public static String DocumentImporterPage_titleImport;
	public static String DocumentImporterPage_descriptionImport;
	public static String DocumentImporterPage_FileFailure;
	public static String DocumentImporterPage_FileSuccess;
	public static String DocumentImporterPage_ImportError;
	
	public static String DocumentImporter_SelectPatient;
	public static String DocumentImporter_WhoIs;
	
	public static String DocumentSettingRecord_Label;
	
	public static String PatientDocumentManager_omnivoreNotInitialized;
	public static String PatientDocumentManager_LabResultTitle;
	public static String PatientDocumentManager_LabResultTitleTransactionFormat;
	public static String PatientDocumentManager_documentId;
	public static String PatientDocumentManager_documentTitel;
	public static String PatientDocumentManager_FormTitle;
	
	public static String PatientLabor_errorAddingDocumentToOmnivore;
	
	public static String DocumentSettingRecordEditDialog_shellTitle;
	public static String DocumentSettingRecordEditDialog_title;
	public static String DocumentSettingRecordEditDialog_message;
	public static String DocumentSettingRecordEditDialog_labelInstitution;
	public static String DocumentSettingRecordEditDialog_labelCategory;
	public static String DocumentSettingRecordEditDialog_labelDocumentPath;
	public static String DocumentSettingRecordEditDialog_labelErrorPath;
	public static String DocumentSettingRecordEditDialog_labelArchivingPath;
	public static String DocumentSettingRecordEditDialog_labelPurgeInterval;
	public static String DocumentSettingRecordEditDialog_labelXIDDomain;
	public static String DocumentSettingRecordEditDialog_NoInstitution;
	public static String DocumentSettingRecordEditDialog_NoCategory;
	public static String DocumentSettingRecordEditDialog_NoPath;
	public static String DocumentSettingRecordEditDialog_NoErrorPath;
	public static String DocumentSettingRecordEditDialog_NoArchivingPath;
	public static String DocumentSettingRecordEditDialog_NotValidPath;
	public static String DocumentSettingRecordEditDialog_NoPurgeInterval;
	
	public static String DocumentPreferences_institutionName;
	public static String DocumentPreferences_category;
	public static String DocumentPreferences_path;
	public static String DocumentPreferences_errorPath;
	public static String DocumentPreferences_archivingPath; 
	public static String DocumentPreferences_archivingPurgeInterval;
	public static String DocumentPreferences_title;
	public static String DocumentPreferences_new;
	public static String DocumentPreferences_delete;
	public static String DocumentPreferences_reallyDelete;
	public static String DocumentPreferences_deleteFailed;
	public static String DocumentPreferences_deleteAll;
	public static String DocumentPreferences_reallyDeleteAll;
	public static String DocumentPreferences_deleteAllExplain;
	public static String DocumentPreferences_deleteAllFailed;
			
	public static String FormPreferences_labelFormsPath;
	public static String FormPreferences_labelErrorPath;
	public static String FormPreferences_labelArchivePath;
	public static String FormPreferences_labelPurgeInterval;
	public static String FormPreferences_NoPath;
	public static String FormPreferences_NoErrorPath;
	public static String FormPreferences_NoArchivingPath;
	public static String FormPreferences_NotValidPath;
	
	public static String MainPreferences_labelExePath;
	public static String MainPreferences_labelLogsPath;
	public static String MainPreferences_labelLogsLevel;
	
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, MedNetMessages.class);
	}
	
	private MedNetMessages(){}
}
