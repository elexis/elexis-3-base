/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordHeadersFooters extends OleWrapper {
	public OleWordHeadersFooters(OleAutomation oleAuto, Display display, OleWrapperManager manager){
		super(oleAuto, display, manager);
	}
	
	public int getCount(){
		Variant oleVar = runGetVariantProperty("Count"); //$NON-NLS-1$
		int ret = (int) oleVar.getLong();
		oleVar.dispose();
		return ret;
	}
	
	public OleWordHeaderFooter getItem(int index, OleWrapperManager manager){
		Variant[] arguments = new Variant[1];
		arguments[0] = new Variant((long) index);
		
		Variant oleVar = runInvoke("Item", arguments); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		arguments[0].dispose();
		return new OleWordHeaderFooter(oleAuto, display, manager);
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
