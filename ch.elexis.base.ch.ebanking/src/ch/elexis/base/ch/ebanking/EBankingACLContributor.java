package ch.elexis.base.ch.ebanking;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.admin.IACLContributor;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.data.Role;

public class EBankingACLContributor implements IACLContributor {
	
	public final static ACE DISPLAY_ESR = new ACE(AccessControlDefaults.DATA,
		"ch.elexis.ebanking_ch:DisplayESR", Messages.ESRView_showESRData); //$NON-NLS-1$
	
	@Override
	public ACE[] getACL(){
		return new ACE[] {DISPLAY_ESR};
	}
	
	@Override
	public void initializeDefaults(AbstractAccessControl ac){
		ac.grant(Role.SYSTEMROLE_LITERAL_USER, DISPLAY_ESR);
	}
	
}
