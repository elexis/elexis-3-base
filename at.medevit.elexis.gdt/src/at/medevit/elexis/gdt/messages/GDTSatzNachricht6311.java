/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.messages;

import java.util.HashMap;
import java.util.LinkedHashMap;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class GDTSatzNachricht6311 extends GDTSatzNachricht {
	
	/**
	 * Erstellt eine GDT Satzart Nachricht vom Typ "Daten einer Untersuchung zeigen" 6311
	 * 
	 * @param patientenkennung
	 * @param namenszusatz optional
	 * @param nachnamePatient optional
	 * @param vornamePatient optional
	 * @param geburtsdatumPatient optional
	 * @param titelPatient optional
	 * @param tagErhebungBehandlungsdaten optional
	 * @param uhrzeitErhebungBehandlungsdaten optional
	 * @param guvskf optional
	 * @param abnahmeDatum optional
	 * @param abnahmeZeit optional
	 * @param gdtIdReceiver optional
	 * @param gdtIdSender optional
	 * @param zeichensatz optional
	 * @param gdtVersion
	 */
	public GDTSatzNachricht6311(String patientenkennung, String namenszusatz, 
		String nachnamePatient, String vornamePatient, String geburtsdatumPatient, String titelPatient,
		String tagErhebungBehandlungsdaten, String uhrzeitErhebungBehandlungsdaten, String guvskf,
		String abnahmeDatum, String abnahmeZeit,
		String gdtIdReceiver, String gdtIdSender, String zeichensatz, String gdtVersion){
		super(GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_ZEIGEN, gdtIdReceiver, gdtIdSender, zeichensatz, gdtVersion);
				
		values.put(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG, patientenkennung);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_NAME, nachnamePatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_VORNAME, vornamePatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM, geburtsdatumPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ, namenszusatz);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_TITEL, titelPatient);
		values.put(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD, guvskf);
		values.put(GDTConstants.FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN, tagErhebungBehandlungsdaten);
		values.put(GDTConstants.FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN, uhrzeitErhebungBehandlungsdaten);
		values.put(GDTConstants.FELDKENNUNG_ABNAHME_DATUM, abnahmeDatum);
		values.put(GDTConstants.FELDKENNUNG_ABNAHME_ZEIT, abnahmeZeit);
	}
	
	@Override
	protected void createMessage(){
		super.createMessage();
		
		addLine(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG);	
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_NAME);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_VORNAME);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_TITEL);
		ifSetAddLine(GDTConstants.FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN);
		ifSetAddLine(GDTConstants.FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN);
		ifSetAddLine(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD);	
		ifSetAddLine(GDTConstants.FELDKENNUNG_ABNAHME_DATUM);
		ifSetAddLine(GDTConstants.FELDKENNUNG_ABNAHME_ZEIT);	
	}

	/**
	 * Bequemlichkeits Konstruktor für Datensatz 6311 - Beinhaltet nur die MUSS Felder
	 * 
	 * @param patientenkennung 
	 * 
	 */
	public GDTSatzNachricht6311(String patientenkennung, String gdtVersion){
		this(patientenkennung, null, null, null, null, null, null, null, null, null, null, null, null, null, gdtVersion);
	}
	
	/**
	 * Parse an incoming 6311 Message from a String array
	 * 
	 * @param satznachricht
	 * @return class if successful else null
	 */
	public static GDTSatzNachricht6311 createfromStringArray(String[] satznachricht){
		HashMap<Integer, String> ds = new LinkedHashMap<Integer, String>();
		
		for (int i = 0; i < satznachricht.length; i++) {
			int length = Integer.parseInt(satznachricht[i].substring(0, 3));
			int feldkennung = Integer.parseInt(satznachricht[i].substring(3, 7));
			String value = satznachricht[i].substring(7, length-2);
			ds.put(feldkennung, value);
		}
		
		int satzkennung = Integer.parseInt(ds.get(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION));
		if(satzkennung!=GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_ZEIGEN) return null;
		
		return new GDTSatzNachricht6311(ds.get(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG),
			ds.get(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ),
			ds.get(GDTConstants.FELDKENNUNG_PATIENT_NAME), 
			ds.get(GDTConstants.FELDKENNUNG_PATIENT_VORNAME), 
			ds.get(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM), 
			ds.get(GDTConstants.FELDKENNUNG_PATIENT_TITEL), 		
			ds.get(GDTConstants.FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN), 
			ds.get(GDTConstants.FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN), 
			ds.get(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD), 
			ds.get(GDTConstants.FELDKENNUNG_ABNAHME_DATUM), 
			ds.get(GDTConstants.FELDKENNUNG_ABNAHME_ZEIT), 
			ds.get(GDTConstants.FELDKENNUNG_GDT_ID_EMPFAENGER), 
			ds.get(GDTConstants.FELDKENNUNG_GDT_ID_SENDER), 
			ds.get(GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ), 
			ds.get(GDTConstants.FELDKENNUNG_VERSION_GDT));
	}
	
}
