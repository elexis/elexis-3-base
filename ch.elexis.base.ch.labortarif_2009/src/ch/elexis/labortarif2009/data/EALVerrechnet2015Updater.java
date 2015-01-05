/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package ch.elexis.labortarif2009.data;

import java.util.List;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;
import ch.elexis.labortarif2009.data.EALBlocksCodeUpdater.AnalysenOnlyTarifResolver;
import ch.rgw.tools.TimeTool;

public class EALVerrechnet2015Updater {
	
	private TimeTool startDate = new TimeTool("1.1.2015");
	private static final String analysenChapter = "5.1.2.2.1";

	public void fix2015Chapters(){
		Labor2009Tarif tarif = Labor2009Tarif.getFromCode("1260.01", startDate);
		if(!isSchnellAnalyse(tarif)) {
			tarif.set(Labor2009Tarif.FLD_CHAPTER, concatChapter(tarif, analysenChapter));
		}
		tarif = Labor2009Tarif.getFromCode("1356.01", startDate);
		if (!isSchnellAnalyse(tarif)) {
			tarif.set(Labor2009Tarif.FLD_CHAPTER, concatChapter(tarif, analysenChapter));
		}
		tarif = Labor2009Tarif.getFromCode("1700.01", startDate);
		if (!isSchnellAnalyse(tarif)) {
			tarif.set(Labor2009Tarif.FLD_CHAPTER, concatChapter(tarif, analysenChapter));
		}
		tarif = Labor2009Tarif.getFromCode("1740.01", startDate);
		if (!isSchnellAnalyse(tarif)) {
			tarif.set(Labor2009Tarif.FLD_CHAPTER, concatChapter(tarif, analysenChapter));
		}
		tarif = Labor2009Tarif.getFromCode("3469.01", startDate);
		if (!isSchnellAnalyse(tarif)) {
			tarif.set(Labor2009Tarif.FLD_CHAPTER, concatChapter(tarif, analysenChapter));
		}
	}

	private String concatChapter(Labor2009Tarif existing, String chapter){
		String existingChapter = existing.get(Labor2009Tarif.FLD_CHAPTER);
		if (existingChapter != null && !existingChapter.isEmpty()) {
			return existingChapter + ", " + chapter;
		} else {
			return chapter;
		}
	}
	
	private boolean isSchnellAnalyse(Labor2009Tarif tarif){
		String chapter = tarif.get(Labor2009Tarif.FLD_CHAPTER).trim();
		if (chapter != null && !chapter.isEmpty()) {
			String[] chapters = chapter.split(",");
			for (String string : chapters) {
				if (string.trim().equals(analysenChapter)) {
					return true;
				}
			}
		}
		return false;
	}

	public String update2015Verrechnet(){
		int absoluteCnt = 0;

		Query<Konsultation> qk = new Query<Konsultation>(Konsultation.class);
		qk.add(Konsultation.FLD_DATE, Query.GREATER_OR_EQUAL,
			startDate.toString(TimeTool.DATE_COMPACT));
		
		AnalysenOnlyTarifResolver resolver = new AnalysenOnlyTarifResolver();

		List<Konsultation> konsultationen = qk.execute();
		for (Konsultation konsultation : konsultationen) {
			List<Verrechnet> leistungen = konsultation.getLeistungen();
			if (leistungen != null && !leistungen.isEmpty()) {
				for (Verrechnet verrechnet : leistungen) {
					IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
					if (verrechenbar instanceof Labor2009Tarif) {
						String code = verrechenbar.getCode();
						Labor2009Tarif analyseTarif =
							(Labor2009Tarif) resolver.getTarif(verrechenbar.getCode(), startDate);
						if (analyseTarif != null) {
							konsultation.removeLeistung(verrechnet);
							konsultation.addLeistung(analyseTarif);
							absoluteCnt++;
						}
						// try removing wrong zuschlag -> reason is error before fix2015Chapters 
						if (code.endsWith(".01")) {
							removeZuschlag(konsultation);
						}
					}
				}
			}
		}
		return absoluteCnt + " EAL codes in Konsultationen angepasst.\n";
	}
	
	private void removeZuschlag(Konsultation konsultation){
		List<Verrechnet> leistungen = konsultation.getLeistungen();
		if (leistungen != null && !leistungen.isEmpty()) {
			for (Verrechnet verrechnet : leistungen) {
				IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
				if (verrechenbar instanceof Labor2009Tarif) {
					Labor2009Tarif tarif = (Labor2009Tarif) verrechenbar;
					String code = tarif.getCode();
					if (code.equals("4707.10") || code.equals("4707.20")) {
						konsultation.removeLeistung(verrechnet);
					}
				}
			}
		}
	}
}
