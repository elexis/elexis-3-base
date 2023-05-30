package ch.elexis.base.ch.ebanking;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.IACLContributor;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.core.ac.AccessControlDefaults;
import ch.elexis.core.model.RoleConstants;

public class EBankingACLContributor implements IACLContributor {

	public final static ACE DISPLAY_ESR = new ACE(AccessControlDefaults.DATA, "ch.elexis.ebanking_ch:DisplayESR", //$NON-NLS-1$
			Messages.ESRView_showESRData);

	@Override
	public ACE[] getACL() {
		return new ACE[] { DISPLAY_ESR };
	}

	@Override
	public void initializeDefaults(AbstractAccessControl ac) {
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_USER, DISPLAY_ESR);
	}

}
