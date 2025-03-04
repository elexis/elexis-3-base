/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.base.befunde;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.befunde.Messwert;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

/**
 * This is a replacement for "MesswerteView" wich is more flexible in
 * displayable elements. It can show arbitrary textual or numerical findings
 *
 * @author gerry
 *
 */
public class FindingsView extends ViewPart implements IRefreshable {
	private static Log log = Log.get(FindingsView.class.getName());

	public static final String ID = "elexis-befunde.findingsView"; //$NON-NLS-1$
	private CTabFolder ctabs;
	private ScrolledForm form;
	private Map hash;
	private Action newValueAction, editValueAction, deleteValueAction, printValuesAction;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	public FindingsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout());
		form = UiDesk.getToolkit().createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new FillLayout());
		ctabs = new CTabFolder(body, SWT.NONE);
		ctabs.setLayout(new FillLayout());
		Messwert setup = Messwert.getSetup();
		hash = setup.getMap(Messwert.FLD_BEFUNDE);
		String names = (String) hash.get(Messwert.HASH_NAMES);
		if (!StringTool.isNothing(names)) {
			for (String n : names.split(Messwert.SETUP_SEPARATOR)) {
				CTabItem ci = new CTabItem(ctabs, SWT.NONE);
				ci.setText(n);
				FindingsPage fp = new FindingsPage(ctabs, n);
				ci.setControl(fp);
			}
		}
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createToolbar(newValueAction, editValueAction, printValuesAction, deleteValueAction);
		ctabs.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CTabItem it = ctabs.getSelection();
				if (it != null) {
					FindingsPage page = (FindingsPage) it.getControl();
					page.setPatient(ElexisEventDispatcher.getSelectedPatient());
				}
			}

		});

		getSite().getPage().addPartListener(udpateOnVisible);
		if (ctabs.getItemCount() > 0) {
			ctabs.setSelection(0);
			((FindingsPage) (ctabs.getItem(0)).getControl()).setPatient(ElexisEventDispatcher.getSelectedPatient());
		}

	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void setPatient(final Patient p) {
		if (p == null) {
			form.setText(Messages.FindingsView_noPatientSelected); // $NON-NLS-1$

		} else {
			form.setText(p.getLabel());
		}
		int idx = ctabs.getSelectionIndex();
		if (idx != -1) {
			CTabItem item = ctabs.getItem(idx);
			FindingsPage fp = (FindingsPage) item.getControl();
			fp.setPatient(p);
		}
	}

	class FindingsPage extends Composite {

		boolean sortDescending = true;
		Table table;
		TableColumn[] tc;
		TableItem[] items;
		String myparm;
		String[] flds = null;

		FindingsPage(final Composite parent, final String param) {
			super(parent, SWT.NONE);
			parent.setLayout(new FillLayout());
			myparm = param;
			setLayout(new GridLayout());

			table = new Table(this, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
			table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			String vals = (String) hash.get(param + Messwert._FIELDS);
			if (vals != null) {
				flds = vals.split(Messwert.SETUP_SEPARATOR);
				tc = new TableColumn[flds.length + 1];
				tc[0] = new TableColumn(table, SWT.NONE);
				tc[0].setText("Datum"); //$NON-NLS-1$
				tc[0].setWidth(80);
				tc[0].addListener(SWT.Selection, new Listener() {

					@Override
					public void handleEvent(Event event) {
						items = table.getItems();
						for (int i = 0; i < items.length; i++) {
							SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
							Date date1 = null;
							Date date2 = null;
							try {
								date1 = formatter.parse(items[i].getText());

								for (int j = 0; j < i; j++) {
									date2 = formatter.parse(items[j].getText());
									if (sortDescending) {
										if (date1.before(date2)) {
											sort(i, j);
											break;
										}
									} else {
										if (date1.after(date2)) {
											sort(i, j);
											break;
										}
									}
								}
							} catch (ParseException e) {
								log.log(e, "Date parsing exception", Log.WARNINGS); //$NON-NLS-1$
							}
						}

						if (sortDescending) {
							sortDescending = false;
						} else {
							sortDescending = true;
						}

						table.setSortColumn(tc[0]);
						table.update();
					}

				});
				for (int i = 1; i <= flds.length; i++) {
					tc[i] = new TableColumn(table, SWT.NONE);
					flds[i - 1] = flds[i - 1].split(Messwert.SETUP_CHECKSEPARATOR)[0];
					String[] header = flds[i - 1].split("=", 2); //$NON-NLS-1$
					tc[i].setText(header[0]);
					if (header.length > 1) {
						tc[i].setData("script", header[1]); //$NON-NLS-1$
					}
					tc[i].setWidth(80);
				}
				tc[flds.length].setWidth(600);
			}
			table.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDoubleClick(final MouseEvent e) {
					TableItem[] it = table.getSelection();
					if (it.length == 1) {
						EditFindingDialog dlg = new EditFindingDialog(getSite().getShell(), (Messwert) it[0].getData(),
								myparm);
						if (dlg.open() == Dialog.OK) {
							setPatient(ElexisEventDispatcher.getSelectedPatient());
						}
					}
				}

			});
		}

		private void sort(int i, int j) {
			if (flds != null) {
				String[] values = new String[flds.length];
				for (int fldIdx = 0; fldIdx < flds.length; fldIdx++) {
					values[fldIdx] = items[i].getText(fldIdx);
				}
				items[i].dispose();
				TableItem item = new TableItem(table, SWT.NONE, j);
				item.setText(values);
				items = table.getItems();
			}
		}

		public String[][] getFields() {
			if (flds != null) {
				String[][] ret = new String[table.getItemCount() + 1][flds.length + 1];
				ret[0][0] = "Datum"; //$NON-NLS-1$
				for (int i = 1; i <= flds.length; i++) {
					ret[0][i] = flds[i - 1];
				}
				for (int i = 0; i < table.getItemCount(); i++) {
					// ret[i+1]=new String[flds.length+1];
					for (int j = 0; j <= flds.length; j++) {
						ret[i + 1][j] = table.getItem(i).getText(j);
					}
				}
				return ret;
			}
			return new String[0][0];
		}

		void setPatient(final Patient pat) {
			if (pat != null) {
				Query<Messwert> qbe = new Query<Messwert>(Messwert.class);
				qbe.add(Messwert.FLD_PATIENT_ID, Query.EQUALS, pat.getId());
				qbe.add(Messwert.FLD_NAME, Query.EQUALS, myparm);
				List<Messwert> list = qbe.execute();
				table.removeAll();
				Collections.sort(list, new Comparator<Messwert>() {

					public int compare(final Messwert o1, final Messwert o2) {
						TimeTool t1 = new TimeTool(o1.getDate());
						TimeTool t2 = new TimeTool(o2.getDate());
						return t1.compareTo(t2);
					}
				});
				for (Messwert m : list) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(0, m.getDate()); // $NON-NLS-1$
					item.setData(m);
					Map hash = m.getMap(Messwert.FLD_BEFUNDE); // $NON-NLS-1$
					for (int i = 0; i < flds.length; i++) {
						item.setText(i + 1, PersistentObject.checkNull((String) hash.get(flds[i])));
					}
				}
			}
		}
	}

	/**
	 * Actions are objects for user - interactions. An action can be displayd as a
	 * menun item or as toolbar item, and it can be active or inactive. Here we need
	 * only one action to add a new measurement for a selectable date.
	 *
	 * Actions sind Objekte zur Benutzerinteraktion. Eine Action kann als Menueitem
	 * oder als Toolbaritem dargestellt werden, und sie kann aktiv oder inaktiv
	 * sein. Diese Action hier dient einfach der Eingabe eines neuen Messwerts a
	 * einem wählbaren Datum.
	 *
	 */
	private void makeActions() {
		newValueAction = new Action(Messages.MesswerteView_enterNewValue) { // $NON-NLS-1$
			{
				setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());
				setToolTipText(Messages.FindingsView_addNewMeasure); // $NON-NLS-1$
			}

			@Override
			public void run() {
				CTabItem ci = ctabs.getSelection();
				if (ci != null) {
					FindingsPage page = (FindingsPage) ci.getControl();
					EditFindingDialog dlg = new EditFindingDialog(getSite().getShell(), null, page.myparm);
					if (dlg.open() == Dialog.OK) {
						page.setPatient(ElexisEventDispatcher.getSelectedPatient());
					}
				}
			}
		};
		editValueAction = new Action(Messages.FindingsView_editActionCaption) { // $NON-NLS-1$
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(Messages.FindingsView_editActionToolTip); // $NON-NLS-1$
			}

			@Override
			public void run() {
				CTabItem ci = ctabs.getSelection();
				if (ci != null) {
					FindingsPage page = (FindingsPage) ci.getControl();
					TableItem[] it = page.table.getSelection();
					if (it.length == 1) {
						EditFindingDialog dlg = new EditFindingDialog(getSite().getShell(), (Messwert) it[0].getData(),
								page.myparm);
						if (dlg.open() == Dialog.OK) {
							page.setPatient(ElexisEventDispatcher.getSelectedPatient());
						}
					}
				}
			}
		};
		deleteValueAction = new Action(Messages.FindingsView_deleteActionCaption) { // $NON-NLS-1$
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText(Messages.FindingsView_deleteActionToolTip); // $NON-NLS-1$
			}

			@Override
			public void run() {
				if (SWTHelper.askYesNo(Messages.FindingsView_deleteConfirmCaption, // $NON-NLS-1$
						Messages.FindingsView_deleteConfirmMessage)) { // $NON-NLS-1$
					CTabItem ci = ctabs.getSelection();
					if (ci != null) {
						FindingsPage page = (FindingsPage) ci.getControl();
						TableItem[] it = page.table.getSelection();
						if (it.length == 1) {
							Messwert mw = (Messwert) it[0].getData();
							mw.delete();
							page.setPatient(ElexisEventDispatcher.getSelectedPatient());
						}
					}
				}
			}
		};
		printValuesAction = new Action(Messages.FindingsView_printActionCaptiob) { // $NON-NLS-1$
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText(Messages.FindingsView_printActionMessage); // $NON-NLS-1$
			}

			@Override
			public void run() {
				CTabItem top = ctabs.getSelection();
				if (top != null) {
					FindingsPage fp = (FindingsPage) top.getControl();
					String[][] table = fp.getFields();
					new PrintFindingsDialog(getViewSite().getShell(), table).open();
				}
			}
		};
	}

	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			setPatient((Patient) NoPoUtil.loadAsPersistentObject(patient));
		}, form);
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}

	public void selectTab(String tabName) {
		for (CTabItem item : ctabs.getItems()) {
			if (item.getText().equalsIgnoreCase(tabName)) {
				ctabs.setSelection(item);
				FindingsPage page = (FindingsPage) item.getControl();
				page.setPatient(ElexisEventDispatcher.getSelectedPatient());
				break;
			}
		}
	}

}
