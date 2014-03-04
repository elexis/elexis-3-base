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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ch.unibe.iam.scg.archie.utils.DatasetHelper;

/**
 * <p>Handles dataset sorting according to the table and it's sorting column and
 * direction. This class is used by the controller (action that starts a new
 * statistic) to attach this listener to the latest result table, so that
 * sorting the table is reflected on the dataset. This is needed in order to
 * have a properly sorted table (and the cloned dataset) in the chart wizard.</p>
 * 
 * $Id: DatasetTableColumnSorter.java 669 2008-12-15 09:55:30Z hephster $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 669 $
 */
public class DatasetTableColumnSorter implements SelectionListener {

	/**
	 * Table instance whose sorting will be synched wit the dataset.
	 */
	private Table table;

	/**
	 * Dataset that will be sorted each time the table is sorted.
	 */
	private DataSet dataset;

	/**
	 * Public constructor.
	 * 
	 * @param table Table whose sorting will be synched wit the dataset.
	 * @param dataset Dataset that will be sorted each time the table is sorted.
	 */
	public DatasetTableColumnSorter(final Table table, final DataSet dataset) {
		this.table = table;
		this.dataset = dataset;

		// add this listener to the table columns
		this.addListenerTableColumns();
	}

	/**
	 * Adds a selection listener to every column of the table in this object.
	 */
	private void addListenerTableColumns() {
		for (TableColumn column : this.table.getColumns()) {
			column.addSelectionListener(this);
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		//this.sortDataset();
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		this.sortDataset();
	}

	/**
	 * Sorts the dataset given to this listener according to the table's sort
	 * direction and column. This ensures that the table sorting is being
	 * reflected onto the dataset.
	 */
	private void sortDataset() {
		TableColumn sortColumn = this.table.getSortColumn();
		int sortDirection = this.table.getSortDirection();

		if (sortColumn != null && sortDirection != 0) {
			DatasetHelper.sortDataSet(this.dataset, sortColumn.getText(), sortDirection);
		}
	}
}