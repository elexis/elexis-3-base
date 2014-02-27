package at.medevit.elexis.weblinks.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "at.medevit.elexis.weblinks.preferences.messages"; //$NON-NLS-1$
	public static String WebLinkEditor_0;
	public static String WebLinkEditor_1;
	public static String WebLinkEditor_2;
	public static String WebLinkPreferencePage_0;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
