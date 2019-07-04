package ch.itmed.fop.printing.xml.documents;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.rgw.io.Settings;

public final class PageProperties {
	public static Element setProperties(Document doc, String docName) {
		Settings settingsStore = SettingsProvider.getStore(docName);
		
		Element page = doc.createElement("Page");
		
		page.setAttribute("pageHeight", settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 5), "89") + "mm");
		page.setAttribute("pageWidth", settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 6), "57") + "mm");
		page.setAttribute("textOrientation", settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 7), "0"));
		page.setAttribute("marginTop", settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 8), "6") + "mm");
		page.setAttribute("marginBottom", settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 9), "6") + "mm");
		page.setAttribute("marginLeft", settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 10), "6") + "mm");
		page.setAttribute("marginRight", settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 11), "6") + "mm");
		
		return page;
	}
}
