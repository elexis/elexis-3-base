/*******************************************************************************
 * Copyright (c) 2005-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.MultilineFieldEditor;
import ch.rgw.tools.StringTool;

public class AgendaDefinitionen extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.globalCfg);
	
	public AgendaDefinitionen(){
		super(GRID);
		
		prefs.setDefault(PreferenceConstants.AG_TERMINTYPEN,
			StringTool.join(Termin.TerminTypes, ",")); //$NON-NLS-1$
		prefs.setDefault(PreferenceConstants.AG_TERMINSTATUS,
			StringTool.join(Termin.TerminStatus, ",")); //$NON-NLS-1$
		setPreferenceStore(prefs);
		setDescription(Messages.AgendaDefinitionen_defForAgenda);
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new MultilineFieldEditor(PreferenceConstants.AG_BEREICHE,
			Messages.AgendaDefinitionen_shortCutsForBer, 5, SWT.V_SCROLL, true,
			getFieldEditorParent()));
		
		addField(new MultilineFieldEditor(PreferenceConstants.AG_TERMINTYPEN,
			Messages.AgendaDefinitionen_enterTypes, 5, SWT.V_SCROLL, true, getFieldEditorParent()));
		
		addField(new MultilineFieldEditor(PreferenceConstants.AG_TERMINSTATUS,
			Messages.AgendaDefinitionen_states, 5, SWT.V_SCROLL, true, getFieldEditorParent()));
		/*
		 * addField(new StringListFieldEditor(PreferenceConstants.AG_BEREICHE,
		 * Messages.AgendaDefinitionen_shortCutsForBer, Messages.AgendaDefinitionen_enterNames +
		 * Messages.AgendaDefinitionen_5,
		 * Messages.AgendaDefinitionen_ranges,getFieldEditorParent()));
		 * 
		 * addField(new StringListFieldEditor(PreferenceConstants.AG_TERMINTYPEN,
		 * Messages.AgendaDefinitionen_enterTypes, Messages.AgendaDefinitionen_enterTypeList,
		 * Messages.AgendaDefinitionen_types,getFieldEditorParent()));
		 * 
		 * addField(new StringListFieldEditor(PreferenceConstants.AG_TERMINSTATUS,
		 * Messages.AgendaDefinitionen_states,Messages.AgendaDefinitionen_enterStates+
		 * Messages.AgendaDefinitionen_12,Messages.AgendaDefinitionen_state
		 * ,getFieldEditorParent()));
		 */
		
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
