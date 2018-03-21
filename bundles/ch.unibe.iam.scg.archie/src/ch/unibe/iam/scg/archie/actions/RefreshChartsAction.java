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

import org.eclipse.jface.action.Action;

import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.ui.views.Dashboard;

/**
 * <p>Action which refreshed charts in the dashboard.</p>
 * 
 * $Id: RefreshChartsAction.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class RefreshChartsAction extends Action {
	
	private Dashboard dashboard;
	
	/**
	 * @param dashboard
	 */
	public RefreshChartsAction(final Dashboard dashboard) {
		this.dashboard = dashboard;
		
		this.setToolTipText("Refresh Charts");
		this.setImageDescriptor(ArchieActivator.getImageDescriptor("icons/arrow_circle_double.png"));
		
		this.setEnabled(false);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void run() {
		this.dashboard.redrawCharts();
		this.dashboard.updateOverview();
	}
}