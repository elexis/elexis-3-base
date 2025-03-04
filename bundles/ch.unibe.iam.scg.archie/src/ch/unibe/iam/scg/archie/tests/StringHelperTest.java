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
import org.junit.Test;

import ch.unibe.iam.scg.archie.utils.StringHelper;
import junit.framework.JUnit4TestAdapter;

/**
 * Test the utility class for strings.
 *
 * $Id: StringHelperTest.java 666 2008-12-13 00:07:54Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 666 $
 */
public class StringHelperTest {

	@Test
	public void removeillegalCharacters() {
		String illegal = "  \"I| am an? ill!egal... Stri//<ng> {contai\\/ning} b'a'd chara%c%ter|s!. ";
		String good = StringHelper.removeIllegalCharacters(illegal, false);

		Assert.assertTrue(good.startsWith("I"));
		Assert.assertTrue(good.endsWith("!"));
		Assert.assertFalse(good.equals("I_am_an_ill!egal_String_containing_bad_characters!."));
		Assert.assertTrue(good.equals("I_am_an_ill!egal____String_containing_bad_characters!"));

		String single = StringHelper.removeIllegalCharacters(good, true);
		Assert.assertFalse(single.equals("I_am_an_ill!egal____String_containing_bad_characters!"));
		Assert.assertTrue(single.equals("I_am_an_ill!egal_String_containing_bad_characters!"));
	}

	/**
	 * Static method for JUnit 4 test classes to make them accessible to a
	 * TestRunner designed to work with earlier versions of JUnit.
	 *
	 * @return A Test that can be used in test suites.
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(StringHelperTest.class);
	}
}
