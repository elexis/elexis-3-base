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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * This object represents a DocumentSettingRecord from the database
 * This manage the Creation of the Table
 *
 */
public class DocumentSettingRecord extends PersistentObject implements Comparable<DocumentSettingRecord> {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentSettingRecord.class.getName());

	//The different field names
	public static final String FLD_INSTITUTION_ID = "InstitutionID";
	public static final String FLD_INSTITUTION_NAME = "InstitutionName";
	public static final String FLD_CATEGORY = "category";
	public static final String FLD_PATH = "Path";
	public static final String FLD_ERROR_PATH = "ErrorPath";
	public static final String FLD_ARCHIVING_PATH = "ArchivingPath";
	public static final String FLD_PURGE_INTERVAL = "PurgeInterval";
	public static final String FLD_XID_DOMAIN = "xid_domain";
	public static final int DEFAULT_PURGE_INTERVAL = 30;
	
	static final String TABLENAME = "MEDNET_DOCUMENT_SETTINGS";
	

//We cannot put any Unique index on the Path column. Since deleted records are keept in the database and just marked as deleted
// 	private static final String index1SQL =
//			"CREATE UNIQUE INDEX " + DocumentSettingRecord.TABLENAME + "_idx_Path on " + DocumentSettingRecord.TABLENAME + "(" + DocumentSettingRecord.FLD_PATH +");";


	private static final String createTable = "CREATE TABLE "
			+ DocumentSettingRecord.TABLENAME + "("
			+ PersistentObject.FLD_ID + " VARCHAR(25) primary key,"
			+ DocumentSettingRecord.FLD_INSTITUTION_ID + " VARCHAR(25),"
			+ DocumentSettingRecord.FLD_INSTITUTION_NAME + " VARCHAR(255),"
			+ DocumentSettingRecord.FLD_CATEGORY + " VARCHAR(255),"
			+ DocumentSettingRecord.FLD_PATH + " VARCHAR(255),"
			+ DocumentSettingRecord.FLD_ERROR_PATH + " VARCHAR(255),"
			+ DocumentSettingRecord.FLD_ARCHIVING_PATH + " VARCHAR(255),"
			+ DocumentSettingRecord.FLD_PURGE_INTERVAL + " VARCHAR(16),"
			+ DocumentSettingRecord.FLD_XID_DOMAIN + " VARCHAR(255),"
			+ PersistentObject.FLD_LASTUPDATE + " BIGINT,"
			+ PersistentObject.FLD_DELETED + " CHAR(1) default '0'"
			+ ");";
			//+ index1SQL;
	
	@Override
	protected String getTableName(){
		return DocumentSettingRecord.TABLENAME;
	}
	
	static {
		addMapping(
			DocumentSettingRecord.TABLENAME,
			DocumentSettingRecord.FLD_INSTITUTION_ID,
			DocumentSettingRecord.FLD_INSTITUTION_NAME,
			DocumentSettingRecord.FLD_CATEGORY,
			DocumentSettingRecord.FLD_PATH,
			DocumentSettingRecord.FLD_ERROR_PATH,
			DocumentSettingRecord.FLD_ARCHIVING_PATH,
			DocumentSettingRecord.FLD_PURGE_INTERVAL,
			DocumentSettingRecord.FLD_XID_DOMAIN
		);
		if(!PersistentObject.tableExists(TABLENAME)){
			DocumentSettingRecord.createOrModifyTable(createTable);
		}
	}
	
	public DocumentSettingRecord(
			String institutionID,
			String institutionName,
			String category,
			Path path,
			Path errorPath,
			Path archivingPath,
			int purgeInterval,
			String xidDomain
		){
		
		create(null);
		set(new String[] {
				FLD_INSTITUTION_ID,
				FLD_INSTITUTION_NAME,
				FLD_CATEGORY,
				FLD_PATH,
				FLD_ERROR_PATH,
				FLD_ARCHIVING_PATH,
				FLD_PURGE_INTERVAL,
				FLD_XID_DOMAIN
			}, 
			institutionID,
			institutionName,
			category,
			path.toString(),
			errorPath.toString(),
			archivingPath.toString(),
			String.valueOf(purgeInterval),
			xidDomain
		);
		
	}
	
	public DocumentSettingRecord(){
	}
	
	public DocumentSettingRecord(String id){
		super(id);
	}
	
	public static DocumentSettingRecord load(String id){
		return new DocumentSettingRecord(id);
	}
	
	
	public String getInstitutionID(){
		return PersistentObject.checkNull(this.get(DocumentSettingRecord.FLD_INSTITUTION_ID));
	}
	
	public void setInstitutionID(String institutionID){
		this.set(DocumentSettingRecord.FLD_INSTITUTION_ID, institutionID);
	}
	
	public String getInstitutionName(){
		return PersistentObject.checkNull(this.get(DocumentSettingRecord.FLD_INSTITUTION_NAME));
	}
	
	public void setInstitutionName(String institutionName){
		this.set(DocumentSettingRecord.FLD_INSTITUTION_NAME, institutionName);
	}
	

	public String getCategory(){
		return PersistentObject.checkNull(this.get(DocumentSettingRecord.FLD_CATEGORY));
	}
	
	public void setCategory(String category){
		this.set(DocumentSettingRecord.FLD_CATEGORY, category);
	}
	
	
	public Path getPath(){
		return Paths.get(PersistentObject.checkNull(this.get(DocumentSettingRecord.FLD_PATH)));
	}
	
	public void setPath(String path){
		this.set(DocumentSettingRecord.FLD_PATH, path);
	}
	
	public Path getErrorPath(){
		return Paths.get(PersistentObject.checkNull(this.get(DocumentSettingRecord.FLD_ERROR_PATH)));
	}
	
	public void setErrorPath(String errorPath){
		this.set(DocumentSettingRecord.FLD_ERROR_PATH, errorPath);
	}
	
	public Path getArchivingPath(){
		return Paths.get(PersistentObject.checkNull(this.get(DocumentSettingRecord.FLD_ARCHIVING_PATH)));
	}
	
	public void setArchivingPath(String archivingPath){
		this.set(DocumentSettingRecord.FLD_ARCHIVING_PATH, archivingPath);
	}
	
	
	public int getPurgeInterval(){
		String logPrefix = "getPurgeInterval() - ";//$NON-NLS-1$
		String interval = this.get(DocumentSettingRecord.FLD_PURGE_INTERVAL);
		if(interval == null || interval.isEmpty()){
			return DocumentSettingRecord.DEFAULT_PURGE_INTERVAL;
		}
		else {
			try{
				return Integer.parseInt(this.get(DocumentSettingRecord.FLD_PURGE_INTERVAL));
			}
			catch(NumberFormatException nfe){
				LOGGER.warn(logPrefix+"PurgeInterval is not numeric value. "+ interval);//$NON-NLS-1$
				return DocumentSettingRecord.DEFAULT_PURGE_INTERVAL;
			}
		}
	}
	
	public void setPurgeInterval(int purgeInterval){
		this.set(DocumentSettingRecord.FLD_PURGE_INTERVAL, Integer.toString(purgeInterval));
	}
	
	public String getXIDDomain(){
		return PersistentObject.checkNull(this.get(DocumentSettingRecord.FLD_XID_DOMAIN));
	}
	
	public void setXIDDomain(String xidDomain){
		this.set(DocumentSettingRecord.FLD_XID_DOMAIN, xidDomain);
	}
	
	
	@Override
	public String getLabel(){
		String[] fields = {
				DocumentSettingRecord.FLD_INSTITUTION_ID,
				DocumentSettingRecord.FLD_INSTITUTION_NAME,
				DocumentSettingRecord.FLD_CATEGORY,
				DocumentSettingRecord.FLD_PATH,
				DocumentSettingRecord.FLD_ERROR_PATH,
				DocumentSettingRecord.FLD_ARCHIVING_PATH,
				DocumentSettingRecord.FLD_PURGE_INTERVAL,
				DocumentSettingRecord.FLD_XID_DOMAIN
		};
		String[] vals = new String[fields.length];
		this.get(fields, vals);
		
		return MessageFormat.format(
			MedNetMessages.DocumentSettingRecord_Label,
			vals[0],
			vals[1],
			vals[2],
			vals[3],
			vals[4],
			vals[5],
			vals[6],
			vals[7]
		);
		
	}
	
	public int compareTo(DocumentSettingRecord other){
		// check for null; put null values at the end
		if (other == null) {
			return -1;
		}
		if(this.getInstitutionID() != null && other.getInstitutionID() == null){
			return -1;
		}
		if(this.getInstitutionID() == null && other.getInstitutionID() != null){
			return 1;
		}
		int comparator = this.getInstitutionID().compareTo(other.getInstitutionID());
		if(comparator != 0){
			return comparator;
		}
		
		if(this.getInstitutionName() != null && other.getInstitutionName() == null){
			return -1;
		}
		if(this.getInstitutionName() == null && other.getInstitutionName() != null){
			return 1;
		}
		comparator = this.getInstitutionName().compareTo(other.getInstitutionName());
		if(comparator != 0){
			return comparator;
		}
		
		if(this.getCategory() != null && other.getCategory() == null){
			return -1;
		}
		if(this.getCategory() == null && other.getCategory() != null){
			return 1;
		}
		
		return this.getCategory().compareTo(other.getCategory());
	}
	
	/**
	 * Get a List of all DocumentSettingRecords from the database
	 * 
	 * @return List of {@link DocumentSettingRecord}
	 */
	public static List<DocumentSettingRecord> getAllDocumentSettingRecords(){
		Query<DocumentSettingRecord> qbe = new Query<DocumentSettingRecord>(DocumentSettingRecord.class);
		return qbe.execute();
	}
	
	/**
	 * Get a List of DocumentSettingRecord matching the specified parameters in the database By specifying null
	 * parameters the DocumentSettingRecord selection can be broadened.
	 * 
	 * @param institutionId
	 * @param institutionName
	 * @param receivingPath
	 * @param errorPath
	 * @param archivingPath
	 * @return List of {@link DocumentSettingRecord}
	 */
	public static List<DocumentSettingRecord> getDocumentSettingsRecord(
			String institutionId,
			String institutionName,
			String category,
			String receivingPath,
			String errorPath,
			String archivingPath,
			String xidDomain
			){
		Query<DocumentSettingRecord> qbe = new Query<DocumentSettingRecord>(DocumentSettingRecord.class);
		if (institutionId != null && institutionId.length() > 0) {
			qbe.add(DocumentSettingRecord.FLD_INSTITUTION_ID, "=", institutionId);
		}
		if (institutionName != null && institutionName.length() > 0) {
			qbe.add(DocumentSettingRecord.FLD_INSTITUTION_NAME, "=", institutionName, true);
		}
		if (category != null && category.length() > 0) {
			qbe.add(DocumentSettingRecord.FLD_CATEGORY, "=", category, true);
		}
		if (receivingPath != null && receivingPath.length() > 0) {
			qbe.add(DocumentSettingRecord.FLD_PATH, "=", receivingPath, true);
		}
		if (errorPath != null && errorPath.length() > 0) {
			qbe.add(DocumentSettingRecord.FLD_ERROR_PATH, "=", errorPath, true);
		}
		if (archivingPath != null && archivingPath.length() > 0) {
			qbe.add(DocumentSettingRecord.FLD_ARCHIVING_PATH, "=", archivingPath, true);
		}
		if (xidDomain != null && xidDomain.length() > 0) {
			qbe.add(DocumentSettingRecord.FLD_XID_DOMAIN, "=", xidDomain, true);
		}
		return qbe.execute();
	}
	

}
