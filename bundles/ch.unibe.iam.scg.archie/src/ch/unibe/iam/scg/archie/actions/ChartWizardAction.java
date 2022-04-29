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

/**
 * <p>Contains all actions used by the Eclipse framework. Actions
 * are important parts of this program as they are the main source
 * of triggering functionality of Archie.</p>
 */
package ch.unibe.iam.scg.archie.actions;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.ui.util.Log;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.controller.ChartModelManager;
import ch.unibe.iam.scg.archie.controller.ProviderChartFactory;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.model.ChartModel;
import ch.unibe.iam.scg.archie.ui.views.ChartView;
import ch.unibe.iam.scg.archie.ui.wizards.ChartWizard;

/**
 * <p>
 * Action for starting the chart generation wizard.
 * </p>
 *
 * $Id: ChartWizardAction.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ChartWizardAction extends Action {

	/**
	 * Constant for the default wizard page height.
	 */
	private static final int WIZARD_PAGE_HEIGHT = 600;

	/**
	 * Constant for the default wizard page width.
	 */
	private static final int WIZARD_PAGE_WIDTH = 400;

	/**
	 * Constructs a new ChartWizardAction
	 */
	public ChartWizardAction() {
		super(Messages.CHART_WIZARD_TITLE, AS_PUSH_BUTTON);

		this.setToolTipText(Messages.CHART_WIZARD_DESCRIPTION);
		this.setImageDescriptor(ArchieActivator.getImageDescriptor("icons/chart_pie.png"));

		this.setEnabled(false);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		ChartWizard chartWizard = new ChartWizard();
		WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), chartWizard);

		wizardDialog.setPageSize(ChartWizardAction.WIZARD_PAGE_WIDTH, ChartWizardAction.WIZARD_PAGE_HEIGHT);
		wizardDialog.setBlockOnOpen(true);
		wizardDialog.create();

		// create the dialog and check for response
		if (WizardDialog.OK == wizardDialog.open()) {

			// get model from wizard
			ChartModel chartModel = chartWizard.getModel();

			// set models
			ProviderChartFactory.getInstance().setChartModel(chartModel);
			ChartModelManager.getInstance().setChartModel(chartModel);

			// check if the chart view is already open and set dirty if needed
			this.setExistingViewDirty();

			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChartView.ID);
			} catch (PartInitException e) {
				ArchieActivator.LOG.log("Could not create the chart view." + StringUtils.LF + e.getLocalizedMessage(),
						Log.ERRORS);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets the chart view dirty. This means that the view will have to refresh /
	 * recreate it's contents according to the current situation reflected by the
	 * chart factory and it's chart model.
	 */
	private void setExistingViewDirty() {
		// check if the view already was initialized
		ChartView chartView = (ChartView) this.getView(ChartView.ID);
		if (chartView != null) {
			chartView.setDirty();
			System.out.println(chartView.getTitle());
		}
	}

	/**
	 * Retrieves a view with the given ID from the view references registry.
	 *
	 * @param id View id.
	 * @return IViewPart View based on the given ID, null else.
	 */
	private IViewPart getView(String id) {
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			if (id.equals(viewReferences[i].getId())) {
				return viewReferences[i].getView(false);
			}
		}
		return null;
	}
}