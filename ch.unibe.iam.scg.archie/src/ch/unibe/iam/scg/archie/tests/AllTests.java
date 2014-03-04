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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * JUnit test suite that runs all tests, hopefully all tests from the
 * <code>ch.unibe.iam.scg.archie.tests</code> package. However, all tests need
 * to be added manually to this suite.
 * </p>
 * 
 * $Id: AllTests.java 666 2008-12-13 00:07:54Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 666 $
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.unibe.iam.scg.archie.tests");
		// $JUnit-BEGIN$
		suite.addTest(RegexValidationTest.suite());
		suite.addTest(AssertionTest.suite());
		suite.addTest(DataSetTest.suite());
		suite.addTest(DatasetHelperTest.suite());
		suite.addTest(StringHelperTest.suite());
		suite.addTest(CohortTest.suite());
		// $JUnit-END$
		return suite;
	}

}
