package ch.elexis.mednet.webapi.core.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = ".messages"; //$NON-NLS-1$

	public static String AttachmentsComposite_attachment;
	public static String AttachmentsComposite_cannotBeConverted;
	public static String AttachmentsComposite_document;
	public static String AttachmentsComposite_dragFileHere;
	public static String AttachmentsComposite_openWithDoubleClick;
	public static String AttachmentsComposite_openWithDoubleClickAgain;
	public static String AttachmentsComposite_warning;
	public static String CustomerComposite_customerID;
	public static String CustomerComposite_customerFirstName;
	public static String CustomerComposite_customerLastName;
	public static String DataHandler_noPatientSelectedTitle;
	public static String DataHandler_noPatientSelectedMessage;
	public static String DataHandler_sendDocumentsTitle;
	public static String DataHandler_sendDocumentsMessage;
	public static String DocumentsSelectionDialog_doubleClickToAttach;
	public static String DocumentsSelectionDialog_epdCheckbox;
	public static String DocumentsSelectionDialog_limitReachedTitle;
	public static String DocumentsSelectionDialog_limitReachedMessage;
	public static String DocumentsSelectionDialog_search;
	public static String DocumentsSelectionDialog_selectionTitle;
	public static String FormComposite_formId;
	public static String FormComposite_formName;
	public static String MednetAuthService_browserAuthorizationPrompt;
	public static String MedNetMainComposite_formWithPatientData;
	public static String MedNetMainComposite_showPatients;
	public static String MedNetMainComposite_browserError;
	public static String MedNetMainComposite_connect;
	public static String MedNetMainComposite_connectionStarted;
	public static String MedNetMainComposite_connectedTooltip;
	public static String MedNetMainComposite_disconnectedTooltip;
	public static String MedNetMainComposite_documents;
	public static String MedNetMainComposite_error;
	public static String MedNetMainComposite_forms;
	public static String MedNetMainComposite_noPathConfigured;
	public static String MedNetMainComposite_noPathWarningTitle;
	public static String MedNetMainComposite_noPathWarningMessage;
	public static String MedNetMainComposite_tasks;
	public static String MedNetMainComposite_therapy;
	public static String MedNetMainComposite_titleMedNet;
	public static String MedNetWebPreferencePage_configForMedNetWebAPI;
	public static String MedNetWebPreferencePage_downloadFolder;
	public static String MedNetWebPreferencePage_loginName;
	public static String MedNetWebPreferencePage_requestNewToken;
	public static String ProviderComposite_Anbieter_ID;
	public static String ProviderComposite_Anbieter_Name;
	public static String SubmittedFormsComposite_noErrorForms;



	static {

		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
