package ch.netzkonzept.elexis.medidata.receive.messageLog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class MessageLogView extends ViewPart {

	public static final String ID = "ch.netzkonzept.elexis.medidata.receive.messageLog.MessageLogView";

	private Composite parent;
	private MessageLogComparator comparator;
	private MessageLogFilter filter;
	private TableViewer viewer;
	MessageLogEntry[] messageLog;

	public MessageLogView(MessageLogEntry[] messageLog) {
		this.messageLog = messageLog;
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		createViewer(parent);
		comparator = new MessageLogComparator();
		viewer.setComparator(comparator);
		searchText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				filter.setSearchString(searchText.getText());
				viewer.refresh();
			}

		});
		filter = new MessageLogFilter();
		viewer.addFilter(filter);
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem[] tableItems = table.getSelection();
				if (tableItems.length == 1) {
					MessageLogEntry mle = (MessageLogEntry) table.getSelection()[0].getData();
					String messageTitle = mle.getId() + ": " + mle.getTemplate();
					String message = mle.getCreated() + "\n\n" + mle.getMessage().getDe().toString();

					if (mle.getSeverity().equalsIgnoreCase("error")) {
						MessageDialog.openError(parent.getShell(), messageTitle, message);
					} else if (mle.getSeverity().equalsIgnoreCase("info")) {
						MessageDialog.openInformation(parent.getShell(), messageTitle, message);
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
		});
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(messageLog);

		// Layout of the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		viewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				MessageLogEntry mle1 = (MessageLogEntry) e1;
				MessageLogEntry mle2 = (MessageLogEntry) e2;
				return mle1.getId().compareTo(mle2.getId());
			}
		});

		Menu contextMenu = new Menu(viewer.getTable());
		viewer.getTable().setMenu(contextMenu);

		for (TableColumn tableColumn : viewer.getTable().getColumns()) {
			createMenuItem(contextMenu, tableColumn);
		}
		this.parent = parent;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public Composite getComposite() {
		resizeTable(viewer.getTable());
		arrangeColumns(viewer.getTable());
		return parent;
	}

	private void resizeColumn(TableColumn tableColumn) {
		tableColumn.pack();
	}

	private void resizeTable(Table table) {
		for (TableColumn tc : table.getColumns()) {
			resizeColumn(tc);
		}
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Created", "ID", "Subject", "Severity", "Read", "Template", "Mode", "ErrorCode",
				"PotentialReasons", "PossibleSolutions", "TechnicalInformation" };
		int[] bounds = { 10, 10, 10, 5, 10, 10, 10, 5, 10, 10, 10 };
		TableViewerColumn col;

		for (int i = 0; i < 11; i++) {
			final Integer innerI = Integer.valueOf(i);
			col = createTableViewerColumn(titles[i], bounds[i], i);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					MessageLogEntry mle = (MessageLogEntry) element;
					return mle.get(innerI);
				}
			});
		}
	}

	private void arrangeColumns(Table table) {
		table.getMenu().getItem(5).setSelection(false);
		table.getColumn(5).setWidth(0);
		table.getColumn(5).setResizable(false);

		table.getMenu().getItem(6).setSelection(false);
		table.getColumn(6).setWidth(0);
		table.getColumn(6).setResizable(false);

		table.getMenu().getItem(7).setSelection(false);
		table.getColumn(7).setWidth(0);
		table.getColumn(7).setResizable(false);

		table.getMenu().getItem(8).setSelection(false);
		table.getColumn(8).setWidth(0);
		table.getColumn(8).setResizable(false);

		table.getMenu().getItem(9).setSelection(false);
		table.getColumn(9).setWidth(0);
		table.getColumn(9).setResizable(false);

		table.getMenu().getItem(10).setSelection(false);
		table.getColumn(10).setWidth(0);
		table.getColumn(10).setResizable(false);
	}

	private void createMenuItem(Menu parent, final TableColumn column) {
		final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
		itemName.setText(column.getText());
		itemName.setSelection(column.getResizable());
		itemName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (itemName.getSelection()) {
					column.setWidth(150);
					column.setResizable(true);
				} else {
					column.setWidth(0);
					column.setResizable(false);
				}
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;

	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
