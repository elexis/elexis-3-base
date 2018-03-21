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
package ch.unibe.iam.scg.archie.ui.wizards;

import org.eclipse.jface.wizard.Wizard;

import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.model.ChartModel;

/**
 * <p>
 * Main entry point for the chart wizard. Contains all the necessary chart
 * wizard pages for building a chart model.
 * </p>
 * 
 * $Id: ChartWizard.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */

public class ChartWizard extends Wizard {

	private ChartModel chartModel;

	/**
	 * Pages in this wizard
	 */
	private ChartWizardMainPage mainPage;
	private PieChartPage pieChartPage;
	private BarChartPage barChartPage;
	private ContentSelectionPage selectionPage;

	/**
	 * Constructs a ChartWizard
	 */
	public ChartWizard() {
		super();

		assert ProviderManager.getInstance().hasProvider();

		// create a new chart model and add a CLONED dataset
		this.chartModel = new ChartModel();
		this.chartModel.setDataSet(ProviderManager.getInstance().getProvider().getDataSet().clone());

		assert this.chartModel.hasDataSet();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return true;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return this.getContainer().getCurrentPage() instanceof ContentSelectionPage && this.chartModel.isValid();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		// main page
		this.mainPage = new ChartWizardMainPage();
		this.addPage(this.mainPage);

		// consecutive pages
		this.pieChartPage = new PieChartPage();
		this.addPage(this.pieChartPage);

		this.barChartPage = new BarChartPage();
		this.addPage(this.barChartPage);

		// final page
		this.selectionPage = new ContentSelectionPage();
		this.addPage(this.selectionPage);
	}

	/**
	 * @return ChartModel
	 */
	public ChartModel getModel() {
		return this.chartModel;
	}
}