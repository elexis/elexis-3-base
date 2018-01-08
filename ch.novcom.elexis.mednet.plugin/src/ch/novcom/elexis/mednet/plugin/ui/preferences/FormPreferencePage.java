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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.WidgetFactory;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * Preference Page for the Form options
 */
public class FormPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	private int defaultPurgeInterval = 30;
	
	private Text  gdtPath;
	private Button gdtPathSelection;
	
	private Text  formsPath;
	private Button formsPathSelection;
	
	private Text  errorPath;
	private Button errorPathSelection;
	
	private Text  archivePath;
	private Button archivePathSelection;
	
	private Text  purgeInterval;
	
	/**
	 * Standard Constructor
	 */
	public FormPreferencePage(){
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
		
		WidgetFactory.createLabel(ret, MedNetMessages.FormPreferences_labelGDTPath);
		gdtPath = new Text(ret, SWT.BORDER);
		gdtPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		gdtPath.setTextLimit(80);
		if(MedNet.getSettings().getFormsGDTPath() != null) {
			gdtPath.setText(MedNet.getSettings().getFormsGDTPath().toString());
		}
		gdtPath.setEnabled(false);
		
		gdtPathSelection = new Button(ret, SWT.PUSH);
		gdtPathSelection.setText("..."); //$NON-NLS-1$
		gdtPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.FormPreferences_labelGDTPath);
		        String selected = dialog.open();
		        gdtPath.setText(selected);
			}
		});
		
		
		WidgetFactory.createLabel(ret, MedNetMessages.FormPreferences_labelFormsPath);
		formsPath = new Text(ret, SWT.BORDER);
		formsPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		formsPath.setTextLimit(80);
		formsPath.setEnabled(false);
		if(MedNet.getSettings().getFormsPath() != null) {
			formsPath.setText(MedNet.getSettings().getFormsPath().toString());
		}
		
		formsPathSelection = new Button(ret, SWT.PUSH);
		formsPathSelection.setText("..."); //$NON-NLS-1$
		formsPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.FormPreferences_labelFormsPath);
		        String selected = dialog.open();
		        formsPath.setText(selected);
			}
		});
		
		WidgetFactory.createLabel(ret, MedNetMessages.FormPreferences_labelErrorPath);
		errorPath = new Text(ret, SWT.BORDER);
		errorPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		errorPath.setTextLimit(80);
		errorPath.setEnabled(false);
		if(MedNet.getSettings().getFormsErrorPath() != null) {
			errorPath.setText(MedNet.getSettings().getFormsErrorPath().toString());
		}
		
		errorPathSelection = new Button(ret, SWT.PUSH);
		errorPathSelection.setText("..."); //$NON-NLS-1$
		errorPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.FormPreferences_labelErrorPath);
		        String selected = dialog.open();
		        errorPath.setText(selected);
			}
		});
		
		WidgetFactory.createLabel(ret, MedNetMessages.FormPreferences_labelArchivePath);
		archivePath = new Text(ret, SWT.BORDER);
		archivePath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		archivePath.setTextLimit(80);
		archivePath.setEnabled(false);
		if(MedNet.getSettings().getFormsArchivePath() != null) {
			archivePath.setText(MedNet.getSettings().getFormsArchivePath().toString());
		}
		
		archivePathSelection = new Button(ret, SWT.PUSH);
		archivePathSelection.setText("..."); //$NON-NLS-1$
		archivePathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.FormPreferences_labelArchivePath);
		        String selected = dialog.open();
		        archivePath.setText(selected);
			}
		});
		

		WidgetFactory.createLabel(ret, MedNetMessages.FormPreferences_labelPurgeInterval);
		purgeInterval = new Text(ret, SWT.BORDER);
		purgeInterval.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		purgeInterval.setTextLimit(80);
		purgeInterval.setText(String.valueOf(MedNet.getSettings().getFormsArchivePurgeInterval()));
		
		purgeInterval.addVerifyListener(new VerifyListener() {
	        @Override
	        public void verifyText(VerifyEvent e) {

	            Text text = (Text)e.getSource();

	            // get old text and create new text by using the VerifyEvent.text
	            final String oldS = text.getText();
	            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

	            boolean isInteger = true;
	            int newInt = -1;
	            try
	            {
	            	newInt = Integer.parseInt(newS);
	            }
	            catch(NumberFormatException ex)
	            {
	            	isInteger = false;
	            }

	            if(!isInteger || newInt < 0 ){
	                e.doit = false;
	            }
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

		MedNet.getSettings().setFormsGDTPath(Paths.get(gdtPath.getText()));
		MedNet.getSettings().setFormsPath(Paths.get(formsPath.getText()));
		MedNet.getSettings().setFormsErrorPath(Paths.get(errorPath.getText()));
		MedNet.getSettings().setFormsArchivePath(Paths.get(archivePath.getText()));

		if(purgeInterval.getText() == null || purgeInterval.getText().isEmpty()){
			MedNet.getSettings().setFormsArchivePurgeInterval(defaultPurgeInterval);
		}
		else {
			MedNet.getSettings().setFormsArchivePurgeInterval(Integer.parseInt(purgeInterval.getText()));
		}
		
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
		if (mySettings == null)
			mySettings = new MedNetSettings();
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
