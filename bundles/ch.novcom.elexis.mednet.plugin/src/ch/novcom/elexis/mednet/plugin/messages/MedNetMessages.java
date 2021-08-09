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
	public static String PatientDocumentManager_LabResultTitleSamplingFormat;
	public static String PatientDocumentManager_DocumentOmnivoreTitle;
	public static String PatientDocumentManager_documentId;
	public static String PatientDocumentManager_documentTitel;
	public static String PatientDocumentManager_FormTitle;
	
	public static String PatientLabor_errorAddingDocumentToOmnivore;
	
	
	public static String MainPreferences_labelExePath;
	public static String MainPreferences_labelPurgeInterval;
	
	public static String ContactLinkPreferences_ContactLabel;
	public static String ContactLinkPreferences_MedNetId;
	public static String ContactLinkPreferences_MedNetName;
	public static String ContactLinkPreferences_DocImport;
	public static String ContactLinkPreferences_DocImportId;
	public static String ContactLinkPreferences_FormImport;
	public static String ContactLinkPreferences_XIDDomain;
	

	public static String ContactLinkPreferences_title;
	public static String ContactLinkPreferences_new;
	public static String ContactLinkPreferences_delete;
	public static String ContactLinkPreferences_reallyDelete;
	public static String ContactLinkPreferences_deleteFailed;
	public static String ContactLinkPreferences_deleteAll;
	public static String ContactLinkPreferences_deleteAllTitle;
	public static String ContactLinkPreferences_deleteAllExplain;
	public static String ContactLinkPreferences_deleteAllFailed;
	

	public static String ContactLinkRecord_Label;
	
	public static String ContactLinkRecordEditDialog_shellTitle;
	public static String ContactLinkRecordEditDialog_title;
	public static String ContactLinkRecordEditDialog_message;
	public static String ContactLinkRecordEditDialog_labelContact;
	public static String ContactLinkRecordEditDialog_labelMedNet;
	public static String ContactLinkRecordEditDialog_labelDoc;
	public static String ContactLinkRecordEditDialog_labelForm;
	public static String ContactLinkRecordEditDialog_labelCategory;
	public static String ContactLinkRecordEditDialog_labelImportIsActive;
	public static String ContactLinkRecordEditDialog_labelDocImportId;
	public static String ContactLinkRecordEditDialog_labelXIDDomain;
	public static String ContactLinkRecordEditDialog_NoContact;
	public static String ContactLinkRecordEditDialog_NoMedNet;

	public static String Omnivore_category_doclabel;
	public static String Omnivore_category_formlabel;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, MedNetMessages.class);
	}
	
	private MedNetMessages(){}
}
