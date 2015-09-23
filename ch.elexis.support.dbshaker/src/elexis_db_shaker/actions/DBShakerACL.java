package elexis_db_shaker.actions;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.admin.IACLContributor;

public class DBShakerACL implements IACLContributor {
	
	public static final ACE EXEC_DBSHAKER = new ACE(AccessControlDefaults.ADMIN,
		"ch.elexis.support.dbshaker", "Datenbank anonymisieren (DBShaker)");
		
	@Override
	public ACE[] getACL(){
		return new ACE[] {
			EXEC_DBSHAKER
		};
	}
	
	@Override
	public void initializeDefaults(AbstractAccessControl ac){
	}
	
}
