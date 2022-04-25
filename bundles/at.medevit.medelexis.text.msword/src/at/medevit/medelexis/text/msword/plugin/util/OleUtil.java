/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;

/**
 * Low level OLE/COM access.
 *
 * @author thomashu
 *
 */
public class OleUtil {
	public static OleAutomation getOleAutomationProperty(OleAutomation auto, String member) {
		Variant varResult = auto.getProperty(getMemberId(auto, member));
		OleAutomation ret = getOleAutomationFromVariant(varResult);
		if (varResult != null) {
			varResult.dispose();
		}
		return ret;
	}

	public static OleAutomation getOleAutomationProperty(OleAutomation auto, int idForMember) {
		Variant varResult = auto.getProperty(idForMember);
		OleAutomation ret = getOleAutomationFromVariant(varResult);
		if (varResult != null) {
			varResult.dispose();
		}
		return ret;
	}

	public static OleAutomation getOleAutomationFromVariant(Variant variant) {
		if (variant != null && variant.getType() != OLE.VT_EMPTY) {
			OleAutomation result = variant.getAutomation();
			return result;
		}
		return null;
	}

	public static Variant getVariantProperty(OleAutomation auto, String member) {
		return auto.getProperty(getMemberId(auto, member));
	}

	public static Variant getVariantProperty(OleAutomation auto, int idForMember) {
		return auto.getProperty(idForMember);
	}

	public static boolean setProperty(OleAutomation auto, String member, Variant value) {
		return auto.setProperty(getMemberId(auto, member), value);
	}

	public static boolean setProperty(OleAutomation auto, int idForMember, Variant value) {
		return auto.setProperty(idForMember, value);
	}

	public static Variant invoke(OleAutomation auto, String member) {
		return auto.invoke(getMemberId(auto, member));
	}

	public static Variant invoke(OleAutomation auto, int idForMember) {
		return auto.invoke(idForMember);
	}

	public static Variant invoke(OleAutomation auto, String member, Variant[] arguments) {
		return auto.invoke(getMemberId(auto, member), arguments);
	}

	public static Variant invoke(OleAutomation auto, int idForMember, Variant[] arguments) {
		return auto.invoke(idForMember, arguments);
	}

	public static Variant invoke(OleAutomation auto, String member, Variant[] arguments, int[] argumentsIds) {
		return auto.invoke(getMemberId(auto, member), arguments, argumentsIds);
	}

	public static Variant invoke(OleAutomation auto, int idForMember, Variant[] arguments, int[] argumentsIds) {
		return auto.invoke(idForMember, arguments, argumentsIds);
	}

	public static int getMemberId(OleAutomation auto, String name) {
		int[] ids = auto.getIDsOfNames(new String[] { name });
		if (ids != null && ids.length > 0) {
			return ids[0];
		} else {
			throw new IllegalArgumentException("OleUtil: could not find id for [" + name + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
