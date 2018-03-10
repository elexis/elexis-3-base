/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.ui.preferences;


import java.nio.file.Paths;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.WidgetFactory;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * Einstellungen für MedNet Plugin
 */
public class MainPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	Text exePath;
	private Button exePathSelection;
	
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
		if(MedNet.getSettings().getExePath() != null) {
			exePath.setText(MedNet.getSettings().getExePath().toString());
		}
		
		exePathSelection = new Button(ret, SWT.PUSH);
		exePathSelection.setText("...");
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
		MedNet.getSettings().saveSettings();
		return true;
	}
	
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
