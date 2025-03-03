/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.view;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.LabItem;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.gpb.elexis.cst.Activator;
/*
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstAbstract;
import ch.gpb.elexis.cst.data.CstGroup;
import ch.gpb.elexis.cst.data.LabItemWrapper;
import ch.gpb.elexis.cst.dialog.CstCategoryDialog;
import ch.gpb.elexis.cst.dialog.CstLabItemSelectionDialog;
import ch.gpb.elexis.cst.dialog.ThemenblockDetailDialog;

/**
 *
 * @author daniel created: 11.01.2015 GUI Class for administration of CST Groups
 *
 */
public class CstThemenblockEditor extends ViewPart implements IActivationListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ch.gpb.elexis.cst.views.cstcategoryview";

	private CheckboxTableViewer tableViewerCstGroup;
	private CheckboxTableViewer tableViewerLabItem;
	private Action actionCreateCstGroup;
	private Action actionDeleteCstGroup;
	private Action actionRemoveLabItem;
	private Action actionAddLabItems;
	private Action actionDisplayOnce;
	private Table tableCstGroup;
	private Table tableLabItem;
	private int sortColumn = 0;
	private boolean sortReverse = false;
	private Color myColorRed;
	private List<CstGroup> cstGroups;
	private List<LabItemWrapper> labItems = new ArrayList<LabItemWrapper>();
	private List<LabItem> dialogLabItems = new ArrayList<LabItem>();
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	// static Random rnd = new Random();
	Patient patient;
	Label labelLeft;
	Text txtAbstract;
	Map<Object, Object> itemRanking = null;
	private Logger log = LoggerFactory.getLogger(CstThemenblockEditor.class.getName());

	private boolean isRepeatedDialog;

	/**
	 * The constructor.
	 */
	public CstThemenblockEditor() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@SuppressWarnings("unchecked")
	public void createPartControl(Composite parent) {

		Canvas baseCanvas = new Canvas(parent, SWT.VERTICAL);
		baseCanvas.setBackground(new Color(Display.getDefault(), 239, 239, 239));

		FillLayout baseGridLayout = new FillLayout();
		// GridLayout baseGridLayout = new GridLayout();
		baseGridLayout.type = SWT.VERTICAL;

		baseCanvas.setLayout(baseGridLayout);

		SashForm form = new SashForm(baseCanvas, SWT.HORIZONTAL);
		// form.setLayout(new FillLayout());
		// form.setSashWidth(5);
		form.setLayout(new GridLayout());
		GridData gdForm = new GridData();
		gdForm.heightHint = 400;
		form.setLayoutData(gdForm);

		// Composite Left Side
		Composite child1 = new Composite(form, SWT.NONE);
		GridLayout gridLayoutLeft = new GridLayout();
		gridLayoutLeft.numColumns = 1;
		child1.setLayout(gridLayoutLeft);

		// Composite Right Side
		Composite child2 = new Composite(form, SWT.NONE);
		GridLayout gridLayoutRight = new GridLayout();
		gridLayoutRight.numColumns = 1;
		child2.setLayout(gridLayoutRight);

		form.setWeights(new int[] { 20, 40 });

		// Label and Table Left Side
		labelLeft = new Label(child1, SWT.BORDER | SWT.CENTER);
		labelLeft.setText("CST Group");

		labelLeft.setSize(100, 20);
		labelLeft.setFont(createBoldFont(labelLeft.getFont()));
		labelLeft.setForeground(UiDesk.getColor(UiDesk.COL_BLUE));
		labelLeft.setBackground(new Color(Display.getDefault(), 251, 247, 247));

		GridData gridDataLabelLeft = new GridData();
		gridDataLabelLeft.horizontalAlignment = GridData.FILL;
		gridDataLabelLeft.grabExcessHorizontalSpace = true;
		labelLeft.setLayoutData(gridDataLabelLeft);

		tableCstGroup = new Table(child1, SWT.CHECK | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData gridDataTableLeft = new GridData();
		gridDataTableLeft.horizontalAlignment = GridData.FILL;
		gridDataTableLeft.verticalAlignment = GridData.FILL;
		gridDataTableLeft.grabExcessHorizontalSpace = true;
		gridDataTableLeft.grabExcessVerticalSpace = true;
		tableCstGroup.setLayoutData(gridDataTableLeft);

		// Label and Table Right Side
		Label labelRight = new Label(child2, SWT.BORDER | SWT.CENTER);
		labelRight.setText("Labor Items");
		labelRight.setSize(100, 20);
		labelRight.setFont(createBoldFont(labelRight.getFont()));
		labelRight.setBackground(new Color(Display.getDefault(), 251, 247, 247));

		GridData gridDataLabelRight = new GridData();
		gridDataLabelRight.horizontalAlignment = GridData.FILL;
		gridDataLabelRight.grabExcessHorizontalSpace = true;
		labelRight.setLayoutData(gridDataLabelRight);

		tableLabItem = new Table(child2, SWT.CHECK | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData gridDataTableRight = new GridData();
		gridDataTableRight.horizontalAlignment = GridData.FILL;
		gridDataTableRight.verticalAlignment = GridData.FILL;
		gridDataTableRight.grabExcessHorizontalSpace = true;
		gridDataTableRight.grabExcessVerticalSpace = true;
		tableLabItem.setLayoutData(gridDataTableRight);

		cstGroups = new ArrayList<CstGroup>();

		String[] colLabels = getCategoryColumnLabels();
		int columnWidth[] = getColumnWidth();
		CategorySortListener categorySortListener = new CategorySortListener();
		TableColumn[] cols = new TableColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TableColumn(tableCstGroup, SWT.NONE);
			cols[i].setWidth(columnWidth[i]);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
			cols[i].addSelectionListener(categorySortListener);
		}
		tableCstGroup.setHeaderVisible(true);
		tableCstGroup.setLinesVisible(true);

		String[] colLabels2 = getLabItemsColumnLabels();
		int columnWidth2[] = getColumnWidthLabItem();

		LabItemSortListener labItemSortListener = new LabItemSortListener();
		TableColumn[] cols2 = new TableColumn[colLabels2.length];
		for (int i = 0; i < colLabels2.length; i++) {
			cols2[i] = new TableColumn(tableLabItem, SWT.NONE);
			cols2[i].setWidth(columnWidth2[i]);
			cols2[i].setText(colLabels2[i]);
			cols2[i].setData(new Integer(i));
			cols2[i].addSelectionListener(labItemSortListener);
		}
		tableLabItem.setHeaderVisible(true);
		tableLabItem.setLinesVisible(true);

		createBoldFont(tableCstGroup.getFont());
		myColorRed = createRedColor(tableCstGroup.getFont());

		tableViewerCstGroup = new CheckboxTableViewer(tableCstGroup);
		tableViewerCstGroup.setContentProvider(new CategoryContentProvider());
		tableViewerCstGroup.setLabelProvider(new CategoryLabelProvider());
		tableViewerCstGroup.setSorter(new CategorySorter());

		tableViewerCstGroup.setInput(getViewSite());
		if (tableCstGroup.getItems().length > 0) {
			tableCstGroup.select(0);
		}

		tableViewerLabItem = new CheckboxTableViewer(tableLabItem);
		tableViewerLabItem.setContentProvider(new LabItemContentProvider());
		tableViewerLabItem.setLabelProvider(new LabItemLabelProvider());
		tableViewerLabItem.setSorter(new LabItemSorter());
		tableViewerLabItem.setInput(getViewSite());

		tableViewerCstGroup.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerCstGroup.getSelection();

				// on selecting a new Category, fetch its LabItems
				CstGroup selGroup = (CstGroup) selection.getFirstElement();
				if (selGroup != null) {
					itemRanking = selGroup.getMap(CstGroup.ITEMRANKING);
					labItems = selGroup.getLabitems();

					// if null, initialize the ranking with the current sequence
					if (itemRanking == null || itemRanking.size() == 0) {
						Hashtable<Object, Object> ranking = new Hashtable<Object, Object>();
						int i = 1;
						for (LabItemWrapper item : labItems) {
							ranking.put(item.getLabItem().getId(), i++);
						}
						itemRanking = (Map<Object, Object>) ranking.clone();
						selGroup.setMap(CstGroup.ITEMRANKING, ranking);
					}

					tableViewerLabItem.refresh();
				}
			}
		});

		Composite abstractCanvas = new Composite(baseCanvas, SWT.HORIZONTAL);
		GridLayout abstractGridLayout = new GridLayout(1, true);
		abstractCanvas.setLayout(abstractGridLayout);
		GridData gdAbstract = new GridData();
		gdAbstract.horizontalAlignment = SWT.FILL;
		abstractCanvas.setLayoutData(gdAbstract);

		Composite movebuttonCompo = new Composite(abstractCanvas, SWT.NONE);
		GridLayout movebuttonGridLayout = new GridLayout(3, true);
		movebuttonCompo.setLayout(movebuttonGridLayout);
		// movebuttonCanvas.setSize(400, 20);
		GridData gdButtons = new GridData();
		gdButtons.horizontalAlignment = SWT.CENTER;
		gdButtons.heightHint = 80;
		// gdButtons.widthHint = 300;
		gdButtons.verticalAlignment = GridData.FILL;
		movebuttonCompo.setLayoutData(gdButtons);
		gdButtons.minimumHeight = 80;

		Image imgArrowUp = UiDesk.getImage(Activator.IMG_ARROW_UP_NAME);
		Image imgArrowDown = UiDesk.getImage(Activator.IMG_ARROW_DOWN_NAME);

		Button btnArrowUp = new Button(movebuttonCompo, SWT.BORDER);
		Button btnArrowDown = new Button(movebuttonCompo, SWT.BORDER);

		btnArrowUp.setImage(imgArrowUp);
		btnArrowDown.setImage(imgArrowDown);

		GridData gdArrowUp = new GridData(GridData.END);
		GridData gdArrowDown = new GridData(GridData.END);

		btnArrowUp.setLayoutData(gdArrowUp);
		btnArrowDown.setLayoutData(gdArrowDown);

		btnArrowDown.setText(Messages.Button_MoveDown);
		btnArrowUp.setText(Messages.Button_MoveUp);

		btnArrowUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveItemUp();
				;
			}
		});

		btnArrowDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveItemDown();
				;
			}
		});

		txtAbstract = new Text(abstractCanvas, SWT.MULTI | SWT.BORDER | SWT.HORIZONTAL);
		GridData gridDataText = new GridData(GridData.FILL_BOTH);
		gridDataText.verticalAlignment = SWT.FILL;
		gridDataText.grabExcessVerticalSpace = true;
		gridDataText.grabExcessHorizontalSpace = true;
		gridDataText.heightHint = 200;
		// gridDataText.widthHint = 600;

		txtAbstract.setLayoutData(gridDataText);

		GridData gdBtnSaveAbst = new GridData();
		gdBtnSaveAbst.verticalAlignment = SWT.BOTTOM;
		gdBtnSaveAbst.horizontalAlignment = SWT.LEFT;
		// gdBtnSaveAbst.grabExcessHorizontalSpace = true;
		// gdBtnSaveAbst.grabExcessVerticalSpace = true;
		Button btnSaveAbstract = new Button(abstractCanvas, SWT.BORDER);
		btnSaveAbstract.setLayoutData(gdBtnSaveAbst);

		btnSaveAbstract.setText("Save Abstract for Lab item");
		btnSaveAbstract.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					saveAbstract();
					break;
				}
			}
		});

		final Label labelAbstract = new Label(abstractCanvas, SWT.NONE);
		final String sLabelAbstract = " Zeichen (max. ca. 500 Z.)";
		labelAbstract.setText("0 " + sLabelAbstract);
		GridData gdLabelAbstract = new GridData();
		gdLabelAbstract.verticalAlignment = SWT.BOTTOM;
		gdLabelAbstract.horizontalAlignment = SWT.LEFT;
		labelAbstract.setLayoutData(gdLabelAbstract);

		txtAbstract.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				labelAbstract.setText(String.valueOf(txtAbstract.getText().length()) + sLabelAbstract);

			}
		});

		tableViewerLabItem.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerLabItem.getSelection();
				// on selecting a new Category, fetch its LabItems
				LabItemWrapper selItem = (LabItemWrapper) selection.getFirstElement();
				if (selItem != null) {
					// labItems = selItem.getLabitems();
					CstAbstract abst = CstAbstract.getByLaboritemId(selItem.getLabItem().getId());

					String text;
					if (abst == null) {
						text = "default";
					} else {
						text = abst.getDescription1();
						log.debug("desc: " + abst.getDescription1());
					}
					txtAbstract.setText(text);
				}
			}
		});

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(tableViewerCstGroup.getControl(), "ch.gpb.elexis.cst.viewer");
		makeActions();
		hookContextMenuCategory();
		hookContextMenuLabItem();
		hookDoubleClickAction();
		contributeToActionBars();
		GlobalEventDispatcher.addActivationListener(this, this);

	}

	/**
	 * save the abstract connected with this lab item
	 */
	private void saveAbstract() {
		TableItem[] selItemC = tableLabItem.getSelection();
		LabItemWrapper selGroup = (LabItemWrapper) selItemC[0].getData();

		CstAbstract abst = CstAbstract.getByLaboritemId(selGroup.getLabItem().getId());
		if (abst == null) {
			abst = new CstAbstract(selGroup.getLabItem().getId(), txtAbstract.getText(), "description2");

		} else {
			abst.setDescription1(txtAbstract.getText());
		}
	}

	/**
	 * reload the objects for the content provider
	 */
	private void loadGroups() {
		cstGroups = CstGroup.getCstGroups();
		tableViewerCstGroup.refresh();
		if (tableViewerCstGroup != null) {
			tableViewerCstGroup.refresh();
			tableCstGroup.setFocus();
		}
		if (tableViewerLabItem != null) {
			tableViewerLabItem.refresh();
		}
	}

	private void moveItemUp() {
		IStructuredSelection selection2 = (IStructuredSelection) tableViewerCstGroup.getSelection();
		CstGroup selGroup = (CstGroup) selection2.getFirstElement();

		try {
			IStructuredSelection selection = (IStructuredSelection) tableViewerLabItem.getSelection();
			LabItemWrapper selItem = (LabItemWrapper) selection.getFirstElement();
			if (selItem == null) {
				return;
			}
			int selIndex = tableViewerLabItem.getTable().getSelectionIndex();

			if (selIndex < 1) {
				return;
			}

			TableItem tableItem = tableViewerLabItem.getTable().getItem(selIndex - 1);
			LabItemWrapper aboveItem = (LabItemWrapper) tableItem.getData();

			int rank1 = (int) itemRanking.get(selItem.getLabItem().getId());
			int rank2 = (int) itemRanking.get(aboveItem.getLabItem().getId());
			itemRanking.put(selItem.getLabItem().getId(), rank1 - 1);
			itemRanking.put(aboveItem.getLabItem().getId(), rank2 + 1);

			selGroup.setMap(CstGroup.ITEMRANKING, itemRanking);

			tableViewerLabItem.refresh();
		} catch (Exception e) {
			showMessage("Fehler: die Reihenfolge der Laboritems muss neu initialisiert werden.");
			reinitRanking(selGroup);
		}
	}

	private void moveItemDown() {
		IStructuredSelection selection2 = (IStructuredSelection) tableViewerCstGroup.getSelection();
		CstGroup selGroup = (CstGroup) selection2.getFirstElement();

		try {
			IStructuredSelection selection = (IStructuredSelection) tableViewerLabItem.getSelection();
			LabItemWrapper selItem = (LabItemWrapper) selection.getFirstElement();
			if (selItem == null) {
				return;
			}
			int selIndex = tableViewerLabItem.getTable().getSelectionIndex();
			if (selIndex + 1 >= tableViewerLabItem.getTable().getItemCount()) {
				return;
			}

			TableItem tableItem = tableViewerLabItem.getTable().getItem(selIndex + 1);
			LabItemWrapper belowItem = (LabItemWrapper) tableItem.getData();

			int rank1 = (int) itemRanking.get(selItem.getLabItem().getId());
			int rank2 = (int) itemRanking.get(belowItem.getLabItem().getId());
			itemRanking.put(selItem.getLabItem().getId(), rank1 + 1);
			itemRanking.put(belowItem.getLabItem().getId(), rank2 - 1);

			selGroup.setMap(CstGroup.ITEMRANKING, itemRanking);

			tableViewerLabItem.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			showMessage("Fehler: die Reihenfolge der Laboritems muss neu initialisiert werden.");
			reinitRanking(selGroup);
		}

	}

	private void selectFirstRow() {
		if (tableViewerCstGroup != null) {
			Object obj = tableViewerCstGroup.getElementAt(0);
			if (!cstGroups.isEmpty() && obj != null) {
				tableViewerCstGroup.setSelection(new StructuredSelection(tableViewerCstGroup.getElementAt(0)), true);
			}
		}
	}

	private void selectRow(int row) {
		if (tableViewerCstGroup != null) {
			Object obj = tableViewerCstGroup.getElementAt(row);
			if (!cstGroups.isEmpty() && obj != null) {
				tableViewerCstGroup.setSelection(new StructuredSelection(tableViewerCstGroup.getElementAt(row)), true);
			}
		}
	}

	@Override
	public void dispose() {
		GlobalEventDispatcher.removeActivationListener(this, this);
	}

	private Font createBoldFont(Font baseFont) {
		FontData fd = baseFont.getFontData()[0];
		Font font = new Font(baseFont.getDevice(), fd.getName(), 10, fd.getStyle() | SWT.BOLD);

		return font;
	}

	private Color createRedColor(Font baseFont) {
		myColorRed = new Color(baseFont.getDevice(), 255, 0, 0);
		return myColorRed;
	}

	private String[] getCategoryColumnLabels() {
		String columnLabels[] = { Messages.CstCategory_name, Messages.CstCategory_description };

		return columnLabels;
	}

	private String[] getLabItemsColumnLabels() {

		String columnLabels[] = { Messages.CstLaborPrefs_name, Messages.CstLaborPrefs_short,
				Messages.CstProfile_Ranking, Messages.CstLaborPrefs_refM, Messages.CstLaborPrefs_refF,
				"Immer anzeigen" };

		return columnLabels;
	}

	private int[] getColumnWidth() {
		int columnWidth[] = { 120, 250 };
		return columnWidth;
	}

	private int[] getColumnWidthLabItem() {
		int columnWidth[] = { 200, 200, 40, 120, 120, 50 };
		return columnWidth;
	}

	private void hookContextMenuCategory() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CstThemenblockEditor.this.fillContextMenuCategory(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewerCstGroup.getControl());
		tableViewerCstGroup.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tableViewerCstGroup);
	}

	private void hookContextMenuLabItem() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CstThemenblockEditor.this.fillContextMenuLabItem(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewerLabItem.getControl());
		tableViewerLabItem.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tableViewerLabItem);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionCreateCstGroup);
		manager.add(new Separator());
		manager.add(actionDeleteCstGroup);
	}

	private void fillContextMenuCategory(IMenuManager manager) {
		manager.add(actionCreateCstGroup);
		manager.add(actionDeleteCstGroup);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillContextMenuLabItem(IMenuManager manager) {
		manager.add(actionRemoveLabItem);
		manager.add(actionAddLabItems);
		manager.add(actionDisplayOnce);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionCreateCstGroup);
		manager.add(actionDeleteCstGroup);
	}

	private void makeActions() {

		actionCreateCstGroup = new Action() {
			public void run() {
				CstCategoryDialog dialog = new CstCategoryDialog(tableViewerCstGroup.getControl().getShell());
				dialog.create();
				if (isRepeatedDialog) {
					dialog.setErrorMessage(Messages.Cst_Text_cstgroup_exists);
				}
				// flag f�r den rekursiven Dialog Aufruf
				isRepeatedDialog = false;
				if (dialog.open() == Window.OK) {
					if (dialog.getGroupName().length() < 1) {
						return;
					}
				} else {
					return;
				}

				try {
					Mandant m = CoreHub.actMandant;
					if (m != null) {
						CstGroup mapping = new CstGroup(dialog.getGroupName(), dialog.getGroupDescription(), null,
								m.getId());

						log.info("New CstGroup with id: " + mapping.getId());
						loadGroups();

						// todo: select newly created Item
						TableItem[] items = tableCstGroup.getItems();
						for (int i = 0; i < items.length; i++) {
							TableItem item = items[i];
							CstGroup g = (CstGroup) item.getData();
							if (g.getId().equals(mapping.getId())) {
								selectRow(i);
								break;
							}
						}
						tableViewerCstGroup.refresh(true);
						tableCstGroup.setFocus();

					} else {
						log.info("error: no mandant available", Log.INFOS);
					}
				} catch (Exception e) {
					log.info("CST Category already exists: " + e.getMessage(), Log.INFOS);

					isRepeatedDialog = true;
					actionCreateCstGroup.run();

				}
			}
		};
		actionCreateCstGroup.setText(Messages.Cst_Text_create_cstgroup);
		actionCreateCstGroup.setToolTipText(Messages.Cst_Text_create_cstgroup_tooltip);
		actionCreateCstGroup.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		// TODO: deleting a group should also clean up the profiles that have utilized
		// this group!!
		actionDeleteCstGroup = new Action() {
			public void run() {
				TableItem[] selItem = tableCstGroup.getSelection();
				if (selItem.length == 0) {
					return;
				}

				CstGroup selGroup = (CstGroup) selItem[0].getData();

				String sMsg = String.format(Messages.Cst_Text_confirm_delete_group, selGroup.getName());

				showMessage(sMsg);

				selGroup.delete();
				loadGroups();

				tableViewerCstGroup.refresh();
				selectFirstRow();
				tableCstGroup.setFocus();

			}
		};
		actionDeleteCstGroup.setText(Messages.Cst_Text_delete_cstgroup);
		actionDeleteCstGroup.setToolTipText(Messages.Cst_Text_delete_cstgroup_tooltip);
		actionDeleteCstGroup.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));

		actionRemoveLabItem = new Action() {
			public void run() {
				TableItem[] selItemC = tableCstGroup.getSelection();
				CstGroup selGroup = (CstGroup) selItemC[0].getData();

				TableItem[] selItem = tableLabItem.getSelection();
				if (selItem.length == 0) {
					return;
				}
				LabItemWrapper labItem = (LabItemWrapper) selItem[0].getData();
				log.debug("LabItem ID:" + labItem.getLabItem().getId());

				selGroup.removeLabitem(labItem.getLabItem());

				loadGroups();

				reinitRanking(selGroup);

				tableViewerCstGroup.refresh();
				tableViewerLabItem.refresh();
				tableCstGroup.setFocus();

			}
		};
		actionRemoveLabItem.setText(Messages.Cst_Text_delete_from_cstgroup);
		actionRemoveLabItem.setToolTipText(Messages.Cst_Text_delete_from_cstgroup_tooltip);
		actionRemoveLabItem.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));

		actionDisplayOnce = new Action() {
			public void run() {
				TableItem[] selItemC = tableCstGroup.getSelection();
				CstGroup selGroup = (CstGroup) selItemC[0].getData();

				TableItem[] selItem = tableLabItem.getSelection();
				if (selItem.length == 0) {
					return;
				}
				LabItemWrapper labItem = (LabItemWrapper) selItem[0].getData();
				log.debug("LabItem ID:" + labItem.getLabItem().getId());

				int ret = selGroup.setDisplayOnce(labItem, labItem.getDisplayOnce().equals("1") ? "0" : "1");

				loadGroups();

				// tableViewerCstGroup.refresh();
				labItems = selGroup.getLabitems();

				tableViewerLabItem.refresh();
				tableCstGroup.setFocus();

			}
		};
		actionDisplayOnce.setText("Immer anzeigen");
		actionDisplayOnce.setToolTipText(Messages.Cst_Text_delete_from_cstgroup_tooltip);
		actionDisplayOnce.setImageDescriptor(Activator.getImageDescriptor(Activator.IMG_DISPLAYONCE_PATH));

		actionAddLabItems = new Action() {
			public void run() {
				List<LabItemWrapper> itemsToAdd;
				if (dialogLabItems == null || dialogLabItems.size() == 0) {
					dialogLabItems = LabItem.getLabItems();
				}

				CstLabItemSelectionDialog dialog = new CstLabItemSelectionDialog(
						tableViewerLabItem.getControl().getShell(), dialogLabItems);

				dialog.create();

				if (dialog.open() == Window.OK) {
					itemsToAdd = LabItemWrapper.wrap(dialog.getSelItems());
				} else {
					return;
				}

				TableItem[] selItemC = tableCstGroup.getSelection();
				if (selItemC == null || selItemC.length < 1) {
					return;
				}

				CstGroup selGroup = (CstGroup) selItemC[0].getData();
				if (selGroup == null) {
					return;
				}

				try {
					selGroup.addItems(itemsToAdd);
				} catch (Exception e) {
					showMessage("The Lab Item already exists in this CSTGroup");
				}

				reinitRanking(selGroup);
				tableViewerLabItem.refresh();

				loadGroups();
				tableViewerCstGroup.refresh();
				tableViewerLabItem.refresh();
				tableCstGroup.setFocus();
			}
		};
		actionAddLabItems.setText(Messages.Cst_Text_add_to_cstgroup);
		actionAddLabItems.setToolTipText(Messages.Cst_Text_add_to_cstgroup_tooltip);
		actionAddLabItems.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

	}

	/**
	 * TODO: there are labitems with identical names!! (see Glucose) and the ranking
	 * is based on a hash with that name as key...
	 *
	 * @param selGroup
	 */
	private void reinitRanking(CstGroup selGroup) {
		labItems = selGroup.getLabitems();
		Hashtable<Object, Object> ranking = new Hashtable<Object, Object>();

		int i = 1;
		for (LabItemWrapper item : labItems) {
			ranking.put(item.getLabItem().getId(), i++);
		}

		itemRanking = (Map) ranking.clone();
		selGroup.setMap(CstGroup.ITEMRANKING, ranking);
		log.debug("reinitialize the ranking");

		// tableViewerLabItem.refresh();
	}

	private void hookDoubleClickAction() {
		tableViewerCstGroup.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// add a new LabItem to the Category
				TableItem[] selItem = tableCstGroup.getSelection();
				selItem[0].getData();

				ISelection selection = tableViewerCstGroup.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				CstGroup profile = (CstGroup) obj;

				ThemenblockDetailDialog dialog = new ThemenblockDetailDialog(
						tableViewerCstGroup.getControl().getShell());
				dialog.create();
				dialog.setName(profile.getName());
				dialog.setDescription(profile.getDescription());

				if (dialog.open() == Window.OK) {
					profile.setName(dialog.getName());
					profile.setDescription(dialog.getDescription());

					loadGroups();
					tableViewerCstGroup.setSelection(selection);

				}
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(tableViewerCstGroup.getControl().getShell(), "CST View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tableViewerCstGroup.getControl().setFocus();
		loadGroups();

	}

	/*
	 * The content provider class is responsible for providing objects to the view.
	 * It can wrap existing objects in adapters or simply return objects as-is.
	 * These objects may be sensitive to the current input of the view, or ignore it
	 * and always show the same content (like Task List, for example).
	 */

	class CategoryContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return cstGroups.toArray();
		}
	}

	class CategoryLabelProvider extends LabelProvider
			implements ITableLabelProvider, ITableFontProvider, IColorProvider {
		public String getColumnText(Object obj, int index) {
			CstGroup cstGroup = (CstGroup) obj;
			switch (index) {
			case 0:
				return cstGroup.getName();
			case 1:
				return cstGroup.getDescription();
			default:
				return "?";
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

		public Font getFont(Object element, int columnIndex) {
			Font font = null;
			if (element instanceof LabItemWrapper) {
			}
			return font;
		}

		@Override
		public Color getForeground(Object element) {
			if (element instanceof CstGroup) {
				CstGroup cstGroup = (CstGroup) element;

				TableItem[] items = tableCstGroup.getItems();
				for (int x = 0; x < items.length; x++) {
					TableItem item = items[x];
					item.setChecked(true);
				}

				if (cstGroup != null) {
					/*
					 * if (cstGroup.getName() != null) { if (cstGroup.getName().startsWith("T")) {
					 * return myColorRed; } }
					 */

				}
			}

			return null;
		}

		@Override
		public Color getBackground(Object element) {
			return null;
		}
	}

	class CategorySortListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			TableColumn col = (TableColumn) e.getSource();

			Integer colNo = (Integer) col.getData();

			if (colNo != null) {
				if (colNo == sortColumn) {
					sortReverse = !sortReverse;
				} else {
					sortReverse = false;
					sortColumn = colNo;
				}
				tableViewerCstGroup.refresh();
			}
		}

	}

	class CategorySorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof CstGroup) && (e2 instanceof CstGroup)) {
				CstGroup d1 = (CstGroup) e1;
				CstGroup d2 = (CstGroup) e2;
				String c1 = StringUtils.EMPTY;
				String c2 = StringUtils.EMPTY;
				switch (sortColumn) {
				case 0:
					c1 = d1.getName();
					c2 = d2.getName();
					break;
				case 1:
					c1 = d1.getDescription();
					c2 = d2.getDescription();
					break;
				}
				if (sortReverse) {
					return c1.compareTo(c2);
				} else {
					return c2.compareTo(c1);
				}
			}
			return 0;
		}

	}

	class LabItemContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return labItems.toArray();
		}

	}

	class LabItemLabelProvider extends LabelProvider
			implements ITableLabelProvider, ITableFontProvider, IColorProvider {
		public String getColumnText(Object obj, int index) {
			LabItemWrapper labItem = (LabItemWrapper) obj;
			switch (index) {
			case 0:
				return labItem.getLabItem().getName();
			case 1:
				return labItem.getLabItem().getKuerzel();
			case 2:
				if (itemRanking.get(labItem.getLabItem().getId()) == null) {
					IStructuredSelection selection = (IStructuredSelection) tableViewerCstGroup.getSelection();
					Object o = ((IStructuredSelection) selection).getFirstElement();
					CstGroup profile = (CstGroup) o;
					// showMessage("Error with Ranking. Reinitializing...");
					reinitRanking(profile);

				}
				return String.valueOf(itemRanking.get(labItem.getLabItem().getId()));
			case 3:
				return labItem.getLabItem().getRefM();
			case 4:
				return labItem.getLabItem().getRefW();
			case 5:
				// return labItem.getDisplayOnce();
				if (labItem.getDisplayOnce() == null) {
					return "null";
				}

				return labItem.getDisplayOnce().equals("1") ? "Ja" : "Nein";
			default:
				return "?";
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

		public Font getFont(Object element, int columnIndex) {
			Font font = null;
			if (element instanceof LabItemWrapper) {
			}
			return font;
		}

		@Override
		public Color getForeground(Object element) {
			/*
			 * if (element instanceof LabItemWrapper) { LabItemWrapper labItem =
			 * (LabItemWrapper) element;
			 *
			 * if (labItem.getName().startsWith("P")) { return myColorRed; }
			 *
			 *
			 * }
			 */

			return null;
		}

		@Override
		public Color getBackground(Object element) {
			return null;
		}
	}

	class LabItemSortListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			TableColumn col = (TableColumn) e.getSource();

			Integer colNo = (Integer) col.getData();

			if (colNo != null) {
				if (colNo == sortColumn) {
					sortReverse = !sortReverse;
				} else {
					sortReverse = false;
					sortColumn = colNo;
				}
				tableViewerLabItem.refresh();
			}
		}

	}

	class LabItemSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof LabItemWrapper) && (e2 instanceof LabItemWrapper)) {
				LabItemWrapper d1 = (LabItemWrapper) e1;
				LabItemWrapper d2 = (LabItemWrapper) e2;

				Integer r1 = (Integer) itemRanking.get(d1.getLabItem().getId());
				Integer r2 = (Integer) itemRanking.get(d2.getLabItem().getId());
				if (r1 == null || r2 == null) {
					return 0;
				}

				return r1.compareTo(r2);

			}
			return 0;
		}

	}

	@Override
	public void activation(boolean mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visible(boolean mode) {
	}

}
