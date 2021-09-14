/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.data;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.services.holder.ContextServiceHolder;

public final class CaseData {

	public String getCoverageName(){
		Optional<ICoverage> iCoverage = ContextServiceHolder.get().getTyped(ICoverage.class);
		if (iCoverage.isPresent()) {
			return iCoverage.get().getDescription();
		}
		return null;
	}
	
	public String getCostBearer(){
		Optional<ICoverage> iCoverage = ContextServiceHolder.get().getTyped(ICoverage.class);
		if (iCoverage.isPresent()) {
			IContact costBearer = iCoverage.get().getCostBearer();
			if (costBearer != null) {
				return costBearer.getDescription1();
			}
		}
		return "";
	}
	
	public String getInsurancePolicyNumber(){
		Optional<ICoverage> iCoverage = ContextServiceHolder.get().getTyped(ICoverage.class);
		if (iCoverage.isPresent()) {
			if (iCoverage.get().getInsuranceNumber() != null) {
				return iCoverage.get().getInsuranceNumber();
			} else {
				if (StringUtils.isNotBlank((String) iCoverage.get().getExtInfo("Unfallnummer"))) {
					return (String) iCoverage.get().getExtInfo("Unfallnummer");
				}
				if (StringUtils.isNotBlank((String) iCoverage.get().getExtInfo("Fallnummer"))) {
					return (String) iCoverage.get().getExtInfo("Fallnummer");
				}
				if (StringUtils
					.isNotBlank((String) iCoverage.get().getExtInfo("Versicherungsnummer"))) {
					return (String) iCoverage.get().getExtInfo("Versicherungsnummer");
				}
			}
		}
		return "";
	}
}
