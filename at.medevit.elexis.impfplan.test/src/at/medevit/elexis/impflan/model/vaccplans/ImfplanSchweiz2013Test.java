package at.medevit.elexis.impflan.model.vaccplans;

import junit.framework.Assert;

import org.junit.Test;

import at.medevit.elexis.impfplan.model.vaccplans.ImpfplanSchweiz2013;
import at.medevit.elexis.impfplan.model.vaccplans.AbstractVaccinationPlan.RequiredVaccination;

public class ImfplanSchweiz2013Test {

	@Test
	public void testImpfplanSchweiz2013(){
		ImpfplanSchweiz2013 ipfplch = new ImpfplanSchweiz2013();
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
