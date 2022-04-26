/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.widgets.Display;

public class OleWordShape extends OleWrapper {
	public OleWordShape(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public OleWordTextFrame getTextFrame(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("TextFrame"); //$NON-NLS-1$
		return new OleWordTextFrame(oleAuto, display, manager);
	}

	public OleWordLineFormat getLine(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Line"); //$NON-NLS-1$
		return new OleWordLineFormat(oleAuto, display, manager);
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
