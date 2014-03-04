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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>Sorts the columns of a TableViewer in a simple manner. It does not consider
 * any sorting done before.</p>
 * 
 * $Id: ColumnSorterAdapter.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ColumnSorterAdapter extends SelectionAdapter {

	/**
	 * Table viewer the sorter is acting on.
	 */
	private TableViewer viewer;
	
	/**
	 * Index of the column that should be sorted.
	 */
	private int index;

	/**
	 * Public constructor.
	 * 
	 * @param viewer
	 *            A tableViewer object.
	 * @param index
	 *            Index of the current column.
	 */
	public ColumnSorterAdapter(TableViewer viewer, int index) {
		this.viewer = viewer;
		this.index = index;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		// determine new sort column and direction
		Table table = this.viewer.getTable();

		TableColumn sortColumn = table.getSortColumn();
		TableColumn currentColumn = (TableColumn) event.widget;
		
		// compute sort direction
		int dir = table.getSortDirection();
		if (sortColumn == currentColumn) {
			dir = (dir == SWT.UP) ? SWT.DOWN : SWT.UP; // reverse
		} else {
			table.setSortColumn(currentColumn);
			dir = SWT.UP; // up by default
		}
		
		// add a sorter if not already present
		ColumnSorter sorter = (ColumnSorter) this.viewer.getSorter();
		if (sorter == null) {
			sorter = new ColumnSorter(this.index);
			this.viewer.setSorter(sorter);
		}

		// set index and sort direction in sorter
		sorter.setIndex((sorter.getIndex() != this.index) ? this.index : sorter.getIndex());
		sorter.setSortDirection(dir);
		
		// update data displayed in table
		table.setSortDirection(dir);
		table.clearAll();
		
		this.viewer.refresh(); // sort
	}
}