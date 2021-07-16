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
import ch.elexis.data.Mandant;

public final class MandatorData {
	private Mandant mandator;
	
	public boolean canLoad(){
		return ElexisEventDispatcher.getSelected(Mandant.class) != null;
	}
	
	public void load() throws NullPointerException {
		mandator = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
		if (mandator == null) {
			SWTHelper.showInfo("Kein Mandant ausgewählt", "Bitte wählen Sie vor dem Drucken einen Mandanten!");
			throw new NullPointerException("No mandator selected");
		}		
	}
	
	public String getEmail() {
		return mandator.getMailAddress();
	}
	
	public String getId() {
		return mandator.getId();
	}
	
	public String getFirstName() {
		return mandator.getVorname();
	}
	
	public String getLastName() {
		return mandator.getName();
	}
	
	public String getPhone() {
		return mandator.get("Telefon1");
	}
	
	public String getTitle() {
		return mandator.get("Titel");
	}
}
