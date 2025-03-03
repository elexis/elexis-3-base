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
package ch.unibe.iam.scg.archie.ui.charts;

import java.awt.Color;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

/**
 * <p>
 * Creates a chart showing how much profit, income and costs we had with
 * consultations of the last six months.
 * </p>
 *
 * $Id: ConsultationMoneyChart.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ConsultationMoneyChart extends AbstractChartComposite {

	private static final String CHART_TITLE = "Consultation Money";

	/**
	 * @param parent
	 * @param style
	 */
	public ConsultationMoneyChart(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @see ch.unibe.iam.scg.archie.ui.charts.AbstractChartComposite#
	 *      initializeChart()
	 */
	@Override
	protected JFreeChart initializeChart() {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(ConsultationMoneyChart.CHART_TITLE, // title
				StringUtils.EMPTY, // x-axis label
				"Amount", // y-axis label
				(XYDataset) this.creator.getDataset(), // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
		);

		// Set chart background color to it's parents background
		chart.setBackgroundPaint(new Color(this.parent.getBackground().getRed(), this.parent.getBackground().getGreen(),
				this.parent.getBackground().getBlue()));

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy")); //$NON-NLS-1$

		return chart;
	}

	/**
	 * @see ch.unibe.iam.scg.archie.ui.charts.AbstractChartComposite#
	 *      initializeCreator()
	 */
	@Override
	protected AbstractDatasetCreator initializeCreator() {
		return new ConsultationMoneyDatasetCreator(ConsultationMoneyChart.CHART_TITLE);
	}

	/**
	 * @see ch.unibe.iam.scg.archie.ui.charts.AbstractChartComposite#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
	}

}
