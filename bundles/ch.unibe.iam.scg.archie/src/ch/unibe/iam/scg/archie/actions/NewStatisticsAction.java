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
package ch.unibe.iam.scg.archie.actions;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.controller.ChartModelManager;
import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.controller.TableFactory;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.model.SetDataException;
import ch.unibe.iam.scg.archie.ui.ParametersPanel;
import ch.unibe.iam.scg.archie.ui.ProviderInformatioPanel;
import ch.unibe.iam.scg.archie.ui.ResultPanel;
import ch.unibe.iam.scg.archie.ui.views.StatisticsView;

/**
 * <p>
 * This action is responsible for the whole procedure of creating a new query:
 * getting all information needed from the user, starting the query in the
 * background and updating the view in the end.
 * </p>
 *
 * $Id: NewStatisticsAction.java 727 2009-01-23 14:26:46Z hephster $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 727 $
 */
public class NewStatisticsAction extends Action implements IJobChangeListener, Observer {

	private StatisticsView view;
	private ParametersPanel parameters;
	private ProviderInformatioPanel providerInformation;
	private ArrayList<IPropertyChangeListener> listeners;

	/** constant for a running job */
	public static final String JOB_RUNNING = "JOB_RUNNING"; //$NON-NLS-1$

	/** constant for a finished job */
	public static final String JOB_DONE = "JOB_DONE"; //$NON-NLS-1$

	/**
	 * Action for creating a new statistical analysis. This class serves as a
	 * controller and mediator between the main and sidebar view. It also acts as a
	 * job listener and listens to the job this actions data provider runs.
	 *
	 * @param parameters Panel containing a provider's parameters.
	 */
	public NewStatisticsAction(ParametersPanel parameters) {
		super(Messages.ACTION_NEWSTAT_TITLE, AS_PUSH_BUTTON);

		// register as observer
		ProviderManager.getInstance().addObserver(this);

		this.setToolTipText(Messages.ACTION_NEWSTAT_DESCRIPTION);
		this.setImageDescriptor(ArchieActivator.getImageDescriptor("icons/database_go.png")); //$NON-NLS-1$

		// disabled by default
		this.setEnabled(false);

		this.parameters = parameters;
		this.listeners = new ArrayList<IPropertyChangeListener>();
	}

	/**
	 * This actions main method, called when the action is run.
	 */
	@Override
	public void run() {
		// user set a provider and all fields are valid
		if (ProviderManager.getInstance().hasProvider() && this.parameters.allFieldsValid()) {

			// get provider from manager
			AbstractDataProvider provider = ProviderManager.getInstance().getProvider();

			// try settings parameters in provider
			try {
				this.parameters.updateProviderParameters();
			} catch (SetDataException e) {
				SWTHelper.showError(Messages.ERROR, e.getMessage());
				return;
			} catch (Exception e) {
				ArchieActivator.LOG.log(Messages.ACTION_NEWSTAT_ERROR_COULDNT_UPDATE_PROVIDER + StringUtils.SPACE
						+ provider.getName() + ".\n" + e.getLocalizedMessage(), Log.WARNINGS); //$NON-NLS-1$
				e.printStackTrace();
			}

			// focus view
			try {
				this.view = (StatisticsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView(StatisticsView.ID);
			} catch (PartInitException e) {
				ArchieActivator.LOG.log(
						Messages.ACTION_NEWSTAT_ERROR_COULDNT_INIT_VIEW + StringUtils.LF + e.getLocalizedMessage(),
						Log.WARNINGS);
				e.printStackTrace();
			}

			// clean view
			this.view.clean();
			this.setEnabled(false);

			// add provider information
			// NOTE: Currently provider information is being handled here. Room
			// for improvement...
			this.providerInformation = new ProviderInformatioPanel(this.view.getParent());
			this.providerInformation.updateProviderInformation(provider);

			// set result composite and layout
			ResultPanel resultComposite = new ResultPanel(this.view.getParent(), SWT.FLAT);
			this.view.setResultComposite(resultComposite);
			this.view.getParent().layout();

			// update listeners
			this.updateListeners();

			// run the job
			provider.schedule();
			provider.addJobChangeListener(this);
		} else {
			SWTHelper.showError(Messages.ERROR_FIELDS_NOT_VALID_TITLE, Messages.ERROR_FIELDS_NOT_VALID);
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// INTERFACE FUNCTIONS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * This method is being called as soon as the job this action observes,
	 * finishes. The action is enabled as soon as the last job finishes. This method
	 * also creates and sets the result table in the result view as well as
	 * information about the parameters of the active provider in the header of the
	 * result panel.
	 *
	 * @param event
	 */
	public void done(final IJobChangeEvent event) {
		// allow other threads to update this UI thread
		// @see http://www.eclipse.org/swt/faq.php#uithread
		UiDesk.getDisplaySafe().ifPresent(d -> {
			d.syncExec(() -> {
				final ResultPanel results = NewStatisticsAction.this.view.getResultPanel();
				if (results != null && !results.isDisposed()) {
					final AbstractDataProvider provider = ProviderManager.getInstance().getProvider();
					final DataSet dataset = provider.getDataSet();

					results.removeLoadingMessage();
					if (dataset.isEmpty()) {
						results.showEmptyMessage();
					} else {

						TableFactory tableFactory = TableFactory.getInstance();
						TreeViewer treeViewer;
						if (provider.getClass().getName()
								.equals("at.medevit.medelexis.buchhaltung.provider.Leistungsstatistik")) {
							treeViewer = tableFactory.createTreeFromData(results, dataset, provider.getLabelProvider(),
									provider.getContentProvider(), true);
						} else {
							treeViewer = tableFactory.createTreeFromData(results, dataset, provider.getLabelProvider(),
									provider.getContentProvider(), false);
						}
							treeViewer.setInput(dataset);
							MenuManager menuManager = new MenuManager();
							Menu menu = menuManager.createContextMenu(treeViewer.getTree());
							treeViewer.getTree().setMenu(menu);
							NewStatisticsAction.this.view.getSite().registerContextMenu(menuManager, treeViewer);
							NewStatisticsAction.this.view.getSite().setSelectionProvider(treeViewer);

					}

					// remove old chart models
					ChartModelManager.getInstance().clean();

					// layout results at last
					results.layout();

					// enable all actions back again
					NewStatisticsAction.this.setEnabled(true);

					// delegate property change event
					for (IPropertyChangeListener listener : NewStatisticsAction.this.listeners) {
						listener.propertyChange(new PropertyChangeEvent(NewStatisticsAction.this,
								NewStatisticsAction.JOB_DONE, null, null));
					}
				}
			});
		});
	}

	/**
	 * Registers a change listener with this action.
	 *
	 * @param listener
	 *
	 * @see IPropertyChangeListener
	 */
	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Unregisters a change listener with this action.
	 *
	 * @param listener
	 *
	 * @see IPropertyChangeListener
	 */
	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		this.listeners.remove(listener);
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (ProviderManager.getInstance().hasProvider()) {
			this.setEnabled(true);
		}
	}

	/**
	 * Updated the IPropertyChangeListener listeners for this action with a new
	 * PropertyChangeEvent containing the jobs current status.
	 */
	private void updateListeners() {
		// delegate property change event
		for (IPropertyChangeListener listener : this.listeners) {
			listener.propertyChange(new PropertyChangeEvent(this, NewStatisticsAction.JOB_RUNNING, null, null));
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// UNUSED INTERFACE FUNCTIONS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(final IJobChangeEvent event) {
		// does nothing
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(final IJobChangeEvent event) {
		// does nothing
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(final IJobChangeEvent event) {
		// does nothing
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(final IJobChangeEvent event) {
		// does nothing
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(final IJobChangeEvent event) {
		// does nothing
	}
}