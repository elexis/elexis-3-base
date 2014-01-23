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

import java.util.List;

/**
 * Service to resolve ATC codes to their names and also resolve the hierarchy of an ATC Code.
 */
public interface ATCCodeService {
	/**
	 * @param atcCode
	 *            the ATC code value to resolve
	 * @return immutable {@link ATCCode} object containing information about the ATCCode or
	 *         <code>null</code> if not found
	 */
	public ATCCode getForATCCode(String atcCode);
	
	/**
	 * Returns the complete hierarchy for a given ATC code w.r.t. to the given level, so the list
	 * size is max 5 elements.
	 * 
	 * @param atcCode
	 *            the ATC code to resolve
	 * @return an ordered list containing the entire hierarchy levels for the ATC code or
	 *         <code>null</code> if not found
	 */
	public List<ATCCode> getHierarchyForATCCode(String atcCode);
}
