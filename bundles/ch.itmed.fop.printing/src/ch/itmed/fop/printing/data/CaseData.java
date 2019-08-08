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

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.itmed.fop.printing.resources.Messages;

public final class CaseData {
	private Fall fall;

	public void load() throws NullPointerException {
		fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
		if (fall == null) {
			SWTHelper.showInfo(Messages.Info_NoCase_Title, Messages.Info_NoCase_Message);
			throw new NullPointerException("No case selected");
		}
	}

	public String getCostBearer() {
		Kontakt kontakt = fall.getCostBearer();
		if (kontakt != null) {
			return kontakt.get("Bezeichnung1");
		}
		return ""; // null if not set or equal to patient
	}

	public String getInsurancePolicyNumber() {
		if ("UVG".equals(fall.getAbrechnungsSystem())) {
			return fall.getRequiredString("Unfallnummer");
		} else if ("KVG".equals(fall.getAbrechnungsSystem())) {
			return fall.getRequiredString("Versicherungsnummer");
		}

		return "";
	}
}
