/*******************************************************************************
 * Copyright (c) 2006-2011, Daniel Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Lutz - initial implementation
 *    Niklaus Giger - new layout with subdirectories
 *    
 *******************************************************************************/
package ch.elexis.extdoc.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.Hub;
import ch.elexis.preferences.SettingsPreferenceStore;
import ch.elexis.extdoc.Messages;


/**
 * Einstellungen zur Verknüpfung externen Dokumenten
 * 
 * @author Daniel Lutz
 */
public class ExterneDokumente extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public ExterneDokumente(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(Hub.localCfg));
		setDescription(Messages.ExterneDokumente_externe_dokumente);
	}
	
	@Override
	protected void createFieldEditors(){
		DirectoryFieldEditor dfe;
		StringFieldEditor sfe;
		FileFieldEditor ffe;
	
		PreferenceConstants.PathElement[] prefElems = PreferenceConstants.getPrefenceElements();
		for (int j = 0; j < prefElems.length; j++) {
			sfe =
				new StringFieldEditor(prefElems[j].prefName, String.format(
					Messages.ExterneDokumente_shorthand_for_path, j), getFieldEditorParent());
			sfe.setTextLimit(8);
			addField(sfe);
			dfe =
				new DirectoryFieldEditor(prefElems[j].prefBaseDir,
					Messages.ExterneDokumente_path_name_preference, getFieldEditorParent());
			addField(dfe);
		}
		sfe =
			new StringFieldEditor(PreferenceConstants.CONCERNS, Messages.ExterneDokumente_Concerns,
				getFieldEditorParent());
		sfe.setTextLimit(60);
		addField(sfe);
		Composite composite = getFieldEditorParent();
		ffe = new FileFieldEditor(PreferenceConstants.EMAIL_PROGRAM, 
			Messages.ExterneDokumente_email_app,  getFieldEditorParent());
		ffe.getLabelControl(composite).setToolTipText("Programm das zum Verschicken von E-Mails verwendet werden soll, falls leer wird dir URL mailto: verwendet, welche keine Anhänge unterstützt");
		addField(ffe);

	}
	
	public void init(IWorkbench workbench){
		setPreferenceStore(new SettingsPreferenceStore(Hub.localCfg));		
	}
}
