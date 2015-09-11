package ch.elexis.connector.medicosearch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.connector.medicosearch.messages"; //$NON-NLS-1$
	
	public static String PrefsDescription;
	public static String PrefsConfigFile;
	public static String PrefsOpenConfig;
	public static String ConfigFile;
	public static String MedicosearchJar;
	public static String Warn_FilesMissing;
	public static String Warn_FilesMissingMsg;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
