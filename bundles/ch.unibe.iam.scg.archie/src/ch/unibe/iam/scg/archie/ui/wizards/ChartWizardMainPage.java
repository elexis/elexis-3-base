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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.model.ChartModel;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.utils.DatasetHelper;

/**
 * <p>
 * Main page in the chart wizard. Contains buttons for selecting the chart type.
 * </p>
 * 
 * $Id: ChartWizardMainPage.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ChartWizardMainPage extends AbstractChartPage implements Listener {

	/**
	 * Name of this page.
	 */
	protected static final String PAGE_NAME = "ChartWizardMainPage";

	/**
	 * Buttons for chart type selection.
	 */
	private ToolItem pieItem;
	private ToolItem barItem;

	/**
	 * Public constructor.
	 */
	protected ChartWizardMainPage() {
		super(ChartWizardMainPage.PAGE_NAME, Messages.CHART_WIZARD_PAGE_TITLE, null);
		this.setDescription(Messages.CHART_WIZARD_PAGE_DESCRIPTION);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		GridData layoutData = new GridData(GridData.FILL_BOTH);

		container.setLayout(layout);
		container.setLayoutData(layoutData);

		// Toolbar layout
		GridLayout toolbarLayout = new GridLayout();
		GridData toolbarLayoutData = new GridData(GridData.FILL_BOTH);

		toolbarLayoutData.grabExcessHorizontalSpace = true;
		toolbarLayoutData.grabExcessVerticalSpace = true;
		toolbarLayoutData.horizontalAlignment = SWT.CENTER;
		toolbarLayoutData.verticalAlignment = SWT.CENTER;

		// Create toolbars
		ToolBar pieToolbar = new ToolBar(container, SWT.NONE);
		ToolBar barToolbar = new ToolBar(container, SWT.NONE);

		pieToolbar.setLayoutData(toolbarLayoutData);
		pieToolbar.setLayout(toolbarLayout);

		barToolbar.setLayoutData(toolbarLayoutData);
		barToolbar.setLayout(toolbarLayout);

		// Create toolbar items
		this.pieItem = new ToolItem(pieToolbar, SWT.RADIO | SWT.CENTER);
		this.pieItem.setImage(ArchieActivator.getImage(ArchieActivator.IMG_CHART_PIE_BIG));
		this.pieItem.setText(Messages.CHART_WIZARD_PAGE_TEXT_PIE_CHART);
		this.pieItem.setEnabled(this.hasValidDataset());
		this.pieItem.addListener(SWT.Selection, this);

		this.barItem = new ToolItem(barToolbar, SWT.RADIO | SWT.CENTER);
		this.barItem.setImage(ArchieActivator.getImage(ArchieActivator.IMG_CHART_BAR_BIG));
		this.barItem.setText(Messages.CHART_WIZARD_PAGE_TEXT_BAR_CHART);
		this.barItem.setEnabled(this.hasValidDataset());
		this.barItem.addListener(SWT.Selection, this);

		this.setControl(container);
		this.applyToStatusLine(this.getErrorStatus());
	}

	// ////////////////////////////////////////////////////////////////////////////
	// INTERFACE FUNCTIONS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event) {
		ToolItem eventItem = (ToolItem) event.widget;

		// Set clicked item as selected
		ChartWizardMainPage.this.pieItem.setSelection(eventItem == ChartWizardMainPage.this.pieItem);
		ChartWizardMainPage.this.barItem.setSelection(eventItem == ChartWizardMainPage.this.barItem);

		// Set chart type in model
		ChartWizardMainPage.this.setModelChartType();
		ChartWizardMainPage.this.getWizard().getContainer().updateButtons();
		ChartWizardMainPage.this.setPageComplete(true);
	}

	// ///////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPER FUNCTIONS
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether the current dataset is valid for chart generation. This
	 * means that the dataset has to have at least one numeric columns to be
	 * able to generate values in charts.
	 * 
	 * @return True if the current dataset is valid, false else.
	 */
	private boolean hasValidDataset() {
		DataSet dataset = ((ChartWizard) this.getWizard()).getModel().getDataSet();
		return DatasetHelper.hasNumericColumn(dataset) || DatasetHelper.hasMoneyColumn(dataset);
	}

	/**
	 * Returns the error status based on the dataset validity.
	 * 
	 * @return True if graphs can be created from the current dataset, false
	 *         else.
	 */
	private IStatus getErrorStatus() {
		// Initialize a variable with the no error status
		Status status = new Status(IStatus.OK, ArchieActivator.PLUGIN_NAME, 0, "", null);

		if (!this.hasValidDataset()) {
			status = new Status(IStatus.ERROR, ArchieActivator.PLUGIN_NAME, 0,
					Messages.CHART_WIZARD_PAGE_ERROR_DATASET, null);
		}

		return status;
	}

	/**
	 * Sets the chart type in the chart model according to the currently
	 * selected item.
	 */
	private void setModelChartType() {
		ChartModel model = ((ChartWizard) ChartWizardMainPage.this.getWizard()).getModel();
		model.setChartType(this.pieItem.getSelection() ? ChartModel.CHART_PIE : ChartModel.CHART_BAR);
	}

	// ///////////////////////////////////////////////////////////////////////////
	// OVERRIDE FUNCTIONS
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canFlipToNextPage() {
		return ((ChartWizard) ChartWizardMainPage.this.getWizard()).getModel().hasValidChartType()
				&& this.hasValidDataset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IWizardPage getNextPage() {
		IWizard wizard = this.getWizard();
		return (this.pieItem.getSelection() ? wizard.getPage(PieChartPage.PAGE_NAME) : wizard
				.getPage(BarChartPage.PAGE_NAME));
	}
}