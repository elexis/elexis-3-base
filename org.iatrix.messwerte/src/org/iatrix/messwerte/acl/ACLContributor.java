package org.iatrix.messwerte.acl;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.IACLContributor;
import ch.elexis.data.Role;

public class ACLContributor implements IACLContributor {
	public ACE[] getACL(){
		return new ACE[] {
			ACL.DATA_MESSWERT_EDIT_INT_LAB_VALUE, ACL.DATA_MESSWERT_EDIT_INT_LAB_PATH_STATE,
			ACL.DATA_MESSWERT_EDIT_EXT_LAB_VALUE, ACL.DATA_MESSWERT_EDIT_EXT_LAB_PATH_STATE,
			ACL.DELETE_MESSWERT_INT_LAB, ACL.DELETE_MESSWERT_EXT_LAB,
		};
	}

	@Override
	public void initializeDefaults(AbstractAccessControl ac){
		// allow any user to change the pathologic state of any value
		ac.grant(Role.SYSTEMROLE_LITERAL_USER, ACL.DATA_MESSWERT_EDIT_INT_LAB_PATH_STATE);
		ac.grant(Role.SYSTEMROLE_LITERAL_USER, ACL.DATA_MESSWERT_EDIT_EXT_LAB_PATH_STATE);
		
		// allow any user to change values of the internal lab
		ac.grant(Role.SYSTEMROLE_LITERAL_USER, ACL.DATA_MESSWERT_EDIT_INT_LAB_VALUE);
		
		// only allow admins to change values of the external labs
		ac.grant(Role.SYSTEMROLE_LITERAL_EXECUTIVE_DOCTOR, ACL.DATA_MESSWERT_EDIT_EXT_LAB_VALUE);
		
		// only allow admins to delete values
		ac.grant(Role.SYSTEMROLE_LITERAL_EXECUTIVE_DOCTOR, ACL.DELETE_MESSWERT_INT_LAB);
		ac.grant(Role.SYSTEMROLE_LITERAL_EXECUTIVE_DOCTOR, ACL.DELETE_MESSWERT_EXT_LAB);
	}
}
