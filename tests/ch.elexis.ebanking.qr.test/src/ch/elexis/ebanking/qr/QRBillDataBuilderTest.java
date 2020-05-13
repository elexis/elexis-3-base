package ch.elexis.ebanking.qr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.builder.IContactBuilder.PersonBuilder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.ebanking.qr.model.QRBillData;

public class QRBillDataBuilderTest {
	
	private IContact cdtr;
	
	@Before
	public void before(){
		cdtr = new PersonBuilder(CoreModelServiceHolder.get(), "CdtrFirstname", "CdtrLastname",
			LocalDate.of(2000, 2, 2), Gender.FEMALE).mandator().build();
		cdtr.setExtInfo("IBAN", "CH4431999123000889012");
	}
	
	@Test
	public void buildSuccess(){
		QRBillDataBuilder builder = new QRBillDataBuilder().cdtrInf(cdtr);
		builder.cdtrInf(cdtr);
		QRBillData data = builder.build();
		assertNotNull(data);
		
		String qrData = data.toString();
		assertTrue(StringUtils.isNotEmpty(qrData));
		String[] parts = qrData.split("\r\n");
		assertEquals("CH4431999123000889012", parts[3]);
	}
}
