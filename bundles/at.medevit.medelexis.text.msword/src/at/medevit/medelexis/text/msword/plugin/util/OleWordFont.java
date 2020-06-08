/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordFont extends OleWrapper {
	
	public OleWordFont(OleAutomation oleAuto, Display display, OleWrapperManager manager){
		super(oleAuto, display, manager);
	}

	public void setBold(boolean bold){
		Variant value = new Variant(bold);
		runSetProperty("Bold", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setItalic(boolean italic){
		Variant value = new Variant(italic);
		runSetProperty("Italic", value); //$NON-NLS-1$
		value.dispose();
	}

	public void setSize(int size){
		Variant value = new Variant(size);
		runSetProperty("Size", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setName(String name){
		Variant value = new Variant(name);
		runSetProperty("Name", value); //$NON-NLS-1$
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
