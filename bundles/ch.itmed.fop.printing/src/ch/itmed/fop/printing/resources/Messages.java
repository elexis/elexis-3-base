/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.resources;

import java.util.ArrayList;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static ArrayList<String> docNames;
	public static final String BUNDLE_NAME = "ch.itmed.fop.printing.resources.messages"; //$NON-NLS-1$

	public static String GeneralPreferences_Document;
	public static String GeneralPreferences_Printer;
	public static String GeneralPreferences_PaperFormat;
	public static String GeneralPreferences_XslTemplate;
	public static String GeneralPreferences_TextOrientation;
	public static String GeneralPreferences_OrientationPortrait;
	public static String GeneralPreferences_OrientationLandscape;
	public static String GeneralPreferences_SettingScope;
	public static String GeneralPreferences_Default;
	public static String GeneralPreferences_Custom;
	
	public static String TemplatePreferences_Description;
	public static String TemplatePreferences_Title;	
	public static String TemplatePreferences_SettingsStore;
	public static String TemplatePreferences_SettingsStore_Global;
	public static String TemplatePreferences_SettingsStore_Local;
	public static String TemplatePreferences_TextOrientation;
	public static String TemplatePreferences_TextOrientation_Horizontal;
	public static String TemplatePreferences_TextOrientation_Vertical;
	public static String TemplatePreferences_XslSetting;
	public static String TemplatePreferences_XslFileChooser;
	public static String TemplatePreferences_XslFileChooser_XslFilter;
	public static String TemplatePreferences_XslFileChooser_AllFilesFilter;
	public static String TemplatePreferences_PaperFormat_CheckBox;
	public static String TemplatePreferences_PaperFormat_Label;
	public static String TemplatePreferences_Page_Width;
	public static String TemplatePreferences_Page_Height;
	public static String TemplatePreferences_Page_MarginTop;
	public static String TemplatePreferences_Page_MarginBottom;
	public static String TemplatePreferences_Page_MarginLeft;
	public static String TemplatePreferences_Page_MarginRight;
	public static String TemplatePreferences_ResponsiblePharmacist;
	
	public static String DefaultError_Title;
	public static String DefaultError_Message;
	
	public static String Info_NoCase_Title;
	public static String Info_NoCase_Message;
	public static String Info_NoConsultation_Title;
	public static String Info_NoConsultation_Message;
	public static String Info_NoContact_Title;
	public static String Info_NoContact_Message;
	public static String Info_NoAppointment_Title;
	public static String Info_NoAppointment_Message;
	public static String Info_NoPatient_Title;
	public static String Info_NoPatient_Message;
	
	public static String Medication_FixedMedication;
	public static String Medication_ReserveMedication;
	public static String Medication_Recipe;
	public static String Medication_SelfDispensed;
	public static String Medication_SymptomaticMedication;
	public static String Medication_Dose_Morning;
	public static String Medication_Dose_Midday;
	public static String Medication_Dose_Evening;	
	public static String Medication_Dose_Night;	
	
	public static String AppointmentCard_Name;
	public static String RecurringAppointmentsCard_Name;
	public static String ArticleLabel_Name;
	public static String MedicationLabel_Name;
	public static String PatientLabel_Name;
	public static String VersionedLabel_Name;
	public static String PatientAddressLabel_Name;
	public static String ContactAddressLabel_Name;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String getDocumentName(int i) {
		if (docNames == null) {
			docNames = new ArrayList<String>();

			docNames.add(AppointmentCard_Name);
			docNames.add(RecurringAppointmentsCard_Name);
			docNames.add(ArticleLabel_Name);
			docNames.add(MedicationLabel_Name);
			docNames.add(PatientLabel_Name);
			docNames.add(VersionedLabel_Name);
			docNames.add(PatientAddressLabel_Name);
			docNames.add(ContactAddressLabel_Name);
		}

		return docNames.get(i);
	}
}
