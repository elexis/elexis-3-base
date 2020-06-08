package ch.elexis.ebanking.qr;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.builder.IContactBuilder.OrganizationBuilder;
import ch.elexis.core.model.builder.IContactBuilder.PersonBuilder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Country;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.Money;

public class QRBillImageTest {
	
	private IContact cdtr;
	private IContact dbtr;
	
	@Before
	public void before(){
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
	public void getImage() throws QRBillDataException{
		QRBillDataBuilder builder = new QRBillDataBuilder(cdtr, new Money(12.00), "CHF", dbtr);
		builder.reference("977598000000002414281387835");
		builder.unstructuredRemark("Ähnliche Rechnung #23 oder -23 über +23 mit <23");
		QRBillImage qrImage = new QRBillImage(builder.build());
		Optional<Image> image = qrImage.getImage();
		assertTrue(image.isPresent());
	}
	
	@Test
	public void getEncodedImage() throws QRBillDataException{
		QRBillDataBuilder builder = new QRBillDataBuilder(cdtr, new Money(12.00), "CHF", dbtr);
		builder.reference("977598000000002414281387835");
		builder.unstructuredRemark("Ähnliche Rechnung #23 oder -23 über +23 mit <23");
		QRBillImage qrImage = new QRBillImage(builder.build());
		Optional<String> encodedImage = qrImage.getEncodedImage();
		assertTrue(encodedImage.isPresent());
	}
}
