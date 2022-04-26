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
package ch.unibe.iam.scg.archie.controller;

import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ch.unibe.iam.scg.archie.model.ColumnSorterAdapter;
import ch.unibe.iam.scg.archie.model.DataSet;

/**
 * <p>
 * Creates a <code>TableViewer</code> from data provided by an implementation of
 * <code>AbstractDataProvider</code>.
 * </p>
 * <p>
 * Uses singleton pattern.
 * </p>
 *
 * $Id: TableFactory.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class TableFactory {

	private static TableFactory INSTANCE = null;

	/**
	 * Private constructor, singleton pattern.
	 */
	private TableFactory() {
	}

	/**
	 * Returns an instance of this object. Static method as this class implements
	 * the Singleton pattern.
	 *
	 * @return TabeFactory Instance
	 */
	public static final TableFactory getInstance() {
		if (TableFactory.INSTANCE == null) {
			TableFactory.INSTANCE = new TableFactory();
		}
		return TableFactory.INSTANCE;
	}

	/**
	 * Creates a tableviewer from the given provider.
	 *
	 * @param parent          Composite that holds the table.
	 * @param dataset
	 * @param labelProvider
	 * @param contentProvider
	 * @return Table
	 */
	public TableViewer createTableFromData(final Composite parent, final DataSet dataset,
			final ILabelProvider labelProvider, final IContentProvider contentProvider) {
		Table table = this.createTable(parent, dataset);

		// Create table viewer and set providers.
		TableViewer tableViewer = new TableViewer(table);

		// Set providers.
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setContentProvider(contentProvider);

		// Invoke the inputChanged method after a content provider is set.
		tableViewer.setInput(table);

		// add column sorters.
		this.addColumnSort(tableViewer);

		return tableViewer;
	}

	/**
	 * Creates the table based on the DataProvider passed to this function.
	 *
	 * @param parent   Composite that holds the table.
	 * @param provider A data provider.
	 * @return A table object.
	 */
	private Table createTable(final Composite parent, final DataSet dataset) {
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

		TableColumnLayout tableLayout = new TableColumnLayout();
		parent.setLayout(tableLayout);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		this.createColumns(table, dataset, tableLayout);

		return table;
	}

	/**
	 * Creates all columns in this table. This method sets a weight to each column
	 * so that all columns are layed out equally in their container.
	 *
	 * @param table    The table to perform column operations on.
	 * @param provider A data provider.
	 * @param layout   Layout to use for the columns.
	 */
	private void createColumns(final Table table, final DataSet dataset, TableColumnLayout layout) {
		int i = 0;
		List<String> headings = dataset.getHeadings();

		for (String text : headings) {
			TableColumn column = new TableColumn(table, SWT.NONE);

			column.setMoveable(true);
			column.setText(text);
			column.pack();

			// computes the weight for each column based on the total number of
			// columns and sets the columndata in the layout accordingly
			int weight = Math.round((100 / headings.size()));
			layout.setColumnData(column, new ColumnWeightData(weight));

			i++;
		}
	}

	/**
	 * Adds a selection listener to all columns in order to be able to sort by
	 * column later.
	 *
	 * @param viewer A TableViewer object.
	 */
	private void addColumnSort(TableViewer viewer) {
		TableColumn[] cols = viewer.getTable().getColumns();
		for (int i = 0; i < cols.length; i++) {
			cols[i].addSelectionListener(new ColumnSorterAdapter(viewer, i));
		}
	}
}
