package at.medevit.ch.artikelstamm.medcalendar;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "at.medevit.ch.artikelstamm.medcalendar.messages"; //$NON-NLS-1$
	
	public static String MedCalFilter;
	public static String MedCalFilterAction;
	public static String MedCalFilterActionToolTip;
	public static String MedCalFilterActionDescription;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
	
}
