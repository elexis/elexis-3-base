/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.privatrechnung.views;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.KontaktFieldEditor;
import ch.elexis.privatrechnung.data.PreferenceConstants;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new StringFieldEditor(PreferenceConstants.cfgTemplateESR, "Vorlage mit ESR",
			getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.cfgTemplateBill, "Vorlage ohne ESR",
			getFieldEditorParent()));
		addField(new KontaktFieldEditor(new ConfigServicePreferenceStore(Scope.GLOBAL),
			PreferenceConstants.cfgBank, "Bank",
			getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.esrIdentity, "ESR-Teilnehmernummer",
			getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.esrUser, "ESR-Kundennummer",
			getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
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
