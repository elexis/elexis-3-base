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
package ch.unibe.iam.scg.archie.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.unibe.iam.scg.archie.model.RegexValidation;
import junit.framework.JUnit4TestAdapter;

/**
 * Tests RegexValidation class.
 *
 * $Id: RegexValidationTest.java 702 2008-12-23 11:20:40Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 702 $
 */
public class RegexValidationTest {

	private RegexValidation someRegexVal;

	private static final String SOME_PATTERN = "\\d{3,}";
	private static final String SOME_MESSAGE = "Some Regex Validation Message";
	private static final String ANOTHER_PATTERN = "\\d{1,}";
	private static final String ANOTHER_MESSAGE = "Another Regex Validation Message";

	@Before
	public void setUp() {
		this.someRegexVal = new RegexValidation(SOME_PATTERN, SOME_MESSAGE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorArgumentsTest() {
		new RegexValidation(null, null);
	}

	@Test
	public void getterAndSetterTest() {
		Assert.assertNotNull(this.someRegexVal.getMessage());
		Assert.assertNotNull(this.someRegexVal.getPattern());
		Assert.assertEquals(this.someRegexVal.getMessage(), SOME_MESSAGE);
		Assert.assertEquals(this.someRegexVal.getPattern(), SOME_PATTERN);
		this.someRegexVal.setMessage(ANOTHER_MESSAGE);
		this.someRegexVal.setPattern(ANOTHER_PATTERN);
		Assert.assertEquals(this.someRegexVal.getMessage(), ANOTHER_MESSAGE);
		Assert.assertEquals(this.someRegexVal.getPattern(), ANOTHER_PATTERN);
	}

	/**
	 * Static method for JUnit 4 test classes to make them accessible to a
	 * TestRunner designed to work with earlier versions of JUnit.
	 *
	 * @return A Test that can be used in test suites.
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RegexValidationTest.class);
	}
}
