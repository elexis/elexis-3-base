package ch.elexis.laborimport.hl7.universal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.laborimport.hl7.universal.messages"; //$NON-NLS-1$
	
	public static String Prefs_ImportDirectory;
	public static String Prefs_ImportAttachedFiles;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
