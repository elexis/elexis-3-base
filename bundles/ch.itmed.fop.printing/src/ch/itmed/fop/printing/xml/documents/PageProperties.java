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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;

public final class PageProperties {
	public static Element setProperties(Document doc, String docName) {
		IPreferenceStore settingsStore = SettingsProvider.getStore(docName);
		
		Element page = doc.createElement("Page");
		
		// set default values
		settingsStore.setDefault(PreferenceConstants.getDocPreferenceConstant(docName, 5), "89");
		settingsStore.setDefault(PreferenceConstants.getDocPreferenceConstant(docName, 6), "57");
		settingsStore.setDefault(PreferenceConstants.getDocPreferenceConstant(docName, 7), "0");
		settingsStore.setDefault(PreferenceConstants.getDocPreferenceConstant(docName, 8), "6");
		settingsStore.setDefault(PreferenceConstants.getDocPreferenceConstant(docName, 9), "6");
		settingsStore.setDefault(PreferenceConstants.getDocPreferenceConstant(docName, 10), "6");
		settingsStore.setDefault(PreferenceConstants.getDocPreferenceConstant(docName, 11), "6");
		
		page.setAttribute("pageHeight",
			settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 5))
				+ "mm");
		page.setAttribute("pageWidth",
			settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 6))
				+ "mm");
		page.setAttribute("textOrientation",
			settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 7)));
		page.setAttribute("marginTop",
			settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 8))
				+ "mm");
		page.setAttribute("marginBottom",
			settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 9))
				+ "mm");
		page.setAttribute("marginLeft",
			settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 10))
				+ "mm");
		page.setAttribute("marginRight",
			settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 11))
				+ "mm");
		
		return page;
	}
	
	public static void setCurrentDate(Element page){
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(ZoneId.systemDefault());
		String currentDate = formatter.format(localDate);
		
		page.setAttribute("currentDate", currentDate);
	}
}
