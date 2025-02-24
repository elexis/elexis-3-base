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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.unibe.iam.scg.archie.model.DataSet;
import junit.framework.JUnit4TestAdapter;

/**
 * <p>
 * Tests <code>DataSet</code>
 * </p>
 *
 * <pre>
 * SampleDataSet:
 * ==============
 *     | First Name | Last Name | Address         | Country
 *     ---------------------------------------------------
 * x/y | 0          | 1         | 2               | 3
 * -------------------------------------------------------
 * 0   | Hans       | Muster    | Superstrasse 1  | Switzerland
 * 1   | Vreni      | Müller    | Musterstrasse 1 | Switzerland
 * 2   | Jakob      | Meier     | Ottweg 3        | Switzerland
 * </pre>
 * <p>
 * E.g getCell(2,1) == Meier (Matrix notation)
 * </p>
 *
 * $Id: DataSetTest.java 666 2008-12-13 00:07:54Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 666 $
 */
public class DataSetTest {

	private DataSet sampleDataSet;
	private DataSet emptyDataSet;
	private ArrayList<String> sampleHeadings = new ArrayList<String>();
	private ArrayList<String> sampleHeadingsTooFew = new ArrayList<String>();
	private ArrayList<Comparable<?>[]> sampleContent = new ArrayList<Comparable<?>[]>();

	private String[] sampleRow1 = { "Hans", "Muster", "Superstrasse 1", "Switzerland" };
	private String[] sampleRow2 = { "Vreni", "Müller", "Musterstrasse 1", "Switzerland" };
	private String[] sampleRow3 = { "Jakob", "Meier", "Ottweg 3", "Switzerland" };
	private String[] smallRow = { "Jakob", "Meier", "Ottweg 3" };

	@Before
	public void setUp() {
		this.sampleHeadings.add("First Name");
		this.sampleHeadings.add("Last Name");
		this.sampleHeadings.add("Address");
		this.sampleHeadings.add("Country");

		this.sampleHeadingsTooFew.add("First Name");
		this.sampleHeadingsTooFew.add("Last Name");
		this.sampleHeadingsTooFew.add("Address");

		this.sampleContent.add(this.sampleRow1);
		this.sampleContent.add(this.sampleRow2);
		this.sampleContent.add(this.sampleRow3);

		this.sampleDataSet = new DataSet(this.sampleContent, this.sampleHeadings);
		this.emptyDataSet = new DataSet();
	}

	@Test
	public void testClone() {
		DataSet clonedSet = (DataSet) this.sampleDataSet.clone();
		Assert.assertNotSame(clonedSet, this.sampleDataSet);
		Assert.assertNotSame(clonedSet.getContent(), this.sampleDataSet.getContent());
		Assert.assertNotSame(clonedSet.getHeadings(), this.sampleDataSet.getHeadings());
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNullAsArguments() {
		new DataSet(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorWithEmptyArguments() {
		new DataSet(new ArrayList<Comparable<?>[]>(), new ArrayList<String>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorWithTooFewHeaders() {
		new DataSet(this.sampleContent, this.sampleHeadingsTooFew);
	}

	@Test
	public void testGetters() {
		Assert.assertEquals(this.sampleContent, this.sampleDataSet.getContent());
		Assert.assertEquals(this.sampleHeadings, this.sampleDataSet.getHeadings());
		Assert.assertArrayEquals(this.sampleRow1, this.sampleDataSet.getRow(0));
		Assert.assertArrayEquals(this.sampleRow2, this.sampleDataSet.getRow(1));
		Assert.assertArrayEquals(this.sampleRow3, this.sampleDataSet.getRow(2));
		Assert.assertEquals("Hans", this.sampleDataSet.getCell(0, 0));
		Assert.assertEquals("Meier", this.sampleDataSet.getCell(2, 1));
		Assert.assertEquals("Vreni", this.sampleDataSet.getCell(1, 0));
		Object[] column1 = new Object[3];
		column1[0] = "Muster";
		column1[1] = "Müller";
		column1[2] = "Meier";
		Assert.assertArrayEquals(column1, this.sampleDataSet.getColumn(1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetWithEmptyDataSet() {
		this.emptyDataSet.getCell(3, 4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetWithEmptyDataSet() {
		this.emptyDataSet.setCell(2, 4, "Some String");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addTooSmallRowTest() {
		this.sampleDataSet.addRow(this.smallRow);
	}

	@Test(expected = IllegalArgumentException.class)
	public void settingContentBeforeHeadings() {
		DataSet dataSet = new DataSet();
		dataSet.setContent(this.sampleContent);
	}

	@Test
	public void toStringTest() {
		String desiredOutput = "| First Name | Last Name  | Address         | Country     \n"
				+ "----------------------------------------------------------\n"
				+ "| Hans       | Muster     | Superstrasse 1  | Switzerland \n"
				+ "| Vreni      | Müller     | Musterstrasse 1 | Switzerland \n"
				+ "| Jakob      | Meier      | Ottweg 3        | Switzerland \n";
		Assert.assertEquals(desiredOutput, this.sampleDataSet.toString());
	}

	/**
	 * Static method for JUnit 4 test classes to make them accessible to a
	 * TestRunner designed to work with earlier versions of JUnit.
	 *
	 * @return A Test that can be used in test suites.
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DataSetTest.class);
	}
}
