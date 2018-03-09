package org.iatrix.messwerte;

public interface Constants {
	public static final String CFG_BASE = "org.iatrix.messwerte";
	
	public static final String CFG_LOCAL_LABORS = CFG_BASE + "/local_labors";
	public static final String CFG_DEFAULT_LOCAL_LABORS = "";
	
	public static final String CFG_MESSWERTE_VIEW_COLUMN_WIDTH_PREFIX =
		CFG_BASE + "/messwerte_view_column_width_";
	
	public static final String CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS =
		CFG_BASE + "/messwerte_view_number_of_columns";
	public static final int CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS_DEFAULT = 7;
	
	/**
	 * boolean value whether ACLs have been initialized for the first time
	 */
	public static final String ACL_INITIALIZED = CFG_BASE + "/acl/initialized";
}