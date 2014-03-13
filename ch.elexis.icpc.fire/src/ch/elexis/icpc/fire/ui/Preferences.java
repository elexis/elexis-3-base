/*******************************************************************************
 * Copyright (c) 2009, SGAM Informatics and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.icpc.fire.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	static final String EXPL_BD_SYST = "BD syst.";
	public static final String CFG_BD_SYST = "icpc/fire/field_bd_syst";
	static final String EXPL_BD_DIAST = "BD diast.";
	public static final String CFG_BD_DIAST = "icpc/fire/field_bd_diast";
	static final String EXPL_PULS = "Puls";
	public static final String CFG_PULS = "icpc/fire/field_puls";
	static final String EXPL_HEIGHT = "Grösse";
	public static final String CFG_HEIGHT = "icpc/fire/field_groesse";
	static final String EXPL_BU = "Bauchumdang";
	public static final String CFG_BU = "icpc/fire/field_bu";
	static final String EXPL_WEIGHT = "Gewicht";
	public static final String CFG_WEIGHT = "icpc/fire/field_gewicht";
	
	public Preferences(){
		super("Fire", GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		Label expl = new Label(getFieldEditorParent(), SWT.WRAP);
		expl.setText("Geben Sie bitte für jedes der folgenden Felder\nan, wie der entsprechende Befund definiert ist.\nVerwenden Sie dafür die Notation Name:Feld");
		expl.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		addField(new StringFieldEditor(CFG_BD_SYST, EXPL_BD_SYST, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_BD_DIAST, EXPL_BD_DIAST, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_PULS, EXPL_PULS, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_HEIGHT, EXPL_HEIGHT, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_WEIGHT, EXPL_WEIGHT, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_BU, EXPL_BU, getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void performApply(){
		super.performApply();
		CoreHub.globalCfg.flush();
	}
	
}
