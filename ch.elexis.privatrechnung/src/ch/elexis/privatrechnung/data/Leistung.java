/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.privatrechnung.data;

import ch.elexis.data.Fall;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.VerrechenbarAdapter;
import ch.elexis.data.Xid;
import ch.elexis.util.IOptifier;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

/**
 * A billing plugin that is able to manage several arbitrary tax systems
 * 
 * @author gerry
 * 
 */
public class Leistung extends VerrechenbarAdapter {
	public static final String XIDDOMAIN = "www.xid.ch/id/customservice";
	
	private static IOptifier noObligationOptifier = new NoObligationOptifier();
	
	/**
	 * Definition of the name of the table where Objects of this class should be stored the
	 * tablename should alwas be prefixed with tghe plugin's id to avoid name clashes if a plugins
	 * needs only one table, the name might as well be just the ID by itself. note: replace dots in
	 * the id with underscores
	 */
	static final String TABLENAME = "CH_ELEXIS_PRIVATRECHNUNG";
	public static final String CODESYSTEM_NAME = "Privat";
	public static final String CODESYSTEM_CODE = "999";
	static final String VERSION = "0.3.1";
	
	/**
	 * If the table does not exist when this plugin is loaded, it must create it on the fly. it
	 * should use this as explained here. The create script must make sure that it can run
	 * successful on any sql compliant database
	 */
	private static final String createDB = "CREATE TABLE "
		+ TABLENAME
		+ "("
		+ "ID	VARCHAR(25) primary key," // This field must always be present
		+ "lastupdate BIGINT," // This field must always be present
		+ "deleted		CHAR(1) default '0'," // This field must always be present
		+ "parent			VARCHAR(80),"
		+ "name			VARCHAR(499),"
		+ "short			VARCHAR(80),"
		+ "cost			CHAR(8),"
		+ // use always fixed char fields for amounts
		"price			CHAR(8),"
		+ // amounts are always in cents/rp
		"time			CHAR(4)," + "subsystem		VARCHAR(25),"
		+ "valid_from		CHAR(8),"
		+ // use always char(8) for dates
		"valid_until	CHAR(8),"
		+ "ExtInfo		BLOB);"
		+ // An ExtInfo field can be used to store arbitrary data
		"INSERT INTO " + TABLENAME + " (ID,name) VALUES ('VERSION','" + VERSION + "');"
		+ "CREATE INDEX chelpr_idx1 on " + TABLENAME + "(parent,name);"
		+ "CREATE INDEX chelpr_idx2 on " + TABLENAME + "(valid_from);";
	
	private static final String UPDATE_030 = "ALTER TABLE " + TABLENAME
		+ " MODIFY name VARCHAR(499);" + "ALTER TABLE " + TABLENAME + " MODIFY short VARCHAR(80);"
		+ "ALTER TABLE " + TABLENAME + " MODIFY cost CHAR(8);" + "ALTER TABLE " + TABLENAME
		+ " MODIFY price CHAR(8);" + "ALTER TABLE " + TABLENAME + " MODIFY parent VARCHAR(80);";
	
	private static final String UPDATE_031 = "ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;";
	/**
	 * Here we define the mapping between internal fieldnames and database fieldnames. (@see
	 * PersistentObject) then we try to load a version element. If this does not exist, we create
	 * the table. If it exists, we check the version
	 */
	static {
		addMapping(TABLENAME, "parent", "Name=name", "Kuerzel=short", "Kosten=cost", "Preis=price",
			"subsystem", "Zeit=time", "DatumVon=S:D:valid_from", "DatumBis=S:D:valid_until",
			"ExtInfo");
		Leistung check = load("VERSION");
		if (check.state() < PersistentObject.DELETED) { // Object never existed, so we have to
			// create the database
			createOrModifyTable(createDB);
		} else { // found existing table, check version
			VersionInfo v = new VersionInfo(check.get("Name"));
			if (v.isOlder(VERSION)) {
				if (v.isOlder("0.3.0")) {
					createOrModifyTable(UPDATE_030);
					check.set("Name", "0.3.0");
				}
				if (v.isOlder("0.3.1")) {
					createOrModifyTable(UPDATE_031);
					check.set("Name", VERSION);
				}
				
			}
		}
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "Privatleistung", Xid.ASSIGNMENT_LOCAL);
	}
	
	public static void createTable(){
		createOrModifyTable(createDB);
	}
	
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	public Leistung(String subsystem, String parent, final String name, final String kuerzel,
		final String kostenInRp, final String preisInRp, final String ZeitInMin, String DatumVon,
		String DatumBis){
		create(null);
		if (subsystem == null) {
			subsystem = "";
		}
		if (DatumVon == null) {
			DatumVon = TimeTool.BEGINNING_OF_UNIX_EPOCH;
		}
		if (DatumBis == null) {
			DatumBis = TimeTool.END_OF_UNIX_EPOCH;
		}
		if (parent == null) {
			parent = "NIL";
		}
		set(new String[] {
			"parent", "Name", "Kuerzel", "Kosten", "Preis", "Zeit", "subsystem", "DatumVon",
			"DatumBis"
		}, new String[] {
			parent, name, kuerzel, kostenInRp, preisInRp, ZeitInMin, subsystem, DatumVon, DatumBis
		});
	}
	
	/**
	 * A code to describe this code system uniquely (can be written on bills etc.)
	 */
	@Override
	public String getCodeSystemCode(){
		return CODESYSTEM_CODE;
	}
	
	/**
	 * The name of this code system to be displayed in the CodeSelector for the user
	 */
	@Override
	public String getCodeSystemName(){
		return CODESYSTEM_NAME;
	}
	
	/**
	 * A Label for this code
	 */
	@Override
	public String getLabel(){
		return get("Name");
	}
	
	@Override
	public String getText(){
		return get("Name");
	}
	
	@Override
	public String getCode(){
		return get("Kuerzel");
	}
	
	@Override
	public Money getKosten(final TimeTool dat){
		return new Money(checkZero(get("Kosten")));
	}
	
	@Override
	public int getMinutes(){
		return checkZero(get("Zeit"));
	}
	
	/**
	 * Mandatory method: return the table where elements of this class are stored
	 */
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	/**
	 * fields to display in code selector
	 */
	public String[] getDisplayedFields(){
		return new String[] {
			"Name", "Preis"
		};
	}
	
	/**
	 * factor to calculate the final price from the base price as stored in the table and the factor
	 * that is in effect at the given date and that migh depend from the "Fall-Type" and the billing
	 * type.
	 */
	public double getFactor(final TimeTool date, final Fall fall){
		return getVKMultiplikator(date, fall);
	}
	
	/**
	 * base price at a given date for this service
	 */
	public int getTP(final TimeTool date, final Fall fall){
		return checkZero(get("Preis"));
	}
	
	/**
	 * -required- This is the standard method to construct a PersistentObject from its
	 * representation in the database. For semantic reasons we intenionally do not use the "new"
	 * constructor for that purpose. "New" should only be used to create really "new" Objects and
	 * not to load existing objects from the database. Internally, however, load simply calls
	 * new(String). The static method load(String) must exist in every sublclass of
	 * PersistentObject, but it can always be written just exacly as here. The method needs not to
	 * guarantee that the returned object exists. It can, if desired, return a null value to
	 * indicate a inexistent object. Here we return just whatever the superclass gives us.
	 * 
	 */
	public static Leistung load(final String id){
		return new Leistung(id);
	}
	
	/**
	 * The empty constructor is only needed by the factory and should never be public.
	 */
	protected Leistung(){}
	
	/**
	 * The constructor with a single String is used to load objects from the database and should
	 * never be called directly. For that purpose is the static method load() to be used instead.
	 * This constructor should always be defined exactly the same way as shown below. It loads the
	 * object "lazily", what means that an access to the database will not occur until a member of
	 * the object is needed. This means, that the constructor will always succeed, even if the
	 * accessed object does not exist or is not valid. The caller could check this with exists() or
	 * isValid(), but this would mean an (in most cases unneccessary) database access.
	 */
	protected Leistung(final String id){
		super(id);
	}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	@Override
	public IOptifier getOptifier(){
		return noObligationOptifier;
	}
}
