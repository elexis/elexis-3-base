package at.medevit.elexis.tarmed.model.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.tarmed.model.Constants;
import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.fd.invoice400.request.BalanceType;
import ch.fd.invoice400.request.BankAddressType;
import ch.fd.invoice400.request.BankCompanyType;
import ch.fd.invoice400.request.BillerAddressType;
import ch.fd.invoice400.request.BillerPersonType;
import ch.fd.invoice400.request.CompanyType;
import ch.fd.invoice400.request.DataValidatorType;
import ch.fd.invoice400.request.DetailType;
import ch.fd.invoice400.request.DiagnosisType;
import ch.fd.invoice400.request.Esr9Type;
import ch.fd.invoice400.request.GeneratorType;
import ch.fd.invoice400.request.GuarantorAddressType;
import ch.fd.invoice400.request.GuarantorPersonType;
import ch.fd.invoice400.request.HeaderPartyType;
import ch.fd.invoice400.request.HeaderType;
import ch.fd.invoice400.request.InsuranceAddressType;
import ch.fd.invoice400.request.InvoiceType;
import ch.fd.invoice400.request.PatientAddressType;
import ch.fd.invoice400.request.PatientPersonType;
import ch.fd.invoice400.request.PayantType;
import ch.fd.invoice400.request.PostalAddressType;
import ch.fd.invoice400.request.PrologType;
import ch.fd.invoice400.request.ProviderAddressType;
import ch.fd.invoice400.request.ProviderPersonType;
import ch.fd.invoice400.request.RecordDrugType;
import ch.fd.invoice400.request.RecordTarmedType;
import ch.fd.invoice400.request.RequestType;
import ch.fd.invoice400.request.ServicesType;
import ch.fd.invoice400.request.SoftwareType;
import ch.fd.invoice400.request.TelecomAddressType;
import ch.fd.invoice400.request.UvgLawType;
import ch.fd.invoice400.request.VatRateType;
import ch.fd.invoice400.request.VatType;
import ch.fd.invoice400.request.XtendHeaderPartyType;
import ch.fd.invoice400.request.ZipType;

public class InvoiceRequest400Tests {

	private static File writeReq400;
	private static File readReq400;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		writeReq400 = new File("rsc/writeReq400.xml");
		if (!writeReq400.exists()) {
			writeReq400.createNewFile();
		}
		
		readReq400 = new File("rsc/readReq400.xml");
	}
	
	@Test
	public void testMarshallInvoiceRequest400() throws DatatypeConfigurationException, IOException{
		try (FileOutputStream fileOutputStream = new FileOutputStream(writeReq400)) {
			TarmedJaxbUtil.marshallInvoiceRequest(generateRequestSample(), fileOutputStream);
		}
		
		assertTrue(writeReq400.exists());
		
		try (FileInputStream fileInputStream = new FileInputStream(writeReq400);) {
			String string = IOUtils.toString(fileInputStream, "UTF-8");
			assertTrue(string.startsWith(Constants.DEFAULT_HEADER));
		}
	}
	
	@Test
	public void testUnmarshalInvoiceRequest400() throws FileNotFoundException{
		RequestType request =
				TarmedJaxbUtil.unmarshalInvoiceRequest400(new FileInputStream(readReq400));
		
		assertNotNull(request);
		assertEquals("production", request.getRole());
		assertNotNull(request.getHeader());
		assertEquals("2099988872462", request.getHeader().getSender().getEanParty());
		assertEquals("2000000000000", request.getHeader().getRecipient().getEanParty());
		assertNotNull(request.getProlog());
		assertEquals("Elexis", request.getProlog().getPackage().getValue());
		assertEquals("JDOM", request.getProlog().getGenerator().getSoftware().getValue());
		assertNotNull(request.getInvoice());
		
		BalanceType balance = request.getInvoice().getBalance();
		assertEquals("110.19", Double.toString(balance.getAmount()));
		assertEquals("CHF", balance.getCurrency());
		assertEquals("3.11", Double.toString(balance.getVat().getVat()));
		
		Esr9Type esr9 = request.getInvoice().getEsr9();
		assertEquals("01-200020-9", esr9.getParticipantNumber());
		assertNotNull(esr9.getBank());
		
		PayantType payant = request.getInvoice().getTiersPayant();
		assertEquals("Schwarz", payant.getBiller().getPerson().getFamilyname());
		assertEquals("Adlerauge", payant.getPatient().getPerson().getFamilyname());
		assertEquals("male", payant.getPatient().getGender());
		assertNotNull(payant.getGuarantor());
		assertNotNull(payant.getInsurance());
		
		List<DiagnosisType> diagnosis = request.getInvoice().getDetail().getDiagnosis();
		assertEquals("V17", diagnosis.get(0).getCode());
		assertEquals("ICD10", diagnosis.get(0).getType());
		
		UvgLawType uvg = request.getInvoice().getDetail().getUvg();
		assertEquals("accident", uvg.getReason());
		assertEquals("97651", uvg.getCaseId());
		
		List<Object> records =
			request.getInvoice().getDetail().getServices()
				.getRecordTarmedOrRecordCantonalOrRecordUnclassified();
		assertEquals(6, records.size());
	}
	
	private RequestType generateRequestSample() throws DatatypeConfigurationException{
		RequestType request = new RequestType();
		request.setRole("UnitTest");
		
		// header
		HeaderType header = new HeaderType();
		HeaderPartyType sender = new HeaderPartyType();
		sender.setEanParty("2099988872462");
		HeaderPartyType intermidate = new HeaderPartyType();
		intermidate.setEanParty("2000000000000");
		XtendHeaderPartyType recipient = new XtendHeaderPartyType();
		recipient.setEanParty("2000000000000");
		header.setSender(sender);
		header.setIntermediate(intermidate);
		header.setRecipient(recipient);
		
		request.setHeader(header);
		
		// prolog
		PrologType prolog = new PrologType();
		SoftwareType pack = new SoftwareType();
		pack.setVersion(new BigInteger("300"));
		pack.setId(new BigInteger("1"));
		pack.setValue("Elexis");
		prolog.setPackage(pack);
		
		GeneratorType generator = new GeneratorType();
		SoftwareType software = new SoftwareType();
		software.setVersion(new BigInteger("100"));
		software.setId(new BigInteger("1"));
		software.setValue("JDOM");
		generator.setSoftware(software);
		prolog.setGenerator(generator);
		
		DataValidatorType validator = new DataValidatorType();
		validator.setFocus("tarmed");
		validator.setVersionSoftware(new BigInteger("300"));
		validator.setVersionDb(new BigInteger("401"));
		validator.setId(new BigInteger("1"));
		validator.setValue("Elexis TarmedVerifier");
		prolog.getValidator().add(validator);
		
		request.setProlog(prolog);
		
		// invoice
		request.setInvoice(generateInvoiceSample());
		
		return request;
	}
	
	private InvoiceType generateInvoiceSample() throws DatatypeConfigurationException{
		GregorianCalendar c = new GregorianCalendar();
		c.set(2015, 03, 19, 10, 30);
		XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		c = new GregorianCalendar();
		c.set(1966, 07, 20, 10, 30);
		XMLGregorianCalendar birthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		InvoiceType invoice = new InvoiceType();
		invoice.setInvoiceTimestamp(new BigInteger("1426759765"));
		invoice.setInvoiceId("001026000562");
		invoice.setInvoiceDate(cal);
		invoice.setCaseId("ka933bacc6535fecf017");
		invoice.setResend(false);
		
		// invoice balance
		BalanceType balance = new BalanceType();
		balance.setCurrency("CHF");
		balance.setAmount(110.19);
		balance.setAmountPrepaid(0.00);
		balance.setAmountDue(110.20);
		balance.setAmountTarmed(54.59);
		balance.setAmountTarmedMt(30.61);
		balance.setAmountTarmedTt(28.16);
		balance.setUnitTarmedTt(28.73);
		balance.setUnitTarmedMt(26.43);
		balance.setAmountCantonal(0.00);
		balance.setAmountUnclassified(0.00);
		balance.setAmountLab(0.00);
		balance.setAmountPhysio(0.00);
		balance.setAmountDrug(55.60);
		balance.setAmountMigel(0.00);
		balance.setAmountObligations(110.19);
		//set vat
		VatType vat = new VatType();
		vat.setVat(3.11);
		
		VatRateType vr1 = new VatRateType();
		vr1.setVatRate(0.00);
		vr1.setAmount(54.59);
		vr1.setVat(0.00);
		vat.getVatRate().add(vr1);
		
		VatRateType vr2 = new VatRateType();
		vr2.setVatRate(2.50);
		vr2.setAmount(24.25);
		vr2.setVat(0.61);
		vat.getVatRate().add(vr2);
		
		VatRateType vr3 = new VatRateType();
		vr3.setVatRate(8.00);
		vr3.setAmount(31.35);
		vr3.setVat(2.51);
		vat.getVatRate().add(vr3);
		
		balance.setVat(vat);
		invoice.setBalance(balance);
		
		//esr9
		Esr9Type esr9 = new Esr9Type();
		esr9.setParticipantNumber("01-200020-9");
		esr9.setType("16or27");
		esr9.setReferenceNumber("81 17000 00000 00001 02600 05629");
		esr9.setCodingLine("0100000110207&gt;811700000000000010260005629+ 012000209&gt;");
		//Bank
		BankAddressType bank = new BankAddressType();
		BankCompanyType bCompany = new BankCompanyType();
		bCompany.setCompanyname("Hartmann");
		PostalAddressType bPostal = new PostalAddressType();
		bPostal.setCity("Zürich");
		bCompany.setPostal(bPostal);
		bank.setCompany(bCompany);
		esr9.setBank(bank);
		
		invoice.setEsr9(esr9);
		
		//tiers payant
		PayantType payant = new PayantType();
		payant.setInvoiceModification(false);
		payant.setPurpose("invoice");
		
		//biller
		BillerAddressType biller = new BillerAddressType();
		biller.setEanParty("2099988872462");
		biller.setZsr("C196719");
		biller.setSpecialty("General Medicine");
		//biller person
		BillerPersonType billPerson = new BillerPersonType();
		billPerson.setSalutation("Ms.");
		billPerson.setFamilyname("Schwarz");
		billPerson.getGivenname().add("Lena");
		PostalAddressType billPostal = new PostalAddressType();
		billPostal.setStreet("Uferweg 3");
		ZipType billZip = new ZipType();
		billZip.setCountrycode("CH");
		billZip.setValue("5000");
		billPostal.setZip(billZip);
		billPostal.setCity("Aarau");
		billPerson.setPostal(billPostal);
		TelecomAddressType billTele = new TelecomAddressType();
		billTele.getPhone().add("555-0944235");
		billPerson.setTelecom(billTele);
		biller.setPerson(billPerson);
		
		payant.setBiller(biller);
		
		// provider
		ProviderAddressType provider = new ProviderAddressType();
		provider.setEanParty("2099988872462");
		provider.setZsr("C196719");
		provider.setSpecialty("General Medicine");
		//Provider Person
		ProviderPersonType pPerson = new ProviderPersonType();
		pPerson.setSalutation("Ms.");
		pPerson.setFamilyname("Schwarz");
		pPerson.getGivenname().add("Lena");
		pPerson.setPostal(billPostal);
		pPerson.setTelecom(billTele);
		provider.setPerson(pPerson);
		
		payant.setProvider(provider);
		
		//insurance
		InsuranceAddressType insurance = new InsuranceAddressType();
		insurance.setEanParty("2000000000000");
		CompanyType iCompany = new CompanyType();
		iCompany.setCompanyname("Adlerauge");
		PostalAddressType iPostal = new PostalAddressType();
		iPostal.setStreet("Bahnhofstrasse 3");
		iPostal.setCity("Aarau");
		ZipType iZip = new ZipType();
		iZip.setCountrycode("CH");
		iZip.setValue("5000");
		iPostal.setZip(iZip);
		iCompany.setPostal(iPostal);
		insurance.setCompany(iCompany);
		
		payant.setInsurance(insurance);
		
		//patient
		PatientAddressType patient = new PatientAddressType();
		patient.setGender("male");
		patient.setBirthdate(birthDate);
		PatientPersonType patPerson = new PatientPersonType();
		patPerson.setFamilyname("Adlerauge");
		patPerson.getGivenname().add("Albertino");
		patPerson.setPostal(iPostal);
		patient.setPerson(patPerson);
		
		payant.setPatient(patient);
		
		//guarantor
		GuarantorAddressType guarantor = new GuarantorAddressType();
		GuarantorPersonType gPerson = new GuarantorPersonType();
		gPerson.setFamilyname("Adlerauge");
		gPerson.getGivenname().add("Albertino");
		gPerson.setPostal(iPostal);
		guarantor.setPerson(gPerson);
		payant.setGuarantor(guarantor);
		
		invoice.setTiersPayant(payant);
		
		// detail
		DetailType detail = new DetailType();
		detail.setDateBegin(cal);
		detail.setDateEnd(cal);
		detail.setCanton("AG");
		detail.setServiceLocalityAttribute("practice");
		
		//diagnosis
		DiagnosisType diagnosis = new DiagnosisType();
		diagnosis.setType("ICD10");
		diagnosis.setCode("V17");
		detail.getDiagnosis().add(diagnosis);
		diagnosis = new DiagnosisType();
		diagnosis.setType("by_contract");
		diagnosis.setCode("R4");
		detail.getDiagnosis().add(diagnosis);
		diagnosis = new DiagnosisType();
		diagnosis.setType("by_contract");
		diagnosis.setCode("R5");
		detail.getDiagnosis().add(diagnosis);
		
		UvgLawType uvg = new UvgLawType();
		uvg.setReason("accident");
		uvg.setCaseId("97651");
		uvg.setCaseDate(cal);
		detail.setUvg(uvg);
		
		//services
		ServicesType services = new ServicesType();
		// rec 1
		RecordDrugType recDrug = new RecordDrugType();
		recDrug.setUnit(3.90);
		recDrug.setUnitFactor(1.00);
		recDrug.setTariffType("400");
		recDrug.setCode("2321636");
		recDrug.setAmount(3.90);
		recDrug.setVatRate(8.0);
		recDrug.setObligation(false);
		recDrug.setValidate(true);
		recDrug.setRecordId(new BigInteger("1"));
		recDrug.setNumber(new BigInteger("1"));
		recDrug.setQuantity(1.0);
		recDrug.setDateBegin(cal);
		recDrug.setValue("HANSAPLAST Wundreinigungstücher 8 Stk ()");
		services.getRecordTarmedOrRecordCantonalOrRecordUnclassified().add(recDrug);
		//rec 2
		recDrug = new RecordDrugType();
		recDrug.setUnit(25.45);
		recDrug.setUnitFactor(1.00);
		recDrug.setTariffType("400");
		recDrug.setCode("2180791");
		recDrug.setAmount(24.45);
		recDrug.setVatRate(8.0);
		recDrug.setObligation(false);
		recDrug.setValidate(true);
		recDrug.setRecordId(new BigInteger("2"));
		recDrug.setNumber(new BigInteger("1"));
		recDrug.setQuantity(1.0);
		recDrug.setDateBegin(cal);
		recDrug.setValue("HANSAPLAST Knie Bandage (1 Stk)");
		services.getRecordTarmedOrRecordCantonalOrRecordUnclassified().add(recDrug);
		//rec 3
		recDrug = new RecordDrugType();
		recDrug.setUnit(3.00);
		recDrug.setUnitFactor(1.00);
		recDrug.setTariffType("400");
		recDrug.setCode("2321599");
		recDrug.setAmount(3.00);
		recDrug.setVatRate(8.0);
		recDrug.setObligation(false);
		recDrug.setValidate(true);
		recDrug.setRecordId(new BigInteger("3"));
		recDrug.setNumber(new BigInteger("1"));
		recDrug.setQuantity(1.0);
		recDrug.setDateBegin(cal);
		recDrug.setValue("HANSAPLAST UNIVERSAL Schnellverb Strips ass 20 Stk ()");
		services.getRecordTarmedOrRecordCantonalOrRecordUnclassified().add(recDrug);
		//rec 4
		recDrug = new RecordDrugType();
		recDrug.setUnit(24.25);
		recDrug.setUnitFactor(1.00);
		recDrug.setTariffType("400");
		recDrug.setCode("2648343");
		recDrug.setAmount(24.45);
		recDrug.setVatRate(24.45);
		recDrug.setObligation(false);
		recDrug.setValidate(true);
		recDrug.setRecordId(new BigInteger("4"));
		recDrug.setNumber(new BigInteger("1"));
		recDrug.setQuantity(1.0);
		recDrug.setDateBegin(cal);
		recDrug.setValue("VOLTAREN DOLO Emulgel (Tube 120 g)");
		services.getRecordTarmedOrRecordCantonalOrRecordUnclassified().add(recDrug);
		// tarmed1
		RecordTarmedType recTarmed =
			createTarmedRecord(cal, 9.57, 8.80, 8.19, 7.53, 16.34, "5", "00.0010",
				"Konsultation, erste 5 Min. (Grundkonsultation)");
		services.getRecordTarmedOrRecordCantonalOrRecordUnclassified().add(recTarmed);
		//tarmed 2
		recTarmed =
			createTarmedRecord(cal, 21.04, 19.36, 20.54, 18.90, 38.25, "6", "01.0110",
				"Taping, Kategorie I");
		services.getRecordTarmedOrRecordCantonalOrRecordUnclassified().add(recTarmed);
		
		detail.setServices(services);
		invoice.setDetail(detail);
		
		return invoice;
	}
	
	private RecordTarmedType createTarmedRecord(XMLGregorianCalendar cal, double unitMt,
		double amountMt, double unitTt, double amountTt, double amount, String recordId,
		String code, String value){
		RecordTarmedType recTarmed = new RecordTarmedType();
		recTarmed.setTreatment("ambulatory");
		recTarmed.setTariffType("001");
		recTarmed.setEanProvider("2099988872462");
		recTarmed.setEanResponsible("2099988872462");
		recTarmed.setBillingRole("both");
		recTarmed.setMedicalRole("self_employed");
		recTarmed.setBodyLocation("none");
		recTarmed.setUnitMt(unitMt);
		recTarmed.setUnitFactorMt(0.92);
		recTarmed.setScaleFactorMt(1.0);
		recTarmed.setExternalFactorMt(1.0);
		recTarmed.setAmountMt(amountMt);
		recTarmed.setUnitTt(unitTt);
		recTarmed.setUnitFactorTt(0.92);
		recTarmed.setScaleFactorTt(1.0);
		recTarmed.setExternalFactorTt(1.0);
		recTarmed.setAmountTt(amountTt);
		recTarmed.setAmount(amount);
		recTarmed.setVatRate(0.0);
		recTarmed.setValidate(true);
		recTarmed.setObligation(true);
		recTarmed.setRecordId(new BigInteger(recordId));
		recTarmed.setNumber(new BigInteger("1"));
		recTarmed.setQuantity(1.0);
		recTarmed.setDateBegin(cal);
		recTarmed.setCode(code);
		recTarmed.setValue(value);
		
		return recTarmed;
	}
}
