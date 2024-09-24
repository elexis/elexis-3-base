/**
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     G. Weirich - initial API and implementation
 * All the rest is done generically. See plug-in elexis-importer.
 *
 */

package ch.elexis.laborimport.labtop;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String JAR_PATH = "labtop/jar_path"; //$NON-NLS-1$
	public static final String INI_PATH = "labtop/ini_path"; //$NON-NLS-1$
	public static final String DL_DIR = "labtop/downloaddir"; //$NON-NLS-1$
	public static final String ON = "OpenMedical aus"; //$NON-NLS-1$
	public static final String OFF = "OpenMedical an"; //$NON-NLS-1$
	public static final String CFG_OPENMEDICAL = "labtop/openmedical"; //$NON-NLS-1$

	private boolean openMedical = false;

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected void createFieldEditors() {
		Button button = new Button(getFieldEditorParent(), SWT.PUSH | SWT.CENTER);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.horizontalSpan = 3;
		button.setLayoutData(gridData);
		Composite container = createContainer(getFieldEditorParent());

		addField(new FileFieldEditor(JAR_PATH, Messages.PreferencePage_JMedTrasferJar, container));
		addField(new FileFieldEditor(INI_PATH, Messages.PreferencePage_JMedTrasferJni, container));
		addField(new DirectoryFieldEditor(DL_DIR, Messages.PreferencePage_DownloadDir, getFieldEditorParent()));

		setBoolean(button);
		toggleExclude(container);
		setBtnText(button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleOpenMedical();
				toggleExclude(container);
				setBtnText(button);
			}
		});
	}

	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 3;
		container.setLayoutData(gridData);
		return container;
	}

	private void toggleExclude(Composite container) {
		container.setVisible(!openMedical);
		((GridData) container.getLayoutData()).exclude = openMedical;
		getFieldEditorParent().layout(true, true);
	}

	private void toggleOpenMedical() {
		ConfigServiceHolder.get().set(CFG_OPENMEDICAL, openMedical);
		openMedical = !openMedical;
	}

	private void setBtnText(Button btn) {
		btn.setText(openMedical ? OFF : ON);
		btn.setSize(99, 25);
	}

	private void setBoolean(Button btn) {
		openMedical = !ConfigServiceHolder.get().get(CFG_OPENMEDICAL, false);
	}
}
