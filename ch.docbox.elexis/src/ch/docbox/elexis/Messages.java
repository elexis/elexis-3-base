/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.elexis;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.docbox.elexis.messages"; //$NON-NLS-1$
	
	public static String DocboxBackgroundJob_AppointmentsUpdated;
	
	public static String DocboxBackgroundJob_DocumentsDownloaded;
	
	public static String DocboxBackgroundJob_Title;
	public static String DocboxBackgroundJob_DoctorDirecotoryUpdated;
	
	public static String DocboxHospitalReferralAction_NoPatientSelectedText;
	public static String DocboxHospitalReferralAction_NoPatientSelectedMessage;
	
	public static String UserDocboxPreferences_LoginId;
	public static String UserDocboxPreferences_Password;
	public static String UserDocboxPreferences_ConnectionTest;
	public static String UserDocboxPreferences_ConnectionTestWithDocbox;
	public static String UserDocboxPreferences_Description;
	public static String UserDocboxPreferences_NoSecretKeyTitle;
	public static String UserDocboxPreferences_NoSecretKey;
	
	public static String UserDocboxPreferences_PathFiles;
	public static String UserDocboxPreferences_PathHCardAPI;
	public static String UserDocboxPreferences_UseProxy;
	public static String UserDocboxPreferences_UseProxyHost;
	public static String UserDocboxPreferences_UseProxyPort;
	public static String UserDocboxPreferences_ClearDocboxInbox;
	public static String UserDocboxPreferences_ClearDocboxInboxConfirm;
	
	public static String DocboxDocumentsView_Title;
	public static String DocboxDocumentsView_Patient;
	public static String DocboxDocumentsView_DateSent;
	public static String DocboxDocumentsView_Sender;
	public static String DocboxDocumentsView_Attachments;
	public static String DocboxDocumentsView_Action_AttachmentsOpen;
	public static String DocboxDocumentsView_Action_Delete;
	public static String DocboxDocumentsView_Action_DeleteConfirmMsg;
	public static String DocboxDocumentsView_Action_ShowCdaDocument;
	public static String DocboxDocumentsView_Action_CreatePatient;
	
	public static String UserDocboxPreferences_AgendaBerich;
	public static String UserDocboxPreferences_AgendaSettingsPerUser;
	public static String UserDocboxPreferences_GetAppointmentsEmergencyService;
	public static String UserDocboxPreferences_GetAppointmentsPharmaVisits;
	public static String UserDocboxPreferences_GetAppointmentsTerminvereinbarungen;
	public static String UserDocboxPreferences_IsDocboxTest;
	public static String UserDocboxPreferences_UseHCard;
	public static String UserDocboxPreferences_SecretKey;
	
	public static String DocboxTerminvereinbarungAction_NoPatientSelectedText;
	public static String DocboxTerminvereinbarungAction_NoPatientSelectedMessage;
	
	public static String DocboxTerminvereinbarungAction_NoDoctorSelectedText;
	public static String DocboxTerminvereinbarungAction_NoDoctorSelectedMessage;
	
	public static String DocboxArztArztAction_NoPatientSelectedText;
	public static String DocboxArztArztAction_NoPatientSelectedMessage;
	public static String DocboxArztArztAction_NoDoctorSelectedText;
	public static String DocboxArztArztAction_NoDoctorSelectedMessage;
	public static String DocboxArztArztAction_NoPdfGeneratedMessage;
	public static String DocboxArztArztAction_NoCdaGeneratedMessage;
	public static String DocboxArztArztAction_NoXmlGeneratedMessage;
	public static String DocboxArztArztAction_NoZipGeneratedMessage;
	public static String DocboxArztArztAction_SendDocumentFailed;
	public static String DocboxArztArztDialog_Title;
	public static String DocboxArztArztDialog_Message;
	public static String DocboxArztArztDialog_TextTitle;
	public static String DocboxArztArztDialog_TextMessage;
	public static String DocboxArztArztDialog_TextPatient;
	public static String DocboxArztArztDialog_TextDoctor;
	public static String DocboxArztArztDialog_TextAttachments;
	public static String DocboxArztArztDialog_ButtonAddAttachments;
	public static String DocboxArztArztDialog_ErrorMessage;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
