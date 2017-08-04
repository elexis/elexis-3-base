/*******************************************************************************
 * Copyright (c) 2017 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.ui.preferences;


import java.nio.file.Paths;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.WidgetFactory;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.MedNetSettings;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * Einstellungen für MedNet Plugin
 */
public class MainPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	Text exePath;
	private Button exePathSelection;
	
	/*Text logsPath;
	private Button logsPathSelection;
	
	private Combo logLevel;*/
	/**
	 * Standard Constructor
	 */
	public MainPreferencePage(){
		super(GRID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createContents(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	protected Control createContents(Composite parent){
		
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(3, false));
		
		WidgetFactory.createLabel(ret, MedNetMessages.MainPreferences_labelExePath);
		exePath = new Text(ret, SWT.BORDER);
		exePath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		exePath.setTextLimit(80);
		exePath.setEnabled(false);
		exePath.setText(MedNet.getSettings().getExePath().toString());
		
		exePathSelection = new Button(ret, SWT.PUSH);
		exePathSelection.setText("..."); //$NON-NLS-1$
		exePathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				FileDialog dialog = new FileDialog(getShell());
				dialog.setText(MedNetMessages.MainPreferences_labelExePath);
				if(System.getProperty("os.name").contains("Windows")){
					dialog.setFilterExtensions(new String[] { "*.exe"});
				}
		        String selected = dialog.open();
		        exePath.setText(selected);
			}
		});
		
	/*	WidgetFactory.createLabel(ret, MedNetMessages.MainPreferences_labelLogsPath);
		logsPath = new Text(ret, SWT.BORDER);
		logsPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		logsPath.setTextLimit(80);
		logsPath.setEnabled(false);
		logsPath.setText(MedNet.getSettings().getLogsPath().toString());
		
		logsPathSelection = new Button(ret, SWT.PUSH);
		logsPathSelection.setText("..."); //$NON-NLS-1$
		logsPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.MainPreferences_labelLogsPath);
		        String selected = dialog.open();
		        logsPath.setText(selected);
			}
		});
		
		WidgetFactory.createLabel(ret, MedNetMessages.MainPreferences_labelLogsLevel);
		logLevel = new Combo(ret, SWT.NULL);
		logLevel.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		logLevel.setTextLimit(80);
		logLevel.setEnabled(false);
	    for ( String level : MedNetSettings.getAvailableLogLevels()){
	    	logLevel.add(level);
	    }*/
	    
		
		return ret;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk(){
		MedNet.getSettings().setExePath(Paths.get(exePath.getText()));
	//	MedNet.getSettings().setLogsPath(Paths.get(logsPath.getText()));
		MedNet.getSettings().saveSettings();
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	/*@Override
	public void init(IWorkbench workbench){
		if (mySettings == null){
			mySettings = new MedNetSettings();
		}
	}*/
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors(){
		// TODO Auto-generated method stub
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	

}
