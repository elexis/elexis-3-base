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
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.admin.IACLContributor;

public class ACLContributor implements IACLContributor {
	public static final ACE ACE_BEFUNDE = new ACE(ACE.ACE_ROOT,
		"Messwert", Messages.getString("ACLContributor.messwertACLName")); //$NON-NLS-1$ //$NON-NLS-2$
	public static final ACE DELETE_PARAM = new ACE(AccessControlDefaults.DELETE,
		"Messwertrubrik", Messages.getString("ACLContributor.messwertRubrikACLName")); //$NON-NLS-1$ //$NON-NLS-2$
	public static final ACE ADD_PARAM = new ACE(ACE_BEFUNDE,
		"Befund zufügen", Messages.getString("ACLContributor.addMesswertACLName")); //$NON-NLS-1$ //$NON-NLS-2$
	
	public ACE[] getACL(){
		return new ACE[] {
			DELETE_PARAM, ADD_PARAM
		};
	}
	
	public ACE[] reject(ACE[] acl){
		return null;
	}
}
