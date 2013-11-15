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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.Fall;
import ch.elexis.core.data.Konsultation;
import ch.elexis.core.data.Patient;
import ch.elexis.core.data.Verrechnet;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.tarmedprefs.PreferenceConstants;
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
	
	boolean bOptify = true;
	
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
	
	public Result<IVerrechenbar> add(IVerrechenbar code, Konsultation kons){
		bOptify = CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_OPTIFY, true);
		if (code instanceof TarmedLeistung) {
			TarmedLeistung tc = (TarmedLeistung) code;
			List<Verrechnet> lst = kons.getLeistungen();
			boolean checkBezug = false;
			boolean bezugOK = true;
			/*
			 * TODO Hier checken, ob dieser code mit der Dignität und Fachspezialisierung des
			 * aktuellen Mandanten usw. vereinbar ist
			 */
			
			Hashtable ext = ((TarmedLeistung) code).loadExtension();
			
			// Bezug prüfen
			String bezug = (String) ext.get("Bezug"); //$NON-NLS-1$
			if (!StringTool.isNothing(bezug)) {
				checkBezug = true;
				bezugOK = false;
			}
			// Gültigkeit gemäss Datum prüfen
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
			}
			Verrechnet check = null;
			// Ist der Hinzuzufügende Code vielleicht schon in der Liste? Dann
			// nur Zahl erhöhen.
			for (Verrechnet v : lst) {
				if (v.isInstance(code) && TarmedLeistung.getSide(v).equals("none")) {
					check = v;
					check.setZahl(check.getZahl() + 1);
					if (bezugOK) {
						break;
					}
				}
				// "Nur zusammen mit" - Bedingung erfüllt ?
				if (checkBezug && bOptify) {
					if (v.getCode().equals(bezug)) {
						bezugOK = true;
						if (check != null) {
							break;
						}
					}
				}
			}
			// Ausschliessende Kriterien prüfen ("Nicht zusammen mit")
			if (check == null) {
				check = new Verrechnet(code, kons, 1);
				// Exclusionen
				if (bOptify) {
					String excl = (String) ext.get("exclusion"); //$NON-NLS-1$
					if ((!StringTool.isNothing(excl))) {
						for (String e : excl.split(",")) { //$NON-NLS-1$
							for (Verrechnet v : lst) {
								if (v.getCode().equals(e)) {
									check.delete();
									return new Result<IVerrechenbar>(
										Result.SEVERITY.WARNING,
										EXKLUSION,
										code.getCode() + " nicht kombinierbar mit " + e, null, false); //$NON-NLS-1$
								}
								if (v.getVerrechenbar() instanceof TarmedLeistung) {
									String ex2 =
										((TarmedLeistung) v.getVerrechenbar()).getExclusion();
									for (String e2 : ex2.split(",")) { //$NON-NLS-1$
										if (e2.equals(code.getCode())) {
											check.delete();
											return new Result<IVerrechenbar>(
												Result.SEVERITY.WARNING, EXKLUSION, code.getCode()
													+ " nicht kombinierbar mit " + e, null, false); //$NON-NLS-1$
										}
									}
								}
							}
						}
					}
					
					if (check.getCode().equals("00.0750") || check.getCode().equals("00.0010")) {
						String excludeCode = null;
						if (check.getCode().equals("00.0010")) {
							excludeCode = "00.0750";
						} else {
							excludeCode = "00.0010";
						}
						for (Verrechnet v : lst) {
							if (v.getCode().equals(excludeCode)) {
								check.delete();
								return new Result<IVerrechenbar>(
									Result.SEVERITY.WARNING,
									EXKLUSION,
									"00.0750 ist nicht im Rahmen einer ärztlichen Beratung 00.0010 verrechnenbar.", null, false); //$NON-NLS-1$								
							}
						}
					}
				}
				check.setDetail(AL, Integer.toString(tc.getAL()));
				check.setDetail(TL, Integer.toString(tc.getTL()));
				lst.add(check);
			}
			/*
			 * Dies führt zu Fehlern bei Codes mit mehreren Master-Möglichkeiten -> vorerst raus //
			 * "Zusammen mit" - Bedingung nicht erfüllt -> Hauptziffer einfügen. if(checkBezug){
			 * if(bezugOK==false){ TarmedLeistung tl=TarmedLeistung.load(bezug);
			 * Result<IVerrechenbar> r1=add(tl,kons); if(!r1.isOK()){
			 * r1.add(Log.WARNINGS,KOMBINATION,code.getCode()+" nur zusammen mit
			 * "+bezug,null,false); //$NON-NLS-1$ return r1; } } }
			 */
			
			// Prüfen, ob zu oft verrechnet - diese Version prüft nur "pro
			// Sitzung" und "pro Tag".
			if (bOptify) {
				String lim = (String) ext.get("limits"); //$NON-NLS-1$
				if (lim != null) {
					String[] lin = lim.split("#"); //$NON-NLS-1$
					for (String line : lin) {
						String[] f = line.split(","); //$NON-NLS-1$
						if (f.length == 5) {
							switch (Integer.parseInt(f[4].trim())) {
							case 7: // Pro Sitzung
								if (check.getCode().equals("00.0020")) {
									if (CoreHub.mandantCfg.get(
										PreferenceConstants.BILL_ELECTRONICALLY, false)) {
										break;
									}
								}
								// todo check if electronic billing
								if (f[2].equals("1") && f[0].equals("<=")) { // 1 Sitzung //$NON-NLS-1$
									int menge = Math.round(Float.parseFloat(f[1]));
									if (check.getZahl() > menge) {
										check.setZahl(menge);
										return new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
											KUMULATION, Messages.TarmedOptifier_codemax + menge
												+ Messages.TarmedOptifier_perSession, null, false); //$NON-NLS-1$ //$NON-NLS-2$
									}
								}
								break;
							case 21: // Pro Tag
								if (f[2].equals("1") && f[0].equals("<=")) { // 1 Tag
									int menge = Math.round(Float.parseFloat(f[1]));
									if (check.getZahl() > menge) {
										check.setZahl(menge);
										return new Result<IVerrechenbar>(Result.SEVERITY.WARNING,
											KUMULATION, Messages.TarmedOptifier_codemax + menge
												+ "Mal pro Tag", null, false); //$NON-NLS-1$ //$NON-NLS-2$
									}
								}
								
								break;
							default:
								break;
							}
						}
					}
				}
			}
			
			String tcid = code.getCode();
			
			// double factor =
			// PersistentObject.checkZeroDouble(check.get("VK_Scale"));
			// Abzug für Praxis-Op. (alle TL von OP I auf 40% reduzieren)
			if (tcid.equals("35.0020")) { //$NON-NLS-1$
			
				double sum = 0.0;
				for (Verrechnet v : lst) {
					if (v.getVerrechenbar() instanceof TarmedLeistung) {
						TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
						if (tl.getSparteAsText().equals("OP I")) { //$NON-NLS-1$
							/*
							 * int tech = tl.getTL(); double abzug = tech 4.0 / 10.0; sum -= abzug;
							 */
							sum += tl.getTL();
						}
					}
				}
				
				// check.setPreis(new Money(sum));
				check.setTP(sum);
				check.setDetail(TL, Double.toString(sum));
				check.setPrimaryScaleFactor(-0.4);
				/*
				 * double sum=0.0; for(Verrechnet v:lst){ if(v.getVerrechenbar() instanceof
				 * TarmedLeistung){ TarmedLeistung tl=(TarmedLeistung) v.getVerrechenbar();
				 * if(tl.getSparteAsText().equals("OP I")){ int tech=tl.getTL(); sum+=tech; } } }
				 * double scale=-0.4; check.setDetail("scale", Double.toString(scale));
				 * sum=sumfactor/100.0; check.setPreis(new Money(sum));
				 */
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
						if (tlc.matches("29.20[13578]0") || (tlc.equals("29.2200"))) {
							sumAL += (z * tl.getAL()) / 2;
							sumTL += (z * tl.getTL()) / 4;
						}
					}
				}
				check.setTP(sumAL + sumTL);
				check.setDetail(AL, Double.toString(sumAL));
				check.setDetail(TL, Double.toString(sumTL));
			}
			
			// Zuschlag Kinder
			else if (tcid.equals("00.0010") || tcid.equals("00.0060")) {
				if (CoreHub.mandantCfg.get(RechnungsPrefs.PREF_ADDCHILDREN, false)) {
					Fall f = kons.getFall();
					if (f != null) {
						Patient p = f.getPatient();
						if (p != null) {
							String alter = p.getAlter();
							if (Integer.parseInt(alter) < 6) {
								TarmedLeistung tl =
									(TarmedLeistung) TarmedLeistung.getFromCode("00.0040",
										new TimeTool(kons.getDatum()));
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
							sumAL += tl.getAL() * z;
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
				check.setTP(sumAL + sumTL);
				check.setDetail(AL, Double.toString(sumAL));
				check.setDetail(TL, Double.toString(sumTL));
				check.setPrimaryScaleFactor(0.5);
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
							sum += (tl.getAL() * v.getZahl());
							// int summand = tl.getAL() >> 2; // TODO ev. float?
							// -> Rundung?
							// ((sum.addCent(summand * v.getZahl());
						}
					}
					// check.setPreis(sum.multiply(factor));
					check.setTP(sum);
					check.setDetail(AL, Double.toString(sum));
					check.setPrimaryScaleFactor(0.25);
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
							sum += (tl.getAL() * v.getZahl());
						}
					}
					// check.setPreis(sum.multiply(factor));
					check.setTP(sum);
					check.setDetail(AL, Double.toString(sum));
					check.setPrimaryScaleFactor(0.5);
					break;
				
				case 60: // Tel. Mo-Fr 19-22, Sa 12-22, So 7-22: 30 TP
					break;
				case 80: // Tel. von 22-7: 70 TP
					break;
				
				}
				return new Result<IVerrechenbar>(Result.SEVERITY.OK, PREISAENDERUNG,
					"Preis", null, false); //$NON-NLS-1$
			}
			return new Result<IVerrechenbar>(null);
		}
		return new Result<IVerrechenbar>(Result.SEVERITY.ERROR, LEISTUNGSTYP,
			Messages.TarmedOptifier_BadType, null, true); //$NON-NLS-1$
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
		return new Result<Verrechnet>(code);
	}
	
}
