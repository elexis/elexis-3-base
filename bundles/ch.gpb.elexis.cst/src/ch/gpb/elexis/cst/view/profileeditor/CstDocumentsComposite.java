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
package ch.gpb.elexis.cst.view.profileeditor;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.views.TextView;
import ch.elexis.data.Brief;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.preferences.CstPreference;
import ch.gpb.elexis.cst.service.DocumentStoreHolder;
import ch.rgw.tools.ExHandler;

// TODO: the handling of the omnivore documents is done quick and dirty. There is an API to use these documents,
// see i.e. in Labor View, and that should be used here for CST Documents
//
public class CstDocumentsComposite extends CstComposite {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	CstProfile aProfile;
	private Table tableOmnivore;
	TableViewer tableViewerOmnivore;
	private Table tableBrief;
	TableViewer tableViewerBrief;
	private Action doubleClickAction;
	private Action briefLadenAction;
	IViewSite viewsite;
	private int sortColumn = 0;
	private boolean sortReverse = false;
	String sIdentBriefe;
	String sIdentOmnivore;

	public CstDocumentsComposite(Composite parent, IViewSite viewsite) {
		super(parent, SWT.NONE);
		this.viewsite = viewsite;

		sIdentBriefe = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_BRIEFE, "CST");
		sIdentOmnivore = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_OMNIVORE, "CST");

		GridLayout gridLayout = new GridLayout(1, true);
		setLayout(gridLayout);

		Label lblOmnivoreDocs = new Label(this, SWT.NONE);
		lblOmnivoreDocs.setText(Messages.Cst_Text_cst_documents_tooltip);

		// Table Omnivore Documents
		tableViewerOmnivore = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tableOmnivore = tableViewerOmnivore.getTable();
		tableOmnivore.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblBriefe = new Label(this, SWT.NONE);
		lblBriefe.setText(Messages.Cst_Text_cst_documents_tooltip_omnivore);

		tableViewerOmnivore.setContentProvider(new DocumentsContentProvider());
		tableViewerOmnivore.setLabelProvider(new DocumentsLabelProvider());

		String[] colLabels = getCategoryColumnLabels();
		int columnWidth[] = getCategoryColumnWidth();
		CstDocumentsSortListener categorySortListener = new CstDocumentsSortListener();
		TableColumn[] cols = new TableColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TableColumn(tableOmnivore, SWT.NONE);
			cols[i].setWidth(columnWidth[i]);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
			cols[i].addSelectionListener(categorySortListener);
		}
		tableOmnivore.setHeaderVisible(true);
		tableOmnivore.setLinesVisible(true);
		tableViewerOmnivore.setInput(this);
		tableViewerOmnivore.setSorter(new CstDocumentsSorter());

		tableOmnivore.setSize(600, 150);
		GridData gdCstDoc = new GridData();
		gdCstDoc.heightHint = 150;
		gdCstDoc.widthHint = 600;
		tableOmnivore.setLayoutData(gdCstDoc);

		// TAble Briefe
		tableViewerBrief = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tableBrief = tableViewerBrief.getTable();
		tableBrief.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewerBrief.setContentProvider(new BriefeContentProvider());
		tableViewerBrief.setLabelProvider(new BriefeLabelProvider());

		String[] colLabelsBrief = getCategoryColumnLabelsBrief();
		int columnWidthBrief[] = getCategoryColumnWidthBrief();
		CstBriefeSortListener briefeListener = new CstBriefeSortListener();
		TableColumn[] colsBrief = new TableColumn[colLabelsBrief.length];
		for (int i = 0; i < colLabelsBrief.length; i++) {
			colsBrief[i] = new TableColumn(tableBrief, SWT.NONE);
			colsBrief[i].setWidth(columnWidthBrief[i]);
			colsBrief[i].setText(colLabelsBrief[i]);
			colsBrief[i].setData(new Integer(i));
			colsBrief[i].addSelectionListener(briefeListener);
		}
		tableBrief.setHeaderVisible(true);
		tableBrief.setLinesVisible(true);
		tableViewerBrief.setInput(this);
		tableViewerBrief.setSorter(new CstBriefeSorter());

		tableBrief.setSize(600, 150);
		GridData gdBrief = new GridData();
		gdBrief.heightHint = 150;
		gdBrief.widthHint = 600;
		tableBrief.setLayoutData(gdBrief);

		createLayout(this);
		makeActions();
		hookDoubleClickAction();

	}

	// dynamic Layout elements
	private void createLayout(Composite parent) {

	}

	/**
	 * Get the selections from the gui
	 *
	 * @param mAuswahl the existing Befunde Auswahl map
	 * @return the updated Befunde map
	 */
	public void getSelection(Map<Object, Object> mAuswahl) {

	}

	/**
	 * Set the buttons selected according to the map passed as parameter
	 *
	 * @param mapAuswahl
	 */
	public void setSelection(Map<String, Object> mapAuswahl) {

	}

	private void hookDoubleClickAction() {
		tableViewerOmnivore.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
		tableViewerBrief.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				briefLadenAction.run();
			}
		});
	}

	private void makeActions() {

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = tableViewerOmnivore.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				IDocument dh = (IDocument) obj;

				try {
					File temp;
					temp = File.createTempFile("csf_", dh.getExtension());
					Program proggie = Program.findProgram(dh.getExtension());
					if (proggie != null) {
						proggie.execute(temp.getAbsolutePath());
					} else {
						if (Program.launch(temp.getAbsolutePath()) == false) {
							Runtime.getRuntime().exec(temp.getAbsolutePath());
						}

					}
				} catch (IOException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Fehler beim Öffnen der Datei");
				}
			}

		};

		briefLadenAction = new Action() { // $NON-NLS-1$
			@Override
			public void run() {

				ISelection selection = tableViewerBrief.getSelection();

				Object obj = ((IStructuredSelection) selection).getFirstElement();
				Brief brief = (Brief) obj;

				/*
				 * try { TextView tv = (TextView) viewsite.getPage().showView(TextView.ID);
				 *
				 * CommonViewer cv = new CommonViewer();
				 *
				 * if (brief != null) { if (tv.openDocument(brief) == false) {
				 * SWTHelper.alert("Messages.Core_Error", //$NON-NLS-1$
				 * "Messages.BriefAuswahlCouldNotLoadText"); //$NON-NLS-1$ } } else {
				 * tv.createDocument(null, null); } cv.notify(CommonViewer.Message.update);
				 *
				 * } catch (PartInitException e) { ExHandler.handle(e); }
				 */

				try {
					TextView tv = (TextView) Hub.plugin.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView(TextView.ID);

					boolean opened = tv.openDocument(Brief.load(brief.getId()));

				} catch (PartInitException e) {
					ExHandler.handle(e);
				} catch (Exception e) {
					log.error("cannot open letter: " + e.getMessage());
				}

			}
		};
	}

	public void clear() {
		tableViewerOmnivore.getTable().clearAll();
		tableViewerOmnivore.refresh();
		tableViewerBrief.refresh();
	}

	/**
	 * letters are searched for a configurable String in their concern, typically
	 * "CST"
	 *
	 * @return a list of CST related letter
	 */
	private List<Brief> loadCstBriefe() {

		Patient actPat = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		if (actPat != null) {
			sIdentBriefe = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_BRIEFE, "CST");

			Query<Brief> qbe = new Query<Brief>(Brief.class);
			qbe.add(Brief.FLD_PATIENT_ID, Query.EQUALS, actPat.getId());

			List<Brief> listResult = new ArrayList<>();
			List<Brief> list = qbe.execute();
			for (Brief brief : list) {
				// TODO: this must be configurable (Preferences)
				if (brief.getBetreff().toLowerCase().indexOf(sIdentBriefe.toLowerCase()) > -1) {
					listResult.add(brief);
				}
			}

			return listResult;
		}

		return new ArrayList<Brief>();
	}

	/**
	 * Omnivore documents are searched for a configurable category, typically "CST"
	 *
	 * @return a list of CST related Omnivore documents
	 */
	private List<IDocument> loadCstdocsOmnivore() {
		sIdentOmnivore = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_OMNIVORE, "CST");

		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat == null) {
			ArrayList<IDocument> emptyList = new ArrayList<IDocument>();
			return emptyList;
		}

		List<IDocument> ret = new LinkedList<IDocument>();

		String cat = sIdentOmnivore;

		ICategory category = DocumentStoreHolder.get().getCategories().stream().filter(c -> c.getName().equals(cat))
				.findFirst().orElse(null);
		if (category == null) {
			category = DocumentStoreHolder.get().createCategory(cat);
		}
		List<IDocument> root = DocumentStoreHolder.get().getDocuments(pat.getId(), null, category, null);

		ret.addAll(root);

		return ret;
	}

	private String[] getCategoryColumnLabels() {
		String columnLabels[] = { "Category", "Date", "Titel", "MIME Type" };
		return columnLabels;
	}

	private String[] getCategoryColumnLabelsBrief() {
		String columnLabels[] = { "Date", "Betreff", "MIME Type", "Typ" };
		return columnLabels;
	}

	class BriefeContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			List<Brief> result = loadCstBriefe();
			return result.toArray();
		}
	}

	class BriefeLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, IColorProvider {

		public String getColumnText(Object obj, int index) {
			Brief docHandle = (Brief) obj;
			switch (index) {
			case 0:
				return docHandle.getDatum();
			case 1:
				return docHandle.getBetreff();
			case 2:
				return docHandle.getTyp();
			case 3:
				return docHandle.getMimeType();
			default:
				return StringUtils.EMPTY;
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
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

	private int[] getCategoryColumnWidth() {
		int columnWidth[] = { 80, 200, 100, 100 };
		return columnWidth;
	}

	private int[] getCategoryColumnWidthBrief() {
		int columnWidth[] = { 80, 200, 100, 100 };
		return columnWidth;
	}

	class DocumentsContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			List<IDocument> result = loadCstdocsOmnivore();
			return result.toArray();
		}
	}

	class DocumentsLabelProvider extends LabelProvider
			implements ITableLabelProvider, ITableFontProvider, IColorProvider {

		public String getColumnText(Object obj, int index) {
			IDocument docHandle = (IDocument) obj;
			switch (index) {
			case 0:
				return docHandle.getCategory().getName();
			case 1:
				return dateFormat.format(docHandle.getCreated());
			case 2:
				return docHandle.getTitle();
			case 3:
				return docHandle.getMimeType();
			default:
				return StringUtils.EMPTY;
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
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

	class CstDocumentsSortListener extends SelectionAdapter {

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
				tableViewerOmnivore.refresh();
			}
		}

	}

	class CstDocumentsSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof IDocumentHandle) && (e2 instanceof IDocumentHandle)) {
				IDocumentHandle d1 = (IDocumentHandle) e1;
				IDocumentHandle d2 = (IDocumentHandle) e2;
				String c1 = StringUtils.EMPTY;
				String c2 = StringUtils.EMPTY;
				switch (sortColumn) {
				case 1:
					c1 = dateFormat.format(d1.getCreated());
					c2 = dateFormat.format(d2.getCreated());
					break;
				case 2:
					c1 = d1.getTitle();
					c2 = d2.getTitle();
					break;
				case 3:
					c1 = d1.getMimeType();
					c2 = d2.getMimeType();
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

	class CstBriefeSortListener extends SelectionAdapter {

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
				tableViewerBrief.refresh();
			}
		}

	}

	class CstBriefeSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof Brief) && (e2 instanceof Brief)) {
				Brief d1 = (Brief) e1;
				Brief d2 = (Brief) e2;
				String c1 = StringUtils.EMPTY;
				String c2 = StringUtils.EMPTY;
				switch (sortColumn) {
				case 0:
					c1 = d1.getDatum();
					c2 = d2.getDatum();
					break;
				case 1:
					c1 = d1.getBetreff();
					c2 = d2.getBetreff();
					break;
				case 2:
					c1 = d1.getTyp();
					c2 = d2.getTyp();
				case 3:
					c1 = d1.getMimeType();
					c2 = d2.getMimeType();
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
