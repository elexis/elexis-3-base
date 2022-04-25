/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordReplacement extends OleWrapper {
	public OleWordReplacement(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public void setText(String text) {
		Variant value = new Variant(text);
		runSetProperty("Text", value); //$NON-NLS-1$
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
