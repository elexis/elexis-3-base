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

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

/**
 * Tests that assertions are enabled in the currently set java compiler. Make
 * sure that your Java VM has the argument <code>-ea</code> set before running
 * this test, otherwise it will always fail.
 *
 * $Id: AssertionTest.java 666 2008-12-13 00:07:54Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 666 $
 */
public class AssertionTest {

	@Test(expected = AssertionError.class)
	public void assertionsEnabled() {
		assert false;
	}

	/**
	 * Static method for JUnit 4 test classes to make them accessible to a
	 * TestRunner designed to work with earlier versions of JUnit.
	 *
	 * @return A Test that can be used in test suites.
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AssertionTest.class);
	}
}