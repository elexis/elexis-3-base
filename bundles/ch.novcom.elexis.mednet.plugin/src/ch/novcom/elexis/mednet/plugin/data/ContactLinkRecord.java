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

package ch.novcom.elexis.mednet.plugin.data;

import java.text.MessageFormat;
import java.util.List;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * This object represents a ContactLinkRecord from the database
 * This manage the Creation of the Table
 *
 */
public class ContactLinkRecord extends PersistentObject implements Comparable<ContactLinkRecord> {
	/**
	 * Logger used to log all activities of the module
	 */
	//private final static Logger LOGGER = LoggerFactory.getLogger(ContactLinkRecord.class.getName());

	//The different field names
	public static final String FLD_CONTACT_ID = "ContactID";
	public static final String FLD_MEDNET_ID = "MedNetID";
	public static final String FLD_CATEGORY_DOC = "category_doc";
	public static final String FLD_CATEGORY_FORM = "category_form";
	public static final String FLD_XID_DOMAIN = "xid_domain";
	
	static final String TABLENAME = "MEDNET_CONTACTLINK";
	

//We cannot put any Unique index since deleted records are keept in the database and just marked as deleted
// 	private static final String index1SQL =
//			"CREATE UNIQUE INDEX " + ContactLinkRecord.TABLENAME + "_idx_Path on " + ContactLinkRecord.TABLENAME + "(" + ContactLinkRecord.FLD_PATH +");";


	private static final String createTable = "CREATE TABLE "
			+ ContactLinkRecord.TABLENAME + "("
			+ PersistentObject.FLD_ID + " VARCHAR(25) primary key,"
			+ ContactLinkRecord.FLD_CONTACT_ID + " VARCHAR(25),"
			+ ContactLinkRecord.FLD_MEDNET_ID + " VARCHAR(255),"
			+ ContactLinkRecord.FLD_CATEGORY_DOC + " VARCHAR(255),"
			+ ContactLinkRecord.FLD_CATEGORY_FORM + " VARCHAR(255),"
			+ ContactLinkRecord.FLD_XID_DOMAIN + " VARCHAR(255),"
			+ PersistentObject.FLD_LASTUPDATE + " BIGINT,"
			+ PersistentObject.FLD_DELETED + " CHAR(1) default '0'"
			+ ");";
			//+ index1SQL;
	
	@Override
	protected String getTableName(){
		return ContactLinkRecord.TABLENAME;
	}
	
	static {
		addMapping(
			ContactLinkRecord.TABLENAME,
			ContactLinkRecord.FLD_CONTACT_ID,
			ContactLinkRecord.FLD_MEDNET_ID,
			ContactLinkRecord.FLD_CATEGORY_DOC,
			ContactLinkRecord.FLD_CATEGORY_FORM,
			ContactLinkRecord.FLD_XID_DOMAIN
		);
		if(!PersistentObject.tableExists(TABLENAME)){
			ContactLinkRecord.createOrModifyTable(createTable);
		}
	}
	
	public ContactLinkRecord(
			String contactID,
			String mednetID,
			String category_doc,
			String category_form,
			String xidDomain
		){
		
		create(null);
		set(new String[] {
				FLD_CONTACT_ID,
				FLD_MEDNET_ID,
				FLD_CATEGORY_DOC,
				FLD_CATEGORY_FORM,
				FLD_XID_DOMAIN
			}, 
				contactID,
				mednetID,
				category_doc,
				category_form,
				xidDomain
		);
		
	}
	
	public ContactLinkRecord(){
	}
	
	public ContactLinkRecord(String id){
		super(id);
	}
	
	public static ContactLinkRecord load(String id){
		return new ContactLinkRecord(id);
	}
	
	@Override
	public String toString(){
		return getLabel();
	}
	
	public String getContactID(){
		return PersistentObject.checkNull(this.get(ContactLinkRecord.FLD_CONTACT_ID));
	}
	
	public void setContactID(String contactID){
		this.set(ContactLinkRecord.FLD_CONTACT_ID, contactID);
	}
	
	public String getMedNetID(){
		return PersistentObject.checkNull(this.get(ContactLinkRecord.FLD_MEDNET_ID));
	}
	
	public void setMedNetID(String mednetID){
		this.set(ContactLinkRecord.FLD_MEDNET_ID, mednetID);
	}
	

	public String getCategoryDoc(){
		return PersistentObject.checkNull(this.get(ContactLinkRecord.FLD_CATEGORY_DOC));
	}
	
	public void setCategoryDoc(String category){
		this.set(ContactLinkRecord.FLD_CATEGORY_DOC, category);
	}
	
	public String getCategoryForm(){
		return PersistentObject.checkNull(this.get(ContactLinkRecord.FLD_CATEGORY_FORM));
	}
	
	public void setCategoryForm(String category){
		this.set(ContactLinkRecord.FLD_CATEGORY_FORM, category);
	}
	
	public String getXIDDomain(){
		return PersistentObject.checkNull(this.get(ContactLinkRecord.FLD_XID_DOMAIN));
	}
	
	public void setXIDDomain(String xidDomain){
		this.set(ContactLinkRecord.FLD_XID_DOMAIN, xidDomain);
	}
	
	
	@Override
	public String getLabel(){
		String[] fields = {
				ContactLinkRecord.FLD_CONTACT_ID,
				ContactLinkRecord.FLD_MEDNET_ID,
				ContactLinkRecord.FLD_CATEGORY_DOC,
				ContactLinkRecord.FLD_CATEGORY_FORM,
				ContactLinkRecord.FLD_XID_DOMAIN
		};
		String[] vals = new String[fields.length];
		this.get(fields, vals);
		
		return MessageFormat.format(
			MedNetMessages.ContactLinkRecord_Label,
			vals[0],
			vals[1],
			vals[2],
			vals[3],
			vals[4]
		);
		
	}
	
	public int compareTo(ContactLinkRecord other){
		// check for null; put null values at the end
		if (other == null) {
			return -1;
		}
		if(this.getContactID() != null && other.getContactID() == null){
			return -1;
		}
		if(this.getContactID() == null && other.getContactID() != null){
			return 1;
		}
		int comparator = this.getContactID().compareTo(other.getContactID());
		if(comparator != 0){
			return comparator;
		}
		
		if(this.getMedNetID() != null && other.getMedNetID() == null){
			return -1;
		}
		if(this.getMedNetID() == null && other.getMedNetID() != null){
			return 1;
		}
		comparator = this.getMedNetID().compareTo(other.getMedNetID());
		if(comparator != 0){
			return comparator;
		}
		
		return 0;
	}
	
	/**
	 * Get a List of all ContactLinkRecords from the database
	 * 
	 * @return List of {@link ContactLinkRecord}
	 */
	public static List<ContactLinkRecord> getAllContactLinkRecords(){
		Query<ContactLinkRecord> qbe = new Query<ContactLinkRecord>(ContactLinkRecord.class);
		return qbe.execute();
	}
	
	/**
	 * Get a List of ContactLinkRecord matching the specified parameters in the database By specifying null
	 * parameters the ContactLinkRecord selection can be broadened.
	 * 
	 * @param contactId
	 * @param mednetId
	 * @return List of {@link ContactLinkRecord}
	 */
	public static List<ContactLinkRecord> getContactLinkRecord(
			String contactId,
			String mednetId
			){
		Query<ContactLinkRecord> qbe = new Query<ContactLinkRecord>(ContactLinkRecord.class);
		if (contactId != null && contactId.length() > 0) {
			qbe.add(ContactLinkRecord.FLD_CONTACT_ID, "=", contactId);
		}
		if (mednetId != null && mednetId.length() > 0) {
			qbe.add(ContactLinkRecord.FLD_MEDNET_ID, "=", mednetId, true);
		}
		return qbe.execute();
	}
	
	
}
