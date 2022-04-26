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

import ch.unibe.iam.scg.archie.model.ChartModel;

/**
 * <p>
 * Manages Chart Models.
 * </p>
 *
 * $Id: ChartModelManager.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ChartModelManager {

	/**
	 * Instance of this chart model manager. There's always only one through the
	 * entire lifecycle of this application.
	 */
	private static ChartModelManager INSTANCE;

	/**
	 * The currently managed bar chart model.
	 */
	private ChartModel barChartModel;

	/**
	 * The currently managed pie chart model.
	 */
	private ChartModel pieChartModel;

	/**
	 * Private constructor.
	 */
	private ChartModelManager() {
		this.barChartModel = null;
		this.pieChartModel = null;
	}

	/**
	 * Returns an instance of this chart model manager.
	 *
	 * @return An instance of this chart model manager.
	 */
	public static ChartModelManager getInstance() {
		if (ChartModelManager.INSTANCE == null) {
			ChartModelManager.INSTANCE = new ChartModelManager();
		}
		return ChartModelManager.INSTANCE;
	}

	/**
	 * @return the barChartModel
	 */
	public ChartModel getBarChartModel() {
		return this.barChartModel;
	}

	/**
	 * @param barChartModel the barChartModel to set
	 */
	public void setBarChartModel(ChartModel barChartModel) {
		if (barChartModel.getChartType() != ChartModel.CHART_BAR) {
			throw new IllegalArgumentException("This method can only be used for bar chart models.");
		}
		this.barChartModel = barChartModel;
	}

	/**
	 *
	 * @param chartModel
	 */
	public void setChartModel(ChartModel chartModel) {
		if (chartModel.getChartType() == ChartModel.CHART_PIE) {
			this.setPieChartModel(chartModel);
		} else if (chartModel.getChartType() == ChartModel.CHART_BAR) {
			this.setBarChartModel(chartModel);
		} else {
			throw new IllegalArgumentException("The model has to have a valid chart type.");
		}
	}

	/**
	 * @return the pieChartModel
	 */
	public ChartModel getPieChartModel() {
		return this.pieChartModel;
	}

	/**
	 * @param pieChartModel the pieChartModel to set
	 */
	public void setPieChartModel(ChartModel pieChartModel) {
		if (pieChartModel.getChartType() != ChartModel.CHART_PIE) {
			throw new IllegalArgumentException("This method can only be used for pie chart models.");
		}
		this.pieChartModel = pieChartModel;
	}

	/**
	 *
	 * @param type
	 * @return whether we have a model or not.
	 */
	public boolean hasChartModel(int type) {
		if (type == ChartModel.CHART_PIE) {
			return this.pieChartModel != null;
		} else if (type == ChartModel.CHART_BAR) {
			return this.barChartModel != null;
		}
		return false;
	}

	/**
	 * @return whether we have a PieChartModel or not.
	 */
	public boolean hasPieChartModel() {
		return this.hasChartModel(ChartModel.CHART_PIE);
	}

	/**
	 *
	 * @return whether we have a BarChartModel or not.
	 */
	public boolean hasBarChartModel() {
		return this.hasChartModel(ChartModel.CHART_BAR);
	}

	/**
	 * Removes all saved chart models.
	 */
	public void clean() {
		this.barChartModel = null;
		this.pieChartModel = null;
	}
}