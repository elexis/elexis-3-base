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

public class CstProimmun extends PersistentObject {

    private static final String TABLENAME = "cstprofile_proimmun";
    public static final String VERSIONID = "VERSION";
    public static final String VERSION = "3.0.0";

    final static String create = "CREATE TABLE " + TABLENAME + " ("
	    + "	`ID` VARCHAR(25) NOT NULL,	"
	    + "`lastupdate` BIGINT(20) NULL DEFAULT NULL,	"
	    + "`deleted` CHAR(1) NULL DEFAULT '0',	"
	    + "`datum` CHAR(8) NULL DEFAULT NULL,	"
	    + "`ProfileID` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`Text1` TEXT NULL,	" + "`Text2` TEXT NULL,	"
	    + "`Text3` TEXT NULL,	" + "`Text4` TEXT NULL,	"
	    + "`Tested` SMALLINT(6) NULL DEFAULT '0',	"
	    + "`ToBeTested` SMALLINT(6) NULL DEFAULT '0',	"
	    + "PRIMARY KEY (`ID`)) "
	    + "COLLate='utf8_general_ci' ENGINE=InnoDB;"

	    + "INSERT INTO "
	    + TABLENAME + " (ID, Text1) VALUES ("
	    + JdbcLink.wrap(VERSIONID)
	    + "," + JdbcLink.wrap(VERSION) + ");";

    static {
	addMapping(TABLENAME,
		"profileId=ProfileId",
		"datum=Datum",
		"text1=Text1",
		"text2=Text2",
		"text3=Text3",
		"text4=Text4",
		"tested=Tested",
		"toBeTested=ToBeTested");

	if (!tableExists(TABLENAME)) {
	    createOrModifyTable(create);
	} else {
	    // load a Record whose ID is 'VERSION' there we set ItemID as Value
	    CstProimmun version = load(VERSIONID);

	    VersionInfo vi = new VersionInfo(version.get("text1"));
	    if (vi.isOlder(VERSION)) {
		// we should update eg. with createOrModifyTable(update.sql);
		// And then set the new version
		/**/
		/* TODO: this create seems to be unnecessary in other 
		 * examples of PersistenObject implementations, check this
		 * */
		// there is no version record yet, create it
		if (version.getText1() == null) {
		    version.create(VERSIONID);
		}

		version.set("text1", VERSION);
	    }
	}
    }

    public CstProimmun() {
	//create(null);
    }

    public CstProimmun(final String id) {
	super(id);
    }

    public static CstProimmun load(final String id) {
	if (StringTool.isNothing(id)) {
	    return null;
	}

	return new CstProimmun(id);
    }

    public CstProimmun(String profileId, String datum) {
	CstProimmun existing = getByProfileId(profileId);
	if (existing != null) {
	    throw new IllegalArgumentException(
		    String
			    .format("Mapping for origin id [%s] - already exists can not create multiple instances.", //$NON-NLS-1$
				    profileId));
	}

	create(null);
	set("profileId", profileId);
	set("datum", datum);
    }

    public CstProimmun(String profileId, String datum, String text1, String text2, String text3, String text4) {
	CstProimmun existing = getByProfileId(profileId);
	if (existing != null) {
	    throw new IllegalArgumentException(
		    String
			    .format("Mapping for origin id [%s] - [%s] already exists can not create multiple instances.", //$NON-NLS-1$
				    profileId, text1));
	}

	create(null);
	set("profileId", profileId);
	set("datum", datum);
	set("text1", text1);
	set("text2", text2);
	set("text3", text3);
	set("text4", text4);
    }

    public static CstProimmun getByProfileId(String profileId) {
	Query<CstProimmun> qbe = new Query<CstProimmun>(CstProimmun.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
	qbe.add("profileId", Query.EQUALS, profileId);
	List<CstProimmun> res = qbe.execute();
	if (res.isEmpty()) {
	    return null;
	} else {
	    if (res.size() > 1) {
		throw new IllegalArgumentException(String.format(
			"There is already a category of name [%s] - [%s]", profileId));
	    }
	    return res.get(0);
	}
    }

    @Override
    public boolean delete() {
	return super.delete();
    }

    public void setDatum(String datum) {
	set("datum", datum);
    }

    public String getDatum() {
	return get("datum");
    }

    public void setProfileId(String profileId) {
	set("profileId", profileId);
    }

    public String getProfileId() {
	return get("profileId");
    }

    public void setText1(String text1) {
	set("text1", text1);
    }

    public String getText1() {
	return get("text1");
    }

    public void setText2(String text2) {
	set("text2", text2);
    }

    public String getText2() {
	return get("text2");
    }

    public void setText3(String text3) {
	set("text3", text3);
    }

    public String getText3() {
	return get("text3");
    }

    public void setText4(String text4) {
	set("text4", text4);
    }

    public String getText4() {
	return get("text4");
    }

    public void setTested(int tested) {
	setInt("tested", tested);
    }

    public int getTested() {
	return getInt("tested");
    }

    public int getToBeTested() {
	return getInt("toBeTested");
    }

    public void setToBeTested(int tested) {
	setInt("toBeTested", tested);
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
