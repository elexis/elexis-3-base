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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import ch.unibe.iam.scg.archie.model.ChartModel;
import ch.unibe.iam.scg.archie.model.DataSet;

/**
 * <p>Simple chart model tests.</p>
 * 
 * $Id: ChartModelTest.java 714 2009-01-06 09:58:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 714 $
 */
public class ChartModelTest {
	
	private ChartModel model;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.model = new ChartModel();
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#ChartModel()}.
	 */
	@Test
	public void testChartModel() {
		ChartModel model = null;
		assertNull(model);

		model = new ChartModel();
		assertNotNull(model);
		
		assertNull(model.getDataSet());
		assertNull(model.getChartName());
		assertNull(model.getRows());
		assertNull(model.getColumns());
		
		assertSame(-1, model.getKeysIndex());
		assertSame(-1, model.getValuesIndex());
		assertSame(-1, model.getChartType());
		assertSame(0, model.getCategoryColumnIndex());
		
		assertFalse(model.isLineChart());
		assertFalse(model.isThreeDimensional());
		assertFalse(model.isValid());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setChartType(int)}.
	 */
	@Test
	public void testSetChartType() {
		assertSame(-1, this.model.getChartType());
		this.model.setChartType(ChartModel.CHART_BAR);
		assertSame(ChartModel.CHART_BAR, this.model.getChartType());
		this.model.setChartType(ChartModel.CHART_PIE);
		assertSame(ChartModel.CHART_PIE, this.model.getChartType());
		this.model.setChartType(ChartModel.CHART_BAR);
		assertSame(ChartModel.CHART_BAR, this.model.getChartType());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getChartType()}.
	 */
	@Test
	public void testGetChartType() {
		assertSame(-1, this.model.getChartType());
		this.model.setChartType(ChartModel.CHART_PIE);
		assertSame(ChartModel.CHART_PIE, this.model.getChartType());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getChartName()}.
	 */
	@Test
	public void testGetChartName() {
		assertNull(this.model.getChartName());
		this.model.setChartName("Star Wars");
		assertNotNull(this.model.getChartName());
		assertEquals("Star Wars", this.model.getChartName());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setChartName(java.lang.String)}.
	 */
	@Test
	public void testSetChartName() {
		assertNull(this.model.getChartName());
		this.model.setChartName("Star Wars");
		assertNotNull(this.model.getChartName());
		assertEquals("Star Wars", this.model.getChartName());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getDataSet()}.
	 */
	@Test
	public void testGetDataSet() {
		assertNull(this.model.getDataSet());
		DataSet dataset = new DataSet();
		this.model.setDataSet(dataset);
		assertNotNull(this.model.getDataSet());
		assertSame(dataset, this.model.getDataSet());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#hasDataSet()}.
	 */
	@Test
	public void testHasDataSet() {
		assertFalse(this.model.hasDataSet());
		DataSet dataset = new DataSet();
		this.model.setDataSet(dataset);
		assertTrue(this.model.hasDataSet());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setDataSet(ch.unibe.iam.scg.archie.model.DataSet)}.
	 */
	@Test
	public void testSetDataSet() {
		assertNull(this.model.getDataSet());
		DataSet dataset = new DataSet();
		this.model.setDataSet(dataset);
		assertNotNull(this.model.getDataSet());
		assertSame(dataset, this.model.getDataSet());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getKeysIndex()}.
	 */
	@Test
	public void testGetKeysIndex() {
		assertSame(-1, this.model.getKeysIndex());
		this.model.setKeysIndex(2);
		assertSame(2, this.model.getKeysIndex());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setKeysIndex(int)}.
	 */
	@Test
	public void testSetKeysIndex() {
		assertSame(-1, this.model.getKeysIndex());
		this.model.setKeysIndex(2);
		assertSame(2, this.model.getKeysIndex());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getValuesIndex()}.
	 */
	@Test
	public void testGetValuesIndex() {
		assertSame(-1, this.model.getValuesIndex());
		this.model.setValuesIndex(5);
		assertSame(5, this.model.getValuesIndex());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setValuesIndex(int)}.
	 */
	@Test
	public void testSetValuesIndex() {
		assertSame(-1, this.model.getValuesIndex());
		this.model.setValuesIndex(1);
		assertSame(1, this.model.getValuesIndex());
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setRows(int[])}.
	 */
	@Test
	public void testSetRows() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getRows()}.
	 */
	@Test
	public void testGetRows() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setColumns(int[])}.
	 */
	@Test
	public void testSetColumns() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getColumns()}.
	 */
	@Test
	public void testGetColumns() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#isThreeDimensional()}.
	 */
	@Test
	public void testIsThreeDimensional() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setThreeDimensional(boolean)}.
	 */
	@Test
	public void testSetThreeDimensional() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setCategoryColumnIndex(int)}.
	 */
	@Test
	public void testSetRowTitleColumnIndex() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#getCategoryColumnIndex()}.
	 */
	@Test
	public void testGetRowTitleColumnIndex() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#setLineChart(boolean)}.
	 */
	@Test
	public void testSetLineChart() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#isLineChart()}.
	 */
	@Test
	public void testIsLineChart() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#toString()}.
	 */
	@Test
	public void testToString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#isValid()}.
	 */
	@Test
	public void testIsValid() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link ch.unibe.iam.scg.archie.model.ChartModel#hasValidChartType()}.
	 */
	@Test
	public void testHasValidChartType() {
		fail("Not yet implemented"); // TODO
	}

}
