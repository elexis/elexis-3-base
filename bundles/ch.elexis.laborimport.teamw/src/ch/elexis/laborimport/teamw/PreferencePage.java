/**
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     G. Weirich - initial API and implementation
 * Adapted to Bioanalytica by Daniel Lutz <danlutz@watz.ch>
 * Adapted to Risch by Gerry Weirich
 * 
 */

package ch.elexis.laborimport.teamw;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.InexistingDirectoryOKDirectoryFieldEditor;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public static final String BATCH = "B"; //$NON-NLS-1$
	public static final String FTP = "F"; //$NON-NLS-1$
	
	public static final String BATCH_OR_FTP = "F"; //$NON-NLS-1$
	public static final String BATCH_DATEI = ""; //$NON-NLS-1$
	public static final String FTP_HOST = "teamw/ftp_host"; //$NON-NLS-1$
	public static final String FTP_USER = "teamw/ftp_user"; //$NON-NLS-1$
	public static final String FTP_PWD = "teamw/ftp_pwd"; //$NON-NLS-1$
	public static final String DL_DIR = "teamw/downloaddir"; //$NON-NLS-1$
	
	public static final String DOCUMENT_CATEGORY = "teamw/documentcategory";
	
	public static final String DEFAULT_FTP_HOST = "195.144.61.180"; //$NON-NLS-1$
	public static final String DEFAULT_FTP_USER = ""; //$NON-NLS-1$
	public static final String DEFAULT_FTP_PWD = ""; //$NON-NLS-1$
	public static final String DEFAULT_DL_DIR = "/tmp/teamw"; //$NON-NLS-1$
	
	public static final String DEFAULT_DOCUMENT_CATEGORY = "TeamW Laborbefunde";
	
	SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.globalCfg);
	
	public PreferencePage(){
		super(GRID);
		prefs.setDefault(FTP_HOST, DEFAULT_FTP_HOST); //$NON-NLS-1$
		prefs.setDefault(FTP_USER, DEFAULT_FTP_USER); //$NON-NLS-1$
		prefs.setDefault(FTP_PWD, DEFAULT_FTP_PWD); //$NON-NLS-1$
		
		prefs.setDefault(DL_DIR, DEFAULT_DL_DIR); //$NON-NLS-1$
		prefs.setDefault(BATCH_DATEI, ""); //$NON-NLS-1$
		prefs.setDefault(BATCH_OR_FTP, FTP); //$NON-NLS-1$
		
		prefs.setDefault(DOCUMENT_CATEGORY, DEFAULT_DOCUMENT_CATEGORY); //$NON-NLS-1$
		
		setPreferenceStore(prefs);
		
		setDescription(Messages.PreferencePage_title_description); //$NON-NLS-1$
	}
	
	@Override
	protected void createFieldEditors(){
		final Composite parentComp = getFieldEditorParent();
		final RadioGroupFieldEditor groupFieldEditor =
			new RadioGroupFieldEditor(
				BATCH_OR_FTP,
				Messages.PreferencePage_direktimport_label, 2, new String[][] { //$NON-NLS-1$
					{ Messages.PreferencePage_batchscript_label, BATCH}, 
					{ Messages.PreferencePage_ftpserver_label, FTP}},
				parentComp, true);
		final FileFieldEditor batchFileEditor =
			new FileFieldEditor(BATCH_DATEI, Messages.PreferencePage_batchdatei_label, //$NON-NLS-1$
				parentComp);
		
		addField(groupFieldEditor);
		addField(batchFileEditor);
		
		addField(new StringFieldEditor(FTP_HOST,
			Messages.PreferencePage_label_host, getFieldEditorParent())); //$NON-NLS-1$
		addField(new StringFieldEditor(FTP_USER,
			Messages.PreferencePage_label_user, getFieldEditorParent())); //$NON-NLS-1$
		addField(new StringFieldEditor(FTP_PWD,
			Messages.PreferencePage_label_password, getFieldEditorParent())); //$NON-NLS-1$
		addField(new InexistingDirectoryOKDirectoryFieldEditor(DL_DIR,
			Messages.PreferencePage_label_download, getFieldEditorParent())); //$NON-NLS-1$
		addField(new StringFieldEditor(DOCUMENT_CATEGORY,
			Messages.PreferencePage_labelDocumentCategory, getFieldEditorParent()));
	}
	
	public void init(final IWorkbench workbench){
		// Do nothing
	}
}
