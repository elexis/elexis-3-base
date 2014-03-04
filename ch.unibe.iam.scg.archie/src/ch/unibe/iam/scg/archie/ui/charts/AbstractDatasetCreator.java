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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.jfree.data.general.AbstractDataset;

/**
 * <p>
 * Abstract dataset creator is resposible for creating a dataset out of the data
 * from the database. This class is used in <code>AbstractChartComposite</code>
 * to build the JFreeChart based charts.<br>
 * <br>
 * An abstract dataset creator extends the Eclipse API class <code>Job</code>
 * for proper monitoring capabilities while creating the dataset's content.
 * </p>
 * 
 * $Id: AbstractDatasetCreator.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public abstract class AbstractDatasetCreator extends Job {

	protected AbstractDataset dataset;

	/**
	 * Creates AbstractDatasetCreator
	 * 
	 * @param jobName
	 */
	public AbstractDatasetCreator(String jobName) {
		super(jobName);
	}

	/**
	 * Returns the dataset for this creator.
	 * 
	 * @return Created dataset.
	 */
	public AbstractDataset getDataset() {
		return this.dataset;
	}

	/**
	 * Creates the content for this dataset creator. Subclasses need to
	 * implement this method and do their main work in here - create the
	 * dataset.
	 * 
	 * @return Status that reflect the outcome of the content creation.
	 */
	public abstract IStatus createContent(IProgressMonitor monitor);

	/**
	 * This method runs the job. In this implementation, this means calling the
	 * content creation method of a dataset creator.
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 *      IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return this.createContent(monitor);
	}
	
	/**
	 * Method to work around the incapability of finding out whether the dataset
	 * has any data in it or not. The method returns <code>false</code> by default
	 * so that even empty dataset get rendered. If a subclass overrides this method,
	 * other objects can check for it and e.g. display a warning or information message
	 * instead of an empty graph. 
	 *
	 * @return True if the dataset is empty, false else. Returns false by default.
	 */
	protected boolean isDatasetEmpty() {
		return false;
	}
}