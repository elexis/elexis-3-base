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

import org.eclipse.swt.SWT;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.rgw.tools.Money;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.utils.DatasetHelper;
import junit.framework.JUnit4TestAdapter;

/**
 * <p>
 * Tests <code>DataSet</code>
 * </p>
 *
 * <pre>
 * SampleDataSet:
 * ==============
 *     | First Name | Age       | Salary          | Happyness
 *     ------------------------------------------------------
 * x/y | 0          | 1         | 2               | 3
 * ----------------------------------------------------------
 * 0   | Hans       | 24        | 3500.70         | 0.03
 * 1   | Vreni      | 16        | 6400.00         | 0.12
 * 2   | Jakob      | 54        | 7891.23         | 0.98
 * </pre>
 * <p>
 * E.g getCell(2,1) == Meier (Matrix notation)
 * </p>
 *
 * $Id: DatasetHelperTest.java 666 2008-12-13 00:07:54Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 666 $
 */
public class DatasetHelperTest {

	private DataSet sampleDataSet;

	private ArrayList<String> sampleHeadings = new ArrayList<String>();
	private ArrayList<Comparable<?>[]> sampleContent = new ArrayList<Comparable<?>[]>();

	private Comparable<?>[] sampleRow1 = { "Hans", 24, new Money(3500.70), 0.03 };
	private Comparable<?>[] sampleRow2 = { "Vreni", 16, new Money(6400.00), 0.12 };
	private Comparable<?>[] sampleRow3 = { "Jakob", 54, new Money(7891.23), 0.98 };

	@Before
	public void setUp() {
		sampleHeadings.add("First Name");
		sampleHeadings.add("Age");
		sampleHeadings.add("Salary");
		sampleHeadings.add("Happyness");

		sampleContent.add(sampleRow1);
		sampleContent.add(sampleRow2);
		sampleContent.add(sampleRow3);

		sampleDataSet = new DataSet(sampleContent, sampleHeadings);
	}

	@Test
	public void testSortingDataset() {
		DataSet clonedDataset = (DataSet) this.sampleDataSet.clone();

		Assert.assertEquals("Hans", clonedDataset.getCell(0, 0));
		Assert.assertEquals("Vreni", clonedDataset.getCell(1, 0));
		Assert.assertEquals("Jakob", clonedDataset.getCell(2, 0));

		DatasetHelper.sortDataSet(clonedDataset, "First Name", SWT.UP);

		Assert.assertEquals("Hans", clonedDataset.getCell(0, 0));
		Assert.assertEquals("Jakob", clonedDataset.getCell(1, 0));
		Assert.assertEquals("Vreni", clonedDataset.getCell(2, 0));

		DatasetHelper.sortDataSet(clonedDataset, "First Name", SWT.DOWN);

		Assert.assertEquals("Vreni", clonedDataset.getCell(0, 0));
		Assert.assertEquals("Jakob", clonedDataset.getCell(1, 0));
		Assert.assertEquals("Hans", clonedDataset.getCell(2, 0));

		DatasetHelper.sortDataSet(clonedDataset, "Salary", SWT.DOWN);

		Assert.assertEquals(new Money(7891.23), clonedDataset.getCell(0, 2));
		Assert.assertEquals(new Money(6400.00), clonedDataset.getCell(1, 2));
		Assert.assertEquals(new Money(3500.70), clonedDataset.getCell(2, 2));
	}

	@Test
	public void testNumericColumns() {
		DataSet dataset = this.sampleDataSet;

		Assert.assertFalse(DatasetHelper.isNumericColumn(dataset, 0));
		Assert.assertTrue(DatasetHelper.isNumericColumn(dataset, 1));

		Assert.assertFalse(DatasetHelper.isNumericColumn(dataset, 2));
		Assert.assertTrue(DatasetHelper.isMoneyColumn(dataset, 2));

		Assert.assertTrue(DatasetHelper.isNumericColumn(dataset, 3));

		Assert.assertFalse(DatasetHelper.isNumericColumn(dataset, "First Name"));
		Assert.assertTrue(DatasetHelper.isNumericColumn(dataset, "Age"));

		Assert.assertFalse(DatasetHelper.isNumericColumn(dataset, "Salary"));
		Assert.assertTrue(DatasetHelper.isMoneyColumn(dataset, "Salary"));

		Assert.assertTrue(DatasetHelper.isNumericColumn(dataset, "Happyness"));
	}

	/**
	 * Static method for JUnit 4 test classes to make them accessible to a
	 * TestRunner designed to work with earlier versions of JUnit.
	 *
	 * @return A Test that can be used in test suites.
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DatasetHelperTest.class);
	}
}