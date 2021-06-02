package ch.elexis.covid.cert.service.rest.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import ch.elexis.core.model.IPatient;

//{
//	  "name": {
//	    "familyName": "Federer",
//	    "givenName": "Roger"
//	  },
//	  "dateOfBirth": "1950-06-04",
//	  "language": "de",
//	  "otp": "string",
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
	
	private TestInfo[] testInfo;
	
	public Name getName(){
		return name;
	}
	
	public void setName(Name name){
		this.name = name;
	}
	
	public String getDateOfBirth(){
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth){
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getLanguage(){
		return language;
	}
	
	public void setLanguage(String language){
		this.language = language;
	}
	
	public String getOtp(){
		return otp;
	}
	
	public void setOtp(String otp){
		this.otp = otp;
	}
	
	public TestInfo[] getTestInfo(){
		return testInfo;
	}
	
	public void setTestInfo(TestInfo[] testInfo){
		this.testInfo = testInfo;
	}
	
	public TestModel initDefault(IPatient patient, String otp2){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		setName(Name.of(patient));
		setDateOfBirth(formatter.format(patient.getDateOfBirth()));
		setLanguage(Locale.getDefault().getLanguage().toLowerCase());
		setOtp(otp);
		TestInfo testinfo = new TestInfo();
		// ISO 8601 date incl. time
		testinfo.setSampleDateTime(
			TestInfo.formatter.format(LocalDateTime.now().atOffset(ZoneOffset.UTC)));
		testinfo.setMemberStateOfTest("CH");
		setTestInfo(new TestInfo[] {
			testinfo
		});
		return this;
	}
	
}
