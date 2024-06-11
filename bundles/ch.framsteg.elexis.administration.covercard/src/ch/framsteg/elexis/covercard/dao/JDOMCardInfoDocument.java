package ch.framsteg.elexis.covercard.dao;

import java.util.Properties;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import ch.framsteg.elexis.covercard.exceptions.BlockedCardException;
import ch.framsteg.elexis.covercard.exceptions.InvalidCardException;
import ch.framsteg.elexis.covercard.exceptions.UnsupportedCardException;

public class JDOMCardInfoDocument extends Document {

	private static final long serialVersionUID = 1L;
	private Document document;
	private Properties applicationProperties;
	private CardInfoData cardInfoData;
	private CardInfoData.CHBaseInformation chBaseInformation;
	private CardInfoData.IdentificationData identificationData;
	private CardInfoData.Name name;
	private CardInfoData.DateOfBirth dateOfBirth;
	private CardInfoData.AdministrativeData administrativeData;
	private CardInfoData.NationalExtension nationalExtension;
	private CardInfoData.MailAddress mailAddress;
	private CardInfoData.KVGInformation kvgInformation;
	private CardInfoData.KVGCanton kvgCanton;
	private CardInfoData.InsurerInformation insurerInformationKVG;
	private CardInfoData.InsurerInformation insurerInformationVVG;
	private CardInfoData.BillingAddress billingAddress;
	private CardInfoData.BillingAddress vvgBillingAddress;
	private CardInfoData.VVGInformation vvgInformation;
	private CardInfoData.VVGInsurerInformation vvgInsurerInformation;
	private CardInfoData.OfacExtension ofacExtension;
	private CardInfoData.AdditionalKVGModel additionalKVGModel;
	private CardInfoData.DebNomAddress debNomAddress;
	private CardInfoData.DebmailAddress debmailAddress;
	private CardInfoData.AgenceKVGAddress agenceKVGAddress;
	private CardInfoData.AgenceVVGAddress agenceVVGAddress;
	private CardInfoData.ContactNumberGerman contactNumberGermanKVG;
	private CardInfoData.ContactNumberGerman contactNumberGermanVVG;

	private static final String NAMESPACE = "xml.namespace";
	private static final String NAMESPACE_PREFIX = "xml.namespace.prefix";

	private Namespace namespace;

	public JDOMCardInfoDocument(Document document, Properties applicationProperties) {
		this.document = document;
		this.applicationProperties = applicationProperties;
	}

	public CardInfoData unmarshall()
			throws InvalidCardException, JDOMException, UnsupportedCardException, BlockedCardException {

		init();

		setNamespace(Namespace.getNamespace(applicationProperties.getProperty(NAMESPACE_PREFIX),
				applicationProperties.getProperty(NAMESPACE)));

		Element rootElement = this.document.getRootElement();

		if (rootElement.getChildText("cardNoVeka", namespace) != null) {
			cardInfoData.setCardNoVeka(rootElement.getChildText("cardNoVeka", namespace));
		}
		if (rootElement.getChildText("ZSRno", namespace) != null) {
			cardInfoData.setzSRNo(rootElement.getChildText("ZSRno", namespace));
		}
		if (rootElement.getChildText("qualifyingDate", namespace) != null) {
			cardInfoData.setQualifyingData(rootElement.getChildText("qualifyingDate", namespace));
		}
		if (rootElement.getChildText("queryNumber", namespace) != null) {
			cardInfoData.setQueryNumber(rootElement.getChildText("queryNumber", namespace));
		}
		if (rootElement.getChildText("validCard", namespace) != null) {
			if (!rootElement.getChildText("validCard", namespace).equalsIgnoreCase("67")) {
				cardInfoData.setValidCard(rootElement.getChildText("validCard", namespace));
			} else {
				throw new BlockedCardException("The used card is blocked");
			}
		}
		if (rootElement.getChildText("codProv", namespace) != null) {
			cardInfoData.setCodProv(rootElement.getChildText("codProv", namespace));
		}

		/* CH-Baseinformation */
		Element chBaseInformationElem = rootElement.getChild("CH-Baseinformation", namespace);
		if (chBaseInformationElem != null) {
			if (chBaseInformationElem.getChild("identificationData", namespace) != null) {
				/* CH-Baseinformation/identificationData */
				Element identificationDataElem = chBaseInformationElem.getChild("identificationData", namespace);
				if (identificationDataElem.getChild("name", namespace) != null) {
					/* CH-Baseinformation/identificationData/name/* */
					Element nameElem = identificationDataElem.getChild("name", namespace);
					if (nameElem.getChild("officialName", namespace) != null) {
						Element officialNameElem = nameElem.getChild("officialName", namespace);
						name.setOfficialName(officialNameElem.getValue() != null ? officialNameElem.getValue() : "");
					}
					if (nameElem.getChild("firstName", namespace) != null) {
						Element firstNameElem = nameElem.getChild("firstName", namespace);
						name.setFirstName(firstNameElem.getValue() != null ? firstNameElem.getValue() : "");
					}
					cardInfoData.setName(name);
				}

				if (identificationDataElem.getChild("dateOfBirth", namespace) != null) {
					/* CH-Baseinformation/identificationData/dateOfBirth/* */
					Element dateOfBirthElem = identificationDataElem.getChild("dateOfBirth", namespace);
					if (dateOfBirthElem.getChild("yearMonthDay", namespace) != null) {
						Element yearMonthDayElem = dateOfBirthElem.getChild("yearMonthDay", namespace);
						dateOfBirth.setYearMonthDay(
								yearMonthDayElem.getValue() != null ? yearMonthDayElem.getValue() : "");
					}
				}

				if (identificationDataElem.getChild("cardholderIdentifier", namespace) != null) {
					/* CH-Baseinformation/identificationData/cardholderIdentifier */
					Element cardholderIdentifierElem = identificationDataElem.getChild("cardholderIdentifier",
							namespace);
					identificationData.setCardholderIdentifier(
							cardholderIdentifierElem.getValue() != null ? cardholderIdentifierElem.getValue() : "");
				}

				if (identificationDataElem.getChild("sex", namespace) != null) {
					/* CH-Baseinformation/identificationData/sex */
					Element sex = identificationDataElem.getChild("sex", namespace);
					identificationData.setSex(sex.getValue() != null ? sex.getValue() : "");
				}
			}
			if (chBaseInformationElem.getChild("administrativeData", namespace) != null) {

				/* CH-Baseinformation/administrativeData/* */
				Element administrativeDataElem = chBaseInformationElem.getChild("administrativeData", namespace);

				if (administrativeDataElem.getChild("issuingStateIdNumer", namespace) != null) {
					/* CH-Baseinformation/administrativeData/issuingStateIdNumer */
					Element issuingStateIdNumerElem = administrativeDataElem.getChild("issuingStateIdNumer", namespace);
					administrativeData.setIssuingStateIdNumber(
							issuingStateIdNumerElem.getValue() != null ? issuingStateIdNumerElem.getValue() : "");
				}
				if (administrativeDataElem.getChild("nameOfTheInstitution", namespace) != null) {
					/* CH-Baseinformation/administrativeData/nameOfTheInstitution */
					Element nameOfTheInstitutionElem = administrativeDataElem.getChild("nameOfTheInstitution",
							namespace);
					administrativeData.setNameOfTheInstitution(
							nameOfTheInstitutionElem.getValue() != null ? nameOfTheInstitutionElem.getValue() : "");
				}

				if (administrativeDataElem.getChild("identificationNumberOfTheInstitution", namespace) != null) {
					/* CH-Baseinformation/administrativeData/identificationNumberOfTheInstitution */
					Element identificationNumberOfTheInstitutionElem = administrativeDataElem
							.getChild("identificationNumberOfTheInstitution", namespace);
					administrativeData.setIdentificationNumberOfTheInstitution(
							identificationNumberOfTheInstitutionElem.getValue() != null
									? identificationNumberOfTheInstitutionElem.getValue()
									: "");
				}

				if (administrativeDataElem.getChild("coverCardNo", namespace) != null) {
					/* CH-Baseinformation/administrativeData/coverCardNo */
					Element coverCardNoElem = administrativeDataElem.getChild("coverCardNo", namespace);
					administrativeData
							.setCoverCardNo(coverCardNoElem.getValue() != null ? coverCardNoElem.getValue() : "");
				}

				if (administrativeDataElem.getChild("insuredPersonNumber", namespace) != null) {
					/* CH-Baseinformation/administrativeData/insuredPersonNumber */
					Element insuredPersonNumberElem = administrativeDataElem.getChild("insuredPersonNumber", namespace);
					administrativeData.setInsuredPersonNumber(
							insuredPersonNumberElem.getValue() != null ? insuredPersonNumberElem.getValue() : "");
				}

				if (administrativeDataElem.getChild("insuredNumber", namespace) != null) {
					/* CH-Baseinformation/administrativeData/insuredNumber */
					Element insuredNumberElem = administrativeDataElem.getChild("insuredNumber", namespace);
					administrativeData
							.setInsuredNumber(insuredNumberElem.getValue() != null ? insuredNumberElem.getValue() : "");
				}
				if (administrativeDataElem.getChild("expiryDate", namespace) != null) {
					/* CH-Baseinformation/administrativeData/expiryDate */
					Element expiryDateElem = administrativeDataElem.getChild("expiryDate", namespace);
					administrativeData
							.setExpiryDate(expiryDateElem.getValue() != null ? expiryDateElem.getValue() : "");
				}
			}
			if (chBaseInformationElem.getChild("nationalExtension", namespace) != null) {
				/* CH-Baseinformation/nationalExtension/* */
				Element nationalExtensionElem = chBaseInformationElem.getChild("nationalExtension", namespace);

				if (nationalExtensionElem.getChild("mailAddress", namespace) != null) {
					/* CH-Baseinformation/nationalExtension/mailAddress */
					Element mailAddressElem = nationalExtensionElem.getChild("mailAddress", namespace);
					if (mailAddressElem.getChild("addressLine1", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/mailAddress/addressLine1 */
						Element mailAddress_addressLine1Elem = mailAddressElem.getChild("addressLine1", namespace);
						mailAddress.setAddressLine1(mailAddress_addressLine1Elem.getValue() != null
								? mailAddress_addressLine1Elem.getValue()
								: "");
					}

					if (mailAddressElem.getChild("town", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/mailAddress/town */
						Element mailAddress_townElem = mailAddressElem.getChild("town", namespace);
						mailAddress.setTown(
								mailAddress_townElem.getValue() != null ? mailAddress_townElem.getValue() : "");
					}

					if (mailAddressElem.getChild("swissZipCode", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/mailAddress/swissZipCode */
						Element mailAddress_swissZipCodeElem = mailAddressElem.getChild("swissZipCode", namespace);
						mailAddress.setSwissZipCode(mailAddress_swissZipCodeElem.getValue() != null
								? mailAddress_swissZipCodeElem.getValue()
								: "");
					}

					if (mailAddressElem.getChild("country", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/mailAddress/country */
						Element mailAddress_countryElem = mailAddressElem.getChild("country", namespace);
						mailAddress.setCountry(
								mailAddress_countryElem.getValue() != null ? mailAddress_countryElem.getValue() : "");
					}
				}

				if (nationalExtensionElem.getChild("KVGInformation", namespace) != null) {
					/* CH-Baseinformation/nationalExtension/KVGInformation */
					Element KVGInformationElem = nationalExtensionElem.getChild("KVGInformation", namespace);
					if (KVGInformationElem.getChild("languageRegion", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/KVGInformation/languageRegion */
						Element languageRegionElem = KVGInformationElem.getChild("languageRegion", namespace);
						kvgInformation.setLanguageRegion(
								languageRegionElem.getValue() != null ? languageRegionElem.getValue() : "");
					}
					if (KVGInformationElem.getChild("KVGBase", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/KVGInformation/KVGBase */
						Element KVGBaseElem = KVGInformationElem.getChild("KVGBase", namespace);
						kvgInformation.setKVGBase(KVGBaseElem.getValue() != null ? KVGBaseElem.getValue() : "");
					} else {
						throw new UnsupportedCardException("The used card is not supported");
					}
					if (KVGInformationElem.getChild("KVGModel", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/KVGInformation/KVGModel */
						Element KVGModelElem = KVGInformationElem.getChild("KVGModel", namespace);
						kvgInformation.setKVGModel(KVGModelElem.getValue() != null ? KVGModelElem.getValue() : "");
					}
					if (KVGInformationElem.getChild("KVGModelText", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/KVGInformation/KVGModelText */
						Element KVGModelTextElem = KVGInformationElem.getChild("KVGModelText", namespace);
						kvgInformation.setKVGModelText(
								KVGModelTextElem.getValue() != null ? KVGModelTextElem.getValue() : "");
					}
					if (KVGInformationElem.getChild("KVGcanton", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/KVGInformation/KVGcanton */
						Element KVGcantonElem = KVGInformationElem.getChild("KVGcanton", namespace);
						if (KVGcantonElem.getChild("KVGcanton", namespace) != null) {
							/* CH-Baseinformation/nationalExtension/KVGInformation/KVGcanton/KVGcanton */
							Element KVGcanton_KVGcantonElem = KVGcantonElem.getChild("KVGcanton", namespace);
							kvgCanton.setKvgCanton(
									KVGcanton_KVGcantonElem.getValue() != null ? KVGcanton_KVGcantonElem.getValue()
											: "");
						}
						if (KVGcantonElem.getChild("foreignCountry", namespace) != null) {
							/*
							 * CH-Baseinformation/nationalExtension/KVGInformation/KVGcanton/foreignCountry
							 */
							Element KVGcanton_foreignCountryElem = KVGcantonElem.getChild("foreignCountry", namespace);
							kvgCanton.setKvgCanton(KVGcanton_foreignCountryElem.getValue() != null
									? KVGcanton_foreignCountryElem.getValue()
									: "");
						}
						kvgInformation.setKvgCanton(kvgCanton);
					}
					if (KVGInformationElem.getChild("KVGaccidentCoverage", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/KVGInformation/KVGaccidentCoverage */
						Element KVGaccidentCoverageElem = KVGInformationElem.getChild("KVGaccidentCoverage", namespace);
						kvgInformation.setKVGAccidentCoverage(
								KVGaccidentCoverageElem.getValue() != null ? KVGaccidentCoverageElem.getValue() : "");
					}

				}

				nationalExtension.setKvgInformation(kvgInformation);

				if (nationalExtensionElem.getChild("insurerInformation", namespace) != null) {

					/* CH-Baseinformation/nationalExtension/insurerInformation */
					Element insurerInformationElem = nationalExtensionElem.getChild("insurerInformation", namespace);

					if (insurerInformationElem.getChild("contactNumberGerman", namespace) != null) {
						/*
						 * CH-Baseinformation/nationalExtension/insurerInformation/contactNumberGerman
						 */
						Element contactNumberGermanElem = insurerInformationElem.getChild("contactNumberGerman",
								namespace);

						if (contactNumberGermanElem.getAttribute("number") != null) {

							/*
							 * CH-Baseinformation/nationalExtension/insurerInformation/contactNumberGerman[
							 * number]
							 */
							Attribute contactNumberGerman_numberElem = contactNumberGermanElem.getAttribute("number");
							contactNumberGermanKVG.setNumber(contactNumberGerman_numberElem.getValue() != null
									? contactNumberGerman_numberElem.getValue()
									: "");
						}

						if (contactNumberGermanElem.getAttribute("international-code") != null) {
							/*
							 * CH-Baseinformation/nationalExtension/insurerInformation/contactNumberGerman[
							 * international-code]
							 */
							Attribute contactNumberGerman_internationalCodeElem = contactNumberGermanElem
									.getAttribute("international-code");
							contactNumberGermanKVG
									.setInternationalCode(contactNumberGerman_internationalCodeElem.getValue() != null
											? contactNumberGerman_internationalCodeElem.getValue()
											: "");
						}

						if (contactNumberGermanElem.getAttribute("local-code") != null) {
							/*
							 * CH-Baseinformation/nationalExtension/insurerInformation/contactNumberGerman[
							 * number]
							 */
							Attribute contactNumberGerman_localCodeElem = contactNumberGermanElem
									.getAttribute("local-code");
							contactNumberGermanKVG.setLocalCode(contactNumberGerman_localCodeElem.getValue() != null
									? contactNumberGerman_localCodeElem.getValue()
									: "");
						}
					}
					insurerInformationKVG.setContactNumberGerman(contactNumberGermanKVG);

					if (insurerInformationElem.getChild("contactEmailAddress", namespace) != null) {
						/*
						 * CH-Baseinformation/nationalExtension/insurerInformation/contactEmailAddress[
						 * local-code]
						 */
						Element contactEmailAddressElem = insurerInformationElem.getChild("contactEmailAddress",
								namespace);
						insurerInformationKVG.setContactEmailAddress(
								contactEmailAddressElem.getValue() != null ? contactEmailAddressElem.getValue() : "");

					}
					if (insurerInformationElem.getChild("contactEanNumber", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/insurerInformation/contactEanNumber */
						Element contactEanNumberElem = insurerInformationElem.getChild("contactEanNumber", namespace);
						insurerInformationKVG.setContactEanNumber(
								contactEanNumberElem.getValue() != null ? contactEanNumberElem.getValue() : "xy");
					}
					if (insurerInformationElem.getChild("billingAddress", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/insurerInformation/billingAddress */
						Element insurerInformation_billingAddressElem = insurerInformationElem
								.getChild("billingAddress", namespace);

						if (insurerInformation_billingAddressElem.getChild("addressLine1", namespace) != null) {
							/*
							 * CH-Baseinformation/nationalExtension/insurerInformation/billingAddress/
							 * addressLine1
							 */
							Element insurerInformation_billingAddress_addressLine1Elem = insurerInformation_billingAddressElem
									.getChild("addressLine1", namespace);

							billingAddress.setAddressLine1(
									insurerInformation_billingAddress_addressLine1Elem.getValue() != null
											? insurerInformation_billingAddress_addressLine1Elem.getValue()
											: "");

							/*
							 * CH-Baseinformation/nationalExtension/insurerInformation/billingAddress/town
							 */
							Element insurerInformation_billingAddress_townElem = insurerInformation_billingAddressElem
									.getChild("town", namespace);
							billingAddress.setTown(insurerInformation_billingAddress_townElem.getValue() != null
									? insurerInformation_billingAddress_townElem.getValue()
									: "");

							/*
							 * CH-Baseinformation/nationalExtension/insurerInformation/billingAddress/
							 * swissZipCode
							 */
							Element insurerInformation_billingAddress_swissZipCodeElem = insurerInformation_billingAddressElem
									.getChild("swissZipCode", namespace);
							billingAddress.setTown(insurerInformation_billingAddress_swissZipCodeElem.getValue() != null
									? insurerInformation_billingAddress_swissZipCodeElem.getValue()
									: "");

							/*
							 * CH-Baseinformation/nationalExtension/insurerInformation/billingAddress/
							 * country
							 */
							Element insurerInformation_billingAddress_countryElem = insurerInformation_billingAddressElem
									.getChild("country", namespace);
							billingAddress.setTown(insurerInformation_billingAddress_countryElem.getValue() != null
									? insurerInformation_billingAddress_countryElem.getValue()
									: "");
						}
						// insurerInformationKVG
					}

				}

				insurerInformationKVG.setBillingAddress(billingAddress);
				nationalExtension.setInsurerInformation(insurerInformationKVG);
				nationalExtension.setMailAddress(mailAddress);

				if (nationalExtensionElem.getChild("VVGInformation", namespace) != null) {
					/* CH-Baseinformation/nationalExtension/VVGInformation */
					Element VVGInformationElem = nationalExtensionElem.getChild("VVGInformation", namespace);

					if (VVGInformationElem.getChild("VVGinsurerInformation", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation */
						Element VVGinsurerInformationElem = VVGInformationElem.getChild("VVGinsurerInformation",
								namespace);

						if (VVGinsurerInformationElem.getChild("issuingStateIdNumer", namespace) != null) {
							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * issuingStateIdNumer
							 */
							Element VVGinsurerInformation_issuingStateIdNumerElem = VVGinsurerInformationElem
									.getChild("issuingStateIdNumer", namespace);

							vvgInsurerInformation.setIssuingStateIdNumer(
									VVGinsurerInformation_issuingStateIdNumerElem.getValue() != null
											? VVGinsurerInformation_issuingStateIdNumerElem.getValue()
											: "");
						}

						if (VVGinsurerInformationElem.getChild("nameOfVVGInsurer", namespace) != null) {
							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * nameOfVVGInsurer
							 */
							Element VVGinsurerInformation_nameOfVVGInsurerElem = VVGinsurerInformationElem
									.getChild("nameOfVVGInsurer", namespace);

							vvgInsurerInformation
									.setNameOfVVGInsurer(VVGinsurerInformation_nameOfVVGInsurerElem.getValue() != null
											? VVGinsurerInformation_nameOfVVGInsurerElem.getValue()
											: "");

						}

						if (VVGinsurerInformationElem.getChild("identificationNumberOfVVGInsurer", namespace) != null) {
							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * identificationNumberOfVVGInsurer
							 */
							Element VVGinsurerInformation_identificationNumberOfVVGInsurerElem = VVGinsurerInformationElem
									.getChild("identificationNumberOfVVGInsurer", namespace);
							vvgInsurerInformation.setIdentificationNumberOfVVGInsurer(
									VVGinsurerInformation_identificationNumberOfVVGInsurerElem.getValue() != null
											? VVGinsurerInformation_identificationNumberOfVVGInsurerElem.getValue()
											: "");
						}

						if (VVGinsurerInformationElem.getChild("coverCardNo", namespace) != null) {

							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * coverCardNo
							 */
							Element VVGinsurerInformation_coverCardNoElem = VVGinsurerInformationElem
									.getChild("coverCardNo", namespace);
							vvgInsurerInformation
									.setCoverCardNo(VVGinsurerInformation_coverCardNoElem.getValue() != null
											? VVGinsurerInformation_coverCardNoElem.getValue()
											: "");

						}

						if (VVGinsurerInformationElem.getChild("insuredPersonNumber", namespace) != null) {
							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * insuredPersonNumber
							 */
							Element VVGinsurerInformation_insuredPersonNumberElem = VVGinsurerInformationElem
									.getChild("insuredPersonNumber", namespace);
							vvgInsurerInformation.setInsuredPersonNumber(
									VVGinsurerInformation_insuredPersonNumberElem.getValue() != null
											? VVGinsurerInformation_insuredPersonNumberElem.getValue()
											: "");
						}

						if (VVGinsurerInformationElem.getChild("insuredNumber", namespace) != null) {
							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * insuredNumber
							 */
							Element VVGinsurerInformation_insuredNumberElem = VVGinsurerInformationElem
									.getChild("insuredNumber", namespace);
							vvgInsurerInformation
									.setInsuredNumber(VVGinsurerInformation_insuredNumberElem.getValue() != null
											? VVGinsurerInformation_insuredNumberElem.getValue()
											: "");
						}

						if (VVGinsurerInformationElem.getChild("expiryDate", namespace) != null) {

							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * expiryDate
							 */
							Element VVGinsurerInformation_expiryDateElem = VVGinsurerInformationElem
									.getChild("expiryDate", namespace);
							vvgInsurerInformation.setExpiryDate(VVGinsurerInformation_expiryDateElem.getValue() != null
									? VVGinsurerInformation_expiryDateElem.getValue()
									: "");

						}

						if (VVGinsurerInformationElem.getChild("insurerInformation", namespace) != null) {

							/*
							 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
							 * insurerInformation
							 */
							Element VVGinsurerInformation_insurerInformationElem = VVGinsurerInformationElem
									.getChild("insurerInformation", namespace);

							// vvgInsurerInformation

							if (VVGinsurerInformation_insurerInformationElem.getChild("contactNumberGerman",
									namespace) != null) {

								/*
								 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
								 * insurerInformation/contactNumberGerman
								 */
								Element VVGinsurerInformation_insurerInformation_contactNumberGermanElem = VVGinsurerInformation_insurerInformationElem
										.getChild("contactNumberGerman", namespace);

								if (VVGinsurerInformation_insurerInformation_contactNumberGermanElem
										.getAttribute("number") != null) {
									/*
									 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
									 * insurerInformation/contactNumberGerman[number]
									 */
									Attribute VVGinsurerInformation_insurerInformation_contactNumberGerman_numberElem = VVGinsurerInformation_insurerInformation_contactNumberGermanElem
											.getAttribute("number");

									contactNumberGermanVVG.setNumber(
											VVGinsurerInformation_insurerInformation_contactNumberGerman_numberElem
													.getValue() != null
															? VVGinsurerInformation_insurerInformation_contactNumberGerman_numberElem
																	.getValue()
															: "");

								}

								if (VVGinsurerInformation_insurerInformation_contactNumberGermanElem
										.getAttribute("international-code") != null) {
									/*
									 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
									 * insurerInformation/contactNumberGerman[international-code]
									 */
									Attribute VVGinsurerInformation_insurerInformation_contactNumberGerman_internationalCodeElem = VVGinsurerInformation_insurerInformation_contactNumberGermanElem
											.getAttribute("international-code");

									contactNumberGermanVVG.setInternationalCode(
											VVGinsurerInformation_insurerInformation_contactNumberGerman_internationalCodeElem
													.getValue() != null
															? VVGinsurerInformation_insurerInformation_contactNumberGerman_internationalCodeElem
																	.getValue()
															: "");
								}

								if (VVGinsurerInformation_insurerInformation_contactNumberGermanElem
										.getAttribute("local-code") != null) {
									/*
									 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
									 * insurerInformation/contactNumberGerman[local-code]
									 */
									Attribute VVGinsurerInformation_insurerInformation_contactNumberGerman_localCodeElem = VVGinsurerInformation_insurerInformation_contactNumberGermanElem
											.getAttribute("local-code");

									contactNumberGermanVVG.setLocalCode(
											VVGinsurerInformation_insurerInformation_contactNumberGerman_localCodeElem
													.getValue());
								}
								insurerInformationVVG.setContactNumberGerman(contactNumberGermanVVG);
							}

							if (VVGinsurerInformation_insurerInformationElem.getChild("contactEmailAddress",
									namespace) != null) {
								/*
								 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
								 * insurerInformation/contactEmailAddress
								 */
								Element VVGinsurerInformation_insurerInformation_contactEmailAddressElem = VVGinsurerInformation_insurerInformationElem
										.getChild("contactEmailAddress", namespace);

								insurerInformationVVG.setContactEmailAddress(
										VVGinsurerInformation_insurerInformation_contactEmailAddressElem
												.getValue() != null
														? VVGinsurerInformation_insurerInformation_contactEmailAddressElem
																.getValue()
														: "");
							}

							if (VVGinsurerInformation_insurerInformationElem.getChild("contactEanNumber",
									namespace) != null) {

								/*
								 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
								 * insurerInformation/contactEanNumber
								 */
								Element VVGinsurerInformation_insurerInformation_contactEanNumberElem = VVGinsurerInformation_insurerInformationElem
										.getChild("contactEanNumber", namespace);

								insurerInformationVVG.setContactEanNumber(
										VVGinsurerInformation_insurerInformation_contactEanNumberElem.getValue() != null
												? VVGinsurerInformation_insurerInformation_contactEanNumberElem
														.getValue()
												: "");
							}

							if (VVGinsurerInformation_insurerInformationElem.getChild("billingAddress",
									namespace) != null) {

								/*
								 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
								 * insurerInformation/billingAddress
								 */
								Element VVGinsurerInformation_insurerInformation_billingAddressElem = VVGinsurerInformation_insurerInformationElem
										.getChild("billingAddress", namespace);

								if (VVGinsurerInformation_insurerInformation_billingAddressElem.getChild("addressLine1",
										namespace) != null) {
									/*
									 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
									 * insurerInformation/billingAddress/addressLine1
									 */
									Element VVGinsurerInformation_insurerInformation_billingAddress_addressLine1Elem = VVGinsurerInformation_insurerInformation_billingAddressElem
											.getChild("addressLine1", namespace);

									vvgBillingAddress.setAddressLine1(
											VVGinsurerInformation_insurerInformation_billingAddress_addressLine1Elem
													.getValue() != null
															? VVGinsurerInformation_insurerInformation_billingAddress_addressLine1Elem
																	.getValue()
															: "");
								}
								if (VVGinsurerInformation_insurerInformation_billingAddressElem.getChild("town",
										namespace) != null) {
									/*
									 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
									 * insurerInformation/billingAddress/town
									 */
									Element VVGinsurerInformation_insurerInformation_billingAddress_townElem = VVGinsurerInformation_insurerInformation_billingAddressElem
											.getChild("town", namespace);

									vvgBillingAddress
											.setTown(VVGinsurerInformation_insurerInformation_billingAddress_townElem
													.getValue() != null
															? VVGinsurerInformation_insurerInformation_billingAddress_townElem
																	.getValue()
															: "");
								}
								if (VVGinsurerInformation_insurerInformation_billingAddressElem.getChild("swissZipCode",
										namespace) != null) {
									/*
									 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
									 * insurerInformation/billingAddress/swissZipCode
									 */
									Element VVGinsurerInformation_insurerInformation_billingAddress_swissZipCodeElem = VVGinsurerInformation_insurerInformation_billingAddressElem
											.getChild("swissZipCode", namespace);

									vvgBillingAddress.setSwissZipCode(
											VVGinsurerInformation_insurerInformation_billingAddress_swissZipCodeElem
													.getValue() != null
															? VVGinsurerInformation_insurerInformation_billingAddress_swissZipCodeElem
																	.getValue()
															: "");
								}
								if (VVGinsurerInformation_insurerInformation_billingAddressElem.getChild("country",
										namespace) != null) {

									/*
									 * CH-Baseinformation/nationalExtension/VVGInformation/VVGinsurerInformation/
									 * insurerInformation/billingAddress/country
									 */
									Element VVGinsurerInformation_insurerInformation_billingAddress_countryElem = VVGinsurerInformation_insurerInformation_billingAddressElem
											.getChild("country", namespace);

									vvgBillingAddress.setCountry(
											VVGinsurerInformation_insurerInformation_billingAddress_countryElem
													.getValue() != null
															? VVGinsurerInformation_insurerInformation_billingAddress_countryElem
																	.getValue()
															: "");
								}
							}
						}
					}
					insurerInformationVVG.setBillingAddress(vvgBillingAddress);
					vvgInsurerInformation.setInsurerInformation(insurerInformationVVG);

					if (VVGInformationElem.getChild("medicationHL", namespace) != null) {

						vvgInformation.setMedicationHL(
								VVGInformationElem.getChild("medicationHL", namespace).getValue() != null
										? VVGInformationElem.getChild("medicationHL", namespace).getValue()
										: "");
					}

					if (VVGInformationElem.getChild("medicationAccidentCoverageHL", namespace) != null) {
						/*
						 * CH-Baseinformation/nationalExtension/VVGInformation/
						 * medicationAccidentCoverageHL
						 */
						Element VVGInformation_medicationAccidentCoverageHLElem = VVGInformationElem
								.getChild("medicationAccidentCoverageHL", namespace);

						vvgInformation.setMedicationAccidentCoverageHL(
								VVGInformation_medicationAccidentCoverageHLElem.getValue() != null
										? VVGInformation_medicationAccidentCoverageHLElem.getValue()
										: "");
					}

					if (VVGInformationElem.getChild("medicationKM", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/VVGInformation/medicationKM */
						Element VVGInformation_medicationKMElem = VVGInformationElem.getChild("medicationKM",
								namespace);
						vvgInformation.setMedicationKM(VVGInformation_medicationKMElem.getValue() != null
								? VVGInformation_medicationKMElem.getValue()
								: "");
					}

					if (VVGInformationElem.getChild("medicationAccidentCoverageKM", namespace) != null) {
						/*
						 * CH-Baseinformation/nationalExtension/VVGInformation/
						 * medicationAccidentCoverageKM
						 */
						Element VVGInformation_medicationAccidentCoverageKMElem = VVGInformationElem
								.getChild("medicationAccidentCoverageKM", namespace);
						vvgInformation.setMedicationAccidentCoverageKM(
								VVGInformation_medicationAccidentCoverageKMElem.getValue() != null
										? VVGInformation_medicationAccidentCoverageKMElem.getValue()
										: "");
					}

					if (VVGInformationElem.getChild("medicalServiceCoverageVVG", namespace) != null) {
						/*
						 * CH-Baseinformation/nationalExtension/VVGInformation/medicalServiceCoverageVVG
						 */
						Element VVGInformation_medicalServiceCoverageVVGElem = VVGInformationElem
								.getChild("medicalServiceCoverageVVG", namespace);

						vvgInformation.setMedicalServiceCoverageVVG(
								VVGInformation_medicalServiceCoverageVVGElem.getValue() != null
										? VVGInformation_medicalServiceCoverageVVGElem.getValue()
										: "");
					}

					if (VVGInformationElem.getChild("hospitalModelBedVVG", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/VVGInformation/hospitalModelBedVVG */
						Element VVGInformation_hospitalModelBedVVGElem = VVGInformationElem
								.getChild("hospitalModelBedVVG", namespace);

						vvgInformation.setHospitalModelBedVVG(VVGInformation_hospitalModelBedVVGElem.getValue() != null
								? VVGInformation_hospitalModelBedVVGElem.getValue()
								: "");
					}

					if (VVGInformationElem.getChild("hospitalModelDocVVG", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/VVGInformation/hospitalModelDocVVG */
						Element VVGInformation_hospitalModelDocVVGElem = VVGInformationElem
								.getChild("hospitalModelDocVVG", namespace);
						vvgInformation.setHospitalModelDocVVG(VVGInformation_hospitalModelDocVVGElem.getValue() != null
								? VVGInformation_hospitalModelDocVVGElem.getValue()
								: "");
					}

					if (VVGInformationElem.getChild("accidentCoverageVVG", namespace) != null) {
						/* CH-Baseinformation/nationalExtension/VVGInformation/accidentCoverageVVG */
						Element VVGInformation_accidentCoverageVVGElem = VVGInformationElem
								.getChild("accidentCoverageVVG", namespace);
						vvgInformation.setAccidentCoverageVVG(VVGInformation_accidentCoverageVVGElem.getValue() != null
								? VVGInformation_accidentCoverageVVGElem.getValue()
								: "");
					}

					// vvgInformation
					vvgInformation.setVvgInsurerInformation(vvgInsurerInformation);

				}
			}

			if (chBaseInformationElem.getChild("ofacExtension", namespace) != null) {
				/* CH-Baseinformation/ofacExtension */
				Element ofacExtensionElem = chBaseInformationElem.getChild("ofacExtension", namespace);

				if (ofacExtensionElem.getChild("zusatzKVGModel", namespace) != null) {

					/* CH-Baseinformation/ofacExtension/zusatzKVGModel */
					Element ofacExtension_zusatzKVGModelElem = ofacExtensionElem.getChild("zusatzKVGModel", namespace);

					if (ofacExtension_zusatzKVGModelElem.getChild("code", namespace) != null) {

						/* CH-Baseinformation/ofacExtension/zusatzKVGModel/code */
						Element ofacExtension_zusatzKVGModel_codeElem = ofacExtension_zusatzKVGModelElem
								.getChild("code", namespace);

						additionalKVGModel.setCode(ofacExtension_zusatzKVGModel_codeElem.getValue() != null
								? ofacExtension_zusatzKVGModel_codeElem.getValue()
								: "");

					}
				}

				if (ofacExtensionElem.getChild("debNomAddress", namespace) != null) {
					if (ofacExtensionElem.getChild("officialName", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/debNomAddress/officialName */
						Element ofacExtension_debNomAddress_officialNameElem = ofacExtensionElem
								.getChild("officialName", namespace);
						debNomAddress.setOfficialName(ofacExtension_debNomAddress_officialNameElem.getValue() != null
								? ofacExtension_debNomAddress_officialNameElem.getValue()
								: "");

					}

					if (ofacExtensionElem.getChild("firstName", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/debNomAddress/firstName */
						Element ofacExtension_debNomAddress_firstNameElem = ofacExtensionElem.getChild("firstName",
								namespace);
						debNomAddress.setFirstName(ofacExtension_debNomAddress_firstNameElem.getValue() != null
								? ofacExtension_debNomAddress_firstNameElem.getValue()
								: "");
					}
				}

				if (ofacExtensionElem.getChild("debNomAddress", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/debmailAddress */
					Element ofacExtension_debmailAddressElem = ofacExtensionElem.getChild("debmailAddress", namespace);

					if (ofacExtension_debmailAddressElem.getChild("addressLine1", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/debmailAddress/addressLine1 */
						Element ofacExtension_debmailAddress_addressLine1Elem = ofacExtension_debmailAddressElem
								.getChild("addressLine1", namespace);
						debmailAddress.setAddressLine1(ofacExtension_debmailAddress_addressLine1Elem.getValue() != null
								? ofacExtension_debmailAddress_addressLine1Elem.getValue()
								: "");
					}
					if (ofacExtension_debmailAddressElem.getChild("town", namespace) != null) {

						/* CH-Baseinformation/ofacExtension/debmailAddress/town */
						Element ofacExtension_debmailAddress_townElem = ofacExtension_debmailAddressElem
								.getChild("town", namespace);
						debmailAddress.setTown(ofacExtension_debmailAddress_townElem.getValue() != null
								? ofacExtension_debmailAddress_townElem.getValue()
								: "");
					}
					if (ofacExtension_debmailAddressElem.getChild("swissZipCode", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/debmailAddress/swissZipCode */
						Element ofacExtension_debmailAddress_swissZipCodeElem = ofacExtension_debmailAddressElem
								.getChild("swissZipCode", namespace);
						debmailAddress.setSwissZipCode(ofacExtension_debmailAddress_swissZipCodeElem.getValue() != null
								? ofacExtension_debmailAddress_swissZipCodeElem.getValue()
								: "");
					}
					if (ofacExtension_debmailAddressElem.getChild("country", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/debmailAddress/swissZipCode */
						Element ofacExtension_debmailAddress_countryElem = ofacExtension_debmailAddressElem
								.getChild("country", namespace);
						debmailAddress.setCountry(ofacExtension_debmailAddress_countryElem.getValue() != null
								? ofacExtension_debmailAddress_countryElem.getValue()
								: "");
					}
				}

				if (ofacExtensionElem.getChild("agenceKVGName", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/agenceKVGName */
					Element ofacExtension_agenceKVGNameElem = ofacExtensionElem.getChild("agenceKVGName", namespace);
					ofacExtension.setAgenceKVGName(ofacExtension_agenceKVGNameElem.getValue() != null
							? ofacExtension_agenceKVGNameElem.getValue()
							: "");
				}

				if (ofacExtensionElem.getChild("agenceKVGAddress", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/agenceKVGAddress */
					Element ofacExtension_agenceKVGAddressElem = ofacExtensionElem.getChild("agenceKVGAddress",
							namespace);
					if (ofacExtension_agenceKVGAddressElem.getChild("addressLine1", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceKVGAddress/addressLine1 */
						Element ofacExtension_agenceKVGAddress_addressLine1Elem = ofacExtension_agenceKVGAddressElem
								.getChild("addressLine1", namespace);
						agenceKVGAddress
								.setAddressLine1(ofacExtension_agenceKVGAddress_addressLine1Elem.getValue() != null
										? ofacExtension_agenceKVGAddress_addressLine1Elem.getValue()
										: "");
					}

					if (ofacExtension_agenceKVGAddressElem.getChild("town", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceKVGAddress/town */
						Element ofacExtension_agenceKVGAddress_townElem = ofacExtension_agenceKVGAddressElem
								.getChild("town", namespace);
						agenceKVGAddress.setTown(ofacExtension_agenceKVGAddress_townElem.getValue() != null
								? ofacExtension_agenceKVGAddress_townElem.getValue()
								: "");
					}

					if (ofacExtension_agenceKVGAddressElem.getChild("swissZipCode", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceKVGAddress/swissZipCode */
						Element ofacExtension_agenceKVGAddress_swissZipCodeElem = ofacExtension_agenceKVGAddressElem
								.getChild("swissZipCode", namespace);
						agenceKVGAddress
								.setSwissZipCode(ofacExtension_agenceKVGAddress_swissZipCodeElem.getValue() != null
										? ofacExtension_agenceKVGAddress_swissZipCodeElem.getValue()
										: "");
					}

					if (ofacExtension_agenceKVGAddressElem.getChild("country", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceKVGAddress/country */
						Element ofacExtension_agenceKVGAddress_countryElem = ofacExtension_agenceKVGAddressElem
								.getChild("country", namespace);
						agenceKVGAddress.setCountry(ofacExtension_agenceKVGAddress_countryElem.getValue() != null
								? ofacExtension_agenceKVGAddress_countryElem.getValue()
								: "");
					}
				}

				if (ofacExtensionElem.getChild("agenceVVGName", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/agenceVVGName */
					Element ofacExtension_agenceVVGNameElem = ofacExtensionElem.getChild("agenceVVGName", namespace);
					ofacExtension.setAgenceVVGName(ofacExtension_agenceVVGNameElem.getValue() != null
							? ofacExtension_agenceVVGNameElem.getValue()
							: "");
				}

				if (ofacExtensionElem.getChild("agenceVVGAddress", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/agenceVVGAddress */
					Element ofacExtension_agenceVVGAddressElem = ofacExtensionElem.getChild("agenceVVGAddress",
							namespace);

					if (ofacExtension_agenceVVGAddressElem.getChild("addressLine1", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceVVGAddress/addressLine1 */
						Element ofacExtension_agenceVVGAddress_addressLine1Elem = ofacExtension_agenceVVGAddressElem
								.getChild("addressLine1", namespace);
						agenceVVGAddress
								.setAddressLine1(ofacExtension_agenceVVGAddress_addressLine1Elem.getValue() != null
										? ofacExtension_agenceVVGAddress_addressLine1Elem.getValue()
										: "");
					}

					if (ofacExtension_agenceVVGAddressElem.getChild("town", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceVVGAddress/town */
						Element ofacExtension_agenceVVGAddress_townElem = ofacExtension_agenceVVGAddressElem
								.getChild("town", namespace);
						agenceVVGAddress.setTown(ofacExtension_agenceVVGAddress_townElem.getValue() != null
								? ofacExtension_agenceVVGAddress_townElem.getValue()
								: "");
					}

					if (ofacExtension_agenceVVGAddressElem.getChild("swissZipCode", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceVVGAddress/swissZipCode */
						Element ofacExtension_agenceVVGAddress_swissZipCodeElem = ofacExtension_agenceVVGAddressElem
								.getChild("swissZipCode", namespace);
						agenceVVGAddress
								.setSwissZipCode(ofacExtension_agenceVVGAddress_swissZipCodeElem.getValue() != null
										? ofacExtension_agenceVVGAddress_swissZipCodeElem.getValue()
										: "");
					}

					if (ofacExtension_agenceVVGAddressElem.getChild("country", namespace) != null) {
						/* CH-Baseinformation/ofacExtension/agenceVVGAddress/country */
						Element ofacExtension_agenceVVGAddress_countryElem = ofacExtension_agenceVVGAddressElem
								.getChild("country", namespace);
						agenceVVGAddress.setCountry(ofacExtension_agenceVVGAddress_countryElem.getValue() != null
								? ofacExtension_agenceVVGAddress_countryElem.getValue()
								: "");
					}
				}
				if (ofacExtensionElem.getChild("medicalServiceCoverageRestriction", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/medicalServiceCoverageRestriction */
					Element ofacExtension_medicalServiceCoverageRestrictionElem = ofacExtensionElem
							.getChild("medicalServiceCoverageRestriction", namespace);
					ofacExtension.setMedicalServiceCoverageRestriction(
							ofacExtension_medicalServiceCoverageRestrictionElem.getValue() != null
									? ofacExtension_medicalServiceCoverageRestrictionElem.getValue()
									: "");
				}

				if (ofacExtensionElem.getChild("validityDate", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/validityDate */
					Element ofacExtension_validityDateElem = ofacExtensionElem.getChild("validityDate", namespace);
					ofacExtension.setValidityDate(ofacExtension_validityDateElem.getValue() != null
							? ofacExtension_validityDateElem.getValue()
							: "");
				}

				if (ofacExtensionElem.getChild("informationVVGSpitalText", namespace) != null) {
					/* CH-Baseinformation/ofacExtension/informationVVGSpitalText */
					Element ofacExtension_informationVVGSpitalTextElem = ofacExtensionElem
							.getChild("informationVVGSpitalText", namespace);
					ofacExtension
							.setInformationVVGSpitalText(ofacExtension_informationVVGSpitalTextElem.getValue() != null
									? ofacExtension_informationVVGSpitalTextElem.getValue()
									: "");
				}

			}
		} else {
			throw new InvalidCardException("The used card is invalid");
		}

		nationalExtension.setVvgInformation(vvgInformation);

		ofacExtension.setAgenceKVGAddress(agenceKVGAddress);
		ofacExtension.setAgenceVVGAddress(agenceVVGAddress);
		ofacExtension.setAttitionalKVGModel(additionalKVGModel);
		ofacExtension.setDebmailAddress(debmailAddress);
		ofacExtension.setDebNomAddress(debNomAddress);

		chBaseInformation.setAdministrativeData(administrativeData);
		chBaseInformation.setIdentificationData(identificationData);
		chBaseInformation.setOfacExtension(ofacExtension);
		chBaseInformation.setNationalExtension(nationalExtension);

		cardInfoData.setAgenceVVGAddress(agenceVVGAddress);
		cardInfoData.setAgenceKVGAddress(agenceKVGAddress);
		cardInfoData.setDebNomAddress(debNomAddress);

		cardInfoData.setOfacExtension(ofacExtension);
		cardInfoData.setContactNumberGermanVVG(contactNumberGermanVVG);
		cardInfoData.setVvgInsurerInformation(vvgInsurerInformation);
		cardInfoData.setVvgInformation(vvgInformation);
		cardInfoData.setContactNumberGermanKVG(contactNumberGermanKVG);
		cardInfoData.setInsurerInformation(insurerInformationKVG);
		cardInfoData.setKvgCanton(kvgCanton);
		cardInfoData.setKvgInformation(kvgInformation);
		cardInfoData.setMailAddress(mailAddress);
		cardInfoData.setNationalExtension(nationalExtension);
		cardInfoData.setAdministrativeData(administrativeData);
		cardInfoData.setDateOfBirth(dateOfBirth);

		cardInfoData.setIdentificationData(identificationData);
		cardInfoData.setChBaseInformation(chBaseInformation);

		return cardInfoData;

	}

	private void init() {
		cardInfoData = new CardInfoData();
		chBaseInformation = cardInfoData.new CHBaseInformation();
		identificationData = cardInfoData.new IdentificationData();
		name = cardInfoData.new Name();
		dateOfBirth = cardInfoData.new DateOfBirth();
		administrativeData = cardInfoData.new AdministrativeData();
		nationalExtension = cardInfoData.new NationalExtension();
		mailAddress = cardInfoData.new MailAddress();
		kvgInformation = cardInfoData.new KVGInformation();
		kvgCanton = cardInfoData.new KVGCanton();
		insurerInformationKVG = cardInfoData.new InsurerInformation();
		insurerInformationVVG = cardInfoData.new InsurerInformation();
		billingAddress = cardInfoData.new BillingAddress();
		vvgBillingAddress = cardInfoData.new BillingAddress();
		vvgInformation = cardInfoData.new VVGInformation();
		vvgInsurerInformation = cardInfoData.new VVGInsurerInformation();
		ofacExtension = cardInfoData.new OfacExtension();
		additionalKVGModel = cardInfoData.new AdditionalKVGModel();
		debNomAddress = cardInfoData.new DebNomAddress();
		debmailAddress = cardInfoData.new DebmailAddress();
		agenceKVGAddress = cardInfoData.new AgenceKVGAddress();
		agenceVVGAddress = cardInfoData.new AgenceVVGAddress();
		contactNumberGermanKVG = cardInfoData.new ContactNumberGerman();
		contactNumberGermanVVG = cardInfoData.new ContactNumberGerman();
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
}
