/*******************************************************************************
 * Copyright (c) 2007-2014 G. Weirich, A. Brögli and A. Häffner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rgw - initial API and implementation
 *    rgw - 2014: Changes for Elexis 2.x
 ******************************************************************************/
package ch.elexis.molemax.data;

import ch.elexis.admin.ACE;
import ch.elexis.admin.AbstractAccessControl;
import ch.elexis.admin.IACLContributor;
import ch.elexis.core.model.RoleConstants;
import ch.elexis.molemax.Messages;

public class MolemaxACL implements IACLContributor {
	public static final ACE ACLBASE = new ACE(ACE.ACE_ROOT, "ch.elexis.molemax", "Molemax");
	public static final ACE SEE_IMAGES = new ACE(ACLBASE, "ch.elexis.molemax.seeImages", Messages.MolemaxACL_seeImages);
	public static final ACE CHANGE_IMAGES = new ACE(ACLBASE, "ch.elexis.molemax.changeImages",
			Messages.MolemaxACL_changeImages);

	public ACE[] getACL() {
		return new ACE[] { ACLBASE, SEE_IMAGES, CHANGE_IMAGES };
	}

	@Override
	public void initializeDefaults(AbstractAccessControl ac) {
		ac.grant(RoleConstants.SYSTEMROLE_LITERAL_DOCTOR, ACLBASE);
	}

}
