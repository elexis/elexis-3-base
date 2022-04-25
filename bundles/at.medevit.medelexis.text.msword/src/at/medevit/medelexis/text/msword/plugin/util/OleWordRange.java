/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

/**
 * OleWrapper for accessing Word Range members.
 *
 * @author thomashu
 *
 */
public class OleWordRange extends OleWrapper {

	public OleWordRange(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public void insertAfter(String text) {
		Variant[] insertArgs = new Variant[1];
		insertArgs[0] = new Variant(text);

		runInvoke("InsertAfter", insertArgs); //$NON-NLS-1$
		insertArgs[0].dispose();
	}

	public String getText() {
		Variant value = runGetVariantProperty("Text"); //$NON-NLS-1$
		String ret = value.getString();
		value.dispose();
		return ret;
	}

	public OleWordParagraphFormat getParagraphFormat(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("ParagraphFormat"); //$NON-NLS-1$
		return new OleWordParagraphFormat(autoObj, display, manager);
	}

	public void setText(String text) {
		Variant value = new Variant(text);
		runSetProperty("Text", value); //$NON-NLS-1$
		value.dispose();
	}

	public OleWordFind getFind(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Find"); //$NON-NLS-1$
		return new OleWordFind(autoObj, display, manager);
	}

	public OleWordTables getTables(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Tables"); //$NON-NLS-1$
		return new OleWordTables(autoObj, display, manager);
	}

	public OleWordFont getFont(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Font"); //$NON-NLS-1$
		return new OleWordFont(autoObj, display, manager);
	}

	public void collapse() {
		Variant[] arguments = new Variant[1];
		arguments[0] = new Variant(OleWordConstants.wdCollapseEnd);
		runInvoke("Collapse", arguments); //$NON-NLS-1$
		arguments[0].dispose();
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
