/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui.table.util;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * ColumnBuilder is responsible to build a column for {@link TableViewerBuilder}
 * Methods are chainable so you can construct table columns in a single line.
 * After customizing the column by calling methods, call build() once to create
 * the actual column.
 *
 * @author Ralf Ebert <info@ralfebert.de>
 */
@SuppressWarnings("unchecked")
public class ColumnBuilder {

	private final TableViewer viewer;
	private final String columnHeaderText;
	private IValue valueHandler;
	private IValueFormatter valueFormatter;
	private ICellFormatter cellFormatter;
	private CellLabelProvider customLabelProvider;
	private Integer widthPixel;
	private Integer widthPercent;
	private int align = SWT.LEFT;
	private CellEditor editor;
	private IValue sortBy;
	private boolean defaultSort;
	private IValueFormatter editorFormat;

	public ColumnBuilder(TableViewer viewer, String columnHeaderText) {
		this.viewer = viewer;
		this.columnHeaderText = columnHeaderText;
	}

	/**
	 * Binds this column to the given property.
	 */
	public ColumnBuilder bindToProperty(String propertyName) {
		return bindToValue(new PropertyValue(propertyName));
	}

	/**
	 * Binds the column to an arbitrary value.
	 */
	public ColumnBuilder bindToValue(IValue valueHandler) {
		this.valueHandler = valueHandler;
		return this;
	}

	/**
	 * Sets a formatter for this column that is responsible to convert the value
	 * into a String. The 'parse' method of the CellFormatter is not required for
	 * this. See {@link Formatter} for commonly-used formatters.
	 */
	public ColumnBuilder format(IValueFormatter valueFormatter) {
		this.valueFormatter = valueFormatter;
		return this;
	}

	/**
	 * A cell formatter allows to format the cell besides the textual value, for
	 * example to customize colors or set images.
	 */
	public ColumnBuilder format(ICellFormatter cellFormatter) {
		this.cellFormatter = cellFormatter;
		return this;
	}

	/**
	 * If your column is not text based (for example a column with images that are
	 * owner-drawn), you can use a custom CellLabelProvider instead of a value and a
	 * value formatter.
	 */
	public ColumnBuilder setCustomLabelProvider(CellLabelProvider customLabelProvider) {
		this.customLabelProvider = customLabelProvider;
		return this;
	}

	/**
	 * Sets column width in percent
	 */
	public ColumnBuilder setPercentWidth(int width) {
		this.widthPercent = width;
		return this;
	}

	/**
	 * Sets column width in pixel
	 */
	public ColumnBuilder setPixelWidth(int width) {
		this.widthPixel = width;
		return this;
	}

	/**
	 * Sets alignment of column cell texts to be centered.
	 */
	public ColumnBuilder alignCenter() {
		this.align = SWT.CENTER;
		return this;
	}

	/**
	 * Sets alignment of column cell texts to be right-aligned.
	 */
	public ColumnBuilder alignRight() {
		this.align = SWT.RIGHT;
		return this;
	}

	/**
	 * Makes this column editable. Using this method you get a text editor without
	 * any formatting applied, to the value type needs to be String.
	 */
	public ColumnBuilder makeEditable() {
		return makeEditable(new TextCellEditor(viewer.getTable()), StringValueFormatter.INSTANCE);
	}

	/**
	 * Makes this column editable. Using this method you get a text editor. The
	 * given valueFormatter will be responsible for formatting the value to a String
	 * and parsing it back to a new value.
	 */
	public ColumnBuilder makeEditable(IValueFormatter valueFormatter) {
		return makeEditable(new TextCellEditor(viewer.getTable()), valueFormatter);
	}

	/**
	 * Makes the column cells editable using a custom cell editor. No formatting is
	 * applied, the editor will see the value as it is.
	 */
	public ColumnBuilder makeEditable(CellEditor cellEditor) {
		return makeEditable(cellEditor, null);
	}

	/**
	 * Makes the column cells editable using a custom cell editor. The given
	 * valueFormatter will be responsible for formatting the value for the editor
	 * and converting it back to a new value.
	 */
	public ColumnBuilder makeEditable(CellEditor cellEditor, IValueFormatter valueFormatter) {
		if (cellEditor.getControl().getParent() != viewer.getTable())
			throw new RuntimeException("Parent of cell editor needs to be the table!"); //$NON-NLS-1$
		this.editor = cellEditor;
		this.editorFormat = valueFormatter;
		return this;
	}

	/**
	 * Sets a custom value to sort by. Implement yourself our use PropertyValue to
	 * sort by a custom property value.
	 */
	public ColumnBuilder sortBy(IValue sortBy) {
		this.sortBy = sortBy;
		return this;
	}

	/**
	 * Sets this column as default sort column
	 */
	public ColumnBuilder useAsDefaultSortColumn() {
		this.defaultSort = true;
		return this;
	}

	/**
	 * Builds the column and returns the TableViewerColumn
	 */
	public TableViewerColumn build() {
		// create column
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, align);
		TableColumn column = viewerColumn.getColumn();
		column.setText(columnHeaderText);

		// set label provider
		if (customLabelProvider != null) {
			if (cellFormatter != null) {
				throw new RuntimeException("If you specify a custom label provider, it is not allowed " //$NON-NLS-1$
						+ "to specify a cell formatter. You need to do the formatting in your labelprovider!"); //$NON-NLS-1$
			}
			viewerColumn.setLabelProvider(customLabelProvider);
		} else {
			viewerColumn.setLabelProvider(new PropertyCellLabelProvider(valueHandler, valueFormatter, cellFormatter));
		}

		// activate column sorting
		if (sortBy == null) {
			sortBy = valueHandler;
		}
		if (sortBy != null) {
			column.setData(SortColumnComparator.SORT_BY, sortBy);
			column.addSelectionListener(new ColumnSortSelectionListener(viewer));
			if (defaultSort) {
				viewer.getTable().setSortColumn(column);
				viewer.getTable().setSortDirection(SWT.UP);
			}
		}

		// set column layout data
		if (widthPixel != null && widthPercent != null) {
			throw new RuntimeException("You can specify a width in pixel OR in percent, but not both!"); //$NON-NLS-1$
		}
		if (widthPercent == null) {
			// default width of 100px if nothing specified
			((TableColumnLayout) viewer.getTable().getParent().getLayout()).setColumnData(column,
					new ColumnPixelData(widthPixel == null ? 100 : widthPixel));
		} else {
			((TableColumnLayout) viewer.getTable().getParent().getLayout()).setColumnData(column,
					new ColumnWeightData(widthPercent));
		}

		// set editing support
		if (editor != null) {
			if (valueHandler == null) {
				throw new RuntimeException(
						"makeEditable() requires that the column is bound to some value using bindTo...()"); //$NON-NLS-1$
			}

			viewerColumn.setEditingSupport(new PropertyEditingSupport(viewer, valueHandler, editorFormat, editor));
		}

		return viewerColumn;
	}

	/**
	 * ColumnSortSelectionListener is a selection listener for {@link TableColumn}
	 * objects. When a column is selected (= header is clicked), it switches the
	 * sort direction if the column is already active sort column, otherwise it sets
	 * the active sort column.
	 *
	 * @author Ralf Ebert <info@ralfebert.de>
	 */
	public class ColumnSortSelectionListener extends SelectionAdapter {
		private final TableViewer viewer;

		public ColumnSortSelectionListener(TableViewer viewer) {
			this.viewer = viewer;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			TableColumn column = (TableColumn) e.getSource();
			Table table = column.getParent();
			boolean alreadyActiveSortColumn = (column == table.getSortColumn());
			if (alreadyActiveSortColumn) {
				table.setSortDirection(table.getSortDirection() == SWT.DOWN ? SWT.UP : SWT.DOWN);
			} else {
				table.setSortColumn(column);
				table.setSortDirection(SWT.UP);
			}
			viewer.refresh();
		}

	}

	/**
	 * An ICellFormatter is responsible for formatting a cell. Should be used to
	 * apply additional formatting to the cell, like setting colors / images.
	 *
	 * @author Ralf Ebert <info@ralfebert.de>
	 */
	public interface ICellFormatter {

		public void formatCell(ViewerCell cell, Object value);

	}

	/**
	 * PropertyCellLabelProvider is a CellLabelProvider that gets cell labels using
	 * a nested bean property string like "company.country.name".
	 *
	 * @author Ralf Ebert <info@ralfebert.de>
	 */
	@SuppressWarnings("unchecked")
	public class PropertyCellLabelProvider extends CellLabelProvider {

		private final IValue valueHandler;
		private IValueFormatter valueFormatter;
		private final ICellFormatter cellFormatter;

		public PropertyCellLabelProvider(String propertyName) {
			this.valueHandler = new PropertyValue(propertyName);
			this.cellFormatter = null;
		}

		public PropertyCellLabelProvider(IValue valueHandler, IValueFormatter valueFormatter,
				ICellFormatter cellFormatter) {
			this.valueHandler = valueHandler;
			this.valueFormatter = valueFormatter;
			this.cellFormatter = cellFormatter;
		}

		@Override
		public void update(ViewerCell cell) {
			try {
				Object rawValue = null;
				if (valueHandler != null) {
					rawValue = valueHandler.getValue(cell.getElement());
					Object formattedValue = rawValue;
					if (valueFormatter != null) {
						formattedValue = valueFormatter.format(rawValue);
					}
					cell.setText(String.valueOf(formattedValue));
				}
				if (cellFormatter != null) {
					cellFormatter.formatCell(cell, rawValue);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * EditingSupport for JFace viewers that gets and sets the value using a nested
	 * bean property string like "company.country.name".
	 *
	 * @author Ralf Ebert <info@ralfebert.de>
	 */
	@SuppressWarnings("unchecked")
	public class PropertyEditingSupport extends EditingSupport {

		private final CellEditor cellEditor;
		private final IValue valueHandler;
		private final IValueFormatter valueFormatter;

		public PropertyEditingSupport(ColumnViewer viewer, String propertyName, CellEditor cellEditor) {
			this(viewer, new PropertyValue(propertyName), null, cellEditor);
		}

		public PropertyEditingSupport(ColumnViewer viewer, IValue valueHandler, IValueFormatter valueFormatter,
				CellEditor cellEditor) {
			super(viewer);
			this.valueHandler = valueHandler;
			this.valueFormatter = valueFormatter;
			this.cellEditor = cellEditor;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			try {
				Object value = valueHandler.getValue(element);
				if (valueFormatter != null) {
					value = valueFormatter.format(value);
				}
				return value;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void setValue(Object element, Object value) {
			try {
				Object parsedValue = value;
				if (valueFormatter != null) {
					parsedValue = valueFormatter.parse(value);
				}
				valueHandler.setValue(element, parsedValue);
				getViewer().refresh();
			} catch (Exception e) {
			}
		}

	}

}
