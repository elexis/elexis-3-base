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

package ch.itmed.fop.printing.xml.documents;

import static ch.elexis.core.constants.Preferences.SETTINGS_PREFERENCE_STORE_DEFAULT;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;

public final class PageProperties {
	public static Element setProperties(Document doc, String docName) {
		boolean globalSetting = Setting.isGlobalSetting(docName);

		Element page = doc.createElement("Page"); //$NON-NLS-1$

		initDefaults(docName, globalSetting);

		page.setAttribute("pageHeight", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 5)) + "mm"); //$NON-NLS-1$
		page.setAttribute("pageWidth", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 6)) + "mm"); //$NON-NLS-1$
		page.setAttribute("textOrientation", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 7)));
		page.setAttribute("marginTop", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 8)) + "mm"); //$NON-NLS-1$
		page.setAttribute("marginBottom", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 9)) + "mm"); //$NON-NLS-1$
		page.setAttribute("marginLeft", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 10)) + "mm"); //$NON-NLS-1$
		page.setAttribute("marginRight", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 11)) + "mm"); //$NON-NLS-1$

		return page;
	}

	private static void initDefaults(String docName, boolean globalSetting) {
		// set default values
		if (globalSetting) {
			ConfigServiceHolder.get().set(
					PreferenceConstants.getDocPreferenceConstant(docName, 5) + SETTINGS_PREFERENCE_STORE_DEFAULT, "89"); //$NON-NLS-1$
			ConfigServiceHolder.get().set(
					PreferenceConstants.getDocPreferenceConstant(docName, 6) + SETTINGS_PREFERENCE_STORE_DEFAULT, "57"); //$NON-NLS-1$
			ConfigServiceHolder.get().set(
					PreferenceConstants.getDocPreferenceConstant(docName, 7) + SETTINGS_PREFERENCE_STORE_DEFAULT, "0"); //$NON-NLS-1$
			ConfigServiceHolder.get().set(
					PreferenceConstants.getDocPreferenceConstant(docName, 8) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
			ConfigServiceHolder.get().set(
					PreferenceConstants.getDocPreferenceConstant(docName, 9) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
			ConfigServiceHolder.get().set(
					PreferenceConstants.getDocPreferenceConstant(docName, 10) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
			ConfigServiceHolder.get().set(
					PreferenceConstants.getDocPreferenceConstant(docName, 11) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
		} else {
			ConfigServiceHolder.get().setLocal(
					PreferenceConstants.getDocPreferenceConstant(docName, 5) + SETTINGS_PREFERENCE_STORE_DEFAULT, "89"); //$NON-NLS-1$
			ConfigServiceHolder.get().setLocal(
					PreferenceConstants.getDocPreferenceConstant(docName, 6) + SETTINGS_PREFERENCE_STORE_DEFAULT, "57"); //$NON-NLS-1$
			ConfigServiceHolder.get().setLocal(
					PreferenceConstants.getDocPreferenceConstant(docName, 7) + SETTINGS_PREFERENCE_STORE_DEFAULT, "0"); //$NON-NLS-1$
			ConfigServiceHolder.get().setLocal(
					PreferenceConstants.getDocPreferenceConstant(docName, 8) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
			ConfigServiceHolder.get().setLocal(
					PreferenceConstants.getDocPreferenceConstant(docName, 9) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
			ConfigServiceHolder.get().setLocal(
					PreferenceConstants.getDocPreferenceConstant(docName, 10) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
			ConfigServiceHolder.get().setLocal(
					PreferenceConstants.getDocPreferenceConstant(docName, 11) + SETTINGS_PREFERENCE_STORE_DEFAULT, "6"); //$NON-NLS-1$
		}

	}

	public static void setCurrentDate(Element page) {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(ZoneId.systemDefault()); //$NON-NLS-1$
		String currentDate = formatter.format(localDate);

		page.setAttribute("currentDate", currentDate); //$NON-NLS-1$
	}
}
