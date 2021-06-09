package ch.elexis.covid.cert.service.rest.model;

//"dateOfFirstPositiveTestResult": "2021-10-03",
//"countryOfTest": "CH"

public class RecoveryInfo {
	
	private String dateOfFirstPositiveTestResult;
	private String countryOfTest;
	
	public String getDateOfFirstPositiveTestResult(){
		return dateOfFirstPositiveTestResult;
	}
	
	public void setDateOfFirstPositiveTestResult(String dateOfFirstPositiveTestResult){
		this.dateOfFirstPositiveTestResult = dateOfFirstPositiveTestResult;
	}
	
	public String getCountryOfTest(){
		return countryOfTest;
	}
	
	public void setCountryOfTest(String countryOfTest){
		this.countryOfTest = countryOfTest;
	}
}
