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

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * <p>
 * Chart composite displaying the patients chart in the dashboard.
 * </p>
 * 
 * $Id: PatientsChart.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class PatientsChart extends AbstractChartComposite {

	private static final String CHART_TITLE = "Patients Gender";

	/**
	 * @param parent
	 * @param style
	 */
	public PatientsChart(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see ch.unibe.iam.scg.archie.ui.charts.AbstractChartComposite#
	 *      initializeCreator()
	 */
	@Override
	protected AbstractDatasetCreator initializeCreator() {
		return new PatientDatasetCreator(PatientsChart.CHART_TITLE);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see ch.unibe.iam.scg.archie.ui.charts.AbstractChartComposite#
	 *      initializeChart()
	 */
	@Override
	protected JFreeChart initializeChart() {
		// create a chart...
		JFreeChart chart = ChartFactory.createPieChart(PatientsChart.CHART_TITLE, (DefaultPieDataset) this.creator
				.getDataset(), true, // legend?
				true, // tooltips?
				false // URLs?
				);

		// Set chart background color to it's parents background
		chart.setBackgroundPaint(new Color(this.parent.getBackground().getRed(),
				this.parent.getBackground().getGreen(), this.parent.getBackground().getBlue()));

		return chart;
	}
}