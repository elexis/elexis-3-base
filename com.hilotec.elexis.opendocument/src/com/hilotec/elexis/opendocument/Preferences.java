package com.hilotec.elexis.opendocument;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;
import ch.elexis.preferences.SettingsPreferenceStore;
import ch.elexis.preferences.inputs.MultilineFieldEditor;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private static final String BASE = "com.hilotec.elexis.ooo.";
	public static final String P_EDITOR = BASE + "editorpath";
	public static final String P_EDITARGS = BASE + "editargs";
	public static final String P_PRINTARGS = BASE + "printargs";
	
	SettingsPreferenceStore prefs = new SettingsPreferenceStore(Hub.localCfg);
	
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
	}
	
	@Override
	public void init(IWorkbench arg0){
		// TODO Auto-generated method stub
		
	}
	
}
