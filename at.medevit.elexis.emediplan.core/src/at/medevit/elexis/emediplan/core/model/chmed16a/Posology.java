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

import java.util.ArrayList;
import java.util.List;

import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

public class Posology {
	public String DtFrom;
	public String DtTo;
	public int CyDu;
	public int InRes;
	public List<Float> D;
	public List<TakingTime> TT;
	
	public static List<Posology> fromPrescription(Prescription prescription){
		List<Posology> ret = new ArrayList<>();
		Posology posology = new Posology();
		String beginDate = prescription.getBeginDate();
		if (beginDate != null && !beginDate.isEmpty()) {
			posology.DtFrom = new TimeTool(beginDate).toString(TimeTool.DATE_ISO);
		}
		String endDate = prescription.getEndDate();
		if (endDate != null && !endDate.isEmpty()) {
			posology.DtTo = new TimeTool(endDate).toString(TimeTool.DATE_ISO);
		}
		ArrayList<Float> floats = Prescription.getDoseAsFloats(prescription.getDosis());
		if (floats != null && !floats.isEmpty()) {
			posology.TT = TakingTime.fromFloats(floats, prescription.isReserveMedication());
			posology.D = floats;
		}
		ret.add(posology);
		return ret;
	}
}
