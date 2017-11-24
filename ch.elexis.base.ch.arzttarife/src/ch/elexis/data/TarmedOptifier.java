/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.TarmedKumulation.TarmedKumulationType;
import ch.elexis.data.TarmedLimitation.LimitationUnit;
import ch.elexis.data.importer.TarmedLeistungAge;
import ch.elexis.data.importer.TarmedReferenceDataImporter;
import ch.elexis.tarmedprefs.RechnungsPrefs;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * Dies ist eine Beispielimplementation des IOptifier Interfaces, welches einige einfache Checks von
 * Tarmed-Verrechnungen durchführt
 * 
 * @author gerry
 * 
 */
public class TarmedOptifier implements IOptifier {
	private static final String TL = "TL"; //$NON-NLS-1$
	private static final String AL = "AL"; //$NON-NLS-1$
	public static final int OK = 0;
	public static final int PREISAENDERUNG = 1;
	public static final int KUMULATION = 2;
	public static final int KOMBINATION = 3;
	public static final int EXKLUSION = 4;
	public static final int INKLUSION = 5;
	public static final int LEISTUNGSTYP = 6;
	public static final int NOTYETVALID = 7;
	public static final int NOMOREVALID = 8;
	public static final int PATIENTAGE = 9;
	public static final int EXKLUSIVE = 10;
	
	private static final String CHAPTER_XRAY = "39.02";
	private static final String DEFAULT_TAX_XRAY_ROOM = "39.2000";
	
	boolean bOptify = true;
	private Verrechnet newVerrechnet;
	private String newVerrechnetSide;
	
	/**
	 * Hier kann eine Konsultation als Ganzes nochmal überprüft werden
	 */
	public Result<Object> optify(Konsultation kons){
		LinkedList<TarmedLeistung> postponed = new LinkedList<TarmedLeistung>();
		for (Verrechnet vv : kons.getLeistungen()) {
			IVerrechenbar iv = vv.getVerrechenbar();
			if (iv instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) iv;
				String tcid = tl.getCode();
				if ((tcid.equals("35.0020")) || (tcid.equals("04.1930")) //$NON-NLS-1$ //$NON-NLS-2$
					|| tcid.startsWith("00.25")) { //$NON-NLS-1$
					postponed.add(tl);
				}
			}
		}
		return null;
	}
	
	/**
	 * Eine Verrechnungsposition zufügen. Der Optifier muss prüfen, ob die Verrechnungsposition im
	 * Kontext der übergebenen Konsultation verwendet werden kann und kann sie ggf. zurückweisen
	 * oder modifizieren.
	 */
	
	public Result<IVerrechenbar> add(IVerrechenbar code, Konsultation kons) {
		if (!(code instanceof TarmedLeistung)) {
			return new Result<IVerrechenbar>(Result.SEVERITY.ERROR, LEISTUNGSTYP, Messages.TarmedOptifier_BadType, null,
					true);
		}

		bOptify = CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_OPTIFY, true);

		TarmedLeistung tc = (TarmedLeistung) code;
		List<Verrechnet> lst = kons.getLeistungen();
		/*
		 * TODO Hier checken, ob dieser code mit der Dignität und
		 * Fachspezialisierung des aktuellen Mandanten usw. vereinbar ist
		 */

		Hashtable<String, String> ext = tc.loadExtension();

		// Gültigkeit gemäss Datum und Alter prüfen
		if (bOptify) {
			TimeTool date = new TimeTool(kons.getDatum());
			String dVon = ((TarmedLeistung) code).get("GueltigVon"); //$NON-NLS-1$
			if (!StringTool.isNothing(dVon)) {
				TimeTool tVon = new TimeTool(dVon);
				if (date.isBefore(tVon)) {
					return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, NOTYETVALID,
							code.getCode() + Messages.TarmedOptifier_NotYetValid, null, false);
				}
			}
			String dBis = ((TarmedLeistung) code).get("GueltigBis"); //$NON-NLS-1$
			if (!StringTool.isNothing(dBis)) {
				TimeTool tBis = new TimeTool(dBis);
				if (date.isAfter(tBis)) {
					return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, NOMOREVALID,
							code.getCode() + Messages.TarmedOptifier_NoMoreValid, null, false);
				}
			}
			String ageLimits = ext.get(TarmedLeistung.EXT_FLD_SERVICE_AGE);
			if (ageLimits != null && !ageLimits.isEmpty()) {
				String errorMessage = checkAge(ageLimits, kons);
				if (errorMessage != null) {
					return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, PATIENTAGE,
						errorMessage, null, false);
				}
			}
		}
		newVerrechnet = null;
		newVerrechnetSide = null;
		// Korrekter Fall Typ prüfen, und ggf. den code ändern
		if (tc.getCode().matches("39.002[01]") || tc.getCode().matches("39.001[0156]")) {
			String gesetz = kons.getFall().getRequiredString("Gesetz");
			if (gesetz == null || gesetz.isEmpty()) {
				gesetz = kons.getFall().getAbrechnungsSystem();
			}

			if (gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0011")) {
				return this.add(getKonsVerrechenbar("39.0010", kons), kons);
			} else if (!gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0010")) {
				return this.add(getKonsVerrechenbar("39.0011", kons), kons);
			}

			if (gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0016")) {
				return this.add(getKonsVerrechenbar("39.0015", kons), kons);
			} else if (!gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0015")) {
				return this.add(getKonsVerrechenbar("39.0016", kons), kons);
			}

			if (gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0021")) {
				return this.add(getKonsVerrechenbar("39.0020", kons), kons);
			} else if (!gesetz.equalsIgnoreCase("KVG") && tc.getCode().matches("39.0020")) {
				return this.add(getKonsVerrechenbar("39.0021", kons), kons);
			}
		}

		if (tc.getCode().matches("35.0020")) {
			List<Verrechnet> opCodes = getOPList(lst);
			List<Verrechnet> opReduction = getVerrechnetMatchingCode(lst, "35.0020");
			// updated reductions to codes, and get not yet reduced codes
			List<Verrechnet> availableCodes = updateOPReductions(opCodes, opReduction);
			if (availableCodes.isEmpty()) {
				return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, KOMBINATION,
					code.getCode(), null, false);
			}
			for (Verrechnet verrechnet : availableCodes) {
				newVerrechnet = new Verrechnet(tc, kons, 1);
				mapOpReduction(verrechnet, newVerrechnet);
			}
			return new Result<IVerrechenbar>(null);
		}
		
		// Ist der Hinzuzufügende Code vielleicht schon in der Liste? Dann
		// nur Zahl erhöhen.
		for (Verrechnet v : lst) {
			if (v.isInstance(code)) {
				if (!tc.requiresSide()) {
					newVerrechnet = v;
					newVerrechnet.setZahl(newVerrechnet.getZahl() + 1);
					break;
				}
			}
		}
		
		if (tc.requiresSide()) {
			newVerrechnetSide = getNewVerrechnetSideOrIncrement(code, lst);
		}
		
		// Ausschliessende Kriterien prüfen ("Nicht zusammen mit")
		if (newVerrechnet == null) {
			newVerrechnet = new Verrechnet(code, kons, 1);
			// make sure side is initialized
			if (tc.requiresSide()) {
				newVerrechnet.setDetail(TarmedLeistung.SIDE, newVerrechnetSide);
			}
			// Exclusionen
			if (bOptify) {
				TarmedLeistung newTarmed = (TarmedLeistung) code;
				for (Verrechnet v : lst) {
					if (v.getVerrechenbar() instanceof TarmedLeistung) {
						TarmedLeistung tarmed = (TarmedLeistung) v.getVerrechenbar();
						if (tarmed != null && tarmed.exists()) {
							// check if new has an exclusion for this verrechnet
							// tarmed
							Result<IVerrechenbar> resCompatible =
								isCompatible(tarmed, newTarmed, kons);
							if (resCompatible.isOK()) {
								// check if existing tarmed has exclusion for
								// new one
								resCompatible = isCompatible(newTarmed, tarmed, kons);
							}

							if (!resCompatible.isOK()) {
								newVerrechnet.delete();
								return resCompatible;
							}
						}
					}
				}
				
				if (newVerrechnet.getCode().equals("00.0750") || newVerrechnet.getCode().equals("00.0010")) {
					String excludeCode = null;
					if (newVerrechnet.getCode().equals("00.0010")) {
						excludeCode = "00.0750";
					} else {
						excludeCode = "00.0010";
					}
					for (Verrechnet v : lst) {
						if (v.getCode().equals(excludeCode)) {
							newVerrechnet.delete();
							return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, EXKLUSION,
									"00.0750 ist nicht im Rahmen einer ärztlichen Beratung 00.0010 verrechnenbar.", //$NON-NLS-1$
									null, false);
						}
					}
				}
			}
			newVerrechnet.setDetail(AL, Integer.toString(tc.getAL(kons.getMandant())));
			newVerrechnet.setDetail(TL, Integer.toString(tc.getTL()));
			lst.add(newVerrechnet);
		}
		
		// set bezug of zuschlagsleistung and referenzleistung
		if (isReferenceInfoAvailable() && shouldDetermineReference(tc)) {
			// lookup available masters
			List<Verrechnet> masters = getPossibleMasters(newVerrechnet, lst);
			if (masters.isEmpty()) {
				int zahl = newVerrechnet.getZahl();
				if (zahl > 1) {
					newVerrechnet.setZahl(zahl - 1);
				} else {
					newVerrechnet.delete();
				}
				return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, KOMBINATION,
					"Für die Zuschlagsleistung " + code.getCode()
						+ " konnte keine passende Hauptleistung gefunden werden.",
					null, false);
			}
			if (!masters.isEmpty()) {
				String bezug = newVerrechnet.getDetail("Bezug");
				if (bezug == null) {
					// set bezug to first available master
					newVerrechnet.setDetail("Bezug", masters.get(0).getCode());
				} else {
					boolean found = false;
					// lookup matching, or create new Verrechnet
					for (Verrechnet mVerr : masters) {
						if (mVerr.getCode().equals(bezug)) {
							// just mark as found as amount is already increased
							found = true;
						}
					}
					if (!found) {
						// create a new Verrechent and decrease amount
						newVerrechnet.setZahl(newVerrechnet.getZahl() - 1);
						newVerrechnet = new Verrechnet(code, kons, 1);
						newVerrechnet.setDetail("Bezug", masters.get(0).getCode());
					}
				}
			}
		}

		Result<IVerrechenbar> limitResult = checkLimitations(kons, tc, newVerrechnet);
		if (!limitResult.isOK()) {
			return limitResult;
		}

		String tcid = code.getCode();

		// check if it's an X-RAY service and add default tax if so
		// default xray tax will only be added once (see above)
		if (!tc.getCode().equals(DEFAULT_TAX_XRAY_ROOM) && !tc.getCode().matches("39.002[01]")
			&& tc.getParent().startsWith(CHAPTER_XRAY)) {
			if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_OPTIFY_XRAY, true)) {
				add(getKonsVerrechenbar(DEFAULT_TAX_XRAY_ROOM, kons), kons);
				// add 39.0020, will be changed according to case (see above)
				add(getKonsVerrechenbar("39.0020", kons), kons);
			}
		}
		
		// Interventionelle Schmerztherapie: Zuschlag cervical und thoracal
		else if (tcid.equals("29.2090")) {
			double sumAL = 0.0;
			double sumTL = 0.0;
			for (Verrechnet v : lst) {
				if (v.getVerrechenbar() instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
					String tlc = tl.getCode();
					double z = v.getZahl();
					if (tlc.matches("29.20[12345678]0") || (tlc.equals("29.2200"))) {
						sumAL += (z * tl.getAL(kons.getMandant())) / 2;
						sumTL += (z * tl.getTL()) / 4;
					}
				}
			}
			newVerrechnet.setTP(sumAL + sumTL);
			newVerrechnet.setDetail(AL, Double.toString(sumAL));
			newVerrechnet.setDetail(TL, Double.toString(sumTL));
		}

		// Zuschlag Kinder
		else if (tcid.equals("00.0010") || tcid.equals("00.0060")) {
			if (CoreHub.mandantCfg != null && CoreHub.mandantCfg.get(RechnungsPrefs.PREF_ADDCHILDREN, false)) {
				Fall f = kons.getFall();
				if (f != null) {
					Patient p = f.getPatient();
					if (p != null) {
						String alter = p.getAlter();
						if (Integer.parseInt(alter) < 6) {
							TarmedLeistung tl =
								(TarmedLeistung) getKonsVerrechenbar("00.0040", kons);
							add(tl, kons);
						}
					}
				}
			}
		}

		// Zuschläge für Insellappen 50% auf AL und TL bei 1910,20,40,50
		else if (tcid.equals("04.1930")) { //$NON-NLS-1$
			double sumAL = 0.0;
			double sumTL = 0.0;
			for (Verrechnet v : lst) {
				if (v.getVerrechenbar() instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
					String tlc = tl.getCode();
					int z = v.getZahl();
					if (tlc.equals("04.1910") || tlc.equals("04.1920") || tlc.equals("04.1940") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							|| tlc.equals("04.1950")) { //$NON-NLS-1$
						sumAL += tl.getAL(kons.getMandant()) * z;
						sumTL += tl.getTL() * z;
						// double al = (tl.getAL() * 15) / 10.0;
						// double tel = (tl.getTL() * 15) / 10.0;
						// sum += al * z;
						// sum += tel * z;
					}
				}
			}
			// sum = sum * factor / 100.0;
			// check.setPreis(new Money(sum));
			newVerrechnet.setTP(sumAL + sumTL);
			newVerrechnet.setDetail(AL, Double.toString(sumAL));
			newVerrechnet.setDetail(TL, Double.toString(sumTL));
			newVerrechnet.setPrimaryScaleFactor(0.5);
		}
		// Zuschläge für 04.0620 sollte sich diese mit 70% auf die Positionen 04.0630 & 04.0640 beziehen
		else if (tcid.equals("04.0620")) {
			double sumAL = 0.0;
			double sumTL = 0.0;
			for (Verrechnet v : lst) {
				if (v.getVerrechenbar() instanceof TarmedLeistung) {
					TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
					String tlc = tl.getCode();
					int z = v.getZahl();
					if (tlc.equals("04.0610") || tlc.equals("04.0630") || tlc.equals("04.0640")) {
						sumAL += tl.getAL(kons.getMandant()) * z;
						sumTL += tl.getTL() * z;
					}
				}
			}
			newVerrechnet.setTP(sumAL + sumTL);
			newVerrechnet.setDetail(AL, Double.toString(sumAL));
			newVerrechnet.setDetail(TL, Double.toString(sumTL));
			newVerrechnet.setPrimaryScaleFactor(0.7);
		}

		// Notfall-Zuschläge
		if (tcid.startsWith("00.25")) { //$NON-NLS-1$
			double sum = 0.0;
			int subcode = Integer.parseInt(tcid.substring(5));
			switch (subcode) {
			case 10: // Mo-Fr 7-19, Sa 7-12: 60 TP
				break;
			case 20: // Mo-Fr 19-22, Sa 12-22, So 7-22: 120 TP
				break;
			case 30: // 25% zu allen AL von 20
			case 70: // 25% zu allen AL von 60 (tel.)
				for (Verrechnet v : lst) {
					if (v.getVerrechenbar() instanceof TarmedLeistung) {
						TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
						if (tl.getCode().startsWith("00.25")) { //$NON-NLS-1$
							continue;
						}
						sum += (tl.getAL(kons.getMandant()) * v.getZahl());
						// int summand = tl.getAL() >> 2; // TODO ev. float?
						// -> Rundung?
						// ((sum.addCent(summand * v.getZahl());
					}
				}
				// check.setPreis(sum.multiply(factor));
				newVerrechnet.setTP(sum);
				newVerrechnet.setDetail(AL, Double.toString(sum));
				newVerrechnet.setPrimaryScaleFactor(0.25);
				break;
			case 40: // 22-7: 180 TP
				break;
			case 50: // 50% zu allen AL von 40
			case 90: // 50% zu allen AL von 70 (tel.)
				for (Verrechnet v : lst) {
					if (v.getVerrechenbar() instanceof TarmedLeistung) {
						TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
						if (tl.getCode().startsWith("00.25")) { //$NON-NLS-1$
							continue;
						}
						// int summand = tl.getAL() >> 1;
						// sum.addCent(summand * v.getZahl());
						sum += (tl.getAL(kons.getMandant()) * v.getZahl());
					}
				}
				// check.setPreis(sum.multiply(factor));
				newVerrechnet.setTP(sum);
				newVerrechnet.setDetail(AL, Double.toString(sum));
				newVerrechnet.setPrimaryScaleFactor(0.5);
				break;

			case 60: // Tel. Mo-Fr 19-22, Sa 12-22, So 7-22: 30 TP
				break;
			case 80: // Tel. von 22-7: 70 TP
				break;

			}
			return new Result<IVerrechenbar>(Result.SEVERITY.OK, PREISAENDERUNG, "Preis", null, false); //$NON-NLS-1$
		}
		return new Result<IVerrechenbar>(null);
	}
	
	private Result<IVerrechenbar> checkLimitations(Konsultation kons, TarmedLeistung tarmedLeistung,
		Verrechnet newVerrechnet){
		if (bOptify) {
			// service limitations
			List<TarmedLimitation> limitations = tarmedLeistung.getLimitations();
			for (TarmedLimitation tarmedLimitation : limitations) {
				if (tarmedLimitation.isTestable()) {
					Result<IVerrechenbar> result = tarmedLimitation.test(kons, newVerrechnet);
					if (!result.isOK()) {
						return result;
					}
				}
			}
			// group limitations
			TimeTool date = new TimeTool(kons.getDatum());
			List<String> groups = tarmedLeistung.getServiceGroups(date);
			for (String groupName : groups) {
				Optional<TarmedGroup> group =
					TarmedGroup.find(groupName, tarmedLeistung.get(TarmedLeistung.FLD_LAW), date);
				if (group.isPresent()) {
					limitations = group.get().getLimitations();
					for (TarmedLimitation tarmedLimitation : limitations) {
						if (tarmedLimitation.isTestable()) {
							Result<IVerrechenbar> result =
								tarmedLimitation.test(kons, newVerrechnet);
							if (!result.isOK()) {
								return result;
							}
						}
					}
				}
			}
		}
		return new Result<IVerrechenbar>(null);
	}
	
	private String checkAge(String limitsString, Konsultation kons){
		LocalDateTime consDate = new TimeTool(kons.getDatum()).toLocalDateTime();
		Patient patient = kons.getFall().getPatient();
		long patientAgeDays = patient.getAgeAt(consDate, ChronoUnit.DAYS);
		
		List<TarmedLeistungAge> ageLimits = TarmedLeistungAge.of(limitsString);
		for (TarmedLeistungAge tarmedLeistungAge : ageLimits) {
			if (tarmedLeistungAge.isValidOn(consDate.toLocalDate())) {
				// if only one of the limits is set, check only that limit
				if (tarmedLeistungAge.getFromDays() >= 0 && !(tarmedLeistungAge.getToDays() >= 0)) {
					if (patientAgeDays < tarmedLeistungAge.getFromDays()) {
						return "Patient ist zu jung, verrechenbar ab "
							+ tarmedLeistungAge.getFromText();
					}
				} else if (tarmedLeistungAge.getToDays() >= 0
					&& !(tarmedLeistungAge.getFromDays() >= 0)) {
					if (patientAgeDays > tarmedLeistungAge.getToDays()) {
						return "Patient ist zu alt, verrechenbar bis "
							+ tarmedLeistungAge.getToText();
					}
				} else if (tarmedLeistungAge.getToDays() >= 0
					&& tarmedLeistungAge.getFromDays() >= 0) {
					if (tarmedLeistungAge.getToDays() < tarmedLeistungAge.getFromDays()) {
						if (patientAgeDays > tarmedLeistungAge.getToDays()
							&& patientAgeDays < tarmedLeistungAge.getFromDays()) {
							return "Patienten Alter nicht ok, verrechenbar "
								+ tarmedLeistungAge.getText();
						}
					} else {
						if (patientAgeDays > tarmedLeistungAge.getToDays()
							|| patientAgeDays < tarmedLeistungAge.getFromDays()) {
							return "Patienten Alter nicht ok, verrechenbar "
								+ tarmedLeistungAge.getText();
						}
					}
				}
			}
		}
		return null;
	}
	
	private IVerrechenbar getKonsVerrechenbar(String code, Konsultation kons){
		TimeTool date = new TimeTool(kons.getDatum());
		if (kons.getFall() != null) {
			String law = kons.getFall().getRequiredString("Gesetz");
			return TarmedLeistung.getFromCode(code, date, law);
		}
		return null;
	}
	
	private boolean isReferenceInfoAvailable(){
		return CoreHub.globalCfg.get(TarmedReferenceDataImporter.CFG_REFERENCEINFO_AVAILABLE,
			false);
	}
	
	private boolean shouldDetermineReference(TarmedLeistung tc){
		String typ = tc.getServiceTyp();
		boolean becauseOfType = typ.equals("Z");
		if (becauseOfType) {
			String text = tc.getText();
			return text.startsWith("+") || text.startsWith("-");
		}
		return false;
	}
	
	private List<Verrechnet> getAvailableMasters(TarmedLeistung slave, List<Verrechnet> lst){
		List<Verrechnet> ret = new LinkedList<Verrechnet>();
		TimeTool konsDate = null;
		for (Verrechnet v : lst) {
			if (konsDate == null) {
				konsDate = new TimeTool(v.getKons().getDatum());
			}
			if (v.getVerrechenbar() instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
				if (tl.getHierarchy(konsDate).contains(slave.getCode())) { //$NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}
	
	private List<Verrechnet> getPossibleMasters(Verrechnet newSlave, List<Verrechnet> lst){
		TarmedLeistung slaveTarmed = (TarmedLeistung) newSlave.getVerrechenbar();
		// lookup available masters
		List<Verrechnet> masters = getAvailableMasters(slaveTarmed, lst);
		// check which masters are left to be referenced
		int maxPerMaster = getMaxPerMaster(slaveTarmed);
		if (maxPerMaster > 0) {
			Map<Verrechnet, List<Verrechnet>> masterSlavesMap = getMasterToSlavesMap(newSlave, lst);
			for (Verrechnet master : masterSlavesMap.keySet()) {
				int masterCount = master.getZahl();
				int slaveCount = 0;
				for(Verrechnet slave : masterSlavesMap.get(master)) {
					slaveCount += slave.getZahl();
					if(slave == newSlave) {
						slaveCount--;
					}
				}
				if (masterCount <= (slaveCount * maxPerMaster)) {
					masters.remove(master);
				}
			}
		}
		return masters;
	}
	
	/**
	 * Creates a map of masters associated to slaves by the Bezug. This map will not contain the
	 * newSlave, as it has no Bezug set yet.
	 * 
	 * @param newSlave
	 * @param lst
	 * @return
	 */
	private Map<Verrechnet, List<Verrechnet>> getMasterToSlavesMap(Verrechnet newSlave,
		List<Verrechnet> lst){
		Map<Verrechnet, List<Verrechnet>> ret = new HashMap<>();
		TarmedLeistung slaveTarmed = (TarmedLeistung) newSlave.getVerrechenbar();
		// lookup available masters
		List<Verrechnet> masters = getAvailableMasters(slaveTarmed, lst);
		for (Verrechnet verrechnet : masters) {
			ret.put(verrechnet, new ArrayList<Verrechnet>());
		}
		// lookup other slaves with same code
		List<Verrechnet> slaves = getVerrechnetMatchingCode(lst, newSlave.getCode());
		// add slaves to separate master list
		for (Verrechnet slave : slaves) {
			String bezug = slave.getDetail("Bezug");
			if (bezug != null && !bezug.isEmpty()) {
				for (Verrechnet master : ret.keySet()) {
					if (master.getCode().equals(bezug)) {
						ret.get(master).add(slave);
					}
				}
			}
		}
		return ret;
	}
	
	private int getMaxPerMaster(TarmedLeistung slave){
		List<TarmedLimitation> limits = slave.getLimitations();
		for (TarmedLimitation limit : limits) {
			if (limit.getLimitationUnit() == LimitationUnit.MAINSERVICE) {
				// only an integer makes sense here
				return (int) limit.getAmount();
			}
		}
		// default to unknown
		return -1;
	}
	
	/**
	 * Create a new mapping between an OP I reduction (35.0020) and a service from the OP I section.
	 * 
	 * @param opVerrechnet
	 *            Verrechnet representing a service from the OP I section
	 * @param reductionVerrechnet
	 *            Verrechnet representing the OP I reduction (35.0020)
	 */
	private void mapOpReduction(Verrechnet opVerrechnet, Verrechnet reductionVerrechnet){
		TarmedLeistung opVerrechenbar = (TarmedLeistung) opVerrechnet.getVerrechenbar();
		reductionVerrechnet.setZahl(opVerrechnet.getZahl());
		reductionVerrechnet.setDetail(TL, Double.toString(opVerrechenbar.getTL()));
		reductionVerrechnet.setDetail(AL, Double.toString(0.0));
		reductionVerrechnet.setTP(opVerrechenbar.getTL());
		reductionVerrechnet.setPrimaryScaleFactor(-0.4);
		reductionVerrechnet.setDetail("Bezug", opVerrechenbar.getCode());
	}
	
	/**
	 * Update existing OP I reductions (35.0020), and return a list of all not yet mapped OP I
	 * services.
	 * 
	 * @param opCodes
	 *            list of all available OP I codes see {@link #getOPList(List)}
	 * @param opReduction
	 *            list of all available reduction codes see {@link #getVerrechnetMatchingCode(List)}
	 * @return list of not unmapped OP I codes
	 */
	private List<Verrechnet> updateOPReductions(List<Verrechnet> opCodes,
		List<Verrechnet> opReduction){
		List<Verrechnet> notMappedCodes = new ArrayList<Verrechnet>();
		notMappedCodes.addAll(opCodes);
		// update already mapped
		for (Verrechnet reductionVerrechnet : opReduction) {
			boolean isMapped = false;
			String bezug = reductionVerrechnet.getDetail("Bezug");
			if (bezug != null && !bezug.isEmpty()) {
				for (Verrechnet opVerrechnet : opCodes) {
					TarmedLeistung opVerrechenbar = (TarmedLeistung) opVerrechnet.getVerrechenbar();
					String opCodeString = opVerrechenbar.getCode();
					if (bezug.equals(opCodeString)) {
						// update
						reductionVerrechnet.setZahl(opVerrechnet.getZahl());	
						reductionVerrechnet.setDetail(TL, Double.toString(opVerrechenbar.getTL()));
						reductionVerrechnet.setDetail(AL, Double.toString(0.0));
						reductionVerrechnet.setPrimaryScaleFactor(-0.4);
						notMappedCodes.remove(opVerrechnet);
						isMapped = true;
						break;
					}
				}
			}
			if (!isMapped) {
				reductionVerrechnet.setZahl(0);
				reductionVerrechnet.setDetail("Bezug", "");
			}
		}
		
		return notMappedCodes;
	}
	
	private List<Verrechnet> getOPList(List<Verrechnet> lst){
		List<Verrechnet> ret = new ArrayList<Verrechnet>();
		for (Verrechnet v : lst) {
			if (v.getVerrechenbar() instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
				if (tl.getSparteAsText().equals("OP I")) { //$NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}
	
	private List<Verrechnet> getVerrechnetMatchingCode(List<Verrechnet> lst, String code){
		List<Verrechnet> ret = new ArrayList<Verrechnet>();
		for (Verrechnet v : lst) {
			if (v.getVerrechenbar() instanceof TarmedLeistung) {
				TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
				if (tl.getCode().equals(code)) { //$NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}
	
	private List<Verrechnet> getVerrechnetWithBezugMatchingCode(List<Verrechnet> lst, String code){
		List<Verrechnet> ret = new ArrayList<Verrechnet>();
		for (Verrechnet v : lst) {
			if (v.getVerrechenbar() instanceof TarmedLeistung) {
				if (code.equals(v.getDetail("Bezug"))) { //$NON-NLS-1$
					ret.add(v);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Always toggle the side of a specific code. Starts with left, then right, then add to the
	 * respective side.
	 * 
	 * @param code
	 * @param lst
	 * @return
	 */
	private String getNewVerrechnetSideOrIncrement(IVerrechenbar code, List<Verrechnet> lst){
		int countSideLeft = 0;
		Verrechnet leftVerrechnet = null;
		int countSideRight = 0;
		Verrechnet rightVerrechnet = null;
		
		for (Verrechnet v : lst) {
			if (v.isInstance(code)) {
				String side = v.getDetail(TarmedLeistung.SIDE);
				if (side.equals(TarmedLeistung.SIDE_L)) {
					countSideLeft += v.getZahl();
					leftVerrechnet = v;
				} else {
					countSideRight += v.getZahl();
					rightVerrechnet = v;
				}
			}
		}
		
		if (countSideLeft > 0 || countSideRight > 0) {
			if ((countSideLeft > countSideRight) && rightVerrechnet != null) {
				newVerrechnet = rightVerrechnet;
				newVerrechnet.setZahl(newVerrechnet.getZahl() + 1);
			} else if ((countSideLeft <= countSideRight) && leftVerrechnet != null) {
				newVerrechnet = leftVerrechnet;
				newVerrechnet.setZahl(newVerrechnet.getZahl() + 1);
			} else if ((countSideLeft > countSideRight) && rightVerrechnet == null) {
				return TarmedLeistung.SIDE_R;
			}
		}
		return TarmedLeistung.SIDE_L;
	}
	
	/**
	 * check compatibility of one tarmed with another
	 * 
	 * @param tarmedCode
	 *            the tarmed and it's parents code are check whether they have to be excluded
	 * @param tarmed
	 *            TarmedLeistung who incompatibilities are examined
	 * @param kons
	 *            {@link Konsultation} providing context
	 * @return true OK if they are compatible, WARNING if it matches an exclusion case
	 */
	public Result<IVerrechenbar> isCompatible(TarmedLeistung tarmedCode, TarmedLeistung tarmed,
		Konsultation kons){
		TimeTool date = new TimeTool(kons.getDatum());
		List<TarmedExclusion> exclusions = tarmed.getExclusions(kons);
		for (TarmedExclusion tarmedExclusion : exclusions) {
			if (tarmedExclusion.isMatching(tarmedCode, date)) {
				return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, EXKLUSION,
					tarmed.getCode() + " nicht kombinierbar mit " + tarmedExclusion.toString(), //$NON-NLS-1$
					null, false);
			}
		}
		List<String> groups = tarmed.getServiceGroups(date);
		for (String groupName : groups) {
			Optional<TarmedGroup> group =
				TarmedGroup.find(groupName, tarmed.get(TarmedLeistung.FLD_LAW), date);
			if (group.isPresent()) {
				List<TarmedExclusion> groupExclusions = group.get().getExclusions(kons);
				for (TarmedExclusion tarmedExclusion : groupExclusions) {
					if (tarmedExclusion.isMatching(tarmedCode, date)) {
						return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, EXKLUSION,
							tarmed.getCode() + " nicht kombinierbar mit " //$NON-NLS-1$
								+ tarmedExclusion.toString(),
							null, false);
					}
				}
			}
		}
		List<String> blocks = tarmed.getServiceBlocks(date);
		for (String blockName : blocks) {
			List<TarmedExclusive> exclusives = TarmedKumulation.getExclusives(blockName,
				TarmedKumulationType.BLOCK, date, tarmed.get(TarmedLeistung.FLD_LAW));
			// currently only test blocks exclusives, exclude hierarchy matches
			if (canHandleAllExculives(exclusives)
				&& !isMatchingHierarchy(tarmedCode, tarmed, date)) {
				boolean included = false;
				for (TarmedExclusive tarmedExclusive : exclusives) {
					if (tarmedExclusive.isMatching(tarmedCode, date)) {
						included = true;
					}
				}
				if (!included) {
					return new Result<IVerrechenbar>(Result.SEVERITY.WARNING, EXKLUSIVE,
						tarmed.getCode() + " nicht kombinierbar mit " //$NON-NLS-1$
							+ tarmedCode.getCode() + ", wegen Block Kumulation",
						null, false);
				}
			}
		}
		return new Result<IVerrechenbar>(Result.SEVERITY.OK, OK, "compatible", null, false);
	}
	
	private boolean isMatchingHierarchy(TarmedLeistung tarmedCode, TarmedLeistung tarmed,
		TimeTool date){
		return tarmed.getHierarchy(date).contains(tarmedCode.getCode());
	}
	
	/**
	 * Test if we can handle all {@link TarmedExclusive}.
	 * 
	 * @param exclusives
	 * @return
	 */
	private boolean canHandleAllExculives(List<TarmedExclusive> exclusives){
		for (TarmedExclusive tarmedExclusive : exclusives) {
			if (tarmedExclusive.getSlaveType() != TarmedKumulationType.BLOCK
				&& tarmedExclusive.getSlaveType() != TarmedKumulationType.CHAPTER
				&& tarmedExclusive.getSlaveType() != TarmedKumulationType.SERVICE) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Eine Verrechnungsposition entfernen. Der Optifier sollte prüfen, ob die Konsultation nach
	 * Entfernung dieses Codes noch konsistent verrechnet wäre und ggf. anpassen oder das Entfernen
	 * verweigern. Diese Version macht keine Prüfungen, sondern erfüllt nur die Anfrage..
	 */
	public Result<Verrechnet> remove(Verrechnet code, Konsultation kons){
		List<Verrechnet> l = kons.getLeistungen();
		l.remove(code);
		code.delete();
		// if no more left, check for bezug and remove
		List<Verrechnet> left = getVerrechnetMatchingCode(l, code.getCode());
		if (left.isEmpty()) {
			List<Verrechnet> verrechnetWithBezug =
				getVerrechnetWithBezugMatchingCode(kons.getLeistungen(), code.getCode());
			for (Verrechnet verrechnet : verrechnetWithBezug) {
				remove(verrechnet, kons);
			}
		}
		return new Result<Verrechnet>(code);
	}

	@Override
	public Verrechnet getCreatedVerrechnet(){
		return newVerrechnet;
	}
	
}
