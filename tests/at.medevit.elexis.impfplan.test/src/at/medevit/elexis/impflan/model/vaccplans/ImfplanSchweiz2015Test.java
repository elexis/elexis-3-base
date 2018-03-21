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
package at.medevit.elexis.impflan.model.vaccplans;

import junit.framework.Assert;

import org.junit.Test;

import at.medevit.elexis.impfplan.model.vaccplans.ImpfplanSchweiz2015;
import at.medevit.elexis.impfplan.model.vaccplans.AbstractVaccinationPlan.RequiredVaccination;

public class ImfplanSchweiz2015Test {

	@Test
	public void testImpfplanSchweiz2015(){
		ImpfplanSchweiz2015 ipfplch = new ImpfplanSchweiz2015();
		for (RequiredVaccination rv : ipfplch.baseVaccinations) {
			System.out.println("B "+rv);
		}
		Assert.assertTrue(ipfplch.baseVaccinations.size()>10);
		for (RequiredVaccination rv : ipfplch.extendedVaccinations) {
			System.out.println("E "+rv);
		}
		Assert.assertTrue(ipfplch.extendedVaccinations.size()>3);
	}

	
}
