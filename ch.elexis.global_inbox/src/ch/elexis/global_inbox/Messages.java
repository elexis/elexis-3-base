package ch.elexis.global_inbox;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.global_inbox.messages"; //$NON-NLS-1$
	public static String Activator_noInbox;
	public static String InboxContentProvider_noInboxDefined;
	public static String InboxContentProvider_thereIsNoDocumentManagerHere;
	public static String InboxView_assign;
	public static String InboxView_assignThisDocument;
	public static String InboxView_assignxtoy;
	public static String InboxView_category;
	public static String InboxView_couldNotStart;
	public static String InboxView_delete;
	public static String InboxView_error;
	public static String InboxView_inbox;
	public static String InboxView_reallydelete;
	public static String InboxView_reload;
	public static String InboxView_reloadNow;
	public static String InboxView_thisreallydelete;
	public static String InboxView_title;
	public static String InboxView_view;
	public static String InboxView_viewThisDocument;
	public static String Preferences_directory;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
