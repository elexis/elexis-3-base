package ch.elexis.covid.cert.service.rest.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.covid.cert.service.CertificatesService;

//{
//	  "name": {
//	    "familyName": "Federer",
//	    "givenName": "Roger"
//	  },
//	  "dateOfBirth": "1950-06-04",
//	  "language": "de",
//	  "otp": "string",
//	  "appCode": "stringstr",
//	  "testInfo": [
//	    {
//	      "typeCode": "LP217198-3",
//	      "manufacturerCode": "1065",
//	      "sampleDateTime": "2020-09-24T17:29:41Z",
//	      "testingCentreOrFacility": "de",
//	      "memberStateOfTest": "CH"
//	    }
//	  ]
//	}

public class TestModel {
	private Name name;

	private String dateOfBirth;

	private String language;

	private String otp;

	private String appCode;

	private TestInfo[] testInfo;

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public TestInfo[] getTestInfo() {
		return testInfo;
	}

	public void setTestInfo(TestInfo[] testInfo) {
		this.testInfo = testInfo;
	}

	public TestModel initDefault(IPatient patient, String otp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		setName(Name.of(patient));
		setDateOfBirth(formatter.format(patient.getDateOfBirth()));
		setLanguage(Locale.getDefault().getLanguage().toLowerCase());
		setOtp(otp);
		TestInfo testinfo = new TestInfo();
		// ISO 8601 date incl. time
		ZonedDateTime zonedNow = LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault());
		ZonedDateTime utcDateTime = zonedNow.withZoneSameInstant(ZoneId.of("Z"));
		testinfo.setSampleDateTime(TestInfo.formatter.format(utcDateTime));
		testinfo.setMemberStateOfTest("CH");
		testinfo.setTestingCentreOrFacility(
				ConfigServiceHolder.get().get(CertificatesService.CFG_TESTCENTERNAME, StringUtils.EMPTY));
		setTestInfo(new TestInfo[] { testinfo });
		return this;
	}

}
