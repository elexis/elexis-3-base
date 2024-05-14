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
 *     Gerry Weirich - modifications for API Changes in 2.1 (ElexisEventDispatcher)
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui.views;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.model.IUser;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.acl.ArchieACL;
import ch.unibe.iam.scg.archie.actions.ChartWizardAction;
import ch.unibe.iam.scg.archie.actions.ExportAction;
import ch.unibe.iam.scg.archie.actions.ExportPdfAction;
import ch.unibe.iam.scg.archie.ui.GraphicalMessage;
import ch.unibe.iam.scg.archie.ui.ResultPanel;

/**
 * <p>
 * This class contains all methods needed to display the output created by any
 * query.
 * </p>
 *
 * $Id: StatisticsView.java 774 2010-01-29 05:47:10Z gerry.weirich $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 774 $
 */
public class StatisticsView extends ViewPart {

	/**
	 * ID of this view.
	 */
	public static final String ID = ArchieActivator.PLUGIN_ID + ".ui.views.StatisticsView"; //$NON-NLS-1$

	private Composite container;

	private ResultPanel resultPanel;

	private GraphicalMessage message;

	private ExportAction exportAction;

	private ExportPdfAction exportPdfAction;

	private ChartWizardAction chartWizardAction;

	@Inject
	void activeUser(@Optional IUser user) {
		Display.getDefault().asyncExec(() -> {
			if (user != null) {
				clean();
				initialize();
			}
		});
	}

	/**
	 * Public constructor.
	 */
	public StatisticsView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.container = parent;

		// add and contribute actions for this view
		this.addActions();

		// initialize view contents
		this.initialize();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * Initialized the controls of this view. This method takes the ACL into
	 * account.
	 */
	private void initialize() {
		// remove margins and vertical spacing
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		// set layout
		this.container.setLayout(layout);

		// set initial message based on the ACL
		if (ArchieACL.userHasAccess()) {
			this.message = new GraphicalMessage(this.container, ArchieActivator.getImage(ArchieActivator.IMG_WARNING),
					Messages.NO_PLUGIN_SELECTED);
		} else {
			this.message = new GraphicalMessage(this.container, ArchieActivator.getImage(ArchieActivator.IMG_ERROR),
					Messages.ACL_ACCESS_DENIED);
			this.setActionsEnabled(false);
		}

		// layout container
		this.container.layout();
	}

	/**
	 * Add actions to this view.
	 */
	private void addActions() {
		this.exportAction = new ExportAction(this);
		this.exportPdfAction = new ExportPdfAction(this);
		this.chartWizardAction = new ChartWizardAction();

		IToolBarManager manager = this.getViewSite().getActionBars().getToolBarManager();
		manager.add(this.exportAction);
		manager.add(this.exportPdfAction);
		manager.add(this.chartWizardAction);
	}

	/**
	 * Removes the initial message which is being shown before any statistics have
	 * been run.
	 */
	public void removeInitializeMessage() {
		if (this.message != null) {
			this.message.dispose();
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		// nothing ?
	}

	/**
	 * Returns the container of this view. This composite is used to populate the
	 * view with other components.
	 *
	 * @return The composite container of this view.
	 */
	public Composite getParent() {
		return this.container;
	}

	/**
	 * Cleans the main result view from all active components.
	 */
	public void clean() {
		if (this.container != null && !this.container.isDisposed()) {
			for (Control child : this.container.getChildren()) {
				child.dispose();
			}
		}
	}

	/**
	 * Sets the result panel for this view.
	 *
	 * @param composite Composite containing the results of a query.
	 */
	public void setResultComposite(ResultPanel composite) {
		this.resultPanel = composite;
	}

	/**
	 *
	 * @return ResultPanel
	 */
	public ResultPanel getResultPanel() {
		return this.resultPanel;
	}

	/**
	 * Sets the enabled state for actions in this view.
	 *
	 * @param enabled True if actions should be enabled, false for disabled.
	 * @see String org.eclipse.jface.action.IAction.ENABLED
	 */
	public void setActionsEnabled(boolean enabled) {
		this.exportAction.setEnabled(enabled);
		this.exportPdfAction.setEnabled(enabled);
		this.chartWizardAction.setEnabled(enabled);
	}

}