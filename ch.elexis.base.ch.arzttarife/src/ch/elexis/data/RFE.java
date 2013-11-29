/*******************************************************************************
 * Copyright (c) 2009-2011, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.data;

import java.util.HashMap;
import java.util.List;

import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class RFE extends PersistentObject {
	public static final String Version = "0.1.0";
	private static final String TABLENAME = "ch_elexis_arzttarif_ch_rfe";
	private static final String createDB = "CREATE TABLE " + TABLENAME + " ("
		+ "ID VARCHAR(25) primary key," + "deleted CHAR(1) default '0'," + "lastupdate bigint,"
		+ "type CHAR(2)," + "konsID VARCHAR(25)" + ");" + "CREATE INDEX " + TABLENAME + "_idx ON "
		+ TABLENAME + " (konsID);" + "INSERT INTO " + TABLENAME
		+ " (ID, konsID) VALUES ('VERSION','" + Version + "');";
	
	static final String[][] rfe = {
		{
			"01", "01- Kontakt auf Wunsch des Patienten", "01-Wunsch"
		},
		{
			"02", "02- Notfallkonsultation (vor 1.6.2012)", "02-NF"
		},
		{
			"03", "03- Kontakt auf Zuweisung", "03-Zuweis."
		},
		{
			"04", "04- Folgekontakt auf Verordnung/Empfehlung", "04-Verord."
		},
		{
			"05", "05- Folgekontakt wegen auswärtiger Hämatologie und Chemie", "05-Labor"
		},
		{
			"06", "06- Kontakt in Zusammenhang mit Langzeitpflege", "06-Langz."
		},
		{
			"07", "07- Kontakt in kausalem Zusammenhang mit Eingriff / Hospitalisation",
			"07-Spital"
		}, {
			"99", "99- Kein Arztkontakt", "99-"
		}
	
	};
	
	static HashMap<String, String> rfeHash;
	
	static {
		addMapping(TABLENAME, "type", "konsID");
		RFE version = load("VERSION");
		if (!version.exists()) {
			createOrModifyTable(createDB);
		}
		rfeHash = new HashMap<String, String>();
		for (String[] line : rfe) {
			rfeHash.put(line[0], line[1]);
		}
	}
	
	public RFE(String KonsId, String code){
		create(null);
		set(new String[] {
			"konsID", "type"
		}, KonsId, code);
	}
	
	public static HashMap<String, String> getRFEDef(){
		return rfeHash;
	}
	
	public static String[] getRFETexts(){
		String[] ret = new String[rfe.length];
		for (int i = 0; i < rfe.length; i++) {
			ret[i] = rfe[i][1];
		}
		return ret;
	}
	
	public static String[][] getRFEDescriptions(){
		return rfe;
	}
	
	public static void clear(Konsultation k){
		getConnection().exec("DELETE FROM " + TABLENAME + " WHERE KonsID=" + k.getWrappedId());
	}
	
	public String getText(){
		String code = getCode();
		return rfeHash.get(code);
	}
	
	public static List<RFE> getRfeForKons(String konsID){
		Query<RFE> qbe = new Query<RFE>(RFE.class);
		qbe.add("konsID", Query.EQUALS, konsID);
		return qbe.execute();
	}
	
	public String getCode(){
		return checkNull(get("type"));
	}
	
	public Konsultation getKons(){
		return Konsultation.load(get("konsID"));
	}
	
	@Override
	public String getLabel(){
		return getKons().getLabel() + " : " + getText();
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static RFE load(String id){
		return new RFE(id);
	}
	
	protected RFE(String id){
		super(id);
	}
	
	protected RFE(){
		
	}
}
