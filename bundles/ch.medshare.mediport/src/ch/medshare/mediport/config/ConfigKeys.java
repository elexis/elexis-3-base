package ch.medshare.mediport.config;

public interface ConfigKeys {

	public final static String TRUE = "true"; //$NON-NLS-1$
	public final static String FALSE = "false"; //$NON-NLS-1$

	public final static String GERMAN = "D"; //$NON-NLS-1$
	public final static String FRENCH = "F"; //$NON-NLS-1$
	public final static String ITALIAN = "I"; //$NON-NLS-1$

	public final static String CLIENT = "CLIENT"; //$NON-NLS-1$

	// General
	public final static String SENDER_EAN = "mpcommunicator.sender.ean"; //$NON-NLS-1$
	public final static String MEDIPORT_DN = "mediport.dn"; //$NON-NLS-1$
	public final static String MEDIPORT_IP = "mpcommunicator.mediport.ip"; //$NON-NLS-1$
	public final static String KEYSTORE_NAME = "keystore.name"; //$NON-NLS-1$

	// Client
	public final static String DIR = "DIR"; //$NON-NLS-1$
	public final static String EAN = "EAN"; //$NON-NLS-1$
	public final static String SEND_DIR = "SEND_DIR"; //$NON-NLS-1$
	public final static String RECEIVE_DIR = "RECEIVE_DIR"; //$NON-NLS-1$
	public final static String RECEIVETEST_DIR = "RECEIVETEST_DIR"; //$NON-NLS-1$
	public final static String ERROR_DIR = "ERROR_DIR"; //$NON-NLS-1$
	public final static String DOCSTAT_DIR = "DOCSTAT_DIR"; //$NON-NLS-1$
	public final static String PARTNER_FILE = "PARTNER_FILE"; //$NON-NLS-1$

	// Parameter Sets
	public final static String NAME = "NAME"; //$NON-NLS-1$
	public final static String DOCATTR = "DOCATTR"; //$NON-NLS-1$
	public final static String DOCPRINTED = "DOCPRINTED"; //$NON-NLS-1$
	public final static String DISTTYPE = "DISTTYPE"; //$NON-NLS-1$
	public final static String PRINTLANGUAGE = "PRINTLANGUAGE"; //$NON-NLS-1$
	public final static String TRUSTCENTEREAN = "TRUSTCENTEREAN"; //$NON-NLS-1$
	public final static String ISPAPERINVOICE = "ISPAPERINVOICE"; //$NON-NLS-1$
}
