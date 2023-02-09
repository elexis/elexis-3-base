/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.archie.patientstatistik;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

/**
 * This view summarizes all services to the currently selected patient
 *
 * @author gerry
 *
 */
public class VerrechnungsStatistikView extends ViewPart implements IRefreshable, Counter.IJobFinishedListener {
	private Action recalcAction, exportCSVAction;
	Form form;
	Table table;

	String[] tableHeaders = { Messages.VerrechnungsStatistikView_CODESYSTEM, Messages.VerrechnungsStatistikView_CODE,
			Messages.VerrechnungsStatistikView_TEXT, Messages.VerrechnungsStatistikView_NUMBER,
			Messages.VerrechnungsStatistikView_AMOUNT };
	int[] columnWidths = new int[] { 130, 60, 160, 40, 50 };

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);
	
	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			if (patient != null) {
				Patient pat = (Patient) NoPoUtil.loadAsPersistentObject(patient);
				if (pat != null) {
					form.setText(pat.getLabel());
				} else {
					form.setText(Messages.VerrechnungsStatistikView_NoPatientSelected);
				}
				recalc();
			} else {
				form.setText(Messages.VerrechnungsStatistikView_NoPatientSelected);
			}
		}, form);
	}
	

	/**
	 * The Eclipse View is created: We use a Form with an SWT Table to display the
	 * data. Then we create a local menu and toolbar and finally, we attach
	 * ourselves as ActivationListener at Elexis' Event scheduler to be informed
	 * when we become visible to the user.
	 */
	@Override
	public void createPartControl(Composite parent) {
		form = UiDesk.getToolkit().createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form.getBody().setLayout(new GridLayout());
		table = new Table(form.getBody(), SWT.NONE);
		table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		for (int i = 0; i < tableHeaders.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText(tableHeaders[i]);
			tc.setWidth(columnWidths[i]);
		}
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createToolbar(exportCSVAction, recalcAction);
		menu.createMenu(exportCSVAction);
		getSite().getPage().addPartListener(udpateOnVisible);
	}

	/**
	 * Important: On disposal of the View, the ActivationListener MUST be removed.
	 */
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * Method from SelectionListener
	 */
	public void clearEvent(Class<? extends PersistentObject> template) {
		// TODO Auto-generated method stub

	}

	private void recalc() {

		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat != null) {
			final Counter counter = new Counter(pat, null, null, this);
			table.removeAll();
			counter.schedule();
		}
	}

	public void jobFinished(final Counter counter) {
		HashMap<IBillable, List<IBilled>> cnt = counter.getValues();
		HashMap<String, Money> totals = new HashMap<String, Money>();

		// TreeSet<IVerrechenbar> set=new
		// TreeSet<IVerrechenbar>(cnt.keySet());
		ArrayList<IBillable> set = new ArrayList<IBillable>(cnt.keySet());
		Collections.sort(set, new Comparator<IBillable>() {

			public int compare(IBillable o1, IBillable o2) {
				if (o1 != null && o2 != null) {
					String csname1 = o1.getCodeSystemName();
					String csname2 = o2.getCodeSystemName();
					int res = csname1.compareTo(csname2);
					if (res == 0) {
						String cscode1 = o1.getCode();
						String cscode2 = o2.getCode();
						res = cscode1.compareTo(cscode2);
					}
					return res;
				}
				return 0;

			}
		});
		for (IBillable iv : set) {
			if (iv != null) {
				TableItem ti = new TableItem(table, SWT.NONE);
				String codename = iv.getCodeSystemName();
				Money tCode = totals.get(codename);
				if (tCode == null) {
					tCode = new Money();
					totals.put(codename, tCode);
				}
				ti.setText(0, StringTool.unNull(codename));
				ti.setText(1, StringTool.unNull(iv.getCode()));
				ti.setText(2, StringTool.unNull(iv.getText()));
				Money total = new Money();
				int count = 0;
				for (IBilled vv : cnt.get(iv)) {
					total.addMoney(vv.getTotal());
					count += vv.getAmount();
				}
				tCode.addMoney(total);
				ti.setText(3, Integer.toString(count));
				ti.setText(4, total.getAmountAsString());

			}
		}
		Money sumAll = new Money();
		for (String n : totals.keySet()) {
			TableItem ti = new TableItem(table, SWT.NONE);
			ti.setText(0, Messages.VerrechnungsStatistikView_SUM + n);
			Money sumClass = totals.get(n);
			ti.setText(4, sumClass.getAmountAsString());
			sumAll.addMoney(sumClass);
		}
		TableItem ti = new TableItem(table, SWT.BOLD);
		ti.setText(0, Messages.VerrechnungsStatistikView_SUMTOTAL);
		ti.setText(4, sumAll.getAmountAsString());
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}

	private void makeActions() {
		recalcAction = new Action(Messages.VerrechnungsStatistikView_REFRESH, Images.IMG_REFRESH.getImageDescriptor()) {
			@Override
			public void run() {
				recalc();
			}

		};
		exportCSVAction = new Action(Messages.VerrechnungsStatistikView_ExportToCSV,
				Images.IMG_EXPORT.getImageDescriptor()) {
			@Override
			public void run() {
				FileDialog fd = new FileDialog(getViewSite().getShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.csv", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				fd.setFilterNames(new String[] { "CSV", Messages.VerrechnungsStatistikView_AllFiles }); //$NON-NLS-1$
				fd.setFileName("elexis-verr.csv"); //$NON-NLS-1$
				String fname = fd.open();
				if (fd != null) {
					try {
						FileWriter fw = new FileWriter(fname);
						fw.write(StringTool.join(tableHeaders, StringConstants.SEMICOLON) + StringConstants.CRLF);
						for (TableItem it : table.getItems()) {
							StringBuilder sb = new StringBuilder();
							sb.append(it.getText(0)).append(StringConstants.SEMICOLON).append(it.getText(1))
									.append(StringConstants.SEMICOLON).append(it.getText(2))
									.append(StringConstants.SEMICOLON).append(it.getText(3))
									.append(StringConstants.SEMICOLON).append(it.getText(4))
									.append(StringConstants.CRLF);
							fw.write(sb.toString());
						}
						fw.close();
					} catch (IOException e) {
						ExHandler.handle(e);

					}

				}

			}

		};
	}
}
