/*******************************************************************************
 * Copyright (c) 2013-2014 MEDEVIT.
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

import ch.elexis.core.jdt.Nullable;

/**
 * Service to resolve ATC codes to their names and also resolve the hierarchy of an ATC Code.
 */
public interface ATCCodeService {
	
	public static final int ATC_NAME_LANGUAGE_ENGLISH = 0;
	public static final int ATC_NAME_LANGUAGE_GERMAN = 1;
	
	public static final int MATCH_NAME_BY_NAME_ONLY = 0;
	public static final int MATCH_NAME_BY_NAME_OR_ATC = 1;
	
	/**
	 * @param atcCode
	 *            the ATC code value to resolve
	 * @return immutable {@link ATCCode} object containing information about the ATCCode or
	 *         <code>null</code> if not found
	 */
	public ATCCode getForATCCode(String atcCode);
	
	/**
	 * Returns the complete parent hierarchy for a given ATC leaf code. With respect to the given
	 * level the list size is max 5 elements.
	 * 
	 * @param atcCode
	 *            the ATC code to resolve
	 * @return an ordered list containing the entire hierarchy levels for the ATC code or
	 *         <code>null</code> if not found
	 */
	public List<ATCCode> getHierarchyForATCCode(String atcCode);
	
	/**
	 * Return all ATC codes that wild-carded match the given name
	 * 
	 * @param name
	 *            the name to perform a wild-carded match upon. e.g. given the name <code>per</code>
	 *            will match on <code>*per*</code>
	 * @param i
	 *            the language to match name on, supports
	 *            {@link ATCCodeService#ATC_NAME_LANGUAGE_ENGLISH} and
	 *            {@link ATCCodeService#ATC_NAME_LANGUAGE_GERMAN}
	 * @param j
	 *            whether to match the given name with the real name of the article only
	 *            {@link ATCCodeService#MATCH_NAME_BY_NAME_ONLY} or to include the atc code in the
	 *            matching {@link ATCCodeService#MATCH_NAME_BY_NAME_OR_ATC}
	 * @return a list containing all matched elements, sorted by ATC Hierarchy
	 */
	public List<ATCCode> getATCCodesMatchingName(String name, int i, int j);
	
	/**
	 * @return A list of all available ATC codes. The order is determined by the ATC hierarchy.
	 * @since 3.1
	 */
	public List<ATCCode> getAllATCCodes();
	
	/**
	 * Retrieve the next ATCCode in the ordered hierarchy of the provided {@link ATCCode}
	 * @param code
	 * @return
	 * @since 3.1
	 */
	public @Nullable ATCCode getNextInHierarchy(ATCCode code);
}
