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
package ch.unibe.iam.scg.archie.controller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import ch.rgw.tools.Money;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.model.ChartModel;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.ui.GraphicalMessage;

/**
 * <p>
 * Factory singleton object for creating chart composited based on JFreeChart
 * charts and our custom chart model.
 * </p>
 * 
 * $Id: ProviderChartFactory.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ProviderChartFactory {

	/**
	 * Instance of this chart factory. There's always only one through the
	 * entire lifecycle of this application.
	 */
	private static ProviderChartFactory INSTANCE;

	/**
	 * The currently managed data provider
	 */
	private ChartModel model;

	/**
	 * Private constructor.
	 */
	private ProviderChartFactory() {
		this.model = null;
	}

	/**
	 * Returns an instance of this provider manager.
	 * 
	 * @return An instance of this provider manager.
	 */
	public static ProviderChartFactory getInstance() {
		if (ProviderChartFactory.INSTANCE == null) {
			ProviderChartFactory.INSTANCE = new ProviderChartFactory();
		}
		return ProviderChartFactory.INSTANCE;
	}

	/**
	 * @param model
	 */
	public void setChartModel(ChartModel model) {
		this.model = model;
		assert (this.model != null);
	}

	/**
	 * Creates a chart from the currently set chart model and attaches it to the
	 * given parent.
	 * 
	 * @param parent
	 *            Chart composite cotainer.
	 * @return Composite containing the chart just created.
	 */
	public Composite createChart(Composite parent) {
		// set layout of parent container
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// create an error message if no model present
		if (this.model == null) {
			return new GraphicalMessage(parent, ArchieActivator.getImage(ArchieActivator.IMG_ERROR),
					Messages.NO_CHART_MODEL);
		}

		// else return a chart composite based on the chart type
		if (this.model.getChartType() == ChartModel.CHART_PIE) {
			return this.createPieChart(parent);
		}
		return this.createBarChart(parent);
	}

	/**
	 * @param parent
	 * @return Composite with a Chart
	 */
	private Composite createPieChart(Composite parent) {
		// create the dataset...
		DataSet dataset = this.model.getDataSet();
		DefaultPieDataset pieDataset = this.createJFreePieDataset(dataset);

		// create the chart...
		JFreeChart chart = this.createJFreePieChart(pieDataset);

		// add subtitles
		TextTitle subtitle = new TextTitle(dataset.getHeadings().get(this.model.getValuesIndex()) + " per "
				+ dataset.getHeadings().get(this.model.getKeysIndex()));
		chart.addSubtitle(subtitle);

		ChartComposite chartComposite = new ChartComposite(parent, SWT.NONE, chart);
		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return chartComposite;
	}

	/**
	 * 
	 * @param pieDataset
	 * @return
	 */
	private JFreeChart createJFreePieChart(PieDataset pieDataset) {
		if (this.model.isThreeDimensional()) {
			return ChartFactory.createPieChart3D(this.model.getChartName(), pieDataset, false, true, false);
		}
		return ChartFactory.createPieChart(this.model.getChartName(), pieDataset, false, true, false);
	}

	/**
	 * 
	 * @param dataset
	 * @return
	 */
	private DefaultPieDataset createJFreePieDataset(DataSet dataset) {
		DefaultPieDataset pieDataset = new DefaultPieDataset();

		Object[] keys = dataset.getColumn(this.model.getKeysIndex());
		Object[] values = dataset.getColumn(this.model.getValuesIndex());

		int[] rows = this.model.getRows();

		for (int i = 0; i < rows.length; i++) {
			double value = 0.0;
			int rowIndex = rows[i];

			if (values[rowIndex] instanceof Money) {
				value = ((Money) values[rowIndex]).doubleValue();
			} else {
				value = new Double(values[rowIndex].toString());
			}
			pieDataset.setValue(keys[rowIndex].toString(), value);
		}

		return pieDataset;
	}

	/**
	 * 
	 * @param parent
	 * @return Composite with a Chart
	 */
	private Composite createBarChart(Composite parent) {
		// create a dataset...
		DataSet dataset = this.model.getDataSet();
		DefaultCategoryDataset barDataset = this.createJFreeBarDataset(dataset);

		// create the chart
		JFreeChart chart = this.createJFreeBarChart(barDataset);

		ChartComposite chartComposite = new ChartComposite(parent, SWT.NONE, chart);
		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return chartComposite;
	}

	/**
	 * @param dataset
	 * @return
	 */
	private DefaultCategoryDataset createJFreeBarDataset(DataSet dataset) {
		DefaultCategoryDataset categoryDataSet = new DefaultCategoryDataset();

		int[] rows = this.model.getRows();
		int[] columns = this.model.getColumns();

		int rowTitleColumnIndex = this.model.getCategoryColumnIndex();

		for (int i = 0; i < rows.length; i++) {
			int rowIndex = rows[i];

			Comparable<?>[] row = dataset.getRow(rowIndex);

			String rowTitle = row[rowTitleColumnIndex].toString();

			for (int j = 0; j < columns.length; j++) {
				double value = 0.0;
				int columnIndex = columns[j];

				String columnTitle = (String) dataset.getHeadings().get(columnIndex);

				Comparable<?> cell = dataset.getCell(rowIndex, columnIndex);

				if (cell instanceof Money) {
					value = ((Money) cell).doubleValue();
				} else {
					value = new Double(cell.toString());
				}

				categoryDataSet.addValue(value, columnTitle, rowTitle);
			}
		}

		return categoryDataSet;
	}

	/**
	 * 
	 * @param pieDataset
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private JFreeChart createJFreeBarChart(CategoryDataset barDataset) {
		if (this.model.isThreeDimensional() && this.model.isLineChart()) {
			return ChartFactory.createLineChart3D(this.model.getChartName(), "Category", "Value", barDataset,
					PlotOrientation.VERTICAL, true, true, false);
		} else if (this.model.isThreeDimensional() && !this.model.isLineChart()) {
			return ChartFactory.createBarChart3D(this.model.getChartName(), "Category", "Value", barDataset,
					PlotOrientation.VERTICAL, true, true, false);
		} else if (this.model.isLineChart()) {
			JFreeChart chart = ChartFactory.createLineChart(this.model.getChartName(), "Category", "Value", barDataset,
					PlotOrientation.VERTICAL, true, true, false);

			LineAndShapeRenderer renderer = (LineAndShapeRenderer) ((CategoryPlot) chart.getPlot()).getRenderer();
			renderer.setShapesVisible(true);
			renderer.setShapesFilled(true);

			return chart;
		}
		return ChartFactory.createBarChart(this.model.getChartName(), "Category", "Value", barDataset,
				PlotOrientation.VERTICAL, true, true, false);
	}
}
