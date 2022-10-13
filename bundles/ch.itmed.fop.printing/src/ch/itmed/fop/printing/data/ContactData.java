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

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IContact;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class ContactData {
	private IContact kontakt;

	public void load() throws NullPointerException {
		kontakt = ContextServiceHolder.get().getTyped(IContact.class).orElse(null);
		if (kontakt == null) {
			throw new NullPointerException("No contact selected"); //$NON-NLS-1$
		}
	}

	public String getAddress() {
		return getAddress(false);
	}

	public String getAddress(boolean useLegalGuardian) {
		if (kontakt != null) {
			IContact legalGuardian = getLegalGuardian();
			if (legalGuardian != null) {
				return legalGuardian.getPostalAddress();
			}
			return kontakt.getPostalAddress();
		} else {
			return StringUtils.EMPTY;
		}

	}

	private IContact getLegalGuardian() {
		if (kontakt.isPerson()) {
			return kontakt.asIPerson().getLegalGuardian();
		}
		return null;
	}

	public String getSalutaton() {
		if (kontakt != null) {
			return Salutation.getSalutation(kontakt);
		} else {
			return StringUtils.EMPTY;
		}
	}

}
