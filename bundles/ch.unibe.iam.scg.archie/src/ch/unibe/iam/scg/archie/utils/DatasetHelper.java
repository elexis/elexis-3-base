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
package ch.unibe.iam.scg.archie.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;

import ch.rgw.tools.Money;
import ch.unibe.iam.scg.archie.model.DataSet;

/**
 * <p>
 * Singleton implementation of a dataset helper class. Contains helper functions
 * such as sorting or column operations to perform on a given dataset.
 *
 * $Id: DatasetHelper.java 666 2008-12-13 00:07:54Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 666 $
 */
public class DatasetHelper {

	/**
	 * @param dataset
	 * @param column
	 * @param direction
	 */
	public static final void sortDataSet(final DataSet dataset, final String column, final int direction) {
		int sortColumnIndex = DatasetHelper.getColumnIndex(dataset, column);
		assert (sortColumnIndex > -1);

		List<Comparable<?>[]> content = dataset.getContent();
		DatasetColumnComparator comparator = new DatasetColumnComparator();

		comparator.setSortColumn(sortColumnIndex);
		comparator.setSortDirection(direction);

		Collections.sort(content, comparator);
	}

	/**
	 * Checks whether a given column contains only money values. This function only
	 * checks the first row for a given column but given the specification and
	 * definition of a dataset, we can assume the data / value types in a column are
	 * the same. This function uses the column name (heading) to search for the
	 * given column in the dataset.
	 *
	 * @param dataset
	 * @param column
	 * @return True if the column contains only numeric characters, false else.
	 */
	public static final boolean isMoneyColumn(final DataSet dataset, final String column) {
		int columnIndex = DatasetHelper.getColumnIndex(dataset, column);
		assert (columnIndex > -1);

		return DatasetHelper.isMoneyColumn(dataset, columnIndex);
	}

	/**
	 * Checks whether a given column contains only money values. This function only
	 * checks the first row for a given column but given the specification and
	 * definition of a dataset, we can assume the data / value types in a column are
	 * the same. This function uses the column index to search for the given column
	 * in the dataset.
	 *
	 * @param dataset
	 * @param columnIndex
	 * @return True if the column contains only numeric characters, false else.
	 */
	public static final boolean isMoneyColumn(final DataSet dataset, final int columnIndex) {
		Comparable<?>[] col = dataset.getColumn(columnIndex);
		for (Comparable<?> cell : col) {
			if (cell instanceof Money) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a given column contains only numeric values. This function
	 * only checks the first row for a given column but given the specification and
	 * definition of a dataset, we can assume the data / value types in a column are
	 * the same. This function uses the column name (heading) to search for the
	 * given column in the dataset.
	 *
	 * @param dataset Dataset to check the column in.
	 * @param column  String Column name.
	 * @return boolean True if the column contains only numeric characters, false
	 *         else.
	 */
	public static final boolean isNumericColumn(final DataSet dataset, final String column) {
		int columnIndex = DatasetHelper.getColumnIndex(dataset, column);
		assert (columnIndex > -1);

		return DatasetHelper.isNumericColumn(dataset, columnIndex);
	}

	/**
	 * Checks whether a given column contains only numeric values. This function
	 * only checks the first row for a given column but given the specification and
	 * definition of a dataset, we can assume the data / value types in a column are
	 * the same. This function uses the column index to search for the given column
	 * in the dataset.
	 *
	 * @param dataset     Dataset to check the column in.
	 * @param columnIndex
	 * @return boolean True if the column contains only numeric characters, false
	 *         else.
	 */
	public static final boolean isNumericColumn(final DataSet dataset, final int columnIndex) {
	    Comparable<?>[] col = dataset.getColumn(columnIndex);
	    if (col == null || col.length == 0) {
	        return false;
	    }
	    for (Comparable<?> cell : col) {
	        if (cell != null && StringHelper.isNumeric(cell.toString())) {
				return true;
	        }
	    }
	    return false;
	}

	/**
	 * Checks whether a given dataset has at least one numeric column of data.
	 *
	 * @param dataset Dataset to check the columns in.
	 * @return True if the dataset has at least one numeric column, false else.
	 */
	public static final boolean hasNumericColumn(final DataSet dataset) {
		boolean hasNumeric = false;
		for (String heading : dataset.getHeadings()) {
			if (DatasetHelper.isNumericColumn(dataset, heading)) {
				hasNumeric = true;
				break;
			}
		}
		return hasNumeric;
	}

	/**
	 * Checks whether a given dataset has at least one money column of data.
	 *
	 * @param dataset Dataset to check the columns in.
	 * @return True if the dataset has at least one money column, false else.
	 */
	public static final boolean hasMoneyColumn(final DataSet dataset) {
		boolean hasMoney = false;
		for (String heading : dataset.getHeadings()) {
			if (DatasetHelper.isMoneyColumn(dataset, heading)) {
				hasMoney = true;
				break;
			}
		}
		return hasMoney;
	}

	/**
	 *
	 * @param dataset
	 * @param column
	 * @return
	 */
	private static final int getColumnIndex(final DataSet dataset, final String column) {
		int columnIndex = -1;
		for (int i = 0; i < dataset.getHeadings().size(); i++) {
			if (dataset.getHeadings().get(i).equals(column)) {
				columnIndex = i;
			}
		}
		return columnIndex;
	}

	/**
	 * Internal comparator used for sorting a dataset according to a given column
	 * index and a sort direction. The comparator compares all values in the given
	 * column and sorts them accordingly.
	 *
	 * $Id: DatasetHelper.java 666 2008-12-13 00:07:54Z peschehimself $
	 *
	 * @author Peter Siska
	 * @author Dennis Schenk
	 * @version $Rev: 666 $
	 */
	public static class DatasetColumnComparator implements Comparator<Object> {

		private int sortColumn;

		private int sortDirection;

		/**
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		public int compare(final Object e1, final Object e2) {
			Object o1 = ((Object[]) e1)[this.sortColumn];
			Object o2 = ((Object[]) e2)[this.sortColumn];

			int result;

			Class<?>[] o1interfaces = o1.getClass().getInterfaces();
			Class<?>[] o2interfaces = o2.getClass().getInterfaces();

			if (ArrayUtils.hasInterface(o1interfaces, Comparable.class)
					&& ArrayUtils.hasInterface(o2interfaces, Comparable.class)) {
				result = ((Comparable) o1).compareTo((Comparable) o2);
			} else {
				result = o1.toString().compareTo(o2.toString());
			}

			return (this.sortDirection == SWT.DOWN ? result * (-1) : result); // invert
		}

		/**
		 * @param column
		 */
		public void setSortColumn(final int column) {
			this.sortColumn = column;
		}

		/**
		 * @param direction
		 */
		public void setSortDirection(final int direction) {
			this.sortDirection = direction;
		}
	}
}