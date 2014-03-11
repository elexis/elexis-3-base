/*******************************************************************************
 * Copyright (c) 2011, Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.views;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.InexistingFileOKFileFieldEditor;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String CONFIG_FILE = "findings/hilotec/configfile"; //$NON-NLS-1$
	
	public void init(IWorkbench workbench){
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new InexistingFileOKFileFieldEditor(CONFIG_FILE, "Konfigurationsdatei", //$NON-NLS-1$
			getFieldEditorParent()));
		
	}
	
	@Override
	public void performApply(){
		CoreHub.localCfg.flush();
	}
	
}
