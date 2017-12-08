/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.data;

import java.util.List;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class GDTProtokoll extends PersistentObject {
	
	public static final String FLD_DATETIME = "DateTime";
	public static final String FLD_PATIENT_ID = "PatientID";
	public static final String FLD_BEZEICHNUNG = "Bezeichnung";
	public static final String FLD_BEMERKUNGEN = "Bemerkungen";
	public static final String FLD_MESSAGE_TYPE = "MessageType";
	public static final String FLD_MESSAGE_DIRECTION = "MessageDirection";
	public static final String FLD_GEGENSTELLE = "Remote";
	public static final String FLD_MESSAGE = "Message";
	static final String VERSION = "1.0.1";
	
	public static final String MESSAGE_DIRECTION_IN = "IN";
	public static final String MESSAGE_DIRECTION_OUT = "OUT";
	
	static final String TABLENAME = "at_medevit_elexis_gdt_protokoll";
	
	static final String createDB =
			"CREATE TABLE " + TABLENAME
				+ "("
				+ "ID VARCHAR(25) primary key," 
				+ "lastupdate BIGINT," 
				+ "deleted CHAR(1) default '0'," 
				+ FLD_DATETIME +" VARCHAR(24)," 
				+ FLD_PATIENT_ID+" VARCHAR(25),"
				+ FLD_BEZEICHNUNG+" VARCHAR(60),"
				+ FLD_BEMERKUNGEN+" VARCHAR(80),"
				+ FLD_MESSAGE_TYPE+" VARCHAR(4),"
				+ FLD_MESSAGE_DIRECTION+" VARCHAR(3)," 
				+ FLD_GEGENSTELLE+" VARCHAR(60),"
				+ FLD_MESSAGE+"	TEXT);" 
				+ "CREATE INDEX "
				+ TABLENAME
				+ "idx1 on " + TABLENAME + " ("+FLD_DATETIME+");"
				+ "INSERT INTO " + TABLENAME + " (ID,DateTime) VALUES ('VERSION',"
				+ JdbcLink.wrap("1.0.0") + ");";
	
	static {
		addMapping(TABLENAME, FLD_DATETIME, FLD_PATIENT_ID,
			FLD_BEZEICHNUNG, FLD_BEMERKUNGEN, FLD_MESSAGE_TYPE, FLD_MESSAGE_DIRECTION, FLD_GEGENSTELLE, FLD_MESSAGE); 
		GDTProtokoll version = load("VERSION");
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(version.get(FLD_DATETIME));	
			if (vi.isOlder(VERSION)) {
				{	// 1.0.1
					createOrModifyTable("ALTER TABLE "+TABLENAME+" MODIFY "+FLD_GEGENSTELLE+" VARCHAR(255);");
					version.set(FLD_DATETIME, VERSION);
				}
			}
		}
	}

	GDTProtokoll(){}
	protected GDTProtokoll(String id) { super(id); }
	
	
	public <U extends GDTSatzNachricht> GDTProtokoll(String messageDirection, IGDTCommunicationPartner cp, U satzNachricht){
		create(null); 
		
		Patient pat = Patient.loadByPatientID(satzNachricht.getValue(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG));
		
		StringBuilder sb = new StringBuilder();
		String[] message = satzNachricht.getMessage();
		for (int i = 0; i < message.length; i++) {
			sb.append(message[i]);
		}
		
		set(new String[] {
			FLD_DATETIME, FLD_PATIENT_ID, FLD_MESSAGE_TYPE, FLD_MESSAGE_DIRECTION, FLD_GEGENSTELLE, FLD_MESSAGE, FLD_BEZEICHNUNG
		}, 
		new TimeTool().toString(TimeTool.TIMESTAMP), 
		(pat!=null) ? pat.getId() : "nicht zugeordnet", 
		satzNachricht.getValue(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION), 
		messageDirection,
		cp.getLabel(),
		sb.toString(),
		satzNachricht.getValue(GDTConstants.FELDKENNUNG_TEST_IDENT)
		);

		int satznachrichtType = Integer.parseInt(satzNachricht.getValue(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION));
		if(messageDirection.equalsIgnoreCase(GDTProtokoll.MESSAGE_DIRECTION_OUT)) {
			switch (satznachrichtType) {
			case GDTConstants.SATZART_UNTERSUCHUNG_ANFORDERN:
				set(FLD_BEZEICHNUNG, "Anforderung Untersuchung: "+satzNachricht.getValue(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD));
				break;
			case GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_ZEIGEN:
				set(FLD_BEZEICHNUNG, "Anzeige Untersuchung: "+satzNachricht.getValue(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD));
				break;
			case GDTConstants.SATZART_STAMMDATEN_UEBERMITTELN:
				set(FLD_BEZEICHNUNG, "Stammdaten übermittelt");
				break;
			default: break;
			}
		}
		if(messageDirection.equalsIgnoreCase(GDTProtokoll.MESSAGE_DIRECTION_IN)) {
			switch (satznachrichtType) {
			case GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN:
				set(FLD_BEZEICHNUNG, "Resultat Untersuchung: "+satzNachricht.getValue(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD));
				break;
			
			default: break;
			}
		}
	}
		
	public static GDTProtokoll load(String id){
		return new GDTProtokoll(id);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		TimeTool tt = new TimeTool(get(FLD_DATETIME));
		Patient pat = Patient.loadByPatientID(get(FLD_PATIENT_ID));
		sb.append(tt.toString(TimeTool.DATE_GER)+": "+pat.getLabel()+" "+FLD_MESSAGE_TYPE+" "+FLD_MESSAGE_DIRECTION);
		return sb.toString();
	}
	
	public String getMenuLabel() {
		StringBuilder sb = new StringBuilder();
		TimeTool tt = new TimeTool(get(FLD_DATETIME));
		
		sb.append(tt.toString(TimeTool.DATE_GER)+": "+get(FLD_BEZEICHNUNG)+" "+"["+get(FLD_GEGENSTELLE)+"]");
		return sb.toString();
	}

	public static <U extends GDTSatzNachricht> GDTProtokoll addEntry(String messageDirection, IGDTCommunicationPartner cp,
		 U satzNachricht){
		return new GDTProtokoll(messageDirection, cp, satzNachricht);
	}
	
	public static GDTProtokoll[] getAllEntries() {
		Query<GDTProtokoll> qbe = new Query<GDTProtokoll>(GDTProtokoll.class);
		qbe.add("ID", Query.NOT_EQUAL, "VERSION");
		List<GDTProtokoll> qre = qbe.execute();
		return qre.toArray(new GDTProtokoll[] {});
	}
	
	public static GDTProtokoll[] getEntriesForPatient(Patient pat) {
		String ID = pat.getId();
		Query<GDTProtokoll> qbe = new Query<GDTProtokoll>(GDTProtokoll.class);
		qbe.add("ID", Query.NOT_EQUAL, "VERSION");
		qbe.add(GDTProtokoll.FLD_PATIENT_ID, Query.EQUALS, ID);
		List<GDTProtokoll> qre = qbe.execute();
		return qre.toArray(new GDTProtokoll[] {});
	}
	
	public String getMessageDirection() {
		return get(FLD_MESSAGE_DIRECTION);
	}
	public TimeTool getEntryTime() {
		return new TimeTool(get(FLD_DATETIME));
	}
	/**
	 * @return value of {@link GDTConstants#FELDKENNUNG_SATZIDENTIFIKATION}
	 */
	public String getMessageType(){
		return get(FLD_MESSAGE_TYPE);
	}
	public Patient getEntryRelatedPatient() {
		return Patient.load(get(FLD_PATIENT_ID));
	}
	public String getBezeichnung() {
		return get(FLD_BEZEICHNUNG);
	}
	public void setBezeichnung(String bezeichnung) {
		set(FLD_BEZEICHNUNG, bezeichnung);
	}
	public String getBemerkungen() {
		return get(FLD_BEMERKUNGEN);
	}
	public void setBemerkungen(String bemerkung) {
		set(FLD_BEMERKUNGEN, bemerkung);
	}
	public String getGegenstelle() {
		return get(FLD_GEGENSTELLE);
	}
	public String getMessage() {
		return get(FLD_MESSAGE);
	}
}
