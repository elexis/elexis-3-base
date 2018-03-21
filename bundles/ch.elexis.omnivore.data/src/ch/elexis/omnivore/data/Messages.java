package ch.elexis.omnivore.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.omnivore.data.messages"; //$NON-NLS-1$
	public static String DocHandle_73;
	public static String DocHandle_cantReadCaption;
	public static String DocHandle_cantReadText;
	public static String DocHandle_configErrorCaption;
	public static String DocHandle_configErrorText;
	public static String DocHandle_couldNotLoadError;
	public static String DocHandle_fileNameTooLong;
	public static String DocHandle_importError2;
	public static String DocHandle_importErrorText;
	public static String DocHandle_importErrorText2;
	public static String DocHandle_importErrorDirectory;
	public static String DocHandle_importErrorDirectoryText;
	public static String DocHandle_loadErrorText;
	public static String DocHandle_noPatientSelected;
	public static String DocHandle_pleaseSelectPatient;
	public static String DocHandle_readError;
	public static String DocHandle_readErrorCaption;
	public static String DocHandle_readErrorCaption2;
	public static String DocHandle_readErrorCaption3;
	public static String DocHandle_readErrorHeading;
	public static String DocHandle_readErrorText;
	public static String DocHandle_readErrorText2;
	public static String DocHandle_runErrorHeading;
	public static String DocHandle_scannedImageDialogCaption;
	public static String DocHandle_writeErrorCaption;
	public static String DocHandle_writeErrorCaption2;
	public static String DocHandle_writeErrorHeading;
	public static String DocHandle_writeErrorText;
	public static String DocHandle_writeErrorText2;
	public static String Dochandle_errorCatNameAlreadyTaken;
	public static String DocHandle_errorCatNameAlreadyTakenMsg;
	
	
	public static String xChangeContributor_thisIsAnOmnivoreDoc;
	public static String DocumentManagement_contentsMatchNotSupported;
	public static String DocHandle_cantReadMessage;
	public static String DocHandle_importErrorCaption;
	public static String DocHandle_importErrorMessage2;
	public static String DocHandle_MoveErrorCaption;
	public static String DocHandle_MoveErrorDestIsDir;
	public static String DocHandle_MoveErrorDestIsFile;
	public static String DocHandle_importErrorMessage;
	public static String DocHandle_MoveError;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
