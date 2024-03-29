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
package ch.unibe.iam.scg.archie.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.controller.ProviderChartFactory;

/**
 * <p>
 * Displays charts generated by the Chart Wizard.
 * </p>
 *
 * $Id: ChartView.java 705 2009-01-03 17:48:46Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 705 $
 */
public class ChartView extends ViewPart {

	private Composite container;

	/**
	 * ID of this view.
	 */
	public static final String ID = ArchieActivator.PLUGIN_ID + ".ui.views.ChartView"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.container = parent;
		ProviderChartFactory.getInstance().createChart(this.container);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// does nothing
	}

	/**
	 * Sets this view dirty. The contents of the view is recreated according to the
	 * chart factory and it's model state.
	 */
	public void setDirty() {
		// this function has to be called only when the view already exists
		if (this.container == null) {
			throw new IllegalStateException("The ChartView can only be set dirty if already created."); //$NON-NLS-1$
		}

		// clean children of this container
		for (Control child : this.container.getChildren()) {
			child.dispose();
		}

		// recreate contents
		ProviderChartFactory.getInstance().createChart(this.container);
		this.container.layout(true);
	}
}