package at.medevit.elexis.agenda.ui.handler;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
	public static String AgendaUI_Delete__delete;
	public static String AgendaUI_Delete_ask_delete_whole_series;
	public static String AgendaUI_Delete_ask_really_delete;
	public static String AgendaUI_Delete_delete;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
