/**
 * Copyright (c) 2010-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 */

package ch.medelexis.templator.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	IPreferenceStore store;
	public static final String PREFERENCE_BRANCH = "briefe/medelexis-templator/";
	public static final String PREF_TEMPLATEBASE = PREFERENCE_BRANCH + "templates";
	public static final String PREF_DOSAVE = PREFERENCE_BRANCH + "save";
	public static final String PREF_CATEGORY = PREFERENCE_BRANCH + "category";
	
	public Preferences(){
		super(GRID);
		store = new SettingsPreferenceStore(CoreHub.localCfg);
		setPreferenceStore(store);
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new DirectoryFieldEditor(PREF_TEMPLATEBASE, "Schablonen-Verzeichnis",
			getFieldEditorParent()));
		Label i2 = new Label(getFieldEditorParent(), SWT.WRAP);
		i2.setText("Geben Sie bitte an, wie das Dokument nach\ndem Fertigstellen archiviert werden soll");
		i2.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		addField(new BooleanFieldEditor(PREF_DOSAVE, "Dokument archivieren", getFieldEditorParent()));
		addField(new StringFieldEditor(PREF_CATEGORY, "Docmanager-Kategorie",
			getFieldEditorParent()));
		
	}
	
	@Override
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void performApply(){
		CoreHub.localCfg.flush();
	}
	
}
