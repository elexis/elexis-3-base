package at.medevit.elexis.tarmed.model.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.fd.invoice500.response.BillerGLNAddressType;
import ch.fd.invoice500.response.BillersAddressType;
import ch.fd.invoice500.response.BodyType;
import ch.fd.invoice500.response.CompanyType;
import ch.fd.invoice500.response.ContactAddressType;
import ch.fd.invoice500.response.EmployeeType;
import ch.fd.invoice500.response.ErrorType;
import ch.fd.invoice500.response.InsuranceAddressType;
import ch.fd.invoice500.response.InvoiceType;
import ch.fd.invoice500.response.OnlineAddressType;
import ch.fd.invoice500.response.PatientAddressType;
import ch.fd.invoice500.response.PayantType;
import ch.fd.invoice500.response.PayloadType;
import ch.fd.invoice500.response.PersonType;
import ch.fd.invoice500.response.PostalAddressType;
import ch.fd.invoice500.response.ProcessingType;
import ch.fd.invoice500.response.ProviderGLNAddressType;
import ch.fd.invoice500.response.ProvidersAddressType;
import ch.fd.invoice500.response.RejectedType;
import ch.fd.invoice500.response.ResponseType;
import ch.fd.invoice500.response.StreetType;
import ch.fd.invoice500.response.TelecomAddressType;
import ch.fd.invoice500.response.TransportType;
import ch.fd.invoice500.response.TransportType.Via;
import ch.fd.invoice500.response.ZipType;

public class InvoiceResponse500Tests {

	private static File writeResp500;
	private static File readResp500;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		writeResp500 = new File("rsc/writeResp500.xml");
		if (!writeResp500.exists()) {
			writeResp500.createNewFile();
		}
		readResp500 = new File("rsc/readResp500.xml");
	}

	@Test
	public void testMarshallInvoiceResponse500() throws FileNotFoundException, DatatypeConfigurationException {
		assertTrue(
				TarmedJaxbUtil.marshallInvoiceResponse(generateResponseSample(), new FileOutputStream(writeResp500)));
		assertTrue(writeResp500.exists());
	}

	@Test
	public void testUnmarshalInvoiceResponse500() throws FileNotFoundException {
		ResponseType response = TarmedJaxbUtil.unmarshalInvoiceResponse500(new FileInputStream(readResp500));

		assertNotNull(response);
		assertEquals("en", response.getLanguage());
		assertEquals("UnitTest", response.getModus());
		assertNotNull(response.getProcessing());
		assertNotNull(response.getPayload());
		BodyType body = response.getPayload().getBody();
		assertNotNull(body);
		assertNotNull(body.getTiersPayant());
		assertNotNull(body.getTiersPayant().getRejected());

		assertEquals("female", body.getPatient().getGender());
		assertNotNull(body.getPatient().getPerson().getPostal());
		assertNotNull(body.getPatient().getPerson().getTelecom());
		assertEquals("Edeltraud", body.getPatient().getPerson().getGivenname());
		assertEquals("Armeswesen", body.getPatient().getPerson().getFamilyname());

		assertEquals(2, response.getPayload().getBody().getTiersPayant().getRejected().getError().size());
		assertEquals("This is an invoice rejection", body.getTiersPayant().getRejected().getExplanation());
		assertNull(response.getPayload().getBody().getTiersPayant().getPending());
	}

	private ResponseType generateResponseSample() throws DatatypeConfigurationException {
		ResponseType response = new ResponseType();
		response.setLanguage("en");
		response.setModus("UnitTest");
		response.setPayload(getPayloadSample());
		response.setProcessing(getProcessingSample());

		return response;
	}

	private PayloadType getPayloadSample() throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.set(2015, 01, 21, 13, 30);
		XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		GregorianCalendar cBd = new GregorianCalendar(1980, 04, 01);
		XMLGregorianCalendar birthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cBd);

		PayloadType payload = new PayloadType();
		payload.setResponseTimestamp(1262874342);

		InvoiceType invoice = new InvoiceType();
		invoice.setRequestDate(cal);
		invoice.setRequestTimestamp(1255521474);
		invoice.setRequestId("001163000564");
		payload.setInvoice(invoice);

		BodyType body = new BodyType();
		BillersAddressType biller = new BillersAddressType();
		BillerGLNAddressType billerGln = new BillerGLNAddressType();
		billerGln.setGln("2011234567890");
		biller.setBillerGln(billerGln);
		ProvidersAddressType provider = new ProvidersAddressType();
		ProviderGLNAddressType providerGln = new ProviderGLNAddressType();
		providerGln.setGln("7634567890111");
		provider.setProviderGln(providerGln);
		InsuranceAddressType insurance = new InsuranceAddressType();
		insurance.setGln("2034567890222");
		body.setBillers(biller);
		body.setProviders(provider);
		body.setInsurance(insurance);

		// patient
		PatientAddressType patient = new PatientAddressType();
		patient.setGender("female");
		patient.setSsn("7561234567890");
		patient.setBirthdate(birthDate);
		PersonType pPatient = new PersonType();
		pPatient.setFamilyname("Armeswesen");
		pPatient.setGivenname("Edeltraud");
		pPatient.setTitle("Dr.");
		PostalAddressType pPostal = new PostalAddressType();
		StreetType street = new StreetType();
		street.setValue("Apfelgasse 2");
		pPostal.setStreet(street);
		pPostal.setCity("Lenzburg");
		pPatient.setPostal(pPostal);
		TelecomAddressType pTele = new TelecomAddressType();
		pTele.getPhone().add("555-7195217");
		pPatient.setTelecom(pTele);
		patient.setPerson(pPatient);
		body.setPatient(patient);

		// contact
		ContactAddressType contact = new ContactAddressType();
		contact.setGln("7600000000191");
		// company
		CompanyType company = new CompanyType();
		company.setCompanyname("Versicherung");
		company.setDepartment("Abteilung Basel");
		PostalAddressType cPostal = new PostalAddressType();
		street = new StreetType();
		street.setValue("St.-Jakobs-Strasse 24");
		cPostal.setStreet(street);
		ZipType cZip = new ZipType();
		cZip.setValue("4002");
		cPostal.setZip(cZip);
		cPostal.setCity("Basel");
		company.setPostal(cPostal);
		TelecomAddressType cTele = new TelecomAddressType();
		cTele.getPhone().add("031 136 82 00");
		company.setTelecom(cTele);
		contact.setCompany(company);
		// employee
		EmployeeType employee = new EmployeeType();
		employee.setSalutation("Herr");
		employee.setFamilyname("Sachbearbeiter");
		employee.setGivenname("Stefan");
		TelecomAddressType eTele = new TelecomAddressType();
		eTele.getPhone().add("031 136 82 39");
		OnlineAddressType eOnline = new OnlineAddressType();
		eOnline.getEmail().add("s.sachearbeiter@insurance.ch");
		contact.setEmployee(employee);

		body.setContact(contact);

		RejectedType rejected = new RejectedType();
		rejected.setStatusIn("unknown");
		rejected.setStatusOut("canceled");
		rejected.setExplanation("This is an invoice rejection");
		ErrorType error1 = new ErrorType();
		error1.setCode("31160");
		error1.setText("Not insured by us");
		ErrorType error2 = new ErrorType();
		error2.setCode("31148");
		error2.setText("Emergency charge not allowed on emergency flat rates");
		error2.setErrorValue("00.0020");
		error2.setRecordId(new BigInteger("99901"));
		error2.setValidValue("00.0021");
		rejected.getError().add(error1);
		rejected.getError().add(error2);

		PayantType payantType = new PayantType();
		payantType.setRejected(rejected);
		body.setTiersPayant(payantType);

		payload.setBody(body);
		return payload;
	}

	private ProcessingType getProcessingSample() {
		ProcessingType processing = new ProcessingType();
		// transport from - to
		TransportType transport = new TransportType();
		transport.setFrom("2099988872462");
		transport.setTo("7601001302181");
		// transport via
		Via via = new Via();
		via.setSequenceId(1);
		via.setVia("7601001304307");
		transport.getVia().add(via);

		// add transport to processing
		processing.setTransport(transport);
		return processing;
	}
}
