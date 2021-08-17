package ch.elexis.covid.cert.service.rest.model;

import java.time.LocalDate;
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
//	  "appCode": "stringstr",
//	  "recoveryInfo": [
//	    {
//	      "dateOfFirstPositiveTestResult": "2021-10-03",
//	      "countryOfTest": "CH"
//	    }
//	  ]
//	}

public class RecoveryModel {
	private Name name;
	
	private String dateOfBirth;
	
	private String language;
	
	private String otp;
	
	private String appCode;
	
	private RecoveryInfo[] recoveryInfo;
	
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
	
	public String getAppCode(){
		return appCode;
	}
	
	public void setAppCode(String appCode){
		this.appCode = appCode;
	}
	
	public RecoveryInfo[] getRecoveryInfo(){
		return recoveryInfo;
	}
	
	public void setRecoveryInfo(RecoveryInfo[] testInfo){
		this.recoveryInfo = testInfo;
	}
	
	public RecoveryModel initDefault(IPatient patient, String otp){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		setName(Name.of(patient));
		setDateOfBirth(formatter.format(patient.getDateOfBirth()));
		setLanguage(Locale.getDefault().getLanguage().toLowerCase());
		setOtp(otp);
		RecoveryInfo recoveryinfo = new RecoveryInfo();
		recoveryinfo.setDateOfFirstPositiveTestResult(formatter.format(LocalDate.now()));
		recoveryinfo.setCountryOfTest("CH");
		setRecoveryInfo(new RecoveryInfo[] {
			recoveryinfo
		});
		return this;
	}
	
}
