/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordColumn extends OleWrapper {
	public OleWordColumn(OleAutomation oleAuto, Display display, OleWrapperManager manager){
		super(oleAuto, display, manager);
	}
	
	public OleWordCells getCells(OleWrapperManager manager){
		OleAutomation oleAuto = runGetOleAutomationProperty("Cells"); //$NON-NLS-1$
		return new OleWordCells(oleAuto, display, manager);
	}
	
	public void setWidth(int points){
		Variant[] arguments = new Variant[2];
		arguments[0] = new Variant(points);
		arguments[1] = new Variant(OleWordConstants.wdAdjustNone);

		runInvoke("SetWidth", arguments); //$NON-NLS-1$
		arguments[0].dispose();
		arguments[1].dispose();
	}
	
	protected static HashMap<String, Integer> memberIdMap = new HashMap<String, Integer>();
	
	@Override
	protected synchronized int getIdForMember(String member){
		Integer id = memberIdMap.get(member);
		if (id == null) {
			id = OleUtil.getMemberId(oleObj, member);
			memberIdMap.put(member, id);
		}
		return id;
	}
}
