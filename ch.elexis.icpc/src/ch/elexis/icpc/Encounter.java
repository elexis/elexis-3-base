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
package ch.elexis.icpc;

import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;

public class Encounter extends PersistentObject {
	private static final String VERSION = "0.2.1";
	private static final String TABLENAME = "CH_ELEXIS_ICPC_ENCOUNTER";
	
	private static final String createDB = "CREATE TABLE " + TABLENAME + " ("
		+ "ID				VARCHAR(25)," + "lastupdate 	BIGINT," + "deleted		CHAR(1) default '0',"
		+ "KONS			VARCHAR(25)," + "EPISODE		VARCHAR(25)," + "RFE			CHAR(4)," + "DIAG			CHAR(4),"
		+ "PROC			CHAR(4)," + "ExtInfo		BLOB);" + "CREATE INDEX " + TABLENAME + "1 ON " + TABLENAME
		+ " (KONS);" + "CREATE INDEX " + TABLENAME + "2 ON " + TABLENAME + " (EPISODE);"
		+ "INSERT INTO " + TABLENAME + " (ID,KONS) VALUES ('1'," + JdbcLink.wrap(VERSION) + ");";
	
	static {
		addMapping(TABLENAME, "KonsID=KONS", "EpisodeID=EPISODE", "RFE", "Diag", "Proc", "ExtInfo");
		Encounter version = load("1");
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(version.get("KonsID"));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.2.0")) {
					getConnection().exec(
						"ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';");
					version.set("KonsID", VERSION);
				} else if (vi.isOlder("0.2.1")) {
					createOrModifyTable("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;");
					version.set("KonsID", VERSION);
				}
			}
		}
	}
	
	public Encounter(Konsultation kons, Episode ep){
		create(null);
		set(new String[] {
			"KonsID", "EpisodeID"
		}, kons.getId(), ep.getId());
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(getKons().getDatum()).append(": ").append(get("RFE")).append(", ")
			.append(get("Diag")).append(", ").append(get("Proc"));
		return sb.toString();
	}
	
	public Konsultation getKons(){
		return Konsultation.load(get("KonsID"));
	}
	
	public Episode getEpisode(){
		return Episode.load(get("EpisodeID"));
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static Encounter load(String id){
		return new Encounter(id);
	}
	
	protected Encounter(String id){
		super(id);
	}
	
	protected Encounter(){}
	
	public void setRFE(IcpcCode code){
		set("RFE", code.getId());
		
	}
	
	public void setDiag(IcpcCode code){
		set("Diag", code.getId());
	}
	
	public void setProc(IcpcCode code){
		set("Proc", code.getId());
	}
	
	public IcpcCode getRFE(){
		String rfeID = get("RFE");
		return StringTool.isNothing(rfeID) ? null : IcpcCode.load(rfeID);
	}
	
	public IcpcCode getDiag(){
		String diagID = get("Diag");
		return StringTool.isNothing(diagID) ? null : IcpcCode.load(diagID);
	}
	
	public IcpcCode getProc(){
		String procID = get("Proc");
		return StringTool.isNothing(procID) ? null : IcpcCode.load(procID);
	}
}
