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

import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.gpb.elexis.cst.Messages;

/**
 * @author daniel ludin ludin@swissonline.ch 27.06.2015
 *
 */

public class CstCopyProfileDialog extends SelectionDialog {
	private Table table;
	private String firstName;
	private String lastName;
	private CheckboxTableViewer tableViewer;
	Query<Patient> qbe;
	Object[] pats;
	List<Patient> lPats;
	private List<Patient> selItems;
	private int sortColumn = 0;
	private boolean sortReverse = false;

	public CstCopyProfileDialog(Shell parentShell) {
		super(parentShell);

		qbe = new Query<Patient>(Patient.class);
		lPats = qbe.execute();
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.Cst_Text_Patientenauswahl);
		setMessage(Messages.Cst_Text_Patientenauswahl_kopieren);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);

		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

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
		tableViewer.setSorter(new PatientSorter());

		tableViewer.setInput(this);

		return area;
	}

	private String[] getColumnLabels() {
		String columnLabels[] = { Messages.CstLaborPrefs_id, Messages.CstLaborPrefs_name,
				Messages.CstLaborPrefs_firstname };
		return columnLabels;
	}

	private int[] getColumnWidth() {
		int columnWidth[] = { 120, 100, 100 };
		return columnWidth;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		selItems = new ArrayList<Patient>();
		Object[] checkedItems = tableViewer.getCheckedElements();

		for (Object object : checkedItems) {
			Patient labItem = (Patient) object;
			selItems.add(labItem);
		}
		// saveInput();
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

			if (lPats == null) {
				lPats = qbe.execute();
			}
			return lPats.toArray(new Patient[0]);
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, IColorProvider {
		public String getColumnText(Object obj, int index) {
			Patient labItem = (Patient) obj;
			switch (index) {
			case 0:
				return labItem.getPatCode();
			case 1:
				return labItem.getName();
			case 2:
				return labItem.getVorname();
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

	class PatientSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof Patient) && (e2 instanceof Patient)) {
				Patient d1 = (Patient) e1;
				Patient d2 = (Patient) e2;
				String c1 = StringUtils.EMPTY;
				String c2 = StringUtils.EMPTY;
				switch (sortColumn) {
				case 0:
					c1 = d1.getId();
					c2 = d2.getId();
					break;
				case 1:
					c1 = d1.getName();
					c2 = d2.getName();
					break;
				case 2:
					c1 = d1.getVorname();
					c2 = d2.getVorname();
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

	public List<Patient> getSelItems() {
		return selItems;
	}

	public void setSelItems(List<Patient> selItems) {
		this.selItems = selItems;
	}

}