package ch.medshare.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.medshare.util.messages"; //$NON-NLS-1$
	public static String UtilFile_error_msg_invalidPath;
	public static String UtilFile_error_msg_creationFailed;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
