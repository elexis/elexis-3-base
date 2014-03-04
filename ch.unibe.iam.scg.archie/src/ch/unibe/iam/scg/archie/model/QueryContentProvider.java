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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * <p>
 * A standard content provider for the queries if no special data should be
 * represented. Each row will be handled as data object in this content
 * provider. E.g. If you want to have the patient as the model represented, you
 * need another content provider.
 * </p>
 * 
 * $Id: QueryContentProvider.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class QueryContentProvider implements IStructuredContentProvider {

	private final DataSet dataSet;

	/**
	 * The dataset to attach to this content provider.
	 * 
	 * @param dataSet
	 *            A dataset object.
	 */
	public QueryContentProvider(final DataSet dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * Returns the elements of the stored <code>DataSet</code> instance as an
	 * array. The parameter <code>inputElement</code> passed to this function is
	 * being ignored.
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(final Object inputElement) {
		return this.dataSet.getContent().toArray();
	}

	/**
	 * Does nothing.
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// does nothing.
	}

	/**
	 * Does nothing.
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// does nothing
	}
}
