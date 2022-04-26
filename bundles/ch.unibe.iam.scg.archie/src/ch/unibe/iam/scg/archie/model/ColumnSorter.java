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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;

import ch.unibe.iam.scg.archie.utils.ArrayUtils;

/**
 * <p>
 * A ViewerSorter which can sort top down and bottom up depending on the setting
 * of the reverse boolean.
 * </p>
 *
 * $Id: ColumnSorter.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ColumnSorter extends ViewerSorter {

	/** Sort direction */
	private int sortDirection;

	/** Index of the column which should be used to sort the results. */
	private int index;

	/**
	 * @param index
	 */
	public ColumnSorter(final int index) {
		this.index = index;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		Object o1 = ((Object[]) e1)[this.index];
		Object o2 = ((Object[]) e2)[this.index];

		int result;

		if (this.isComparable(o1) && this.isComparable(o2)) {
			result = ((Comparable) o1).compareTo((Comparable) o2);
		} else {
			result = o1.toString().compareTo(o2.toString());
		}

		return (this.sortDirection == SWT.DOWN ? result * (-1) : result); // invert
	}

	/**
	 * Sets the sort direction.
	 *
	 * @param sortDirection Sort direction to use, should be one of
	 *                      <code>SWT.UP</code> or <code>SWT.DOWN</code>
	 */
	public void setSortDirection(final int sortDirection) {
		this.sortDirection = sortDirection;
	}

	/**
	 * Returns the current column index.
	 *
	 * @return index Column index.
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Sets the current column index.
	 *
	 * @param index Column index.
	 */
	public void setIndex(final int index) {
		this.index = index;
	}

	/**
	 * Checks whether a given object can be compared. This method checks whether an
	 * object implements the <code>Comparable</code> interface directly or whether
	 * it has the <em>compareTo</em> method in it's object methods array.
	 *
	 * @return True if the object has a compareTo method, false else.
	 */
	private boolean isComparable(Object object) {
		return ArrayUtils.hasInterface(object.getClass().getInterfaces(), Comparable.class)
				|| ArrayUtils.hasMethod(object.getClass().getMethods(), "compareTo");
	}
}
