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

public class Messages {

	public static String DocboxBackgroundJob_AppointmentsUpdated = ch.elexis.core.l10n.Messages.DocboxBackgroundJob_AppointmentsUpdated;

	public static String DocboxBackgroundJob_DocumentsDownloaded = ch.elexis.core.l10n.Messages.DocboxBackgroundJob_DocumentsDownloaded;

	public static String DocboxBackgroundJob_Title = ch.elexis.core.l10n.Messages.DocboxBackgroundJob_Title;
	public static String DocboxBackgroundJob_DoctorDirecotoryUpdated = ch.elexis.core.l10n.Messages.DocboxBackgroundJob_DoctorDirecotoryUpdated;

	public static String DocboxHospitalReferralAction_NoPatientSelectedText = ch.elexis.core.l10n.Messages.DocboxHospitalReferralAction_NoPatientSelectedText;
	public static String DocboxHospitalReferralAction_NoPatientSelectedMessage = ch.elexis.core.l10n.Messages.DocboxHospitalReferralAction_NoPatientSelectedMessage;

	public static String UserDocboxPreferences_LoginId = ch.elexis.core.l10n.Messages.UserDocboxPreferences_LoginId;
	public static String UserDocboxPreferences_Password = ch.elexis.core.l10n.Messages.Core_Password;
	public static String UserDocboxPreferences_ConnectionTest = ch.elexis.core.l10n.Messages.DocBox_ConnectionTest;
	public static String UserDocboxPreferences_ConnectionTestWithDocbox = ch.elexis.core.l10n.Messages.DocBox_ConnectionTest;
	public static String UserDocboxPreferences_Description = ch.elexis.core.l10n.Messages.UserDocboxPreferences_Description;
	public static String UserDocboxPreferences_NoSecretKeyTitle = ch.elexis.core.l10n.Messages.UserDocboxPreferences_NoSecretKeyTitle;
	public static String UserDocboxPreferences_NoSecretKey = ch.elexis.core.l10n.Messages.UserDocboxPreferences_NoSecretKey;

	public static String UserDocboxPreferences_PathFiles = ch.elexis.core.l10n.Messages.UserDocboxPreferences_PathFiles;
	public static String UserDocboxPreferences_PathHCardAPI = ch.elexis.core.l10n.Messages.UserDocboxPreferences_PathHCardAPI;
	public static String UserDocboxPreferences_UseProxy = ch.elexis.core.l10n.Messages.UserDocboxPreferences_UseProxy;
	public static String UserDocboxPreferences_UseProxyHost = ch.elexis.core.l10n.Messages.UserDocboxPreferences_UseProxyHost;
	public static String UserDocboxPreferences_UseProxyPort = ch.elexis.core.l10n.Messages.UserDocboxPreferences_UseProxyPort;
	public static String UserDocboxPreferences_ClearDocboxInbox = ch.elexis.core.l10n.Messages.UserDocboxPreferences_ClearDocboxInbox;
	public static String UserDocboxPreferences_ClearDocboxInboxConfirm = ch.elexis.core.l10n.Messages.UserDocboxPreferences_ClearDocboxInboxConfirm;
	public static String UserDocboxPreferences_ConvertDocboxIds = ch.elexis.core.l10n.Messages.UserDocboxPreferences_ConvertDocboxIds;
	public static String UserDocboxPreferences_ConvertDocboxIds_Tooltip = ch.elexis.core.l10n.Messages.UserDocboxPreferences_ConvertDocboxIds_Tooltip;

	public static String DocboxDocumentsView_Title = ch.elexis.core.l10n.Messages.DocboxDocumentsView_Title;
	public static String DocboxDocumentsView_Patient = ch.elexis.core.l10n.Messages.Core_Patient;
	public static String DocboxDocumentsView_DateSent = ch.elexis.core.l10n.Messages.DocboxDocumentsView_DateSent;
	public static String DocboxDocumentsView_Sender = ch.elexis.core.l10n.Messages.Core_Sender;
	public static String DocboxDocumentsView_Attachments = ch.elexis.core.l10n.Messages.Attachements;
	public static String DocboxDocumentsView_Action_AttachmentsOpen = ch.elexis.core.l10n.Messages.DocboxDocumentsView_Action_AttachmentsOpen;
	public static String DocboxDocumentsView_Action_Delete = ch.elexis.core.l10n.Messages.Core_Delete_Document;
	public static String DocboxDocumentsView_Action_DeleteConfirmMsg = ch.elexis.core.l10n.Messages.DocboxDocumentsView_Action_DeleteConfirmMsg;
	public static String DocboxDocumentsView_Action_ShowCdaDocument = ch.elexis.core.l10n.Messages.DocboxDocumentsView_Action_ShowCdaDocument;
	public static String DocboxDocumentsView_Action_CreatePatient = ch.elexis.core.l10n.Messages.DocboxDocumentsView_Action_CreatePatient;

	public static String UserDocboxPreferences_AgendaBerich = ch.elexis.core.l10n.Messages.UserDocboxPreferences_AgendaBerich;
	public static String UserDocboxPreferences_AgendaSettingsPerUser = ch.elexis.core.l10n.Messages.UserDocboxPreferences_AgendaSettingsPerUser;
	public static String UserDocboxPreferences_GetAppointmentsEmergencyService = ch.elexis.core.l10n.Messages.UserDocboxPreferences_GetAppointmentsEmergencyService;
	public static String UserDocboxPreferences_GetAppointmentsPharmaVisits = ch.elexis.core.l10n.Messages.UserDocboxPreferences_GetAppointmentsPharmaVisits;
	public static String UserDocboxPreferences_GetAppointmentsTerminvereinbarungen = ch.elexis.core.l10n.Messages.UserDocboxPreferences_GetAppointmentsTerminvereinbarungen;
	public static String UserDocboxPreferences_IsDocboxTest = ch.elexis.core.l10n.Messages.UserDocboxPreferences_IsDocboxTest;
	public static String UserDocboxPreferences_UseHCard = ch.elexis.core.l10n.Messages.UserDocboxPreferences_UseHCard;
	public static String UserDocboxPreferences_SecretKey = ch.elexis.core.l10n.Messages.UserDocboxPreferences_SecretKey;

	public static String DocboxTerminvereinbarungAction_NoPatientSelectedText = ch.elexis.core.l10n.Messages.DocBox_AppointmentAction;
	public static String DocboxTerminvereinbarungAction_NoPatientSelectedMessage = ch.elexis.core.l10n.Messages.DocboxTerminvereinbarungAction_NoPatientSelectedMessage;

	public static String DocboxTerminvereinbarungAction_NoDoctorSelectedText = ch.elexis.core.l10n.Messages.DocBox_AppointmentAction;
	public static String DocboxTerminvereinbarungAction_NoDoctorSelectedMessage = ch.elexis.core.l10n.Messages.DocboxTerminvereinbarungAction_NoDoctorSelectedMessage;

	public static String DocboxArztArztAction_NoPatientSelectedText = ch.elexis.core.l10n.Messages.DoxBox_doctor2doctor_Communication;
	public static String DocboxArztArztAction_NoPatientSelectedMessage = ch.elexis.core.l10n.Messages.DocboxArztArztAction_NoPatientSelectedMessage;
	public static String DocboxArztArztAction_NoDoctorSelectedText = ch.elexis.core.l10n.Messages.DoxBox_doctor2doctor_Communication;
	public static String DocboxArztArztAction_NoDoctorSelectedMessage = ch.elexis.core.l10n.Messages.DocboxArztArztAction_NoDoctorSelectedMessage;
	public static String DocboxArztArztAction_NoPdfGeneratedMessage = ch.elexis.core.l10n.Messages.DocboxArztArztAction_NoPdfGeneratedMessage;
	public static String DocboxArztArztAction_NoCdaGeneratedMessage = ch.elexis.core.l10n.Messages.DocboxArztArztAction_NoCdaGeneratedMessage;
	public static String DocboxArztArztAction_NoXmlGeneratedMessage = ch.elexis.core.l10n.Messages.DocboxArztArztAction_NoXmlGeneratedMessage;
	public static String DocboxArztArztAction_NoZipGeneratedMessage = ch.elexis.core.l10n.Messages.DocboxArztArztAction_NoZipGeneratedMessage;
	public static String DocboxArztArztAction_SendDocumentFailed = ch.elexis.core.l10n.Messages.DocboxArztArztAction_SendDocumentFailed;
	public static String DocboxArztArztDialog_Title = ch.elexis.core.l10n.Messages.DoxBox_doctor2doctor_Communication;
	public static String DocboxArztArztDialog_Message = ch.elexis.core.l10n.Messages.Core_Message;
	public static String DocboxArztArztDialog_TextTitle = ch.elexis.core.l10n.Messages.DocboxArztArztDialog_TextTitle;
	public static String DocboxArztArztDialog_TextMessage = ch.elexis.core.l10n.Messages.Core_Message;
	public static String DocboxArztArztDialog_TextPatient = ch.elexis.core.l10n.Messages.Core_Patient;
	public static String DocboxArztArztDialog_TextDoctor = ch.elexis.core.l10n.Messages.DocboxArztArztDialog_TextDoctor;
	public static String DocboxArztArztDialog_TextAttachments = ch.elexis.core.l10n.Messages.Attachements;
	public static String DocboxArztArztDialog_ButtonAddAttachments = ch.elexis.core.l10n.Messages.DocboxArztArztDialog_ButtonAddAttachments;
	public static String DocboxArztArztDialog_ErrorMessage = ch.elexis.core.l10n.Messages.DocboxArztArztDialog_ErrorMessage;

}
