/*******************************************************************************
 * Copyright (c) 2008-2015 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *     MEDEVIT <office@medevit.at> - adaptations for RBAC
 *******************************************************************************/
package ch.unibe.iam.scg.archie.acl;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.IACLContributor;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Role;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.i18n.Messages;

/**
 * <p>
 * Handles the access to Archie based on access control lists defined in Elexis
 * properties.<br/>
 * 
 * $Id: ArchieACL.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ArchieACL implements IACLContributor {

	/**
	 * Access control string that will be displayed in the ACL.
	 */
	public static final ACE USE_ARCHIE = new ACE(ACE.ACE_ROOT, "archie", ArchieActivator.PLUGIN_NAME + " "
			+ Messages.ACL_ACCESS);

	/**
	 * Returns the ACL for this plugin.
	 * 
	 * @return String[]
	 */
	public ACE[] getACL() {
		return new ACE[] { ArchieACL.USE_ARCHIE };
	}


	/**
	 * Static function to check whether the currently active user has access to
	 * archie or not.
	 * 
	 * @return boolean True if the current user can access archie, false else.
	 */
	public static boolean userHasAccess() {
		return CoreHub.acl.request(USE_ARCHIE);
	}


	/**
	 * @{inheritDoc}
	 */
	@Override
	public void initializeDefaults(AbstractAccessControl ac) {
		ac.grant(Role.SYSTEMROLE_LITERAL_DOCTOR, USE_ARCHIE);
	}

}
