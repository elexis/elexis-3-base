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

package ch.itmed.fop.printing.preferences;

import java.util.ArrayList;
import java.util.HashMap;

public class PreferenceConstants {
	private static HashMap<String, ArrayList<String>> hm;
	private static ArrayList<String> docNames;

	private static final String SETTINGS_BASE_PATH = "itmed/fop/"; //$NON-NLS-1$

	public static final String SETTINGS_PROVIDER_PAGES = SETTINGS_BASE_PATH + "SettingsProviderPages"; //$NON-NLS-1$
	public static final String SETTINGS_PROVIDER_PRINTERS = SETTINGS_BASE_PATH + "SettingsProviderPrinters"; //$NON-NLS-1$
	public static final String SETTINGS_PROVIDER_TEMPLATES = SETTINGS_BASE_PATH + "SettingsProviderTemplates"; //$NON-NLS-1$

	public static final String APPOINTMENT_CARD = "AppointmentCard"; //$NON-NLS-1$
	public static final String RECURRING_APPOINTMENTS_CARD = "RecurringAppointmentsCard"; //$NON-NLS-1$
	public static final String ARTICLE_LABEL = "ArticleLabel"; //$NON-NLS-1$
	public static final String MEDICATION_LABEL = "MedicationLabel"; //$NON-NLS-1$
	public static final String PATIENT_LABEL = "PatientLabel"; //$NON-NLS-1$
	public static final String VERSIONED_LABEL = "VersionedLabel"; //$NON-NLS-1$
	public static final String PATIENT_ADDRESS_LABEL = "PatientAddressLabel"; //$NON-NLS-1$
	public static final String CONTACT_ADDRESS_LABEL = "ContactAddressLabel"; //$NON-NLS-1$
	public static final String BAR_CODE_LABEL = "BarCodeLabel"; //$NON-NLS-1$
	public static final String ARTICLE_MEDIC_LABEL = "ArticleMedicLabel"; //$NON-NLS-1$

	public static final int APPOINTMENT_CARD_ID = 0;
	public static final int RECURRING_APPOINTMENTS_CARD_ID = 1;
	public static final int ARTICLE_LABEL_ID = 2;
	public static final int MEDICATION_LABEL_ID = 3;
	public static final int PATIENT_LABEL_ID = 4;
	public static final int VERSIONED_LABEL_ID = 5;
	public static final int PATIENT_ADDRESSLABEL_ID = 6;
	public static final int CONTACT_ADDRESS_LABEL_ID = 7;
	public static final int BAR_CODE_LABEL_ID = 8;
	public static final int ARTICLE_MEDIC_LABEL_ID = 9;

	private static final String PRINTER_NAME = "/printer/name"; // 0 //$NON-NLS-1$
	private static final String XSL_TEMPLATE_PATH = "/xsl/templatePath"; // 1 //$NON-NLS-1$
	private static final String XSL_CUSTOM_FLAG = "/xsl/customFlag"; // 2 //$NON-NLS-1$
	private static final String PAGE_TEMPLATE = "/page/template"; // 3 //$NON-NLS-1$
	private static final String PAGE_CUSTOM_FLAG = "/page/customFlag"; // 4 //$NON-NLS-1$
	private static final String PAGE_HEIGHT = "/page/height"; // 5 //$NON-NLS-1$
	private static final String PAGE_WIDTH = "/page/width"; // 6 //$NON-NLS-1$
	private static final String PAGE_TEXT_ORIENTATION = "/page/textOrientation"; // 7 //$NON-NLS-1$
	private static final String PAGE_MARGIN_TOP = "/page/marginTop"; // 8 //$NON-NLS-1$
	private static final String PAGE_MARGIN_BOTTOM = "/page/marginBottom"; // 9 //$NON-NLS-1$
	private static final String PAGE_MARGIN_LEFT = "/page/marginLeft"; // 10 //$NON-NLS-1$
	private static final String PAGE_MARGIN_RIGHT = "/page/marginRight"; // 11 //$NON-NLS-1$
	private static final String SETTING_SCOPE = "/settingScope"; // 12 //$NON-NLS-1$
	private static final String MEDICATION_RESPONSIBLE_PHARMACIST = "medication/responsiblePharmacist"; // 13 //$NON-NLS-1$
	private static final String PAGE_TEXT_TEXTBARCODEFORMAT = "/page/textBarcodeFormat"; // 14 //$NON-NLS-1$

	/**
	 * @param docName
	 */
	public static ArrayList<String> getDocPreferenceConstants(String docName) {
		if (docNames == null) {
			initDocNames();
		}

		if (hm == null) {
			initPreferenceConstants();
		}

		return hm.get(docName);
	}

	public static String getDocPreferenceConstant(String docName, int preferenceIndex) {
		ArrayList<String> prefs = getDocPreferenceConstants(docName);

		return prefs.get(preferenceIndex);
	}

	public static String getDocumentName(int i) {
		if (docNames == null) {
			initDocNames();
		}
		return docNames.get(i);
	}

	// FIXME: Necessary?
	public static ArrayList<String> getDocumentNames() {
		if (docNames == null) {
			initDocNames();
		}
		return docNames;
	}

	private static void initDocNames() {
		docNames = new ArrayList<String>();
		docNames.add(APPOINTMENT_CARD);
		docNames.add(RECURRING_APPOINTMENTS_CARD);
		docNames.add(ARTICLE_LABEL);
		docNames.add(MEDICATION_LABEL);
		docNames.add(PATIENT_LABEL);
		docNames.add(VERSIONED_LABEL);
		docNames.add(PATIENT_ADDRESS_LABEL);
		docNames.add(CONTACT_ADDRESS_LABEL);
		docNames.add(BAR_CODE_LABEL);
		docNames.add(ARTICLE_MEDIC_LABEL);
	}

	private static void initPreferenceConstants() {
		hm = new HashMap<String, ArrayList<String>>();

		for (String docName : docNames) {
			ArrayList<String> constants = new ArrayList<>();
			constants.add(SETTINGS_BASE_PATH + docName + PRINTER_NAME);
			constants.add(SETTINGS_BASE_PATH + docName + XSL_TEMPLATE_PATH);
			constants.add(SETTINGS_BASE_PATH + docName + XSL_CUSTOM_FLAG);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_TEMPLATE);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_CUSTOM_FLAG);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_HEIGHT);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_WIDTH);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_TEXT_ORIENTATION);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_MARGIN_TOP);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_MARGIN_BOTTOM);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_MARGIN_LEFT);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_MARGIN_RIGHT);
			constants.add(SETTINGS_BASE_PATH + docName + SETTING_SCOPE);
			constants.add(SETTINGS_BASE_PATH + docName + PAGE_TEXT_TEXTBARCODEFORMAT);

			if (docName.equals(BAR_CODE_LABEL)) {
				constants.add(SETTINGS_BASE_PATH + docName + PAGE_TEXT_TEXTBARCODEFORMAT);
			}
			if (docName.equals(MEDICATION_LABEL)) {
				constants.add(SETTINGS_BASE_PATH + docName + MEDICATION_RESPONSIBLE_PHARMACIST);
			}

			hm.put(docName, constants);
		}
	}
}
