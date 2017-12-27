/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.base.befunde;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.admin.IACLContributor;
import ch.elexis.core.model.RoleConstants;

public class ACLContributor implements IACLContributor {
	public static final ACE ACE_BEFUNDE =
		new ACE(ACE.ACE_ROOT, "Messwert", Messages.ACLContributor_messwertACLName); //$NON-NLS-1$ 
	public static final ACE DELETE_PARAM = new ACE(AccessControlDefaults.DELETE, "Messwertrubrik", //$NON-NLS-1$
		Messages.ACLContributor_messwertRubrikACLName);
	public static final ACE ADD_PARAM = new ACE(ACE_BEFUNDE, "Befund zufügen", //$NON-NLS-1$
		Messages.ACLContributor_addMesswertACLName);
		
	public ACE[] getACL(){
		return new ACE[] {
			DELETE_PARAM, ADD_PARAM
		};
	}
	
	@Override
	public void initializeDefaults(AbstractAccessControl ac){
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_USER, ACE_BEFUNDE);
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_EXECUTIVE_DOCTOR, DELETE_PARAM);
	}
}
