/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordBorder extends OleWrapper {
	public OleWordBorder(OleAutomation oleAuto, Display display, OleWrapperManager manager){
		super(oleAuto, display, manager);
	}

	public void setLineStyle(int wdLineStyle){
		Variant value = new Variant(wdLineStyle);
		runSetProperty("LineStyle", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setLineWidth(int wdLineWidth){
		Variant value = new Variant(wdLineWidth);
		runSetProperty("LineWidth", value); //$NON-NLS-1$
		value.dispose();
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
