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

import java.util.ArrayList;
import java.util.List;

import ch.elexis.data.LabItem;
import ch.elexis.data.Patient;
/*
 * 
 * DB Object for cstgroup_labitem_joint
 */
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;

/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 */

public class CstGroup extends PersistentObject {

    private static final String TABLENAME = "cstgroups";
    public static final String VERSIONID = "VERSION";
    public static final String VERSION = "3.0.0";
    public static final String GROUP_ITEM_TABLENAME = "cstgroup_labitem_joint";
    public static final String ITEMRANKING = "itemsRanking";
    private JdbcLink j = getConnection();

    static final String create =
	    "CREATE TABLE `cstgroups` ("
		    + "	`ID` VARCHAR(25) NOT NULL,"
		    + "	`lastupdate` BIGINT(20) NULL DEFAULT NULL,"
		    + "	`deleted` CHAR(1) NULL DEFAULT '0',"
		    + "	`KontaktID` VARCHAR(25) NULL DEFAULT NULL,"
		    + "	`MandantID` VARCHAR(25) NULL DEFAULT NULL,"
		    + "	`Name` VARCHAR(30) NULL DEFAULT NULL,"
		    + "	`Description` VARCHAR(256) NULL DEFAULT NULL,"
		    + "	`itemsRanking` BLOB NULL, "
		    + "	`Icon` VARCHAR(25) NULL, "
		    + "	PRIMARY KEY (`ID`)" + ")"
		    + " COlLATE='utf8_general_ci' "
		    + " ENGINE=InnoDB;"
		    + " INSERT INTO " + TABLENAME
		    + " (ID, name) VALUES (" +
		    JdbcLink.wrap(VERSIONID) + ","
		    + JdbcLink.wrap(VERSION) + ");";

    static final String create2 = "CREATE TABLE `cstgroup_labitem_joint` ("
	    + "	`ID` VARCHAR(25) NULL DEFAULT NULL,"
	    + "	`deleted` CHAR(1) NULL DEFAULT '0',"
	    + "	`lastupdate` BIGINT(20) NULL DEFAULT NULL,"
	    + "	`GroupID` VARCHAR(25) NULL DEFAULT NULL,"
	    + "	`ItemID` VARCHAR(25) NULL DEFAULT NULL,"
	    + "	`DisplayOnce` CHAR(1) NOT NULL DEFAULT '0',"
	    + "	`Comment` TEXT NULL,"
	    + "	UNIQUE INDEX `GroupID` (`GroupID`, `ItemID`)" + ")"
	    + " COLlATE='utf8_general_ci' ENGINE=InnoDB;";

    static {
	addMapping(TABLENAME,
		"name=Name",
		"description=Description",
		"icon=Icon",
		"kontaktId=KontaktID",
		"mandantId=MandantID",
		"itemsRanking=ItemsRanking",
		"Labitems=JOINT:ItemID:GroupID:cstgroup_labitem_joint");

	if (!tableExists(TABLENAME)) {
	    createOrModifyTable(create);
	    createOrModifyTable(create2);
	} else {
	    // load a Record whose ID is 'VERSION' there we set ItemID as Value
	    CstGroup version = load(VERSIONID);

	    VersionInfo vi = new VersionInfo(version.get("name"));
	    if (vi.isOlder(VERSION)) {
		// we should update eg. with createOrModifyTable(update.sql);
		// And then set the new version
		/**/
		/* TODO: this create seems to be unnecessary in other 
		 * examples of PersistenObject implementations, check this
		 * */
		// there is no version record yet, create it
		if (version.getName() == null) {
		    version.create(VERSIONID);
		}

		version.set("name", VERSION);
	    }
	}
    }

    public CstGroup() {
	// TODO Auto-generated constructor stub
    }

    public CstGroup(final String id) {
	super(id);
    }

    public static CstGroup load(final String id) {
	return new CstGroup(id);
    }

    public CstGroup(String name, String description, String icon, String kontaktID, String mandantID) {
	CstGroup existing = getByNameAndPatientAndMandant(name, kontaktID, mandantID);
	if (existing != null) {
	    throw new IllegalArgumentException(
		    String
			    .format("Mapping for origin id [%s] - [%s] already exists can not create multiple instances.", //$NON-NLS-1$
				    name, description));
	}

	create(null);
	set("name", name);
	set("description", description);
	set("icon", icon);
	set("kontaktId", kontaktID);
	set("mandantId", mandantID);
    }

    public CstGroup(String name, String description, String icon, String mandantID) {
	CstGroup existing = getByNameAndMandant(name, mandantID);
	if (existing != null) {
	    throw new IllegalArgumentException(
		    String
			    .format("Mapping for origin id [%s] - [%s] already exists can not create multiple instances.", //$NON-NLS-1$
				    name, description));
	}

	create(null);
	set("name", name);
	set("description", description);
	set("icon", icon);
	set("mandantId", mandantID);
    }

    public static CstGroup getByNameAndPatientAndMandant(String name, String kontaktId, String mandantId) {
	Query<CstGroup> qbe = new Query<CstGroup>(CstGroup.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
	qbe.add("name", Query.EQUALS, name);
	qbe.add("kontaktId", Query.EQUALS, kontaktId);
	qbe.add("mandantId", Query.EQUALS, mandantId);
	List<CstGroup> res = qbe.execute();
	if (res.isEmpty()) {
	    return null;
	} else {
	    if (res.size() > 1) {
		throw new IllegalArgumentException(String.format(
			"There is already a category of name [%s] - [%s]", name));
	    }
	    return res.get(0);
	}
    }

    public static CstGroup getByNameAndMandant(String name, String mandantId) {
	Query<CstGroup> qbe = new Query<CstGroup>(CstGroup.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
	qbe.add("name", Query.EQUALS, name);
	qbe.add("mandantId", Query.EQUALS, mandantId);
	List<CstGroup> res = qbe.execute();
	if (res.isEmpty()) {
	    return null;
	} else {
	    if (res.size() > 1) {
		throw new IllegalArgumentException(String.format(
			"There is already a category of name [%s] - [%s]", name));
	    }
	    return res.get(0);
	}
    }

    public static List<CstGroup> getByLabItemId(String labItemId) {
	Query<CstGroup> qbe = new Query<CstGroup>(CstGroup.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
	qbe.add("itemId", Query.EQUALS, labItemId);
	return qbe.execute();
    }

    public static List<CstGroup> getCstGroups(Patient patient) {
	Query<CstGroup> qbe = new Query<CstGroup>(CstGroup.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
	qbe.add("kontaktId", Query.EQUALS, patient.getId());
	return qbe.execute();
    }

    public static List<CstGroup> getCstGroups(Patient patient, String mandantId) {
	Query<CstGroup> qbe = new Query<CstGroup>(CstGroup.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
	qbe.add("mandantId", Query.EQUALS, mandantId);
	qbe.add("kontaktId", Query.EQUALS, patient.getId());
	return qbe.execute();
    }

    public static List<CstGroup> getCstGroups() {
	Query<CstGroup> qbe = new Query<CstGroup>(CstGroup.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
	return qbe.execute();
    }

    public List<LabItemWrapper> getLabitems() {
	List<String[]> lResp = getList("Labitems", new String[] { "displayOnce" });
	ArrayList<LabItemWrapper> ret = new ArrayList<LabItemWrapper>(lResp.size());
	for (String[] r : lResp) {

	    //ret.add(new LabItem.load(r[0]));
	    ret.add(new LabItemWrapper(LabItem.load(r[0]), r[1]));
	    //System.out.println("DisplayOnce;" + r[1]);
	}
	return ret;
    }

    // remove a LabItem from the CST Category
    public void removeLabitem(final LabItem a) {
	for (LabItemWrapper labitem : getLabitems()) {
	    if (labitem.getLabItem().getId().equalsIgnoreCase(a.getId()))
		removeFromList("Labitems", a.getId());
	}
    }

    public int setDisplayOnce(LabItemWrapper labItem, String displayOnce) {
	StringBuffer sql = new StringBuffer();
	sql.append("UPDATE " + GROUP_ITEM_TABLENAME + " SET DisplayOnce = '").append(displayOnce)
		.append("' WHERE GroupID = ").append(getWrappedId())
		.append(" AND ItemId = ").append(labItem.getLabItem().getWrappedId());
	int result = j.exec(sql.toString());
	return result;
    }

    public void addItem(LabItemWrapper item) {
	if (item != null && (item.getLabItem().state() == EXISTS)) {
	    // add item if it doesn't yet exists
	    String exists =
		    j.queryString("SELECT ItemID FROM " + GROUP_ITEM_TABLENAME + " WHERE GroupID = "
			    + getWrappedId() + " AND ItemID = " + item.getLabItem().getWrappedId());
	    if (StringTool.isNothing(exists)) {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO " + GROUP_ITEM_TABLENAME + " (GroupID, ItemID, DisplayOnce) VALUES (")
			.append(getWrappedId()).append(",").append(item.getLabItem().getWrappedId()).append(", ")
			.append(item.getDisplayOnce()).append(")");
		j.exec(sql.toString());
	    }
	    else {
		log.error("Lab item " + item.getLabItem().getName() + " already exists in CSTGroup: " + this.getName());
		throw new IllegalArgumentException(String.format(
			"There is already a category of name [%s] - [%s]", item.getLabItem().getName()));

	    }
	}
    }

    /*
     * add a new labitem to the list of labitems of this group
     */
    /*
    public void addLabitem(final LabItem a){
    	for (LabItem labItem: getLabitems()) {
    		if (labItem.getId().equalsIgnoreCase(a.getId()))
    			return;
    	}
    	addToList("Labitems", a.getId(), (String[]) null);
    }*/
    public void addItems(List<LabItemWrapper> items) {
	if (items != null) {
	    for (LabItemWrapper item : items) {
		addItem(item);
	    }
	}
    }

    @Override
    public boolean delete() {
	getConnection().exec(
		"DELETE FROM  " + GROUP_ITEM_TABLENAME + " WHERE GroupID =" + getWrappedId());
	return super.delete();
    }

    public void setName(String name) {
	set("name", name);
    }

    public String getName() {
	return get("name");
    }

    public void setDescription(String description) {
	set("description", description);
    }

    public String getDescription() {
	return get("description");
    }

    public void setKontaktId(String kontaktId) {
	set("kontaktId", kontaktId);
    }

    public String getKontaktId() {
	return get("kontaktId");
    }

    public void setMandantId(String mandantId) {
	set("mandantId", mandantId);
    }

    public String getMandantId() {
	return get("mandantId");
    }

    public void setIcon(byte[] icon) {
	setBinary("icon", icon);
    }

    public byte[] getIcon() {
	return getBinary("icon");
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
