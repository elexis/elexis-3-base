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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
	private Combo mednetIDSelection;
	private List<MedNetItem> mednetItems;
	private Text category_doc;
	private Text category_form;
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
		

		WidgetFactory.createLabel(result, MedNetMessages.ContactLinkRecordEditDialog_labelMedNet);
		this.mednetIDSelection = new Combo(result, SWT.READ_ONLY);
		this.mednetIDSelection.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		mednetItems = new ArrayList<MedNetItem>();
		
		for(Entry<String,String> mednetEntry : MedNet.getSettings().getInstitutions().entrySet()) {
			mednetItems.add(new MedNetItem(mednetEntry.getKey(),mednetEntry.getValue()));
		}
		Collections.sort(mednetItems);
		
		String[] textList = new String[mednetItems.size()];
		for(int i= 0; i< mednetItems.size(); i++) {
			textList[i]= mednetItems.get(i).getName();
		}
		this.mednetIDSelection.setItems(textList);
		
		WidgetFactory.createLabel(result, MedNetMessages.ContactLinkRecordEditDialog_labelContact);
		this.contactSelection = new KontaktSelectionComposite(result, SWT.NONE);
		this.contactSelection.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		WidgetFactory.createLabel(result, MedNetMessages.ContactLinkRecordEditDialog_labelCategoryDoc);
		this.category_doc = new Text(result, SWT.BORDER);
		this.category_doc.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		this.category_doc.setTextLimit(80);
		

		WidgetFactory.createLabel(result, MedNetMessages.ContactLinkRecordEditDialog_labelCategoryForm);
		this.category_form = new Text(result, SWT.BORDER);
		this.category_form.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		this.category_form.setTextLimit(80);
		

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
		
		if (this.record != null) {
			Kontakt contact = Kontakt.load(this.record.getContactID());
			this.contactSelection.setKontakt(contact);
			for(int i=0; i<mednetItems.size(); i++) {
				if(mednetItems.get(i).getId().equals(this.record.getMedNetID())) {
					this.mednetIDSelection.select(i);
					break;
				}
			}
			this.category_doc.setText(this.record.getCategoryDoc());
			this.category_form.setText(this.record.getCategoryForm());
			this.xidDomain.setText(String.valueOf(record.getXIDDomain()));
		}
		else {
			this.mednetIDSelection.deselectAll();;
			this.category_doc.setText("");
			this.category_form.setText("");
			this.xidDomain.setText("");
		}
		
		
		return result;
	}
	
	
	@Override
	protected void okPressed(){
		
		if(this.contactSelection.getKontakt() == null){
			setErrorMessage(MedNetMessages.ContactLinkRecordEditDialog_NoContact);
			return;
		}
		
		if(this.mednetIDSelection.getSelectionIndex() < 0) {
			setErrorMessage(MedNetMessages.ContactLinkRecordEditDialog_NoMedNet);
			return;
		}
		
		
		//If we have no record, we should create it
		if (this.record == null) {
			this.record = new ContactLinkRecord(
					this.contactSelection.getKontakt().getId(),
					this.mednetItems.get(this.mednetIDSelection.getSelectionIndex()).getId(),
					this.category_doc.getText(),
					this.category_form.getText(),
					this.xidDomain.getText()
			);
			//mapping.persistTransientLabMappings(result);
		}
		else { //Else edit it
			this.record.set(
						new String[]{
							ContactLinkRecord.FLD_CONTACT_ID,
							ContactLinkRecord.FLD_MEDNET_ID,
							ContactLinkRecord.FLD_CATEGORY_DOC,
							ContactLinkRecord.FLD_CATEGORY_FORM,
							ContactLinkRecord.FLD_XID_DOMAIN
						}, 
						this.contactSelection.getKontakt().getId(),
						this.mednetItems.get(this.mednetIDSelection.getSelectionIndex()).getId(),
						this.category_doc.getText(),
						this.category_form.getText(),
						this.xidDomain.getText()
			);
			
		}
		
		super.okPressed();
	}
	
	public void setInstitutionIdText(String id){
		this.contactSelection.setKontakt(Kontakt.load(id));
	}
	
	public void setCategoryDoc(String string){
		this.category_doc.setText(string);
	}
	
	public void setCategoryForm(String string){
		this.category_form.setText(string);
	}

	public void setXIDDomainText(String string){
		this.xidDomain.setText(string);
	}
	
	public static class MedNetItem implements Comparable<MedNetItem> {
		
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
	        return this.id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }
	    
	    public String getName() {
	        return this.name;
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
	    

		public int compareTo(MedNetItem other){
			// check for null; put null values at the end
			if (other == null) {
				return -1;
			}

			if(this.getName() != null && other.getName() == null){
				return -1;
			}
			if(this.getName() == null && other.getName() != null){
				return 1;
			}
			
			int comparator = this.getName().compareTo(other.getName());
			if(comparator != 0){
				return comparator;
			}
			
			if(this.getId() != null && other.getId() == null){
				return -1;
			}
			if(this.getId() == null && other.getId() != null){
				return 1;
			}
			
			comparator = this.getId().compareTo(other.getId());
			if(comparator != 0){
				return comparator;
			}
			
			
			return 0;
			
		}

	}
	
}
