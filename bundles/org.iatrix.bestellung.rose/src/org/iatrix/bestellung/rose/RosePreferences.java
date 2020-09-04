/*******************************************************************************
 * Copyright (c) 2010-2011, Medelexis AG
 * All rights reserved.
 *******************************************************************************/

package org.iatrix.bestellung.rose;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.KontaktFieldEditor;

public class RosePreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public RosePreferences(){
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		setDescription("Einstellung zur Bestellung bei der Apotheke zur Rose");
	}
	
	protected void createFieldEditors(){
		addField(new StringFieldEditor(Constants.CFG_ROSE_CLIENT_NUMBER, "Kundennummer",
			getFieldEditorParent()));
		
		addField(new StringFieldEditor(Constants.CFG_ASAS_PROXY_HOST, "HIN-Client Adresse",
			getFieldEditorParent()));
		addField(new StringFieldEditor(Constants.CFG_ASAS_PROXY_PORT, "HIN-Client Port",
			getFieldEditorParent()));
		addField(new KontaktFieldEditor(new ConfigServicePreferenceStore(Scope.GLOBAL),
			Constants.CFG_ROSE_SUPPLIER,
			"Lieferant", getFieldEditorParent()));
	}
	
	public void init(final IWorkbench workbench){
		// do nothing
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
