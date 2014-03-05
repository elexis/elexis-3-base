/*******************************************************************************
 * Copyright (c) 2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.archie.wzw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.artikel_ch.data.Medical;
import ch.elexis.artikel_ch.data.Medikament;
import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.PhysioLeistung;
import ch.elexis.data.Rechnung;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.elexis.medikamente.bag.data.BAGMedi;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

/**
 * This view lists patients with their cost, can be ordered by sex, age, Tarmed-AL-cost,
 * Tarmed-TL-cost, Physio cost, lab cost
 * 
 * @author gerry
 * 
 */
public class PatientenHitlist extends BaseStats {
	static final String NAME = "Patienten-Hitliste";
	static final String DESC = "Listet Patienten nach Kosten";
	static final String[] HEADINGS = {
		"PatientNr", "Alter", "Geschlecht", "Kosten", "Tarmed", "Tarmed-AL", "Tarmed-TL",
		"Medicals", "Medikamente", "Physio", "Andere", "Anz. Kons", "Anz. Besuch", "Anz. Rn"
	};
	
	private int males;
	private int females;
	private double age_female;
	private double age_male;
	private double cost_male, cost_female, tarmed_male, tarmed_female, tal_male, tal_female,
			ttl_male, ttl_female, medicals_male, medicals_female, medics_male, medics_female,
			physio_male, physio_female, other_male, other_female, cons_male, cons_female,
			visit_male, visit_female, bills_female, bills_male;
	
	public PatientenHitlist(){
		super(NAME, DESC, HEADINGS);
	}
	
	@Override
	protected IStatus createContent(IProgressMonitor monitor){
		try {
			HashMap<String, PatientStat> pstat =
				new HashMap<String, PatientenHitlist.PatientStat>();
			List<Konsultation> conses = getConses(monitor);
			if (conses.size() > 0) {
				int clicksPerRound = HUGE_NUMBER / conses.size();
				for (Konsultation k : conses) {
					Mandant m = k.getMandant();
					if (m != null) {
						Fall fall = k.getFall();
						if (fall != null) {
							Patient pat = fall.getPatient();
							if (pat != null) {
								PatientStat ps = pstat.get(pat.getId());
								if (ps == null) {
									ps = new PatientStat(pat);
									pstat.put(pat.getId(), ps);
								}
								ps.addCons(k);
							}
						}
						monitor.worked(clicksPerRound);
						if (monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
					}
				}
				
				// Resultat-Array für Archie aufbauen
				final ArrayList<Comparable<?>[]> result = new ArrayList<Comparable<?>[]>();
				Comparable<?>[] sum_all = new Comparable<?>[this.dataSet.getHeadings().size()];
				Comparable<?>[] sum_male = new Comparable<?>[this.dataSet.getHeadings().size()];
				Comparable<?>[] sum_female = new Comparable<?>[this.dataSet.getHeadings().size()];
				result.add(sum_all);
				result.add(sum_female);
				result.add(sum_male);
				sum_all[0] = "Durchschnitt Alle";
				sum_female[0] = "Frauen";
				sum_male[0] = "Männer";
				for (PatientStat ps : pstat.values()) {
					Comparable<?>[] row = new Comparable<?>[this.dataSet.getHeadings().size()];
					Patient pat = Patient.load(ps.PatientID);
					if (pat != null && pat.isValid()) {
						row[0] = pat.getPatCode();
						row[1] = pat.getAlter();
						row[2] = pat.getGeschlecht();
						row[3] = round(ps.costTotal);
						row[4] = round(ps.costTarmedAL + ps.costTarmedTL);
						row[5] = round(ps.costTarmedAL);
						row[6] = round(ps.costTarmedTL);
						row[7] = round(ps.costMedical);
						row[8] = round(ps.costMedikamente);
						row[9] = round(ps.costPhysio);
						row[10] = round(ps.costOther);
						row[11] = ps.numCons;
						row[12] = ps.numVisits;
						row[13] = ps.bills.size();
						result.add(row);
						if (pat.getGeschlecht().equalsIgnoreCase(Person.MALE)) {
							males++;
							age_male += (Double.parseDouble((String) row[1]));
							cost_male += (Double) row[3];
							tarmed_male += (Double) row[4];
							tal_male += (Double) row[5];
							ttl_male += (Double) row[6];
							medicals_male += (Double) row[7];
							medics_male += (Double) row[8];
							physio_male += (Double) row[9];
							other_male += (Double) row[10];
							cons_male += (Integer) row[11];
							visit_male += (Integer) row[12];
							bills_male += (Integer) row[13];
						} else {
							females++;
							age_female += (Double.parseDouble((String) row[1]));
							cost_female += (Double) row[3];
							tarmed_female += (Double) row[4];
							tal_female += (Double) row[5];
							ttl_female += (Double) row[6];
							medicals_female += (Double) row[7];
							medics_female += (Double) row[8];
							physio_female += (Double) row[9];
							other_female += (Double) row[10];
							cons_female += (Integer) row[11];
							visit_female += (Integer) row[12];
							bills_female += (Integer) row[13];
							
						}
					}
				}
				sum_female[1] = round(age_female / females);
				sum_female[2] = new Integer(females);
				sum_female[3] = round(cost_female / females);
				sum_female[4] = round(tarmed_female / females);
				sum_female[5] = round(tal_female / females);
				sum_female[6] = round(ttl_female / females);
				sum_female[7] = round(medicals_female / females);
				sum_female[8] = round(medics_female / females);
				sum_female[9] = round(physio_female / females);
				sum_female[10] = round(other_female / females);
				sum_female[11] = round(cons_female / females);
				sum_female[12] = round(visit_female / females);
				sum_female[13] = round(bills_female / females);
				
				sum_male[1] = round(age_male / males);
				sum_male[2] = new Integer(males);
				sum_male[3] = round(cost_male / males);
				sum_male[4] = round(tarmed_male / males);
				sum_male[5] = round(tal_male / males);
				sum_male[6] = round(ttl_male / males);
				sum_male[7] = round(medicals_male / males);
				sum_male[8] = round(medics_male / males);
				sum_male[9] = round(physio_male / males);
				sum_male[10] = round(other_male / males);
				sum_male[11] = round(cons_male / males);
				sum_male[12] = round(visit_male / males);
				sum_male[13] = round(bills_male / males);
				
				sum_all[1] = round((age_male + age_female) / (males + females));
				sum_all[2] = new Integer(males + females);
				sum_all[3] = round((cost_male + cost_female) / (males + females));
				sum_all[4] = round((tarmed_male + tarmed_female) / (males + females));
				sum_all[5] = round((tal_male + tal_female) / (males + females));
				sum_all[6] = round((ttl_male + ttl_female) / (males + females));
				sum_all[7] = round((medicals_male + medicals_female) / (males + females));
				sum_all[8] = round((medics_male + medics_female) / (males + females));
				sum_all[9] = round((physio_male + physio_female) / (males + females));
				sum_all[10] = round((other_male + other_female) / (males + females));
				sum_all[11] = round((cons_male + cons_female) / (males + females));
				sum_all[12] = round((visit_male + visit_female) / (males + females));
				sum_all[13] = round((bills_male + bills_female) / (males + females));
				// Und an Archie übermitteln
				this.dataSet.setContent(result);
				monitor.done();
				return Status.OK_STATUS;
			}
		} catch (Throwable t) {
			ExHandler.handle(t);
			return new Status(Status.ERROR, "ch.elexis.archie.wzw", t.getMessage());
		}
		return Status.CANCEL_STATUS;
		
	}
	
	private double round(double x){
		return Math.round(x * 100) / 100.0;
	}
	
	static class PatientStat {
		
		PatientStat(Patient pat){
			PatientID = pat.getId();
			birthDate = new TimeTool(pat.get(Patient.FLD_DOB));
			sex = pat.getGeschlecht();
			numCons = 0;
			numVisits = 0;
			costTarmedAL = 0.0;
			costTarmedTL = 0.0;
			costMedical = 0.0;
			costMedikamente = 0.0;
			costPhysio = 0.0;
			costLabor = 0.0;
			costOther = 0.0;
			costTotal = 0.0;
		}
		
		void addCons(Konsultation k){
			TimeTool kdate = new TimeTool(k.getDatum());
			numCons++;
			List<Verrechnet> vr = k.getLeistungen();
			Fall fall = k.getFall();
			for (Verrechnet v : vr) {
				IVerrechenbar vv = v.getVerrechenbar();
				if (vv != null) {
					double cost = v.getNettoPreis().doubleValue() * v.getZahl();
					costTotal += cost;
					if (vv instanceof TarmedLeistung) {
						TarmedLeistung tl = (TarmedLeistung) vv;
						if (tl.getCode().equals("00.0060")) {
							numVisits++;
						}
						double cal =
							Math.round(v.getZahl() * tl.getAL() * tl.getFactor(kdate, fall)) / 100.0;
						costTarmedAL += cal;
						double ctl =
							Math.round(v.getZahl() * tl.getTL() * tl.getFactor(kdate, fall)) / 100.0;
						costTarmedTL += ctl;
					} else if (vv instanceof PhysioLeistung) {
						costPhysio += cost;
					} else if (vv instanceof Medical || vv instanceof MiGelArtikel) {
						costMedical += cost;
					} else if (vv instanceof Medikament || vv instanceof BAGMedi) {
						costMedikamente += cost;
					} else if (vv instanceof Labor2009Tarif) {
						costLabor += cost;
					} else {
						costOther += cost;
					}
				}
			}
			Rechnung r = k.getRechnung();
			if (r != null) {
				if (!bills.contains(r)) {
					bills.add(r);
				}
			}
		}
		
		String PatientID;
		TimeTool birthDate;
		String sex;
		Integer numCons;
		Integer numVisits;
		Double costTarmedAL;
		Double costTarmedTL;
		Double costMedical;
		Double costMedikamente;
		Double costPhysio;
		Double costLabor;
		Double costOther;
		Double costTotal;
		ArrayList<Rechnung> bills = new ArrayList<Rechnung>();
	}
}
