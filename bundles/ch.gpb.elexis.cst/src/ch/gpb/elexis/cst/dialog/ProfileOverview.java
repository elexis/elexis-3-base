package ch.gpb.elexis.cst.dialog;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Kontakt;
import ch.gpb.elexis.cst.data.CstProfile;

public class ProfileOverview extends StatusDialog {
	// List list;
	private Table table;
	java.util.List<CstProfile> profiles;
	private int sortColumn = 0;
	private boolean sortReverse = false;
	TableViewer tableViewer;
	private java.util.List<String[]> lProf = new ArrayList<String[]>();

	// TODO: text localisations missing
	public ProfileOverview(Shell parent) {
		super(parent);
		setShellStyle(SWT.BORDER | SWT.RESIZE);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (id == IDialogConstants.CANCEL_ID)
			return null;
		return super.createButton(parent, id, label, defaultButton);

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		// TODO Auto-generated method stub
		Composite base = (Composite) super.createDialogArea(parent);

		Label lblNewLabel = new Label(base, SWT.NONE);
		lblNewLabel.setText("Welches Profil ist bei welchem Patienten?");

		tableViewer = new TableViewer(base, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.widthHint = 400;
		gd_table.minimumWidth = 400;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		java.util.List<CstProfile> p = CstProfile.getAllProfiles(CoreHub.actMandant.getId());

		for (CstProfile cstProfile : p) {
			Kontakt k = Kontakt.load(cstProfile.getKontaktId());

			String[] sProf = new String[4];
			sProf[0] = cstProfile.getName();
			sProf[1] = k.getLabel();
			sProf[2] = cstProfile.getTemplate();
			sProf[3] = k.getKuerzel();
			lProf.add(sProf);

		}
		profiles = CstProfile.getAllProfiles(CoreHub.actMandant.getId());
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
		tableViewer.setContentProvider(new ViewContentProvider());
		tableViewer.setLabelProvider(new ViewLabelProvider());
		tableViewer.setInput(this);
		tableViewer.setSorter(new Sorter());

		/*
		 */
		return base;
	}

	private String[] getColumnLabels() {
		String columnLabels[] = { "Profile", "Patient", "Template", "Patienten-Nr" };
		return columnLabels;
	}

	private int[] getColumnWidth() {
		int columnWidth[] = { 150, 150, 50, 100 };
		return columnWidth;
	}

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return lProf.toArray();
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, IColorProvider {
		public String getColumnText(Object obj, int index) {
			String[] tableLine = (String[]) obj;
			switch (index) {
			case 0:
				return tableLine[0];
			case 1:
				return tableLine[1];
			case 2:
				if (tableLine[2].equals("1")) {
					return "ja";
				}
				return "nein";
			case 3:
				return tableLine[3];

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
			Font fontNormal = UiDesk.getFont("Helvetica", 8, SWT.NORMAL); //$NON-NLS-1$
			return fontNormal;
		}

		@Override
		public Color getForeground(Object element) {
			return null;
		}

		@Override
		public Color getBackground(Object element) {
			String[] tableLine = (String[]) element;
			if (tableLine[2].equals("1")) {
				return UiDesk.getColorFromRGB("FF1188");
			}

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
			if ((e1 instanceof String[]) && (e2 instanceof String[])) {
				String[] d1 = (String[]) e1;
				String[] d2 = (String[]) e2;
				String c1 = StringUtils.EMPTY;
				String c2 = StringUtils.EMPTY;
				switch (sortColumn) {
				case 0:
					c1 = d1[0];
					c2 = d2[0];
					break;
				case 1:
					c1 = d1[1];
					c2 = d2[1];
					break;
				case 2:
					c1 = d1[2];
					c2 = d2[2];
					break;
				case 3:
					c1 = d1[3];
					c2 = d2[3];
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
