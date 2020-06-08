/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordBorders extends OleWrapper {

	public OleWordBorders(OleAutomation oleAuto, Display display, OleWrapperManager manager){
		super(oleAuto, display, manager);
	}
	
	public OleWordCell getItem(long index, OleWrapperManager manager){
		Variant[] arguments = new Variant[1];
		arguments[0] = new Variant(index);
		
		Variant oleVar = runInvoke("Item", arguments); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		arguments[0].dispose();
		return new OleWordCell(oleAuto, display, manager);
	}
	
	public long getCount(){
		Variant oleVar = runGetVariantProperty("Count"); //$NON-NLS-1$
		long ret = oleVar.getLong();
		oleVar.dispose();
		return ret;
	}
	
	public void setOutsideLineStyle(int wdLineStyle){
		Variant value = new Variant(wdLineStyle);
		runSetProperty("OutsideLineStyle", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setInsideLineStyle(int wdLineStyle){
		Variant value = new Variant(wdLineStyle);
		runSetProperty("InsideLineStyle", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public OleWordBorder getItem(int wdBorderType, OleWrapperManager manager){
		Variant[] arguments = new Variant[1];
		arguments[0] = new Variant(wdBorderType);

		Variant oleVar = runInvoke("Item", arguments); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		arguments[0].dispose();
		return new OleWordBorder(oleAuto, display, manager);
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
