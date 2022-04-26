/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.widgets.Display;

public class OleWordRow extends OleWrapper {
	public OleWordRow(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public OleWordCells getCells(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Cells"); //$NON-NLS-1$
		return new OleWordCells(oleAuto, display, manager);
	}

	public OleWordRange getRange(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Range"); //$NON-NLS-1$
		return new OleWordRange(oleAuto, display, manager);
	}

	public OleWordBorders getBorders(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Borders"); //$NON-NLS-1$
		return new OleWordBorders(oleAuto, display, manager);
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
