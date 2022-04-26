/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

public class OleWordShapes extends OleWrapper {

	public static final int msoTextOrientationHorizontal = 1;
	public static final int msoTextOrientationVertical = 5;
	public static final int msoTextOrientationDownward = 3;
	public static final int msoTextOrientationUpward = 2;

	public OleWordShapes(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
	}

	public OleWordShape addTextbox(final int orientation, final int x, final int y, final int width, final int height,
			OleWrapperManager manager) {
		Variant[] addArgs = new Variant[5];
		addArgs[0] = new Variant(orientation);
		addArgs[1] = new Variant(x);
		addArgs[2] = new Variant(y);
		addArgs[3] = new Variant(width);
		addArgs[4] = new Variant(height);

		Variant oleVar = runInvoke("AddTextbox", addArgs); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		addArgs[0].dispose();
		addArgs[1].dispose();
		addArgs[2].dispose();
		addArgs[3].dispose();
		addArgs[4].dispose();
		return new OleWordShape(oleAuto, display, manager);
	}

	public int getCount() {
		Variant oleVar = runGetVariantProperty("Count"); //$NON-NLS-1$
		int ret = (int) oleVar.getLong();
		oleVar.dispose();
		return ret;
	}

	public OleWordShape getItem(int index, OleWrapperManager manager) {
		Variant[] arguments = new Variant[1];
		arguments[0] = new Variant((long) index);

		Variant oleVar = runInvoke("Item", arguments); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		arguments[0].dispose();
		return new OleWordShape(oleAuto, display, manager);
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
