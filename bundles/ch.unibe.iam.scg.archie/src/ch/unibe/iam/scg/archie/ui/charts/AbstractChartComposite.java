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
package ch.unibe.iam.scg.archie.ui.charts;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.swt.ChartComposite;

import ch.elexis.core.ui.UiDesk;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.ui.GraphicalMessage;

/**
 * <p>
 * An abstract chart composite class. Inheriting from an SWT
 * <code>Composite</code>, it can be used in UI classes. This class also
 * implements the <code>IJobChangeListener</code> interface. It contains an
 * <code>AbstractDatasetCreator</code> which creates the corresponding
 * JFreeChart based charts, and by using this listener interface reacts to
 * certain job events propagated by the dataset creator class.
 * </p>
 * 
 * $Id: AbstractChartComposite.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public abstract class AbstractChartComposite extends Composite implements IJobChangeListener {

	protected Composite parent;
	protected AbstractDatasetCreator creator;

	private ChartComposite chartComposite;
	private GridData layoutData;

	/**
	 * Default constructor.
	 * 
	 * @param parent
	 * @param style
	 */
	public AbstractChartComposite(final Composite parent, final int style) {
		super(parent, style);
		this.parent = parent;

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		this.layoutData = new GridData(GridData.FILL_BOTH);

		this.setLoadingMessage();

		this.setLayout(layout);
		this.setLayoutData(layoutData);

		this.creator = this.initializeCreator();
		this.creator.addJobChangeListener(this);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialized the dataset creator. Subclasses have to initialize their
	 * specialized creators.
	 * 
	 * @return A JFreeChart dataset creator.
	 */
	abstract protected AbstractDatasetCreator initializeCreator();

	/**
	 * Initializes the chart.
	 * 
	 * @return An initialized chart object.
	 */
	abstract protected JFreeChart initializeChart();

	// ////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Refreshes this objects dataset creator
	 */
	public void refresh() {
		this.clean();
		this.setLoadingMessage();
		this.creator.schedule();
	}

	/**
	 * Requests a cancellation of the dataset creator. It's the creators
	 * responsibility however to properly react to this request.
	 */
	public void cancelCreator() {
		this.creator.cancel();
	}

	/**
	 * Schedules the creator (job) for this composite.
	 */
	public void startCreator() {
		this.creator.schedule();
	}

	public AbstractDatasetCreator getCreator() {
		return this.creator;
	}

	/**
	 * @param listener
	 */
	public void addJobChangeListener(IJobChangeListener listener) {
		this.creator.addJobChangeListener(listener);
	}

	/**
	 * @param listener
	 */
	public void removeJobChangeListener(IJobChangeListener listener) {
		this.creator.removeJobChangeListener(listener);

	}

	// ////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Display loading screen
	 */
	private void setLoadingMessage() {
		new GraphicalMessage(this, ArchieActivator.getInstance().getImageRegistry().get(ArchieActivator.IMG_COFFEE),
				Messages.WORKING);
		this.layout();
	}

	/**
	 * Displays a message that a creators dataset is empty.
	 */
	private void setEmptyMessage() {
		new GraphicalMessage(this, ArchieActivator.getInstance().getImageRegistry().get(ArchieActivator.IMG_INFO),
				Messages.RESULT_EMPTY);
		this.layout();
	}

	/**
	 * Cleans this AbstractChartComposite of all content.
	 */
	private void clean() {
		if (this.chartComposite != null) {
			this.chartComposite.dispose();
		}
		for (Control child : this.getChildren()) {
			child.dispose();
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	// INTERFACE METHODS
	// ////////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core
	 * .runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {
		// allow other threads to update this UI thread
		// http://www.eclipse.org/swt/faq.php#uithread
		UiDesk.getDisplay().syncExec(new Runnable() {
			public void run() {
				AbstractChartComposite.this.clean();

				// check if the creator has an empty dataset
				if (AbstractChartComposite.this.creator.isDatasetEmpty()) {
					AbstractChartComposite.this.setEmptyMessage();
				} else {
					// displa the chart else
					AbstractChartComposite.this.chartComposite = new ChartComposite(AbstractChartComposite.this,
							SWT.NONE, AbstractChartComposite.this.initializeChart());
					AbstractChartComposite.this.chartComposite.setLayoutData(AbstractChartComposite.this.layoutData);
				}

				AbstractChartComposite.this.layout();
			}
		});
	}

	// /////////////////////////////////////////////////////////////////////////////
	// UNUSED INTERFACE METHODS
	// /////////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse
	 * .core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core
	 * .runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.
	 * core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse
	 * .core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse
	 * .core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
		// nothing
	}
}