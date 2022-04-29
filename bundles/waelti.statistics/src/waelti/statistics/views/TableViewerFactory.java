package waelti.statistics.views;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import waelti.statistics.actions.ColumnSorterAdapter;
import waelti.statistics.queries.AbstractQuery;

/** Creates a SWT Table containing the results of the given query. */
public class TableViewerFactory {

	/** Parent of the viewer to be created. */
	private Composite parent;
	private AbstractQuery query;

	/** constructor */
	public TableViewerFactory(Composite parent, AbstractQuery query) {
		this.parent = parent;
		this.query = query;

	}

	/** Creates a table viewer containing the query's data. */
	public TableViewer createTableViewer() {
		Table table = this.createTable();

		TableViewer viewer = new TableViewer(table);
		viewer.setContentProvider(this.query.getContentProvider());
		viewer.setLabelProvider(this.query.getLabelProvider());
		this.addColumnSort(viewer);

		viewer.setInput(StringUtils.EMPTY); // fill the table
		packColumns(viewer.getTable()); // width of single columns
		return viewer;
	}

	private Table createTable() {
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL);

		GridData data = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		table.setLayoutData(data);
		table.setHeaderVisible(true);

		this.createColumns(table);

		return table;
	}

	private void packColumns(Table table) {
		TableColumn[] col = table.getColumns();

		for (int i = 0; i < col.length; i++) {
			col[i].pack();
		}
	}

	private void createColumns(Table table) {
		int i = 0;
		for (String text : this.query.getTableHeadings()) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(text);
			column.setWidth(text.length() * 8);
			// this.addColumnSort(column, i);
			i++;
		}
	}

	private void addColumnSort(TableViewer viewer) {
		TableColumn[] cols = viewer.getTable().getColumns();
		for (int i = 0; i < cols.length; i++) {
			cols[i].addSelectionListener(new ColumnSorterAdapter(viewer, i));
		}

	}

	public AbstractQuery getQuery() {
		return query;
	}

	public void setQuery(AbstractQuery query) {
		this.query = query;
	}
}
