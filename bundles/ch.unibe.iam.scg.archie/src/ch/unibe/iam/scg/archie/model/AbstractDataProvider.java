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
package ch.unibe.iam.scg.archie.model;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import ch.elexis.core.ui.util.Log;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.controller.TreeContentProvider;
import ch.unibe.iam.scg.archie.controller.TreeLabelProvider;

/**
 * <p>
 * An abstract class for data providers used by this plugin. An
 * <code>AbstractDataProvider</code> is being constructed with a name, which is
 * also the name of the background job being run when the provider is collecting
 * its data. The provider holds a <code>DataSet</code> object which provides
 * convenience methods for presenting and retrieving statistical data. Each
 * provider also has to set the size of its elements accordingly so the
 * <code>Job</code> so progress information is being displayed accurately.
 * </p>
 *
 * <p>
 * Providers have to <strong>initialize additional default values</strong> of
 * their properties either in the constructor or as part of the class
 * definition. There is no abstract method that binds the implementors to
 * initialize their default values. An error will be thrown in the GUI when no
 * default value has been initialized for a provider property.
 * </p>
 *
 * $Id: AbstractDataProvider.java 258 2008-10-06 17:51:15Z psiska
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 258
 */
public abstract class AbstractDataProvider extends Job {

	/**
	 * DataSet which stores results of this query in matrix form.
	 */
	protected DataSet dataSet;

	private ILabelProvider labelProvider;
	private IStructuredContentProvider contentProvider;
	private ITreeContentProvider treeContentProvider;

	/**
	 * Public constructor.
	 *
	 * @param jobName
	 */
	public AbstractDataProvider(String jobName) {
		super(jobName);
		this.dataSet = new DataSet();

		// initialize providers
		this.initializeProviders();
		assert (this.labelProvider != null);
		assert (this.contentProvider != null);
	}

	/**
	 * Returns the description for this data provider.
	 *
	 * @return Returns the description for this data provider.
	 */
	public abstract String getDescription();

	/**
	 * Creates headings for each column in the dataset object of this provider.
	 *
	 * @return A list of strings (List<String>) containing the headings.
	 */
	protected abstract List<String> createHeadings();

	/**
	 * This method should do all the work necessary to populate the dataset's
	 * content. It's called in the job's execute method after some initializations
	 * have been done.
	 *
	 * @return The status of the current job.
	 * @see org.eclipse.core.runtime.IStatus
	 */
	protected abstract IStatus createContent(IProgressMonitor monitor);

	/** {@inheritDoc} */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ArchieActivator.LOG.log("Job + " + this.getName(), Log.INFOS); //$NON-NLS-1$
		return this.execute(monitor);
	}

	/**
	 * Executes this job.
	 *
	 * @param monitor
	 * @return The status of the current job.
	 */
	public IStatus execute(final IProgressMonitor monitor) {
		// Set headings in the dataset.
		this.dataSet.setHeadings(this.createHeadings());

		// Return the status coming from the content creation method.
		return this.createContent(monitor);
	}

	/**
	 * Returns the content provider for this data provider.
	 *
	 * @return IStructuredContentProvider
	 *
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider
	 */
	public IStructuredContentProvider getContentProvider() {
		return this.contentProvider;
	}

	/**
	 * Returns the label provider for this data provider.
	 *
	 * @see org.eclipse.jface.viewers.LabelProvider
	 * @return LabelProvider A label provider for this object.
	 */
	public ILabelProvider getLabelProvider() {
		return this.labelProvider;
	}

	/**
	 * Returns the DataSet being held by this data provider.
	 *
	 * @see ch.unibe.iam.scg.archie.model.DataSet
	 * @return The DataSet object for this provider.
	 */
	public DataSet getDataSet() {
		return this.dataSet;
	}


	/**
	 * Initializes content and label providers and sets them accordingly. This is a
	 * generic method using two default providers for labels and content. Every
	 * class that has custom providers needs to override this method.
	 */
	protected void initializeProviders() {
		QueryContentProvider content = new QueryContentProvider(this.dataSet);
		QueryLabelProvider label = new QueryLabelProvider();

		this.setContentProvider(content);
		this.setLabelProvider(label);
		if (isTree()) {
			TreeContentProvider treeContent = new TreeContentProvider(true);
			treeContent.refreshDataSet(this.dataSet);
			this.setTreeContentProvider(treeContent);

			TreeLabelProvider treeLabel = new TreeLabelProvider(true);
			this.setLabelProvider(treeLabel);
		}
	}

	/**
	 * Sets the label provider for this data provider.
	 *
	 * @see org.eclipse.jface.viewers.LabelProvider
	 * @param labelProvider A label provider.
	 */
	protected void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/**
	 * Sets the content provider for this data provider.
	 *
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider
	 * @param contentProvider A content provider for this object.
	 */
	protected void setContentProvider(IStructuredContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	/**
	 * Override in subclass if TreeViewer is to be used.
	 * 
	 * @return true if TreeViewer is required.
	 */
	public boolean isTree() {
		return false;
	}

	/**
	 * Sets the TreeContentProvider for this data provider.
	 *
	 * @param treeContentProvider
	 *            The TreeContentProvider instance.
	 */
	protected void setTreeContentProvider(ITreeContentProvider treeContentProvider) {
		this.treeContentProvider = treeContentProvider;
	}

	/**
	 * Returns the TreeContentProvider, if applicable.
	 * 
	 * @return ITreeContentProvider or null
	 */
	public ITreeContentProvider getTreeContentProvider() {
		return this.treeContentProvider;
	}
}
