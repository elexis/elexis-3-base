/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordPageSetup extends OleWrapper {
	public OleWordPageSetup(OleAutomation oleAuto, Display display, OleWrapperManager manager){
		super(oleAuto, display, manager);
	}
	
	public int getLeftMargin(){
		Variant value = runGetVariantProperty("LeftMargin"); //$NON-NLS-1$
		int ret = value.getInt();
		value.dispose();
		return ret;
	}
	
	public int getTopMargin(){
		Variant value = runGetVariantProperty("TopMargin"); //$NON-NLS-1$
		int ret = value.getInt();
		value.dispose();
		return ret;
	}
	
	public int getRightMargin(){
		Variant value = runGetVariantProperty("RightMargin"); //$NON-NLS-1$
		int ret = value.getInt();
		value.dispose();
		return ret;
	}
	
	public int getBottomMargin(){
		Variant value = runGetVariantProperty("BottomMargin"); //$NON-NLS-1$
		int ret = value.getInt();
		value.dispose();
		return ret;
	}

	public int getPageWidth(){
		Variant value = runGetVariantProperty("PageWidth"); //$NON-NLS-1$
		int ret = value.getInt();
		value.dispose();
		return ret;
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
