/*******************************************************************************
 * Copyright (c) 2011, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    
 *******************************************************************************/

package ch.elexis.icpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.core.data.interfaces.IDataAccess;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class DataAccessor implements IDataAccess {
	
	public String getName(){
		return "ICPC2-Daten";
	}
	
	public String getDescription(){
		return "Daten aus dem ICPC2-Plugin";
	}
	
	public List<Element> getList(){
		// TODO Auto-generated method stub
		return null;
	}
	
	private String code(IcpcCode ic){
		return (ic == null ? "" : ic.getCode());
	}
	
	/**
	 * Alle Encounters in der Episode ep suchen, die zum Zeitraum dates passen und Liste zurueck
	 * geben.
	 */
	private Result<Object> sucheEncounters(Episode ep, String dates, PersistentObject dep){
		Query<Encounter> qen = new Query<Encounter>(Encounter.class);
		qen.add("EpisodeID", Query.LIKE, ep.getId());
		if (dep instanceof Konsultation) {
			Konsultation kons = (Konsultation) dep;
			qen.add("KonsID", Query.LIKE, kons.getId());
		}
		
		// TODO: Filtern nach dates
		
		// Encounters ohne RFE, Diag und Procedere rauswerfen
		qen.addPostQueryFilter(new IFilter() {
			public boolean select(Object element){
				Encounter e = (Encounter) element;
				return e.getRFE() != null || e.getDiag() != null || e.getProc() != null;
			}
		});
		List<Encounter> result = qen.execute();
		
		// Sortieren
		Collections.sort(result, new Comparator<Encounter>() {
			public int compare(Encounter e1, Encounter e2){
				TimeTool tt1 = new TimeTool(e1.getKons().getDatum());
				TimeTool tt2 = new TimeTool(e2.getKons().getDatum());
				return tt1.compareTo(tt2);
			}
		});
		
		return new Result<Object>(result);
	}
	
	private Result<Object> sucheEncounters(List<Episode> eps, Map<Episode, List<Encounter>> encs,
		PersistentObject dep, String dates){
		
		Patient pat;
		
		if (dep instanceof Konsultation) {
			Konsultation kons = (Konsultation) dep;
			pat = kons.getFall().getPatient();
		} else if (dep instanceof Patient) {
			pat = (Patient) dep;
		} else {
			return new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.INVALID_PARAMETERS,
				"Ungültiger Parameter", dep, true);
		}
		
		// Alle Episoden des Patienten zusammensuchen
		Query<Episode> qep = new Query<Episode>(Episode.class);
		qep.add(Episode.FLD_PATIENT_ID, Query.LIKE, pat.getId());
		qep.orderBy(false, Episode.FLD_START_DATE);
		List<Episode> raw_eps = qep.execute();
		
		int count = 0;
		for (Episode ep : raw_eps) {
			// Betroffene Encounters suchen
			Result<Object> res = sucheEncounters(ep, dates, dep);
			if (!res.isOK()) {
				return res;
			}
			
			// Falls mindestens ein Encounter gefunden wurde,
			// Episode in Liste aufnehmen.
			@SuppressWarnings("unchecked")
			List<Encounter> ep_encs = (List<Encounter>) res.get();
			if (ep_encs.size() > 0) {
				eps.add(ep);
				encs.put(ep, ep_encs);
				// Eine Zeile fuer die Episode + eine pro Encounter
				count += 1 + ep_encs.size();
			}
		}
		
		return new Result<Object>(count);
	}
	
	public Result<Object> getObject(String descriptor, PersistentObject dependentObject,
		String dates, String[] params){
		
		if (descriptor.toLowerCase().equals("encounters")) {
			/*
			 * Tabelle in der Form: Problem1 | Datum | aktiv/inaktiv | Datum | RFE | Diagnose |
			 * Procedere . . . . . Problem2 | Datum | aktiv/inaktiv . . . . .
			 */
			
			List<Episode> episodes = new LinkedList<Episode>();
			HashMap<Episode, List<Encounter>> encounters = new HashMap<Episode, List<Encounter>>();
			
			Result<Object> res = sucheEncounters(episodes, encounters, dependentObject, dates);
			if (!res.isOK()) {
				return res;
			}
			
			int rows = (Integer) res.get() + 1;
			String result[][] = new String[rows][5];
			int i = 0;
			
			// Spaltenüberschriften
			result[i][0] = "Problem";
			result[i][1] = "Datum";
			result[i][2] = "RFE";
			result[i][3] = "Diagnose";
			result[i][4] = "Procedere";
			i++;
			
			for (Episode ep : episodes) {
				/* Zeile fuer Episode generieren */
				result[i][0] = ep.getTitle();
				result[i][1] = ep.getStartDate();
				result[i][2] = ep.getStatusText();
				result[i][3] = result[i][4] = "";
				i++;
				
				/* Zeilen fuer Encounters generieren */
				for (Encounter en : encounters.get(ep)) {
					result[i][0] = "";
					result[i][1] = en.getKons().getDatum();
					result[i][2] = code(en.getRFE());
					result[i][3] = code(en.getDiag());
					result[i][4] = code(en.getProc());
					i++;
				}
			}
			
			return new Result<Object>(result);
		} else {
			return new Result<Object>(Result.SEVERITY.ERROR, IDataAccess.OBJECT_NOT_FOUND,
				"Ungültiger Parameter", descriptor, true);
		}
	}
	
}
