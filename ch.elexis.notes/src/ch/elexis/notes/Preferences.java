/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.notes;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

/**
 * Settings for the notes-Plugin
 * 
 * @author gerry
 * 
 */
public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String CFGTREE = "notes/basedir"; //$NON-NLS-1$
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new DirectoryFieldEditor(CFGTREE, Messages.Preferences_basedir,
			getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
	}
	
}
