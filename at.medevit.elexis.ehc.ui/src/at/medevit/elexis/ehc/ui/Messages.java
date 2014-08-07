package at.medevit.elexis.ehc.ui;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "at.medevit.elexis.ehc.ui.messages";//$NON-NLS-1$
	
	private Messages(){
		// Do not instantiate
	}
	
	public static String Btn_Display;
	public static String Dlg_ResolveError;
	public static String Dlg_ResolveErrorMsg;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}