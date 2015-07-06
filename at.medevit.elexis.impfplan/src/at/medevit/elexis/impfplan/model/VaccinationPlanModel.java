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
package at.medevit.elexis.impfplan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.medevit.elexis.impfplan.model.vaccplans.AbstractVaccinationPlan;
import at.medevit.elexis.impfplan.model.vaccplans.ImpfplanSchweiz2015;

public class VaccinationPlanModel {
	
	private static List<AbstractVaccinationPlan> vaccinationPlans = null;
	
	public static List<AbstractVaccinationPlan> getVaccinationPlans(){
		if (vaccinationPlans == null)
			init();
		return vaccinationPlans;
	}
	
	private static void init(){
		vaccinationPlans = new ArrayList<>();
		vaccinationPlans.add(new ImpfplanSchweiz2015());
		// add in the future
		vaccinationPlans = Collections.unmodifiableList(vaccinationPlans);
	}
	
}
