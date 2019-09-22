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

import org.junit.Test;

import at.medevit.elexis.impfplan.model.vaccplans.AbstractVaccinationPlan.RequiredVaccination;
import at.medevit.elexis.impfplan.model.vaccplans.ImpfplanSchweiz2019;
import junit.framework.Assert;

public class ImfplanSchweiz2019Test {

	@Test
	public void testImpfplanSchweiz2019(){
		ImpfplanSchweiz2019 ipfplch = new ImpfplanSchweiz2019();
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
