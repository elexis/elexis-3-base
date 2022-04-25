/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordParagraphFormat extends OleWrapper {
	public static final int wdAlignParagraphLeft = 0;
	public static final int wdAlignParagraphCenter = 1;
	public static final int wdAlignParagraphRight = 2;
	public static final int wdAlignParagraphJustify = 3;
	public static final int wdAlignParagraphDistribute = 4;

	public OleWordParagraphFormat(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public void setAlignment(int swtAlignment) {
		Variant value = null;
		try {
			value = new Variant(alignmentSwt2Word(swtAlignment));
			runSetProperty("Alignment", value); //$NON-NLS-1$
			value.dispose();
		} catch (IllegalStateException e) {
			if (value != null)
				value.dispose();
		}
	}

	private int alignmentSwt2Word(int swtAlignment) {
		switch (swtAlignment) {
		case SWT.LEFT:
			return wdAlignParagraphLeft;
		case SWT.RIGHT:
			return wdAlignParagraphRight;
		case SWT.CENTER:
			return wdAlignParagraphCenter;
		default:
			return wdAlignParagraphLeft;
		}
	}

	public void setSpaceAfter(int points) {
		Variant value = null;
		try {
			value = new Variant(points);
			runSetProperty("SpaceAfter", value);
			value.dispose();
		} catch (IllegalStateException e) {
			if (value != null)
				value.dispose();
		}
	}

	public void setSpaceBefore(int points) {
		Variant value = null;
		try {
			value = new Variant(points);
			runSetProperty("SpaceBefore", value);
			value.dispose();
		} catch (IllegalStateException e) {
			if (value != null)
				value.dispose();
		}
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
