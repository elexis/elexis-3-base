package ch.elexis.covid.cert.service.rest.model;

import java.time.format.DateTimeFormatter;

//"typeCode": "LP217198-3",
//"manufacturerCode": "1065",
//"sampleDateTime": "2020-09-24T17:29:41Z",
//"testingCentreOrFacility": "de",
//"memberStateOfTest": "CH"

public class TestInfo {

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private String typeCode;
	private String manufacturerCode;
	private String sampleDateTime;
	private String testingCentreOrFacility;
	private String memberStateOfTest;

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getManufacturerCode() {
		return manufacturerCode;
	}

	public void setManufacturerCode(String manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	public String getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(String sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public String getTestingCentreOrFacility() {
		return testingCentreOrFacility;
	}

	public void setTestingCentreOrFacility(String testingCentreOrFacility) {
		this.testingCentreOrFacility = testingCentreOrFacility;
	}

	public String getMemberStateOfTest() {
		return memberStateOfTest;
	}

	public void setMemberStateOfTest(String memberStateOfTest) {
		this.memberStateOfTest = memberStateOfTest;
	}
}
