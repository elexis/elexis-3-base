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

public class CstProfile extends PersistentObject implements Comparable<CstProfile> {

    private static final String TABLENAME = "cstprofiles";
    public static final String VERSIONID = "VERSION";
    public static final String VERSION = "3.0.0";
    public static final String GROUP_ITEM_TABLENAME = "cstgroup_profile_joint";
    public static final String ITEMRANKING = "itemsRanking";
    public static final String ANZEIGETYP_RASTER = "Zeitraster";
    public static final String ANZEIGETYP_MINIMAX = "Minimal/Maximal";
    public static final String ANZEIGETYP_EFFEKTIV = "Effektiv";
    public static final String KEY_AUSWAHLBEFUNDE = "Auswahlbefunde";
    private JdbcLink j = getConnection();

    final static String create = "CREATE TABLE " + TABLENAME + " ("
	    + "`ID` VARCHAR(25) NOT NULL,	"
	    + "`lastupdate` BIGINT(20) NULL DEFAULT NULL,	"
	    + "`deleted` CHAR(1) NULL DEFAULT '0',	"
	    + "`KontaktID` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`MandantID` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`Name` VARCHAR(256) NULL DEFAULT NULL, "
	    + "`Description` VARCHAR(256) NULL DEFAULT NULL,	"
	    + "`Icon` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`ValidFrom` CHAR(8) NULL DEFAULT NULL,	"
	    + "`ValidTo` CHAR(8) NULL DEFAULT NULL,	"
	    + "`Active` CHAR(1) NULL DEFAULT '1',	"
	    + "`Template` CHAR(1) NULL DEFAULT '0',	"
	    + "`OutputHeader` VARCHAR(256) NULL DEFAULT NULL, "
	    + "`AusgabeRichtung` CHAR(1) NULL DEFAULT '0',	"
	    + "`Auswahlbefunde` BLOB NULL,	"
	    + "`itemsRanking` BLOB NULL,	"
	    + "`PlausibilityCheck` char(1) DEFAULT '0', "
	    + "`AnzeigeTyp` VARCHAR(50) NULL DEFAULT NULL,	"
	    + "`CrawlBack` SMALLINT(6) NULL DEFAULT '180',	"
	    + "`DaySpan1` SMALLINT(6) NULL DEFAULT '720',	"
	    + "`DaySpan2` SMALLINT(6) NULL DEFAULT '360',	"
	    + "`DaySpan3` SMALLINT(6) NULL DEFAULT '0',	"
	    + "`Therapievorschlag` TEXT NULL,	"
	    + "`Period1DateStart` CHAR(8) NULL DEFAULT NULL,	"
	    + "`Period1DateEnd` CHAR(8) NULL DEFAULT NULL,	"
	    + "`Period2DateStart` CHAR(8) NULL DEFAULT NULL,	"
	    + "`Period2DateEnd` CHAR(8) NULL DEFAULT NULL,	"
	    + "`Period3DateStart` CHAR(8) NULL DEFAULT NULL,	"
	    + "`Period3DateEnd` CHAR(8) NULL DEFAULT NULL,	"
	    + "PRIMARY KEY (`ID`)) COLLATe='utf8_general_ci' ENGINE=InnoDB; "

	    + "CREATE TABLE `cstgroup_profile_joint` (	"
	    + "`ID` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`deleted` CHAR(1) NULL DEFAULT '0',	"
	    + "`lastupdate` BIGINT(20) NULL DEFAULT NULL,	"
	    + "`CstgroupID` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`ProfileID` VARCHAR(25) NULL DEFAULT NULL,	"
	    + "`Comment` TEXT NULL,	"
	    + "UNIQUE INDEX `CstgroupID` (`CstgroupID`, `ProfileID`)) "
	    + "COLLAte='utf8_general_ci' ENGINE=InnoDB; "
	    + "INSERT INTO "
	    + TABLENAME +
	    " (ID, name) VALUES (" + JdbcLink.wrap(VERSIONID)
	    + "," + JdbcLink.wrap(VERSION) + ");";

    static {
	addMapping(TABLENAME,
		"name=Name",
		"description=Description",
		"icon=Icon",
		"kontaktId=KontaktID",
		"mandantId=MandantID",
		"validFrom=ValidFrom",
		"validTo=ValidTo",
		"active=Active",
		"template=Template",
		"outputHeader=OutputHeader",
		"ausgabeRichtung=AusgabeRichtung",
		"itemsRanking=ItemsRanking",
		"therapievorschlag=Therapievorschlag",
		"diagnose=Diagnose",
		"anzeigeTyp=AnzeigeTyp",
		"plausibilityCheck=PlausibilityCheck",
		"crawlBack=CrawlBack",
		"daySpan1=DaySpan1",
		"daySpan2=DaySpan2",
		"daySpan3=DaySpan3",
		"period1DateStart=Period1DateStart",
		"period1DateEnd=Period1DateEnd",
		"period2DateStart=Period2DateStart",
		"period2DateEnd=Period2DateEnd",
		"period3DateStart=Period3DateStart",
		"period3DateEnd=Period3DateEnd",
		"CstGroups=JOINT:CstgroupID:ProfileID:cstgroup_profile_joint");

	if (!tableExists(TABLENAME)) {
	    createOrModifyTable(create);
	    log.debug("Creating table:\r\n" + create);
	} else {
	    // load a Record whose ID is 'VERSION' there we set ItemID as Value
	    CstProfile version = load(VERSIONID);

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

    public CstProfile() {
	// TODO Auto-generated constructor stub
    }

    public CstProfile(final String id) {
	super(id);
    }

    public static CstProfile load(final String id) {
	return new CstProfile(id);
    }

    public CstProfile(String name, String description, String icon, String kontaktID, String mandantID,
	    String validFrom, String validTo, String active) {
	CstProfile existing = getByNameAndPatientAndMandant(name, kontaktID, mandantID);
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
	set("validFrom", validFrom);
	set("validTo", validTo);
	set("active", active);

    }

    public CstProfile(String name, String description, String icon, String mandantID) {
	//CstGroup existing = getByNameAndPatientAndMandant(name, kontaktID, mandantID);
	CstProfile existing = getByNameAndMandant(name, mandantID);
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

    public static CstProfile getByNameAndPatientAndMandant(String name, String kontaktId, String mandantId) {
	Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
	qbe.add("name", Query.EQUALS, name);
	qbe.add("kontaktId", Query.EQUALS, kontaktId);
	qbe.add("mandantId", Query.EQUALS, mandantId);
	List<CstProfile> res = qbe.execute();
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

    public static CstProfile getByNameAndMandant(String name, String mandantId) {
	Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
	qbe.add("name", Query.EQUALS, name);
	qbe.add("mandantId", Query.EQUALS, mandantId);
	List<CstProfile> res = qbe.execute();
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

    /*
    public static CstProfile getByNameAndPatient(String name, String kontaktId) {
    Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
    qbe.add("ID", Query.NOT_EQUAL, VERSIONID); //$NON-NLS-1$
    qbe.add("name", Query.EQUALS, name);
    qbe.add("profileId", Query.EQUALS, profileId);
    List<CstProfile> res = qbe.execute();
    if (res.isEmpty()) {
        return null;
    } else {
        if (res.size() > 1) {
    	throw new IllegalArgumentException(String.format(
    		"There is already a category of name [%s] - [%s]", name));
        }
        return res.get(0);
    }
    }*/

    /*
    public static List<CstProfile> getByLabItemId(String labItemId) {
    Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
    qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
    qbe.add("itemId", Query.EQUALS, labItemId);
    return qbe.execute();
    }*/

    /*
    public static List<CstProfile> getCstGroups(Patient patient) {
    Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
    qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
    qbe.add("kontaktId", Query.EQUALS, patient.getId());
    return qbe.execute();
    }*/

    public static List<CstProfile> getCstGroups(Patient patient, String mandantId) {
	Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
	qbe.add("mandantId", Query.EQUALS, mandantId);
	qbe.add("kontaktId", Query.EQUALS, patient.getId());
	return qbe.execute();
    }

    public static List<CstProfile> getAllProfiles(String mandantId) {
	Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
	qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
	qbe.add("mandantId", Query.EQUALS, mandantId);
	qbe.orderBy(false, new String[] { "name" });
	return qbe.execute();
    }

    /*
    public static List<CstProfile> getCstGroups() {
    Query<CstProfile> qbe = new Query<CstProfile>(CstProfile.class);
    qbe.add("ID", Query.NOT_EQUAL, VERSIONID);
    return qbe.execute();
    }*/

    /*
     * TODO: this should be renamed, on this level, the objects are CstGroup instances
     * so getCstGroups would be adequate
     */
    public List<CstGroup> getCstGroups() {
	List<String[]> lResp = getList("CstGroups", new String[0]);
	ArrayList<CstGroup> ret = new ArrayList<CstGroup>(lResp.size());
	for (String[] r : lResp) {
	    ret.add(CstGroup.load(r[0]));
	}
	return ret;
    }

    /*
     * TODO: this should be renamed, on this level, the objects are CstGroup instances
     * so removeCstGroup would be adequate
     */
    public void removeCstGroup(final CstGroup a) {
	for (CstGroup labitem : getCstGroups()) {
	    if (labitem.getId().equalsIgnoreCase(a.getId()))
		removeFromList("CstGroups", a.getId());
	}
    }

    public void addItem(CstGroup item) {
	if (item != null && (item.state() == EXISTS)) {

	    // add item if it doesn't yet exists
	    String exists =
		    j.queryString("SELECT CstgroupID FROM " + GROUP_ITEM_TABLENAME + " WHERE ProfileID = "
			    + getWrappedId() + " AND CstgroupID = " + item.getWrappedId());
	    if (StringTool.isNothing(exists)) {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO " + GROUP_ITEM_TABLENAME + " (ProfileID, CstgroupID) VALUES (")
			.append(getWrappedId()).append(",").append(item.getWrappedId()).append(")");
		j.exec(sql.toString());
	    }
	    else {
		log.error("CSTGroup " + item.getName() + " already exists in CSTProfile :" + this.getName());
		throw new IllegalArgumentException(String.format(
			"CSTGroup of name [%s] - [%s] already exists", item.getName()));

	    }
	}
    }

    public void addItems(List<CstGroup> items) {
	if (items != null) {
	    for (CstGroup item : items) {
		addItem(item);
	    }
	}
    }

    @Override
    public boolean delete() {
	getConnection().exec(
		"DELETE FROM " + GROUP_ITEM_TABLENAME + " WHERE ProfileID =" + getWrappedId());
	return super.delete();
    }

    public void setTherapievorschlag(String therapieVorschlag) {
	set("therapievorschlag", therapieVorschlag);
    }

    public String getTherapievorschlag() {
	return get("therapievorschlag");
    }

    public void setDiagnose(String diagnose) {
	set("diagnose", diagnose);
    }

    public String getDiagnose() {
	return get("diagnose");
    }

    public void setPlausibilityCheck(String plausibilitycheck) {
	set("plausibilityCheck", plausibilitycheck);
    }

    public String getPlausibilityCheck() {
	return get("plausibilityCheck");
    }

    public void setName(String name) {
	set("name", name);
    }

    public String getName() {
	return get("name");
    }

    public void setAnzeigeTyp(String anzeigeTyp) {
	set("anzeigeTyp", anzeigeTyp);
    }

    public String getAnzeigeTyp() {
	return get("anzeigeTyp");
    }

    public void setOutputHeader(String outputHeader) {
	set("outputHeader", outputHeader);
    }

    public String getOutputHeader() {
	return get("outputHeader");
    }

    // true = a4quer, false = a4hoch
    public void setAusgabeRichtung(boolean ausgabeRichtung) {
	if (ausgabeRichtung) {
	    set("ausgabeRichtung", "1");
	} else {
	    set("ausgabeRichtung", "0");
	}
    }

    /**
     * 
     * @return true if A4 Quer, false if A4 Hoch
     */
    public boolean getAusgabeRichtung() {
	//return get("ausgabeRichtung");
	if (get("ausgabeRichtung").equals("0")) {
	    return false;
	}
	else {
	    return true;
	}
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

    public void setCrawlBack(int crawlBack) {
	setInt("crawlBack", crawlBack);
    }

    public int getCrawlBack() {
	return getInt("crawlBack");
    }

    public void setDaySpan1(int daySpan1) {
	setInt("daySpan1", daySpan1);
    }

    public int getDaySpan1() {
	return getInt("daySpan1");
    }

    public void setDaySpan2(int daySpan2) {
	setInt("daySpan2", daySpan2);
    }

    public int getDaySpan2() {
	return getInt("daySpan2");
    }

    public void setDaySpan3(int daySpan3) {
	setInt("daySpan3", daySpan3);
    }

    public int getDaySpan3() {
	return getInt("daySpan3");
    }

    public void setPeriod1DateStart(String period1DateStart) {
	set("period1DateStart", period1DateStart);
    }

    public String getPeriod1DateStart() {
	return get("period1DateStart");
    }

    public void setPeriod1DateEnd(String period1DateEnd) {
	set("period1DateEnd", period1DateEnd);
    }

    public String getPeriod1DateEnd() {
	return get("period1DateEnd");
    }

    public void setPeriod2DateStart(String period2DateStart) {
	set("period2DateStart", period2DateStart);
    }

    public String getPeriod2DateStart() {
	return get("period2DateStart");
    }

    public void setPeriod2DateEnd(String period2DateEnd) {
	set("period2DateEnd", period2DateEnd);
    }

    public String getPeriod2DateEnd() {
	return get("period2DateEnd");
    }

    public void setPeriod3DateStart(String period3DateStart) {
	set("period3DateStart", period3DateStart);
    }

    public String getPeriod3DateStart() {
	return get("period3DateStart");
    }

    public void setPeriod3DateEnd(String period3DateEnd) {
	set("period3DateEnd", period3DateEnd);
    }

    public String getPeriod3DateEnd() {
	return get("period3DateEnd");
    }

    public void setIcon(byte[] icon) {
	setBinary("icon", icon);
    }

    public byte[] getIcon() {
	return getBinary("icon");
    }

    public void setValidFrom(String validFrom) {
	set("validFrom", validFrom);
    }

    public void setValidTo(String validTo) {
	set("validTo", validTo);
    }

    public void setActive(String active) {
	set("active", active);
    }

    public void setTemplate(String template) {
	set("template", template);
    }

    public String getValidFrom() {
	return get("validFrom");
    }

    public String getValidTo() {
	return get("validTo");
    }

    public String getActive() {
	return get("active");
    }

    public String getTemplate() {
	return get("template");
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

    @Override
    public int compareTo(CstProfile profile) {
	//return profile.getName().compareTo(this.getName());
	return this.getName().compareTo(profile.getName());

    }

}
