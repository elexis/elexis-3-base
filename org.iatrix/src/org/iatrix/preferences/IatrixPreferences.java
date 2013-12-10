/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     D. Lutz - initial API and implementation
 * 
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.iatrix.Iatrix;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

/**
 * Settings for Iatrix
 */
public class IatrixPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public static final String ID = "org.iatrix.preferences.IatrixPreferences";
	
	public IatrixPreferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
		setDescription("Iatrix");
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new StringFieldEditor(Iatrix.CFG_MAX_SHOWN_CHARGES, "Kons-Leistungen",
			getFieldEditorParent()));
		
		addField(new StringFieldEditor(Iatrix.CFG_MAX_SHOWN_CONSULTATIONS,
			"Angezeigte Konsultationen", getFieldEditorParent()));
	}
	
	public void init(final IWorkbench workbench){
		// initialize values if needed
		
		String value;
		
		value = CoreHub.globalCfg.get(Iatrix.CFG_MAX_SHOWN_CHARGES, null);
		if (value == null) {
			CoreHub.globalCfg.set(Iatrix.CFG_MAX_SHOWN_CHARGES, Iatrix.CFG_MAX_SHOWN_CHARGES_DEFAULT);
		}
		
		value = CoreHub.globalCfg.get(Iatrix.CFG_MAX_SHOWN_CONSULTATIONS, null);
		if (value == null) {
			CoreHub.globalCfg.set(Iatrix.CFG_MAX_SHOWN_CONSULTATIONS,
				Iatrix.CFG_MAX_SHOWN_CONSULTATIONS_DEFAULT);
		}
	}
	
	@Override
	public boolean performOk(){
		if (super.performOk()) {
			CoreHub.globalCfg.flush();
			return true;
		}
		return false;
	}
	
}
