/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     G. Weirich - initial API and implementation
 ******************************************************************************/
package ch.elexis.laborimport.hl7.universal;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String CFG_DIRECTORY = "hl7/downloaddir";
	public static final String CFG_DIRECTORY_AUTOIMPORT = "hl7/autoimport";
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new DirectoryFieldEditor(CFG_DIRECTORY, Messages.Prefs_ImportDirectory,
			getFieldEditorParent()));
		addField(new BooleanFieldEditor(HL7Parser.CFG_IMPORT_ENCDATA,
			Messages.Prefs_ImportAttachedFiles, getFieldEditorParent()));
		addField(new BooleanFieldEditor(CFG_DIRECTORY_AUTOIMPORT,
			"Verzeichnis überwachen und automatisch importieren", getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench){
		
	}
	
	@Override
	protected void performApply(){
		super.performApply();
		CoreHub.localCfg.flush();
	}
	
}
