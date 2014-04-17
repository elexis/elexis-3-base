/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/
package ch.elexis.labor.viollier.v2;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.labor.viollier.v2.messages"; //$NON-NLS-1$
	public static String LabOrderImport_AllFiles;
	public static String LabOrderImport_AutomaticMedTransfer;
	public static String LabOrderImport_Browse;
	public static String LabOrderImport_CreateLabResults;
	public static String LabOrderImport_JMedTransfer;
	public static String LabOrderImport_descriptionImport;
	public static String LabOrderImport_EndImport;
	public static String LabOrderImport_Error;
	public static String LabOrderImport_ErrorFileNotFound;
	public static String LabOrderImport_ErrorHL7Exception;
	public static String LabOrderImport_ErrorIdentifyingPatient;
	public static String LabOrderImport_ErrorMatchingPatientWithOrderNr;
	public static String LabOrderImport_ErrorMultiplePDFFilesFound;
	public static String LabOrderImport_ErrorSaveXid;
	public static String LabOrderImport_ErrorNonNumericFillerOrderNumber;
	public static String LabOrderImport_ErrorNonNumericPlacerOrderNumber;
	public static String LabOrderImport_ErrorStoreDocument;
	public static String LabOrderImport_ErrorWhileParsingHL7File;
	public static String LabOrderImport_errorMsgVerarbeitung;
	public static String LabOrderImport_errorTitle;
	public static String LabOrderImport_HL7File;
	public static String LabOrderImport_HL7Files;
	public static String LabOrderImport_ImportCompletedSSuccessText;
	public static String LabOrderImport_ImportCompletedTitle;
	public static String LabOrderImport_ImportFromHL7;
	public static String LabOrderImport_InfoNumberDonloadedFiles;
	public static String LabOrderImport_InfoParseFile;
	public static String LabOrderImport_InfoPatientIdentifiedByExactDemographics;
	public static String LabOrderImport_InfoProcessFiles;
	public static String LabOrderImport_InfoPurgingArchiveDir;
	public static String LabOrderImport_InfoReadDownloadDir;
	public static String LabOrderImport_InfoStoredFillerOrderNr;
	public static String LabOrderImport_InfoSaveXid;
	public static String LabOrderImport_LabResult;
	public static String LabOrderImport_monitorImportiereHL7;
	public static String LabOrderImport_OverwriteOlderValues;
	public static String LabOrderImport_PatientIdentifiedByUser;
	public static String LabOrderImport_PurgeArchiveDir;
	public static String LabOrderImport_ReferenceRangeWarningText;
	public static String LabOrderImport_ReferenceRangeWarningTitle;
	public static String LabOrderImport_titleImport;
	public static String LabOrderImport_SelectPatient;
	public static String LabOrderImport_StartImport;
	public static String LabOrderImport_StartMedTransfer;
	public static String LabOrderImport_Warning;
	public static String LabOrderImport_WarningUserAbortWhileIdentifyingPatient;
	public static String LabOrderImport_WhoIs;
	public static String LabOrderImport_InfoCategoryCreate;
	public static String LabOrderImport_WarnCategoryCreate;
	public static String PatientLabor_DocumentLabItemName;
	public static String PatientLabor_errorAddingDocument;
	public static String PatientLabor_errorCreatingTmpDir;
	public static String PatientLabor_errorKeineDokumentablage;
	public static String PatientLabor_InfoDocSavedToOmnivore;
	public static String PatientLabor_InfoExistingValueIsValid;
	public static String PatientLabor_InfoNewerResultAlreadyExists;
	public static String PatientLabor_InfoOverwriteRefRange;
	public static String PatientLabor_InfoOverwriteValue;
	public static String PatientLabor_kuerzelViollier;
	public static String PatientLabor_nameDokumentLaborParameter;
	public static String PatientLabor_nameViollierLabor;
	public static String PatientLabor_TextForComments;
	public static String PatientLabor_WarningRefRangeMismatch;
	
	public static String Preferences_undefiniert;
	public static String Preferences_UseGlobalSettings;
	public static String Preferences_GlobalSettings;
	public static String Preferences_LocalSettingsFor;
	public static String Preferences_MandantSettingsFor;
	public static String Preferences_JMedTransferJar;
	public static String Preferences_JMedTransferParam;
	public static String Preferences_DirDownload;
	public static String Preferences_DirArchive;
	public static String Preferences_DirError;
	public static String Preferences_ArchivePurgeInterval;
	public static String Preferences_DocumentCategory;
	public static String Preferences_SaveRefRange;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
