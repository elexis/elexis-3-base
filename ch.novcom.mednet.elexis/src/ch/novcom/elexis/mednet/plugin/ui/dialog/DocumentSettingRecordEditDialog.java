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
package ch.novcom.elexis.mednet.plugin.ui.dialog;

import java.nio.file.Paths;

import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.WidgetFactory;
import ch.elexis.core.ui.views.controls.LaborSelectionComposite;
import ch.elexis.data.Kontakt;
import ch.novcom.elexis.mednet.plugin.data.DocumentSettingRecord;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * Dialog for editing or creating a DocumentSetting record
 * @author David Gutknecht
 *
 */
public class DocumentSettingRecordEditDialog extends TitleAreaDialog {
	
	LaborSelectionComposite institutionSelection;
	Text institutionId;
	Text institutionName;
	Text category;
	Text documentPath;
	Text errorPath;
	Text archivingPath;
	Text purgeInterval;
	
	DocumentSettingRecord record;
	
	private Button documentPathSelection;
	private Button errorPathSelection;
	private Button archivingPathSelection;
	
	public DocumentSettingRecordEditDialog(Shell parentShell, DocumentSettingRecord record){
		super(parentShell);
		this.record = record;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		this.getShell().setText(MedNetMessages.DocumentSettingRecordEditDialog_shellTitle);
		this.setTitle(MedNetMessages.DocumentSettingRecordEditDialog_title);
		this.setMessage(MedNetMessages.DocumentSettingRecordEditDialog_message);
		
		Composite result = new Composite(parent, SWT.NONE);
		result.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		result.setLayout(new GridLayout(3, false));
		
		
		/*WidgetFactory.createLabel(ret, MedNetMessages.DocumentSettingRecordEditDialog_labelInstitutionId);
		institutionId = new Text(ret, SWT.BORDER);
		institutionId.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		institutionId.setTextLimit(80);
		
		WidgetFactory.createLabel(ret, MedNetMessages.DocumentSettingRecordEditDialog_labelInstitutionName);
		institutionName = new Text(ret, SWT.BORDER);
		institutionName.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		institutionName.setTextLimit(80);*/
		

		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelInstitution);
		this.institutionSelection = new LaborSelectionComposite(result, SWT.NONE);
		this.institutionSelection.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		

		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelCategory);
		this.category = new Text(result, SWT.BORDER);
		this.category.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		this.category.setTextLimit(80);
		
		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelDocumentPath);
		this.documentPath = new Text(result, SWT.BORDER);
		this.documentPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		this.documentPath.setTextLimit(80);
		this.documentPath.setEnabled(false);
		
		this.documentPathSelection = new Button(result, SWT.PUSH);
		this.documentPathSelection.setText("..."); //$NON-NLS-1$
		this.documentPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.DocumentSettingRecordEditDialog_labelDocumentPath);
		        String selected = dialog.open();
		        documentPath.setText(selected);
			}
		});
		
		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelErrorPath);
		this.errorPath = new Text(result, SWT.BORDER);
		this.errorPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		this.errorPath.setTextLimit(80);

		this.errorPathSelection = new Button(result, SWT.PUSH);
		this.errorPathSelection.setText("..."); //$NON-NLS-1$
		this.errorPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.DocumentSettingRecordEditDialog_labelErrorPath);
		        String selected = dialog.open();
		        errorPath.setText(selected);
			}
		});
		
		
		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelArchivingPath);
		this.archivingPath = new Text(result, SWT.BORDER);
		this.archivingPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		this.archivingPath.setTextLimit(80);
		
		this.archivingPathSelection = new Button(result, SWT.PUSH);
		this.archivingPathSelection.setText("..."); //$NON-NLS-1$
		this.archivingPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.DocumentSettingRecordEditDialog_labelArchivingPath);
		        String selected = dialog.open();
		        archivingPath.setText(selected);
			}
		});
		

		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelPurgeInterval);
		this.purgeInterval = new Text(result, SWT.BORDER);
		this.purgeInterval.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		this.purgeInterval.setTextLimit(80);
		this.purgeInterval.addVerifyListener(new VerifyListener() {
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
		

		if (record != null) {
			this.institutionId.setText(record.getInstitutionID());
			this.institutionName.setText(record.getInstitutionName());
			this.category.setText(record.getCategory());
			this.documentPath.setText(record.getPath().toString());
			this.errorPath.setText(record.getErrorPath().toString());
			this.archivingPath.setText(record.getArchivingPath().toString());
			this.purgeInterval.setText(String.valueOf(record.getPurgeInterval()));
		}
		
		
		return result;
	}
	
	
	@Override
	protected void okPressed(){
		
		/*if(institutionId.getText() == null || institutionId.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoInstitutionID);
			return;
		}
		if(institutionName.getText() == null || institutionName.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoInstitutionName);
			return;
		}*/
		if(this.institutionSelection.getKontakt() == null){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoInstitution);
			return;
		}
		if(this.category.getText() == null || category.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoCategory);
			return;
		}
		if(this.documentPath.getText() == null || documentPath.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoPath);
			return;
		}
		if(this.errorPath.getText() == null || errorPath.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoErrorPath);
			return;
		}
		if(this.archivingPath.getText() == null || archivingPath.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoArchivingPath);
			return;
		}
		if(this.purgeInterval.getText() == null || purgeInterval.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoPurgeInterval);
			return;
		}
		
		//If we have no record, we should create it
		if (this.record == null) {
			this.record = new DocumentSettingRecord(
					this.institutionSelection.getKontakt().getId(),
					this.institutionSelection.getKontakt().getLabel(),
					/*institutionId.getText(),*/
					/*institutionName.getText(),*/
					this.category.getText(),
					Paths.get(this.documentPath.getText()),
					Paths.get(this.errorPath.getText()),
					Paths.get(this.archivingPath.getText()),
					Integer.parseInt(this.purgeInterval.getText())
			);
			//mapping.persistTransientLabMappings(result);
		}
		else { //Else edit it
			this.record.set(
						new String[]{
							DocumentSettingRecord.FLD_INSTITUTION_ID,
							DocumentSettingRecord.FLD_INSTITUTION_NAME,
							DocumentSettingRecord.FLD_CATEGORY,
							DocumentSettingRecord.FLD_PATH,
							DocumentSettingRecord.FLD_ERROR_PATH,
							DocumentSettingRecord.FLD_ARCHIVING_PATH,
							DocumentSettingRecord.FLD_PURGE_INTERVAL
						}, 
						this.institutionSelection.getKontakt().getId(),
						this.institutionSelection.getKontakt().getLabel(),
						this.category.getText(),
						this.documentPath.getText(),
						this.errorPath.getText(),
						this.archivingPath.getText(),
						String.valueOf(this.purgeInterval.getText())
			);
			
		}
		
		super.okPressed();
	}
	
	public void setInstitutionIdText(String id){
		/*institutionId.setText(string);*/
		this.institutionSelection.setKontakt(Kontakt.load(id));
	}
	
	/*public void setInstitutionNameText(String string){
		institutionName.setText(string);
	}*/

	public void setCategory(String string){
		this.category.setText(string);
	}
	
	public void setReceivingPathText(String string){
		this.documentPath.setText(string);
	}
	public void setErrorPathText(String string){
		this.errorPath.setText(string);
	}
	public void setArchivingPathText(String string){
		this.archivingPath.setText(string);
	}
	public void setpurgeIntervalText(String string){
		this.purgeInterval.setText(string);
	}
	
}
