/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordFind extends OleWrapper {
	public static final int wdReplaceOne = 1;
	public static final int wdReplaceAll = 2;

	public OleWordFind(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public void setText(String text) {
		Variant value = new Variant(text);
		runSetProperty("Text", value); //$NON-NLS-1$
		value.dispose();
	}

	public void setForward(boolean bValue) {
		Variant value = new Variant(bValue);
		runSetProperty("Forward", value); //$NON-NLS-1$
		value.dispose();
	}

	public void setMatchWholeWord(boolean bValue) {
		Variant value = new Variant(bValue);
		runSetProperty("MatchWholeWord", value); //$NON-NLS-1$
		value.dispose();
	}

	public OleWordReplacement getReplacement(OleWrapperManager manager) {
		OleAutomation autoObj = runGetOleAutomationProperty("Replacement"); //$NON-NLS-1$
		return new OleWordReplacement(autoObj, display, manager);
	}

	public boolean execute() {
		Variant value = runInvoke("Execute"); //$NON-NLS-1$
		boolean ret = value.getBoolean();
		value.dispose();
		return ret;
	}

	public boolean execute(String find, String replace) {
		Variant[] arguments = new Variant[11];
		arguments[0] = new Variant(find); // FindText
		arguments[1] = new Variant(true); // MatchCase
		arguments[2] = new Variant(false); // MatchWholeWord
		arguments[3] = new Variant(false); // MatchWildcards
		arguments[4] = new Variant(false); // MatchSoundsLike
		arguments[5] = new Variant(false); // MatchAllWordForms
		arguments[6] = new Variant(true); // Forward
		arguments[7] = new Variant(false); // Wrap
		arguments[8] = new Variant(false); // Format
		arguments[9] = new Variant(replace); // ReplaceWith
		arguments[10] = new Variant(OleWordFind.wdReplaceOne); // Replace

		Variant value = runInvoke("Execute", arguments); //$NON-NLS-1$
		boolean ret = value.getBoolean();
		arguments[0].dispose();
		arguments[1].dispose();
		arguments[2].dispose();
		arguments[3].dispose();
		arguments[4].dispose();
		arguments[5].dispose();
		arguments[6].dispose();
		arguments[7].dispose();
		arguments[8].dispose();
		arguments[9].dispose();
		arguments[10].dispose();
		value.dispose();
		return ret;
	}

	public boolean execute(int replace) {
		Variant[] arguments = new Variant[1];
		arguments[0] = new Variant(replace);
		Variant value = runInvoke("Execute", arguments, new String[] { //$NON-NLS-1$
				"Replace" //$NON-NLS-1$
		});
		boolean ret = value.getBoolean();
		value.dispose();
		arguments[0].dispose();
		return ret;
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
