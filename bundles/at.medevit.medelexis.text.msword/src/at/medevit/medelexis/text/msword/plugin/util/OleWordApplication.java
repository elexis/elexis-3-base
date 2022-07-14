/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

import at.medevit.medelexis.text.msword.ui.MSWordPreferencePage;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class OleWordApplication extends OleWrapper {

	public static int WORD_VERSION = 0;

	private OleWordSite site;

	public OleWordApplication(OleWordSite site, OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
		this.site = site;

		if (WORD_VERSION == 0) {
			Variant value = runGetVariantProperty("Version"); //$NON-NLS-1$
			String ret = value.getString();
			value.dispose();
			if (ret.startsWith("14")) //$NON-NLS-1$
				WORD_VERSION = 2010;
			else if (ret.startsWith("12")) //$NON-NLS-1$
				WORD_VERSION = 2007;
		}

		// set some default options
		OleWordOptions options = getOptions(manager);
		if (!ConfigServiceHolder.getUser(MSWordPreferencePage.MSWORD_SPELLING_CHECK, false)) {
			options.setCheckGrammarAsYouType(false);
			options.setCheckGrammarWithSpelling(false);
			options.setCheckSpellingAsYouType(false);
			options.setSuggestSpellingCorrections(false);
		} else {
			options.setCheckGrammarAsYouType(false);
			options.setCheckGrammarWithSpelling(false);
			options.setCheckSpellingAsYouType(true);
			options.setSuggestSpellingCorrections(false);
		}

		// prevent dialog prompt if possible
		setDisplayAlerts(false);
		options.setDoNotPromptForConvert(true);
	}

	public OleWordDocument getActiveDocument(OleWrapperManager manager) {
		OleAutomation autoObj;
		try {
			autoObj = runGetOleAutomationProperty("ActiveDocument"); //$NON-NLS-1$
		} catch (IllegalStateException e) {
			return null;
		}
		return new OleWordDocument(site, autoObj, display, manager);
	}

	public OleWordSelection getSelection(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Selection"); //$NON-NLS-1$
		return new OleWordSelection(autoObj, display, manager);
	}

	public void setActivePrinter(String printerName) {
		Variant value = new Variant(printerName);
		runSetProperty("ActivePrinter", value); //$NON-NLS-1$
		value.dispose();
	}

	public OleWordWindow getActiveWindow(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("ActiveWindow"); //$NON-NLS-1$
		return new OleWordWindow(autoObj, display, manager);
	}

	public void setScreenUpdating(boolean value) {
		Variant var = new Variant(value);
		runSetProperty("ScreenUpdating", var); //$NON-NLS-1$
		var.dispose();
	}

	public void screenRefresh() {
		runInvoke("ScreenRefresh");
	}

	public void setDisplayAlerts(boolean value) {
		Variant var = new Variant(value);
		runSetProperty("DisplayAlerts", var); //$NON-NLS-1$
		var.dispose();
	}

	public String getActivePrinter() {
		Variant value = runGetVariantProperty("ActivePrinter"); //$NON-NLS-1$
		String ret = value.getString();
		value.dispose();
		return ret;
	}

	public OleWordOptions getOptions(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Options"); //$NON-NLS-1$
		return new OleWordOptions(autoObj, display, manager);
	}

	public OleWordDialogs getDialogs(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Dialogs"); //$NON-NLS-1$
		return new OleWordDialogs(autoObj, display, manager);
	}

	public OleWordDocuments getDocuments(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Documents"); //$NON-NLS-1$
		return new OleWordDocuments(site, autoObj, display, manager);
	}

	/**
	 * Get points for millimeters. Tried to call MillimetersToPoints method here but
	 * could not due to missing Single data type.
	 *
	 * @param millimeters
	 * @return
	 */
	public int getPoints(final int millimeters) {
		// 1 mm = 2.85 points
		return (int) (millimeters * 2.85);
	}

	protected static HashMap<String, Integer> memberIdMap = new HashMap<String, Integer>();

	@Override
	protected synchronized int getIdForMember(String member) {
		Integer id = memberIdMap.get(member);
		if (id == null) {
			id = OleUtil.getMemberId(oleObj, member);
			memberIdMap.put(member, id);
		}
		return id;
	}
}
