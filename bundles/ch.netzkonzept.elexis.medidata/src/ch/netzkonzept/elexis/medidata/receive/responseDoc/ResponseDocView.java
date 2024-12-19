package ch.netzkonzept.elexis.medidata.receive.responseDoc;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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

public class ResponseDocView extends ViewPart {

	public static final String ID = "ch.netzkonzept.elexis.medidata.receive.responseDoc.ResponseDocView";

	private Composite parent;
	private ResponseDocComparator comparator;
	private ResponseDocFilter filter;
	private TableViewer viewer;
	private ResponseDocEntry[] responseDocLog;
	private Path baseDir;

	public ResponseDocView(ResponseDocEntry[] responseDocLog, Path baseDir) {
		this.responseDocLog = responseDocLog;
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
		comparator = new ResponseDocComparator();
		viewer.setComparator(comparator);
		searchText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				filter.setSearchString(searchText.getText());
				viewer.refresh();
			}

		});
		filter = new ResponseDocFilter();
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
					ResponseDocEntry rde = (ResponseDocEntry) table.getSelection()[0].getData();
					String xmlPath = baseDir.resolve("receive").resolve(rde.getFilename()).toString();

					try {
						String extractedXML = XmlReader.extract(xmlPath);
						XmlEditor xmlEditor = new XmlEditor(parent.getShell(), extractedXML, rde.getFilename());
						xmlEditor.getShell().open();
					} catch (SAXException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						MessageDialog.openError(parent.getShell(), "Fehler",
								"Die Datei " + xmlPath + " kann nicht geöffnet werden!");
						e1.printStackTrace();
					} catch (ParserConfigurationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TransformerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(responseDocLog);

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
				ResponseDocEntry rde1 = (ResponseDocEntry) e1;
				ResponseDocEntry rde2 = (ResponseDocEntry) e2;
				return rde1.getCreated().compareTo(rde2.getCreated());
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

	private void resizeTable(Table table) {
		for (TableColumn tc : table.getColumns()) {
			resizeColumn(tc);
		}
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Response Documents", "File", "Path" };
		int[] bounds = { 10, 10, 10 };
		TableViewerColumn col;

		for (int i = 0; i < 3; i++) {
			final Integer innerI = Integer.valueOf(i);
			col = createTableViewerColumn(titles[i], bounds[i], i);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					ResponseDocEntry tle = (ResponseDocEntry) element;
					return tle.get(innerI);
				}
			});
		}
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

	private void resizeColumn(TableColumn tableColumn) {
		tableColumn.pack();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
