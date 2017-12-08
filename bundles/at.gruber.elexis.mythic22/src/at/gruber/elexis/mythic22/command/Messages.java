package at.gruber.elexis.mythic22.command;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "at.gruber.elexis.mythic22.command.messages"; //$NON-NLS-1$
	public static String ServerControl_1;
	public static String ServerControl_3;
	public static String ServerControl_5;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
