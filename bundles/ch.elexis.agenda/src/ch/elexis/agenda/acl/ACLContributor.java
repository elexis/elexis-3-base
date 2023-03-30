/*******************************************************************************
 * Copyright (c) 2007-2015, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    MEDEVIT <office@medevit.at> - adapted to RBAC
 *******************************************************************************/
package ch.elexis.agenda.acl;

import ch.elexis.actions.Activator;
import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.IACLContributor;
import ch.elexis.core.model.RoleConstants;

/**
 * Define access rights needed for the various actions with the agenda
 *
 * @author gerry
 *
 */
public class ACLContributor implements IACLContributor {
	/** The right to use the agenda at all */
	public static final ACE ACE_AGENDA = new ACE(ACE.ACE_ROOT, Activator.PLUGIN_ID, "Agenda"); //$NON-NLS-1$
	public static final ACE USE_AGENDA = new ACE(ACE_AGENDA, "user", Messages.ACLContributor_acl_use); //$NON-NLS-1$

	/** administrative rights to the agenda */
	public static final ACE ADMIN_AGENDA = new ACE(ACE_AGENDA, "admin", Messages.ACLContributor_acl_administer); //$NON-NLS-1$

	/** The right to see appointments */
	public static final ACE DISPLAY_APPOINTMENTS = new ACE(USE_AGENDA, "zeigeTermine", //$NON-NLS-1$
			Messages.ACLContributor_acl_showAppointments);
	/** The right to modify appointments */
	public static final ACE CHANGE_APPOINTMENTS = new ACE(USE_AGENDA, "ändereTermine", //$NON-NLS-1$
			Messages.ACLContributor_acl_changeAppointments);
	/** The right to delete appointments */
	public static final ACE DELETE_APPOINTMENTS = new ACE(USE_AGENDA, "löscheTermine", //$NON-NLS-1$
			Messages.ACLContributor_acl_deleteAppointments);

	/** The right to modify the day limits */
	public static final ACE CHANGE_DAYSETTINGS = new ACE(ADMIN_AGENDA, "Tagesgrenzen", //$NON-NLS-1$
			Messages.ACLContributor_acl_daylimits);

	/** The right to lock or unlock appointments */
	public static final ACE CHANGE_APPLOCK = new ACE(ADMIN_AGENDA, "TerminSperren", //$NON-NLS-1$
			Messages.ACLContributor_acl_lockappointments);

	/**
	 * get the ACE's that should be managed.
	 */
	public ACE[] getACL() {
		return new ACE[] { ACE_AGENDA, USE_AGENDA, ADMIN_AGENDA, DISPLAY_APPOINTMENTS, CHANGE_APPOINTMENTS,
				DELETE_APPOINTMENTS, CHANGE_DAYSETTINGS, CHANGE_APPLOCK };
	}

	@Override
	public void initializeDefaults(AbstractAccessControl ac) {
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_USER, USE_AGENDA);
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_USER, DISPLAY_APPOINTMENTS);
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_EXECUTIVE_DOCTOR, ADMIN_AGENDA);
	}

}
