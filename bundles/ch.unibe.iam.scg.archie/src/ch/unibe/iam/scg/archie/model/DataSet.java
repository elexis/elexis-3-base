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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Holds objects (which implement the comparable interface) in tabular form. A
 * List of Strings serves as table headings. Cells are denoted by their x and y
 * coordinates. Headings have to be set before content.
 * </p>
 *
 * <p>
 * <strong>IMPORTANT</strong>: The dataset's content has to be composed out of
 * data types that implement the Comparable interface. This is to ensure that
 * the contents of the dataset can be sorted properly.
 * </p>
 *
 * Example Structure of a DataSet:
 *
 * <pre>
 *      | Heading1| Heading2 |
 *       ====================
 *      | Column0 | Column1  |
 *       --------------------
 * Row0 | 0,0     | 0,1      |
 * Row1 | 1,0     | 1,1      |
 * </pre>
 *
 * $Id: DataSet.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class DataSet implements Iterable<Comparable<?>[]>, Cloneable {

	/**
	 * Content of the DataSet: ArrayList with Comparable implementing objects array.
	 */
	private List<Comparable<?>[]> content;

	/** Description of the columns */
	private List<String> headings;

	/** The number of columns currently existing. */
	private int width = 0;

	/**
	 * Constructs an empty DataSet. Needs to be filled before it can be used.
	 */
	public DataSet() {
		this.content = new ArrayList<Comparable<?>[]>();
		this.headings = new ArrayList<String>();
	}

	/**
	 * Constructs a <code>DataSet</code> with a list of objects arrays and a heading
	 * list.
	 *
	 * @param content
	 * @param headings
	 * @throws IllegalArgumentException
	 */
	public DataSet(final List<Comparable<?>[]> content, final List<String> headings) throws IllegalArgumentException {
		// Checking Preconditions
		if (content == null || headings == null || content.isEmpty() || headings.isEmpty()) {
			throw new IllegalArgumentException("Argument lists and headings must not be null and not empty!"); //$NON-NLS-1$
		}
		// Columns and headings length must match.
		if (content.get(0).length != headings.size()) {
			throw new IllegalArgumentException("Number of columns has to match provided number of headings!"); //$NON-NLS-1$
		}

		this.content = content;
		this.headings = headings;
		this.width = this.content.get(0).length;
	}

	/**
	 * @param x     Row index.
	 * @param y     Column index.
	 * @param value Content.
	 */
	public void setCell(final int x, final int y, final Comparable<?> value) {
		// Checking Preconditions
		if (x > this.content.size() || y > this.content.get(x).length - 1) {
			throw new IllegalArgumentException(
					"Your trying to update a dataset element at a position that is greater than the dataset's boundaries."); //$NON-NLS-1$
		}
		this.content.get(x)[y] = value;
	}

	/**
	 * @param x Row index.
	 * @param y Column index.
	 * @return Comparable<?> at specified location, null if list is empty.
	 */
	public Comparable<?> getCell(final int x, final int y) {
		return this.content.get(x)[y];
	}

	/**
	 * @param x Row index.
	 * @return Comparable<?> Array of specified row.
	 */
	public Comparable<?>[] getRow(final int x) {
		return this.content.get(x);
	}

	/**
	 * @param x   Row index.
	 * @param obj content.
	 */
	public void setRow(final int x, final Comparable<?>[] obj) {
		// Checking Preconditions
		if (x > this.content.size()) {
			throw new IllegalArgumentException(
					"Your trying to access a row in the dataset that is greater than the dataset's boundaries."); //$NON-NLS-1$
		}
		this.content.set(x, obj);
	}

	/**
	 * @param y Column index.
	 * @return Comparable<?> Array of specified column.
	 */
	public Comparable<?>[] getColumn(final int y) {
		ArrayList<Comparable<?>> column = new ArrayList<Comparable<?>>(this.content.size());

		for (Comparable<?>[] objects : this.content) {
			column.add(objects[y]);
		}

		return column.toArray(new Comparable<?>[column.size()]);
	}

	/**
	 * Adds an additional row to the dataSet and fills it with the provided row
	 * content.
	 *
	 * @param row Comparable<?> Array
	 */
	public void addRow(final Comparable<?>[] row) {
		// Checking Preconditions
		if (row.length != this.width) {
			throw new IllegalArgumentException(
					"The number of columns of a row being added to the dataset hat so equal the number of columns in the dataset."); //$NON-NLS-1$
		}
		this.content.add(row);
	}

	/**
	 * @return Iterator over content.
	 */
	public Iterator<Comparable<?>[]> iterator() {
		return this.content.iterator();
	}

	/**
	 * @return Content
	 */
	public List<Comparable<?>[]> getContent() {
		return this.content;
	}

	/**
	 * DataSet can be empty, but not null.
	 *
	 * @param content
	 */
	public void setContent(final List<Comparable<?>[]> content) {
		// Checking Preconditions
		if (content == null) {
			throw new IllegalArgumentException("Content of a dataset can not be null!"); //$NON-NLS-1$
		}
		if (this.headings.isEmpty()) {
			throw new IllegalArgumentException(
					"Can not set content before headings are set, also headings can not be empty!"); //$NON-NLS-1$
		}
		if (!content.isEmpty() && content.get(0).length != this.headings.size()) {
			throw new IllegalArgumentException("Provided number of content does not match the number of headings!"); //$NON-NLS-1$
		}
		this.content = content;
		this.width = this.headings.size();
	}

	/**
	 * @return List of table headings.
	 */
	public List<String> getHeadings() {
		return this.headings;
	}

	/**
	 * Set headings. We assume that headings get set before content. Precondition
	 * checking for same row length gets done by <code>setContent</code>.
	 *
	 * @param headings List
	 */
	public void setHeadings(final List<String> headings) {
		// Checking Preconditions
		if (headings == null || headings.isEmpty()) {
			throw new IllegalArgumentException("Provided table headings can not be null or empty!"); //$NON-NLS-1$
		}
		this.headings = headings;
	}

	/**
	 * Creates a string representation of this DataSet. This method is expensive and
	 * should only be used for debugging purposes.
	 *
	 * @return String representation of this DataSet.
	 */
	@Override
	public String toString() {
		int[] columnWidths = new int[this.headings.size()];
		int totalWidth = 0;

		// get the maximum width of the contents for each column
		for (int i = 0; i < columnWidths.length; i++) {
			// check headings lengths
			for (String heading : this.headings) {
				if (heading.toString().length() > columnWidths[i]) {
					columnWidths[i] = heading.toString().length();
				}
			}

			// check columns lengths
			Comparable<?>[] column = this.getColumn(i);
			for (Comparable<?> length : column) {
				if (length.toString().length() > columnWidths[i]) {
					columnWidths[i] = length.toString().length();
				}
			}
		}

		// compute total length
		for (int width : columnWidths) {
			totalWidth += width;
		}

		StringBuilder output = new StringBuilder();

		// headings string
		for (int i = 0; i < this.headings.size(); i++) {
			String heading = this.headings.get(i);
			output.append("| "); //$NON-NLS-1$
			output.append(heading);

			// heading is smaller than the maximum column width
			if (heading.length() < columnWidths[i]) {
				// fill up with blanks
				int difference = columnWidths[i] - heading.length();
				for (int j = 0; j < difference; j++) {
					output.append(StringUtils.SPACE);
				}
			}

			// append a blank an the end of each heading
			output.append(StringUtils.SPACE);
		}
		output.append(StringUtils.LF);

		// append heading separator
		for (int i = 0; i < totalWidth; i++) {
			output.append("-"); //$NON-NLS-1$
		}
		// and three spaces plus for each column
		for (int i = 0; i < this.headings.size(); i++) {
			output.append("---"); //$NON-NLS-1$
		}
		output.append(StringUtils.LF);

		// print out rows
		for (int x = 0; x < this.content.size(); x++) {
			Comparable<?>[] row = this.getRow(x);
			// get columns
			for (int y = 0; y < row.length; y++) {
				String cellValue = this.getCell(x, y).toString();
				output.append("| "); //$NON-NLS-1$
				output.append(cellValue);

				if (cellValue.length() < columnWidths[y]) {
					// fill up with blanks
					int difference = columnWidths[y] - cellValue.length();
					for (int j = 0; j < difference; j++) {
						output.append(StringUtils.SPACE);
					}
				}
				// append a blank at the end of each column
				output.append(StringUtils.SPACE);
			}
			output.append(StringUtils.LF);
		}

		return output.toString();
	}

	/**
	 * Checks if the dataset contains any real data or is empty.
	 *
	 * @return True if the dataset's content is empty, false else.
	 */
	public boolean isEmpty() {
		return this.content.isEmpty();
	}

	/**
	 * @return a clone of this dataSet.
	 */
	@Override
	public DataSet clone() {
		DataSet cloneSet = null;
		try {
			cloneSet = (DataSet) super.clone();
			cloneSet.content = new ArrayList<Comparable<?>[]>(cloneSet.content);
			cloneSet.headings = new ArrayList<String>(cloneSet.headings);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return cloneSet;
	}
}