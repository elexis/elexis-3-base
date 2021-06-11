package ch.elexis.base.ch.arzttarife.xml.exporter;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.TarmedRechnung.Messages;
import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.TarmedRechnung.XMLExporterUtil;
import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.base.ch.arzttarife.xml.exporter.VatRateSum.VatRateElement;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.model.FallConstants;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDiagnosisReference;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.model.format.PostalAddress;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.ICoverageService.Tiers;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.services.holder.InvoiceServiceHolder;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.fd.invoice450.request.BalanceTGType;
import ch.fd.invoice450.request.BalanceTPType;
import ch.fd.invoice450.request.BillerAddressType;
import ch.fd.invoice450.request.BodyType;
import ch.fd.invoice450.request.CompanyType;
import ch.fd.invoice450.request.DebitorAddressType;
import ch.fd.invoice450.request.DiagnosisType;
import ch.fd.invoice450.request.Esr9Type;
import ch.fd.invoice450.request.EsrAddressType;
import ch.fd.invoice450.request.EsrQRType;
import ch.fd.invoice450.request.GarantType;
import ch.fd.invoice450.request.GuarantorAddressType;
import ch.fd.invoice450.request.InsuranceAddressType;
import ch.fd.invoice450.request.InvoiceType;
import ch.fd.invoice450.request.IvgLawType;
import ch.fd.invoice450.request.KvgLawType;
import ch.fd.invoice450.request.MvgLawType;
import ch.fd.invoice450.request.OnlineAddressType;
import ch.fd.invoice450.request.PatientAddressType;
import ch.fd.invoice450.request.PatientAddressType.Card;
import ch.fd.invoice450.request.PayantType;
import ch.fd.invoice450.request.PayloadType;
import ch.fd.invoice450.request.PersonType;
import ch.fd.invoice450.request.PostalAddressType;
import ch.fd.invoice450.request.ProcessingType;
import ch.fd.invoice450.request.ProcessingType.Demand;
import ch.fd.invoice450.request.PrologType;
import ch.fd.invoice450.request.ProviderAddressType;
import ch.fd.invoice450.request.ReferrerAddressType;
import ch.fd.invoice450.request.RequestType;
import ch.fd.invoice450.request.ServiceExType;
import ch.fd.invoice450.request.ServiceType;
import ch.fd.invoice450.request.ServicesType;
import ch.fd.invoice450.request.SoftwareType;
import ch.fd.invoice450.request.TelecomAddressType;
import ch.fd.invoice450.request.TransportType;
import ch.fd.invoice450.request.TransportType.Via;
import ch.fd.invoice450.request.TreatmentType;
import ch.fd.invoice450.request.UvgLawType;
import ch.fd.invoice450.request.VatRateType;
import ch.fd.invoice450.request.VatType;
import ch.fd.invoice450.request.VvgLawType;
import ch.fd.invoice450.request.ZipType;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;
import ch.rgw.tools.XMLTool;

public class Tarmed45Exporter {
	
	public static enum EsrType {
			esr9, esrQR
	}
	
	private static Logger logger = LoggerFactory.getLogger(Tarmed45Exporter.class);
	
	protected boolean printAtIntermediate;
	
	private ESR besr;
	
	private EsrType esrType = EsrType.esr9;
	
	private ServicesType services;
	
	/**
	 * Create a tarmed invoice request model for the {@link IInvoice}, and marshall it into the
	 * provided {@link OutputStream}.
	 * 
	 * @param invoice
	 * @param dest
	 * @param type
	 * @return
	 */
	public boolean doExport(final IInvoice invoice,
		final OutputStream dest,
		final IRnOutputter.TYPE type){
		
		try {
			besr = new ESR(
				(String) invoice.getMandator().getBiller()
					.getExtInfo(TarmedACL.getInstance().ESRNUMBER),
				(String) invoice.getMandator().getBiller().getExtInfo(TarmedACL
					.getInstance().ESRSUB),
				InvoiceServiceHolder.get().getCombinedId(invoice), ESR.ESR27);
			
			// calculate financial information first
			services = getServices(invoice);
			
			// build the tarmed invoice request for the IInvoice
			RequestType requestType = new RequestType();
			requestType.setModus(getModus(invoice.getCoverage()));
			requestType.setLanguage(Locale.getDefault().getLanguage());
			
			requestType.setProcessing(getProcessing(invoice));
			requestType.setPayload(getPayload(invoice));
			
			// cleanup
			services = null;
			
			return TarmedJaxbUtil.marshallInvoiceRequest(requestType, dest);
			
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error generating tarmed xml model", e);
			return false;
		}
	}
	
	public EsrType getEsrType(){
		return esrType;
	}
	
	public void setEsrType(EsrType esrType){
		this.esrType = esrType;
	}
	
	public boolean isPrintAtIntermediate(){
		return printAtIntermediate;
	}
	
	public void setPrintAtIntermediate(boolean value){
		printAtIntermediate = value;
	}
	
	protected String getModus(ICoverage coverage){
		return "production"; //$NON-NLS-1$
	}
	
	protected Object getBalance(IInvoice invoice){
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		ServicesFinancialInfo financialInfo = ServicesFinancialInfo.of(services);
		
		if (tiersType == Tiers.GARANT) {
			BalanceTGType balanceTGType = new BalanceTGType();
			
			balanceTGType.setCurrency(getCurrency(invoice));
			balanceTGType.setAmountPrepaid(invoice.getPayedAmount().doubleValue());
			balanceTGType.setAmountReminder(invoice.getDemandAmount().doubleValue());
			balanceTGType.setAmount(invoice.getTotalAmount().doubleValue());
			balanceTGType.setAmountDue(invoice.getOpenAmount().roundTo5().doubleValue());
			balanceTGType.setAmountObligations(financialInfo.getObligationSum().doubleValue());
			balanceTGType.setVat(getVat(invoice, financialInfo));
			
			return balanceTGType;
		} else if (tiersType == Tiers.PAYANT) {
			BalanceTPType balanceTPType = new BalanceTPType();
			
			balanceTPType.setCurrency(getCurrency(invoice));
			balanceTPType.setAmountReminder(invoice.getDemandAmount().doubleValue());
			balanceTPType.setAmount(invoice.getTotalAmount().doubleValue());
			balanceTPType.setAmountDue(invoice.getOpenAmount().roundTo5().doubleValue());
			balanceTPType.setAmountObligations(financialInfo.getObligationSum().doubleValue());
			balanceTPType.setVat(getVat(invoice, financialInfo));
			
			return balanceTPType;
		}
		return null;
	}
	
	protected VatType getVat(IInvoice invoice, ServicesFinancialInfo financialInfo){
		VatType vatType = new VatType();
		
		String vatNumber =
			(String) invoice.getMandator().getBiller().getExtInfo(XMLExporter.VAT_MANDANTVATNUMBER);
		if (StringUtils.isNotBlank(vatNumber)) {
			vatType.setVatNumber(vatNumber);
		}
		
		VatRateSum vatSum = financialInfo.getVatRateSum();
		vatType.setVat(vatSum.getSumVat().doubleValue());
		
		VatRateElement[] vatValues = vatSum.getRates().values().toArray(new VatRateElement[0]);
		Arrays.sort(vatValues);
		for (VatRateElement rate : vatValues) {
			VatRateType vatRateType = new VatRateType();
			
			vatRateType.setVatRate(rate.getScale());
			vatRateType.setAmount(rate.getAmount().doubleValue());
			vatRateType.setVat(rate.getVat().doubleValue());
			vatType.getVatRate().add(vatRateType);
		}
		return vatType;
	}
	
	protected String getCurrency(IInvoice invoice){
		String curr =
			(String) invoice.getMandator().getBiller().getExtInfo(Messages.XMLExporter_Currency);
		if (StringUtils.isNotBlank(curr)) {
			return curr; //$NON-NLS-1$
		}
		return "CHF";
	}
	
	protected Object getTiers(IInvoice invoice) throws DatatypeConfigurationException{
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		if(tiersType == Tiers.GARANT) {
			GarantType garantType = new GarantType();
			
			String paymentPeriode =
				(String) invoice.getMandator().getBiller().getExtInfo("rnfrist"); //$NON-NLS-1$
			if (StringTool.isNothing(paymentPeriode)) {
				paymentPeriode = "30"; //$NON-NLS-1$
			}
			garantType.setPaymentPeriod(
				DatatypeFactory.newInstance().newDuration("P" + paymentPeriode + "D")); //$NON-NLS-1$ //$NON-NLS-2$
			
			garantType.setBiller(getBiller(invoice));
			garantType.setDebitor(getDebitor(invoice));
			garantType.setProvider(getProvider(invoice));
			garantType.setInsurance(getInsurance(invoice));
			garantType.setPatient(getPatient(invoice));
			garantType.setGuarantor(getGuarantor(invoice));
			garantType.setReferrer(getReferrer(invoice));
			
			garantType.setBalance((BalanceTGType) getBalance(invoice));
			
			return garantType;
		} else if (tiersType == Tiers.PAYANT) {
			PayantType payantTyp = new PayantType();
			
			payantTyp.setBiller(getBiller(invoice));
			payantTyp.setDebitor(getDebitor(invoice));
			payantTyp.setProvider(getProvider(invoice));
			payantTyp.setInsurance(getInsurance(invoice));
			payantTyp.setPatient(getPatient(invoice));
			payantTyp.setGuarantor(getGuarantor(invoice));
			payantTyp.setReferrer(getReferrer(invoice));
			
			payantTyp.setBalance((BalanceTPType) getBalance(invoice));
			
			return payantTyp;
		}
		throw new IllegalStateException("Unknown tiers [" + tiersType + "]");
	}
	
	protected DebitorAddressType getDebitor(IInvoice invoice){
		DebitorAddressType debitorAddressType = new DebitorAddressType();
		
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		IContact debitor =
			XMLExporterUtil.getGuarantor(tiersType.getShortName(), invoice.getCoverage()
				.getPatient(),
			invoice.getCoverage());

		debitorAddressType.setEanParty(TarmedRequirements.getEAN(debitor));
		
		Object companyOrPerson = getCompanyOrPerson(debitor, false);
		if (companyOrPerson instanceof CompanyType) {
			debitorAddressType.setCompany((CompanyType) companyOrPerson);
		} else if (companyOrPerson instanceof PersonType) {
			debitorAddressType.setPerson((PersonType) companyOrPerson);
		}
		
		return debitorAddressType;
	}
	
	protected ReferrerAddressType getReferrer(IInvoice invoice){
		IContact auftraggeber =
			CoverageServiceHolder.get().getRequiredContact(invoice.getCoverage(), "Zuweiser");
		if (auftraggeber != null) {
			ReferrerAddressType referrerAddressType = new ReferrerAddressType();
			
			String ean = TarmedRequirements.getEAN(auftraggeber);
			if (StringUtils.isNotBlank(ean)) {
				referrerAddressType.setEanParty(ean);
			}
			String zsr = TarmedRequirements.getKSK(auftraggeber);
			if (StringUtils.isNotBlank(zsr)) {
				referrerAddressType.setZsr(zsr);
			}
			Object companyOrPerson = getCompanyOrPerson(auftraggeber, false);
			if (companyOrPerson instanceof CompanyType) {
				referrerAddressType.setCompany((CompanyType) companyOrPerson);
			} else if (companyOrPerson instanceof PersonType) {
				referrerAddressType.setPerson((PersonType) companyOrPerson);
			}
			return referrerAddressType;
		}
		return null;
	}
	
	protected GuarantorAddressType getGuarantor(IInvoice invoice){
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		IContact guarantor =
			XMLExporterUtil.getGuarantor(tiersType.getShortName(), invoice.getCoverage()
				.getPatient(),
			invoice.getCoverage());
		
		GuarantorAddressType guarantorAddressType = new GuarantorAddressType();
		
		Object companyOrPerson = getCompanyOrPerson(guarantor, false);
		if (companyOrPerson instanceof CompanyType) {
			guarantorAddressType.setCompany((CompanyType) companyOrPerson);
		} else if (companyOrPerson instanceof PersonType) {
			guarantorAddressType.setPerson((PersonType) companyOrPerson);
		}
		
		return guarantorAddressType;
	}
	
	protected PatientAddressType getPatient(IInvoice invoice) throws DatatypeConfigurationException{
		IPatient patient = invoice.getCoverage().getPatient();
		PatientAddressType patientAddressType = new PatientAddressType();

		if (patient == null) {
			throw new IllegalStateException("Invoice without patient");
		}
		patientAddressType.setGender(patient.getGender().toString().toLowerCase());
		LocalDateTime dateOfBirth = patient.getDateOfBirth();
		if (dateOfBirth == null) { 
			// make validator happy if we don't know the birthdate
			patientAddressType.setBirthdate(XMLExporterUtil.makeXMLDateTime(LocalDateTime.of(1, 1, 1, 0, 0)));
		} else {
			patientAddressType.setBirthdate(XMLExporterUtil.makeXMLDateTime(dateOfBirth));

		}
		patientAddressType.setPerson(getPerson(patient, false));
		if (StringUtils.isNotBlank((String) invoice.getCoverage().getExtInfo("VEKANr"))
			&& StringUtils.isNotBlank((String) invoice.getCoverage().getExtInfo("VEKAValid"))) {
			Card card = new Card();
			card.setCardId((String) invoice.getCoverage().getExtInfo("VEKANr"));
			card.setExpiryDate(XMLExporterUtil.makeTarmedDate((String) invoice.getCoverage().getExtInfo("VEKAValid")));
			patientAddressType.setCard(card);
		}
		return patientAddressType;
	}
	
	protected InsuranceAddressType getInsurance(IInvoice invoice){
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		IContact costBearer = invoice.getCoverage().getCostBearer();
		if (costBearer == null) {
			costBearer = invoice.getCoverage().getPatient();
		}
		String kEAN = TarmedRequirements.getEAN(costBearer);
		InsuranceAddressType insuranceAddressType = new InsuranceAddressType();
		
		if (tiersType == Tiers.GARANT) {
			if (costBearer.isOrganization()) {
				if (kEAN.matches("[0-9]{13,13}")) { //$NON-NLS-1$
					insuranceAddressType.setEanParty(kEAN);
					
					insuranceAddressType.setCompany(getCompany(costBearer, false));
				}
			}
		} else if (tiersType == Tiers.PAYANT) {
			insuranceAddressType.setEanParty(kEAN);
			insuranceAddressType.setCompany(getCompany(costBearer, false));
		}
		
		return insuranceAddressType.getEanParty() == null ? null : insuranceAddressType;
	}
	
	protected CompanyType getCompany(IContact contact, boolean useContactPostalAddress){
		if (contact.isOrganization()) {
			return (CompanyType) getCompanyOrPerson(contact, useContactPostalAddress);
		} else {
			// must be an organization so we fake one
			// note this may lead to a person mistreated as organization. So
			// these faults should be caught when generating bills
			CompanyType companyType = new CompanyType();
			
			companyType.setCompanyname(StringUtils.abbreviate(contact.getDescription1(), 35));
			companyType.setPostal(getPostalAddress(contact));
			companyType.setTelecom(getTelecom(contact));
			companyType.setOnline(getOnline(contact));
			return companyType;
		}
	}
	
	protected PersonType getPerson(IContact contact, boolean useContactPostalAddress){
		if (contact.isPerson()) {
			return (PersonType) getCompanyOrPerson(contact, useContactPostalAddress);
		} else {
			throw new IllegalStateException("Contact is not a person");
		}
	}
	
	protected ProviderAddressType getProvider(IInvoice invoice){
		ProviderAddressType providerAddressType = new ProviderAddressType();
		
		providerAddressType.setEanParty(TarmedRequirements.getEAN(invoice.getMandator()));
		String zsr = TarmedRequirements.getKSK(invoice.getMandator());
		if (StringUtils.isNotBlank(zsr)) {
			providerAddressType.setZsr(zsr);
		}
		String spec =
			(String) invoice.getMandator().getExtInfo(TarmedACL.getInstance().SPEC);
		if (StringUtils.isNotBlank(spec)) { //$NON-NLS-1$
			providerAddressType.setSpecialty(spec);
		}
		Object companyOrPerson = getCompanyOrPerson(invoice.getMandator(), false);
		if (companyOrPerson instanceof CompanyType) {
			providerAddressType.setCompany((CompanyType) companyOrPerson);
		} else if (companyOrPerson instanceof PersonType) {
			providerAddressType.setPerson((PersonType) companyOrPerson);
		}
		return providerAddressType;
	}
	
	protected BillerAddressType getBiller(IInvoice invoice){
		IContact biller = invoice.getMandator().getBiller();
		
		BillerAddressType billerAddressType = new BillerAddressType();
		billerAddressType.setEanParty(TarmedRequirements.getEAN(biller));
		String zsr = TarmedRequirements.getKSK(biller);
		if (StringUtils.isNotBlank(zsr)) {
			billerAddressType.setZsr(zsr);
		}
		
		String spec =
			(String) invoice.getMandator().getBiller().getExtInfo(TarmedACL.getInstance().SPEC);
		if (StringUtils.isNotBlank(spec)) { //$NON-NLS-1$
			billerAddressType.setSpecialty(spec);
		}
		Object companyOrPerson = getCompanyOrPerson(biller, false);
		if (companyOrPerson instanceof CompanyType) {
			billerAddressType.setCompany((CompanyType) companyOrPerson);
		} else if (companyOrPerson instanceof PersonType) {
			billerAddressType.setPerson((PersonType) companyOrPerson);
		}
		return billerAddressType;
	}
	
	protected Object getCompanyOrPerson(IContact contact, boolean useContactPostalAddress){
		if (contact.isOrganization()) {
			CompanyType companyType = new CompanyType();
			
			companyType.setCompanyname(StringUtils.abbreviate(contact.getDescription1(), 35));
			
			companyType.setPostal(getPostalAddress(contact));
			
			companyType.setTelecom(getTelecom(contact));
			companyType.setOnline(getOnline(contact));
			return companyType;
		} else if (contact.isPerson()) {
			PersonType personType = new PersonType();
			
			if (useContactPostalAddress) {
				PostalAddress postAnschrift = PostalAddress.ofText(contact.getPostalAddress());
				personType.setFamilyname(StringUtils.abbreviate(postAnschrift.getLastName(), 35));
				personType.setGivenname(StringUtils.abbreviate(postAnschrift.getFirstName(), 35));
				if (StringUtils.isNotBlank(postAnschrift.getSalutation())) {
					personType
						.setSalutation(StringUtils.abbreviate(postAnschrift.getSalutation(), 35));
				}
			} else {
				String salutation = (String) contact.getExtInfo("Anrede");
				if (StringUtils.isNotBlank(salutation)) {
					personType.setSalutation(StringUtils.abbreviate(salutation, 35));
				}
				if (contact.isPerson()) {
					IPerson person =
						CoreModelServiceHolder.get().load(contact.getId(), IPerson.class).get();
					if (StringUtils.isNotBlank(person.getTitel())) {
						personType.setTitle(StringUtils.abbreviate(person.getTitel(), 35));
					}
					if (StringUtils.isBlank(salutation)) {
						personType.setSalutation(
							StringUtils.abbreviate(PersonFormatUtil.getSalutation(person), 35));
					}
				}
				personType.setFamilyname(StringUtils.abbreviate(contact.getDescription1(), 35));
				personType.setGivenname(StringUtils.abbreviate(contact.getDescription2(), 35));
				if (StringUtils.isEmpty(contact.getDescription2())) {
					personType.setGivenname("Unbekannt"); // make validator happy //$NON-NLS-1$
				}
			}
			personType.setPostal(getPostalAddress(contact));
			personType.setTelecom(getTelecom(contact));
			personType.setOnline(getOnline(contact));
			
			return personType;
		}
		throw new IllegalStateException("Contact is no organization and no person");
	}
	
	protected OnlineAddressType getOnline(IContact contact){
		OnlineAddressType onlineAddressType = new OnlineAddressType();
		
		if (StringUtils.isNotBlank(contact.getEmail())) {
			String email =
				XMLExporterUtil.getValidXMLString(StringUtils.left(contact.getEmail(), 70));
			if (!email.matches(".+@.+")) { //$NON-NLS-1$
				email = "mail@invalid.invalid"; //$NON-NLS-1$
			}
			onlineAddressType.getEmail().add(email);
		}
		
		if (StringUtils.isNotBlank(contact.getWebsite())) {
			String website =
				XMLExporterUtil.getValidXMLString(StringUtils.left(contact.getWebsite(), 100));
			
			onlineAddressType.getUrl().add(website);
		}
		
		return onlineAddressType.getEmail().isEmpty() && onlineAddressType.getUrl().isEmpty() ? null
				: onlineAddressType;
	}
	
	protected TelecomAddressType getTelecom(IContact contact){
		TelecomAddressType telecomAddressType = new TelecomAddressType();
		
		if (StringUtils.isNotBlank(contact.getPhone1())) {
			telecomAddressType.getPhone().add(StringUtils.abbreviate(contact.getPhone1(), 25));
		}
		// only add the fax element if there is a phone, telcom without phone is not allowed by xsd
		if (!telecomAddressType.getPhone().isEmpty()) {
			if (StringUtils.isNotBlank(contact.getFax())) {
				telecomAddressType.getFax().add(StringUtils.abbreviate(contact.getFax(), 25));
			}
		}
		return telecomAddressType.getPhone().isEmpty() ? null : telecomAddressType;
	}
	
	protected PostalAddressType getPostalAddress(IContact contact){
		PostalAddressType postalAddressType = new PostalAddressType();
		
		String pobox = (String) contact.getExtInfo("Postfach");
		if (StringUtils.isNotBlank(pobox)) {
			postalAddressType.setPobox(StringUtils.abbreviate(pobox, 35));
		}
		if (StringUtils.isNotBlank(contact.getStreet())) {
			postalAddressType.setStreet(StringUtils.abbreviate(contact.getStreet(), 35));
		}
		postalAddressType.setCity(StringUtils.abbreviate(
			StringUtils.defaultIfBlank(contact.getCity(), Messages.XMLExporter_Unknown), 35));
		String zip = StringUtils.defaultIfBlank(contact.getZip(), "0000");
		ZipType zipType = new ZipType();
		zipType.setValue(StringUtils.left(zip, 9));
		if (StringUtils.isNotBlank(contact.getCountry().toString())) {
			zipType.setCountrycode(StringUtils.left(contact.getCountry().toString(), 3));
		}
		postalAddressType.setZip(zipType);
		
		return postalAddressType;
	}
	
	protected ServicesType getServices(IInvoice invoice) throws DatatypeConfigurationException{
		ServicesType servicesType = new ServicesType();
		List<IEncounter> encounters = invoice.getEncounters();
		int recordNumber = 1;
		LocalDate lastEncounterDate = null;
		int session = 1;
		for (IEncounter encounter : encounters) {
			List<IBilled> encounterBilled = encounter.getBilled();
			// encounters list is ordered by date, so we can just compare with previous
			LocalDate encounterDate = encounter.getDate();
			if (encounterDate.equals(lastEncounterDate)) {
				session++;
			} else {
				lastEncounterDate = encounterDate;
				session = 1;
			}
			
			boolean bRFE = false; // RFE already encoded
			
			for (IBilled billed : encounterBilled) {
				IBillable billable = billed.getBillable();
				if (billable == null) {
					logger.error(Messages.XMLExporter_ErroneusBill + invoice.getNumber()
						+ " Null-Verrechenbar bei Kons " //$NON-NLS-1$
						+ encounter.getLabel());
					continue;
				}
				
				if ("001".equals(billable.getCodeSystemCode())) { // tarmed service
					ServiceExType serviceExType = new ServiceExType();
					
					serviceExType.setTreatment("ambulatory");
					String bezug =
						(String) ((ITarmedLeistung) billable).getExtension().getExtInfo("Bezug"); //$NON-NLS-1$
					if (StringTool.isNothing(bezug)) {
						bezug = (String) billed.getExtInfo("Bezug"); //$NON-NLS-1$
					}
					if (!StringTool.isNothing(bezug)) {
						serviceExType.setRefCode(bezug);
					}
					serviceExType.setBillingRole("both");
					serviceExType.setMedicalRole("self_employed");
					serviceExType.setBodyLocation(ArzttarifeUtil.getSide(billed));
					
					double primaryScale = billed.getPrimaryScaleFactor();
					double secondaryScale = billed.getSecondaryScaleFactor();
					double mult = billed.getFactor();
					double tlAL = ArzttarifeUtil.getAL(billed);
					double tlTL = ArzttarifeUtil.getTL(billed);
					// build monetary values of this TarmedLeistung
					Money mAL = new Money((int) Math
						.round(tlAL * mult * billed.getAmount() * primaryScale * secondaryScale));
					Money mTL = new Money((int) Math
						.round(tlTL * mult * billed.getAmount() * primaryScale * secondaryScale));
					Money mAmountLocal = new Money((int) Math.round(
						(tlAL + tlTL) * mult * billed.getAmount() * primaryScale * secondaryScale));
					
					// tarmed AL
					serviceExType.setUnitMt(tlAL / 100.0);
					XMLExporterUtil.getALNotScaled(billed).ifPresent(d -> {
						serviceExType.setUnitMt(d / 100.0);
					});
					serviceExType.setUnitFactorMt(mult);
					serviceExType.setScaleFactorMt(primaryScale);
					XMLExporterUtil.getALScalingFactor(billed).ifPresent(f -> {
						f = f * primaryScale;
						serviceExType.setScaleFactorMt(f);
					});
					serviceExType.setExternalFactorMt(secondaryScale);
					serviceExType.setAmountMt(mAL.doubleValue());
					// tarmed TL
					serviceExType.setUnitTt(tlTL / 100.0);
					serviceExType.setUnitFactorTt(mult);
					serviceExType.setScaleFactorTt(primaryScale);
					serviceExType.setExternalFactorTt(secondaryScale);
					serviceExType.setAmountTt(mTL.doubleValue());
					
					serviceExType.setAmount(mAmountLocal.doubleValue());
					serviceExType.setVatRate(getVatRate(billed));
					
					if (!bRFE) {
						List<IReasonForEncounter> rfes =
							XMLExporterUtil.getReasonsForEncounter(encounter);
						if (rfes.size() > 0) {
							StringBuilder sb = new StringBuilder();
							for (IReasonForEncounter rfe : rfes) {
								sb.append("551_").append(rfe.getCode()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
							}
							serviceExType.setRemark(sb.toString());
						}
						bRFE = true;
					}

					serviceExType.setTariffType(billable.getCodeSystemCode());
					serviceExType.setCode(billable.getCode());
					serviceExType.setQuantity(billed.getAmount());
					serviceExType.setRecordId(recordNumber++);
					serviceExType.setSession(session);
					serviceExType.setName(billed.getText());
					serviceExType.setDateBegin(XMLExporterUtil.makeXMLDate(encounterDate));
					serviceExType.setProviderId(TarmedRequirements.getEAN(encounter.getMandator()));
					serviceExType.setResponsibleId(XMLExporterUtil.getResponsibleEAN(encounter));
					serviceExType.setObligation(getObligation(billed, billable));
					
					servicesType.getServiceExOrService().add(serviceExType);
				} else { // any service
					ServiceType serviceType = new ServiceType();
					
					serviceType.setAmount(billed.getTotal().doubleValue());
					serviceType.setVatRate(getVatRate(billed));
					
					serviceType.setUnit(billed.getPrice().doubleValue());
					serviceType.setUnitFactor(billed.getFactor());
					
					serviceType.setTariffType(billable.getCodeSystemCode());
					serviceType.setCode(billable.getCode());
					serviceType.setQuantity(billed.getAmount());
					serviceType.setRecordId(recordNumber++);
					serviceType.setSession(session);
					serviceType.setName(billed.getText());
					serviceType.setDateBegin(XMLExporterUtil.makeXMLDate(encounterDate));
					serviceType.setProviderId(TarmedRequirements.getEAN(encounter.getMandator()));
					serviceType.setResponsibleId(XMLExporterUtil.getResponsibleEAN(encounter));
					serviceType.setObligation(getObligation(billed, billable));
					
					servicesType.getServiceExOrService().add(serviceType);
				}
			}
		}
		
		return servicesType;
	}
	
	protected Boolean getObligation(IBilled billed, IBillable billable){
		String tariffTyp = billable.getCodeSystemCode();
		if (billable instanceof ITarmedLeistung) {
			return ArzttarifeUtil.isObligation(billed);
		}
		if (billable instanceof IArticle) {
			if ("452".equals(tariffTyp)) {
				return true;
			}
			String ckzl = (String) ((IArticle) billable).getExtInfo("Kassentyp"); // MedikamentImporter#KASSENTYP
			return "1".equals(ckzl);
		}
		// physio
		if ("311".equals(tariffTyp)) {
			return true;
		}
		if ("312".equals(tariffTyp)) {
			return true;
		}
		// laboratory eal
		if ("317".equals(tariffTyp)) {
			return true;
		}
		return false;
	}
	
	protected Double getVatRate(IBilled billed){
		Double ret = 0.0;
		
		String vatScale = (String) billed.getExtInfo(Constants.VAT_SCALE);
		if (vatScale != null && vatScale.length() > 0) {
			ret = Double.parseDouble(vatScale);
		}
		
		return ret;
	}
	
	protected PayloadType getPayload(IInvoice invoice) throws DatatypeConfigurationException{
		PayloadType payloadType = new PayloadType();
		
		//		payloadType.setCredit(value);
		payloadType.setInvoice(getInvoice(invoice));
		
		payloadType.setBody(getBody(invoice));
		
		return payloadType;
	}
	
	protected InvoiceType getInvoice(IInvoice invoice) throws DatatypeConfigurationException{
		InvoiceType invoiceType = new InvoiceType();
		
		invoiceType.setRequestTimestamp((int) (new Date().getTime() / 1000));
		invoiceType.setRequestDate(XMLExporterUtil.makeXMLDate(invoice.getDate()));
		invoiceType.setRequestId(InvoiceServiceHolder.get().getCombinedId(invoice));
		
		return invoiceType;
	}
	
	protected BodyType getBody(IInvoice invoice) throws DatatypeConfigurationException{
		BodyType bodyType = new BodyType();
		
		bodyType.setRole("physician");
		bodyType.setPlace("practice");
		
		bodyType.setProlog(getProlog(invoice));
		//		bodyType.setRemark(value);
		
		Object tiers = getTiers(invoice);
		if (tiers instanceof GarantType) {
			bodyType.setTiersGarant((GarantType) tiers);
		} else if (tiers instanceof PayantType) {
			bodyType.setTiersPayant((PayantType) tiers);
		}
		
		if (esrType == EsrType.esr9) {
			bodyType.setEsr9(getEsr9(invoice));
		} else if (esrType == EsrType.esrQR) {
			bodyType.setEsrQR(getEsrQR(invoice));
		}
		
		Object lawType = getLawType(invoice);
		if (lawType instanceof KvgLawType) {
			bodyType.setKvg((KvgLawType) lawType);
		} else if (lawType instanceof UvgLawType) {
			bodyType.setUvg((UvgLawType) lawType);
		} else if (lawType instanceof IvgLawType) {
			bodyType.setIvg((IvgLawType) lawType);
		} else if (lawType instanceof MvgLawType) {
			bodyType.setMvg((MvgLawType) lawType);
		} else if (lawType instanceof VvgLawType) {
			bodyType.setVvg((VvgLawType) lawType);
		}
		
		bodyType.setTreatment(getTreatment(invoice));
		
		bodyType.setServices(services);
		
		return bodyType;
	}
	
	protected TreatmentType getTreatment(IInvoice invoice) throws DatatypeConfigurationException{
		TreatmentType treatmentType = new TreatmentType();
		
		treatmentType.setDateBegin(XMLExporterUtil.makeXMLDate(invoice.getDateFrom()));
		treatmentType.setDateEnd(XMLExporterUtil.makeXMLDate(invoice.getDateTo()));
		treatmentType
			.setCanton((String) invoice.getMandator().getExtInfo(TarmedACL.getInstance().KANTON));
		treatmentType.setReason(getTreatmentReason(invoice.getCoverage()));
		
		for (IDiagnosisReference invoiceDiagnosis : getInvoiceDiagnosis(invoice)) {
			DiagnosisType diagnosisType = new DiagnosisType();
			String type = getTreatmentDiagnosisType(invoiceDiagnosis);
			diagnosisType.setType(type);
			String code = invoiceDiagnosis.getCode();
			if (type.equalsIgnoreCase("freetext")) {
				diagnosisType.setValue(invoiceDiagnosis.getText());
			} else {
				diagnosisType.setCode(StringUtils.left(code, 12));
			}
			treatmentType.getDiagnosis().add(diagnosisType);
		}
		return treatmentType;
	}
	
	private String getTreatmentDiagnosisType(IDiagnosisReference invoiceDiagnosis){
		String name = invoiceDiagnosis.getCodeSystemName();
		if (name != null) {
			if (name.equalsIgnoreCase("freetext")) { //$NON-NLS-1$
				return "freetext"; //$NON-NLS-1$
			}
			if (name.equalsIgnoreCase("ICD-10")) { //$NON-NLS-1$
				return "ICD"; //$NON-NLS-1$
			}
			if (name.equalsIgnoreCase("by contract")) { //$NON-NLS-1$
				return "by_contract"; //$NON-NLS-1$
			}
			if (name.equalsIgnoreCase("ICPC")) {//$NON-NLS-1$
				return "ICPC"; //$NON-NLS-1$
			}
			if (name.equalsIgnoreCase("birthdefect")) { //$NON-NLS-1$
				return "birthdefect"; //$NON-NLS-1$
			}
		}
		return "by_contract"; //$NON-NLS-1$
	}
	
	protected List<IDiagnosisReference> getInvoiceDiagnosis(IInvoice invoice){
		ArrayList<IDiagnosisReference> ret = new ArrayList<IDiagnosisReference>();
		List<IEncounter> encounters = invoice.getEncounters();
		for (IEncounter encounter : encounters) {
			List<IDiagnosisReference> encounterDiagnosis = encounter.getDiagnoses();
			for (IDiagnosisReference encounterDiagnose : encounterDiagnosis) {
				String dgc = encounterDiagnose.getCode();
				if (dgc != null) {
					ret.add(encounterDiagnose);
				}
			}
		}
		return ret;
	}
	
	private String getTreatmentReason(ICoverage coverage){
		String type = coverage.getReason();
		if (type == null) {
			return XMLExporter.DISEASE;
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_DISEASE)) {
			return XMLExporter.DISEASE;
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_ACCIDENT)) {
			return "accident"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_MATERNITY)) {
			return "maternity"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_PREVENTION)) {
			return "prevention"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(FallConstants.TYPE_BIRTHDEFECT)) {
			return XMLExporter.BIRTHDEFECT;
		}
		return XMLExporter.DISEASE;
	}
	
	protected Object getLawType(IInvoice invoice) throws DatatypeConfigurationException{
		BillingLaw law = invoice.getCoverage().getBillingSystem().getLaw();
		if (law == BillingLaw.KVG) {
			KvgLawType kvgLawType = new KvgLawType();
			
			kvgLawType.setInsuredId(getInsuredId(invoice));
			kvgLawType.setCaseId(getCaseNumber(invoice));
			
			return kvgLawType;
		} else if (law == BillingLaw.UVG) {
			UvgLawType uvgLawType = new UvgLawType();
			
			String casenumber = getCaseNumber(invoice);
			if (StringTool.isNothing(casenumber)) {
				casenumber = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
					TarmedRequirements.ACCIDENT_NUMBER);
			}
			if (StringUtils.isNotBlank(casenumber)) {
				uvgLawType.setCaseId(casenumber);
			}
			String casedate = (String) invoice.getCoverage().getExtInfo("Unfalldatum"); //$NON-NLS-1$
			if (StringUtils.isEmpty(casedate)) {
				uvgLawType.setCaseDate(XMLExporterUtil.makeXMLDate(invoice.getDateFrom()));
			} else {
				TimeTool timeTool = new TimeTool(casedate);
				uvgLawType.setCaseDate(XMLExporterUtil.makeXMLDate(timeTool.toLocalDate()));
			}
			uvgLawType.setSsn(getSSN(invoice));
			uvgLawType.setInsuredId(getInsuredId(invoice));
			
			return uvgLawType;
		} else if (law == BillingLaw.IV) {
			IvgLawType ivgLawType = new IvgLawType();
			
			ivgLawType.setCaseId(getCaseNumber(invoice));
			ivgLawType.setSsn(getSSN(invoice));
			String nif =
				TarmedRequirements.getNIF(invoice.getMandator().getBiller()).replaceAll("[^0-9]", //$NON-NLS-1$
					StringConstants.EMPTY);
			ivgLawType.setNif(nif);
			
			return ivgLawType;
		} else if (law == BillingLaw.MV) {
			MvgLawType mvgLawType = new MvgLawType();
			
			mvgLawType.setSsn(getSSN(invoice));
			mvgLawType.setInsuredId(getInsuredId(invoice));
			mvgLawType.setCaseId(getCaseNumber(invoice));
			
			return mvgLawType;
		} else if (law == BillingLaw.VVG) {
			VvgLawType vvgLawType = new VvgLawType();
			
			vvgLawType.setInsuredId(getInsuredId(invoice));
			vvgLawType.setCaseId(getCaseNumber(invoice));
			
			return vvgLawType;
		} else {
			throw new UnsupportedOperationException("Billing law [" + law + "] not implemented");
		}
	}
	
	protected String getCaseNumber(IInvoice invoice){
		String caseNumber = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
			TarmedRequirements.CASE_NUMBER);
		return caseNumber.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
	}
	
	protected String getInsuredId(IInvoice invoice){
		String vnummer = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
			TarmedRequirements.INSURANCE_NUMBER);
		if (StringTool.isNothing(vnummer)) {
			vnummer = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
				TarmedRequirements.CASE_NUMBER);
		}
		if (StringTool.isNothing(vnummer)) {
			vnummer = invoice.getCoverage().getPatient().getId();
		}
		return vnummer;
	}
	
	protected String getSSN(IInvoice invoice){
		String ahv = TarmedRequirements.getAHV(invoice.getCoverage().getPatient())
			.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		if (ahv.length() == 0) {
			ahv = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
				TarmedRequirements.SSN)
				.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		}
		return ahv;
	}
	
	protected EsrQRType getEsrQR(IInvoice invoice){
		EsrQRType esrQRType = new EsrQRType();
		
		String iban = (String) invoice.getMandator().getBiller().getExtInfo("IBAN");
		if (StringUtils.isEmpty(iban)) {
			MessageDialog.openError(null, Messages.XMLExporter_MandatorErrorCaption,
				Messages.XMLExporter_MandatorErrorEsr + " [" + invoice.getMandator().getLabel() //$NON-NLS-1$
					+ "]"); //$NON-NLS-1$
			return null;
		}
		esrQRType.setIban(iban);
		esrQRType.setReferenceNumber(besr.makeRefNr(false));
		
		IContact creditor = invoice.getMandator().getBiller();
		EsrAddressType esrAddressType = new EsrAddressType();
		Object creditorCompanyOrPerson = getCompanyOrPerson(creditor, false);
		if (creditorCompanyOrPerson instanceof CompanyType) {
			esrAddressType.setCompany((CompanyType) creditorCompanyOrPerson);
		} else if (creditorCompanyOrPerson instanceof PersonType) {
			esrAddressType.setPerson((PersonType) creditorCompanyOrPerson);
		}
		esrQRType.setCreditor(esrAddressType);
		
		String bankid =
			(String) invoice.getMandator().getBiller().getExtInfo(TarmedACL.getInstance().RNBANK);
		if (StringUtils.isNotBlank(bankid)) { //$NON-NLS-1$
			Optional<IOrganization> bank =
				CoreModelServiceHolder.get().load(bankid, IOrganization.class);
			bank.ifPresent(b -> {
				EsrAddressType bankEsrAddressType = new EsrAddressType();
				CompanyType bankCompany = getCompany(b, false);
				bankEsrAddressType.setCompany(bankCompany);
				esrQRType.setBank(bankEsrAddressType);
			});
		}
		
		return esrQRType;
	}
	
	protected Esr9Type getEsr9(IInvoice invoice){
		Esr9Type esr9Type = new Esr9Type();
		
		String participantNumber = besr.makeParticipantNumber(true);
		if (StringUtils.isEmpty(participantNumber)) {
			MessageDialog.openError(null, Messages.XMLExporter_MandatorErrorCaption,
				Messages.XMLExporter_MandatorErrorEsr + " [" + invoice.getMandator().getLabel() //$NON-NLS-1$
					+ "]"); //$NON-NLS-1$
			return null;
		}
		esr9Type.setParticipantNumber(participantNumber);
		esr9Type.setType("16or27");
		esr9Type.setReferenceNumber(besr.makeRefNr(true));
		String codingline = besr.createCodeline(
			XMLTool.moneyToXmlDouble(invoice.getOpenAmount()).replaceFirst("[.,]", ""), null); //$NON-NLS-1$ //$NON-NLS-2$
		esr9Type.setCodingLine(codingline);
		
		IContact creditor = invoice.getMandator().getBiller();
		EsrAddressType esrAddressType = new EsrAddressType();
		Object creditorCompanyOrPerson = getCompanyOrPerson(creditor, false);
		if (creditorCompanyOrPerson instanceof CompanyType) {
			esrAddressType.setCompany((CompanyType) creditorCompanyOrPerson);
		} else if (creditorCompanyOrPerson instanceof PersonType) {
			esrAddressType.setPerson((PersonType) creditorCompanyOrPerson);
		}
		esr9Type.setCreditor(esrAddressType);
		
		String bankid =
			(String) invoice.getMandator().getBiller().getExtInfo(TarmedACL.getInstance().RNBANK);
		if (StringUtils.isNotBlank(bankid)) { //$NON-NLS-1$
			Optional<IOrganization> bank =
				CoreModelServiceHolder.get().load(bankid, IOrganization.class);
			bank.ifPresent(b -> {
				EsrAddressType bankEsrAddressType = new EsrAddressType();
				CompanyType bankCompany = getCompany(b, false);
				bankEsrAddressType.setCompany(bankCompany);
				esr9Type.setBank(bankEsrAddressType);
			});
		}
		
		return esr9Type;
	}
	
	protected PrologType getProlog(IInvoice invoice){
		PrologType prologType = new PrologType();
		
		SoftwareType softwareType = new SoftwareType();
		softwareType.setName("Elexis");
		VersionInfo vi = new VersionInfo(CoreHub.Version);
		softwareType.setVersion(Long.valueOf(vi.getMaior() + vi.getMinor() + vi.getRevision()));
		prologType.setPackage(softwareType);
		
		softwareType = new SoftwareType();
		softwareType.setName("JAXB");
		softwareType.setVersion((long) getJavaVersion());
		prologType.setGenerator(softwareType);
		
		return prologType;
	}
	
	private int getJavaVersion(){
		String version = System.getProperty("java.version");
		if (version.startsWith("1.")) {
			version = version.substring(2, 3);
		} else {
			int dot = version.indexOf(".");
			if (dot != -1) {
				version = version.substring(0, dot);
			}
		}
		return Integer.parseInt(version);
	}
	
	protected ProcessingType getProcessing(IInvoice invoice) throws DatatypeConfigurationException{
		ProcessingType processingType = new ProcessingType();
		processingType.setPrintAtIntermediate(printAtIntermediate);
		
		if (CoverageServiceHolder.get().getCopyForPatient(invoice.getCoverage())) {
			processingType.setPrintCopyToGuarantor(true);
		}
		
		TransportType transportType = new TransportType();
		transportType.setFrom(TarmedRequirements.getEAN(invoice.getMandator()));
		transportType.setTo(XMLExporterUtil.getRecipientEAN(invoice));
		
		logger.info("Using intermediate EAN [" + XMLExporterUtil.getIntermediateEAN(invoice) + "]");
		Via via = new Via();
		via.setVia(XMLExporterUtil.getIntermediateEAN(invoice));
		via.setSequenceId(1);
		transportType.getVia().add(via);
		
		// insert demand if TG and TC contract
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		if (Tiers.GARANT == tiersType
			&& (TarmedRequirements.hasTCContract(invoice.getMandator()))) {
			String tcCode = TarmedRequirements.getTCCode(invoice.getMandator());
			Demand demand = new ProcessingType.Demand();
			demand.setTcDemandId(0);
			
			String tcToken = besr.createCodeline(
				XMLTool.moneyToXmlDouble(invoice.getOpenAmount()).replaceFirst("[.,]", //$NON-NLS-1$
					""), //$NON-NLS-1$
				tcCode);
			demand.setTcToken(tcToken);
			demand.setInsuranceDemandDate(XMLExporterUtil.makeXMLDate(invoice.getDate()));
			
			processingType.setDemand(demand);
		}
		processingType.setTransport(transportType);
		
		return processingType;
	}
}
