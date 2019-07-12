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
import ch.elexis.data.Kontakt;
import ch.itmed.fop.printing.resources.Messages;

public class ContactData {
	private Kontakt kontakt;

	public void load() throws NullPointerException {
		kontakt = (Kontakt) ElexisEventDispatcher.getSelected(Kontakt.class);
		if (kontakt == null) {
			SWTHelper.showInfo(Messages.Info_NoContact_Title, Messages.Info_NoContact_Message);
			throw new NullPointerException("No contact selected");
		}
	}

	public String getAddress() {
		if (kontakt != null) {
			return kontakt.getPostAnschrift(true);
		} else {
			return "";
		}
	}

	public String getSalutaton() {
		if (kontakt != null) {
			return kontakt.getSalutation();
		} else {
			return "";
		}
	}

}
