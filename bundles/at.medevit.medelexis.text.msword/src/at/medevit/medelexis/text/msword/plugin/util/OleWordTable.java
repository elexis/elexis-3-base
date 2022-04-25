/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordTable extends OleWrapper {
	public OleWordTable(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public OleWordRows getRows(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Rows"); //$NON-NLS-1$
		return new OleWordRows(oleAuto, display, manager);
	}

	public OleWordColumns getColumns(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Columns"); //$NON-NLS-1$
		return new OleWordColumns(oleAuto, display, manager);
	}

	public OleWordBorders getBorders(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Borders"); //$NON-NLS-1$
		return new OleWordBorders(oleAuto, display, manager);
	}

	public void setApplyStyleHeadingRows(boolean value) {
		Variant varValue = new Variant(value);
		runSetProperty("ApplyStyleHeadingRows", varValue); //$NON-NLS-1$
		varValue.dispose();
	}

	public void setStyle(int wdBuiltinStyle) {
		Variant value = new Variant(wdBuiltinStyle);
		runSetProperty("Style", value); //$NON-NLS-1$
		value.dispose();
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
