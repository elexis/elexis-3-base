// Copyright 2010 (c) Niklaus Giger <niklaus.giger@member.fsf.org>
package ch.elexis.laborimport.synlab;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.laborimport.synlab.messages"; //$NON-NLS-1$
	public static String PreferencePage_DownloadDir;
	public static String PreferencePage_JMedTrasferJar;
	public static String PreferencePage_JMedTrasferJni;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
