package ch.elexis.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.messages.messages"; //$NON-NLS-1$
	public static String MsgDetailDialog_asReminder;
	public static String MsgDetailDialog_cancel;
	public static String MsgDetailDialog_createMessage;
	public static String MsgDetailDialog_delete;
	public static String MsgDetailDialog_from;
	public static String MsgDetailDialog_message;
	public static String MsgDetailDialog_messageDated;
	public static String MsgDetailDialog_readMessage;
	public static String MsgDetailDialog_reply;
	public static String MsgDetailDialog_send;
	public static String MsgDetailDialog_to;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
