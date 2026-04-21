/*******************************************************************************

 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda.preferences;


import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
/**
 * Einstellen von Zeitvorgaben für jeden Termintyp und jeden Mandanten Unter
 * agenda/zeitvorgaben ist für jeden mandanten ein String der Form
 * std=x[::Termintyp=x]... abgelegt, wobei std die Zeitvorgabe ist, wenn keine
 * der folgenden Termintypen passt. (Es muss nicht für alle Termintypen eine
 * Vorgabe bei jedem mandanten gemacht werden). Wenn eine Vorgabe 0 ist, dann
 * hat dieser Mandant den entsprechenden Termintyp gar nicht.
 *
 * @author gerry
 *
 */
public class Zeitvorgaben extends PreferencePage implements IWorkbenchPreferencePage {

	Table table;
	TableColumn[] cols;
	TableItem[] rows;
	TableCursor cursor;
	ControlEditor editor;
	String[] bereiche;

	public Zeitvorgaben() {
		super(Messages.Zeitvorgaben_timePrefs);
	}

	@Override
	protected Control createContents(Composite parent) {
		// parent.setLayout(new GridLayout());
		Composite check = new Composite(parent, SWT.BORDER);
		check.setLayout(new GridLayout());
		bereiche = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICHE, Messages.Zeitvorgaben_praxis)
				.split(",");

		table = new Table(check, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		cols = new TableColumn[bereiche.length + 1];
		cols[0] = new TableColumn(table, SWT.NONE);
		cols[0].setText(Messages.Zeitvorgaben_terminTypes);
		cols[0].setWidth(70);

		cursor = new TableCursor(table, SWT.NONE);
		editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;

		cursor.addSelectionListener(new SelectionAdapter() {
			// Tabellenauswahl soll dem Cursor folgen
			public void widgetSelected(SelectionEvent e) {
				table.setSelection(new TableItem[] { cursor.getRow() });
			}

			// Eingabetaste
			public void widgetDefaultSelected(SelectionEvent e) {
				if (cursor.getColumn() == 0)
					return;

				TableItem row = cursor.getRow();
				int column = cursor.getColumn();
				doEdit(row.getText(column));
			}
		});

		cursor.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (cursor.getColumn() == 0)
					return;

				if (e.keyCode == SWT.DEL || e.character == SWT.BS || e.character == SWT.DEL) {
					doEdit(StringUtils.EMPTY);
				} else if (Character.isDigit(e.character)) {
					doEdit(String.valueOf(e.character));
				}
			}
		});

		int i = 1;
		for (String bereich : bereiche) {
			cols[i] = new TableColumn(table, SWT.NONE);
			cols[i].setWidth(70);
			cols[i++].setText(bereich);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		Color customColor = CoreUiUtil.getColorForString("0000FF");
		final Color lightGray = CoreUiUtil.getColorForString("F0F0F0");
		TableItem t0 = new TableItem(table, SWT.NONE);
		t0.setText(0, Messages.DefaultOutputter_defaultOutputForCase);
		for (String typ : Termin.TerminTypes) {
			if (typ.equals(Termin.typFrei()) || typ.equals(Termin.typReserviert())) {
				continue;
			}
			TableItem it = new TableItem(table, SWT.NONE);
			it.setText(0, typ);
			i = 1;
			for (String bereich : bereiche) {
				Map<String, Integer> durations = AppointmentServiceHolder.get().getPreferredDurations(bereich);
				String tStd = String.valueOf(durations.get(IAppointmentService.AG_KEY_STD));
				Integer tTypInt = durations.get(typ);
				String tTyp = tTypInt != null ? String.valueOf(tTypInt) : null;

				t0.setText(i, tStd);

				if (tTyp != null && !tTyp.isEmpty()) {
					it.setText(i, tTyp);
					it.setForeground(i, customColor);
				} else {
					it.setText(i, tStd);
				}
				i++;
			}
		}

		TableItem[] allItems = table.getItems();
		for (int r = 0; r < allItems.length; r++) {
			if (r % 2 != 0) {
				allItems[r].setBackground(lightGray);
			}
		}

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 600;
		gd.heightHint = 400;
		table.setLayoutData(gd);

		Label legend = new Label(check, SWT.NONE);
		legend.setText(Messages.Zeitvorgaben_LegendBlueDeviations);
		legend.setForeground(customColor);
		legend.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Label hintLabel = new Label(check, SWT.NONE);
		hintLabel.setText(Messages.Zeitvorgaben_HintRightClickReset);
		hintLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		Menu contextMenu = new Menu(cursor);
		MenuItem resetItem = new MenuItem(contextMenu, SWT.NONE);

		resetItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int colIdx = cursor.getColumn();
				if (colIdx > 0) {
					resetBereichToDefault(colIdx, cols[colIdx].getText());
				}
			}
		});
		cursor.setMenu(contextMenu);
		cursor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.setSelection(new TableItem[] { cursor.getRow() });
				int colIdx = cursor.getColumn();
				if (colIdx > 0) {
					resetItem.setEnabled(true);
					resetItem.setText(
							MessageFormat.format(Messages.Zeitvorgaben_RestoreDefaultsForArea, cols[colIdx].getText()));
				} else {
					resetItem.setEnabled(false);
					resetItem.setText(Messages.Zeitvorgaben_SelectAreaToReset);
				}
			}
		});
		return check;
	}

	public void init(IWorkbench workbench) {
	}

	private void doEdit(String inp) {
		final Text text = new Text(cursor, SWT.BORDER);
		text.addVerifyListener(e -> {
			if (!e.text.matches("\\d*")) {
				e.doit = false;
			}
		});

		text.setText(inp);
		text.setSelection(inp.length());
		Runnable saveAndDispose = () -> {
			if (text.isDisposed())
				return;
			TableItem it = cursor.getRow();
			int idx = cursor.getColumn();
			if (idx == 0) {
				text.dispose();
				return;
			}
			String ntext = text.getText().trim();
			String typ = it.getText(0);
			Color customColor = CoreUiUtil.getColorForString("0000FF");

			if (typ == null || typ.isEmpty() || typ.equals(Messages.DefaultOutputter_defaultOutputForCase)) {
				typ = IAppointmentService.AG_KEY_STD;
			}
			Map<String, Integer> durations = AppointmentServiceHolder.get().getPreferredDurations(cols[idx].getText());
			if (IAppointmentService.AG_KEY_STD.equals(typ)) {
				if (ntext.isEmpty()) {
					ntext = "30";
				}
				durations.put(IAppointmentService.AG_KEY_STD, Integer.parseInt(ntext));
				it.setText(idx, ntext);
				it.setForeground(idx, null);
			} else if (ntext.isEmpty()) {
				durations.remove(typ);
				Integer fallbackStd = durations.get(IAppointmentService.AG_KEY_STD);
				it.setText(idx, fallbackStd != null ? String.valueOf(fallbackStd) : StringUtils.EMPTY);
				it.setForeground(idx, null);
			} else {
				durations.put(typ, Integer.parseInt(ntext));
				it.setText(idx, ntext);
				it.setForeground(idx, customColor);
			}
			AppointmentServiceHolder.get().setPreferredDurations(cols[idx].getText(), durations);
			if (IAppointmentService.AG_KEY_STD.equals(typ)) {
				TableItem[] allRows = table.getItems();
				for (int r = 1; r < allRows.length; r++) {
					TableItem rowItem = allRows[r];
					String rowTyp = rowItem.getText(0);
					if (rowTyp.equals(Messages.DefaultOutputter_defaultOutputForCase))
						continue;
					Integer specificTime = durations.get(rowTyp);
					if (specificTime == null) {
						rowItem.setText(idx, ntext);
						rowItem.setForeground(idx, null);
					}
				}
			}
			text.dispose();
		};

		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if ((e.character == SWT.CR) || (e.keyCode == SWT.ARROW_DOWN)) {
					saveAndDispose.run();
				}
				if (e.character == SWT.ESC) {
					text.dispose();
				}
			}
		});

		text.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
				saveAndDispose.run();
			}
		});

		text.addFocusListener(new org.eclipse.swt.events.FocusAdapter() {
			@Override
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
				saveAndDispose.run();
			}
		});

		editor.setEditor(text);
		text.setFocus();
	}

	@Override
	protected void performDefaults() {
		for (int c = 1; c < cols.length; c++) {
			resetBereichToDefault(c, cols[c].getText());
		}
		super.performDefaults();
	}

	private void resetBereichToDefault(int colIndex, String bereich) {
		Map<String, Integer> durations = AppointmentServiceHolder.get().getPreferredDurations(bereich);
		Integer stdValueInt = durations.get(IAppointmentService.AG_KEY_STD);
		String stdValueStr = String.valueOf(stdValueInt);
		durations.clear();
		durations.put(IAppointmentService.AG_KEY_STD, stdValueInt);
		AppointmentServiceHolder.get().setPreferredDurations(bereich, durations);
		TableItem[] allItems = table.getItems();
		for (int r = 1; r < allItems.length; r++) {
			allItems[r].setText(colIndex, stdValueStr);
			allItems[r].setForeground(colIndex, null);
		}
	}
}