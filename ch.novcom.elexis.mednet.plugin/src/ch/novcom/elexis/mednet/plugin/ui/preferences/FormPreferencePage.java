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


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

		if(purgeInterval.getText() == null || purgeInterval.getText().isEmpty()){
			MedNet.getSettings().setFormsArchivePurgeInterval(defaultPurgeInterval);
		}
		else {
			MedNet.getSettings().setFormsArchivePurgeInterval(Integer.parseInt(purgeInterval.getText()));
		}
		
		
		MedNet.getSettings().saveSettings();
		this.setErrorMessage(null);
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
