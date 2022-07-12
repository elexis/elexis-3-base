/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.preference;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static String EHC_OUTPUTDIR = "at.medevit.elexis.ehc.ui.output.dir"; //$NON-NLS-1$
	public static String EHC_INPUTDIR = "at.medevit.elexis.ehc.ui.input.dir"; //$NON-NLS-1$

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.USER));
		setDescription("e-Health Connector Einstellungen");

		initDirectories();
	}

	public static void initDirectories() {
		if (ConfigServiceHolder.getUser(EHC_OUTPUTDIR, "notset").equals("notset")) { //$NON-NLS-1$ //$NON-NLS-2$
			File outputDir = new File(getDefaultOutputDir());
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			ConfigServiceHolder.setUser(EHC_OUTPUTDIR, getDefaultOutputDir());
		}

		if (ConfigServiceHolder.getUser(EHC_INPUTDIR, "notset").equals("notset")) { //$NON-NLS-1$ //$NON-NLS-2$
			File inputDir = new File(getDefaultInputDir());
			if (!inputDir.exists()) {
				inputDir.mkdirs();
			}
			ConfigServiceHolder.setUser(EHC_INPUTDIR, getDefaultInputDir());
		}
	}

	@Override
	protected void createFieldEditors() {
		FieldEditor editor;
		editor = new DirectoryFieldEditor(EHC_OUTPUTDIR, "Standard Ausgabeverzeichnis", getFieldEditorParent());
		addField(editor);

		editor = new DirectoryFieldEditor(EHC_INPUTDIR, "Standard Eingangsverzeichnis", getFieldEditorParent());
		addField(editor);
	}

	public static String getDefaultOutputDir() {
		return CoreHub.getWritableUserDir() + File.separator + "eHC" + File.separator + "output"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getDefaultInputDir() {
		return CoreHub.getWritableUserDir() + File.separator + "eHC" + File.separator + "input"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
