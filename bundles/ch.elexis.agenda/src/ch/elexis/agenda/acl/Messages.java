package ch.elexis.agenda.acl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.base.l10n.messages";
	public static String  ACLContributor_acl_administer = ch.elexis.base.l10n.Messages.ACLContributor_acl_administer;
	public static String  ACLContributor_acl_changeAppointments = ch.elexis.base.l10n.Messages.ACLContributor_acl_changeAppointments;
	public static String  ACLContributor_acl_daylimits = ch.elexis.base.l10n.Messages.ACLContributor_acl_daylimits;
	public static String  ACLContributor_acl_deleteAppointments = ch.elexis.base.l10n.Messages.ACLContributor_acl_deleteAppointments;
	public static String  ACLContributor_acl_lockappointments = ch.elexis.base.l10n.Messages.ACLContributor_acl_lockappointments;
	public static String  ACLContributor_acl_showAppointments = ch.elexis.base.l10n.Messages.ACLContributor_acl_showAppointments;
	public static String  ACLContributor_acl_use = ch.elexis.base.l10n.Messages.ACLContributor_acl_use;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
