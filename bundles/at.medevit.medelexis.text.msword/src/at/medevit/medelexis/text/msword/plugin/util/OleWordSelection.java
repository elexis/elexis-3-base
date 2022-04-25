/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.widgets.Display;

public class OleWordSelection extends OleWrapper {
	public OleWordSelection(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public OleWordFind getFind(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Find"); //$NON-NLS-1$
		return new OleWordFind(autoObj, display, manager);
	}

	public OleWordParagraphFormat getParagraphFormat(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("ParagraphFormat"); //$NON-NLS-1$
		return new OleWordParagraphFormat(autoObj, display, manager);
	}

	public OleWordRange getRange(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Range"); //$NON-NLS-1$
		return new OleWordRange(autoObj, display, manager);
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
