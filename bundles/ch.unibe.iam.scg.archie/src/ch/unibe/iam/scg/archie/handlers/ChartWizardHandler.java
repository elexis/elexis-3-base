package ch.unibe.iam.scg.archie.handlers;

import org.apache.commons.lang3.StringUtils;

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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.controller.ChartModelManager;
import ch.unibe.iam.scg.archie.controller.ProviderChartFactory;
import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.model.ChartModel;
import ch.unibe.iam.scg.archie.ui.views.ChartView;
import ch.unibe.iam.scg.archie.ui.views.StatisticsView;
import ch.unibe.iam.scg.archie.ui.wizards.ChartWizard;

/**
 * <p>
 * Handler for starting the chart generation wizard.
 * </p>
 *
 * @author Peter Siska
 * @author Dennis Schenk
 */
public class ChartWizardHandler extends AbstractHandler {

	/**
	 * Constant for the default wizard page height.
	 */
	private static final int WIZARD_PAGE_HEIGHT = 600;

	/**
	 * Constant for the default wizard page width.
	 */
	private static final int WIZARD_PAGE_WIDTH = 400;

	private StatisticsView view;
	private static Logger log = LoggerFactory.getLogger(ExportHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = activePage.findView("ch.unibe.iam.scg.archie.ui.views.StatisticsView");
		if (viewPart instanceof StatisticsView) {
			this.view = (StatisticsView) viewPart;
		} else {
			log.error("chartWizardHandler: StatisticsView not found");
			return null;
		}
		if (ProviderManager.getInstance().getProvider() == null && this.view.getResultPanel() == null) {
			SWTHelper.showError(Messages.CHART_WIZARD_TITLE + " " + Messages.ERROR, Messages.ERROR_SELECT_DATA);
		} else {
			ChartWizard chartWizard = new ChartWizard();
			WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), chartWizard);

			wizardDialog.setPageSize(ChartWizardHandler.WIZARD_PAGE_WIDTH, ChartWizardHandler.WIZARD_PAGE_HEIGHT);
			wizardDialog.setBlockOnOpen(true);
			wizardDialog.create();

			// create the dialog and check for response
			if (WizardDialog.OK == wizardDialog.open()) {

				// get model from wizard
				ChartModel chartModel = chartWizard.getModel();

				// set models
				ProviderChartFactory.getInstance().setChartModel(chartModel);
				ChartModelManager.getInstance().setChartModel(chartModel);

				// check if the chart view is already open and set dirty if
				// needed
				this.setExistingViewDirty();

				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChartView.ID);
				} catch (PartInitException e) {
					ArchieActivator.LOG.log(
							"Could not create the chart view." + StringUtils.LF + e.getLocalizedMessage(), //$NON-NLS-1$
							Log.ERRORS);
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private void setExistingViewDirty() {
		// check if the view already was initialized
		IViewPart chartView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ChartView.ID);
		if (chartView instanceof ChartView) {
			((ChartView) chartView).setDirty();
			System.out.println(chartView.getTitle());
		}
	}

}
