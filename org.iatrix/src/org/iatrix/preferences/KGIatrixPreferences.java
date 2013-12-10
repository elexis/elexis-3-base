/*******************************************************************************
 * Copyright (c) 2008, Peter Schoenbucher
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    D. Lutz - initial implementation
 *    
 *******************************************************************************/
package org.iatrix.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.iatrix.Iatrix;

import ch.elexis.Hub;
import ch.elexis.preferences.SettingsPreferenceStore;

/**
 * Settings for KG Iatrix
 */
public class KGIatrixPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public static final String ID = "org.iatrix.preferences.KGIatrixPreferences";
	
	public KGIatrixPreferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(Hub.userCfg));
		setDescription("Einstellungen für KG Iatrix");
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new StringFieldEditor(Iatrix.CFG_AUTO_SAVE_PERIOD,
			"Automatische Speicherung (in Sekunden, 0 = nicht) [U]", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
			"Auswahl Leistungen/Diagnosen: Fenster nach Auswahl schliessen [U]",
			getFieldEditorParent()));
	}
	
	public void init(final IWorkbench workbench){
		// initialize values if needed
		
		String value;
		
		value = Hub.userCfg.get(Iatrix.CFG_AUTO_SAVE_PERIOD, null);
		if (value == null) {
			Hub.userCfg.set(Iatrix.CFG_AUTO_SAVE_PERIOD, Iatrix.CFG_AUTO_SAVE_PERIOD_DEFAULT);
		}
		value = Hub.userCfg.get(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE, null);
		if (value == null) {
			Hub.userCfg.set(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
				Iatrix.CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT);
		}
	}
	
	@Override
	public boolean performOk(){
		if (super.performOk()) {
			Hub.userCfg.flush();
			return true;
		}
		return false;
	}
	
}
