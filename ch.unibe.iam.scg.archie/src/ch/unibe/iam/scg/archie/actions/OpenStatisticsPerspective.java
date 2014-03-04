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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.WorkbenchException;

import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.ui.perspectives.StatisticsPerspective;
import ch.unibe.iam.scg.archie.ui.views.StatisticsView;

/**
 * 
 * <p>
 * This action opens the statistic view. This class is not used anymore as our
 * plugin contributes to the <code>Sidebar</code> extension point defined by
 * Elexis, which automatically makes an <em>Open Arcie</em> button &mdash; thus
 * deprecated.
 * </p>
 * 
 * NOTE: This class is not used anymore
 * 
 * @see IWorkbenchWindowActionDelegate
 * @see StatisticsView $Id: OpenStatisticsPerspective.java 492 2008-11-18
 *      22:18:18Z psiska $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 705 $
 */
@Deprecated
public class OpenStatisticsPerspective implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public OpenStatisticsPerspective() {
		// empty constructor
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @param action
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(final IAction action) {
		try {
			this.window.getWorkbench().showPerspective(StatisticsPerspective.ID, this.window);
		} catch (WorkbenchException e) {
			MessageDialog.openInformation(this.window.getShell(), ArchieActivator.PLUGIN_NAME,
					"Error while opening the statistics perspective.");
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @param action
	 * @param selection
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @param window
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}