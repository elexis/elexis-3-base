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

import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ContextServiceHolder;

public final class MandatorData {
	private IMandator mandator;

	public boolean canLoad() {
		return ContextServiceHolder.get().getActiveMandator().orElse(null) != null;
	}

	public void load() throws NullPointerException {
		mandator = ContextServiceHolder.get().getActiveMandator().orElse(null);
		if (mandator == null) {
			throw new NullPointerException("No mandator selected"); //$NON-NLS-1$
		}
	}

	public String getEmail() {
		return mandator.getEmail();
	}

	public String getId() {
		return mandator.getId();
	}

	public String getFirstName() {
		return mandator.getDescription2();
	}

	public String getLastName() {
		return mandator.getDescription1();
	}

	public String getPhone() {
		return mandator.getPhone1();
	}

	public String getTitle() {
		if (mandator.isPerson()) {
			return mandator.asIPerson().getTitel();
		}
		return "";
	}
}
