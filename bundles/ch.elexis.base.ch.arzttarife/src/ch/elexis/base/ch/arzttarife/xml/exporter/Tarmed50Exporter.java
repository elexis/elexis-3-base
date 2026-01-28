package ch.elexis.base.ch.arzttarife.xml.exporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.TarmedRechnung.Messages;
import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.TarmedRechnung.XMLExporterProcessing;
import ch.elexis.TarmedRechnung.XMLExporterUtil;
import ch.elexis.base.ch.arzttarife.coding.SectionCodeCodingContribution;
import ch.elexis.base.ch.arzttarife.importer.TrustCenters;
import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.base.ch.arzttarife.xml.exporter.VatRateSum.VatRateElement;
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.ExtInfoConstants;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.interfaces.IRnOutputter.TYPE;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.ICodingContribution;
import ch.elexis.core.model.FallConstants;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.ICustomService;
import ch.elexis.core.model.IDiagnosisReference;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.model.format.PostalAddress;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.ICoverageService.Tiers;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.services.holder.InvoiceServiceHolder;
import ch.elexis.core.types.ArticleSubTyp;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.types.Country;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.tarmedprefs.PreferenceConstants;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.fd.invoice500.request.BalanceTGType;
import ch.fd.invoice500.request.BalanceTPType;
import ch.fd.invoice500.request.BillerGLNAddressType;
import ch.fd.invoice500.request.BillersAddressType;
import ch.fd.invoice500.request.BodyType;
import ch.fd.invoice500.request.CompanyType;
import ch.fd.invoice500.request.CountryType;
import ch.fd.invoice500.request.DebitorAddressType;
import ch.fd.invoice500.request.DiagnosisType;
import ch.fd.invoice500.request.DocumentType;
import ch.fd.invoice500.request.DocumentsType;
import ch.fd.invoice500.request.EsrAddressType;
import ch.fd.invoice500.request.EsrQRType;
import ch.fd.invoice500.request.GarantType;
import ch.fd.invoice500.request.GuarantorAddressType;
import ch.fd.invoice500.request.InstructionType;
import ch.fd.invoice500.request.InstructionsType;
import ch.fd.invoice500.request.InsuranceAddressType;
import ch.fd.invoice500.request.InsuredAddressType;
import ch.fd.invoice500.request.InvoiceType;
import ch.fd.invoice500.request.LawType;
import ch.fd.invoice500.request.OnlineAddressType;
import ch.fd.invoice500.request.PartnerAddressType;
import ch.fd.invoice500.request.PartnersAddressType;
import ch.fd.invoice500.request.PatientAddressType;
import ch.fd.invoice500.request.PayantType;
import ch.fd.invoice500.request.PayloadType;
import ch.fd.invoice500.request.PersonType;
import ch.fd.invoice500.request.PostalAddressType;
import ch.fd.invoice500.request.ProcessingType;
import ch.fd.invoice500.request.PrologType;
import ch.fd.invoice500.request.ProviderGLNAddressType;
import ch.fd.invoice500.request.ProvidersAddressType;
import ch.fd.invoice500.request.ReminderType;
import ch.fd.invoice500.request.RequestType;
import ch.fd.invoice500.request.ServiceExType;
import ch.fd.invoice500.request.ServiceType;
import ch.fd.invoice500.request.ServicesType;
import ch.fd.invoice500.request.SoftwareType;
import ch.fd.invoice500.request.StreetType;
import ch.fd.invoice500.request.TelecomAddressType;
import ch.fd.invoice500.request.TransportType;
import ch.fd.invoice500.request.TransportType.Via;
import ch.fd.invoice500.request.TreatmentType;
import ch.fd.invoice500.request.VatRateType;
import ch.fd.invoice500.request.VatType;
import ch.fd.invoice500.request.XtraDrugType;
import ch.fd.invoice500.request.ZipType;
import ch.fd.invoice500.request.ZsrAddressType;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class Tarmed50Exporter {

	public static String UNKNOWN_SSN = "7569999999991";

	public static final String EAN_PSEUDO = "2000000000008"; //$NON-NLS-1$

	public static enum EsrType {
		esr9, esrQR
	}

	private static Logger logger = LoggerFactory.getLogger(Tarmed50Exporter.class);

	protected boolean printAtIntermediate;

	private ESR besr;

	private EsrType esrType = EsrType.esrQR;

	private boolean updateElectronicDelivery = false;

	private SectionCodeCodingContribution sectionCodeContribution;

	/**
	 * Create a tarmed invoice request model for the {@link IInvoice}, and marshall
	 * it into the provided {@link OutputStream}.
	 *
	 * @param invoice
	 * @param dest
	 * @param type
	 * @return
	 */
	public boolean doExport(final IInvoice invoice, final OutputStream dest, final IRnOutputter.TYPE type) {

		try {
			besr = null;
			// build the tarmed invoice request for the IInvoice
			RequestType requestType = new RequestType();
			requestType.setModus(getModus(invoice.getCoverage()));
			requestType.setLanguage(Locale.getDefault().getLanguage());
			String uuid = UUID.randomUUID().toString().replace("-", "");
			requestType.setGuid(uuid);

			requestType.setProcessing(getProcessing(invoice));
			requestType.setPayload(getPayload(invoice, type));

			if (updateElectronicDelivery) {
				updateElectronicDelivery(invoice, requestType);
			}

			if (getBalanceAmount(requestType) != null) {
				if (invoice.adjustAmount(getBalanceAmount(requestType).roundTo5()) == false) {
					invoice.reject(InvoiceState.REJECTCODE.SUM_MISMATCH, Messages.XMLExporter_SumMismatch);
				}
				// save rounded amount
				CoreModelServiceHolder.get().save(invoice);
			}

			return TarmedJaxbUtil.marshallInvoiceRequest(requestType, dest);

		} catch (DatatypeConfigurationException e) {
			LoggerFactory.getLogger(getClass()).error("Error generating tarmed xml model", e);
			return false;
		}
	}

	public Money getBalanceAmount(RequestType requestType) {
		if (requestType.getPayload() != null && requestType.getPayload().getBody() != null) {
			if (requestType.getPayload().getBody().getTiersGarant() != null) {
				return new Money(requestType.getPayload().getBody().getTiersGarant().getBalance().getAmount());
			} else if (requestType.getPayload().getBody().getTiersPayant() != null) {
				return new Money(requestType.getPayload().getBody().getTiersPayant().getBalance().getAmount());
			}
		}
		return null;
	}

	public EsrType getEsrType() {
		return esrType;
	}

	public boolean isPrintAtIntermediate() {
		return printAtIntermediate;
	}

	public void setPrintAtIntermediate(boolean value) {
		printAtIntermediate = value;
	}

	protected String getModus(ICoverage coverage) {
		return "production"; //$NON-NLS-1$
	}

	protected Object getBalance(IInvoice invoice) {
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());

		ServicesFinancialInfo financialInfo = ServicesFinancialInfo.of(getServices(invoice), invoice.getDateFrom());

		if (tiersType == Tiers.GARANT) {
			BalanceTGType balanceTGType = new BalanceTGType();

			balanceTGType.setCurrency(getCurrency(invoice));
			balanceTGType.setAmountPrepaid(invoice.getPayedAmount().doubleValue());
			if (!invoice.getDemandAmount().isZero()) {
				balanceTGType.setAmountReminder(invoice.getDemandAmount().doubleValue());
			}
			// use tariff sum as invoice total is rounded to 5
			balanceTGType.setAmount(financialInfo.getTotalSum().doubleValue());
			balanceTGType.setAmountDue(invoice.getOpenAmount().roundTo5().doubleValue());
			balanceTGType.setVat(getVat(invoice, financialInfo));

			return balanceTGType;
		} else if (tiersType == Tiers.PAYANT) {
			BalanceTPType balanceTPType = new BalanceTPType();

			balanceTPType.setCurrency(getCurrency(invoice));
			if (!invoice.getDemandAmount().isZero()) {
				balanceTPType.setAmountReminder(invoice.getDemandAmount().doubleValue());
			}
			// use tariff sum as invoice total is rounded to 5
			balanceTPType.setAmount(financialInfo.getTotalSum().doubleValue());
			balanceTPType.setAmountDue(invoice.getOpenAmount().roundTo5().doubleValue());
			balanceTPType.setVat(getVat(invoice, financialInfo));

			return balanceTPType;
		}
		return null;
	}

	protected VatType getVat(IInvoice invoice, ServicesFinancialInfo financialInfo) {
		VatType vatType = new VatType();

		String vatNumber = (String) invoice.getMandator().getBiller().getExtInfo(XMLExporter.VAT_MANDANTVATNUMBER);
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

	protected String getCurrency(IInvoice invoice) {
		String curr = (String) invoice.getMandator().getBiller().getExtInfo(Messages.XMLExporter_Currency);
		if (StringUtils.isNotBlank(curr)) {
			return curr; // $NON-NLS-1$
		}
		return "CHF";
	}

	protected Object getTiers(IInvoice invoice) throws DatatypeConfigurationException {
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		if (tiersType == Tiers.GARANT) {
			GarantType garantType = new GarantType();

			garantType.setBillers(getBiller(invoice));
			garantType.setDebitor(getDebitor(invoice));
			garantType.setProviders(getProvider(invoice));
			garantType.setInsurance(getInsurance(invoice));
			garantType.setPatient(getPatient(invoice));
			garantType.setGuarantor(getGuarantor(invoice));
			garantType.setInsured(getInsured(invoice));
			garantType.setPartners(getPartners(invoice));

			garantType.setBalance((BalanceTGType) getBalance(invoice));

			return garantType;
		} else if (tiersType == Tiers.PAYANT) {
			PayantType payantTyp = new PayantType();

			payantTyp.setBillers(getBiller(invoice));
			payantTyp.setDebitor(getDebitor(invoice));
			payantTyp.setProviders(getProvider(invoice));
			payantTyp.setInsurance(getInsurance(invoice));
			payantTyp.setPatient(getPatient(invoice));
			payantTyp.setGuarantor(getGuarantor(invoice));
			payantTyp.setInsured(getInsured(invoice));
			payantTyp.setPartners(getPartners(invoice));

			payantTyp.setBalance((BalanceTPType) getBalance(invoice));

			return payantTyp;
		}
		throw new IllegalStateException("Unknown tiers [" + tiersType + "]");
	}

	private PartnersAddressType getPartners(IInvoice invoice) {
		PartnersAddressType partnersType = new PartnersAddressType();

		// referrer
		IContact referrer = CoverageServiceHolder.get().getRequiredContact(invoice.getCoverage(), "Zuweiser");
		if (referrer != null) {
			PartnerAddressType referrerPartner = new PartnerAddressType();
			referrerPartner.setType("referrer");
			String ean = TarmedRequirements.getEAN(referrer, EAN_PSEUDO);
			if (StringUtils.isNotBlank(ean)) {
				referrerPartner.setGln(ean);
			}
			String zsr = TarmedRequirements.getKSK(referrer);
			if (StringUtils.isNotBlank(zsr)) {
				referrerPartner.setZsr(zsr);
			}
			Object companyOrPerson = getCompanyOrPerson(referrer, false);
			if (companyOrPerson instanceof CompanyType) {
				referrerPartner.setCompany((CompanyType) companyOrPerson);
			} else if (companyOrPerson instanceof PersonType) {
				referrerPartner.setPerson((PersonType) companyOrPerson);
			}
			partnersType.getPartner().add(referrerPartner);
		}

		return partnersType;
	}

	protected DebitorAddressType getDebitor(IInvoice invoice) {
		DebitorAddressType debitorAddressType = new DebitorAddressType();

		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		IContact debitor = null;
		if (tiersType == Tiers.GARANT) {
			debitor = XMLExporterUtil.getGuarantor(tiersType.getShortName(), invoice.getCoverage().getPatient(),
					invoice.getCoverage());
		} else {
			debitor = invoice.getCoverage().getCostBearer();
		}
		debitorAddressType.setGln(TarmedRequirements.getEAN(debitor, EAN_PSEUDO));

		Object companyOrPerson = getCompanyOrPerson(debitor, false);
		if (companyOrPerson instanceof CompanyType) {
			debitorAddressType.setCompany((CompanyType) companyOrPerson);
		} else if (companyOrPerson instanceof PersonType) {
			debitorAddressType.setPerson((PersonType) companyOrPerson);
		}

		return debitorAddressType;
	}

	protected GuarantorAddressType getGuarantor(IInvoice invoice) {
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		IContact guarantor = XMLExporterUtil.getGuarantor(tiersType.getShortName(), invoice.getCoverage().getPatient(),
				invoice.getCoverage());
		if (guarantor != null) {
			GuarantorAddressType guarantorAddressType = new GuarantorAddressType();

			Object companyOrPerson = getCompanyOrPerson(guarantor, false);
			if (companyOrPerson instanceof CompanyType) {
				guarantorAddressType.setCompany((CompanyType) companyOrPerson);
			} else if (companyOrPerson instanceof PersonType) {
				guarantorAddressType.setPerson((PersonType) companyOrPerson);
			}

			return guarantorAddressType;
		}
		return null;
	}

	protected InsuredAddressType getInsured(IInvoice invoice) throws DatatypeConfigurationException {
		IPatient patient = invoice.getCoverage().getPatient();
		InsuredAddressType insuredAddressType = new InsuredAddressType();

		if (patient == null) {
			throw new IllegalStateException("Invoice without patient");
		}
		insuredAddressType.setGender(patient.getGender().toString().toLowerCase());
		insuredAddressType.setSex(patient.getGender().toString().toLowerCase());
		LocalDateTime dateOfBirth = patient.getDateOfBirth();
		if (dateOfBirth == null) {
			// make validator happy if we don't know the birthdate
			insuredAddressType.setBirthdate(XMLExporterUtil.makeXMLDateTime(LocalDateTime.of(1, 1, 1, 0, 0)));
		} else {
			insuredAddressType.setBirthdate(XMLExporterUtil.makeXMLDateTime(dateOfBirth));

		}
		insuredAddressType.setPerson(getPerson(patient, false));
		if (StringUtils.isNotBlank((String) invoice.getCoverage().getExtInfo("VEKANr"))) {
			insuredAddressType.setCardId((String) invoice.getCoverage().getExtInfo("VEKANr"));
		}
		insuredAddressType.setSsn(getSSN(invoice));
		return insuredAddressType;
	}

	protected PatientAddressType getPatient(IInvoice invoice) throws DatatypeConfigurationException {
		IPatient patient = invoice.getCoverage().getPatient();
		PatientAddressType patientAddressType = new PatientAddressType();

		if (patient == null) {
			throw new IllegalStateException("Invoice without patient");
		}
		patientAddressType.setGender(patient.getGender().toString().toLowerCase());
		patientAddressType.setSex(patient.getGender().toString().toLowerCase());
		LocalDateTime dateOfBirth = patient.getDateOfBirth();
		if (dateOfBirth == null) {
			// make validator happy if we don't know the birthdate
			patientAddressType.setBirthdate(XMLExporterUtil.makeXMLDateTime(LocalDateTime.of(1, 1, 1, 0, 0)));
		} else {
			patientAddressType.setBirthdate(XMLExporterUtil.makeXMLDateTime(dateOfBirth));

		}
		patientAddressType.setPerson(getPerson(patient, false));
		patientAddressType.setSsn(getSSN(invoice));
		return patientAddressType;
	}

	protected InsuranceAddressType getInsurance(IInvoice invoice) {
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
		IContact costBearer = invoice.getCoverage().getCostBearer();
		if (costBearer == null) {
			costBearer = invoice.getCoverage().getPatient();
		}
		String kEAN = TarmedRequirements.getEAN(costBearer, EAN_PSEUDO);
		InsuranceAddressType insuranceAddressType = new InsuranceAddressType();

		if (tiersType == Tiers.GARANT) {
			if (costBearer.isOrganization()) {
				if (kEAN.matches("[0-9]{13,13}")) { //$NON-NLS-1$
					insuranceAddressType.setGln(kEAN);

					insuranceAddressType.setCompany(getCompany(costBearer, false));
				}
			}
		} else if (tiersType == Tiers.PAYANT) {
			insuranceAddressType.setGln(kEAN);
			insuranceAddressType.setCompany(getCompany(costBearer, false));
		}

		return insuranceAddressType.getGln() == null ? null : insuranceAddressType;
	}

	protected CompanyType getCompany(IContact contact, boolean useContactPostalAddress) {
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

	protected PersonType getPerson(IContact contact, boolean useContactPostalAddress) {
		if (contact.isPerson()) {
			return (PersonType) getCompanyOrPerson(contact, useContactPostalAddress);
		} else {
			throw new IllegalStateException("Contact is not a person");
		}
	}

	protected ProvidersAddressType getProvider(IInvoice invoice) {
		ProvidersAddressType providerAddressType = new ProvidersAddressType();

		IContact provider = invoice.getMandator();
		if (StringUtils.isNotBlank(ConfigServiceHolder.getGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER, null))) {
			Optional<IContact> fixProvider = CoreModelServiceHolder.get().load(
					ConfigServiceHolder.getGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER, null), IContact.class);
			if (fixProvider.isPresent()) {
				logger.info("Fixed provider [" + fixProvider.get().getLabel() + "] ean ["
						+ TarmedRequirements.getEAN(fixProvider.get(), EAN_PSEUDO) + "]");
				provider = fixProvider.get();
			}
		}

		ProviderGLNAddressType glnAddressType = new ProviderGLNAddressType();
		ZsrAddressType zsrAddressType = new ZsrAddressType();

		glnAddressType.setGln(TarmedRequirements.getEAN(provider, EAN_PSEUDO));
		if(provider instanceof IMandator) {
			IContact biller = ((IMandator) provider).getBiller();
			glnAddressType.setGlnLocation(TarmedRequirements.getEAN(biller, EAN_PSEUDO));
		} else {
			glnAddressType.setGlnLocation(TarmedRequirements.getEAN(provider, EAN_PSEUDO));
		}
		providerAddressType.setProviderGln(glnAddressType);
		String zsr = TarmedRequirements.getKSK(provider);
		if (StringUtils.isNotBlank(zsr)) {
			zsrAddressType.setZsr(zsr);
			providerAddressType.setProviderZsr(zsrAddressType);
		}
		Object companyOrPerson = getCompanyOrPerson(provider, false);
		if (companyOrPerson instanceof CompanyType) {
			glnAddressType.setCompany((CompanyType) companyOrPerson);
			zsrAddressType.setCompany((CompanyType) companyOrPerson);
		} else if (companyOrPerson instanceof PersonType) {
			glnAddressType.setPerson((PersonType) companyOrPerson);
			zsrAddressType.setPerson((PersonType) companyOrPerson);
		}
		return providerAddressType;
	}

	protected BillersAddressType getBiller(IInvoice invoice) {
		IContact biller = invoice.getMandator().getBiller();

		BillersAddressType billerAddressType = new BillersAddressType();
		BillerGLNAddressType glnAddressType = new BillerGLNAddressType();
		ZsrAddressType zsrAddressType = new ZsrAddressType();
		glnAddressType.setGln(TarmedRequirements.getEAN(biller, EAN_PSEUDO));
		billerAddressType.setBillerGln(glnAddressType);

		String zsr = TarmedRequirements.getKSK(biller);
		if (StringUtils.isNotBlank(zsr)) {
			zsrAddressType.setZsr(zsr);
			billerAddressType.setBillerZsr(zsrAddressType);
		}

		Object companyOrPerson = getCompanyOrPerson(biller, false);
		if (companyOrPerson instanceof CompanyType) {
			glnAddressType.setCompany((CompanyType) companyOrPerson);
			zsrAddressType.setCompany((CompanyType) companyOrPerson);
		} else if (companyOrPerson instanceof PersonType) {
			glnAddressType.setPerson((PersonType) companyOrPerson);
			zsrAddressType.setPerson((PersonType) companyOrPerson);
		}
		return billerAddressType;
	}

	protected Object getCompanyOrPerson(IContact contact, boolean useContactPostalAddress) {
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
					personType.setSalutation(StringUtils.abbreviate(postAnschrift.getSalutation(), 35));
				}
			} else {
				String salutation = (String) contact.getExtInfo(ExtInfoConstants.ANREDE);
				if (StringUtils.isNotBlank(salutation)) {
					personType.setSalutation(StringUtils.abbreviate(salutation, 35));
				}
				if (contact.isPerson()) {
					IPerson person = CoreModelServiceHolder.get().load(contact.getId(), IPerson.class).get();
					if (StringUtils.isNotBlank(person.getTitel())) {
						personType.setTitle(StringUtils.abbreviate(person.getTitel(), 35));
					}
					if (StringUtils.isBlank(salutation)
							&& StringUtils.isNotBlank(PersonFormatUtil.getSalutation(person))) {
						personType.setSalutation(StringUtils.abbreviate(PersonFormatUtil.getSalutation(person), 35));
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
		throw new IllegalStateException("Contact [" + contact.getLabel() + "] is no organization and no person");
	}

	protected OnlineAddressType getOnline(IContact contact) {
		OnlineAddressType onlineAddressType = new OnlineAddressType();

		if (StringUtils.isNotBlank(contact.getEmail())) {
			String email = XMLExporterUtil.getValidXMLString(StringUtils.left(contact.getEmail(), 70));
			if (!email.matches(".+@.+")) { //$NON-NLS-1$
				email = "mail@invalid.invalid"; //$NON-NLS-1$
			}
			onlineAddressType.getEmail().add(email);
		}

		if (StringUtils.isNotBlank(contact.getWebsite())) {
			String website = XMLExporterUtil.getValidXMLString(StringUtils.left(contact.getWebsite(), 100));

			onlineAddressType.getUri().add(website);
		}

		return onlineAddressType.getEmail().isEmpty() ? null : onlineAddressType;
	}

	protected TelecomAddressType getTelecom(IContact contact) {
		TelecomAddressType telecomAddressType = new TelecomAddressType();

		if (!contact.isMandator() && StringUtils.isNotBlank(contact.getMobile())) {
			telecomAddressType.getPhone().add(StringUtils.abbreviate(contact.getMobile(), 25));
		}
		if (StringUtils.isNotBlank(contact.getPhone1())) {
			telecomAddressType.getPhone().add(StringUtils.abbreviate(contact.getPhone1(), 25));
		}
		// only add the fax element if there is a phone, telcom without phone is not
		// allowed by xsd
		if (!telecomAddressType.getPhone().isEmpty()) {
			if (StringUtils.isNotBlank(contact.getFax())) {
				telecomAddressType.getPhone().add(StringUtils.abbreviate(contact.getFax(), 25));
			}
		}
		return telecomAddressType.getPhone().isEmpty() ? null : telecomAddressType;
	}

	protected PostalAddressType getPostalAddress(IContact contact) {
		PostalAddressType postalAddressType = new PostalAddressType();

		String pobox = (String) contact.getExtInfo("Postfach");
		if (StringUtils.isNotBlank(pobox)) {
			postalAddressType.setPobox(StringUtils.abbreviate(pobox, 35));
		}
		if (StringUtils.isNotBlank(contact.getStreet())) {
			StreetType street = new StreetType();
			street.setValue(StringUtils.abbreviate(contact.getStreet(), 35));
			postalAddressType.setStreet(street);
		}
		postalAddressType.setCity(StringUtils
				.abbreviate(StringUtils.defaultIfBlank(contact.getCity(), Messages.XMLExporter_Unknown), 35));
		String zip = StringUtils.defaultIfBlank(contact.getZip(), "0000");
		ZipType zipType = new ZipType();
		zipType.setValue(StringUtils.left(zip, 9));
		Country country = contact.getCountry();
		if (Country.NDF == country) {
			logger.info("IContact [] Country not set, defaulting to CH", contact.getId());
			country = Country.CH;
		}
		if (StringUtils.isNotBlank(country.toString())) {
			CountryType countryType = new CountryType();
			countryType.setCountryCode(StringUtils.left(country.toString(), 3));
			countryType.setValue(country.toString());
			postalAddressType.setCountry(countryType);
		}
		postalAddressType.setZip(zipType);

		return postalAddressType;
	}

	protected ServicesType getServices(IInvoice invoice) {
		ServicesType servicesType = new ServicesType();
		List<IEncounter> encounters = invoice.getEncounters();
		LocalDate lastEncounterDate = null;
		int session = 1;
		for (IEncounter encounter : encounters) {
			Optional<String> sectionCode = Optional.empty();
			IContact biller = encounter.getMandator().getBiller();
			if (biller.isOrganization()) {
				sectionCode = getSectionCode(encounter.getMandator());
			}

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

			try {
				for (IBilled billed : encounterBilled) {
					IBillable billable = billed.getBillable();
					if (billable == null) {
						logger.error(
								Messages.XMLExporter_ErroneusBill + invoice.getNumber() + " Null-Verrechenbar bei Kons " //$NON-NLS-1$
										+ encounter.getLabel());
						continue;
					}
					// tarmed or tardoc service
					if ("001".equals(billable.getCodeSystemCode()) || "007".equals(billable.getCodeSystemCode())) {
						ServiceExType serviceExType = new ServiceExType();

						String bezug = getBezug(billable);
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
						double secondaryScale = 1.0;
						if (!billed.isNonIntegerAmount()) {
							secondaryScale = billed.getSecondaryScaleFactor();
						}
						double mult = billed.getFactor();
						double tlAL = ArzttarifeUtil.getAL(billed);
						double tlTL = ArzttarifeUtil.getTL(billed);
						// build monetary values of this TarmedLeistung
						Money mAL = ArzttarifeUtil.getALMoney(billed);
						Money mTL = ArzttarifeUtil.getTLMoney(billed);
						Money mAmountLocal = billed.getTotal();

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
							List<IReasonForEncounter> rfes = XMLExporterUtil.getReasonsForEncounter(encounter);
							if (rfes.size() > 0) {
								StringBuilder sb = new StringBuilder();
								for (IReasonForEncounter rfe : rfes) {
									sb.append("551_").append(rfe.getCode()).append(StringUtils.SPACE); //$NON-NLS-1$
								}
								serviceExType.setRemark(sb.toString());
							}
							bRFE = true;
						}

						serviceExType.setTariffType(billable.getCodeSystemCode());
						serviceExType.setCode(billable.getCode());
						serviceExType.setQuantity(billed.getAmount());
						serviceExType.setSession(session);
						serviceExType.setName(billed.getText());
						serviceExType.setDateBegin(XMLExporterUtil.makeXMLDate(encounterDate));
						serviceExType.setProviderId(TarmedRequirements.getEAN(encounter.getMandator(), EAN_PSEUDO));
						serviceExType.setResponsibleId(XMLExporterUtil.getResponsibleEAN(encounter));

						sectionCode.ifPresent(c -> serviceExType.setSectionCode(c));

						servicesType.getServiceExOrService().add(serviceExType);
					} else { // any service
						ServiceType serviceType = new ServiceType();

						serviceType.setAmount(billed.getTotal().doubleValue());
						serviceType.setVatRate(getVatRate(billed));

						serviceType.setUnit(billed.getPrice().doubleValue());
						if ("581".equals(billable.getCodeSystemCode())) {
							serviceType.setUnit((double) billed.getPoints() / 100);
						}
						serviceType.setUnitFactor(billed.getFactor());

						serviceType.setTariffType(billable.getCodeSystemCode());
						if ("311".equals(billable.getCodeSystemCode())) {
							// change physio tariff type for KVG from 311 to 312
							if (encounter.getCoverage().getBillingSystem().getLaw() == BillingLaw.KVG) {
								serviceType.setTariffType("312");
							}
							serviceType.setUnit(billed.getPoints() / 100);
						}
						serviceType.setCode(billable.getCode());
						serviceType.setQuantity(billed.getAmount());
						serviceType.setSession(session);
						serviceType.setName(billed.getText());
						if (billable instanceof IArticle) {
							XtraDrugType drugType = new XtraDrugType();
							if ("true".equals(billed.getExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE))) {
								serviceType.setName(serviceType.getName() + " (Substitution nicht möglich)");
							}
							serviceType.setXtraDrug(drugType);
						}
						serviceType.setDateBegin(XMLExporterUtil.makeXMLDate(encounterDate));
						serviceType.setProviderId(TarmedRequirements.getEAN(encounter.getMandator(), EAN_PSEUDO));
						serviceType.setResponsibleId(XMLExporterUtil.getResponsibleEAN(encounter));

						// all 406 will have code 2000
						if ("406".equals(billable.getCodeSystemCode()) && !isCovid(billable)) {
							serviceType.setCode("2000");
							if ((billable instanceof IArticle
									&& ((IArticle) billable).getTyp() == ArticleTyp.EIGENARTIKEL)
									&& StringUtils.isBlank(getServiceCode(billed))
									&& StringUtils.isNotBlank(((IArticle) billable).getGtin())) {
								serviceType
										.setName(serviceType.getName() + " [" + ((IArticle) billable).getGtin() + "]");
							} else {
								serviceType.setName(serviceType.getName() + " [" + getServiceCode(billed) + "]");
							}
						}
						// custom article with 590 will have code 1310
						if ("590".equals(billable.getCodeSystemCode()) && (billable instanceof IArticle
								&& ((IArticle) billable).getTyp() == ArticleTyp.EIGENARTIKEL)) {
							serviceType.setCode("1310");
						}
						// all 410 will have code 1000
						if ("410".equals(billable.getCodeSystemCode()) && (billable instanceof IArticle
								&& ((IArticle) billable).getTyp() == ArticleTyp.EIGENARTIKEL)) {
							serviceType.setCode("1000");
						}

						servicesType.getServiceExOrService().add(serviceType);
					}
				}
			} catch (DatatypeConfigurationException e) {
				logger.error("Error creating tarmed services for invoice nr [" + invoice.getNumber() + "]", e);
				return null;
			}
		}
		if (servicesType.getServiceExOrService() != null) {
			servicesType.getServiceExOrService().sort((l, r) -> {
				LocalDate lDate = getServiceExOrServiceDate(l);
				LocalDate rDate = getServiceExOrServiceDate(r);
				int ret = lDate.compareTo(rDate);
				if (ret == 0) {
					Integer lTariff = getServiceExOrServiceTariffType(l);
					Integer rTariff = getServiceExOrServiceTariffType(r);
					ret = lTariff.compareTo(rTariff);
					if (ret == 0) {
						String lCode = getServiceExOrServiceCode(l);
						String rCode = getServiceExOrServiceCode(r);
						ret = lCode.compareTo(rCode);
					}
				}
				return ret;
			});
		}
		// set record_id of sorted services starting with 1
		List<Object> list = servicesType.getServiceExOrService();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof ServiceExType) {
				((ServiceExType) list.get(i)).setRecordId(i + 1);
			} else if (list.get(i) instanceof ServiceType) {
				((ServiceType) list.get(i)).setRecordId(i + 1);
			}
		}

		return servicesType;
	}

	private Optional<String> getSectionCode(IMandator mandator) {
		Optional<ICoding> configSectionCode = ArzttarifeUtil.getMandantSectionCode(mandator);
		if (configSectionCode.isPresent()) {
			return Optional.of(configSectionCode.get().getCode());
		}
		// perform lookup if not configured
		List<ICoding> specialistCodes = ArzttarifeUtil.getMandantTardocSepcialist(mandator);
		Optional<ICoding> specialistSectionCode = getSectionCodeForSpecialist(specialistCodes);
		if (specialistSectionCode.isPresent()) {
			return Optional.of(specialistSectionCode.get().getCode());
		}
		return Optional.empty();
	}

	private Optional<ICoding> getSectionCodeForSpecialist(List<ICoding> specialistCodes) {
		if (sectionCodeContribution == null) {
			sectionCodeContribution = (SectionCodeCodingContribution) OsgiServiceUtil
					.getService(ICodingContribution.class, "(system=forumdatenaustausch_sectioncode)").orElse(null);
		}
		if (sectionCodeContribution != null) {
			return sectionCodeContribution.getMappedBySpecialistCode(specialistCodes);
		} else {
			logger.warn("No section code coding contribution available");
		}
		return Optional.empty();
	}

	private String getBezug(IBillable billable) {
		if (billable instanceof ITarmedLeistung) {
			return (String) ((ITarmedLeistung) billable).getExtension().getExtInfo("Bezug"); //$NON-NLS-1$
		} else if (billable instanceof ITardocLeistung) {
			return (String) ((ITardocLeistung) billable).getExtension().getExtInfo("Bezug"); //$NON-NLS-1$
		}
		return StringUtils.EMPTY;
	}

	private String getServiceCode(IBilled billed) {
		String ret = billed.getCode();
		IBillable billable = billed.getBillable();
		if (billable instanceof ICustomService
				|| (billable instanceof IArticle && ((IArticle) billable).getTyp() == ArticleTyp.EIGENARTIKEL)) {
			if (billable.getId().equals(ret)) {
				ret = StringUtils.EMPTY;
			}
		}
		return ret;
	}

	private boolean isCovid(IBillable billable) {
		if (billable instanceof IArticle) {
			return ((IArticle) billable).getTyp() == ArticleTyp.EIGENARTIKEL
					&& ((IArticle) billable).getSubTyp() == ArticleSubTyp.COVID;
		}
		return false;
	}

	private String getServiceExOrServiceCode(Object obj) {
		if (obj instanceof ServiceType) {
			return ((ServiceType) obj).getCode();
		} else if (obj instanceof ServiceExType) {
			return ((ServiceExType) obj).getCode();
		}
		throw new IllegalArgumentException("Unknown type [" + obj.getClass().getName() + "]");
	}

	private Integer getServiceExOrServiceTariffType(Object obj) {
		if (obj instanceof ServiceType) {
			try {
				return Integer.parseInt(((ServiceType) obj).getTariffType());
			} catch (NumberFormatException e) {
				return Integer.MAX_VALUE;
			}
		} else if (obj instanceof ServiceExType) {
			try {
				return Integer.parseInt(((ServiceExType) obj).getTariffType());
			} catch (NumberFormatException e) {
				return Integer.MAX_VALUE;
			}
		}
		throw new IllegalArgumentException("Unknown type [" + obj.getClass().getName() + "]");
	}

	private LocalDate getServiceExOrServiceDate(Object obj) {
		if (obj instanceof ServiceType) {
			return XMLExporterUtil.getAsLocalDate(((ServiceType) obj).getDateBegin());
		} else if (obj instanceof ServiceExType) {
			return XMLExporterUtil.getAsLocalDate(((ServiceExType) obj).getDateBegin());
		}
		throw new IllegalArgumentException("Unknown type [" + obj.getClass().getName() + "]");
	}

	protected Boolean getObligation(IBilled billed, IBillable billable) {
		String tariffTyp = billable.getCodeSystemCode();
		if (billable instanceof ITarmedLeistung) {
			return ArzttarifeUtil.isObligation(billed);
		}
		if (billable instanceof IArticle) {
			if ("452".equals(tariffTyp)) {
				return true;
			}
			return ((IArticle) billable).isObligation();
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
		// pandemie
		if ("351".equals(tariffTyp)) {
			return true;
		}
		// psycho
		if ("581".equals(tariffTyp)) {
			return true;
		}
		return false;
	}

	protected Double getVatRate(IBilled billed) {
		Double ret = 0.0;

		String vatScale = (String) billed.getExtInfo(Constants.VAT_SCALE);
		if (vatScale != null && vatScale.length() > 0) {
			ret = Double.parseDouble(vatScale);
		}

		return ret;
	}

	protected PayloadType getPayload(IInvoice invoice, TYPE type) throws DatatypeConfigurationException {
		PayloadType payloadType = new PayloadType();

		payloadType.setRequestType("invoice");
		payloadType.setRequestSubtype("normal");
		payloadType.setInvoice(getInvoice(invoice));

		payloadType.setBody(getBody(invoice));
		if (type == TYPE.COPY) {
			payloadType.setRequestSubtype("copy");
		}
		if (type == TYPE.STORNO) {
			payloadType.setRequestSubtype("storno");
		}

		return payloadType;
	}

	protected InvoiceType getInvoice(IInvoice invoice) throws DatatypeConfigurationException {
		InvoiceType invoiceType = new InvoiceType();

		invoiceType.setRequestTimestamp((int) (new Date().getTime() / 1000));
		invoiceType.setRequestDate(XMLExporterUtil.makeXMLDate(invoice.getDate()));
		invoiceType.setRequestId(InvoiceServiceHolder.get().getCombinedId(invoice));

		return invoiceType;
	}

	protected BodyType getBody(IInvoice invoice) throws DatatypeConfigurationException {
		BodyType bodyType = new BodyType();

		bodyType.setRole("physician");
		bodyType.setPlace("practice");

		bodyType.setProlog(getProlog(invoice));
		// bodyType.setRemark(value);

		Object tiers = getTiers(invoice);
		if (tiers instanceof GarantType) {
			bodyType.setTiersGarant((GarantType) tiers);
		} else if (tiers instanceof PayantType) {
			bodyType.setTiersPayant((PayantType) tiers);
		}

		bodyType.setEsrQR(getEsrQR(invoice));

		bodyType.setLaw(getLawType(invoice));

		bodyType.setTreatment(getTreatment(invoice));

		bodyType.setServices(getServices(invoice));

		if (invoice.getAttachments() != null && !invoice.getAttachments().isEmpty()) {
			bodyType.setDocuments(getDocuments(invoice));
		}

		if (StringUtils.isNotBlank(invoice.getRemark())) {
			bodyType.setRemark(invoice.getRemark());
		}

		return bodyType;
	}

	private DocumentsType getDocuments(IInvoice invoice) {
		DocumentsType documentsType = new DocumentsType();
		for (IDocument attachment : invoice.getAttachments()) {
			String mimeType = attachment.getMimeType();
			if (mimeType == null || !mimeType.endsWith("pdf")) {
				logger.warn("Cannot add attachment [{}], mimeType is null or not pdf", attachment.getId());
				continue;
			}
			try {
				InputStream content = attachment.getContent();
				if (content != null) {
					byte[] byteArray = IOUtils.toByteArray(attachment.getContent());

					DocumentType document = new DocumentType();
					document.setDocumentType("UndefinedDoc");
					document.setFilename(attachment.getTitle());
					document.setMimeType("application/pdf");
					document.setBase64(byteArray);
					documentsType.getDocument().add(document);
				} else {
					logger.warn("Cannot add attachment [{}], content is null", attachment.getId());
				}

			} catch (IOException e) {
				logger.warn("Cannot add attachment [{}], cannot read content", attachment.getId(), e);
			}
		}
		documentsType.setNumber(new BigInteger(Integer.toString(documentsType.getDocument().size())));
		return documentsType.getDocument().isEmpty() ? null : documentsType;
	}

	protected TreatmentType getTreatment(IInvoice invoice) throws DatatypeConfigurationException {
		TreatmentType treatmentType = new TreatmentType();

		treatmentType.setDateBegin(XMLExporterUtil.makeXMLDate(invoice.getDateFrom()));
		treatmentType.setDateEnd(XMLExporterUtil.makeXMLDate(invoice.getDateTo()));
		treatmentType.setCanton((String) invoice.getMandator().getExtInfo(TarmedACL.getInstance().KANTON));
		treatmentType.setReason(getTreatmentReason(invoice.getCoverage()));

		if (FallConstants.TYPE_MATERNITY.equals(invoice.getCoverage().getReason())) {
			String gestationWeekString = (String) invoice.getCoverage()
					.getExtInfo(FallConstants.FLD_EXT_GESTATIONWEEK13);
			if (StringUtils.isNotBlank(gestationWeekString)) {
				treatmentType.setGestationWeek13(XMLExporterUtil.makeXMLDate(new TimeTool(gestationWeekString)));
			}
		}

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

	private String getTreatmentDiagnosisType(IDiagnosisReference invoiceDiagnosis) {
		String name = invoiceDiagnosis.getCodeSystemName();
		if (name != null) {
			if (name.equalsIgnoreCase("freetext")) { //$NON-NLS-1$
				return "freetext"; //$NON-NLS-1$
			}
			if (name.equalsIgnoreCase("ICD-10")) { //$NON-NLS-1$
				return "ICD"; //$NON-NLS-1$
			}
			if (name.equalsIgnoreCase("TI-Code")) { //$NON-NLS-1$
				return "cantonal"; //$NON-NLS-1$
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

	protected List<IDiagnosisReference> getInvoiceDiagnosis(IInvoice invoice) {
		HashSet<String> seen = new HashSet<>();
		ArrayList<IDiagnosisReference> ret = new ArrayList<IDiagnosisReference>();
		List<IEncounter> encounters = invoice.getEncounters();
		for (IEncounter encounter : encounters) {
			List<IDiagnosisReference> encounterDiagnosis = encounter.getDiagnoses();
			for (IDiagnosisReference encounterDiagnose : encounterDiagnosis) {
				String dgc = encounterDiagnose.getCode();
				if (dgc != null) {
					// each diag code and system only once
					if (seen.add(encounterDiagnose.getCode() + encounterDiagnose.getCodeSystemName())) {
						ret.add(encounterDiagnose);
					}
				}
			}
		}
		return ret;
	}

	private String getTreatmentReason(ICoverage coverage) {
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
		if (type.equalsIgnoreCase(FallConstants.TYPE_OTHER)) {
			return "unknown";
		}
		return XMLExporter.DISEASE;
	}

	protected LawType getLawType(IInvoice invoice) throws DatatypeConfigurationException {
		BillingLaw law = invoice.getCoverage().getBillingSystem().getLaw();

		TimeTool caseDate = null;
		if (StringUtils.isNotBlank((String) invoice.getCoverage().getExtInfo("Unfalldatum"))) {
			caseDate = new TimeTool((String) invoice.getCoverage().getExtInfo("Unfalldatum"));
		} else if (invoice.getDateFrom() != null) {
			caseDate = new TimeTool(invoice.getDateFrom());
		}

		LawType lawType = new LawType();
		lawType.setCaseId(getCaseNumber(invoice));
		lawType.setCaseDate(XMLExporterUtil.makeXMLDate(caseDate));
		lawType.setInsuredId(getInsuredId(invoice));

		if (law == BillingLaw.KVG) {
			lawType.setType(law.name());

		} else if (law == BillingLaw.UVG) {
			lawType.setType(law.name());
			String casenumber = getCaseNumber(invoice);
			if (StringTool.isNothing(casenumber)) {
				casenumber = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
						TarmedRequirements.ACCIDENT_NUMBER);
			}
			if (StringUtils.isNotBlank(casenumber)) {
				lawType.setCaseId(casenumber);
			}
		} else if (law == BillingLaw.IV) {
			lawType.setType("IVG");
		} else if (law == BillingLaw.MV) {
			lawType.setType("MVG");
		} else if (law == BillingLaw.VVG) {
			lawType.setType(law.name());
		} else if (law == BillingLaw.ORG) {
			lawType.setType(law.name());
		} else {
			throw new UnsupportedOperationException("Billing law [" + law + "] not implemented");
		}
		return lawType;
	}

	protected String getCaseNumber(IInvoice invoice) {
		String caseNumber = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
				TarmedRequirements.CASE_NUMBER);
		caseNumber = caseNumber.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		return StringUtils.isNotBlank(caseNumber) ? caseNumber : null;
	}

	protected String getInsuredId(IInvoice invoice) {
		String vnummer = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
				TarmedRequirements.INSURANCE_NUMBER);
		if (StringTool.isNothing(vnummer)) {
			vnummer = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(),
					TarmedRequirements.CASE_NUMBER);
		}
		return StringUtils.isNotBlank(vnummer) ? vnummer : null;
	}

	protected String getSSN(IInvoice invoice) {
		String ahv = TarmedRequirements.getAHV(invoice.getCoverage().getPatient()).replaceAll("[^0-9]", //$NON-NLS-1$
				StringConstants.EMPTY);
		if (ahv.length() == 0) {
			ahv = CoverageServiceHolder.get().getRequiredString(invoice.getCoverage(), TarmedRequirements.SSN)
					.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		}
		return StringUtils.isNotBlank(ahv) ? ahv : UNKNOWN_SSN;
	}

	protected EsrQRType getEsrQR(IInvoice invoice) throws DatatypeConfigurationException {
		EsrQRType esrQRType = new EsrQRType();

		String paymentPeriode = (String) invoice.getMandator().getBiller().getExtInfo("rnfrist"); //$NON-NLS-1$
		if (StringTool.isNothing(paymentPeriode)) {
			paymentPeriode = "30"; //$NON-NLS-1$
		}
		esrQRType.setPaymentPeriod(DatatypeFactory.newInstance().newDuration("P" + paymentPeriode + "D")); //$NON-NLS-1$ //$NON-NLS-2$

		String iban = (String) invoice.getMandator().getBiller().getExtInfo("IBAN");
		if (StringUtils.isEmpty(iban)) {
			Display.getDefault().syncExec(() -> {
				MessageDialog.openError(null, Messages.XMLExporter_MandatorErrorCaption,
						Messages.XMLExporter_MandatorErrorEsr + " [" + invoice.getMandator().getLabel() //$NON-NLS-1$
								+ "]"); //$NON-NLS-1$
			});
			return null;
		}
		esrQRType.setIban(iban);
		esrQRType.setReferenceNumber(getBesr(invoice).makeRefNr(false));
		String additionalInformation = (String) invoice.getMandator().getBiller()
				.getExtInfo(TarmedACL.getInstance().RNINFORMATION);
		if (StringUtils.isNotBlank(additionalInformation)) {
			esrQRType.setPaymentReason(StringUtils.abbreviate(additionalInformation, 140));
		}

		EsrAddressType esrAddressType = getEsrCreditor(invoice);
		esrQRType.setCreditor(esrAddressType);

		return esrQRType;
	}

	private EsrAddressType getEsrCreditor(IInvoice invoice) {
		IContact creditor = invoice.getMandator().getBiller();
		// update creditor if configured
		if (StringUtils.isNotBlank((String) creditor.getExtInfo(TarmedACL.getInstance().RNACCOUNTOWNER))) {
			Optional<IContact> loadedCreditor = CoreModelServiceHolder.get()
					.load((String) creditor.getExtInfo(TarmedACL.getInstance().RNACCOUNTOWNER), IContact.class);
			if (loadedCreditor.isPresent()) {
				creditor = loadedCreditor.get();
			}
		}
		EsrAddressType esrAddressType = new EsrAddressType();
		Object creditorCompanyOrPerson = getCompanyOrPerson(creditor, false);
		if (creditorCompanyOrPerson instanceof CompanyType) {
			esrAddressType.setCompany((CompanyType) creditorCompanyOrPerson);
		} else if (creditorCompanyOrPerson instanceof PersonType) {
			esrAddressType.setPerson((PersonType) creditorCompanyOrPerson);
		}
		return esrAddressType;
	}

	private ESR getBesr(IInvoice invoice) {
		if (besr == null) {
			besr = new ESR((String) invoice.getMandator().getBiller().getExtInfo(TarmedACL.getInstance().ESRNUMBER),
					(String) invoice.getMandator().getBiller().getExtInfo(TarmedACL.getInstance().ESRSUB),
					InvoiceServiceHolder.get().getCombinedId(invoice), ESR.ESR27);
		}
		return besr;
	}

	protected PrologType getProlog(IInvoice invoice) {
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

	private int getJavaVersion() {
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

	protected ProcessingType getProcessing(IInvoice invoice) throws DatatypeConfigurationException {
		ProcessingType processingType = new ProcessingType();

		processingType.setPrintCopyToGuarantor(CoverageServiceHolder.get().getCopyForPatient(invoice.getCoverage()));

		TransportType transportType = new TransportType();
		transportType.setFrom(TarmedRequirements.getEAN(invoice.getMandator(), EAN_PSEUDO));
		transportType.setTo(XMLExporterUtil.getRecipientEAN(invoice));

		logger.info("Using intermediate EAN [" + XMLExporterUtil.getIntermediateEAN(invoice) + "]");
		Via via = new Via();
		via.setVia(XMLExporterUtil.getIntermediateEAN(invoice));
		via.setSequenceId(1);
		transportType.getVia().add(via);

		// insert demand if TG and TC contract
		if (TarmedRequirements.hasTCContract(invoice.getMandator())) {
			String trustCenter = TarmedRequirements.getTCName(invoice.getMandator());
			if (StringUtils.isNotBlank(trustCenter)) {
				processingType.setSendCopyToTrustcenter(TrustCenters.getTCEAN(trustCenter));
				Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
				if(tiersType == Tiers.GARANT) {
					InstructionsType instructions = new InstructionsType();
					InstructionType instruction = new InstructionType();
					instruction.setToken("tx_print_to_guarantor");
					instruction.setValue("true");
					instructions.getInstruction().add(instruction);
					processingType.setInstructions(instructions);
				} else {
					InstructionsType instructions = new InstructionsType();
					InstructionType instruction = new InstructionType();
					instruction.setToken("tx_send_to_insurance");
					instruction.setValue("true");
					instructions.getInstruction().add(instruction);
					processingType.setInstructions(instructions);
				}
			}
		}
		processingType.setTransport(transportType);

		return processingType;
	}

	public void updateExistingXml(RequestType request, TYPE type, IInvoice invoice, XMLExporter xmlExporter) {
		try {
			besr = null;
			// update processing, print_at_intermediate and transport via EAN
			if (request.getProcessing() != null) {
				if (request.getProcessing().getTransport() != null) {
					String iEAN = XMLExporterProcessing.getIntermediateEAN(invoice, xmlExporter);
					List<Via> via = request.getProcessing().getTransport().getVia();
					if (via != null && !via.isEmpty()) {
						via.get(0).setVia(iEAN);
					}
				}
				// if TC contract make sure print at intermediate is true
				if (TarmedRequirements.hasTCContract(invoice.getMandator())) {
					String trustCenter = TarmedRequirements.getTCName(invoice.getMandator());
					if (StringUtils.isNotBlank(trustCenter)) {
						request.getProcessing().setSendCopyToTrustcenter(TrustCenters.getTCEAN(trustCenter));
						// reset tx instructions
						Tiers tiersType = CoverageServiceHolder.get().getTiersType(invoice.getCoverage());
						if (tiersType == Tiers.GARANT) {
							InstructionsType instructions = new InstructionsType();
							InstructionType instruction = new InstructionType();
							instruction.setToken("tx_print_to_guarantor");
							instruction.setValue("true");
							instructions.getInstruction().add(instruction);
							request.getProcessing().setInstructions(instructions);
						} else {
							InstructionsType instructions = new InstructionsType();
							InstructionType instruction = new InstructionType();
							instruction.setToken("tx_send_to_insurance");
							instruction.setValue("true");
							instructions.getInstruction().add(instruction);
							request.getProcessing().setInstructions(instructions);
						}
					}
				}
				// no copy for patient for reminders
				if (request.getPayload().getReminder() != null) {
					request.getProcessing().setPrintCopyToGuarantor(false);
				} else {
					request.getProcessing().setPrintCopyToGuarantor(
							CoverageServiceHolder.get().getCopyForPatient(invoice.getCoverage()));
				}
			}

			// update payload and balance
			if (request.getPayload() != null && request.getPayload().getBody() != null) {
				// do not update esr, balance and contacts on storno
				if (type != TYPE.STORNO) {
					// update treatment
					TreatmentType updateTreatment = getTreatment(invoice);
					if (updateTreatment != null) {
						request.getPayload().getBody().setTreatment(updateTreatment);
					}
					if (request.getPayload().getBody().getTiersGarant() != null
							&& invoice.getCoverage().getPatient() != null) {
						// TG contacts
						// update patient information
						PatientAddressType updatePatient = getPatient(invoice);
						if (updatePatient != null) {
							request.getPayload().getBody().getTiersGarant().setPatient(updatePatient);
						}
						// update guarantor information
						GuarantorAddressType updateGuarantor = getGuarantor(invoice);
						if (updateGuarantor != null) {
							request.getPayload().getBody().getTiersGarant().setGuarantor(updateGuarantor);
						}
						// update debitor information
						DebitorAddressType updateDebitor = getDebitor(invoice);
						if (updateDebitor != null) {
							request.getPayload().getBody().getTiersGarant().setDebitor(updateDebitor);
						}
						// update insurance information
						InsuranceAddressType updateInsurance = getInsurance(invoice);
						if (updateInsurance != null) {
							request.getPayload().getBody().getTiersGarant().setInsurance(updateInsurance);
						}
						// update provider information
						ProvidersAddressType updateProvider = getProvider(invoice);
						if (updateProvider != null) {
							request.getPayload().getBody().getTiersGarant().setProviders(updateProvider);
						}
						// update biller information
						BillersAddressType updateBiller = getBiller(invoice);
						if (updateBiller != null) {
							request.getPayload().getBody().getTiersGarant().setBillers(updateBiller);
						}
						// update creditor information
						EsrAddressType creditor = getEsrCreditor(invoice);
						if (creditor != null) {
							if (request.getPayload().getBody().getEsrQR() != null) {
								request.getPayload().getBody().getEsrQR().setCreditor(creditor);
							}
						}
					} else if (request.getPayload().getBody().getTiersPayant() != null
							&& invoice.getCoverage().getPatient() != null) {
						// TP contacts
						// update patient information
						PatientAddressType updatePatient = getPatient(invoice);
						if (updatePatient != null) {
							request.getPayload().getBody().getTiersPayant().setPatient(updatePatient);
						}
						// update guarantor information
						GuarantorAddressType updateGuarantor = getGuarantor(invoice);
						if (updateGuarantor != null) {
							request.getPayload().getBody().getTiersPayant().setGuarantor(updateGuarantor);
						}
						// update debitor information
						DebitorAddressType updateDebitor = getDebitor(invoice);
						if (updateDebitor != null) {
							request.getPayload().getBody().getTiersPayant().setDebitor(updateDebitor);
						}
						// update insurance information
						InsuranceAddressType updateInsurance = getInsurance(invoice);
						if (updateInsurance != null) {
							request.getPayload().getBody().getTiersPayant().setInsurance(updateInsurance);
						}
						// update provider information
						ProvidersAddressType updateProvider = getProvider(invoice);
						if (updateProvider != null) {
							request.getPayload().getBody().getTiersPayant().setProviders(updateProvider);
						}
						// update biller information
						BillersAddressType updateBiller = getBiller(invoice);
						if (updateBiller != null) {
							request.getPayload().getBody().getTiersPayant().setBillers(updateBiller);
						}
						// update creditor information
						EsrAddressType creditor = getEsrCreditor(invoice);
						if (creditor != null) {
							if (request.getPayload().getBody().getEsrQR() != null) {
								request.getPayload().getBody().getEsrQR().setCreditor(creditor);
							}
						}
					}

					Object updateBalance = getBalance(invoice);
					if (request.getPayload().getBody().getTiersGarant() != null
							&& updateBalance instanceof BalanceTGType) {
						BalanceTGType balance = request.getPayload().getBody().getTiersGarant().getBalance();
						if (((BalanceTGType) updateBalance).getAmountPrepaid() != balance.getAmountPrepaid()) {
							request.getPayload().getBody().getTiersGarant().setBalance((BalanceTGType) updateBalance);
						}
					} else if (request.getPayload().getBody().getTiersPayant() != null
							&& updateBalance instanceof BalanceTPType) {
						BalanceTPType balance = request.getPayload().getBody().getTiersPayant().getBalance();
						if (((BalanceTPType) updateBalance).getAmountDue() != balance.getAmountDue()) {
							request.getPayload().getBody().getTiersPayant().setBalance((BalanceTPType) updateBalance);
						}
					}
				}
				// always update copy information
				if (type == TYPE.COPY) {
					request.getPayload().setRequestSubtype("copy");
				}
				if (type == TYPE.STORNO) {
					request.getPayload().setRequestSubtype("storno");
				}
				if (type == TYPE.ORIG) {
					request.getPayload().setRequestSubtype("normal");
				}
				// always update remark
				if (StringUtils.isNotBlank(invoice.getRemark())) {
					request.getPayload().getBody().setRemark(invoice.getRemark());
				} else {
					request.getPayload().getBody().setRemark(null);
				}
				// update balance for storno
				if (type.equals(TYPE.STORNO)) {
					request.getProcessing().setPrintCopyToGuarantor(false);
					negate(request.getPayload().getBody().getServices());
					if (request.getPayload().getBody().getTiersGarant() != null) {
						BalanceTGType balance = request.getPayload().getBody().getTiersGarant().getBalance();
						balance.setAmount(-Math.abs(balance.getAmount()));
						balance.setAmountDue(0.0);
						balance.setAmountPrepaid(0.0);
						balance.getVat().setVat(0.0);
						balance.getVat().getVatRate().forEach(vr -> {
							vr.setAmount(-Math.abs(vr.getAmount()));
							vr.setVat(-Math.abs(vr.getVat()));
						});
					} else if (request.getPayload().getBody().getTiersPayant() != null) {
						BalanceTPType balance = request.getPayload().getBody().getTiersPayant().getBalance();
						balance.setAmount(-Math.abs(balance.getAmount()));
						balance.setAmountDue(0.0);
						balance.getVat().setVat(0.0);
						balance.getVat().getVatRate().forEach(vr -> {
							vr.setAmount(-Math.abs(vr.getAmount()));
							vr.setVat(-Math.abs(vr.getVat()));
						});
					}
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error updating tarmed xml model", e);
		}
	}

	public void addReminderEntry(RequestType request, IInvoice invoice, String reminderLevel) {
		if (request.getPayload() != null) {
			try {
				// no copy for patient for reminders
				if (request.getProcessing() != null) {
					request.getProcessing().setPrintCopyToGuarantor(false);
				}
				GregorianCalendar now = new GregorianCalendar();

				ReminderType reminderType = request.getPayload().getReminder();
				if (reminderType == null) {
					reminderType = new ReminderType();
					request.getPayload().setReminder(reminderType);
					request.getPayload().setRequestType("reminder");
				}
				reminderType.setRequestId(InvoiceServiceHolder.get().getCombinedId(invoice));
				reminderType.setReminderLevel(reminderLevel);
				reminderType.setRequestDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(now));
				reminderType.setRequestTimestamp((int) (now.getTimeInMillis() / 1000));

				// add amount reminder and recalculate amount due
				if (request.getPayload().getBody() != null) {
					Money mReminder = invoice.getDemandAmount();
					Money mDue = new Money(invoice.getTotalAmount());
					mDue.addMoney(mReminder);
					mDue.subtractMoney(invoice.getPayedAmount());
					if (request.getPayload().getBody().getTiersGarant() != null) {
						BalanceTGType balance = request.getPayload().getBody().getTiersGarant().getBalance();
						balance.setAmountReminder(mReminder.doubleValue());
						balance.setAmountDue(mDue.doubleValue());
					} else if (request.getPayload().getBody().getTiersPayant() != null) {
						BalanceTPType balance = request.getPayload().getBody().getTiersPayant().getBalance();
						balance.setAmountReminder(mReminder.doubleValue());
						balance.setAmountDue(mDue.doubleValue());
					}
				}
			} catch (DatatypeConfigurationException e) {
				LoggerFactory.getLogger(getClass()).error("Error adding reminder to xml model", e);
			}
		}
	}

	public void removeReminderEntry(RequestType request, IInvoice invoice) {
		if (request.getPayload() != null && request.getPayload().getReminder() != null) {
			request.getPayload().setReminder(null);
			request.getPayload().setRequestType("invoice");
			// reset copy for patient
			request.getProcessing()
					.setPrintCopyToGuarantor(CoverageServiceHolder.get().getCopyForPatient(invoice.getCoverage()));
		}
	}

	public boolean isReminder(RequestType request) {
		return request.getPayload() != null && (request.getPayload().getReminder() != null
				|| "reminder".equalsIgnoreCase(request.getPayload().getRequestType()));
	}

	private void negate(ServicesType services) {
		if (services != null) {
			services.getServiceExOrService().forEach(s -> {
				if (s instanceof ServiceType) {
					((ServiceType) s).setQuantity(-Math.abs(((ServiceType) s).getQuantity()));
					((ServiceType) s).setAmount(-Math.abs(((ServiceType) s).getAmount()));
				} else if (s instanceof ServiceExType) {
					((ServiceExType) s).setQuantity(-Math.abs(((ServiceExType) s).getQuantity()));
					((ServiceExType) s).setAmount(-Math.abs(((ServiceExType) s).getAmount()));
					((ServiceExType) s).setAmountMt(-Math.abs(((ServiceExType) s).getAmountMt()));
					((ServiceExType) s).setAmountTt(-Math.abs(((ServiceExType) s).getAmountTt()));
				}
			});
		}
	}

	/**
	 * If set to true, and {@link BillingLaw} is KVG and is copy to patient and is
	 * no electronic delivery, the telcom and online information of patient debitor
	 * and guarantor is removed. This leads to no electronic delivery.
	 * 
	 * @param value
	 */
	public void setUpdateElectronicDelivery(boolean value) {
		this.updateElectronicDelivery = value;
	}

	private void updateElectronicDelivery(IInvoice invoice, RequestType invoiceRequest) {
		BillingLaw law = invoice.getCoverage().getBillingSystem().getLaw();
		if (law == BillingLaw.KVG && isCopyToPatient(invoiceRequest) && isNoElectronicDelivery(invoice)) {
			// remove telcom and email if no electronic delivery should be performed
			if (invoiceRequest.getPayload().getBody().getTiersGarant() != null) {
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getDebitor() != null) {
					DebitorAddressType debitor = invoiceRequest.getPayload().getBody().getTiersGarant().getDebitor();
					setTelcomNull(debitor);
					setOnlineNull(debitor);
				}
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getPatient() != null) {
					PatientAddressType patient = invoiceRequest.getPayload().getBody().getTiersGarant().getPatient();
					setTelcomNull(patient);
					setOnlineNull(patient);
				}
				if (invoiceRequest.getPayload().getBody().getTiersGarant().getGuarantor() != null) {
					GuarantorAddressType guarantor = invoiceRequest.getPayload().getBody().getTiersGarant()
							.getGuarantor();
					setTelcomNull(guarantor);
					setOnlineNull(guarantor);
				}
			} else if (invoiceRequest.getPayload().getBody().getTiersPayant() != null) {
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getDebitor() != null) {
					DebitorAddressType debitor = invoiceRequest.getPayload().getBody().getTiersPayant().getDebitor();
					setTelcomNull(debitor);
					setOnlineNull(debitor);
				}
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getPatient() != null) {
					PatientAddressType patient = invoiceRequest.getPayload().getBody().getTiersPayant().getPatient();
					setTelcomNull(patient);
					setOnlineNull(patient);
				}
				if (invoiceRequest.getPayload().getBody().getTiersPayant().getGuarantor() != null) {
					GuarantorAddressType guarantor = invoiceRequest.getPayload().getBody().getTiersPayant()
							.getGuarantor();
					setTelcomNull(guarantor);
					setOnlineNull(guarantor);
				}
			}
		}
	}

	private void setOnlineNull(GuarantorAddressType guarantor) {
		if (guarantor.getPerson() != null) {
			guarantor.getPerson().setOnline(null);
		}
	}

	private void setTelcomNull(GuarantorAddressType guarantor) {
		if (guarantor.getPerson() != null) {
			guarantor.getPerson().setTelecom(null);
		}
	}

	private void setOnlineNull(DebitorAddressType debitor) {
		if (debitor.getCompany() != null) {
			debitor.getCompany().setOnline(null);
		} else if (debitor.getPerson() != null) {
			debitor.getPerson().setOnline(null);
		}
	}

	private void setTelcomNull(DebitorAddressType debitor) {
		if (debitor.getCompany() != null) {
			debitor.getCompany().setTelecom(null);
		} else if (debitor.getPerson() != null) {
			debitor.getPerson().setTelecom(null);
		}
	}

	private void setOnlineNull(PatientAddressType patient) {
		if (patient.getPerson() != null) {
			patient.getPerson().setOnline(null);
		}
	}

	private void setTelcomNull(PatientAddressType patient) {
		if (patient.getPerson() != null) {
			patient.getPerson().setTelecom(null);
		}
	}

	private boolean isCopyToPatient(ch.fd.invoice500.request.RequestType invoiceRequest) {
		if (invoiceRequest.getProcessing() != null) {
			return invoiceRequest.getProcessing().isPrintCopyToGuarantor();
		}
		return false;
	}

	private boolean isNoElectronicDelivery(IInvoice invoice) {
		if (invoice.getCoverage() != null) {
			return StringConstants.ONE
					.equals(invoice.getCoverage().getExtInfo(FallConstants.FLD_EXT_NO_ELECTRONIC_DELIVERY));
		}
		return false;
	}
}
