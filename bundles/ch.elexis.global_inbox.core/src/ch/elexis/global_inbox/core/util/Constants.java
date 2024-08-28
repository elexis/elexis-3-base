package ch.elexis.global_inbox.core.util;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IDocument;

public class Constants {

	public static final String IMPORTFILETOOMNIVORE = "importFileToOmnivore";
	public static final String PREFERENCE_BRANCH = "plugins/global_inbox/"; //$NON-NLS-1$
	public static final String PREF_DIR = PREFERENCE_BRANCH + "dir"; //$NON-NLS-1$
	public static final String PREF_AUTOBILLING = PREFERENCE_BRANCH + "autobilling"; //$NON-NLS-1$
	public static final String STOREFSGLOBAL = PREFERENCE_BRANCH + "store_in_fs_global"; //$NON-NLS-1$
	public static final String PREF_INFO_IN_INBOX = PREFERENCE_BRANCH + "infoToInbox"; //$NON-NLS-1$
	public static final String PREF_DIR_DEFAULT = StringUtils.EMPTY;
}
