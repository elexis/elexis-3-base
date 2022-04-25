package at.medevit.elexis.tarmed.model.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import ch.fd.invoice400.response.ErrorBusinessType;
import ch.fd.invoice400.response.GeneratorType;
import ch.fd.invoice400.response.HeaderPartyType;
import ch.fd.invoice400.response.HeaderType;
import ch.fd.invoice400.response.InvoiceType;
import ch.fd.invoice400.response.InvoiceType.Biller;
import ch.fd.invoice400.response.InvoiceType.Insurance;
import ch.fd.invoice400.response.OnlineAddressType;
import ch.fd.invoice400.response.PostalAddressType;
import ch.fd.invoice400.response.PrologType;
import ch.fd.invoice400.response.RejectedErrorType;
import ch.fd.invoice400.response.RejectedType;
import ch.fd.invoice400.response.ReplyAddressType;
import ch.fd.invoice400.response.ReplyCompanyType;
import ch.fd.invoice400.response.ReplyContactType;
import ch.fd.invoice400.response.ResponseType;
import ch.fd.invoice400.response.SoftwareType;
import ch.fd.invoice400.response.StatusType;
import ch.fd.invoice400.response.TelecomAddressType;
import ch.fd.invoice400.response.ZipType;

public class InvoiceResponse400Tests {

	private static File writeResp400;
	private static File readResp400;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		writeResp400 = new File("rsc/writeResp400.xml");
		if (!writeResp400.exists()) {
			writeResp400.createNewFile();
		}

		readResp400 = new File("rsc/readResp400.xml");
	}

	@Test
	public void testMarshallInvoiceResponse400() throws FileNotFoundException, DatatypeConfigurationException {
		TarmedJaxbUtil.marshallInvoiceResponse(generateResponseSample(), new FileOutputStream(writeResp400));

		assertTrue(writeResp400.exists());
	}

	@Test
	public void testUnmarshalInvoiceResponse400() throws FileNotFoundException {
		ResponseType response = TarmedJaxbUtil.unmarshalInvoiceResponse400(new FileInputStream(readResp400));
		assertNotNull(response);
		assertEquals("test", response.getRole());

		HeaderType header = response.getHeader();
		assertNotNull(header);
		assertEquals("7601003002119", header.getSender().getEanParty());
		assertEquals("7601001304307", header.getIntermediate().getEanParty());
		assertEquals("7601000019202", header.getRecipient().getEanParty());

		PrologType prolog = response.getProlog();
		assertNotNull(prolog);
		assertEquals("Sumex II", prolog.getPackage().getValue());
		assertEquals("BackofficeInvoiceResponseBuilder", prolog.getGenerator().getSoftware().getValue());

		StatusType status = response.getStatus();
		assertNotNull(status);
		assertNotNull(status.getRejected());
		assertEquals("bereits bezahlt", status.getRejected().getExplanation());
		assertEquals(1, status.getRejected().getError().size());
		assertEquals(1016, status.getRejected().getError().get(0).getMajor());

		InvoiceType invoice = response.getInvoice();
		assertNotNull(invoice);
		assertEquals("7601000019202", invoice.getBiller().getEanParty());

		assertEquals("SWICA UVG Abteilung Leistungen", invoice.getReply().getCompany().getCompanyname());
		assertEquals("Pattavino", invoice.getReply().getContact().getFamilyname());
	}

	private ResponseType generateResponseSample() throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.set(2015, 01, 26, 10, 30);
		XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		ResponseType response = new ResponseType();
		response.setRole("UnitTest");

		// header
		HeaderType header = new HeaderType();
		// sender
		HeaderPartyType sender = new HeaderPartyType();
		sender.setEanParty("7601003002119");
		header.setSender(sender);
		// intermediate
		HeaderPartyType intermediate = new HeaderPartyType();
		intermediate.setEanParty("7601001304307");
		header.setIntermediate(intermediate);
		// recipient
		HeaderPartyType recipient = new HeaderPartyType();
		recipient.setEanParty("7601000019202");
		header.setRecipient(recipient);

		response.setHeader(header);

		// prolog
		PrologType prolog = new PrologType();
		SoftwareType pack = new SoftwareType();
		pack.setId(new BigInteger("0"));
		pack.setVersion(new BigInteger("202"));
		pack.setValue("Sumex II");
		prolog.setPackage(pack);

		GeneratorType generator = new GeneratorType();
		SoftwareType software = new SoftwareType();
		software.setId(new BigInteger("0"));
		software.setVersion(new BigInteger("202"));
		software.setValue("BackofficeInvoiceResponseBuilder");
		generator.setSoftware(software);
		prolog.setGenerator(generator);

		response.setProlog(prolog);

		// status
		StatusType status = new StatusType();
		RejectedType rejected = new RejectedType();
		rejected.setExplanation("already payed");
		// define error
		RejectedErrorType error = new RejectedErrorType();
		error.setError(new BigInteger("0"));
		error.setMajor(1016);
		error.setMinor(new BigInteger("0"));
		ErrorBusinessType errBusiness = new ErrorBusinessType();
		errBusiness.setValue("test");
		error.setErrorBusiness(errBusiness);
		rejected.getError().add(error);

		status.setRejected(rejected);
		response.setStatus(status);

		// invoice
		InvoiceType invoice = new InvoiceType();
		invoice.setCaseId("obe6c21c29a337c590657");
		invoice.setInvoiceDate(cal);
		invoice.setInvoiceId("015999002766");
		invoice.setInvoiceTimestamp(new BigInteger("1422456026"));
		invoice.setResponseId("99009960_12.03.15_1382");
		invoice.setResponseTimestamp(new BigInteger("1426122914"));

		// EANs
		Biller billerEAN = new Biller();
		billerEAN.setEanParty("7601000019202");
		invoice.setBiller(billerEAN);

		Insurance insuranceEAN = new Insurance();
		insuranceEAN.setEanParty("7601003002119");
		invoice.setInsurance(insuranceEAN);

		// reply
		ReplyAddressType reply = new ReplyAddressType();
		reply.setEanParty("7601003002119");
		ReplyCompanyType replyCompany = new ReplyCompanyType();
		replyCompany.setCompanyname("SWICA UVG Abteilung Leistungen");

		PostalAddressType rPostal = new PostalAddressType();
		rPostal.setStreet("Römerstrasse 37");
		rPostal.setCity("Winterthur");
		ZipType rZip = new ZipType();
		rZip.setCountrycode("CH");
		rZip.setValue("8401");
		rPostal.setZip(rZip);
		replyCompany.setPostal(rPostal);
		reply.setCompany(replyCompany);

		ReplyContactType replyContact = new ReplyContactType();
		replyContact.setFamilyname("Pattavino");
		replyContact.getGivenname().add("Lorena");
		TelecomAddressType rTel = new TelecomAddressType();
		rTel.getPhone().add("056 200 19 65");
		replyContact.setTelecom(rTel);
		OnlineAddressType rMail = new OnlineAddressType();
		rMail.getEmail().add("lorena.pattavino@swica.ch");
		replyContact.setOnline(rMail);
		reply.setContact(replyContact);

		invoice.setReply(reply);

		response.setInvoice(invoice);
		return response;
	}
}
