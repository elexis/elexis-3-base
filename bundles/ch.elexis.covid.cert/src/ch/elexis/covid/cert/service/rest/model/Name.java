package ch.elexis.covid.cert.service.rest.model;

import ch.elexis.core.model.IPatient;

//"name": {
//"familyName": "Federer",
//"givenName": "Roger"
//},

public class Name {

	private String familyName;
	private String givenName;

	public String getFamilyName() {
		return familyName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public static Name of(IPatient patient) {
		Name ret = new Name();
		ret.familyName = patient.getLastName();
		ret.givenName = patient.getFirstName();
		return ret;
	}
}
