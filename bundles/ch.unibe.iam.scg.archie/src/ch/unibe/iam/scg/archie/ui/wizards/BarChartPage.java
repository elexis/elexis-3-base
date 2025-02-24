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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.controller.ChartModelManager;
import ch.unibe.iam.scg.archie.model.ChartModel;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.ui.widgets.TextWidget;
import ch.unibe.iam.scg.archie.utils.ArrayUtils;
import ch.unibe.iam.scg.archie.utils.DatasetHelper;
import ch.unibe.iam.scg.archie.utils.SWTUtils;

/**
 * <p>
 * Chart wizard page for setting parameters for bar charts.
 * </p>
 *
 * $Id: BarChartPage.java 734 2009-03-23 12:11:13Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 734 $
 */
public class BarChartPage extends AbstractChartPage implements Listener {

	/**
	 * Name of this page.
	 */
	protected static final String PAGE_NAME = "BarChartPage"; //$NON-NLS-1$

	/**
	 * Constant for bar chart type in the combo box.
	 */
	private static final int TYPE_BAR = 0;

	/**
	 * Constant for line chart type in the combo box.
	 */
	private static final int TYPE_LINE = 1;

	private TextWidget chartName;
	private Combo chartType;
	private Combo rowTitle;

	/**
	 * Maps the buttons to the column index of the dataset. We had to implement this
	 * because subclassing SWT objects other than Composites is generally a bad
	 * practice. That's why we had to abandon our custom Button class and go with
	 * the map.
	 */
	private HashMap<Button, Integer> columnButtons;

	private Button threeDimensional;

	/**
	 * Constructs BarChartPage
	 */
	public BarChartPage() {
		super(BarChartPage.PAGE_NAME, Messages.CHART_WIZARD_BAR_CHART_PAGE_TITLE,
				ArchieActivator.getImageDescriptor("icons/chart_bar_big.png")); //$NON-NLS-1$
		super.setDescription(Messages.CHART_WIZARD_BAR_CHART_PAGE_DESCRIPTION);

		this.columnButtons = new HashMap<Button, Integer>();
	}

	// ////////////////////////////////////////////////////////////////////////////
	// INTERFACE FUNCTIONS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 *      Event)
	 */
	public void handleEvent(Event event) {
		// Initialize a variable with the no error status
		Status status = new Status(IStatus.OK, ArchieActivator.PLUGIN_NAME, 0, StringUtils.EMPTY, null);

		// throw an error if nothing selected
		int[] selected = this.getSelectedIndexes();
		if (selected.length <= 0) {
			status = new Status(IStatus.ERROR, ArchieActivator.PLUGIN_NAME, 0,
					Messages.CHART_WIZARD_BAR_CHART_ERROR_ONE_COLUMN, null);
		}

		// apply status
		this.applyToStatusLine(status);

		this.getWizard().getContainer().updateButtons();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		ScrolledComposite scrolled = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);

		Composite composite = new Composite(scrolled, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		SWTUtils.createLabel(composite).setText(Messages.CHART_WIZARD_BAR_CHART_TEXT_CHART_TYPE);

		// initialize chart type combo
		this.chartType = new Combo(composite, SWT.READ_ONLY);
		this.chartType.add(Messages.CHART_WIZARD_BAR_CHART_BAR_CHART, BarChartPage.TYPE_BAR);
		this.chartType.add(Messages.CHART_WIZARD_BAR_CHART_LINE_CHART, BarChartPage.TYPE_LINE);
		this.chartType.select(BarChartPage.TYPE_BAR);
		this.chartType.addListener(SWT.Selection, this);

		SWTUtils.createLabel(composite).setText(Messages.CHART_WIZARD_BAR_CHART_TEXT_EXPLANATON);

		this.chartName = new TextWidget(composite, SWT.NONE, Messages.CHART_WIZARD_BAR_CHART_TEXT_NAME_CHART, null);
		this.chartName.addListener(SWT.KeyUp, this);

		// add separator
		SWTUtils.createSpacedSeparator(composite, SWT.HORIZONTAL);

		DataSet dataset = ((ChartWizard) this.getWizard()).getModel().getDataSet();
		List<String> columns = dataset.getHeadings();

		SWTUtils.createLabel(composite).setText(Messages.CHART_WIZARD_BAR_CHART_TEXT_COLUMN_ROW_LABEL);

		this.rowTitle = new Combo(composite, SWT.READ_ONLY);
		this.rowTitle.setItems(columns.toArray(new String[0]));

		SWTUtils.createLabel(composite).setText(Messages.CHART_WIZARD_BAR_CHART_TEXT_COLUMNS_CATEGORIES);

		// add buttons
		this.addButtons(composite, dataset);

		// add separator
		SWTUtils.createSpacedSeparator(composite, SWT.HORIZONTAL);

		SWTUtils.createLabel(composite).setText(Messages.CHART_WIZARD_BAR_CHART_TEXT_3D_EXPLANATION);

		this.threeDimensional = new Button(composite, SWT.CHECK);
		this.threeDimensional.setText(Messages.CHART_WIZARD_BAR_CHART_TEXT_3D);
		this.threeDimensional.setToolTipText(Messages.CHART_WIZARD_BAR_CHART_TEXT_3D_TOOLTIP);
		this.threeDimensional.addListener(SWT.Selection, this);

		// compute composite widht and height
		parent.pack();
		Point size = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		scrolled.setContent(composite);
		scrolled.setMinHeight(size.y + 100);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);

		// set data from already used model if available
		this.initializePreviousModelData();

		this.setControl(scrolled);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// PRIVATE HELPER FUNCTIONS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates the buttons for all "numeric" columns in a dataset. These columns can
	 * be chosen for values in the chart. Adds the buttons to the list of column
	 * buttons.
	 */
	private void addButtons(Composite parent, DataSet dataset) {
		List<String> columns = dataset.getHeadings();

		// add buttons for columns that have numeric values
		for (int index = 0; index < columns.size(); index++) {
			boolean firstColumn = index <= 0;
			if ((DatasetHelper.isNumericColumn(dataset, index) || DatasetHelper.isMoneyColumn(dataset, index))
					&& !firstColumn) {
				Button columnButton = new Button(parent, SWT.CHECK);
				columnButton.setText(columns.get(index));
				columnButton.addListener(SWT.Selection, this);
				this.columnButtons.put(columnButton, new Integer(index));
			}
		}
	}

	/**
	 * Saves the gathered data to the model.
	 */
	private void saveDataToModel() {
		// Gets the model
		final ChartWizard wizard = (ChartWizard) this.getWizard();
		final ChartModel chartModel = wizard.getModel();

		// Set chart columns
		int[] indexes = this.getSelectedIndexes();
		int rowTitleIndex = this.rowTitle.getSelectionIndex();
		if (indexes.length > 0) {
			chartModel.setLineChart(this.chartType.getSelectionIndex() == BarChartPage.TYPE_LINE);
			chartModel.setColumns(indexes);
			chartModel.setChartName(this.chartName.getValue().toString());
			chartModel.setCategoryColumnIndex(rowTitleIndex > -1 ? rowTitleIndex : 0);
			chartModel.setThreeDimensional(this.threeDimensional.getSelection());
		}
	}

	/**
	 * Initialized the UI fields with data from previous bar chart model
	 * configuration, if available.
	 */
	private void initializePreviousModelData() {
		ChartModelManager manager = ChartModelManager.getInstance();
		if (manager.hasBarChartModel()) {

			ChartModel model = manager.getBarChartModel();

			this.chartName.setValue(model.getChartName());
			this.chartType.select(model.isLineChart() ? BarChartPage.TYPE_LINE : BarChartPage.TYPE_BAR);
			this.rowTitle.select(model.getCategoryColumnIndex());
			this.threeDimensional.setSelection(model.isThreeDimensional());

			int[] columns = model.getColumns();
			for (Entry<Button, Integer> entry : this.columnButtons.entrySet()) {
				if (ArrayUtils.inArray(columns, entry.getValue().intValue())) {
					entry.getKey().setSelection(true);
				}
			}
		}
	}

	/**
	 * Returns the number of selected items in the column buttons list.
	 *
	 * @return Number of selected column buttons.
	 */
	private int getNumberSelected() {
		return this.getSelectedIndexes().length;
	}

	/**
	 * Array of selected indexes of the column buttons.
	 *
	 * @return Array of selected indexes of the column buttons.
	 */
	private int[] getSelectedIndexes() {
		ArrayList<Integer> selected = new ArrayList<Integer>();
		for (Entry<Button, Integer> entry : this.columnButtons.entrySet()) {
			if (entry.getKey().getSelection()) {
				selected.add(entry.getValue().intValue());
			}
		}

		int[] indexes = new int[selected.size()];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = selected.get(i).intValue();
		}

		return indexes;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// OVERRIDE FUNCTIONS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getPreviousPage()
	 */
	@Override
	public IWizardPage getPreviousPage() {
		return this.getWizard().getPage(ChartWizardMainPage.PAGE_NAME);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		this.saveDataToModel();
		return this.getWizard().getPage(ContentSelectionPage.PAGE_NAME);
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return this.chartName != null && this.chartName.getValue() != null
				&& !this.chartName.getValue().toString().equals(StringUtils.EMPTY) && this.getErrorMessage() == null
				&& this.getNumberSelected() > 0;
	}
}