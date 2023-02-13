/* May 2013: Niklaus Giger various fixes
 * Date displays year with 2 digits
 * Comments shorter than 15 characters are shown inline, e.g. 'negative'
 */
package org.iatrix.messwerte.views;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.iatrix.messwerte.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.LabResultConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.types.PathologicDescription;
import ch.elexis.core.types.PathologicDescription.Description;
import ch.elexis.core.ui.dialogs.DateSelectorDialog;
import ch.elexis.core.ui.dialogs.DisplayTextDialog;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.LabGroup;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/*
 * TODO: implement HeartListener (register listeners)
 */

public class MesswerteView extends ViewPart implements IRefreshable, HeartListener {

	public static final String ID = "org.iatrix.messwerte.views.MesswerteView";

	protected static Logger log = LoggerFactory.getLogger(ID);

	/**
	 * Combo item for showing all lab groups
	 */
	private static final String GROUPS_ALL = "Alle Resultate";
	/**
	 * Combo item for showing the praxis lab items
	 */
	private static final String GROUPS_PRAXIS = "Praxislabor";

	private static final String FEMININ = Person.FEMALE;
	/**
	 * Number of columns shown per page
	 */
	private static int columnsPerPage;

	/**
	 * index of the parameter column
	 */
	private static final int PARAMETER_INDEX = 0;
	/**
	 * index of the reference column
	 */
	private static final int REF_INDEX = 1;
	/**
	 * Offset of the first date column
	 */
	private static final int DATES_OFFSET = 2;

	private static final int COLUMN_NAME_DEFAULT_WITH = 200;
	private static final int COLUMN_REF_DEFAULT_WITH = 110;
	private static final int COLUMN_DATE_DEFAULT_WITH = 110;

	private static final int COLUMN_DATE_INITIAL_MIN_WIDTH = 50;

	private Label pagesLabel;

	private TableViewer viewer;
	private TableViewerColumn tableNameColumn;
	private TableViewerColumn tableRefColumn;
	private TableViewerColumn[] tableDateColumns;
	private TableDate[] tableDates;

	private TableViewerFocusCellManager focusCellManager;

	/**
	 * List of dates there are values of
	 */
	private List<TimeTool> availableDates = new ArrayList<TimeTool>();

	/**
	 * List of dates we have columns for
	 */
	private List<DateColumn> dateColumns = new ArrayList<DateColumn>();
	/**
	 * contains the actual TableDate Values
	 */
	private List<TableDate> dateColumnsExpanded = new ArrayList<TableDate>();

	private Action newDateAction;
	private Action fwdAction;
	private Action backAction;

	private Action pathologicAction;

	private ComboViewer laborGroupsViewer;
	private List<BaseLabGroupElement> labGroupElements = new ArrayList<BaseLabGroupElement>();

	private Patient actPatient = null;

	private int currentPage = 0;
	private int lastPage = 0;

	private List<LabRow> viewerRows = new ArrayList<LabRow>();

	/**
	 * configured own labors
	 */
	private List<Labor> ownLabors = new ArrayList<Labor>();

	/*
	 * TODO The following two methods actually belong to ch.elexis.data.LabResult
	 */

	private static final String LABORWRTE_TABLENAME = "LABORWERTE";

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	/**
	 * Get all dates having lab results for the given patient. The dates are sorted
	 * in ascending order.
	 *
	 * @param patient the patient to get the available dates for
	 * @return the dates having lab results, sorted in ascending order
	 */
	public static List<TimeTool> getAvailableDates(Patient patient) {
		List<TimeTool> dates = new ArrayList<TimeTool>();

		String sql = "SELECT Datum FROM " + LABORWRTE_TABLENAME + " WHERE PatientID = ?" + " GROUP BY Datum"
				+ " ORDER BY Datum";

		try {
			PreparedStatement ps = PersistentObject.getConnection().prepareStatement(sql);
			if (ps != null) {
				ps.setString(1, patient.getId());
				if (ps.execute()) {
					ResultSet rs = ps.getResultSet();
					while (rs.next()) {
						String date = rs.getString(1);
						dates.add(new TimeTool(date));
					}
				}
			}
		} catch (SQLException ex) {
			ExHandler.handle(ex);
			log.error("Fehler beim Ausführen von " + sql);
		}
		log.debug("getAvailableDates: return " + dates.size() + " dates for " + patient.getPersonalia());
		return dates;
	}

	/**
	 * Get all dates having lab results for the given patient, restricted to items
	 * given in labItems. The dates are sorted in ascending order.
	 *
	 * @param patient the patient to get the available dates for
	 * @return the dates having lab results, sorted in ascending order
	 */
	public static List<TimeTool> getAvailableDatesOfGroup(Patient patient, List<LabItem> labItems) {
		List<TimeTool> dates = new ArrayList<TimeTool>();

		String sql = "SELECT Datum, ItemID FROM " + LABORWRTE_TABLENAME + " WHERE PatientID = ?"
				+ " GROUP BY Datum, ItemID" + " ORDER BY Datum";

		try {
			// temporary list for avoiding double values
			List<String> dateStrings = new ArrayList<String>();

			PreparedStatement ps = PersistentObject.getConnection().prepareStatement(sql);
			if (ps != null) {
				ps.setString(1, patient.getId());
				if (ps.execute()) {
					ResultSet rs = ps.getResultSet();
					while (rs.next()) {
						String date = rs.getString(1);
						String itemId = rs.getString(2);

						if (!dateStrings.contains(date)) {
							for (LabItem labItem : labItems) {
								if (itemId.equals(labItem.getId())) {
									// remember this date to avoid duplicates
									dateStrings.add(date);

									// add this date to the returned dates
									dates.add(new TimeTool(date));

									// don't continue with further LabItems
									break;
								}
							}
						}
					}
				}
			}
		} catch (SQLException ex) {
			ExHandler.handle(ex);
			log.error("Fehler beim Ausführen von " + sql);
		}
		log.debug("getAvailableDatesOfGroup: return " + dates.size() + " for " + patient.getPersonalia());
		return dates;
	}

	/**
	 * Get all LabItems the given patient has lab results for.
	 *
	 * @param patient the patient to get available lab items for
	 * @return all available lab items for the given patient. not ordered.
	 */
	public static List<LabItem> getAvailableItems(Patient patient) {
		List<LabItem> items = new ArrayList<LabItem>();

		String sql = "SELECT ItemID FROM " + LABORWRTE_TABLENAME + " WHERE PatientID = ?" + " GROUP BY ItemID";

		try {
			PreparedStatement ps = PersistentObject.getConnection().prepareStatement(sql);
			if (ps != null) {
				ps.setString(1, patient.getId());
				if (ps.execute()) {
					ResultSet rs = ps.getResultSet();
					while (rs.next()) {
						String itemId = rs.getString(1);
						LabItem item = LabItem.load(itemId);
						if (item != null) {
							items.add(item);
						}
					}
				}
			}
		} catch (SQLException ex) {
			ExHandler.handle(ex);
			log.error("Fehler beim Ausführen von " + sql);
		}
		log.debug("getAvailableItems: return " + items.size() + " items for " + patient.getPersonalia());
		return items;
	}

	/*
	 * The content provider class is responsible for providing objects to the view.
	 * It can wrap existing objects in adapters or simply return objects as-is.
	 * These objects may be sensitive to the current input of the view, or ignore it
	 * and always show the same content (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return viewerRows.toArray();
		}
	}

	/*
	 * Used by ViewLabelProvider
	 */
	private static Font boldFont = null;

	class ViewLabelProvider extends ColumnLabelProvider {

		private int columnIndex;

		ViewLabelProvider(int columnIndex) {
			super();

			this.columnIndex = columnIndex;
		}

		public String getText(Object element) {
			return getColumnText(element, columnIndex);
		}

		public Font getFont(Object element) {
			return getFont(element, columnIndex);
		}

		public Color getForeground(Object element) {
			return getForeground(element, columnIndex);
		}

		public Color getBackground(Object element) {
			return null;
		}

		public String getColumnText(Object obj, int index) {
			String text = StringUtils.EMPTY;

			if (obj instanceof LabRowGroup) {
				if (index == 0) {
					LabRowGroup labRowGroup = (LabRowGroup) obj;
					text = labRowGroup.group.groupName;
					int spaceIndex = text.indexOf(StringUtils.SPACE);
					if (spaceIndex >= 0) {
						text = text.substring(spaceIndex + 1);
					}
				}
			} else if (obj instanceof LabRowValues) {
				LabRowValues row = (LabRowValues) obj;
				if (index == 0) {
					text = row.labItem.getShortLabel();
				} else if (index == 1) {
					if (actPatient != null) {
						if (actPatient.getGeschlecht().equals(FEMININ)) {
							text = row.labItem.getRefW();
						} else {
							text = row.labItem.getRefM();
						}
					}
				} else {
					// index >= DATES_OFFSET: date
					int datesIndex = index - DATES_OFFSET;
					TableDate tableDate = tableDates[datesIndex];
					if (tableDate != null) {
						String date = tableDate.date;
						int valueIndex = tableDate.index;

						if (!StringTool.isNothing(date)) {
							List<LabResult> values = row.results.get(date);
							// check whether the requested value exists
							if (values != null && values.size() > valueIndex) {
								StringBuffer sb = new StringBuffer();
								boolean isPathologic = false;
								LabResult labResult = values.get(valueIndex);
								sb.append(labResult.getResult());
								if (labResult.isFlag(LabResultConstants.PATHOLOGIC)) {
									isPathologic = true;
								}

								if (isPathologic && SWT.getPlatform().equals("gtk")) {
									sb.insert(0, "*");
								}
								text = sb.toString();
								// Länge 15 ist Wunsch von Peter Schönbucher.
								// https://redmine.medelexis.ch/issues/491
								if (text.equalsIgnoreCase("text") && labResult.getComment().length() <= 15) {
									text = labResult.getComment();
								}
							}
						}
					}
				}
			}
			return text;
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Color getForeground(Object element, int columnIndex) {
			if (columnIndex >= DATES_OFFSET) {
				if (element instanceof LabRowValues) {
					LabRowValues row = (LabRowValues) element;

					// index >= DATES_OFFSET: date
					int datesIndex = columnIndex - DATES_OFFSET;
					TableDate tableDate = tableDates[datesIndex];
					if (tableDate != null) {
						String date = tableDate.date;
						int valueIndex = tableDate.index;

						if (!StringTool.isNothing(date)) {
							List<LabResult> values = row.results.get(date);
							if (values != null && values.size() > valueIndex) {
								boolean isPathologic = false;
								boolean hasComment = false;
								LabResult labResult = values.get(valueIndex);
								if (labResult.isFlag(LabResultConstants.PATHOLOGIC)) {
									isPathologic = true;
								}
								if (!StringTool.isNothing(labResult.getComment())) {
									hasComment = true;
								}

								if (isPathologic) {
									return viewer.getTable().getDisplay().getSystemColor(SWT.COLOR_RED);
								} else if (hasComment) {
									return viewer.getTable().getDisplay().getSystemColor(SWT.COLOR_BLUE);
								}
							}
						}
					}
				}
			}

			// default color
			return null;
		}

		public Color getBackground(Object element, int columnIndex) {
			// default color
			return null;
		}

		public Font getFont(Object element, int columnIndex) {
			if (element instanceof LabRowGroup) {
				if (boldFont == null) {
					Font defaultFont = viewer.getTable().getFont();
					FontData defaultFontData = defaultFont.getFontData()[0];
					boldFont = new Font(defaultFont.getDevice(), defaultFontData.getName(), defaultFontData.getHeight(),
							defaultFontData.getStyle() | SWT.BOLD);
				}
				return boldFont;
			} else {
				// default font
				return null;
			}
		}
	}

	class ColumnWidthSafer extends ControlAdapter {
		private int columnIndex;

		ColumnWidthSafer(int columnIndex) {
			this.columnIndex = columnIndex;
		}

		public void controlResized(ControlEvent e) {
			if (e.widget instanceof TableColumn) {
				TableColumn eventTableColumn = (TableColumn) e.widget;
				int width = eventTableColumn.getWidth();
				setInitialColumnWidth(columnIndex, width);
			}

			updateDateColumnWidths();
		}
	}

	/**
	 * The constructor.
	 */
	public MesswerteView() {
	}

	private int getInitialColumnWidth(int columnIndex) {
		switch (columnIndex) {
		case PARAMETER_INDEX:
			return CoreHub.localCfg.get(Constants.CFG_MESSWERTE_VIEW_COLUMN_WIDTH_PREFIX + columnIndex,
					COLUMN_NAME_DEFAULT_WITH);
		case REF_INDEX:
			return CoreHub.localCfg.get(Constants.CFG_MESSWERTE_VIEW_COLUMN_WIDTH_PREFIX + columnIndex,
					COLUMN_REF_DEFAULT_WITH);
		default:
			return CoreHub.localCfg.get(Constants.CFG_MESSWERTE_VIEW_COLUMN_WIDTH_PREFIX + columnIndex,
					COLUMN_DATE_DEFAULT_WITH);
		}
	}

	private void setInitialColumnWidth(int columnIndex, int width) {
		CoreHub.localCfg.set(Constants.CFG_MESSWERTE_VIEW_COLUMN_WIDTH_PREFIX + columnIndex, width);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		columnsPerPage = CoreHub.localCfg.get(Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS,
				new Integer(Constants.CFG_MESSWERTE_VIEW_NUMBER_OF_COLUMNS_DEFAULT));
		tableDateColumns = new TableViewerColumn[columnsPerPage];
		tableDates = new TableDate[columnsPerPage];

		Composite headerArea = new Composite(parent, SWT.NONE);
		headerArea.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		headerArea.setLayout(new GridLayout(2, false));

		Composite filterArea = new Composite(headerArea, SWT.NONE);
		filterArea.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		filterArea.setLayout(new GridLayout(2, false));

		Label filterLabel = new Label(filterArea, SWT.NONE);
		filterLabel.setText("Messwert-Gruppen:");

		laborGroupsViewer = new ComboViewer(filterArea, SWT.DROP_DOWN | SWT.H_SCROLL | SWT.V_SCROLL);
		laborGroupsViewer.setContentProvider(new LabGroupsContentProvider());

		laborGroupsViewer.setInput(this);

		laborGroupsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// reload the whole table
				reload();
			}
		});

		Composite infoArea = new Composite(headerArea, SWT.NONE);
		infoArea.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		infoArea.setLayout(new GridLayout(1, false));

		pagesLabel = new Label(infoArea, SWT.RIGHT);
		pagesLabel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		pagesLabel.setText(StringUtils.EMPTY);

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		focusCellManager = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor.create(viewer, focusCellManager, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tableNameColumn = new TableViewerColumn(viewer, SWT.LEFT);
		tableNameColumn.getColumn().setWidth(getInitialColumnWidth(PARAMETER_INDEX));
		tableNameColumn.getColumn().addControlListener(new ColumnWidthSafer(PARAMETER_INDEX));
		tableNameColumn.getColumn().setText("Parameter");
		tableNameColumn.setLabelProvider(new ViewLabelProvider(PARAMETER_INDEX));

		tableRefColumn = new TableViewerColumn(viewer, SWT.LEFT);
		tableRefColumn.getColumn().setWidth(getInitialColumnWidth(REF_INDEX));
		tableRefColumn.getColumn().addControlListener(new ColumnWidthSafer(REF_INDEX));
		tableRefColumn.getColumn().setText("Ref");
		tableRefColumn.setLabelProvider(new ViewLabelProvider(REF_INDEX));

		for (int i = 0; i < columnsPerPage; i++) {
			tableDateColumns[i] = new TableViewerColumn(viewer, SWT.LEFT);

			tableDateColumns[i].getColumn().setWidth(COLUMN_DATE_INITIAL_MIN_WIDTH);
			// column widths are updated by updateDateColumnWidths();

			tableDates[i] = null;

			tableDateColumns[i].setEditingSupport(new DateEditingSupport(viewer, i));

			tableDateColumns[i].setLabelProvider(new ViewLabelProvider(DATES_OFFSET + i));
		}

		viewer.setContentProvider(new ViewContentProvider());
		// viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		initialize();

		// manage date column's width (equal sizes)
		viewer.getTable().addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				updateDateColumnWidths();
			}
		});

		getSite().getPage().addPartListener(udpateOnVisible);
	}

	/**
	 * make sure the date columns have equal sizes.
	 */
	private void updateDateColumnWidths() {
		int tableWidth = viewer.getTable().getSize().x;
		int nameColumnWidth = getInitialColumnWidth(PARAMETER_INDEX);
		int refColumnWidth = getInitialColumnWidth(REF_INDEX);
		int dateColumnsWidth = tableWidth - nameColumnWidth - refColumnWidth;
		if (dateColumnsWidth > 0) {
			int dateColumnWidth = dateColumnsWidth / columnsPerPage;

			int usedWidth = 0;

			// all date columns except last one
			for (int i = 0; i < tableDateColumns.length - 1; i++) {
				tableDateColumns[i].getColumn().setWidth(dateColumnWidth);
				usedWidth += dateColumnWidth;
			}
			// last column (remaining space)
			int remainingWidth = dateColumnsWidth - usedWidth;
			tableDateColumns[tableDateColumns.length - 1].getColumn().setWidth(remainingWidth);
		}
	}

	private void initialize() {
		loadLaborGroups();
		loadOwnLaborsFromConfig();

		reload();
	}

	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);

		super.dispose();
	}

	/**
	 * Update the labor groups in the combo
	 */
	private void loadLaborGroups() {
		labGroupElements = new ArrayList<BaseLabGroupElement>();

		labGroupElements.add(new AllGroupElement());
		labGroupElements.add(new OwnLabsElement());

		List<LabGroup> labGroups = getLabGroups();
		for (LabGroup labGroup : labGroups) {
			Group group = new Group(labGroup);
			labGroupElements.add(new GroupElement(group));
		}

		// labitem groups
		List<String> labItemGroups = getLabItemGroups();
		for (String labItemGroup : labItemGroups) {
			Group group = new Group(labItemGroup);
			labGroupElements.add(new GroupElement(group));
		}

		laborGroupsViewer.refresh();
		selectLabGroup(labGroupElements.get(0));
	}

	private void selectLabGroup(BaseLabGroupElement element) {
		IStructuredSelection sel = new StructuredSelection(element);
		laborGroupsViewer.setSelection(sel);
	}

	private BaseLabGroupElement getSelectedLabGroupElement() {
		IStructuredSelection sel = (IStructuredSelection) laborGroupsViewer.getSelection();
		BaseLabGroupElement labGroupElement = (BaseLabGroupElement) sel.getFirstElement();
		return labGroupElement;
	}

	private List<LabGroup> getLabGroups() {
		Query<LabGroup> query = new Query<LabGroup>(LabGroup.class);
		query.orderBy(false, "Name");
		List<LabGroup> labGroups = query.execute();
		if (labGroups == null) {
			labGroups = new ArrayList<LabGroup>();
		}

		return labGroups;
	}

	private List<String> getLabItemGroups() {
		List<String> labItemGroups = new ArrayList<String>();

		Query<LabItem> query = new Query<LabItem>(LabItem.class);
		query.orderBy(false, "Gruppe");
		List<LabItem> items = query.execute();
		if (items != null) {
			for (LabItem item : items) {
				String groupName = item.getGroup();
				if (!labItemGroups.contains(groupName)) {
					labItemGroups.add(groupName);
				}
			}
		}

		return labItemGroups;
	}

	private void loadOwnLaborsFromConfig() {
		ownLabors = new ArrayList<Labor>();

		String localLabors = ConfigServiceHolder.getGlobal(Constants.CFG_LOCAL_LABORS,
				Constants.CFG_DEFAULT_LOCAL_LABORS);
		String[] laborIds = localLabors.split("\\s*,\\s*");
		for (String laborId : laborIds) {
			if (!StringTool.isNothing(laborId)) {
				Labor labor = Labor.load(laborId);
				if (labor != null && labor.exists()) {
					ownLabors.add(labor);
				}
			}
		}
	}

	/**
	 * Check if this is a "local" lab
	 *
	 * @param labor the lab to test
	 * @return true, if labor is a configured local lab
	 */
	private boolean isOwnLabor(Labor labor) {
		if (labor == null) {
			return false;
		}

		return ownLabors.contains(labor);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MesswerteView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(newDateAction);
		manager.add(backAction);
		manager.add(fwdAction);
		// manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// show "Pathologisch" if cell contains lab value
		Object element = viewer.getColumnViewerEditor().getFocusCell().getElement();
		int columnIndex = viewer.getColumnViewerEditor().getFocusCell().getColumnIndex();
		if (element instanceof LabRowValues) {
			LabRowValues labRowValues = (LabRowValues) element;

			if (columnIndex >= DATES_OFFSET) {
				int datesIndex = columnIndex - DATES_OFFSET;
				TableDate tableDate = tableDates[datesIndex];
				if (tableDate != null) {
					String date = tableDate.date;
					int valueIndex = tableDate.index;

					if (!StringTool.isNothing(date)) {
						List<LabResult> labResults = labRowValues.results.get(date);
						if (labResults != null && labResults.size() > valueIndex) {
							LabResult labResult = labResults.get(valueIndex);
							boolean isPathologic = false;
							if (labResult.isFlag(LabResultConstants.PATHOLOGIC)) {
								isPathologic = true;
							}
							pathologicAction.setChecked(isPathologic);
							manager.add(pathologicAction);
						}
					}
				}
			}
		}
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(newDateAction);
		manager.add(backAction);
		manager.add(fwdAction);
	}

	private void makeActions() {
		newDateAction = new Action() {
			@Override
			public void run() {
				DateSelectorDialog dsd = new DateSelectorDialog(getViewSite().getShell());
				if (dsd.open() == DateSelectorDialog.OK) {
					TimeTool date = dsd.getSelectedDate();

					for (int i = dateColumns.size() - 1; i >= 0; i--) {
						DateColumn dateColumn = dateColumns.get(i);
						if (dateColumn.date.equals(date)) {
							// We already have an entry for this date
							MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
									"Datumskonflikt", "Es existiert bereits ein Eintrag für dieses Datum.");
							return;
						}
					}

					boolean found = false;

					// update data structure (reverse for better performance,
					// since dates are usually added at the end
					for (int i = dateColumns.size() - 1; i >= 0; i--) {
						DateColumn dateColumn = dateColumns.get(i);

						TimeTool currentDate = dateColumn.date;
						int cmp = currentDate.compareTo(date);
						if (cmp < 0) {
							// current date is lower; insert new date after current date
							DateColumn newDateColumn = new DateColumn(date, 1);
							dateColumns.add(i + 1, newDateColumn);
							found = true;
							break;
						} else if (cmp == 0) {
							// add a new column to the current date
							dateColumn.numberOfColumns++;
							found = true;
							break;
						}
					}

					if (!found) {
						// new date is before any existing columns; put it at
						// the beginning
						DateColumn newDateColumn = new DateColumn(date, 1);
						dateColumns.add(0, newDateColumn);
					}

					prepareViewerPages();

					// set current page so that the new date is visible
					currentPage = lastPage; // default if not found
					int index = -1;
					// find the latest corresponding column
					for (int i = dateColumnsExpanded.size() - 1; i >= 0; i--) {
						TableDate tableDate = dateColumnsExpanded.get(i);
						if (tableDate.date.equals(date)) {
							// found
							index = i;
							break;
						}
					}

					if (index >= 0) {
						// set the corresponding page
						int numberOfDates = dateColumnsExpanded.size();
						int firstPageColumnOffset = (columnsPerPage - (numberOfDates % columnsPerPage))
								% columnsPerPage;
						int indexInPages = index + firstPageColumnOffset;

						currentPage = indexInPages / columnsPerPage;
					}

					updateViewerPage();
				}
			}
		};

		newDateAction.setText("Neues Datum...");
		newDateAction.setToolTipText("Neue Datum-Spalte erstellen");
		newDateAction.setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());

		fwdAction = new Action() {
			@Override
			public void run() {
				if (currentPage < lastPage) {
					currentPage++;
					updateViewerPage();
				}
			}
		};
		fwdAction.setText("Nächste Seite");
		fwdAction.setToolTipText("Nächste Seite");
		fwdAction.setImageDescriptor(Images.IMG_NEXT.getImageDescriptor());

		backAction = new Action() {
			@Override
			public void run() {
				if (currentPage > 0) {
					currentPage--;
					updateViewerPage();
				}
			}
		};
		backAction.setText("Vorherige Seite");
		backAction.setToolTipText("Vorherige Seite");
		backAction.setImageDescriptor(Images.IMG_PREVIOUS.getImageDescriptor());

		/*
		 * Set the currently selected lab results to "pathologic"
		 */
		pathologicAction = new Action() {
			@Override
			public void run() {
				Object element = viewer.getColumnViewerEditor().getFocusCell().getElement();
				int columnIndex = viewer.getColumnViewerEditor().getFocusCell().getColumnIndex();
				if (element instanceof LabRowValues) {
					LabRowValues labRowValues = (LabRowValues) element;

					if (columnIndex >= DATES_OFFSET) {
						int datesIndex = columnIndex - DATES_OFFSET;
						TableDate tableDate = tableDates[datesIndex];
						if (tableDate != null) {
							String date = tableDate.date;
							int valueIndex = tableDate.index;

							if (!StringTool.isNothing(date)) {
								List<LabResult> labResults = labRowValues.results.get(date);
								if (labResults != null && labResults.size() > valueIndex) {
									LabResult labResult = labResults.get(valueIndex);
									// isChecked() returnes the value as
									// activated by the user's click
									boolean isPathologic = isChecked();
									labResult.setFlag(LabResultConstants.PATHOLOGIC, isPathologic);
									labResult.setPathologicDescription(
											new PathologicDescription(Description.PATHO_MANUAL));
									viewer.refresh();
								}
							}
						}
					}
				}
			}
		};
		pathologicAction.setText("Pathologisch");
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClicked();
			}
		});
	}

	private void doubleClicked() {
		log.debug("doubleClicked");

		ViewerCell cell = focusCellManager.getFocusCell();
		Object element = cell.getElement();
		if (element instanceof LabRowValues) {
			LabRowValues labRowValues = (LabRowValues) element;
			int columnIndex = cell.getColumnIndex();
			if (columnIndex >= DATES_OFFSET) {
				int datesIndex = columnIndex - DATES_OFFSET;
				LabItem labItem = labRowValues.labItem;

				log.debug("index+" + datesIndex + ", item: " + labItem.getShortLabel());

				if (labItem.getTyp() == LabItemTyp.TEXT) {
					StringBuffer sb = new StringBuffer();
					System.getProperty("line.separator");

					TableDate tableDate = tableDates[datesIndex];
					if (tableDate != null) {
						String date = tableDate.date;
						int valueIndex = tableDate.index;

						if (!StringTool.isNothing(date)) {
							List<LabResult> labResults = labRowValues.results.get(date);
							if (labResults != null && labResults.size() > valueIndex) {
								LabResult labResult = labResults.get(valueIndex);
								sb.append(labResult.getComment());
								new DisplayTextDialog(getViewSite().getShell(), "Textbefund " + date, labItem.getName(),
										sb.toString()).open();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Set new patient
	 *
	 * @param patient
	 */
	private void setPatient(Patient patient) {
		boolean changed = false;
		if (patient != null || actPatient != null) {
			// inv: not both objects are == null

			if (patient == null || actPatient == null) {
				// inv: one object == null, one object != null

				changed = true;
			} else {
				// inv: both objects are != null

				if (!patient.getId().equals(actPatient.getId())) {
					changed = true;
				}
			}
		}

		if (changed) {
			this.actPatient = patient;

			// reload configuration of own labors, just to be up-to-date
			loadOwnLaborsFromConfig();

			// reload labor items and values
			reload();
		}
	}

	/**
	 * Re-create table and refresh values
	 */
	private void reload() {
		List<LabRow> newViewerRows = new ArrayList<LabRow>();
		availableDates = new ArrayList<TimeTool>();
		dateColumns = new ArrayList<DateColumn>();
		HashMap<String, DateColumn> dateColumnsLookup = new HashMap<String, DateColumn>();

		if (actPatient != null) {
			BaseLabGroupElement currentLabGroupElement = getSelectedLabGroupElement();
			List<LabItem> items = currentLabGroupElement.getLabItems();
			if (items != null) {
				// specific group chosen. get available dates for this group
				availableDates = getAvailableDatesOfGroup(actPatient, items);
			} else {
				// all items chosen. get all dates having any lab results
				items = getAvailableItems(actPatient);
				availableDates = getAvailableDates(actPatient);
			}

			// update dateColumns from availableDates
			for (TimeTool time : availableDates) {
				// create a DateColumn with a single column per date
				// (will be updated while loading values below)
				DateColumn dateColumn = new DateColumn(time, 1);
				dateColumns.add(dateColumn);
				// dateColumnsLookup must be in human readable format
				dateColumnsLookup.put(time.toString(TimeTool.DATE_GER_SHORT), dateColumn);
			}

			/*
			 * Sort LabItems use temporary comparator, as LabItem's comparator is not yet
			 * available, as of 2008-05-29
			 */
			Collections.sort(items, new Comparator<LabItem>() {
				public int compare(LabItem item1, LabItem item2) {
					// check for null; put null values at the end
					if (item1 == null) {
						return 1;
					}
					if (item2 == null) {
						return -1;
					}

					// first, compare the groups
					String mineGroup = item1.getGroup();
					String otherGroup = item2.getGroup();
					if (!mineGroup.equals(otherGroup)) {
						// groups differ, just compare groups
						return mineGroup.compareTo(otherGroup);
					}

					// compare item priorities
					String mine = item1.getPrio();
					String others = item2.getPrio();
					if ((mine.matches("[0-9]+")) && (others.matches("[0-9]+"))) {
						Integer iMine = Integer.parseInt(mine);
						Integer iOthers = Integer.parseInt(others);
						return iMine.compareTo(iOthers);
					}
					return mine.compareTo(others);
				}
			});

			// inv: items are sorted by group/prio

			// if (!availableDates.isEmpty() && !items.isEmpty()) {
			if (!items.isEmpty()) {
				HashMap<String, LabRowValues> itemsLookup = new HashMap<String, LabRowValues>();

				List<String> groupNames = new ArrayList<String>();
				boolean isFirstGroup = true;

				for (LabItem item : items) {
					String groupName = item.getGroup();
					if (groupName != null && !groupNames.contains(groupName)) {
						// start a new group

						if (isFirstGroup) {
							isFirstGroup = false;
						} else {
							newViewerRows.add(new LabRowSeparator());
						}

						groupNames.add(groupName);
						Group group = new Group(groupName);
						LabRowGroup labRowGroup = new LabRowGroup(group);
						newViewerRows.add(labRowGroup);
					}

					// add the item (with empty values hash map)
					LabRowValues labRowValues = new LabRowValues(item, new HashMap<String, List<LabResult>>());
					newViewerRows.add(labRowValues);
					// put the item/row into the lookup table using its id
					itemsLookup.put(item.getId(), labRowValues);
				}

				// get the results
				Query<LabResult> query = new Query<LabResult>(LabResult.class);
				query.add("PatientID", "=", actPatient.getId());
				List<LabResult> labResults = query.execute();
				if (labResults != null) {
					for (LabResult labResult : labResults) {
						String dateDisplay = new TimeTool(labResult.getDate()).toString(TimeTool.DATE_GER_SHORT);
						String dateSqlColumn = new TimeTool(labResult.getDate()).toString(TimeTool.DATE_COMPACT);

						LabRowValues labRowValues = itemsLookup.get(labResult.getItem().getId());
						if (labRowValues != null) {
							List<LabResult> values = labRowValues.results.get(dateSqlColumn);
							if (values == null) {
								values = new ArrayList<LabResult>();
								labRowValues.results.put(dateSqlColumn, values);
							}
							values.add(labResult);

							// update dateColumns (number of columns of same
							// date)
							DateColumn dateColumn = dateColumnsLookup.get(dateDisplay);
							if (dateColumn != null) {
								if (dateColumn.numberOfColumns < values.size()) {
									dateColumn.numberOfColumns = values.size();
								}
							} else {
								log.error("Serious error for " + dateDisplay);
							} // else serious error!
						} // else, serious error!
					}
				}
			}
		}

		viewerRows = newViewerRows;

		// update data structures for all pages
		prepareViewerPages();

		// initial page
		currentPage = lastPage;

		// set new viewport
		updateViewerPage();

		updateDateColumnWidths();
	}

	private void prepareViewerPages() {
		dateColumnsExpanded = new ArrayList<TableDate>();

		// update expand dateColumns for easier access
		for (DateColumn dateColumn : dateColumns) {
			for (int i = 0; i < dateColumn.numberOfColumns; i++) {
				TableDate tableDate = new TableDate(dateColumn.date, i);
				dateColumnsExpanded.add(tableDate);
			}
		}
		// update the viewer configuration
		int numberOfDates = dateColumnsExpanded.size();
		lastPage = (numberOfDates - 1) / columnsPerPage;
	}

	private void updateViewerPage() {
		// see CodeRelations.ods for calculation instructions

		int numberOfDates = dateColumnsExpanded.size();

		int pageOffset = currentPage * columnsPerPage;
		int minColumnIndex = 0;

		for (int i = 0; i < tableDateColumns.length; i++) {
			if (numberOfDates > 0 && i >= minColumnIndex) {
				try {
					TableDate tableDate = null;
					if ((i + pageOffset) < numberOfDates) {
						tableDate = dateColumnsExpanded.get(i + pageOffset);
						// convert compact date to TimeTool.DATE_GER_SHORT for formatting
						String strDate = new TimeTool(tableDate.date).toString(TimeTool.DATE_GER_SHORT);

						tableDateColumns[i].getColumn().setText(strDate);
					} else {
						tableDateColumns[i].getColumn().setText(StringUtils.EMPTY);
					}
					tableDates[i] = tableDate;
				} catch (IndexOutOfBoundsException e) {
					System.out.println(e);
				}

			} else {
				// no date column available
				tableDateColumns[i].getColumn().setText(StringUtils.EMPTY);
				tableDates[i] = null;
			}
		}

		viewer.refresh();

		if (actPatient != null) {
			pagesLabel.setText("Seite " + (currentPage + 1) + "/" + (lastPage + 1));
		} else {
			pagesLabel.setText(StringUtils.EMPTY);
		}

		// update action status
		backAction.setEnabled(currentPage > 0);
		fwdAction.setEnabled(currentPage < lastPage);
	}

	/*
	 * HeartListener
	 */

	public void heartbeat() {
		// TODO
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}

	/**
	 * Class representing labor groups
	 */
	class Group {
		private boolean isLabGroup = false;;
		private LabGroup labGroup = null;
		private String groupName = StringUtils.EMPTY;

		Group(LabGroup labGroup) {
			this.labGroup = labGroup;
			this.groupName = labGroup.getName();
			isLabGroup = true;
		}

		Group(String groupName) {
			this.groupName = groupName;
			isLabGroup = false;
		}
	}

	/**
	 * Base for LabRowGroup and LabRowValues
	 */
	interface LabRow {
	}

	// empty row as separator
	class LabRowSeparator implements LabRow {
	}

	/**
	 * Class representing labor groups as rows in the viewer.
	 */
	class LabRowGroup implements LabRow {
		Group group;

		LabRowGroup(Group group) {
			this.group = group;
		}
	}

	/**
	 * Class representing labor value rows in the viewer. The results are stored in
	 * a hash map, with the date as the key.
	 */
	class LabRowValues implements LabRow {
		LabItem labItem;
		HashMap<String, List<LabResult>> results;

		LabRowValues(LabItem labItem, HashMap<String, List<LabResult>> results) {
			this.labItem = labItem;
			this.results = results;
			if (this.results == null) {
				this.results = new HashMap<String, List<LabResult>>();
			}
		}
	}

	class DateEditingSupport extends EditingSupport {
		private final CellEditor valueEditor;

		DateEditingSupport(TableViewer viewer, int datesIndex) {
			super(viewer);

			valueEditor = new TextCellEditor(viewer.getTable());
		}

		public CellEditor getCellEditor(Object element) {
			if (element instanceof LabRowValues) {
				log.debug("getCellEditor");
				return valueEditor;
			} else {
				log.debug("no getCellEditor for " + element.getClass().getName());
				return null;
			}
		}

		public boolean canEdit(Object element) {

			ViewerCell cell = focusCellManager.getFocusCell();
			int columnIndex = cell.getColumnIndex();
			if (element instanceof LabRowValues) {
				if (columnIndex >= DATES_OFFSET) {
					LabRowValues labRowValues = (LabRowValues) element;
					LabItem labItem = labRowValues.labItem;

					Labor labor = labItem.getLabor();
					log.debug("canEdit preIsOwnLabor");
					return isOwnLabor(labor);
				}
			}
			log.debug("canEdit false for " + element.getClass().getName());
			return false;
		}

		public Object getValue(Object element) {
			String value = StringUtils.EMPTY;

			ViewerCell cell = focusCellManager.getFocusCell();
			int columnIndex = cell.getColumnIndex();
			if (element instanceof LabRowValues) {
				LabRowValues labRowValues = (LabRowValues) element;

				if (columnIndex >= DATES_OFFSET) {
					int datesIndex = columnIndex - DATES_OFFSET;
					LabItem labItem = labRowValues.labItem;

					TableDate tableDate = tableDates[datesIndex];
					if (tableDate != null) {
						String date = tableDate.date;
						int valueIndex = tableDate.index;

						if (!StringTool.isNothing(date)) {
							List<LabResult> results = labRowValues.results.get(date);
							if (results != null && results.size() > valueIndex) {
								LabResult labResult = results.get(valueIndex);
								if (labItem.getTyp() == LabItemTyp.TEXT) {
									value = labResult.getComment();
									if (StringTool.isNothing(value)) {
										value = labResult.getResult();
									}
								} else {
									value = labResult.getResult();
								}
							}
						}
					}
				}
			}

			return value;
		}

		public void setValue(Object element, Object value) {
			ViewerCell cell = focusCellManager.getFocusCell();
			int columnIndex = cell.getColumnIndex();
			if (element instanceof LabRowValues && value instanceof String) {
				LabRowValues labRowValues = (LabRowValues) element;
				String newValue = (String) value;

				if (columnIndex >= DATES_OFFSET) {
					int datesIndex = columnIndex - DATES_OFFSET;
					LabItem labItem = labRowValues.labItem;

					TableDate tableDate = tableDates[datesIndex];
					if (tableDate != null) {
						String date = tableDate.date;
						int valueIndex = tableDate.index;

						if (!StringTool.isNothing(date)) {
							List<LabResult> results = labRowValues.results.get(date);
							if (results == null) {
								results = new ArrayList<LabResult>();
								labRowValues.results.put(date, results);
							}
							if (!(results.size() > valueIndex)) {
								// no value exists yet for this column; create
								// it now
								LabResult labResult;
								TimeTool timeTool = new TimeTool(date);
								if (labItem.getTyp() == LabItemTyp.TEXT) {
									labResult = new LabResult(actPatient, timeTool, labItem, "text", newValue);
								} else {
									labResult = new LabResult(actPatient, timeTool, labItem, newValue,
											StringUtils.EMPTY);
								}

								// update data structure
								results.add(labResult);
							} else {
								// update
								LabResult labResult = results.get(valueIndex);
								if (labItem.getTyp() == LabItemTyp.TEXT) {
									labResult.set("Kommentar", newValue);
								} else {
									labResult.setResult(newValue);
								}
							}
							viewer.update(labRowValues, null);
						}
					}
				}
			}
		}
	}

	abstract class BaseLabGroupElement {
		abstract public String getLabel();

		public String toString() {
			return getLabel();
		}

		/**
		 * The LabItems contained in this group. return the LabItems contained in this
		 * group. A return value of null means this group contains all available
		 * LabItems.
		 */
		abstract public List<LabItem> getLabItems();
	}

	class AllGroupElement extends BaseLabGroupElement {
		public AllGroupElement() {
			// nothing to do
		}

		public String getLabel() {
			return GROUPS_ALL;
		}

		public List<LabItem> getLabItems() {
			return null;
		}
	}

	class OwnLabsElement extends BaseLabGroupElement {
		public OwnLabsElement() {
			// nothing to do
		}

		public String getLabel() {
			return GROUPS_PRAXIS;
		}

		public List<LabItem> getLabItems() {
			List<LabItem> ownLabsItems = new ArrayList<LabItem>();

			Query<LabItem> query = new Query<LabItem>(LabItem.class);
			List<LabItem> labItems = query.execute();
			if (labItems != null) {
				for (LabItem labItem : labItems) {
					for (Labor ownLabor : ownLabors) {
						if (labItem.getLabor() != null && labItem.getLabor().getId() != null
								&& labItem.getLabor().getId().equals(ownLabor.getId())) {
							ownLabsItems.add(labItem);
						}
					}
				}
			}

			return ownLabsItems;
		}
	}

	class GroupElement extends BaseLabGroupElement {
		private Group group;

		public GroupElement(Group group) {
			this.group = group;
		}

		public String getLabel() {
			return group.groupName;
		}

		public List<LabItem> getLabItems() {
			List<LabItem> labItems = new ArrayList<LabItem>();

			if (group.isLabGroup) {
				// group is of type LabGroup

				LabGroup labGroup = group.labGroup;
				labItems.addAll(labGroup.getItems());
			} else {
				// group is just a name

				String groupName = group.groupName;

				Query<LabItem> query = new Query<LabItem>(LabItem.class);
				List<LabItem> groupLabItems = query.execute();
				if (groupLabItems != null) {
					for (LabItem labItem : groupLabItems) {
						if (labItem.getGroup().equals(groupName)) {
							labItems.add(labItem);
						}
					}
				}
			}

			return labItems;
		}
	}

	class LabGroupsContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			return labGroupElements.toArray();
		}

		public void dispose() {
			// do nothing
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			// do nothing
		}
	}

	/**
	 * Column instance; items of tableDates array
	 */
	class TableDate {
		/**
		 * Same format as in SQL-table Laborwerte, name yyyyMMdd (DATE_COMPACT)
		 */
		String date;
		int index;

		TableDate(TimeTool date, int index) {
			this.date = date.toString(TimeTool.DATE_COMPACT);
			this.index = index;
		}
	}

	/**
	 * used available dates, including number of columns of the same date
	 */
	class DateColumn {
		/**
		 * Date is a Timetool object for easier and correct comparison
		 */
		TimeTool date;
		/**
		 * Number of columns for the same date
		 *
		 * @note Manual entries are restricted to one per date
		 */
		int numberOfColumns;

		DateColumn(TimeTool date, int numberOfColumns) {
			this.date = date;
			this.numberOfColumns = numberOfColumns;
		}
	}

	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			setPatient((Patient) NoPoUtil.loadAsPersistentObject(patient));
		}, viewer);
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}
}