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

public class CstGastroColo extends PersistentObject {

    private static final String TABLENAME = "cstprofile_gastrocolo";
    public static final String VERSIONID = "VERSION";
    public static final String VERSION = "3.0.0";

    static final String create = "CREATE TABLE `cstprofile_gastrocolo` "
	    + "(`ID` VARCHAR(25) NOT NULL,	"
	    + "`lastupdate` BIGINT(20) NULL DEFAULT NULL,	"
	    + "`deleted` CHAR(1) NULL DEFAULT '0',	"
	    + "`ProfileID` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`DatumGastro` CHAR(8) NULL DEFAULT NULL,	"
	    + "`DatumColo` CHAR(8) NULL DEFAULT NULL,	"
	    + "`Text1` TEXT NULL,	"
	    + "`Text2` TEXT NULL,	"
	    + "`Text3` TEXT NULL,	"
	    + "`Text4` TEXT NULL,	"
	    + "`GastroMakroBefund` CHAR(1) NULL DEFAULT '0',	"
	    + "`GastroHistoBefund` CHAR(1) NULL DEFAULT '0',	"
	    + "`ColoMakroBefund` CHAR(1) NULL DEFAULT '0',	"
	    + "`ColoHistoBefund` CHAR(1) NULL DEFAULT '0',	PRIMARY KEY (`ID`)) "
	    + "CoLLATE='utf8_general_ci' ENGINE=InnoDB;"

	    + "INSERT INTO "
	    + TABLENAME + " (ID, text1) VALUES ("
	    + JdbcLink.wrap(VERSIONID)
	    + "," + JdbcLink.wrap(VERSION) + ")";

    static {
	addMapping(TABLENAME,
		"profileId=ProfileId",
		"datumColo=DatumColo",
		"datumGastro=DatumGastro",
		"text1=Text1",
		"text2=Text2",
		"text3=Text3",
		"text4=Text4",
		"gastroMakroBefund=GastroMakroBefund",
		"gastroHistoBefund=GastroHistoBefund",
		"coloMakroBefund=ColoMakroBefund",
		"coloHistoBefund=ColoHistoBefund");

	if (!tableExists(TABLENAME)) {
	    createOrModifyTable(create);
	    log.debug("Creating table:\r\n" + create);

	} else {
	    // load a Record whose ID is 'VERSION' there we set ItemID as Value
	    CstGastroColo version = load(VERSIONID);

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

    public CstGastroColo() {
	//create(null);
    }

    public CstGastroColo(final String id) {
	super(id);
    }

    public static CstGastroColo load(final String id) {
	if (StringTool.isNothing(id)) {
	    return null;
	}

	return new CstGastroColo(id);
    }

    public CstGastroColo(String profileId, String datumGastro, String datumColo) {
	CstGastroColo existing = getByProfileId(profileId);
	if (existing != null) {
	    throw new IllegalArgumentException(
		    String
			    .format("Mapping for origin id [%s] - [%s] already exists can not create multiple instances.", //$NON-NLS-1$
				    profileId));
	}

	create(null);
	set("profileId", profileId);
	set("datumGastro", datumGastro);
	set("datumColo", datumColo);
    }

    public CstGastroColo(String profileId, String datumGastro, String datumColo, String text1, String text2,
	    String text3, String text4) {
	CstGastroColo existing = getByProfileId(profileId);
	if (existing != null) {
	    throw new IllegalArgumentException(
		    String
			    .format("Mapping for origin id [%s] - [%s] already exists can not create multiple instances.", //$NON-NLS-1$
				    profileId, text1));
	}

	create(null);
	set("profileId", profileId);
	set("datumGastro", datumGastro);
	set("datumColo", datumColo);
	set("text1", text1);
	set("text2", text2);
	set("text3", text3);
	set("text4", text4);
    }

    public static CstGastroColo getByProfileId(String profileId) {
	Query<CstGastroColo> qbe = new Query<CstGastroColo>(CstGastroColo.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
	qbe.add("profileId", Query.EQUALS, profileId);
	List<CstGastroColo> res = qbe.execute();
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

    public void setDatumGastro(String datumGastro) {
	set("datumGastro", datumGastro);
    }

    public String getDatumGastro() {
	return get("datumGastro");
    }

    public void setDatumColo(String datumColo) {
	set("datumColo", datumColo);
    }

    public String getDatumColo() {
	return get("datumColo");
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

    public void setGastroMakroBefund(char gastromakrobefund) {
	set("gastroMakroBefund", String.valueOf(gastromakrobefund));
    }

    public char getGastroMakroBefund() {
	String result = get("gastroMakroBefund");
	if (result.equals("2")) {
	    return '2';
	} else if (result.equals("1")) {
	    return '1';
	} else {
	    return '0';
	}

    }

    public void setGastroHistoBefund(char gastrohistobefund) {
	set("gastroHistoBefund", String.valueOf(gastrohistobefund));
    }

    public char getGastroHistoBefund() {
	String result = get("gastroHistoBefund");
	if (result.equals("2")) {
	    return '2';
	} else if (result.equals("1")) {
	    return '1';
	} else {
	    return '0';
	}
    }

    public void setColoMakroBefund(char colomakrobefund) {
	set("coloMakroBefund", String.valueOf(colomakrobefund));
    }

    public char getColoMakroBefund() {
	String result = get("coloMakroBefund");
	if (result.equals("2")) {
	    return '2';
	} else if (result.equals("1")) {
	    return '1';
	} else {
	    return '0';
	}
    }

    public void setColoHistoBefund(char colohistobefund) {
	set("coloHistoBefund", String.valueOf(colohistobefund));
    }

    public char getColoHistoBefund() {
	String result = get("coloHistoBefund");
	if (result.equals("2")) {
	    return '2';
	} else if (result.equals("1")) {
	    return '1';
	} else {
	    return '0';
	}
    }

    @Override
    public String getLabel() {
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
