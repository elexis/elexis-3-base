package org.iatrix.messwerte.acl;

import org.iatrix.messwerte.Activator;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AccessControlDefaults;

public interface ACL {
	// access control tokens
	
	static final String MESSWERT = "Messwert";
	static final String INT_LAB = "Internes Labor";
	static final String EXT_LAB = "Externes Labor";
	static final String EDIT = "Ändern";
	static final String VALUE = "Wert";
	static final String PATH_STATE = "Path. Zustand";
	
	// access control paths
	public static final ACE MESSWERT_DELETE =
		new ACE(AccessControlDefaults.DELETE, Activator.PLUGIN_ID, MESSWERT);
	public static final ACE MESSWERT_EDIT =
		new ACE(AccessControlDefaults.DATA, Activator.PLUGIN_ID, MESSWERT);
	
	public static final ACE DELETE_MESSWERT_INT_LAB =
		new ACE(MESSWERT_DELETE, "deleteIntLab", INT_LAB);
	public static final ACE DELETE_MESSWERT_EXT_LAB =
		new ACE(MESSWERT_DELETE, "deleteExtLab", EXT_LAB);
	public static final ACE DATA_MESSWERT_EDIT_INT_LAB_VALUE =
		new ACE(MESSWERT_EDIT, "editIntLabValue", INT_LAB + ": " + EDIT);
	public static final ACE DATA_MESSWERT_EDIT_INT_LAB_PATH_STATE =
		new ACE(MESSWERT_EDIT, "editIntLabState", INT_LAB + ": " + PATH_STATE);
	public static final ACE DATA_MESSWERT_EDIT_EXT_LAB_VALUE =
		new ACE(MESSWERT_EDIT, "editExtLabValue", EXT_LAB + ": " + EDIT);
	public static final ACE DATA_MESSWERT_EDIT_EXT_LAB_PATH_STATE =
		new ACE(MESSWERT_EDIT, "editExtLabState", EXT_LAB + ": " + PATH_STATE);
}
