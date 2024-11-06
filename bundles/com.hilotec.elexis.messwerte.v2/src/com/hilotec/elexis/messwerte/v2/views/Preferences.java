/*******************************************************************************
 * Copyright (c) 2011, Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.views;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.InexistingFileOKFileFieldEditor;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String CONFIG_FILE = "findings/hilotec/configfile"; //$NON-NLS-1$

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected void createFieldEditors() {
		addField(new InexistingFileOKFileFieldEditor(CONFIG_FILE, "Konfigurationsdatei", //$NON-NLS-1$
				getFieldEditorParent()));

		Button migrationBtn = new Button(getFieldEditorParent().getParent(), SWT.PUSH);
		migrationBtn.setText("Messwerte Migration");
		migrationBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
				try {
					progressDialog.run(true, false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							ObservationMigrator migrator = new ObservationMigrator();
							Display.getDefault().syncExec(() -> {
								// File standard dialog
								FileDialog fileDialog = new FileDialog(progressDialog.getShell());
								// Set the text
								fileDialog.setText("Messwerte Migration Mapping");
								// Set filter on .properties files
								fileDialog.setFilterExtensions(new String[] { "*.properties" });
								// Open Dialog and save result of selection
								String selected = fileDialog.open();
								if (StringUtils.isNotBlank(selected)) {
									migrator.loadProperties(selected);
								}
							});
							migrator.migrate(monitor);
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					MessageDialog.openError(getShell(), "Messwerte konvertieren",
							"Fehler beim erzeugen der strukturierten Messwerte.");
					LoggerFactory.getLogger(getClass()).error("Error creating structured diagnosis", e);
				}
			}
		});
	}

	@Override
	public void performApply() {
		CoreHub.localCfg.flush();
	}

}
