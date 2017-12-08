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
package ch.unibe.iam.scg.archie.model;

/**
 * <p>
 * Simple wrapper class that provides getters and setters for a Regular
 * Expression (regex) pattern <code>String</code> and associated error message
 * <code>String</code> (if a regex match should fail).
 * </p>
 * 
 * $Id: RegexValidation.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class RegexValidation {

	private String pattern; // regex pattern.
	private String message; // Message if the regex match fails.

	/**
	 * @param pattern
	 *            String regex pattern.
	 * @param message
	 *            String error message if regex match fails.
	 * @throws IllegalArgumentException
	 */
	public RegexValidation(final String pattern, final String message) throws IllegalArgumentException {
		if (pattern == null || message == null) {
			throw new IllegalArgumentException("Arguments pattern and message must not be null.");
		}
		this.pattern = pattern;
		this.message = message;
	}

	/**
	 * @return String regex pattern
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @param pattern
	 *            String regex pattern
	 */
	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return String error message if regex match fails.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param message
	 *            set error message if regex match fails.
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
}
