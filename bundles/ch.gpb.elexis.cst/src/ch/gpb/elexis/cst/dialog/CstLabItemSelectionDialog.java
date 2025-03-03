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
package ch.gpb.elexis.cst.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import ch.elexis.data.LabItem;
import ch.gpb.elexis.cst.Messages;

/**
 * @author daniel ludin ludin@swissonline.ch 27.06.2015
 *
 */

public class CstLabItemSelectionDialog extends SelectionDialog {
	private Table table;

	private String firstName;
	private String lastName;
	private CheckboxTableViewer tableViewer;
	private List<LabItem> labItems;

	public List<LabItem> getSelItems() {
		return selItems;
	}

	public void setSelItems(List<LabItem> selItems) {
		this.selItems = selItems;
	}

	private List<LabItem> selItems;
	private int sortColumn = 0;
	private boolean sortReverse = false;

	public CstLabItemSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	public CstLabItemSelectionDialog(Shell parentShell, List<LabItem> labItems) {
		super(parentShell);
		this.labItems = labItems;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.Cst_Text_Laboritemauswahl);
		setMessage(Messages.Cst_Text_Laboritemauswahl_label);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);

		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		/*
		 *
		 * createFirstName(container); createLastName(container);
		 */
		table = new Table(container, SWT.CHECK | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		String[] colLabels = getColumnLabels();
		int columnWidth[] = getColumnWidth();
		SortListener sortListener = new SortListener();
		TableColumn[] cols = new TableColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TableColumn(table, SWT.NONE);
			cols[i].setWidth(columnWidth[i]);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
			cols[i].addSelectionListener(sortListener);
		}
		GridData gridDataTable = new GridData();
		gridDataTable.horizontalAlignment = GridData.FILL;
		gridDataTable.verticalAlignment = GridData.FILL;
		gridDataTable.grabExcessHorizontalSpace = true;
		gridDataTable.grabExcessVerticalSpace = true;
		table.setLayoutData(gridDataTable);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer = new CheckboxTableViewer(table);
		tableViewer.setContentProvider(new ViewContentProvider());
		tableViewer.setLabelProvider(new ViewLabelProvider());
		tableViewer.setSorter(new Sorter());

		tableViewer.setInput(this);

		return area;
	}

	private String[] getColumnLabels() {
		String columnLabels[] = { Messages.CstLaborPrefs_name, Messages.CstLaborPrefs_short,
				Messages.CstLaborPrefs_unit, Messages.CstLaborPrefs_type, Messages.CstLaborPrefs_sortmode, "Ref M",
				"Ref F" };
		return columnLabels;
	}

	private int[] getColumnWidth() {
		int columnWidth[] = { 120, 150, 200, 200, 100, 100, 100 };
		return columnWidth;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		selItems = new ArrayList<LabItem>();
		Object[] checkedItems = tableViewer.getCheckedElements();
		for (Object object : checkedItems) {
			LabItem labItem = (LabItem) object;
			selItems.add(labItem);
		}
		super.okPressed();
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return labItems.toArray();
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, IColorProvider {
		public String getColumnText(Object obj, int index) {
			LabItem labItem = (LabItem) obj;
			switch (index) {
			case 0:
				return labItem.getName();
			case 1:
				return labItem.getKuerzel();
			case 2:
				return labItem.getEinheit();
			case 3:
				return labItem.getPrio();
			case 4:
				return labItem.getGroup();
			case 5:
				return labItem.getRefM();
			case 6:
				return labItem.getRefW();
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
			return font;
		}

		@Override
		public Color getForeground(Object element) {
			return null;
		}

		@Override
		public Color getBackground(Object element) {
			return null;
		}
	}

	class SortListener extends SelectionAdapter {

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
				tableViewer.refresh();
			}

		}

	}

	class Sorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof LabItem) && (e2 instanceof LabItem)) {
				LabItem d1 = (LabItem) e1;
				LabItem d2 = (LabItem) e2;
				String c1 = StringUtils.EMPTY;
				String c2 = StringUtils.EMPTY;
				switch (sortColumn) {
				case 0:
					c1 = d1.getName();
					c2 = d2.getName();
					break;
				case 1:
					c1 = d1.getKuerzel();
					c2 = d2.getKuerzel();
					break;
				case 2:
					c1 = d1.getEinheit();
					c2 = d2.getEinheit();
					break;
				case 3:
					c1 = d1.getPrio();
					c2 = d2.getPrio();
					break;
				case 4:
					c1 = d1.getGroup();
					c2 = d2.getGroup();
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

}