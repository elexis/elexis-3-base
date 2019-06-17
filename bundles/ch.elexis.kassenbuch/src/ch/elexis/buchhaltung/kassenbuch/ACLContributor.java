/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.IACLContributor;
import ch.elexis.core.model.RoleConstants;

/**
 * The ACLContributor defines, what rights should be configured to use this plugin
 * 
 * @author gerry
 * 
 */
public class ACLContributor implements IACLContributor {
	
	public static ACE KB = new ACE(ACE.ACE_ROOT, "Kassenbuch", "Kassenbuch");
	public static final ACE BOOKING = new ACE(KB, "Buchung", "Buchung");
	public static final ACE STORNO = new ACE(KB, "Storno", "Storno");
	public static final ACE VIEW = new ACE(KB, "Display", "Anzeigen");
	
	public ACE[] getACL(){
		return new ACE[] {
			KB, BOOKING, STORNO, VIEW
		};
	}

	@Override
	public void initializeDefaults(AbstractAccessControl ac){
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_USER, KB);
	}
}
