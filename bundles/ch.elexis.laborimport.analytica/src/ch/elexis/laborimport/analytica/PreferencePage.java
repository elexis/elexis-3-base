/**
 * Copyright (c) 2010 by Niklaus Giger
 * based on importer.java by G. Weirich
 * Adapted from Viollier to Bioanalytica by Daniel Lutz <danlutz@watz.ch>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * From: Laborimport Viollier
 * 
 * Adapted to Bioanalytica by Daniel Lutz <danlutz@watz.ch>
 * Adapted to Risch by Gerry Weirich
 * 
 */

package ch.elexis.laborimport.analytica;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.InexistingDirectoryOKDirectoryFieldEditor;
import ch.elexis.core.ui.preferences.inputs.InexistingFileOKFileFieldEditor;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public static final String BATCH = "B"; //$NON-NLS-1$
	public static final String FTP = "F"; //$NON-NLS-1$
	
	public static final String BATCH_OR_FTP = "F"; //$NON-NLS-1$
	public static final String BATCH_DATEI = ""; //$NON-NLS-1$
	public static final String FTP_HOST = "analytica/ftp_host"; //$NON-NLS-1$
	public static final String FTP_USER = "analytica/ftp_user"; //$NON-NLS-1$
	public static final String FTP_PWD = "analytica/ftp_pwd"; //$NON-NLS-1$
	public static final String DL_DIR = "analytica/downloaddir"; //$NON-NLS-1$
	public static final String OVPN_DIR = "/etc/openvpn/openvpn.cfg"; //$NON-NLS-1$
	
	public static final String DEFAULT_FTP_HOST = "172.23.45.1"; //$NON-NLS-1$
	public static final String DEFAULT_FTP_USER = ""; //$NON-NLS-1$
	public static final String DEFAULT_FTP_PWD = ""; //$NON-NLS-1$
	public static final String DEFAULT_DL_DIR = "C:\\LaborDownloads"; //$NON-NLS-1$
	public static final String DEFAULT_OVPN_DIR = "C:\\Programme\\OpenVPN\\config\\praxis.ovpn"; //$NON-NLS-1$
	
	ConfigServicePreferenceStore prefs = new ConfigServicePreferenceStore(Scope.GLOBAL);
	
	public PreferencePage(){
		super(GRID);
		prefs.setDefault(FTP_HOST, DEFAULT_FTP_HOST); //$NON-NLS-1$
		prefs.setDefault(FTP_USER, DEFAULT_FTP_USER); //$NON-NLS-1$
		prefs.setDefault(FTP_PWD, DEFAULT_FTP_PWD); //$NON-NLS-1$
		
		prefs.setDefault(OVPN_DIR, DEFAULT_OVPN_DIR); //$NON-NLS-1$
		prefs.setDefault(DL_DIR, DEFAULT_DL_DIR); //$NON-NLS-1$
		prefs.setDefault(BATCH_DATEI, ""); //$NON-NLS-1$
		prefs.setDefault(BATCH_OR_FTP, FTP); //$NON-NLS-1$
		
		setPreferenceStore(prefs);
		
		setDescription(Messages.PreferencePage_title_description); //$NON-NLS-1$
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new StringFieldEditor(FTP_HOST,
			Messages.PreferencePage_label_host, getFieldEditorParent())); //$NON-NLS-1$
		addField(new StringFieldEditor(FTP_USER,
			Messages.PreferencePage_label_user, getFieldEditorParent())); //$NON-NLS-1$
		addField(new StringFieldEditor(FTP_PWD,
			Messages.PreferencePage_label_password, getFieldEditorParent())); //$NON-NLS-1$
		addField(new InexistingDirectoryOKDirectoryFieldEditor(DL_DIR,
			Messages.PreferencePage_label_download, getFieldEditorParent())); //$NON-NLS-1$
		addField(new InexistingFileOKFileFieldEditor(OVPN_DIR,
			Messages.PreferencePage_label_ovpn, getFieldEditorParent())); //$NON-NLS-1$
	}
	
	public void init(final IWorkbench workbench){
		// Do nothing
	}
}
