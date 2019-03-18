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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.rgw.tools.TimeTool;

public class Posology {
	public String DtFrom;
	public String DtTo;
	public int CyDu;
	public int InRes;
	public List<Float> D;
	public List<TakingTime> TT;
	
	public static List<Posology> fromPrescription(IPrescription prescription){
		List<Posology> ret = new ArrayList<>();
		Posology posology = new Posology();
		LocalDateTime beginDate = prescription.getDateFrom();
		if (beginDate != null) {
			posology.DtFrom = new TimeTool(beginDate).toString(TimeTool.DATE_ISO);
		}
		LocalDateTime endDate = prescription.getDateTo();
		if (endDate != null) {
			posology.DtTo = new TimeTool(endDate).toString(TimeTool.DATE_ISO);
		}
		List<Float> floats = MedicationServiceHolder.get().getDosageAsFloats(prescription);
		if (floats != null && !floats.isEmpty()) {
			posology.TT = TakingTime.fromFloats(floats, prescription.getEntryType() == EntryType.RESERVE_MEDICATION);
			posology.D = floats;
		}
		ret.add(posology);
		return ret;
	}
}
