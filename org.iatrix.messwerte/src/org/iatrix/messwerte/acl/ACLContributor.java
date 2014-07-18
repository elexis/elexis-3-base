package org.iatrix.messwerte.acl;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AccessControl;
import ch.elexis.admin.IACLContributor;
import ch.elexis.core.data.activator.CoreHub;

public class ACLContributor implements IACLContributor {
	public ACE[] getACL(){
		return new ACE[] {
			ACL.DATA_MESSWERT_EDIT_INT_LAB_VALUE, ACL.DATA_MESSWERT_EDIT_INT_LAB_PATH_STATE,
			ACL.DATA_MESSWERT_EDIT_EXT_LAB_VALUE, ACL.DATA_MESSWERT_EDIT_EXT_LAB_PATH_STATE,
			ACL.DELETE_MESSWERT_INT_LAB, ACL.DELETE_MESSWERT_EXT_LAB,
		};
	}
	
	public ACE[] reject(ACE[] acl){
		return null;
	}
	
	public static void initialize(){
		// allow any user to change the pathologic state of any value
		CoreHub.acl.grant(AccessControl.USER_GROUP, ACL.DATA_MESSWERT_EDIT_INT_LAB_PATH_STATE);
		CoreHub.acl.grant(AccessControl.USER_GROUP, ACL.DATA_MESSWERT_EDIT_EXT_LAB_PATH_STATE);
		
		// allow any user to change values of the internal lab
		CoreHub.acl.grant(AccessControl.USER_GROUP, ACL.DATA_MESSWERT_EDIT_INT_LAB_VALUE);
		
		// only allow admins to change values of the external labs
		CoreHub.acl.grant(AccessControl.ADMIN_GROUP, ACL.DATA_MESSWERT_EDIT_EXT_LAB_VALUE);
		
		// only allow admins to delete values
		CoreHub.acl.grant(AccessControl.ADMIN_GROUP, ACL.DELETE_MESSWERT_INT_LAB);
		CoreHub.acl.grant(AccessControl.ADMIN_GROUP, ACL.DELETE_MESSWERT_EXT_LAB);
		
		CoreHub.acl.flush();
	}
}
