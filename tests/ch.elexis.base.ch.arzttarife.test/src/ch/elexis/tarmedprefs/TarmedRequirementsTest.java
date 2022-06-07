package ch.elexis.tarmedprefs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.base.ch.arzttarife.test.TestData.TestSzenario;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IXid;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class TarmedRequirementsTest {

	@Test
	public void getEAN() throws IOException {
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		assertNotNull(szenario);

		IContact testPatient = CoreModelServiceHolder.get().load(szenario.getPatienten().get(0).getId(), IContact.class)
				.get();
		String ean = TarmedRequirements.getEAN(testPatient);
		assertEquals(TarmedRequirements.EAN_PSEUDO, ean);

		IXid eanXid = testPatient.getXid(XidConstants.DOMAIN_EAN);
		assertNull(eanXid);
		testPatient.addXid(XidConstants.DOMAIN_EAN, "", false);
		eanXid = testPatient.getXid(XidConstants.DOMAIN_EAN);
		assertNotNull(eanXid);
		// empty Xid is removed and pseudo EAN is returned
		ean = TarmedRequirements.getEAN(testPatient);
		assertEquals(TarmedRequirements.EAN_PSEUDO, ean);
	}
}
