/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.data;
import java.util.List;
/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 * DB Object for cstgroup_labitem_joint
 */

import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;

public class CstAbstract extends PersistentObject {
	
	private static final String TABLENAME = "cstlaboritem_abstracts";
	public static final String VERSIONID = "VERSION"; 
	public static final String VERSION = "3.0.0";

	static final String create =(
			"CREATE TABLE `" + TABLENAME + "` (" +
					"  `ID` varchar(25) NOT NULL," +
					"  `lastupdate` bigint(20) DEFAULT NULL," +
					"  `deleted` char(1) DEFAULT '0'," +
					"  `ItemID` varchar(25) DEFAULT NULL," +
					"  `Description1` varchar(1024) DEFAULT NULL," +
					"  `Description2` varchar(1024) DEFAULT NULL," +
					"  PRIMARY KEY (`ID`)) "
					+ "COLLATE='utf8_general_ci' ENGINE=InnoDB;"
					+ "INSERT INTO " + TABLENAME
					+ " (ID, description1) VALUES ("
					+ JdbcLink.wrap(VERSIONID) + "," + JdbcLink.wrap(VERSION) + ")"
			);
	
	
	static {
		addMapping(TABLENAME, 
				"itemId=ItemID", 
				"description1=Description1", 
				"description2=Description2"
				);
	
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(create);
			log.debug("Creating table:\r\n" + create);

		} else {
			// load a Record whose ID is 'VERSION' there we set ItemID as Value
			CstAbstract version = load(VERSIONID);
			
			VersionInfo vi = new VersionInfo(version.get("description1"));
			if (vi.isOlder(VERSION)) {
				// we should update eg. with createOrModifyTable(update.sql);
				// And then set the new version
				/**/
				/* TODO: this create seems to be unnecessary in other 
				 * examples of PersistenObject implementations, check this
				 * */
				// there is no version record yet, create it
				if (version.getDescription1() == null) {
					version.create(VERSIONID);
				}
				
				version.set("description1", VERSION);
			}
		}
	}
	public CstAbstract(){
	}
	
	public CstAbstract(final String id){
		super(id);
	}
	
	public static CstAbstract load(final String id){
		if (StringTool.isNothing(id)) {
			return null;
		}

		return new CstAbstract(id);
	}
	
	public CstAbstract(String itemID, String description1, String description2){
		CstAbstract existing = getByLaboritemId(itemID);
		if (existing != null) {
			throw new IllegalArgumentException(
				String
					.format(
						"Mapping for origin id [%s] - [%s] already exists can not create multiple instances.", //$NON-NLS-1$
						itemID, description1));
		}
		
		create(null);
		set("itemId", itemID);
		set("description1", description1);
		set("description2", description2);		
	}


	
	public static CstAbstract getByLaboritemId(String itemId){
		Query<CstAbstract> qbe = new Query<CstAbstract>(CstAbstract.class);
		qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
		qbe.add("itemId", Query.EQUALS, itemId); 
		List<CstAbstract> res = qbe.execute();
		if (res.isEmpty()) {
			return null;
		} else {
			if (res.size() > 1) {
				throw new IllegalArgumentException(String.format(
					"There is already a category of name [%s] - [%s]", itemId));
			}
			return res.get(0);
		}
	}
	 
	
	

	
	@Override
	public boolean delete(){		
		return super.delete();
	}


	public void setItemId(String itemId) {
		set("itemId", itemId);
	}
	public String getItemId() {
		return get("itemId");
	}
	

	public void setDescription1(String description1) {
		set("description1", description1);
	}
	public String getDescription1() {
		return get("description1");
	}

	public void setDescription2(String description2) {
		set("description1", description2);
	}
	public String getDescription2() {
		return get("description1");
	}


	
	
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}
	// for the View content provider
	public Object getParent() {
		return new Object();
	}

}
