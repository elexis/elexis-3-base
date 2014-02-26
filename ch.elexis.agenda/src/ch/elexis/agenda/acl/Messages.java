package ch.elexis.agenda.acl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.agenda.acl.messages"; //$NON-NLS-1$
	public static String ACLContributor_acl_administer;
	public static String ACLContributor_acl_changeAppointments;
	public static String ACLContributor_acl_daylimits;
	public static String ACLContributor_acl_deleteAppointments;
	public static String ACLContributor_acl_lockappointments;
	public static String ACLContributor_acl_showAppointments;
	public static String ACLContributor_acl_use;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
