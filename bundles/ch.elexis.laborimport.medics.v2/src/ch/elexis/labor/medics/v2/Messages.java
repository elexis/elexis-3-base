/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.labor.medics.v2;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.laborimport.medics.v2.messages";
	public static String KontaktOrderManagement_messageErrorCreateDB;
	public static String KontaktOrderManagement_titleErrorCreateDB;
	public static String LabOrderAction_errorMessageNoFallSelected;
	public static String LabOrderAction_errorMessageNoPatientSelected;
	public static String LabOrderAction_errorTitleCannotCreateHL7;
	public static String LabOrderAction_errorTitleCannotShowURL;
	public static String LabOrderAction_errorTitleNoFallSelected;
	public static String LabOrderAction_errorTitleNoPatientSelected;
	public static String LabOrderAction_infoMessageLabOrderFinshed;
	public static String LabOrderAction_infoTitleLabOrderFinshed;
	public static String LabOrderAction_nameAction;
	public static String LabOrderAction_receivingApplication;
	public static String LabOrderAction_receivingFacility;
	public static String LabOrderImport_descriptionImport;
	public static String LabOrderImport_errorMsgVerarbeitung;
	public static String LabOrderImport_errorTitle;
	public static String LabOrderImport_labelDocumentCategory;
	public static String LabOrderImport_labelDownloadDir;
	public static String LabOrderImport_monitorImportiereHL7;
	public static String LabOrderImport_titleImport;
	public static String MedicsBrowserView_errorOpeningBrowserURL;
	public static String MedicsPreferencePage_defaultMedicsUrl;
	public static String MedicsPreferencePage_documentCategoryName;
	public static String MedicsPreferencePage_labelArchivDir;
	public static String MedicsPreferencePage_labelDocumentCategory;
	public static String MedicsPreferencePage_labelDownloadDir;
	public static String MedicsPreferencePage_labelErrorDir;
	public static String MedicsPreferencePage_labelUploadDir;
	public static String MedicsPreferencePage_labelUploadDirimed;
	public static String PatientLabor_errorAddingDocument;
	public static String PatientLabor_errorCreatingTmpDir;
	public static String PatientLabor_errorKeineDokumentablage;
	public static String PatientLabor_kuerzelMedics;
	public static String PatientLabor_nameDokumentLaborParameter;
	public static String PatientLabor_nameMedicsLabor;
	public static String iMedAction_nameAction;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
