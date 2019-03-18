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

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.MedicationServiceHolder;
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
	public transient IArtikelstammItem artikelstammItem;
	public transient String dosis;
	public transient String dateFrom;
	public transient String dateTo;
	public transient State state = State.NEW;
	public transient Prescription foundPrescription;
	public transient String stateInfo = "";
	public transient EntryType entryType;
	
	public static final String FREETEXT_PREFIX = "[Dosis: ";
	public static final String FREETEXT_POSTFIX = "]";
	
	public static List<Medicament> fromPrescriptions(List<IPrescription> prescriptions){
		if (prescriptions != null && !prescriptions.isEmpty()) {
			List<Medicament> ret = new ArrayList<>();
			for (IPrescription prescription : prescriptions) {
				Medicament medicament = new Medicament();
				medicament.Unit = "";
				medicament.AutoMed = 0;
				medicament.AppInstr = prescription.getRemark();
				medicament.TkgRsn = prescription.getDisposalComment();
				IArticle article = prescription.getArticle();
				medicament.IdType = getIdType(article);
				medicament.Id = getId(article);
				medicament.Pos = Posology.fromPrescription(prescription);
				
				// check if it has freetext dosis
				if (medicament.Pos != null && !medicament.Pos.isEmpty()
					&& (medicament.Pos.get(0).D == null || medicament.Pos.get(0).D.isEmpty())) {
					String freeTextDosis = getDosageAsFreeText(prescription.getDosageInstruction());
					if (freeTextDosis != null) {
						medicament.AppInstr += (FREETEXT_PREFIX + freeTextDosis + FREETEXT_POSTFIX);
					}
				}
				
				IContact prescriptor = prescription.getPrescriptor();
				medicament.PrscBy = getPrescriptorEAN(prescriptor);
				ret.add(medicament);
			}
			return ret;
		}
		return null;
	}
	
	private static String getDosageAsFreeText(String dosis){
		if (dosis != null && !dosis.isEmpty()) {
			String[] signature = MedicationServiceHolder.get().getSignatureAsStringArray(dosis);
			boolean isFreetext = !signature[0].isEmpty() && signature[1].isEmpty()
				&& signature[2].isEmpty() && signature[3].isEmpty();
			if (isFreetext) {
				return signature[0];
			}
		}
		return null;
	}
	
	private static String getPrescriptorEAN(IContact prescriptor){
		if (prescriptor != null) {
			IXid ean = prescriptor.getXid(DOMAIN_EAN);
			if (ean != null && ean.getDomainId() != null && !ean.getDomainId().isEmpty()) {
				return ean.getDomainId();
			}
		}
		return null;
	}
	
	private static int getIdType(IArticle article){
		String gtin = article.getGtin();
		if (gtin != null && !gtin.isEmpty()) {
			return 2;
		}
		String code = article.getCode();
		if (code != null && !code.isEmpty()) {
			return 3;
		}
		return 1;
	}
	
	private static String getId(IArticle article){
		String gtin = article.getGtin();
		if (gtin != null && !gtin.isEmpty()) {
			return gtin;
		}
		String code = article.getCode();
		if (code != null && !code.isEmpty()) {
			return code;
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
