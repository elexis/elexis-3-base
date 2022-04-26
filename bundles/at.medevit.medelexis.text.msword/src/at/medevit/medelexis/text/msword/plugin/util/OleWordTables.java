/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordTables extends OleWrapper {
	public OleWordTables(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public OleWordTable add(OleWordRange range, long rows, long columns, OleWrapperManager manager) {
		Variant[] arguments = new Variant[3];
		arguments[0] = new Variant(range.oleObj);
		arguments[1] = new Variant(rows);
		arguments[2] = new Variant(columns);

		Variant oleVar = runInvoke("Add", arguments); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		arguments[0].dispose();
		arguments[1].dispose();
		arguments[2].dispose();
		return new OleWordTable(oleAuto, display, manager);
	}

	public OleWordCell getCell(long row, long column, OleWrapperManager manager) {
		Variant[] arguments = new Variant[2];
		arguments[0] = new Variant(row);
		arguments[1] = new Variant(column);

		Variant oleVar = runInvoke("Cell", arguments); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		arguments[0].dispose();
		arguments[1].dispose();
		return new OleWordCell(oleAuto, display, manager);
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
