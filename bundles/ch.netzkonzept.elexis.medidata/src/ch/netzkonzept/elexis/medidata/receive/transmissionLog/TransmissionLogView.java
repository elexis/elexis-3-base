package ch.netzkonzept.elexis.medidata.receive.transmissionLog;

import java.io.*;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
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
import org.xml.sax.SAXException;

import ch.netzkonzept.elexis.medidata.xml.XmlEditor;
import ch.netzkonzept.elexis.medidata.xml.XmlReader;

public class TransmissionLogView extends ViewPart {

	public static final String ID = "ch.netzkonzept.elexis.medidata.receive.transmissionLog.TransmissionLogView";

	private Composite parent;
	private TransmissionLogComparator comparator;
	private TransmissionLogFilter filter;
	private TableViewer viewer;
	private TransmissionLogEntry[] transmissionLog;
	private Path baseDir;

	public TransmissionLogView(TransmissionLogEntry[] transmissionLog, Path baseDir) {
		this.transmissionLog = transmissionLog;
		this.baseDir = baseDir;
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
		comparator = new TransmissionLogComparator();
		viewer.setComparator(comparator);
		searchText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				filter.setSearchString(searchText.getText());
				viewer.refresh();
			}

		});
		filter = new TransmissionLogFilter();
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
					TransmissionLogEntry tle = (TransmissionLogEntry) table.getSelection()[0].getData();
					String xmlPath = baseDir.resolve("send").resolve("done").resolve(tle.getInvoiceReference())
							.toString();
					try {
						String extractedXML = XmlReader.extract(xmlPath);
						XmlEditor xmlEditor = new XmlEditor(parent.getShell(), extractedXML,
								tle.getInvoiceReference() + " (" + tle.getStatus() + ")");
						xmlEditor.getShell().open();
					} catch (SAXException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						MessageDialog.openError(parent.getShell(), "Fehler",
								"Die Datei " + xmlPath + " kann nicht geöffnet werden!");
						e1.printStackTrace();
					} catch (ParserConfigurationException e1) {
						e1.printStackTrace();
					} catch (TransformerConfigurationException e1) {
						e1.printStackTrace();
					} catch (TransformerException e1) {
						e1.printStackTrace();
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
		viewer.setInput(transmissionLog);

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
				TransmissionLogEntry tle1 = (TransmissionLogEntry) e1;
				TransmissionLogEntry tle2 = (TransmissionLogEntry) e2;
				return tle1.getCreated().compareTo(tle2.getCreated());
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
		String[] titles = { "Reference", "Created", "Modified", "Status", "Invoice Reference", "Control File" };
		int[] bounds = { 10, 10, 10, 10, 10, 10 };
		TableViewerColumn col;

		for (int i = 0; i < 6; i++) {
			final Integer innerI = Integer.valueOf(i);
			col = createTableViewerColumn(titles[i], bounds[i], i);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					TransmissionLogEntry tle = (TransmissionLogEntry) element;
					return tle.get(innerI);
				}
			});
		}
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
