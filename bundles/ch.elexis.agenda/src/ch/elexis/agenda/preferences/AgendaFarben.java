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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.util.SWTHelper;

public class AgendaFarben extends PreferencePage implements IWorkbenchPreferencePage {
	private ConfigServicePreferenceStore prefs;
	private int typCols, typRows, statusCols;

	// private ColorCellEditor[] editors;
	// private String[] columnProperties;
	
	// Liste um die UI-Updates zu speichern
	private List<Runnable> uiUpdaters = new ArrayList<>();

	public AgendaFarben() {
		prefs = new ConfigServicePreferenceStore(Scope.USER);
		setPreferenceStore(prefs);
		setDescription(Messages.AgendaFarben_colorSettings);
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite par = new Composite(parent, SWT.NONE);
		par.setLayout(new GridLayout(1, false));
		Group top = new Group(par, SWT.BORDER);
		Group bottom = new Group(par, SWT.BORDER);
		String[] typ = Termin.TerminTypes;
		top.setText(Messages.AgendaFarben_appTypes);
		bottom.setText(Messages.AgendaFarben_appstateTypes);
		int num = typ.length;
		typRows = ((int) Math.sqrt(num));
		typCols = (num - (typRows * typRows));
		if (typCols < 4) {
			typCols = 4;
		}
		top.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		top.setLayout(new GridLayout(typCols, true));
		for (int i = 0; i < num; i++) {
			Label lab = new Label(top, SWT.NONE);
			lab.setText(typ[i]);
			String coldesc = ConfigServiceHolder.getUser(PreferenceConstants.AG_TYPCOLOR_PREFIX + typ[i], "FFFFFF"); //$NON-NLS-1$
			Color background = UiDesk.getColorFromRGB(coldesc);
			lab.setBackground(background);
			GridData gd = new GridData(GridData.FILL_BOTH);
			lab.setLayoutData(gd);
			lab.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					ColorDialog cd = new ColorDialog(getShell());
					Label l = (Label) e.getSource();
					RGB selected = cd.open();
					String symbolic = UiDesk.createColor(selected);
					l.setBackground(UiDesk.getColorFromRGB(symbolic));
					ConfigServiceHolder.setUser(PreferenceConstants.AG_TYPCOLOR_PREFIX + l.getText(), symbolic);
				}

			});
		}

		String[] status = Termin.TerminStatus;

		num = status.length;
		int statusRows = ((int) Math.sqrt(num));
		statusCols = num - (statusRows * statusRows);
		if (statusCols < 4) {
			statusCols = 4;
		}
		bottom.setLayout(new GridLayout(statusCols, true));
		bottom.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		for (int i = 0; i < num; i++) {
			Label lab = new Label(bottom, SWT.NONE);
			lab.setText(status[i]);
			GridData gd = new GridData(GridData.FILL_BOTH);
			lab.setLayoutData(gd);
			lab.setBackground(UiDesk.getColorFromRGB(
					ConfigServiceHolder.getUser(PreferenceConstants.AG_STATCOLOR_PREFIX + status[i], "FFFFFF"))); //$NON-NLS-1$
			lab.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					ColorDialog cd = new ColorDialog(getShell());
					Label l = (Label) e.getSource();
					RGB selected = cd.open();
					String symbolic = UiDesk.createColor(selected);
					l.setBackground(UiDesk.getColorFromRGB(symbolic));
					ConfigServiceHolder.setUser(PreferenceConstants.AG_STATCOLOR_PREFIX + l.getText(), symbolic);
				}

			});
		}
		Group terminListeColors = new Group(par, SWT.BORDER);
		terminListeColors.setText(Messages.AgendaFarben_Terminliste);
		terminListeColors.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		terminListeColors.setLayout(new GridLayout(2, false));
		final Button cbGlobal = new Button(terminListeColors, SWT.CHECK);
		cbGlobal.setText(Messages.AgendaFarben_Preferences_GlobalSettings);
		GridData gdCb = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
		cbGlobal.setLayoutData(gdCb);
		boolean isGlobal = ConfigServiceHolder.getGlobal(PreferenceConstants.TL_USE_GLOBAL_SETTINGS, false);
		cbGlobal.setSelection(isGlobal);
		cbGlobal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = cbGlobal.getSelection();
				for (Runnable updater : uiUpdaters) {
					updater.run();
				}
				new Thread(() -> {
					ConfigServiceHolder.get().set(PreferenceConstants.TL_USE_GLOBAL_SETTINGS, isSelected);
				}).start();
			}
		});
		createColorRow(terminListeColors, Messages.AgendaFarben_PastAppointments, PreferenceConstants.TL_PAST_BG_COLOR,
				PreferenceConstants.TL_PAST_BG_COLOR_DEFAULT, cbGlobal);
		createColorRow(terminListeColors, Messages.AgendaFarben_FutureAppointments,
				PreferenceConstants.TL_FUTURE_BG_COLOR, PreferenceConstants.TL_FUTURE_BG_COLOR_DEFAULT, cbGlobal);
		return par;
	}

	private void createColorRow(Composite parent, String labelText, String prefKey, String defaultColor,
			Button cbGlobal) {
		Label text = new Label(parent, SWT.NONE);
		text.setText(labelText);
		Label colorPreview = new Label(parent, SWT.BORDER);
		colorPreview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Runnable updateColorPreview = new Runnable() {
			@Override
			public void run() {
				String coldesc;
				if (cbGlobal.getSelection()) {
					coldesc = ConfigServiceHolder.getGlobal(prefKey, defaultColor);
				} else {
					coldesc = ConfigServiceHolder.getUser(prefKey, defaultColor);
				}
				if (!colorPreview.isDisposed()) {
					colorPreview.setBackground(UiDesk.getColorFromRGB(coldesc));
				}
			}
		};
		uiUpdaters.add(updateColorPreview);
		updateColorPreview.run();
		colorPreview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ColorDialog cd = new ColorDialog(getShell());
				RGB selected = cd.open();
				if (selected == null) {
					return;
				}
				String symbolic = UiDesk.createColor(selected);
				if (cbGlobal != null && cbGlobal.getSelection()) {
					ConfigServiceHolder.get().set(prefKey, symbolic);
					ConfigServiceHolder.setUser(prefKey, null);
				} else {
					ConfigServiceHolder.setUser(prefKey, symbolic);
				}
				updateColorPreview.run();
			}
		});
		colorPreview.setToolTipText(Messages.AgendaFarben_DoubleClickToChange);
	}
}
