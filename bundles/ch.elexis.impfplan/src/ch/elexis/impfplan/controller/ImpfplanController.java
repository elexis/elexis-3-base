
/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    F. Schmid	 - migrated from scala to java
 *
 *
 *******************************************************************************/

package ch.elexis.impfplan.controller;

import java.util.List;


import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.impfplan.model.Vaccination;
import ch.elexis.impfplan.model.VaccinationType;

public final class ImpfplanController {

	public static List<VaccinationType> allVaccs() {
		Query<VaccinationType> q = new Query<VaccinationType>(VaccinationType.class);
		q.add(VaccinationType.FLD_ID.toUpperCase(), "!=", "VERSION"); //$NON-NLS-1$ //$NON-NLS-2$
		return q.execute();
	}

	public static List<Vaccination> getVaccinations(Patient pat) {
		return new Query<Vaccination>(Vaccination.class, Vaccination.PATIENT_ID, pat.getId()).execute();
	}

}
