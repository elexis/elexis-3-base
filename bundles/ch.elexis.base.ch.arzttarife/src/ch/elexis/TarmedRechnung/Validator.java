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

package ch.elexis.TarmedRechnung;

import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;

public class Validator {
	
	public Result<Rechnung> checkBill(final XMLExporter xp, final Result<Rechnung> res){
		Rechnung rn = xp.rn;
		Kontakt m = rn.getMandant();
		if (rn.getStatus() > RnStatus.OFFEN) {
			return res; // Wenn sie eh schon gedruckt war machen wir kein Büro mehr auf
		}
		
		if ((m == null) || (!m.isValid())) {
			rn.reject(RnStatus.REJECTCODE.NO_MANDATOR, Messages.Validator_NoMandator);
			res.add(Result.SEVERITY.ERROR, 2, Messages.Validator_NoMandator, rn, true);
			
		}
		Fall fall = rn.getFall();
		
		if ((fall == null) || (!fall.isValid())) {
			rn.reject(RnStatus.REJECTCODE.NO_CASE, Messages.Validator_NoCase);
			res.add(Result.SEVERITY.ERROR, 4, Messages.Validator_NoCase, rn, true);
		}

		String ean = TarmedRequirements.getEAN(m);
		if (StringTool.isNothing(ean)) {
			rn.reject(RnStatus.REJECTCODE.NO_MANDATOR, Messages.Validator_NoEAN);
			res.add(Result.SEVERITY.ERROR, 3, Messages.Validator_NoEAN, rn, true);
		}
		if (xp.getDiagnoses().isEmpty()) {
			rn.reject(RnStatus.REJECTCODE.NO_DIAG, Messages.Validator_NoDiagnosis);
			res.add(Result.SEVERITY.ERROR, 8, Messages.Validator_NoDiagnosis, rn, true);
		}
		
		
		Kontakt kostentraeger =
			(fall != null) ? fall.getRequiredContact(TarmedRequirements.INSURANCE) : null;
		// kostentraeger is optional for tiers garant else check if valid
		if (kostentraeger == null && xp.tiers != null && xp.tiers.equals(XMLExporter.TIERS_GARANT)) {
			return res;
		} else {
			if (kostentraeger == null) {
				rn.reject(RnStatus.REJECTCODE.NO_GUARANTOR, Messages.Validator_NoName);
				res.add(Result.SEVERITY.ERROR, 7, Messages.Validator_NoName, rn, true);
				return res;
			}
			ean = TarmedRequirements.getEAN(kostentraeger);
			
			if (StringTool.isNothing(ean) || (!ean.matches(TarmedRequirements.EAN_PATTERN))) {
				rn.reject(RnStatus.REJECTCODE.NO_GUARANTOR, Messages.Validator_NoEAN2);
				res.add(Result.SEVERITY.ERROR, 6, Messages.Validator_NoEAN2, rn, true);
			}
			String bez = kostentraeger.get(Kontakt.FLD_NAME1);
			if (StringTool.isNothing(bez)) {
				rn.reject(RnStatus.REJECTCODE.NO_GUARANTOR, Messages.Validator_NoName);
				res.add(Result.SEVERITY.ERROR, 7, Messages.Validator_NoName, rn, true);
			}
		}
		return res;
	}
}
