package ch.elexis.global_inbox.core.util;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IDocument;

public class Constants {

	public static final String IMPORTFILETOOMNIVORE = "importFileToOmnivore";
	public static final String PREFERENCE_BRANCH = "plugins/global_inbox/"; //$NON-NLS-1$
	public static final String PREFERENCE_BRANCH_SERVER = "plugins/global_inbox_server/"; //$NON-NLS-1$
	public static final String PREF_DIR = PREFERENCE_BRANCH + "dir"; //$NON-NLS-1$
	public static final String STOREFSGLOBAL = PREFERENCE_BRANCH + "store_in_fs_global"; //$NON-NLS-1$
	public static final String PREF_DIR_DEFAULT = StringUtils.EMPTY;
	public static final String PREF_LAST_SELECTED_CATEGORY = PREFERENCE_BRANCH_SERVER + "last_selected_category"; //$NON-NLS-1$
}
