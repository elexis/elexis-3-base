package com.hilotec.elexis.kgview.medikarte;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hilotec.elexis.kgview.Preferences;
import com.hilotec.elexis.kgview.data.FavMedikament;

import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class MedikarteHelpers {
	private final static String PRESC_EI_ORD = "hilotec:ordnungszahl";
	private final static String PRESC_EI_ZWECK = "hilotec:zweck";

	/**
	 * Medikation auf der Medikarte des Patienten zusammensuchen.
	 * Wenn !alle, dann wird nur die noch aktuelle Medikation zurueckgegeben.
	 */
	public static List<Prescription> medikarteMedikation(
			Patient patient, boolean alle)
	{
		return medikarteMedikation(patient, alle, false);
	}

	/**
	 * Medikation auf der Medikarte des Patienten zusammensuchen.
	 * Wenn !alle, dann wird nur die noch aktuelle Medikation zurueckgegeben.
	 * Mit geloescht kann gesteuert werden ob auch geloeschte Medikamente
	 * angezeigt werden sollen.
	 */
	public static List<Prescription> medikarteMedikation(
			Patient patient, boolean alle, boolean geloescht)
	{
		Query<Prescription> qbe = new Query<Prescription>(Prescription.class);

		// FIXME: sollte mit executed with deleted gehen
		if (geloescht) {
			qbe.clear();
		}

		qbe.add(Prescription.PATIENT_ID, Query.EQUALS, patient.getId());
		qbe.add(Prescription.REZEPT_ID, StringTool.leer, null);
		if (!alle) {
			qbe.startGroup();
			String today = new TimeTool().toString(TimeTool.DATE_COMPACT);
			if (Preferences.getMedikarteStopdatumInkl()) {
				qbe.add(Prescription.DATE_UNTIL, Query.GREATER_OR_EQUAL, today);
			} else {
				qbe.add(Prescription.DATE_UNTIL, Query.GREATER, today);
			}
			qbe.or();
			qbe.add(Prescription.DATE_UNTIL, StringTool.leer, null);
			qbe.or();
			qbe.add(Prescription.DATE_UNTIL, Query.EQUALS, "");
			qbe.endGroup();
		}

		// Medikamente ohne Fav-Medi Verknuepfung oder mit falsch formatierter
		// Dosis rauswerfen 
		List<Prescription> pl = qbe.execute();
		Iterator<Prescription> i = pl.iterator();
		while(i.hasNext()) {
			Prescription p = i.next();
			if (FavMedikament.load(p.getArtikel()) == null)
				i.remove();
			else if (p.getDosis().split("-").length != 4)
				i.remove();
		}

		return pl;
	}
	
	/**
	 * Datum der letzten Aenderung der Medikarte (letztes von oder bis datum)
	 */
	public static String medikarteDatum(Patient patient)
	{
		// TODO: Koennte man mit einer Query sauberer loesen
		List<Prescription> medis = medikarteMedikation(patient, false);
		TimeTool max = new TimeTool(0);
		TimeTool cur = new TimeTool();
		for (Prescription p: medis) {
			cur.set(p.getBeginDate());
			if (cur.isAfter(max)) max.set(p.getBeginDate());
			cur.set(p.getEndDate());
			if (cur.isAfter(max)) max.set(p.getEndDate());
		}
		
		return max.toString(TimeTool.DATE_GER);
	}
	
	/**
	 * Ordnungszahl fuer Verschreibung holen
	 */
	@SuppressWarnings("rawtypes")
	public static int getOrdnungszahl(Prescription presc) {
		Map ht = presc.getMap(Prescription.FLD_EXTINFO);
		
		// Ordnungszahl der Verschreibung
		if (ht.containsKey(PRESC_EI_ORD))
			return (Integer) ht.get(PRESC_EI_ORD);
		
		// Standard fuers Medikament
		FavMedikament fm = FavMedikament.load(presc.getArtikel());
		if (fm != null)
			return fm.getOrdnungszahl();
		
		return 0;
	}
	
	/**
	 * Ordnungszahl fuer Verschreibung setzen
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setOrdnungszahl(Prescription presc, int ord) {
		Map ht = presc.getMap(Prescription.FLD_EXTINFO);
		ht.put(PRESC_EI_ORD, ord);
		presc.setMap(Prescription.FLD_EXTINFO, ht);
	}
	
	/**
	 * Zweck fuer Verschreibung holen
	 */
	@SuppressWarnings("rawtypes")
	public static String getPZweck(Prescription presc) {
		Map ht = presc.getMap(Prescription.FLD_EXTINFO);
		
		// Ordnungszahl der Verschreibung
		if (ht.containsKey(PRESC_EI_ZWECK))
			return (String) ht.get(PRESC_EI_ZWECK);
		
		// Standard fuers Medikament
		FavMedikament fm = FavMedikament.load(presc.getArtikel());
		if (fm != null)
			return fm.getZweck();
		
		return "";
	}
	
	/**
	 * Ordnungszahl fuer Verschreibung setzen
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setPZweck(Prescription presc, String zweck) {
		Map ht = presc.getMap(Prescription.FLD_EXTINFO);
		
		// Wenns dem Standard entspricht speichern wir den Eintrag nicht
		FavMedikament fm = FavMedikament.load(presc.getArtikel());
		if (fm != null && fm.getZweck().equals(zweck)) {
			ht.remove(PRESC_EI_ZWECK);
		} else {
			ht.put(PRESC_EI_ZWECK, zweck);
		}
		
		presc.setMap(Prescription.FLD_EXTINFO, ht);
	}
}
