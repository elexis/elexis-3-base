package ch.elexis.global_inbox.core.util;

import org.apache.commons.lang3.StringUtils;


public class Constants {

	public static final String IMPORTFILETOOMNIVORE = "importFileToOmnivore"; //$NON-NLS-1$
	public static final String PREFERENCE_BRANCH_SERVER = "plugins/global_inbox_server/"; //$NON-NLS-1$
	public static final String PREF_DIR = PREFERENCE_BRANCH_SERVER + "dir"; //$NON-NLS-1$
	public static final String STOREFSGLOBAL = PREFERENCE_BRANCH_SERVER + "store_in_fs_global"; //$NON-NLS-1$
	public static final String PREF_DIR_DEFAULT = StringUtils.EMPTY; // $NON-NLS-1$
	public static final String PREF_LAST_SELECTED_CATEGORY = PREFERENCE_BRANCH_SERVER + "last_selected_category"; //$NON-NLS-1$
	public static final String PREF_DEVICE_DIR_PREFIX = PREFERENCE_BRANCH_SERVER + "device_dir_"; //$NON-NLS-1$
	public static final String PREF_DEVICES = PREFERENCE_BRANCH_SERVER + "devices"; //$NON-NLS-1$
	public static final String PREF_SELECTED_DEVICE = PREFERENCE_BRANCH_SERVER + "selectedDevice"; //$NON-NLS-1$
	public static final String PREF_CATEGORY_PREFIX = PREFERENCE_BRANCH_SERVER + "categories_"; //$NON-NLS-1$
	public static final String PREF_PATIENT_STRATEGY_PREFIX = "global_inbox/patient_strategy_"; //$NON-NLS-1$

}
