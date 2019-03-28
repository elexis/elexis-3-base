/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.core.model.chmed16a;

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.ArrayList;
import java.util.List;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.data.Anwender;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

public class Medicament {
	public String Id;
	public int IdType;
	public List<Posology> Pos;
	public String Unit;
	public String TkgRsn;
	public String AppInstr;
	public int AutoMed;
	public String PrscBy;
	public String Roa;
	public int Rep;
	public int Subs;
	public float NbPack;
	public List<PrivateField> PFields;
	public transient ArtikelstammItem artikelstammItem;
	public transient String dosis;
	public transient String dateFrom;
	public transient String dateTo;
	public transient State state = State.NEW;
	public transient Prescription foundPrescription;
	public transient String stateInfo = "";
	public transient EntryType entryType;
	
	public static final String FREETEXT_PREFIX = "[Dosis: ";
	public static final String FREETEXT_POSTFIX = "]";
	
	public static List<Medicament> fromPrescriptions(List<Prescription> prescriptions){
		if (prescriptions != null && !prescriptions.isEmpty()) {
			List<Medicament> ret = new ArrayList<>();
			for (Prescription prescription : prescriptions) {
				Medicament medicament = new Medicament();
				medicament.Unit = "";
				medicament.AutoMed = 0;
				medicament.AppInstr = prescription.getBemerkung();
				medicament.TkgRsn = prescription.getDisposalComment();
				Artikel article = prescription.getArtikel();
				medicament.IdType = getIdType(article);
				medicament.Id = getId(article);
				medicament.Pos = Posology.fromPrescription(prescription);
				
				// check if it has freetext dosis
				if (medicament.Pos != null && !medicament.Pos.isEmpty()
					&& (medicament.Pos.get(0).D == null || medicament.Pos.get(0).D.isEmpty())) {
					String freeTextDosis = getDosageAsFreeText(prescription.getDosis());
					if (freeTextDosis != null) {
						medicament.AppInstr += (FREETEXT_PREFIX + freeTextDosis + FREETEXT_POSTFIX);
					}
				}
				
				String prescriptorId = prescription.get(Prescription.FLD_PRESCRIPTOR);
				medicament.PrscBy = getPrescriptorEAN(prescriptorId);
				ret.add(medicament);
			}
			return ret;
		}
		return null;
	}
	
	private static String getDosageAsFreeText(String dosis){
		if (dosis != null && !dosis.isEmpty()) {
			String[] signature = Prescription.getSignatureAsStringArray(dosis);
			boolean isFreetext = !signature[0].isEmpty() && signature[1].isEmpty()
				&& signature[2].isEmpty() && signature[3].isEmpty();
			if (isFreetext) {
				return signature[0];
			}
		}
		return null;
	}
	
	private static String getPrescriptorEAN(String prescriptorId){
		if (prescriptorId != null && !prescriptorId.isEmpty()) {
			Anwender prescriptor = Anwender.load(prescriptorId);
			if (prescriptor != null && prescriptor.exists()) {
				String ean = prescriptor.getXid(DOMAIN_EAN);
				if (ean != null && !ean.isEmpty()) {
					return ean;
				}
			}
		}
		return null;
	}
	
	private static int getIdType(Artikel article){
		String gtin = article.getEAN();
		if (gtin != null && !gtin.isEmpty()) {
			return 2;
		}
		String pharma = article.getPharmaCode();
		if (pharma == null || pharma.isEmpty()) {
			pharma = article.get(Artikel.FLD_SUB_ID);
		}
		if (pharma != null && !pharma.isEmpty()
			&& !pharma.startsWith(PersistentObject.MAPPING_ERROR_MARKER)) {
			return 3;
		}
		return 1;
	}
	
	private static String getId(Artikel article){
		String gtin = article.getEAN();
		if (gtin != null && !gtin.isEmpty()) {
			return gtin;
		}
		String pharma = article.getPharmaCode();
		if (pharma == null || pharma.isEmpty()) {
			pharma = article.get(Artikel.FLD_SUB_ID);
		}
		if (pharma != null && !pharma.isEmpty()
			&& !pharma.startsWith(PersistentObject.MAPPING_ERROR_MARKER)) {
			return pharma;
		}
		if (getIdType(article) == 1) {
			return article.getText();
		}
		throw new IllegalStateException(
			"No ID (GTIN, Pharmacode) for article [" + article.getLabel() + "]");
	}
	
	public enum State {
			// prioritized order dont change it
			NEW, ATC, ATC_SAME, ATC_SAME_DOSAGE, GTIN_SAME, GTIN_SAME_DOSAGE;
		
		public static boolean isHigherState(State current, State newState){
			return current.ordinal() < newState.ordinal();
		}
	}
	
	public boolean isMedicationExpired(){
		if (dateTo != null) {
			TimeTool now = new TimeTool();
			now.add(TimeTool.SECOND, 5);
			if (new TimeTool(dateTo).isBefore(now)) {
				return true;
			}
		}
		return false;
	}
}
