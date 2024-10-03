/**
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     G. Weirich - initial API and implementation
 * Adapted to Bioanalytica by Daniel Lutz <danlutz@watz.ch>
 */

package ch.elexis.laborimport.bioanalytica;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String JAR_PATH = "bioanalytica/jar_path"; //$NON-NLS-1$
	public static final String INI_PATH = "bioanalytica/ini_path"; //$NON-NLS-1$
	public static final String DL_DIR = "bioanalytica/downloaddir"; //$NON-NLS-1$

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected void createFieldEditors() {
		FileFieldEditor jarEditor = new FileFieldEditor(JAR_PATH, Messages.PreferencePage_JMedTrasferJar,
				getFieldEditorParent());
		FileFieldEditor iniEditor = new FileFieldEditor(INI_PATH, Messages.PreferencePage_JMedTrasferJni,
				getFieldEditorParent());
		DirectoryFieldEditor dirEditor = new DirectoryFieldEditor(DL_DIR, Messages.PreferencePage_DownloadDir,
				getFieldEditorParent());

		jarEditor.getTextControl(getFieldEditorParent()).setMessage("Optional");//$NON-NLS-1$
		iniEditor.getTextControl(getFieldEditorParent()).setMessage("Optional");//$NON-NLS-1$

		addField(jarEditor);
		addField(iniEditor);
		addField(dirEditor);
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}
}
