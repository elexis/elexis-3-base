/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui.startup;

import org.eclipse.ui.IStartup;

import ch.elexis.core.data.events.ElexisEventDispatcher;

public class VaccinationStartup implements IStartup {
	
	private final VaccinationPrescriptionEventListener vpel =
		new VaccinationPrescriptionEventListener();
	
	@Override
	public void earlyStartup(){
		ElexisEventDispatcher.getInstance().addListeners(vpel);
	}
	
}
