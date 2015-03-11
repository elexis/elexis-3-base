/*******************************************************************************
 * Copyright (c) 2005-2009, D. Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    D. Lutz - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.agenda.Messages;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class AgendaAnzeige extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.userCfg);
	
	public AgendaAnzeige(){
		super(GRID);
		
		prefs.setDefault(PreferenceConstants.AG_SHOW_REASON, false);
		prefs.setDefault(PreferenceConstants.AG_BIG_SAVE_COLUMNWIDTH, true);
		
		setPreferenceStore(prefs);
		
		setDescription(Messages.AgendaAnzeige_options);
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new BooleanFieldEditor(PreferenceConstants.AG_SHOW_REASON,
			Messages.AgendaAnzeige_showReason, getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(PreferenceConstants.AG_BIG_SAVE_COLUMNWIDTH,
			Messages.AgendaAnzeige_saveColumnSize, getFieldEditorParent()));
	}
	
	@Override
	public boolean performOk(){
		prefs.flush();
		return super.performOk();
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
}
