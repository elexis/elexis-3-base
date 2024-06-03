package ch.framsteg.elexis.covercard.control;

import java.util.Properties;

import ch.framsteg.elexis.covercard.dao.CardInfoData;
import ch.framsteg.elexis.covercard.dao.PatientInfoData;

public class PatientInfoRetriever {

	private CardInfoData cardInfoData;
	private Properties applicationProperties;

	private static final String MALE_NUM = "male.num";
	private static final String MALE_CODE = "male.code";
	private static final String FEMALE_CODE = "female.code";

	public PatientInfoRetriever(CardInfoData cardInfoData, Properties applicationProperties,
			Properties messagesProperties) {
		this.cardInfoData = cardInfoData;
		this.applicationProperties = applicationProperties;
	}

	public PatientInfoData getPatientInfo() {
		
		PatientInfoData patientInfoData = new PatientInfoData();
		patientInfoData.setName(cardInfoData.getName().getOfficialName());
		patientInfoData.setPrename(cardInfoData.getName().getFirstName());
		patientInfoData.setBirthday(cardInfoData.getDateOfBirth().getYearMonthDay().substring(8, 10) + "."
				+ cardInfoData.getDateOfBirth().getYearMonthDay().substring(5, 7) + "."
				+ cardInfoData.getDateOfBirth().getYearMonthDay().substring(0, 4));
		patientInfoData.setSex(cardInfoData.getIdentificationData().getSex().equalsIgnoreCase(
				applicationProperties.getProperty(MALE_NUM)) ? applicationProperties.getProperty(MALE_CODE)
						: applicationProperties.getProperty(FEMALE_CODE));
		String cardHolderIdentifier = cardInfoData.getIdentificationData().getCardholderIdentifier().substring(0, 3)
				+ "." + cardInfoData.getIdentificationData().getCardholderIdentifier().substring(3, 7) + "."
				+ cardInfoData.getIdentificationData().getCardholderIdentifier().substring(7, 11) + "."
				+ cardInfoData.getIdentificationData().getCardholderIdentifier().substring(11, 13);
		patientInfoData.setCardholderIdentifier(cardHolderIdentifier);
		patientInfoData.setAddress(cardInfoData.getMailAddress().getAddressLine1());
		patientInfoData.setZip(cardInfoData.getMailAddress().getSwissZipCode());
		patientInfoData.setLocation(cardInfoData.getMailAddress().getTown());
		patientInfoData.setOkpBsv(cardInfoData.getAdministrativeData().getIdentificationNumberOfTheInstitution());
		patientInfoData.setOkpEan(cardInfoData.getInsurerInformation().getContactEanNumber());
		patientInfoData.setInsuredNumber(cardInfoData.getAdministrativeData().getInsuredNumber());
		patientInfoData.setCardNumber(cardInfoData.getAdministrativeData().getCoverCardNo());
		patientInfoData.setInsuredPersonNumber(cardInfoData.getAdministrativeData().getInsuredPersonNumber());
		patientInfoData.setVvgBsv(
				cardInfoData.getVvgInformation().getVvgInsurerInformation().getIdentificationNumberOfVVGInsurer());
		patientInfoData.setVvgEan(cardInfoData.getVvgInformation().getVvgInsurerInformation().getInsurerInformation()
				.getContactEanNumber());
		return patientInfoData;
	}
}
