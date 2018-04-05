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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.jface.viewers.ComboViewer;
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
import ch.novcom.elexis.mednet.plugin.data.ContactLinkRecord;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * Dialog for editing or creating a ContactLink record
 *
 */
public class ContactLinkRecordEditDialog extends TitleAreaDialog {
	
	//private LaborSelectionComposite institutionSelection;
	private KontaktSelectionComposite contactSelection;
	private ComboViewer mednetIDSelection;
	private Text category;
	private Text xidDomain;
	
	private ContactLinkRecord record;
	
	public ContactLinkRecordEditDialog(Shell parentShell, ContactLinkRecord record){
		super(parentShell);
		this.record = record;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		this.getShell().setText(MedNetMessages.ContactLinkRecordEditDialog_shellTitle);
		this.setTitle(MedNetMessages.ContactLinkRecordEditDialog_title);
		this.setMessage(MedNetMessages.ContactLinkRecordEditDialog_message);
		
		Composite result = new Composite(parent, SWT.NONE);
		result.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		result.setLayout(new GridLayout(3, false));
		
		WidgetFactory.createLabel(result, MedNetMessages.ContactLinkRecordEditDialog_labelInstitution);
		this.contactSelection = new KontaktSelectionComposite(result, SWT.NONE);
		this.contactSelection.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		this.mednetIDSelection = new ComboViewer(result, SWT.READ_ONLY);
		this.mednetIDSelection.setContentProvider(ArrayContentProvider.getInstance());
		
		/* if the current institution is selected, show text */
		this.mednetIDSelection.setLabelProvider(new LabelProvider() {
	        @Override
	        public String getText(Object element) {
	            if (element instanceof MedNetItem) {
	            	MedNetItem current = (MedNetItem) element;

	                if(current.isSelected())
	                    return current.getName();
	                else
	                    return "";
	            }
	            return super.getText(element);
	        }
	    });
	    
	    List<MedNetItem> mednetItems = new ArrayList<MedNetItem>();
	    for(Entry<String,String> item : MedNet.getSettings().getInstitutions().entrySet()) {
	    	mednetItems.add(new MedNetItem(item.getKey(), item.getValue()));
	    }
	    
	    this.mednetIDSelection.setInput(mednetItems);

	    /* within the selection event, tell the object it was selected */
	    this.mednetIDSelection.addSelectionChangedListener(new ISelectionChangedListener() {
	        @Override
	        public void selectionChanged(SelectionChangedEvent event) {
	            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
	            MedNetItem item = (MedNetItem)selection.getFirstElement();

	            for(MedNetItem i : MedNetItem)
	                i.setSelected(false);

	            item.setSelected(true);

	            this.mednetIDSelection.refresh();
	        }
	    });

	    this.mednetIDSelection.setSelection(new StructuredSelection(this.mednetIDSelection.getElementAt(0)), true);
	    
	    
		WidgetFactory.createLabel(result, MedNetMessages.ContactLinkRecordEditDialog_labelCategory);
		this.category = new Text(result, SWT.BORDER);
		this.category.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		this.category.setTextLimit(80);
		

		WidgetFactory.createLabel(result, MedNetMessages.ContactLinkRecordEditDialog_labelXIDDomain);
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
			this.xidDomain.setText(String.valueOf(record.getXIDDomain()));
		}
		else {
			this.purgeInterval.setText(String.valueOf(ContactLinkRecord.DEFAULT_PURGE_INTERVAL));
			this.xidDomain.setText("");
		}
		
		
		return result;
	}
	
	
	@Override
	protected void okPressed(){
		
		if(this.contactSelection.getKontakt() == null){
			setErrorMessage(MedNetMessages.ContactLinkRecordEditDialog_NoInstitution);
			return;
		}
		if(this.category.getText() == null || category.getText().isEmpty()){
			setErrorMessage(MedNetMessages.ContactLinkRecordEditDialog_NoCategory);
			return;
		}
		
		//If we have no record, we should create it
		if (this.record == null) {
			this.record = new ContactLinkRecord(
					this.contactSelection.getKontakt().getId(),
					((MedNetItem)(this.mednetIDSelection.getSelection().getFirstElement())).getId(),
					this.category.getText(),
					this.xidDomain.getText()
			);
			//mapping.persistTransientLabMappings(result);
		}
		else { //Else edit it
			this.record.set(
						new String[]{
							ContactLinkRecord.FLD_CONTACT_ID,
							ContactLinkRecord.FLD_MEDNET_ID,
							ContactLinkRecord.FLD_CATEGORY,
							ContactLinkRecord.FLD_XID_DOMAIN
						}, 
						this.contactSelection.getKontakt().getId(),
						((MedNetItem)(this.mednetIDSelection.getSelection().getFirstElement())).getId(),
						this.category.getText(),
						this.xidDomain.getText()
			);
			
		}
		
		super.okPressed();
	}
	
	public void setInstitutionIdText(String id){
		this.contactSelection.setKontakt(Kontakt.load(id));
	}
	
	public void setCategory(String string){
		this.category.setText(string);
	}

	public void setXIDDomainText(String string){
		this.xidDomain.setText(string);
	}
	
	public static class MedNetItem {
		
	    private String id;
	    private String name;

	    /* this will be true for the selected person */
	    boolean isSelected;

	    public MedNetItem(String id, String name) {
	        this.id = id;
	        this.name = name;
	        this.setSelected(false);
	    }
	    
	    public String getId() {
	        return name;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }
	    
	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public boolean isSelected() {
	        return isSelected;
	    }

	    public void setSelected(boolean isSelected) {
	        this.isSelected = isSelected;
	    }

	}
	
}
