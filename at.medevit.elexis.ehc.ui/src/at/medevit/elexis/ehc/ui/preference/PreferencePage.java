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
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public static String EHC_OUTPUTDIR = "at.medevit.elexis.ehc.ui.output.dir"; //$NON-NLS-1$
	public static String EHC_INPUTDIR = "at.medevit.elexis.ehc.ui.input.dir"; //$NON-NLS-1$
	
	@Override
	public void init(IWorkbench workbench){
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.userCfg));
		setDescription("e-Health Connector Einstellungen");
		
		initDirectories();
	}
	
	public static void initDirectories(){
		if (CoreHub.userCfg.get(EHC_OUTPUTDIR, "notset").equals("notset")) {
			File outputDir = new File(getDefaultOutputDir());
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			CoreHub.userCfg.set(EHC_OUTPUTDIR, getDefaultOutputDir());
		}
		
		if (CoreHub.userCfg.get(EHC_INPUTDIR, "notset").equals("notset")) {
			File inputDir = new File(getDefaultInputDir());
			if (!inputDir.exists()) {
				inputDir.mkdirs();
			}
			CoreHub.userCfg.set(EHC_INPUTDIR, getDefaultInputDir());
		}
	}
	
	@Override
	protected void createFieldEditors(){
		FieldEditor editor;
		editor =
			new DirectoryFieldEditor(EHC_OUTPUTDIR, "Standard Ausgabeverzeichnis",
				getFieldEditorParent());
		addField(editor);
		
		editor =
			new DirectoryFieldEditor(EHC_INPUTDIR, "Standard Eingangsverzeichnis",
				getFieldEditorParent());
		addField(editor);
	}
	
	public static String getDefaultOutputDir(){
		return CoreHub.getWritableUserDir() + File.separator + "eHC" + File.separator + "output";
	}
	
	public static String getDefaultInputDir(){
		return CoreHub.getWritableUserDir() + File.separator + "eHC" + File.separator + "input";
	}
}
