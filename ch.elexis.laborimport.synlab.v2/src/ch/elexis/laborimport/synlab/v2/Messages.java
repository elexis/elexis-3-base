package ch.elexis.laborimport.synlab.v2;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.laborimport.synlab.v2.messages"; //$NON-NLS-1$
	
	public static String Prefs_DownloadDir;
	public static String Prefs_GDTExportDir;
	public static String SynlabImporter_Title;
	public static String SynlabImporter_Description;
	public static String SynlabImporter_FromFile;
	public static String SynlabImporter_AutoImport;
	public static String SynlabImporter_Browse;
	public static String SynlabImporter_ImportLocationNotResolvable;
	public static String SynlabImporter_NoModeSelected;
	public static String SynlabGDT_Label;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
