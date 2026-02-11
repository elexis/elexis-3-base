package ch.elexis.pdfBills.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
	public static String ElexisPDFGenerator_PrintScriptExists;
	public static String ElexisPDFGenerator_PrintScriptExistsPart1;
	public static String ElexisPDFGenerator_PrintScriptExistsPart2;
	public static String ElexisPDFGenerator_QRAmountError;
	public static String ElexisPDFGenerator_QRCheckAddress;
	public static String ElexisPDFGenerator_QRCodeError;
	public static String ElexisPDFGenerator_QRCreateError;
	public static String ElexisPDFGenerator_QRCreditorError;
	public static String ElexisPDFGenerator_QRDebitorError;
	public static String ElexisPDFGenerator_QRDebitorErrorNone;
	public static String ElexisPDFGenerator_QRRemarkError;
	public static String ElexisPDFGenerator_TypeReferrer;
	public static String ElexisPDFGenerator_TypeServiceProvider;
	public static String ElexisPDFGenerator_TypeServiceEmployer;
	public static String ElexisPDFGenerator_TypeServiceLeadDoctor;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
