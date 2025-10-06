package ch.elexis.ebanking.qr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.builder.IContactBuilder.OrganizationBuilder;
import ch.elexis.core.model.builder.IContactBuilder.PersonBuilder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Country;
import ch.elexis.core.types.Gender;
import ch.elexis.ebanking.qr.model.QRBillData;
import ch.rgw.tools.Money;

public class QRBillDataBuilderTest {

	private IContact cdtr;
	private IContact dbtr;

	@Before
	public void before() {
		cdtr = new PersonBuilder(CoreModelServiceHolder.get(), "CdtrFirstname", "CdtrLastname",
				LocalDate.of(2000, 2, 2), Gender.FEMALE).mandator().build();
		cdtr.setExtInfo("IBAN", "CH4431999123000889012");
		cdtr.setStreet("Grosse Marktgasse 28");
		cdtr.setZip("9400");
		cdtr.setCity("Rorschach");
		cdtr.setCountry(Country.CH);

		dbtr = new OrganizationBuilder(CoreModelServiceHolder.get(), "DbtrOrg").build();
		dbtr.setStreet("Rue du Lac 1268");
		dbtr.setZip("2501");
		dbtr.setCity("Biel");
		dbtr.setCountry(Country.CH);
	}

	@Test
	public void buildSuccess() throws QRBillDataException {
		QRBillDataBuilder builder = new QRBillDataBuilder(cdtr, new Money(12.00), "CHF", dbtr);
		builder.reference("977598000000002414281387835");
		builder.unstructuredRemark("Ähnliche Rechnung #23 oder -23 über +23 mit <23");
		QRBillData data = builder.build();
		assertNotNull(data);

		String qrData = data.toString();
		assertTrue(StringUtils.isNotEmpty(qrData));
		String[] parts = qrData.split("\r\n", -1);
		assertEquals(32, parts.length);

		assertEquals("CH4431999123000889012", parts[3]);
		assertEquals("Grosse Marktgasse", parts[6]);
		assertEquals("28", parts[7]);
		assertEquals("9400", parts[8]);
		assertEquals("Rorschach", parts[9]);
		assertEquals("CH", parts[10]);

		assertEquals("12.00", parts[18]);
		assertEquals("CHF", parts[19]);

		assertEquals("DbtrOrg", parts[21]);
		assertEquals("Rue du Lac", parts[22]);
		assertEquals("1268", parts[23]);
		assertEquals("CH", parts[26]);

		assertEquals("QRR", parts[27]);
		assertEquals("977598000000002414281387835", parts[28]);

		assertEquals("Ähnliche Rechnung #23 oder -23 über +23 mit <23", parts[29]);
	}
}
