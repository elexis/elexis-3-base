package at.medevit.medelexis.text.msword;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "at.medevit.medelexis.text.msword.messages"; //$NON-NLS-1$
	public static String MissingConversionDialog_OfficeHome;
	public static String MissingConversionDialog_OfficeHomeConfig;
	public static String MissingConversionDialog_OfficeHomeMessage;
	public static String MissingConversionDialog_OfficeHomeNoValidPath;
	public static String MSWordPreferencePage_EnableSpellcheck;
	public static String MSWordPreferencePage_ConvertAll;
	public static String MSWordPreferencePage_ConvertDone;
	public static String MSWordPreferencePage_ConvertDone_0;
	public static String MSWordPreferencePage_ConvertDone_1;
	public static String MSWordPreferencePage_ConvertDone_2;
	public static String MSWordPreferencePage_ConvertLetter;
	public static String MSWordPreferencePage_ConvertLetters;
	public static String MSWordPreferencePage_LoadLetter;
	public static String MSWordPreferencePage_StartConvert;
	public static String OleWordSite_CouldNotStartWord;
	public static String OleWordSite_CouldNotActivate;
	public static String OleWordSite_CouldNotActivateCheck;
	public static String WordTextPlugin_NoActiveWordView;
	public static String WordTextPlugin_PrintError;
	public static String WordTextPlugin_PrintConnectionIssue;
	public static String WordTextPlugin_SelectAnotherPrinter;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
