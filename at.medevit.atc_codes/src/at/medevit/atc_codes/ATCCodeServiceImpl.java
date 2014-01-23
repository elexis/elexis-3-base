/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.atc_codes;

import java.util.ArrayList;
import java.util.List;

import at.medevit.atc_codes.internal.ATCCodes;

public class ATCCodeServiceImpl implements ATCCodeService {
	
	@Override
	public ATCCode getForATCCode(String atcCode){
		return ATCCodes.getInstance().getATCCode(atcCode);
	}
	
	@Override
	public List<ATCCode> getHierarchyForATCCode(String atcCode){
		ArrayList<ATCCode> ret = new ArrayList<ATCCode>();
		ATCCode root = getForATCCode(atcCode);
		if (root != null) {
			ret.add(root);
			int currentLevel = root.level - 1;
			while (currentLevel > 0) {
				ATCCode temp = fetchLevelForATCCode(root, currentLevel);
				currentLevel--;
				ret.add(temp);
			}
		}
		return ret;
	}
	
	private ATCCode fetchLevelForATCCode(ATCCode root, int currentLevel){
		switch (currentLevel) {
		case 4:
			return getForATCCode(root.atcCode.substring(0, 5));
		case 3:
			return getForATCCode(root.atcCode.substring(0, 4));
		case 2:
			return getForATCCode(root.atcCode.substring(0, 3));
		case 1:
			return getForATCCode(root.atcCode.substring(0, 1));
		default:
			return null;
		}
	}
	
}
