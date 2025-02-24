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

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import ch.unibe.iam.scg.archie.model.Cohort;
import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

/**
 * <p>
 * Tests the custom cohort object.
 * </p>
 *
 * $Id: CohortTest.java 705 2009-01-03 17:48:46Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 705 $
 */
public class CohortTest {

	private Cohort cohort;
	private Cohort cohortSame;
	private Cohort cohortSmall;
	private Cohort cohortLarge;
	private Cohort cohortStrange;

	@Before
	public void setUp() {
		this.cohort = new Cohort(5, 30, "Hello World");
		this.cohortSame = new Cohort(5, 30, "Something else");
		this.cohortSmall = new Cohort(10, 15, new Integer(3));
		this.cohortLarge = new Cohort(10, 30, new Integer(5));
		this.cohortStrange = new Cohort(-30, -10, cohort);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor() {
		new Cohort(-5, -10, "Preconditions not satisfied!");
		new Cohort(45, 13, "Preconditions not satisfied!");
		new Cohort(9, -1, "Preconditions not satisfied!");
	}

	@Test
	public void testCompareTo() {
		Assert.assertEquals(this.cohortSmall.compareTo(this.cohortLarge), -1);
		Assert.assertEquals(this.cohortLarge.compareTo(this.cohortSmall), 1);
		Assert.assertEquals(this.cohortLarge.compareTo(this.cohortLarge), 0);
		ArrayList<Cohort> cohorts = new ArrayList<Cohort>(5);
		cohorts.add(this.cohort);
		cohorts.add(this.cohortSame);
		cohorts.add(this.cohortStrange);
		cohorts.add(this.cohortLarge);
		cohorts.add(this.cohortSmall);
		Collections.sort(cohorts);
		Assert.assertTrue(cohorts.get(0).equals(this.cohortStrange));
		Assert.assertTrue(cohorts.get(1).equals(this.cohortSame));
		Assert.assertTrue(cohorts.get(2).equals(this.cohort));
		Assert.assertTrue(cohorts.get(3).equals(this.cohortSmall));
		Assert.assertTrue(cohorts.get(4).equals(this.cohortLarge));
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(this.cohort.equals(this.cohort));
		Assert.assertTrue(this.cohortStrange.getValue().equals(this.cohort));
		Assert.assertTrue(this.cohort.equals(this.cohortSame));
		Assert.assertFalse(this.cohort.equals(this.cohortLarge));
		Assert.assertFalse(this.cohort.equals(this.cohortSmall));
	}

	@Test
	public void testGettersAndSetters() {
		Assert.assertEquals(cohort.getCohortSize(), 26);
		Assert.assertEquals(cohort.getLowerBound(), 5);
		Assert.assertEquals(cohort.getUpperBound(), 30);
		Assert.assertEquals(cohort.getValue(), "Hello World");
		Assert.assertEquals(cohort.toString(), "5 - 30");
		cohort.setLowerBound(7);
		cohort.setUpperBound(12);
		cohort.setValue("Another String");
		Assert.assertEquals(cohort.getCohortSize(), 6);
		Assert.assertEquals(cohort.getLowerBound(), 7);
		Assert.assertEquals(cohort.getUpperBound(), 12);
		Assert.assertEquals(cohort.getValue(), "Another String");
		Assert.assertEquals(cohort.toString(), "7 - 12");
		cohort.setLowerBound(2);
		cohort.setUpperBound(2);
		Assert.assertEquals(cohort.toString(), "2");
		Assert.assertEquals(cohort.getCohortSize(), 1);
		Assert.assertEquals(cohortStrange.getCohortSize(), 21);
	}

	/**
	 * Static method for JUnit 4 test classes to make them accessible to a
	 * TestRunner designed to work with earlier versions of JUnit.
	 *
	 * @return A Test that can be used in test suites.
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(CohortTest.class);
	}
}