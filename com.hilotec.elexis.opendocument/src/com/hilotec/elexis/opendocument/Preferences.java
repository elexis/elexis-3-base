/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation
 *
 *******************************************************************************/

package com.hilotec.elexis.opendocument;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.MultilineFieldEditor;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private static final String BASE = "com.hilotec.elexis.ooo.";
	public static final String P_EDITOR = BASE + "editorpath";
	public static final String P_EDITARGS = BASE + "editargs";
	public static final String P_PRINTARGS = BASE + "printargs";
	public static final String P_PDFCONVERTER = BASE + "pdfpath";
	public static final String P_PDFARGS = BASE + "pdfargs";
	public static final String P_WRAPPERSCRIPT = BASE + "wrapperscript";
	
	SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.localCfg);
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(prefs);
		setDescription("Einstellungen für das Hilotec-OpenDocument-Plugin");
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new FileFieldEditor(P_EDITOR, "OpenDocument Editor", getFieldEditorParent()));
		addField(new MultilineFieldEditor(P_EDITARGS, "Argumente zum Editieren", 5, SWT.V_SCROLL,
			true, getFieldEditorParent()));
		addField(new MultilineFieldEditor(P_PRINTARGS, "Argumente zum Drucken", 5, SWT.V_SCROLL,
			true, getFieldEditorParent()));
		addField(new FileFieldEditor(P_PDFCONVERTER, "PDF-Konverter", getFieldEditorParent()));
		addField(new MultilineFieldEditor(P_PDFARGS, "Argumente fuer PDF-Konvertierung", 5,
			SWT.V_SCROLL, false, getFieldEditorParent()));
		addField(new BooleanFieldEditor(P_WRAPPERSCRIPT, "Wrapper Skript aktivieren",
			getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench arg0){
		// TODO Auto-generated method stub
		
	}
	
}
