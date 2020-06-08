/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordTextFrame extends OleWrapper {
	public OleWordTextFrame(OleAutomation oleAuto, Display display, OleWrapperManager manager){
		super(oleAuto, display, manager);
	}
	
	public OleWordRange getTextRange(OleWrapperManager manager){
		OleAutomation oleAuto = runGetOleAutomationProperty("TextRange"); //$NON-NLS-1$
		return new OleWordRange(oleAuto, display, manager);
	}
	
	public boolean getHasText(){
		Variant oleVar = runGetVariantProperty("HasText"); //$NON-NLS-1$
		boolean ret = oleVar.getBoolean();
		oleVar.dispose();
		return ret;
	}
	
	public void setMarginLeft(int margin){
		Variant value = new Variant(margin);
		runSetProperty("MarginLeft", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setMarginTop(int margin){
		Variant value = new Variant(margin);
		runSetProperty("MarginTop", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setMarginRight(int margin){
		Variant value = new Variant(margin);
		runSetProperty("MarginRight", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setMarginBottom(int margin){
		Variant value = new Variant(margin);
		runSetProperty("MarginBottom", value); //$NON-NLS-1$
		value.dispose();
	}
	
	public void setAutoSize(boolean bool){
		Variant value = new Variant(bool);
		runSetProperty("AutoSize", value);
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
