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

import org.apache.commons.lang3.StringUtils;

import ch.unibe.iam.scg.archie.utils.ArrayUtils;

/**
 * <p>
 * Represents a model of a chart. Contains information on how to render a chart.
 * </p>
 *
 * $Id: ChartModel.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ChartModel {

	/**
	 * Constant for pie chart types, 1.
	 */
	public static final int CHART_PIE = 1;

	/**
	 * Constant for bar chart types, 2. Bar charts can also be handled as line
	 * charts as they both are created from a category dataset. There's a switch in
	 * the bar chart type that can be activated for line charts.
	 */
	public static final int CHART_BAR = 2;

	/**
	 * This switch can be activated for bar charts which makes them render as line
	 * charts.
	 */
	private boolean isLineChart;

	private String chartName;
	private DataSet dataSet;

	private int[] rows;
	private int[] columns;

	private int keysIndex;
	private int valuesIndex;
	private int categoryColumnIndex; // used in bar & line charts
	private int chartType;

	private boolean isThreeDimensional;

	/**
	 *
	 */
	public ChartModel() {
		// Initialize with invalid, dummy data
		this.dataSet = null;
		this.chartName = null;

		this.rows = null;
		this.columns = null;

		this.keysIndex = -1;
		this.valuesIndex = -1;
		this.categoryColumnIndex = 0; // defaults to first column
		this.chartType = -1;

		this.isLineChart = false;
		this.isThreeDimensional = false;
	}

	// ///////////////////////////////////////////////////////////////////////////
	// GETTERS / SETTERS
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * @param chartType
	 */
	public void setChartType(int chartType) {
		this.chartType = chartType;
	}

	/**
	 *
	 * @return int chartType
	 */
	public int getChartType() {
		return this.chartType;
	}

	/**
	 * @return String ChartName
	 */
	public String getChartName() {
		return chartName;
	}

	/**
	 *
	 * @param chartName
	 */
	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	/**
	 * @return DataSet
	 */
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * Checks whether the chart model has a dataset set.
	 *
	 * @return True if the dataset in this model is other than <code>null</code> ,
	 *         false else.
	 */
	public boolean hasDataSet() {
		return this.dataSet != null;
	}

	/**
	 * @param dataSet
	 */
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * @return int keysIndex
	 */
	public int getKeysIndex() {
		return keysIndex;
	}

	/**
	 * @param keysIndex
	 */
	public void setKeysIndex(int keysIndex) {
		this.keysIndex = keysIndex;
	}

	/**
	 * @return int valuesIndex
	 */
	public int getValuesIndex() {
		return valuesIndex;
	}

	/**
	 * @param valuesIndex
	 */
	public void setValuesIndex(int valuesIndex) {
		this.valuesIndex = valuesIndex;
	}

	/**
	 * @param rows
	 */
	public void setRows(int[] rows) {
		this.rows = rows;
	}

	/**
	 * @return rows
	 */
	public int[] getRows() {
		return this.rows;
	}

	/**
	 * @param columns
	 */
	public void setColumns(int[] columns) {
		this.columns = columns;
	}

	/**
	 * @return rows
	 */
	public int[] getColumns() {
		return this.columns;
	}

	/**
	 *
	 * @return isThreeDimensional
	 */
	public boolean isThreeDimensional() {
		return this.isThreeDimensional;
	}

	/**
	 *
	 * @param isThreeDimensional
	 */
	public void setThreeDimensional(boolean isThreeDimensional) {
		this.isThreeDimensional = isThreeDimensional;
	}

	/**
	 *
	 * @param columnIndex
	 */
	public void setCategoryColumnIndex(int columnIndex) {
		this.categoryColumnIndex = columnIndex;
	}

	/**
	 * Returns the index of the column used for grouping columns for each row in a
	 * bar chart.
	 *
	 * @return The index of the column used for grouping columns for each row in a
	 *         bar chart, or -1 if the index was not set yet.
	 */
	public int getCategoryColumnIndex() {
		return this.categoryColumnIndex;
	}

	/**
	 *
	 * @param isLineChart
	 */
	public void setLineChart(boolean isLineChart) {
		this.isLineChart = isLineChart;
	}

	/**
	 * Checks whether the chart type is a line chart. Line chart is a specialized
	 * case of a bar chart, so the chart type in the model also needs to be a bar
	 * chart.
	 *
	 * @return True if the chart type set in the model is a line chart, false else.
	 */
	public boolean isLineChart() {
		return this.chartType == ChartModel.CHART_BAR && this.isLineChart;
	}

	// ///////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * String representation of this chart model which means string representations
	 * of all chart model variables.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder output = new StringBuilder();
		output.append("********************************************************************************\n"); //$NON-NLS-1$
		output.append("Type: " + this.chartType + StringUtils.LF);
		output.append("Name: " + this.chartName + StringUtils.LF);
		output.append("Keys Index: " + this.keysIndex + StringUtils.LF);
		output.append("Values Index: " + this.valuesIndex + StringUtils.LF);
		output.append("Rows: " + ArrayUtils.toString(this.rows) + StringUtils.LF);
		output.append("Columns: " + ArrayUtils.toString(this.columns) + StringUtils.LF);
		output.append("Category Column Index: " + this.categoryColumnIndex + StringUtils.LF);
		output.append("Line Chart: " + this.isLineChart + StringUtils.LF);
		output.append("3D: " + this.isThreeDimensional + StringUtils.LF);
		output.append("Dataset:\n\n" + this.dataSet.toString());
		output.append("********************************************************************************\n"); //$NON-NLS-1$
		return output.toString();
	}

	// ///////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * @return true if this model is valid
	 */
	public boolean isValid() {
		return this.hasValidChartType()
				&& (this.isValidPieChart() || this.isValidBarChart() || this.isValidLineChart());
	}

	/**
	 *
	 * @return
	 */
	private boolean isValidPieChart() {
		return this.chartName != null && this.dataSet != null && this.chartType == ChartModel.CHART_PIE
				&& this.keysIndex != -1 && this.valuesIndex != -1 && this.rows != null && this.rows.length > 0;
	}

	/**
	 *
	 * @return
	 */
	private boolean isValidBarChart() {
		return this.chartName != null && this.dataSet != null && this.chartType == ChartModel.CHART_BAR
				&& this.categoryColumnIndex >= 0 && this.columns != null && this.columns.length > 0 && this.rows != null
				&& this.rows.length > 0 && !this.isLineChart;
	}

	/**
	 * Valid line charts have the same definitions as bar charts, as they're only a
	 * variation of those.
	 *
	 * @return
	 */
	private boolean isValidLineChart() {
		return this.chartName != null && this.dataSet != null && this.chartType == ChartModel.CHART_BAR
				&& this.categoryColumnIndex >= 0 && this.columns != null && this.columns.length > 0 && this.rows != null
				&& this.rows.length > 0 && this.isLineChart;
	}

	/**
	 * @return true, if this model has a valid chartType defined.
	 */
	public boolean hasValidChartType() {
		return this.chartType == ChartModel.CHART_BAR || this.chartType == ChartModel.CHART_PIE;
	}
}
