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
package ch.novcom.elexis.mednet.plugin.ui.dialog;

import java.nio.file.FileSystems;
import java.nio.file.Files;
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
import ch.elexis.core.ui.views.controls.KontaktSelectionComposite;
import ch.elexis.data.Kontakt;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.data.DocumentSettingRecord;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * Dialog for editing or creating a DocumentSetting record
 *
 */
public class DocumentSettingRecordEditDialog extends TitleAreaDialog {
	
	//private LaborSelectionComposite institutionSelection;
	private KontaktSelectionComposite institutionSelection;
	private Text institutionId;
	private Text institutionName;
	private Text category;
	private Text documentPath;
	private Text errorPath;
	private Text archivingPath;
	private Text purgeInterval;
	private Text xidDomain;
	
	private DocumentSettingRecord record;
	
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
		
		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelInstitution);
		this.institutionSelection = new KontaktSelectionComposite(result, SWT.NONE);
		this.institutionSelection.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		

		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelCategory);
		this.category = new Text(result, SWT.BORDER);
		this.category.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		this.category.setTextLimit(80);
		
		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelDocumentPath);
		this.documentPath = new Text(result, SWT.BORDER);
		this.documentPath.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		this.documentPath.setTextLimit(80);
		
		this.documentPathSelection = new Button(result, SWT.PUSH);
		this.documentPathSelection.setText("..."); //$NON-NLS-1$
		this.documentPathSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(MedNetMessages.DocumentSettingRecordEditDialog_labelDocumentPath);
				if(!documentPath.getText().isEmpty()) {
					dialog.setFilterPath(documentPath.getText());	
				}
				else if(	MedNet.getSettings().getExePath() != null ){
					dialog.setFilterPath(MedNet.getSettings().getExePath().getParent().toString()+FileSystems.getDefault().getSeparator()+"interfaces");//$NON-NLS-1$
				}
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

				if(!errorPath.getText().isEmpty()) {
					dialog.setFilterPath(errorPath.getText());	
				}
				else if(!documentPath.getText().isEmpty()) {
					dialog.setFilterPath(documentPath.getText());//$NON-NLS-1$
				}
				else if(	MedNet.getSettings().getExePath() != null ){
					dialog.setFilterPath(MedNet.getSettings().getExePath().getParent().toString()+FileSystems.getDefault().getSeparator()+"interfaces");
				}
				
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

				if(!archivingPath.getText().isEmpty()) {
					dialog.setFilterPath(archivingPath.getText());	
				}
				else if(!documentPath.getText().isEmpty()) {
					dialog.setFilterPath(documentPath.getText());//$NON-NLS-1$
				}
				else if(	MedNet.getSettings().getExePath() != null ){
					dialog.setFilterPath(MedNet.getSettings().getExePath().getParent().toString()+FileSystems.getDefault().getSeparator()+"interfaces");
				}
				
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
		
		

		WidgetFactory.createLabel(result, MedNetMessages.DocumentSettingRecordEditDialog_labelXIDDomain);
		this.xidDomain = new Text(result, SWT.BORDER);
		this.xidDomain.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		this.xidDomain.setTextLimit(80);
		this.xidDomain.addVerifyListener(new VerifyListener() {
	        @Override
	        public void verifyText(VerifyEvent e) {
	            Text text = (Text)e.getSource();
	            // get old text and create new text by using the VerifyEvent.text
	            final String oldS = text.getText();
	            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

	            if ( newS.matches(".*[;#].*") ) {
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
			this.xidDomain.setText(String.valueOf(record.getXIDDomain()));
		}
		else {
			this.purgeInterval.setText(String.valueOf(DocumentSettingRecord.DEFAULT_PURGE_INTERVAL));
			this.xidDomain.setText("");
		}
		
		
		return result;
	}
	
	
	@Override
	protected void okPressed(){
		
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
		if(!Files.isDirectory(Paths.get(this.documentPath.getText()))) {
			setErrorMessage(String.format(MedNetMessages.DocumentSettingRecordEditDialog_NotValidPath,this.documentPath.getText()));
			return;
		}
		if(this.errorPath.getText() == null || errorPath.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoErrorPath);
			return;
		}
		if(!Files.isDirectory(Paths.get(this.errorPath.getText()))) {
			setErrorMessage(String.format(MedNetMessages.DocumentSettingRecordEditDialog_NotValidPath,this.errorPath.getText()));
			return;
		}
		if(this.archivingPath.getText() == null || archivingPath.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoArchivingPath);
			return;
		}
		if(!Files.isDirectory(Paths.get(this.archivingPath.getText()))) {
			setErrorMessage(String.format(MedNetMessages.DocumentSettingRecordEditDialog_NotValidPath,this.archivingPath.getText()));
			return;
		}
		if(this.purgeInterval.getText() == null || this.purgeInterval.getText().isEmpty()){
			setErrorMessage(MedNetMessages.DocumentSettingRecordEditDialog_NoPurgeInterval);
			return;
		}
		
		//If we have no record, we should create it
		if (this.record == null) {
			this.record = new DocumentSettingRecord(
					this.institutionSelection.getKontakt().getId(),
					this.institutionSelection.getKontakt().getLabel(true),
					this.category.getText(),
					Paths.get(this.documentPath.getText()),
					Paths.get(this.errorPath.getText()),
					Paths.get(this.archivingPath.getText()),
					Integer.parseInt(this.purgeInterval.getText()),
					this.xidDomain.getText()
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
							DocumentSettingRecord.FLD_PURGE_INTERVAL,
							DocumentSettingRecord.FLD_XID_DOMAIN
						}, 
						this.institutionSelection.getKontakt().getId(),
						this.institutionSelection.getKontakt().getLabel(true),
						this.category.getText(),
						this.documentPath.getText(),
						this.errorPath.getText(),
						this.archivingPath.getText(),
						String.valueOf(this.purgeInterval.getText()),
						this.xidDomain.getText()
			);
			
		}
		
		super.okPressed();
	}
	
	public void setInstitutionIdText(String id){
		this.institutionSelection.setKontakt(Kontakt.load(id));
	}
	
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
	public void setPurgeIntervalText(String string){
		this.purgeInterval.setText(string);
	}

	public void setXIDDomainText(String string){
		this.xidDomain.setText(string);
	}
	
}
