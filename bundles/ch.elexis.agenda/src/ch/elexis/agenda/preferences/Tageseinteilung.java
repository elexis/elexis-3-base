/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
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

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.agenda.util.TermineLockedTimesUpdater;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.TimeTool.DAYS;

public class Tageseinteilung extends PreferencePage implements IWorkbenchPreferencePage {
	Text tMo, tDi, tMi, tDo, tFr, tSa, tSo;
	int actBereich;
	String[] bereiche;
	private Composite compositeDayBorders;
	private Text sodt;
	private Text eodt;
	private ComboViewer comboViewerDayEditSelector;
	private Button btnEditValuesFor;
	private Label lblChangedValuesAre;
	private DateTime dateTimeStartingFrom;
	private Button btnApplyEdit;
	private Composite compositeEditStarting;
	private TimeTool.DAYS editSelection;
	private Text editSelectionText;
	private Color originalBackgroundColor;

	public Tageseinteilung() {
		super(Messages.Tageseinteilung_dayPlanning);
		bereiche = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICHE, Messages.Tageseinteilung_praxis)
				.split(","); //$NON-NLS-1$
		actBereich = 0;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		new Label(ret, SWT.None).setText(Messages.Tageseinteilung_enterPeriods);
		final Combo cbBereich = new Combo(ret, SWT.READ_ONLY | SWT.SINGLE);
		cbBereich.setItems(bereiche);

		Composite grid = new Composite(ret, SWT.BORDER);
		grid.setLayout(new GridLayout(7, true));
		grid.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		new Label(grid, SWT.CENTER).setText(Messages.Tageseinteilung_mo);
		new Label(grid, SWT.CENTER).setText(Messages.Tageseinteilung_tu);
		new Label(grid, SWT.CENTER).setText(Messages.Tageseinteilung_we);
		new Label(grid, SWT.CENTER).setText(Messages.Tageseinteilung_th);
		new Label(grid, SWT.NONE).setText(Messages.Tageseinteilung_fr);
		new Label(grid, SWT.NONE).setText(Messages.Tageseinteilung_sa);
		new Label(grid, SWT.NONE).setText(Messages.Tageseinteilung_so);
		tMo = new Text(grid, SWT.BORDER | SWT.MULTI);
		tMo.setEnabled(false);
		tMo.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		originalBackgroundColor = tMo.getBackground();
		tDi = new Text(grid, SWT.BORDER | SWT.MULTI);
		tDi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tDi.setEnabled(false);
		tMi = new Text(grid, SWT.BORDER | SWT.MULTI);
		tMi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tMi.setEnabled(false);
		tDo = new Text(grid, SWT.BORDER | SWT.MULTI);
		tDo.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tDo.setEnabled(false);
		tFr = new Text(grid, SWT.BORDER | SWT.MULTI);
		tFr.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tFr.setEnabled(false);
		tSa = new Text(grid, SWT.BORDER | SWT.MULTI);
		tSa.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tSa.setEnabled(false);
		tSo = new Text(grid, SWT.BORDER | SWT.MULTI);
		tSo.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tSo.setEnabled(false);
		cbBereich.select(actBereich);

		Composite editDayComposite = new Composite(grid, SWT.None);
		editDayComposite.setLayoutData(SWTHelper.getFillGridData(7, true, 1, false));
		editDayComposite.setLayout(new GridLayout(3, false));

		btnEditValuesFor = new Button(editDayComposite, SWT.NONE);
		btnEditValuesFor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) comboViewerDayEditSelector.getSelection();
				editSelection = (DAYS) ss.getFirstElement();
				if (editSelection == null)
					return;

				Text[] days = new Text[] { tMo, tDi, tMi, tDo, tFr, tSa, tSo };
				for (Text text : days) {
					text.setEnabled(false);
					text.setBackground(originalBackgroundColor);
				}

				switch (editSelection) {
				case MONDAY:
					editSelectionText = tMo;
					break;
				case TUESDAY:
					editSelectionText = tDi;
					break;
				case FRIDAY:
					editSelectionText = tFr;
					break;
				case SATURDAY:
					editSelectionText = tSa;
					break;
				case SUNDAY:
					editSelectionText = tSo;
					break;
				case THURSDAY:
					editSelectionText = tDo;
					break;
				case WEDNESDAY:
					editSelectionText = tMi;
					break;
				default:
					break;
				}
				editSelectionText.setEnabled(true);
				editSelectionText
						.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_YELLOW));
				btnApplyEdit.setEnabled(true);
				dateTimeStartingFrom.setEnabled(true);
				editSelectionText.setFocus();
			}
		});
		btnEditValuesFor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnEditValuesFor.setText(Messages.Tageseinteilung_lblEditValuesFor_text);

		comboViewerDayEditSelector = new ComboViewer(editDayComposite, SWT.NONE);
		Combo comboDayEditSelector = comboViewerDayEditSelector.getCombo();
		comboDayEditSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		compositeEditStarting = new Composite(editDayComposite, SWT.NONE);
		compositeEditStarting.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_compositeEditStarting = new GridLayout(3, false);
		gl_compositeEditStarting.verticalSpacing = 0;
		gl_compositeEditStarting.marginHeight = 0;
		compositeEditStarting.setLayout(gl_compositeEditStarting);

		lblChangedValuesAre = new Label(compositeEditStarting, SWT.NONE);
		lblChangedValuesAre.setText(Messages.Tageseinteilung_lblChangedValuesAre_text);

		dateTimeStartingFrom = new DateTime(compositeEditStarting, SWT.BORDER);
		TimeTool tomorrow = new TimeTool();
		tomorrow.addDays(1);
		dateTimeStartingFrom.setDate(tomorrow.get(TimeTool.YEAR), tomorrow.get(TimeTool.MONTH),
				tomorrow.get(TimeTool.DAY_OF_MONTH));
		dateTimeStartingFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setErrorMessage(null);
				DateTime dt = (DateTime) e.getSource();
				int day = dateTimeStartingFrom.getDay(); // Calendar.DAY_OF_MONTH
				int month = dateTimeStartingFrom.getMonth(); // Calendar.MONTH
				int year = dateTimeStartingFrom.getYear(); // Calendar.YEAR
				String timeString = String.format("%02d", day) + "." + String.format("%02d", month + 1) + "."
						+ String.format("%04d", year);
				TimeTool tt = new TimeTool(timeString);
				if (tt.isBefore(new TimeTool())) {
					setErrorMessage(Messages.Tageseinteilung_no_past_Date);
					TimeTool tomorrow = new TimeTool();
					tomorrow.addDays(1);
					dateTimeStartingFrom.setDate(tomorrow.get(TimeTool.YEAR), tomorrow.get(TimeTool.MONTH),
							tomorrow.get(TimeTool.DAY_OF_MONTH));
				}
			}
		});
		dateTimeStartingFrom.setEnabled(false);

		btnApplyEdit = new Button(compositeEditStarting, SWT.NONE);
		btnApplyEdit.setText(Messages.Tageseinteilung_btnNewButton_text);
		btnApplyEdit.setEnabled(false);
		btnApplyEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Apply the selected edits starting from the selected date
				int day = dateTimeStartingFrom.getDay(); // Calendar.DAY_OF_MONTH
				int month = dateTimeStartingFrom.getMonth(); // Calendar.MONTH
				int year = dateTimeStartingFrom.getYear(); // Calendar.YEAR
				String timeString = String.format("%02d", day) + "." + String.format("%02d", month + 1) + "."
						+ String.format("%04d", year);
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(UiDesk.getTopShell());
				IRunnableWithProgress irp = new TermineLockedTimesUpdater(new TimeTool(timeString), editSelection,
						editSelectionText.getText(), Termin.TerminBereiche[actBereich]);
				try {
					pmd.run(false, false, irp);
					editSelectionText.setBackground(originalBackgroundColor);
					editSelectionText.setEnabled(false);
				} catch (InvocationTargetException e1) {
					Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Execution Error", e1);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
				} catch (InterruptedException e1) {
					Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Execution Error", e1);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
				}

				dateTimeStartingFrom.setEnabled(false);
				btnApplyEdit.setEnabled(false);
			}
		});

		comboViewerDayEditSelector.setContentProvider(ArrayContentProvider.getInstance());
		comboViewerDayEditSelector.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				TimeTool.DAYS day = (TimeTool.DAYS) element;
				return day.fullName;
			}
		});
		TimeTool.DAYS[] days = TimeTool.DAYS.values();
		comboViewerDayEditSelector.setInput(days);
		comboViewerDayEditSelector.setSelection(new StructuredSelection(days[0]));

		compositeDayBorders = new Composite(ret, SWT.NONE);
		compositeDayBorders.setLayout(new GridLayout(2, false));

		Composite compositeStart = new Composite(compositeDayBorders, SWT.NONE);
		compositeStart.setLayout(new GridLayout(3, false));

		Label btnDayStartHourIsSet = new Label(compositeStart, SWT.CHECK);
		btnDayStartHourIsSet.setText(Messages.Tageseinteilung_btnCheckButton_text);

		sodt = new Text(compositeStart, SWT.BORDER);
		sodt.setTextLimit(4);

		Label lblHours = new Label(compositeStart, SWT.NONE);
		lblHours.setText(Messages.Tageseinteilung_lblHours_text);

		Composite compositeEnd = new Composite(compositeDayBorders, SWT.NONE);
		compositeEnd.setLayout(new GridLayout(3, false));

		Label btnEndStartHourIsSet = new Label(compositeEnd, SWT.CHECK);
		btnEndStartHourIsSet.setText(Messages.Tageseinteilung_btnCheckButton_text_1);

		eodt = new Text(compositeEnd, SWT.BORDER);
		eodt.setTextLimit(4);

		Label lblHours_1 = new Label(compositeEnd, SWT.NONE);
		lblHours_1.setText(Messages.Tageseinteilung_lblHours_1_text);

		reload();
		cbBereich.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = cbBereich.getSelectionIndex();
				if (idx != -1) {
					save();
					actBereich = idx;
					reload();
				}
			}

		});
		return ret;
	}

	void reload() {
		Hashtable<String, String> map = Plannables.getDayPrefFor(bereiche[actBereich]);
		String p = map.get(Messages.Tageseinteilung_mo);
		tMo.setText(p == null ? "0000-0800\n1800-2359" : p); //$NON-NLS-1$
		p = map.get(Messages.Tageseinteilung_tu);
		tDi.setText(p == null ? "0000-0800\n1800-2359" : p); //$NON-NLS-1$
		p = map.get(Messages.Tageseinteilung_we);
		tMi.setText(p == null ? "0000-0800\n1800-2359" : p); //$NON-NLS-1$
		p = map.get(Messages.Tageseinteilung_th);
		tDo.setText(p == null ? "0000-0800\n1800-2359" : p); //$NON-NLS-1$
		p = map.get(Messages.Tageseinteilung_fr);
		tFr.setText(p == null ? "0000-0800\n1800-2359" : p); //$NON-NLS-1$
		p = map.get(Messages.Tageseinteilung_sa);
		tSa.setText(p == null ? "0000-0800\n1200-2359" : p); //$NON-NLS-1$
		p = map.get(Messages.Tageseinteilung_su);
		tSo.setText(p == null ? "0000-2359" : p); //$NON-NLS-1$

		String sodtString = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_DAY_PRESENTATION_STARTS_AT, "0000");
		sodt.setText(sodtString);
		String eodtString = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_DAY_PRESENTATION_ENDS_AT, "2359");
		eodt.setText(eodtString);

	}

	void save() {
		Hashtable<String, String> map = new Hashtable<String, String>();
		map.put(Messages.Tageseinteilung_mo, tMo.getText());
		map.put(Messages.Tageseinteilung_tu, tDi.getText());
		map.put(Messages.Tageseinteilung_we, tMi.getText());
		map.put(Messages.Tageseinteilung_th, tDo.getText());
		map.put(Messages.Tageseinteilung_fr, tFr.getText());
		map.put(Messages.Tageseinteilung_sa, tSa.getText());
		map.put(Messages.Tageseinteilung_su, tSo.getText());
		Plannables.setDayPrefFor(bereiche[actBereich], map);

		ConfigServiceHolder.get().set(PreferenceConstants.AG_DAY_PRESENTATION_STARTS_AT, sodt.getText());
		ConfigServiceHolder.get().set(PreferenceConstants.AG_DAY_PRESENTATION_ENDS_AT, eodt.getText());
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performApply() {
		save();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		save();
		return super.performOk();
	}
}
