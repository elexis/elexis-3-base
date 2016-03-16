/*******************************************************************************
 * Copyright (c) 2009, SGAM Informatics and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.icpc.fire.handlers;

import java.util.HashMap;

import org.jdom.Element;

import ch.elexis.base.befunde.xchange.XChangeContributor;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.Konsultation;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.icpc.Encounter;
import ch.elexis.icpc.IcpcCode;
import ch.elexis.icpc.fire.ui.Preferences;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class Analyzer {
	private final Konsultation mine;
	private final XChangeContributor xc = new XChangeContributor();
	private final Patient pat;
	private String bdSystTab, bdDiastTab, pulseTab, heightTab, weightTab, waistTab;
	
	public void addVitalElement(Element eKons){
		Element elVital = new Element("vital");
		readVital(elVital);
		eKons.addContent(elVital);
	}
	
	public void addMediElements(Element eKons){
		Query<Prescription> qbe = new Query<Prescription>(Prescription.class);
		qbe.add("DatumVon", "=", mine.getDatum());
		for (Prescription p : qbe.execute()) {
			Element eMedi = new Element("medi");
			Artikel art = p.getArtikel();
			String pk = art.getPharmaCode();
			addElement(eMedi, "pharmacode", pk);
			String dose = p.getDosis();
			if (!StringTool.isNothing(dose)) {
				String[] ds = dose.split("\\s*\\-\\s*");
				addElement(eMedi, "dosismo", ds[0]);
				if (ds.length > 1) {
					addElement(eMedi, "dosismi", ds[1]);
					if (ds.length > 2) {
						addElement(eMedi, "dosisab", ds[2]);
						if (ds.length > 3) {
							addElement(eMedi, "dosisna", ds[3]);
						}
					}
				}
			}
			eKons.addContent(eMedi);
		}
	}
	
	public void addDiagnoseElement(Element eKons){
		Element eDiag = null;
		Query<Encounter> qbe = new Query<Encounter>(Encounter.class);
		qbe.add("KonsID", "=", mine.getId());
		for (Encounter enc : qbe.execute()) {
			IcpcCode diag = enc.getDiag();
			if (diag != null) {
				if (eDiag == null) {
					eDiag = new Element("diagnose");
				}
				addElement(eDiag, "icpc", diag.getCode());
			}
		}
		if (eDiag != null) {
			eKons.addContent(eDiag);
		}
	}
	
	public void addLaborElements(Element eKons){
		TimeTool ttDate = new TimeTool(mine.getDatum());
		Query<LabResult> qbe = new Query<LabResult>(LabResult.class);
		qbe.add("PatientID", "=", pat.getId());
		qbe.add("Datum", "=", ttDate.toString(TimeTool.DATE_COMPACT));
		for (LabResult lr : qbe.execute()) {
			LabItem li = (LabItem) lr.getItem();
			Element eLab = new Element("labor");
			addElement(eLab, "labordate", ttDate.toString(TimeTool.DATE_ISO));
			addElement(eLab, "quelle", li.getLabor().get("Bezeichnung1"));
			addElement(eLab, "analyse", li.getName());
			addElement(eLab, "einheit", li.getEinheit());
			String ref = li.getRefM();
			if (pat.getGeschlecht().equals(Person.FEMALE)) {
				ref = li.getRefW();
			}
			String[] rx = ref.split("\\s*\\-\\s*");
			if (rx.length > 1) {
				addElement(eLab, "min", rx[0]);
				addElement(eLab, "max", rx[1]);
			}
			addElement(eLab, "laborwert", lr.getResult());
		}
	}
	
	public Analyzer(Konsultation k){
		mine = k;
		pat = k.getFall().getPatient();
		if (pat != null) {
			bdSystTab = getOrFail(Preferences.CFG_BD_SYST);
			if (bdSystTab != null) {
				bdDiastTab = getOrFail(Preferences.CFG_BD_DIAST);
				if (bdDiastTab != null) {
					pulseTab = getOrFail(Preferences.CFG_PULS);
					if (pulseTab != null) {
						heightTab = getOrFail(Preferences.CFG_HEIGHT);
						if (heightTab != null) {
							weightTab = getOrFail(Preferences.CFG_WEIGHT);
							if (weightTab != null) {
								waistTab = getOrFail(Preferences.CFG_BU);
								if (waistTab != null) {
									xc.setPatient(pat);
								}
							}
							
						}
					}
				}
			}
		}
	}
	
	private void readVital(Element el){
		String[] split = bdSystTab.split("\\s*\\:\\s*");
		String bdsyst = null, bddiast = null;
		if (split.length > 1) {
			HashMap<String, String> vals = xc.getResult(split[0].trim(), mine.getDatum());
			
			if (bdSystTab.equals(bdDiastTab)) {
				String bd = vals.get(split[1].trim());
				if (bd != null) {
					String[] bds = bd.split("\\s*\\/\\s*");
					if (bds.length > 1) {
						bdsyst = bds[0].trim();
						bddiast = bds[1].trim();
					}
				}
				
			} else {
				bdsyst = vals.get(split[1]).trim();
				split = bdDiastTab.split("\\s:\\s");
				if (split.length > 1) {
					vals = xc.getResult(split[0].trim(), mine.getDatum());
					bddiast = vals.get(split[1]).trim();
					
				}
			}
		}
		if (bdsyst != null) {
			Element en = new Element("bdsyst");
			en.setText(bdsyst);
			el.addContent(en);
		}
		if (bddiast != null) {
			Element en = new Element("bddiast");
			en.setText(bddiast);
			el.addContent(en);
		}
		addVitalParm(el, "puls", pulseTab);
		addVitalParm(el, "groesse", heightTab);
		addVitalParm(el, "gewicht", weightTab);
		addVitalParm(el, "bauchumfang", waistTab);
	}
	
	private String addVitalParm(Element el, String elName, String p){
		String[] split = p.split("\\s*\\:\\s*");
		if (split.length > 1) {
			HashMap<String, String> vals = xc.getResult(split[0].trim(), mine.getDatum());
			if (vals != null) {
				String res = vals.get(split[1].trim());
				if (res != null) {
					Element en = new Element(elName);
					en.setText(res);
					el.addContent(en);
				}
			}
		}
		return null;
	}
	
	private String getOrFail(String prefs){
		String ret = CoreHub.globalCfg.get(prefs, null);
		if (ret == null) {
			SWTHelper.showError("ICPC/Fire",
				"Bitte konfigurieren Sie das Fire Plugin (Datei-Einstellungen)");
		}
		return ret;
	}
	
	private void addElement(Element parent, String title, String value){
		Element child = new Element(title);
		child.setText(value);
		parent.addContent(child);
	}
}
