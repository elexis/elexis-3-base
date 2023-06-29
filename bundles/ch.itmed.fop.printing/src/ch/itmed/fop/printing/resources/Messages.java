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

public class Messages {
	private static ArrayList<String> docNames;
	public static final String BUNDLE_NAME = "ch.itmed.fop.printing.resources.messages"; //$NON-NLS-1$
	public static String GeneralPreferences_Document = ch.elexis.core.l10n.Messages.Core_Document;
	public static String GeneralPreferences_Printer = ch.elexis.core.l10n.Messages.GeneralPreferences_Printer;
	public static String GeneralPreferences_PaperFormat = ch.elexis.core.l10n.Messages.Core_Paper_Size;
	public static String GeneralPreferences_XslTemplate = ch.elexis.core.l10n.Messages.GeneralPreferences_XslTemplate;
	public static String GeneralPreferences_TextOrientation = ch.elexis.core.l10n.Messages.GeneralPreferences_TextOrientation;
	public static String GeneralPreferences_OrientationPortrait = ch.elexis.core.l10n.Messages.Core_Vertical;
	public static String GeneralPreferences_OrientationLandscape = ch.elexis.core.l10n.Messages.Core_Horizontal;
	public static String GeneralPreferences_SettingScope = ch.elexis.core.l10n.Messages.Core_Use_global_settings;
	public static String GeneralPreferences_BarcodScope = ch.elexis.core.l10n.Messages.Core_Barcode_global_settings;
	public static String GeneralPreferences_Default = ch.elexis.core.l10n.Messages.GeneralPreferences_Default;
	public static String GeneralPreferences_Custom = ch.elexis.core.l10n.Messages.GeneralPreferences_Custom;
	public static String TemplatePreferences_Description = ch.elexis.core.l10n.Messages.TemplatePreferences_Description;
	public static String TemplatePreferences_Title = ch.elexis.core.l10n.Messages.Core_Settings;
	public static String TemplatePreferences_SettingsStore = ch.elexis.core.l10n.Messages.TemplatePreferences_SettingsStore;
	public static String TemplatePreferences_SettingsStore_Global = ch.elexis.core.l10n.Messages.TemplatePreferences_SettingsStore_Global;
	public static String TemplatePreferences_SettingsStore_Local = ch.elexis.core.l10n.Messages.TemplatePreferences_SettingsStore_Local;
	public static String TemplatePreferences_TextOrientation = ch.elexis.core.l10n.Messages.TemplatePreferences_TextOrientation;
	public static String TemplatePreferences_TextBarCodeFormate = ch.elexis.core.l10n.Messages.TemplatePreferences_TextBarCodeFormate;
	public static String TemplatePreferences_TextOrientation_Horizontal = ch.elexis.core.l10n.Messages.Core_Horizontal;
	public static String TemplatePreferences_TextOrientation_Vertical = ch.elexis.core.l10n.Messages.Core_Vertical;
	public static String TemplatePreferences_TextOrientation_BarcodeElexis = ch.elexis.core.l10n.Messages.Core_BarcodeElexis;
	public static String TemplatePreferences_TextOrientation_BarcodePat = ch.elexis.core.l10n.Messages.Core_BarcodePat;
	public static String TemplatePreferences_XslSetting = ch.elexis.core.l10n.Messages.TemplatePreferences_XslSetting;
	public static String TemplatePreferences_XslFileChooser = ch.elexis.core.l10n.Messages.TemplatePreferences_XslFileChooser;
	public static String TemplatePreferences_XslFileChooser_XslFilter = ch.elexis.core.l10n.Messages.TemplatePreferences_XslFileChooser_XslFilter;
	public static String TemplatePreferences_XslFileChooser_AllFilesFilter = ch.elexis.core.l10n.Messages.Core_All_Files_Filter;
	public static String TemplatePreferences_PaperFormat_CheckBox = ch.elexis.core.l10n.Messages.TemplatePreferences_PaperFormat_CheckBox;
	public static String TemplatePreferences_PaperFormat_Label = ch.elexis.core.l10n.Messages.Core_Paper_Size;
	public static String TemplatePreferences_Page_Width = ch.elexis.core.l10n.Messages.TemplatePreferences_Page_Width;
	public static String TemplatePreferences_Page_Height = ch.elexis.core.l10n.Messages.TemplatePreferences_Page_Height;
	public static String TemplatePreferences_Page_MarginTop = ch.elexis.core.l10n.Messages.TemplatePreferences_Page_MarginTop;
	public static String TemplatePreferences_Page_MarginBottom = ch.elexis.core.l10n.Messages.TemplatePreferences_Page_MarginBottom;
	public static String TemplatePreferences_Page_MarginLeft = ch.elexis.core.l10n.Messages.TemplatePreferences_Page_MarginLeft;
	public static String TemplatePreferences_Page_MarginRight = ch.elexis.core.l10n.Messages.TemplatePreferences_Page_MarginRight;
	public static String TemplatePreferences_ResponsiblePharmacist = ch.elexis.core.l10n.Messages.TemplatePreferences_ResponsiblePharmacist;
	public static String DefaultError_Title = ch.elexis.core.l10n.Messages.Core_Error_while_printing;
	public static String DefaultError_Message = ch.elexis.core.l10n.Messages.DefaultError_Message;
	public static String Info_NoCase_Title = ch.elexis.core.l10n.Messages.Core_No_case_selected;
	public static String Info_NoCase_Message = ch.elexis.core.l10n.Messages.Info_NoCase_Message;
	public static String Info_NoConsultation_Title = ch.elexis.core.l10n.Messages.Info_NoConsultation_Title;
	public static String Info_NoConsultation_Message = ch.elexis.core.l10n.Messages.Info_NoConsultation_Message;
	public static String Info_NoContact_Title = ch.elexis.core.l10n.Messages.Core_No_contact_selected;
	public static String Info_NoContact_Message = ch.elexis.core.l10n.Messages.Info_NoContact_Message;
	public static String Info_NoAppointment_Title = ch.elexis.core.l10n.Messages.Info_NoAppointment_Title;
	public static String Info_NoAppointment_Message = ch.elexis.core.l10n.Messages.Info_NoAppointment_Message;
	public static String Info_NoPatient_Title = ch.elexis.core.l10n.Messages.Core_No_patient_selected;
	public static String Info_NoPatient_Message = ch.elexis.core.l10n.Messages.Info_NoPatient_Message;
	public static String Medication_FixedMedication = ch.elexis.core.l10n.Messages.Medication_FixedMedication;
	public static String Medication_ReserveMedication = ch.elexis.core.l10n.Messages.Medication_ReserveMedication;
	public static String Medication_Recipe = ch.elexis.core.l10n.Messages.Core_Prescription;
	public static String Medication_SelfDispensed = ch.elexis.core.l10n.Messages.Medication_SelfDispensed;
	public static String Medication_SymptomaticMedication = ch.elexis.core.l10n.Messages.Medication_SymptomaticMedication;
	public static String Medication_Dose_Morning = ch.elexis.core.l10n.Messages.Medication_Dose_Morning;
	public static String Medication_Dose_Midday = ch.elexis.core.l10n.Messages.Core_Midday;
	public static String Medication_Dose_Evening = ch.elexis.core.l10n.Messages.Core_Evening;
	public static String Medication_Dose_Night = ch.elexis.core.l10n.Messages.Medication_Dose_Night;
	public static String AppointmentCard_Name = ch.elexis.core.l10n.Messages.Agenda_Appointmentcard;
	public static String RecurringAppointmentsCard_Name = ch.elexis.core.l10n.Messages.RecurringAppointmentsCard_Name;
	public static String ArticleLabel_Name = ch.elexis.core.l10n.Messages.ArticleLabel_Name;
	public static String MedicationLabel_Name = ch.elexis.core.l10n.Messages.MedicationLabel_Name;
	public static String PatientLabel_Name = ch.elexis.core.l10n.Messages.PatientLabel_Name;
	public static String VersionedLabel_Name = ch.elexis.core.l10n.Messages.VersionedLabel_Name;
	public static String PatientAddressLabel_Name = ch.elexis.core.l10n.Messages.PatientAddressLabel_Name;
	public static String ContactAddressLabel_Name = ch.elexis.core.l10n.Messages.ContactAddressLabel_Name;
	public static String BarCodeLabel_Name = ch.elexis.core.l10n.Messages.BarCodeLabel_Name;

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
			docNames.add(BarCodeLabel_Name);
		}

		return docNames.get(i);
	}
}
