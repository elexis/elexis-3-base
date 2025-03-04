/*******************************************************************************
 * Copyright (c) 2010-2011, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.views;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.rfe.ReasonsForEncounter;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import jakarta.inject.Inject;

public class RFEView extends ViewPart {
	Table longTable, shortTable, mediumTable;
	CTabFolder tabs;
	Composite cCalc;
	boolean bDaempfung = false;
	HashMap<String, Integer> mapCodeToIndex = new HashMap<String, Integer>();
	HashMap<Integer, String> mapIndexToCode = new HashMap<Integer, String>();
	private IEncounter currentEncounter;
	static final int No_More_Valid = 1;

	@Inject
	void selectedEncounter(@Optional IEncounter encounter) {
		adjustTable(encounter);
		currentEncounter = encounter;
	}

	private void adjustTable(IEncounter encounter) {
		List<IReasonForEncounter> rfeForKOns;
		if (encounter != null) {
			rfeForKOns = getReasonsForEncounter(encounter);
		} else {
			rfeForKOns = Collections.emptyList();
		}

		if (tabs == null) {
			return;
		}

		CTabItem top = tabs.getSelection();
		if (top != null) {
			Control c = top.getControl();
			if (c instanceof Table) {
				Table table = (Table) c;
				table.deselectAll();
				for (TableItem it : table.getItems()) {
					// it.setBackground(null);
					// it.setForeground(null);
					it.setImage((Image) null);
				}
				for (IReasonForEncounter rfe : rfeForKOns) {
					int idx = mapCodeToIndex.get(rfe.getCode());
					TableItem item = table.getItem(idx);
					// item.setBackground(Desk.getColor(Desk.COL_SKYBLUE));
					// item.setForeground(Desk.getColor(Desk.COL_RED));
					if (item.getChecked())
						item.setImage(Images.IMG_TICK.getImage());
					// table.select(idx);
				}
			}
		}
	}

	private List<IReasonForEncounter> getReasonsForEncounter(IEncounter encounter) {
		IQuery<IReasonForEncounter> query = ArzttarifeModelServiceHolder.get().getQuery(IReasonForEncounter.class);
		query.and("konsID", COMPARATOR.EQUALS, encounter.getId());
		return query.execute();
	}

	private void removeReasonsForEncounter(IEncounter encounter) {
		List<IReasonForEncounter> existingReasons = getReasonsForEncounter(encounter);
		existingReasons.forEach(reason -> ArzttarifeModelServiceHolder.get().remove(reason));
	}

	@Override
	public void createPartControl(Composite parent) {
		tabs = new CTabFolder(parent, SWT.BOTTOM);
		tabs.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		CTabItem ctLong = new CTabItem(tabs, SWT.NONE);
		ctLong.setText("lang");
		longTable = new Table(tabs, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);
		ctLong.setControl(longTable);
		CTabItem ctMedium = new CTabItem(tabs, SWT.NONE);
		ctMedium.setText("kurz");
		mediumTable = new Table(tabs, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK);
		ctMedium.setControl(mediumTable);

		CTabItem ctStat = new CTabItem(tabs, SWT.NONE);
		ctStat.setText("Statistik");
		Composite cStat = new Composite(tabs, SWT.NONE);
		cStat.setLayout(new GridLayout());
		ctStat.setControl(cStat);
		Button bRecalc = new Button(cStat, SWT.PUSH);
		bRecalc.setText("Berechnen");
		bRecalc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cCalc = new Composite(cStat, SWT.NONE);
		cCalc.setLayout(new GridLayout());
		cCalc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		bRecalc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control c : cCalc.getChildren()) {
					c.dispose();
				}
				IQuery<IReasonForEncounter> query = ArzttarifeModelServiceHolder.get()
						.getQuery(IReasonForEncounter.class);
				int[] result = new int[ReasonsForEncounter.getCodeToReasonMap().values().size()];
				int all = 0;
				for (IReasonForEncounter rfe : query.execute()) {
					String code = rfe.getCode();
					if (code.length() != 2) {
						continue;
					}
					int idx = mapCodeToIndex.get(code);
					result[idx]++;
					all++;
				}
				for (int rline = 0; rline < result.length; rline++) {
					String code = mapIndexToCode.get(rline);
					int num = result[rline];
					float percent = num * 100f / all;
					int pc = Math.round(percent);
					Label lbl = new Label(cCalc, SWT.NONE);
					lbl.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
					lbl.setText(code + ": " + num + " (=" + pc + "%)");
				}
				cCalc.layout(true);
			}

		});
		int i = 0;
		for (String code : ReasonsForEncounter.getCodeToReasonMap().keySet()) {
			TableItem longItem = new TableItem(longTable, SWT.NONE);
			longItem.setText(ReasonsForEncounter.getCodeToShortReasonMap().get(code));
			TableItem mediumItem = new TableItem(mediumTable, SWT.NONE);
			mediumItem.setText(ReasonsForEncounter.getCodeToReasonMap().get(code));
			mapCodeToIndex.put(code, i);
			mapIndexToCode.put(i, code);
			if (i == No_More_Valid) {
				mediumItem.setBackground(UiDesk.getColor(UiDesk.COL_LIGHTGREY));
				mediumItem.setGrayed(true);
				longItem.setBackground(UiDesk.getColor(UiDesk.COL_LIGHTGREY));
				longItem.setGrayed(true);
			}
			i++;
		}
		longTable.addSelectionListener(new ClickListener(longTable));
		mediumTable.addSelectionListener(new ClickListener(mediumTable));
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	class ClickListener extends SelectionAdapter {
		Table table;

		ClickListener(Table table) {
			this.table = table;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (currentEncounter != null) {
				int[] sel = table.getSelectionIndices();
				if (sel.length > 0) {
					removeReasonsForEncounter(currentEncounter);

					for (int s : sel) {
						if (s == No_More_Valid) {
							break;
						}
						String code = mapIndexToCode.get(s);
						IReasonForEncounter reason = ArzttarifeModelServiceHolder.get()
								.create(IReasonForEncounter.class);
						reason.setEncounter(currentEncounter);
						reason.setCode(code);
						ArzttarifeModelServiceHolder.get().save(reason);
					}
					adjustTable(currentEncounter);
				}
			}
		}
	}
}
