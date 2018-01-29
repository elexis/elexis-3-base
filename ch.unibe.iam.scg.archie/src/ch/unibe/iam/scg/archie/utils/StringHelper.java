/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.utils;

/**
 * <p>Helper class for string handling.</p>
 * 
 * $Id: StringHelper.java 731 2009-03-18 10:07:47Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 731 $
 */
public class StringHelper {

	/**
	 * Removes all illegal filename characters from a given String
	 * 
	 * @param name
	 * @param singleSpaces if true, no double spaces are allowed; they get removed.
	 * @return String
	 * @see "http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words"
	 */
	public static final String removeIllegalCharacters(String name, final boolean singleSpaces) {
		// remove illegal characters and replace with a more friendly char ;)
		String safe = name.trim();

		// remove illegal characters
		safe = safe.replaceAll("[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]", "");

		// replace . dots with _ and remove the _ if at the end
		safe = safe.replaceAll("\\.", "_");
		if (safe.endsWith("_")) {
			safe = safe.substring(0, safe.length() - 1);
		}

		// replace whitespace characters with _
		safe = safe.replaceAll("\\s+", "_");

		// replace double or more spaces with a single one
		if (singleSpaces) {
			safe = safe.replaceAll("_{2,}", "_");
		}

		return safe;
	}
	
	/**
	 * Checks whether a given string only contains numeric characters and thus is a number.
	 * @param inputData 
	 * @return boolean True if a string has numeric characters only, false else.
	 */
	public static final boolean isNumeric(final String inputData) {
		return inputData.matches("-?\\d+(.\\d+)?");
	}
}
