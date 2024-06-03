package ch.framsteg.elexis.covercard.dao;

public class CardInfoData {

	private final static String DEFAULT = "--";

	private String cardNoVeka;
	private String zSRNo;
	private String qualifyingData;
	private String queryNumber;
	private String validCard;
	private String codProv;

	private CHBaseInformation chBaseInformation;
	private IdentificationData identificationData;
	private Name name;
	private DateOfBirth dateOfBirth;
	private AdministrativeData administrativeData;
	private NationalExtension nationalExtension;
	private MailAddress mailAddress;
	private KVGInformation kvgInformation;
	private KVGCanton kvgCanton;
	private InsurerInformation insurerInformation;
	private BillingAddress billingAddress;
	private VVGInformation vvgInformation;
	private VVGInsurerInformation vvgInsurerInformation;
	private OfacExtension ofacExtension;
	private AdditionalKVGModel additionalKVGModel;
	private DebNomAddress debNomAddress;
	private DebmailAddress debmailAddress;
	private AgenceKVGAddress agenceKVGAddress;
	private AgenceVVGAddress agenceVVGAddress;

	private ContactNumberGerman contactNumberGermanKVG;
	private ContactNumberGerman contactNumberGermanVVG;

	public CardInfoData() {
		init();
	}

	private void init() {
		setCardNoVeka(DEFAULT);
		setzSRNo(DEFAULT);
		setQualifyingData(DEFAULT);
		setQueryNumber(DEFAULT);
		setValidCard(DEFAULT);
		setCodProv(DEFAULT);
	}

	public class AdditionalKVGModel {
		private String code;

		public AdditionalKVGModel() {
			init();
		}

		private void init() {
			setCode(DEFAULT);
		}

		public String getCode() {
			return (code != null) ? code : "";
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("AdditionalKVGModel");
			sb.append("\n");
			sb.append("code: " + code);
			return sb.toString();
		}
	}

	public class AdministrativeData {
		private String issuingStateIdNumber;
		private String nameOfTheInstitution;
		private String identificationNumberOfTheInstitution;
		private String coverCardNo;
		private String insuredPersonNumber;
		private String insuredNumber;
		private String expiryDate;

		public AdministrativeData() {
			init();
		}

		private void init() {
			setIssuingStateIdNumber(DEFAULT);
			setNameOfTheInstitution(DEFAULT);
			setIdentificationNumberOfTheInstitution(DEFAULT);
			setCoverCardNo(DEFAULT);
			setInsuredPersonNumber(DEFAULT);
			setInsuredNumber(DEFAULT);
			setExpiryDate(DEFAULT);
		}

		public String getIssuingStateIdNumber() {
			return (issuingStateIdNumber != null) ? issuingStateIdNumber : "";
		}

		public void setIssuingStateIdNumber(String issuingStateIdNumber) {
			this.issuingStateIdNumber = issuingStateIdNumber;
		}

		public String getNameOfTheInstitution() {
			return (nameOfTheInstitution != null) ? nameOfTheInstitution : "";
		}

		public void setNameOfTheInstitution(String nameOfTheInstitution) {
			this.nameOfTheInstitution = nameOfTheInstitution;
		}

		public String getIdentificationNumberOfTheInstitution() {
			return (identificationNumberOfTheInstitution != null) ? identificationNumberOfTheInstitution : "";
		}

		public void setIdentificationNumberOfTheInstitution(String identificationNumberOfTheInstitution) {
			this.identificationNumberOfTheInstitution = identificationNumberOfTheInstitution;
		}

		public String getCoverCardNo() {
			return (coverCardNo != null) ? coverCardNo : "";
		}

		public void setCoverCardNo(String coverCardNo) {
			this.coverCardNo = coverCardNo;
		}

		public String getInsuredPersonNumber() {
			return (insuredPersonNumber != null) ? insuredPersonNumber : "";
		}

		public void setInsuredPersonNumber(String insuredPersonNumber) {
			this.insuredPersonNumber = insuredPersonNumber;
		}

		public String getInsuredNumber() {
			return (insuredNumber != null) ? insuredNumber : "";
		}

		public void setInsuredNumber(String insuredNumber) {
			this.insuredNumber = insuredNumber;
		}

		public String getExpiryDate() {
			return (expiryDate != null) ? expiryDate : "";
		}

		public void setExpiryDate(String expiryDate) {
			this.expiryDate = expiryDate;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("AdministrativeData");
			sb.append("\n");
			sb.append("issuingStateIdNumber: " + issuingStateIdNumber);
			sb.append("\n");
			sb.append("nameOfTheInstitution: " + nameOfTheInstitution);
			sb.append("\n");
			sb.append("identificationNumberOfTheInstitution: " + identificationNumberOfTheInstitution);
			sb.append("\n");
			sb.append("coverCardNo: " + coverCardNo);
			sb.append("\n");
			sb.append("insuredPersonNumber: " + insuredPersonNumber);
			sb.append("\n");
			sb.append("insuredNumber: " + insuredNumber);
			sb.append("\n");
			sb.append("expiryDate: " + expiryDate);
			return sb.toString();
		}
	}

	public class AgenceKVGAddress {
		private String addressLine1;
		private String town;
		private String swissZipCode;
		private String country;

		public AgenceKVGAddress() {
			init();
		}

		private void init() {
			setAddressLine1(DEFAULT);
			setTown(DEFAULT);
			setSwissZipCode(DEFAULT);
			setCountry(DEFAULT);
		}

		public String getAddressLine1() {
			return (addressLine1 != null) ? addressLine1 : "";
		}

		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}

		public String getTown() {
			return (town != null) ? town : "";
		}

		public void setTown(String town) {
			this.town = town;
		}

		public String getSwissZipCode() {
			return (swissZipCode != null) ? swissZipCode : "";
		}

		public void setSwissZipCode(String swissZipCode) {
			this.swissZipCode = swissZipCode;
		}

		public String getCountry() {
			return (country != null) ? country : "";
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("AgenceKVGAddress");
			sb.append("\n");
			sb.append("addressLine1: " + addressLine1);
			sb.append("\n");
			sb.append("town: " + town);
			sb.append("\n");
			sb.append("swissZipCode: " + swissZipCode);
			sb.append("\n");
			sb.append("country: " + country);
			return sb.toString();
		}
	}

	public class AgenceVVGAddress {
		private String addressLine1;
		private String town;
		private String swissZipCode;
		private String country;

		public AgenceVVGAddress() {
			init();
		}

		private void init() {
			setAddressLine1(DEFAULT);
			setTown(DEFAULT);
			setSwissZipCode(DEFAULT);
			setCountry(DEFAULT);
		}

		public String getAddressLine1() {
			return (addressLine1 != null) ? addressLine1 : "";
		}

		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}

		public String getTown() {
			return (town != null) ? town : "";
		}

		public void setTown(String town) {
			this.town = town;
		}

		public String getSwissZipCode() {
			return (swissZipCode != null) ? swissZipCode : "";
		}

		public void setSwissZipCode(String swissZipCode) {
			this.swissZipCode = swissZipCode;
		}

		public String getCountry() {
			return (country != null) ? country : "";
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("AgenceVVGAddress");
			sb.append("\n");
			sb.append("addressLine1: " + addressLine1);
			sb.append("\n");
			sb.append("town: " + town);
			sb.append("\n");
			sb.append("swissZipCode: " + swissZipCode);
			sb.append("\n");
			sb.append("country: " + country);
			return sb.toString();
		}
	}

	public class BillingAddress {
		private String addressLine1;
		private String town;
		private String swissZipCode;
		private String country;

		public BillingAddress() {
			init();
		}

		public void init() {
			setAddressLine1(DEFAULT);
			setTown(DEFAULT);
			setSwissZipCode(DEFAULT);
			setCountry(DEFAULT);
		}

		public String getAddressLine1() {
			return (addressLine1 != null) ? addressLine1 : "";
		}

		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}

		public String getTown() {
			return (town != null) ? town : "";
		}

		public void setTown(String town) {
			this.town = town;
		}

		public String getSwissZipCode() {
			return (swissZipCode != null) ? swissZipCode : "";
		}

		public void setSwissZipCode(String swissZipCode) {
			this.swissZipCode = swissZipCode;
		}

		public String getCountry() {
			return (country != null) ? country : "";
		}

		public void setCountry(String country) {

			this.country = country;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("BillingAddress");
			sb.append("\n");
			sb.append("addressLine1: " + addressLine1);
			sb.append("\n");
			sb.append("town: " + town);
			sb.append("\n");
			sb.append("swissZipCode: " + swissZipCode);
			sb.append("\n");
			sb.append("country: " + country);
			return sb.toString();
		}
	}

	public class CHBaseInformation {
		private IdentificationData identificationData;
		private AdministrativeData administrativeData;
		private NationalExtension nationalExtension;
		private OfacExtension ofacExtension;

		public IdentificationData getIdentificationData() {
			return (identificationData != null) ? identificationData : new IdentificationData();
		}

		public void setIdentificationData(IdentificationData identificationData) {
			this.identificationData = identificationData;
		}

		public AdministrativeData getAdministrativeData() {
			return (administrativeData != null) ? administrativeData : new AdministrativeData();
		}

		public void setAdministrativeData(AdministrativeData administrativeData) {
			this.administrativeData = administrativeData;
		}

		public NationalExtension getNationalExtension() {
			return (nationalExtension != null) ? nationalExtension : new NationalExtension();
		}

		public void setNationalExtension(NationalExtension nationalExtension) {
			this.nationalExtension = nationalExtension;
		}

		public OfacExtension getOfacExtension() {
			return (ofacExtension != null) ? ofacExtension : new OfacExtension();
		}

		public void setOfacExtension(OfacExtension ofacExtension) {
			this.ofacExtension = ofacExtension;
		}
	}

	public class ContactNumberGerman {
		private String number;
		private String localCode;
		private String internationalCode;

		public ContactNumberGerman() {
			init();
		}

		private void init() {
			setNumber(DEFAULT);
			setLocalCode(DEFAULT);
			setInternationalCode(DEFAULT);
		}

		public String getNumber() {
			return (number != null) ? number : "";
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getLocalCode() {
			return (localCode != null) ? localCode : "";
		}

		public void setLocalCode(String localCode) {
			this.localCode = localCode;
		}

		public String getInternationalCode() {
			return (internationalCode != null) ? internationalCode : "";
		}

		public void setInternationalCode(String internationalCode) {
			this.internationalCode = internationalCode;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("ContactNumberGerman");
			sb.append("\n");
			sb.append("number: " + number);
			sb.append("\n");
			sb.append("localCode: " + localCode);
			sb.append("\n");
			sb.append("internationalCode: " + internationalCode);
			return sb.toString();
		}
	}

	public class DateOfBirth {
		private String yearMonthDay;

		public DateOfBirth() {
			init();
		}

		private void init() {
			setYearMonthDay(DEFAULT);
		}

		public String getYearMonthDay() {
			return (yearMonthDay != null) ? yearMonthDay : "";
		}

		public void setYearMonthDay(String yearMonthDay) {
			this.yearMonthDay = yearMonthDay;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("DateOfBirth");
			sb.append("\n");
			sb.append("yearMonthDay: " + yearMonthDay);
			sb.append("\n");
			return sb.toString();
		}

	}

	public class DebmailAddress {
		private String addressLine1;
		private String town;
		private String swissZipCode;
		private String country;

		public DebmailAddress() {
			init();
		}

		private void init() {
			setAddressLine1(DEFAULT);
			setTown(DEFAULT);
			setSwissZipCode(DEFAULT);
			setCountry(DEFAULT);
		}

		public String getAddressLine1() {
			return (addressLine1 != null) ? addressLine1 : "";
		}

		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}

		public String getTown() {
			return (town != null) ? town : "";
		}

		public void setTown(String town) {
			this.town = town;
		}

		public String getSwissZipCode() {
			return (swissZipCode != null) ? swissZipCode : "";
		}

		public void setSwissZipCode(String swissZipCode) {
			this.swissZipCode = swissZipCode;
		}

		public String getCountry() {
			return (country != null) ? country : "";
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("DebmailAddress");
			sb.append("\n");
			sb.append("addressLine1: " + addressLine1);
			sb.append("\n");
			sb.append("town: " + town);
			sb.append("\n");
			sb.append("swissZipCode: " + swissZipCode);
			sb.append("\n");
			sb.append("country: " + country);
			return sb.toString();
		}
	}

	public class DebNomAddress {
		private String officialName;
		private String firstName;

		public DebNomAddress() {
			init();
		}

		private void init() {
			setOfficialName(DEFAULT);
			setFirstName(DEFAULT);
		}

		public String getOfficialName() {
			return (officialName != null) ? officialName : "";
		}

		public void setOfficialName(String officialName) {
			this.officialName = officialName;
		}

		public String getFirstName() {
			return (firstName != null) ? firstName : "";
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("DebNomAddress");
			sb.append("\n");
			sb.append("officialName: " + officialName);
			sb.append("\n");
			sb.append("firstName: " + firstName);
			return sb.toString();
		}

	}

	public class IdentificationData {
		private String cardholderIdentifier;
		private String sex;
		private Name name;
		private DateOfBirth dateOfBirth;

		public IdentificationData() {
			init();
		}

		private void init() {
			setCardholderIdentifier(DEFAULT);
			setSex(DEFAULT);
		}

		public String getCardholderIdentifier() {
			return (cardholderIdentifier != null) ? cardholderIdentifier : "";
		}

		public void setCardholderIdentifier(String cardholderIdentifier) {
			this.cardholderIdentifier = cardholderIdentifier;
		}

		public String getSex() {
			return (sex != null) ? sex : "";
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public Name getName() {
			return (name != null) ? name : new Name();
		}

		public void setName(Name name) {
			this.name = name;
		}

		public DateOfBirth getDateOfBirth() {
			return (dateOfBirth != null) ? dateOfBirth : new DateOfBirth();
		}

		public void setDateOfBirth(DateOfBirth dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("IdentificationData");
			sb.append("\n");
			sb.append("cardholderIdentifier: " + cardholderIdentifier);
			sb.append("\n");
			sb.append("sex: " + sex);
			sb.append("\n");
			sb.append("name: " + name);
			sb.append("\n");
			sb.append("dateOfBirth: " + dateOfBirth);
			return sb.toString();
		}
	}

	public class InsurerInformation {
		private ContactNumberGerman contactNumberGerman;
		private String contactEmailAddress;
		private String contactEanNumber;
		private BillingAddress billingAddress;

		public InsurerInformation() {
			init();
		}

		private void init() {
			setContactEmailAddress(DEFAULT);
			setContactEanNumber(DEFAULT);
		}

		public ContactNumberGerman getContactNumberGerman() {
			return (contactNumberGerman != null) ? contactNumberGerman : new ContactNumberGerman();
		}

		public void setContactNumberGerman(ContactNumberGerman contactNumberGerman) {
			this.contactNumberGerman = contactNumberGerman;
		}

		public String getContactEmailAddress() {
			return (contactEmailAddress != null) ? contactEmailAddress : "";
		}

		public void setContactEmailAddress(String contactEmailAddress) {
			this.contactEmailAddress = contactEmailAddress;
		}

		public String getContactEanNumber() {
			return (contactEanNumber != null) ? contactEanNumber : "";
		}

		public void setContactEanNumber(String contactEanNumber) {
			this.contactEanNumber = contactEanNumber;
		}

		public BillingAddress getBillingAddress() {
			return (billingAddress != null) ? billingAddress : new BillingAddress();
		}

		public void setBillingAddress(BillingAddress billingAddress) {
			this.billingAddress = billingAddress;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("InsurerInformation");
			sb.append("\n");
			sb.append("contactNumberGerman: " + contactNumberGerman);
			sb.append("\n");
			sb.append("contactEmailAddress: " + contactEmailAddress);
			sb.append("\n");
			sb.append("contactEanNumber: " + contactEanNumber);
			sb.append("\n");
			sb.append("billingAddress: " + billingAddress);
			return sb.toString();
		}
	}

	public class KVGCanton {
		private String kvgCanton;

		public KVGCanton() {
			init();
		}

		private void init() {
			setKvgCanton(DEFAULT);
		}

		public String getKvgCanton() {
			return (kvgCanton != null) ? kvgCanton : "";
		}

		public void setKvgCanton(String kvgCanton) {
			this.kvgCanton = kvgCanton;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("KVGCanton");
			sb.append("\n");
			sb.append("kvgCanton: " + kvgCanton);
			return sb.toString();
		}
	}

	public class KVGInformation {
		private String languageRegion;
		private String KVGBase;
		private String KVGModel;
		private String KVGModelText;
		private KVGCanton kvgCanton;
		private String KVGAccidentCoverage;

		public KVGInformation() {
			init();
		}

		private void init() {
			setLanguageRegion(DEFAULT);
			setKVGBase(DEFAULT);
			setKVGModel(DEFAULT);
			setKVGModelText(DEFAULT);
			setKVGAccidentCoverage(DEFAULT);
		}

		public String getLanguageRegion() {
			return (languageRegion != null) ? languageRegion : "";
		}

		public void setLanguageRegion(String languageRegion) {
			this.languageRegion = languageRegion;
		}

		public String getKVGBase() {
			return (KVGBase != null) ? KVGBase : "";
		}

		public void setKVGBase(String kVGBase) {
			KVGBase = kVGBase;
		}

		public String getKVGModel() {
			return (KVGModel != null) ? KVGModel : "";
		}

		public void setKVGModel(String kVGModel) {
			KVGModel = kVGModel;
		}

		public String getKVGModelText() {
			return (KVGModelText != null) ? KVGModelText : "";
		}

		public void setKVGModelText(String kVGModelText) {
			KVGModelText = kVGModelText;
		}

		public KVGCanton getKvgCanton() {
			return (kvgCanton != null) ? kvgCanton : new KVGCanton();
		}

		public void setKvgCanton(KVGCanton kvgCanton) {
			this.kvgCanton = kvgCanton;
		}

		public String getKVGAccidentCoverage() {
			return (KVGAccidentCoverage != null) ? KVGAccidentCoverage : "";
		}

		public void setKVGAccidentCoverage(String kVGAccidentCoverage) {
			KVGAccidentCoverage = kVGAccidentCoverage;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("KVGInformation");
			sb.append("\n");
			sb.append("languageRegion: " + languageRegion);
			sb.append("\n");
			sb.append("KVGBase: " + KVGBase);
			sb.append("\n");
			sb.append("KVGModel: " + KVGModel);
			sb.append("\n");
			sb.append("KVGModelText: " + KVGModelText);
			sb.append("\n");
			sb.append("kvgCanton: " + kvgCanton);
			sb.append("\n");
			sb.append("KVGAccidentCoverage: " + KVGAccidentCoverage);
			return sb.toString();
		}
	}

	public class MailAddress {
		private String addressLine1;
		private String town;
		private String swissZipCode;
		private String country;

		public MailAddress() {
			init();
		}

		public void init() {
			setAddressLine1(DEFAULT);
			setTown(DEFAULT);
			setSwissZipCode(DEFAULT);
			setCountry(DEFAULT);
		}

		public String getAddressLine1() {
			return (addressLine1 != null) ? addressLine1 : "";
		}

		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}

		public String getTown() {
			return (town != null) ? town : "";
		}

		public void setTown(String town) {
			this.town = town;
		}

		public String getSwissZipCode() {
			return swissZipCode;
		}

		public void setSwissZipCode(String swissZipCode) {
			this.swissZipCode = swissZipCode;
		}

		public String getCountry() {
			return (country != null) ? country : "";
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("MailAddress");
			sb.append("\n");
			sb.append("addressLine1: " + addressLine1);
			sb.append("\n");
			sb.append("town: " + town);
			sb.append("\n");
			sb.append("swissZipCode: " + swissZipCode);
			sb.append("\n");
			sb.append("country: " + country);
			return sb.toString();
		}
	}

	public class Name {
		private String officialName;
		private String firstName;

		public Name() {
			init();
		}

		public void init() {
			setOfficialName(DEFAULT);
			setFirstName(DEFAULT);
		}

		public String getOfficialName() {
			return (officialName != null) ? officialName : "";
		}

		public void setOfficialName(String officialName) {
			this.officialName = officialName;
		}

		public String getFirstName() {
			return (firstName != null) ? firstName : "";
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("Name");
			sb.append("\n");
			sb.append("officialName: " + officialName);
			sb.append("\n");
			sb.append("firstName: " + firstName);

			return sb.toString();
		}
	}

	public class NationalExtension {
		private MailAddress mailAddress;
		private KVGInformation kvgInformation;
		private InsurerInformation insurerInformation;
		private VVGInformation vvgInformation;

		public MailAddress getMailAddress() {
			return (mailAddress != null) ? mailAddress : new MailAddress();
		}

		public void setMailAddress(MailAddress mailAddress) {
			this.mailAddress = mailAddress;
		}

		public KVGInformation getKvgInformation() {
			return (kvgInformation != null) ? kvgInformation : new KVGInformation();
		}

		public void setKvgInformation(KVGInformation kvgInformation) {
			this.kvgInformation = kvgInformation;
		}

		public InsurerInformation getInsurerInformation() {
			return (insurerInformation != null) ? insurerInformation : new InsurerInformation();
		}

		public void setInsurerInformation(InsurerInformation insurerInformation) {
			this.insurerInformation = insurerInformation;
		}

		public VVGInformation getVvgInformation() {
			return (vvgInformation != null) ? vvgInformation : new VVGInformation();
		}

		public void setVvgInformation(VVGInformation vvgInformation) {
			this.vvgInformation = vvgInformation;
		}
	}

	public class OfacExtension {
		private AdditionalKVGModel attitionalKVGModel;
		private DebNomAddress debNomAddress;
		private DebmailAddress debmailAddress;
		private String agenceKVGName;
		private AgenceKVGAddress agenceKVGAddress;
		private String agenceVVGName;
		private AgenceVVGAddress agenceVVGAddress;
		private String medicalServiceCoverageRestriction;
		private String validityDate;
		private String informationVVGSpitalText;

		public OfacExtension() {
			init();
		}

		private void init() {
			setAgenceKVGName(DEFAULT);
			setAgenceVVGName(DEFAULT);
			setMedicalServiceCoverageRestriction(DEFAULT);
			setValidityDate(DEFAULT);
			setInformationVVGSpitalText(DEFAULT);
		}

		public AdditionalKVGModel getAttitionalKVGModel() {
			return (attitionalKVGModel != null) ? attitionalKVGModel : new AdditionalKVGModel();
		}

		public void setAttitionalKVGModel(AdditionalKVGModel attitionalKVGModel) {
			this.attitionalKVGModel = attitionalKVGModel;
		}

		public DebNomAddress getDebNomAddress() {
			return (debNomAddress != null) ? debNomAddress : new DebNomAddress();
		}

		public void setDebNomAddress(DebNomAddress debNomAddress) {
			this.debNomAddress = debNomAddress;
		}

		public DebmailAddress getDebmailAddress() {
			return (debmailAddress != null) ? debmailAddress : new DebmailAddress();
		}

		public void setDebmailAddress(DebmailAddress debmailAddress) {
			this.debmailAddress = debmailAddress;
		}

		public String getAgenceKVGName() {
			return (agenceKVGName != null) ? agenceKVGName : "";
		}

		public void setAgenceKVGName(String agenceKVGName) {
			this.agenceKVGName = agenceKVGName;
		}

		public AgenceKVGAddress getAgenceKVGAddress() {
			return (agenceKVGAddress != null) ? agenceKVGAddress : new AgenceKVGAddress();
		}

		public void setAgenceKVGAddress(AgenceKVGAddress agenceKVGAddress) {
			this.agenceKVGAddress = agenceKVGAddress;
		}

		public String getAgenceVVGName() {
			return (agenceVVGName != null) ? agenceVVGName : "";
		}

		public void setAgenceVVGName(String agenceVVGName) {
			this.agenceVVGName = agenceVVGName;
		}

		public AgenceVVGAddress getAgenceVVGAddress() {
			return (agenceVVGAddress != null) ? agenceVVGAddress : new AgenceVVGAddress();
		}

		public void setAgenceVVGAddress(AgenceVVGAddress agenceVVGAddress) {
			this.agenceVVGAddress = agenceVVGAddress;
		}

		public String getMedicalServiceCoverageRestriction() {
			return (medicalServiceCoverageRestriction != null) ? medicalServiceCoverageRestriction : "";
		}

		public void setMedicalServiceCoverageRestriction(String medicalServiceCoverageRestriction) {
			this.medicalServiceCoverageRestriction = medicalServiceCoverageRestriction;
		}

		public String getValidityDate() {
			return (validityDate != null) ? validityDate : "";
		}

		public void setValidityDate(String validityDate) {
			this.validityDate = validityDate;
		}

		public String getInformationVVGSpitalText() {
			return (informationVVGSpitalText != null) ? informationVVGSpitalText : "";
		}

		public void setInformationVVGSpitalText(String informationVVGSpitalText) {
			this.informationVVGSpitalText = informationVVGSpitalText;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("OfacExtension");
			sb.append("\n");
			sb.append("agenceKVGName: " + agenceKVGName);
			sb.append("\n");
			sb.append("agenceVVGName: " + agenceVVGName);
			sb.append("\n");
			sb.append("medicalServiceCoverageRestriction: " + medicalServiceCoverageRestriction);
			sb.append("\n");
			sb.append("validityDate: " + validityDate);
			sb.append("\n");
			sb.append("informationVVGSpitalText: " + informationVVGSpitalText);
			return sb.toString();
		}
	}

	public class VVGInformation {
		private String medicationHL;
		private String medicationAccidentCoverageHL;
		private String medicationKM;
		private String medicationAccidentCoverageKM;
		private String medicalServiceCoverageVVG;
		private String hospitalModelBedVVG;
		private String hospitalModelDocVVG;
		private String accidentCoverageVVG;
		private VVGInsurerInformation vvgInsurerInformation;

		public VVGInformation() {
			init();
		}

		private void init() {
			setMedicationHL(DEFAULT);
			setMedicationAccidentCoverageHL(DEFAULT);
			setMedicationKM(DEFAULT);
			setMedicationAccidentCoverageKM(DEFAULT);
			setMedicalServiceCoverageVVG(DEFAULT);
			setHospitalModelBedVVG(DEFAULT);
			setHospitalModelDocVVG(DEFAULT);
			setAccidentCoverageVVG(DEFAULT);
		}

		public String getMedicationHL() {
			return (medicationHL != null) ? medicationHL : "";
		}

		public void setMedicationHL(String medicationHL) {
			this.medicationHL = medicationHL;
		}

		public String getMedicationAccidentCoverageHL() {
			return (medicationAccidentCoverageHL != null) ? medicationAccidentCoverageHL : "";
		}

		public void setMedicationAccidentCoverageHL(String medicationAccidentCoverageHL) {
			this.medicationAccidentCoverageHL = medicationAccidentCoverageHL;
		}

		public String getMedicationKM() {
			return (medicationKM != null) ? medicationKM : "";
		}

		public void setMedicationKM(String medicationKM) {
			this.medicationKM = medicationKM;
		}

		public String getMedicationAccidentCoverageKM() {
			return (medicationAccidentCoverageKM != null) ? medicationAccidentCoverageKM : "";
		}

		public void setMedicationAccidentCoverageKM(String medicationAccidentCoverageKM) {
			this.medicationAccidentCoverageKM = medicationAccidentCoverageKM;
		}

		public String getMedicalServiceCoverageVVG() {
			return (medicalServiceCoverageVVG != null) ? medicalServiceCoverageVVG : "";
		}

		public void setMedicalServiceCoverageVVG(String medicalServiceCoverageVVG) {
			this.medicalServiceCoverageVVG = medicalServiceCoverageVVG;
		}

		public String getHospitalModelBedVVG() {
			return (hospitalModelBedVVG != null) ? hospitalModelBedVVG : "";
		}

		public void setHospitalModelBedVVG(String hospitalModelBedVVG) {
			this.hospitalModelBedVVG = hospitalModelBedVVG;
		}

		public String getHospitalModelDocVVG() {
			return (hospitalModelDocVVG != null) ? hospitalModelDocVVG : "";
		}

		public void setHospitalModelDocVVG(String hospitalModelDocVVG) {
			this.hospitalModelDocVVG = hospitalModelDocVVG;
		}

		public String getAccidentCoverageVVG() {
			return (accidentCoverageVVG != null) ? accidentCoverageVVG : "";
		}

		public void setAccidentCoverageVVG(String accidentCoverageVVG) {
			this.accidentCoverageVVG = accidentCoverageVVG;
		}

		public VVGInsurerInformation getVvgInsurerInformation() {
			return (vvgInsurerInformation != null) ? vvgInsurerInformation : new VVGInsurerInformation();
		}

		public void setVvgInsurerInformation(VVGInsurerInformation vvgInsurerInformation) {
			this.vvgInsurerInformation = vvgInsurerInformation;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("VVGInformation");
			sb.append("\n");
			sb.append("medicationHL: " + medicationHL);
			sb.append("\n");
			sb.append("medicationAccidentCoverageHL: " + medicationAccidentCoverageHL);
			sb.append("\n");
			sb.append("medicationKM: " + medicationKM);
			sb.append("\n");
			sb.append("medicationAccidentCoverageKM: " + medicationAccidentCoverageKM);
			sb.append("\n");
			sb.append("medicalServiceCoverageVVG: " + medicalServiceCoverageVVG);
			sb.append("\n");
			sb.append("hospitalModelBedVVG: " + hospitalModelBedVVG);
			sb.append("\n");
			sb.append("hospitalModelDocVVG: " + hospitalModelDocVVG);
			sb.append("\n");
			sb.append("accidentCoverageVVG: " + accidentCoverageVVG);
			return sb.toString();
		}
	}

	public class VVGInsurerInformation {
		private String issuingStateIdNumer;
		private String nameOfVVGInsurer;
		private String identificationNumberOfVVGInsurer;
		private String coverCardNo;
		private String insuredPersonNumber;
		private String insuredNumber;
		private String expiryDate;
		private InsurerInformation insurerInformation;

		public VVGInsurerInformation() {
			init();
		}

		private void init() {
			setIssuingStateIdNumer(DEFAULT);
			setNameOfVVGInsurer(DEFAULT);
			setIdentificationNumberOfVVGInsurer(DEFAULT);
			setCoverCardNo(DEFAULT);
			setInsuredPersonNumber(DEFAULT);
			setInsuredNumber(DEFAULT);
			setExpiryDate(DEFAULT);
		}

		public String getIssuingStateIdNumer() {
			return (issuingStateIdNumer != null) ? issuingStateIdNumer : "";
		}

		public void setIssuingStateIdNumer(String issuingStateIdNumer) {
			this.issuingStateIdNumer = issuingStateIdNumer;
		}

		public String getNameOfVVGInsurer() {
			return (nameOfVVGInsurer != null) ? nameOfVVGInsurer : "";
		}

		public void setNameOfVVGInsurer(String nameOfVVGInsurer) {
			this.nameOfVVGInsurer = nameOfVVGInsurer;
		}

		public String getIdentificationNumberOfVVGInsurer() {
			return (identificationNumberOfVVGInsurer != null) ? identificationNumberOfVVGInsurer : "";
		}

		public void setIdentificationNumberOfVVGInsurer(String identificationNumberOfVVGInsurer) {
			this.identificationNumberOfVVGInsurer = identificationNumberOfVVGInsurer;
		}

		public String getCoverCardNo() {
			return (coverCardNo != null) ? coverCardNo : "";
		}

		public void setCoverCardNo(String coverCardNo) {
			this.coverCardNo = coverCardNo;
		}

		public String getInsuredPersonNumber() {
			return (insuredPersonNumber != null) ? insuredPersonNumber : "";
		}

		public void setInsuredPersonNumber(String insuredPersonNumber) {
			this.insuredPersonNumber = insuredPersonNumber;
		}

		public String getInsuredNumber() {
			return (insuredNumber != null) ? insuredNumber : "";
		}

		public void setInsuredNumber(String insuredNumber) {
			this.insuredNumber = insuredNumber;
		}

		public String getExpiryDate() {
			return (expiryDate != null) ? expiryDate : "";
		}

		public void setExpiryDate(String expiryDate) {
			this.expiryDate = expiryDate;
		}

		public InsurerInformation getInsurerInformation() {
			return (insurerInformation != null) ? insurerInformation : new InsurerInformation();
		}

		public void setInsurerInformation(InsurerInformation insurerInformation) {
			this.insurerInformation = insurerInformation;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------------");
			sb.append("VVGInsurerInformation");
			sb.append("\n");
			sb.append("issuingStateIdNumer: " + issuingStateIdNumer);
			sb.append("\n");
			sb.append("nameOfVVGInsurer: " + nameOfVVGInsurer);
			sb.append("\n");
			sb.append("identificationNumberOfVVGInsurer: " + identificationNumberOfVVGInsurer);
			sb.append("\n");
			sb.append("coverCardNo: " + coverCardNo);
			sb.append("\n");
			sb.append("insuredPersonNumber: " + insuredPersonNumber);
			sb.append("\n");
			sb.append("insuredNumber: " + insuredNumber);
			sb.append("\n");
			sb.append("expiryDate: " + expiryDate);
			return sb.toString();
		}
	}

	public AdministrativeData getAdministrativeData() {
		return (administrativeData != null) ? administrativeData : new AdministrativeData();
	}

	public void setAdministrativeData(AdministrativeData administrativeData) {
		this.administrativeData = administrativeData;
	}

	public AdditionalKVGModel getAdditionalKVGModel() {
		return (additionalKVGModel != null) ? additionalKVGModel : new AdditionalKVGModel();
	}

	public void setAdditionalKVGModel(AdditionalKVGModel additionalKVGModel) {
		this.additionalKVGModel = additionalKVGModel;
	}

	public AgenceKVGAddress getAgenceKVGAddress() {
		return (agenceKVGAddress != null) ? agenceKVGAddress : new AgenceKVGAddress();
	}

	public void setAgenceKVGAddress(AgenceKVGAddress agenceKVGAddress) {
		this.agenceKVGAddress = agenceKVGAddress;
	}

	public AgenceVVGAddress getAgenceVVGAddress() {
		return (agenceVVGAddress != null) ? agenceVVGAddress : new AgenceVVGAddress();
	}

	public void setAgenceVVGAddress(AgenceVVGAddress agenceVVGAddress) {
		this.agenceVVGAddress = agenceVVGAddress;
	}

	public BillingAddress getBillingAddress() {
		return (billingAddress != null) ? billingAddress : new BillingAddress();
	}

	public void setBillingAddress(BillingAddress billingAddress) {
		this.billingAddress = billingAddress;
	}

	public CHBaseInformation getChBaseInformation() {
		return (chBaseInformation != null) ? chBaseInformation : new CHBaseInformation();
	}

	public void setChBaseInformation(CHBaseInformation chBaseInformation) {
		this.chBaseInformation = chBaseInformation;
	}

	public ContactNumberGerman getContactNumberGermanKVG() {
		return (contactNumberGermanKVG != null) ? contactNumberGermanKVG : new ContactNumberGerman();
	}

	public void setContactNumberGermanKVG(ContactNumberGerman contactNumberGermanKVG) {
		this.contactNumberGermanKVG = contactNumberGermanKVG;
	}

	public ContactNumberGerman getContactNumberGermanVVG() {
		return (contactNumberGermanVVG != null) ? contactNumberGermanVVG : new ContactNumberGerman();
	}

	public void setContactNumberGermanVVG(ContactNumberGerman contactNumberGermanVVG) {
		this.contactNumberGermanVVG = contactNumberGermanVVG;
	}

	public DateOfBirth getDateOfBirth() {
		return (dateOfBirth != null) ? dateOfBirth : new DateOfBirth();
	}

	public void setDateOfBirth(DateOfBirth dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public DebmailAddress getDebmailAddress() {
		return (debmailAddress != null) ? debmailAddress : new DebmailAddress();
	}

	public void setDebmailAddress(DebmailAddress debmailAddress) {
		this.debmailAddress = debmailAddress;
	}

	public DebNomAddress getDebNomAddress() {
		return (debNomAddress != null) ? debNomAddress : new DebNomAddress();
	}

	public void setDebNomAddress(DebNomAddress debNomAddress) {
		this.debNomAddress = debNomAddress;
	}

	public IdentificationData getIdentificationData() {
		return (identificationData != null) ? identificationData : new IdentificationData();
	}

	public void setIdentificationData(IdentificationData identificationData) {
		this.identificationData = identificationData;
	}

	public InsurerInformation getInsurerInformation() {
		return (insurerInformation != null) ? insurerInformation : new InsurerInformation();
	}

	public void setInsurerInformation(InsurerInformation insurerInformation) {
		this.insurerInformation = insurerInformation;
	}

	public KVGCanton getKvgCanton() {
		return (kvgCanton != null) ? kvgCanton : new KVGCanton();
	}

	public void setKvgCanton(KVGCanton kvgCanton) {
		this.kvgCanton = kvgCanton;
	}

	public KVGInformation getKvgInformation() {
		return (kvgInformation != null) ? kvgInformation : new KVGInformation();
	}

	public void setKvgInformation(KVGInformation kvgInformation) {
		this.kvgInformation = kvgInformation;
	}

	public MailAddress getMailAddress() {
		return (mailAddress != null) ? mailAddress : new MailAddress();
	}

	public void setMailAddress(MailAddress mailAddress) {
		this.mailAddress = mailAddress;
	}

	public Name getName() {
		return (name != null) ? name : new Name();
	}

	public void setName(Name name) {
		this.name = name;
	}

	public NationalExtension getNationalExtension() {
		return (nationalExtension != null) ? nationalExtension : new NationalExtension();
	}

	public void setNationalExtension(NationalExtension nationalExtension) {
		this.nationalExtension = nationalExtension;
	}

	public OfacExtension getOfacExtension() {
		return (ofacExtension != null) ? ofacExtension : new OfacExtension();
	}

	public void setOfacExtension(OfacExtension ofacExtension) {
		this.ofacExtension = ofacExtension;
	}

	public VVGInformation getVvgInformation() {

		return (vvgInformation != null) ? vvgInformation : new VVGInformation();
	}

	public void setVvgInformation(VVGInformation vvgInformation) {
		this.vvgInformation = vvgInformation;
	}

	public VVGInsurerInformation getVvgInsurerInformation() {
		return (vvgInsurerInformation != null) ? vvgInsurerInformation : new VVGInsurerInformation();
	}

	public void setVvgInsurerInformation(VVGInsurerInformation vvgInsurerInformation) {
		this.vvgInsurerInformation = vvgInsurerInformation;
	}

	public String getCardNoVeka() {
		return (cardNoVeka != null) ? cardNoVeka : "";
	}

	public void setCardNoVeka(String cardNoVeka) {
		this.cardNoVeka = cardNoVeka;
	}

	public String getzSRNo() {
		return (zSRNo != null) ? zSRNo : "";
	}

	public void setzSRNo(String zSRNo) {
		this.zSRNo = zSRNo;
	}

	public String getQualifyingData() {
		return (qualifyingData != null) ? qualifyingData : "";
	}

	public void setQualifyingData(String qualifyingData) {
		this.qualifyingData = qualifyingData;
	}

	public String getQueryNumber() {
		return (queryNumber != null) ? queryNumber : "";
	}

	public void setQueryNumber(String queryNumber) {
		this.queryNumber = queryNumber;
	}

	public String getValidCard() {
		return (validCard != null) ? validCard : "";
	}

	public void setValidCard(String validCard) {
		this.validCard = validCard;
	}

	public String getCodProv() {
		return (codProv != null) ? codProv : "";
	}

	public void setCodProv(String codProv) {
		this.codProv = codProv;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------");
		sb.append("CardInfo");
		sb.append("\n");
		sb.append("cardNoVeka: " + cardNoVeka);
		sb.append("\n");
		sb.append("zSRNo: " + zSRNo);
		sb.append("\n");
		sb.append("qualifyingData: " + qualifyingData);
		sb.append("\n");
		sb.append("queryNumber: " + queryNumber);
		sb.append("\n");
		sb.append("validCard: " + validCard);
		sb.append("\n");
		sb.append("codProv: " + codProv);
		sb.append("\n");
		return sb.toString();
	}

}
