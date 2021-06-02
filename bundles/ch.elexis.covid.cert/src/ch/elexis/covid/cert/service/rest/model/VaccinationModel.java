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
//	  "vaccinationInfo": [
//	    {
//	      "medicinalProductCode": "68267",
//	      "numberOfDoses": 2,
//	      "totalNumberOfDoses": 2,
//	      "vaccinationDate": "2021-05-14",
//	      "countryOfVaccination": "CH"
//	    }
//	  ]
//	}

public class VaccinationModel {
	
	private Name name;
	
	private String dateOfBirth;
	
	private String language;
	
	private String otp;
	
	private VaccinationInfo[] vaccinationInfo;
	
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
	
	public VaccinationInfo[] getVaccinationInfo(){
		return vaccinationInfo;
	}
	
	public void setVaccinationInfo(VaccinationInfo[] vaccinationInfo){
		this.vaccinationInfo = vaccinationInfo;
	}
	
	public VaccinationModel initDefault(IPatient patient, String otp){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		setName(Name.of(patient));
		setDateOfBirth(
			formatter.format(patient.getDateOfBirth()));
		setLanguage(Locale.getDefault().getLanguage().toLowerCase());
		setOtp(otp);
		VaccinationInfo vaccinfo = new VaccinationInfo();
		vaccinfo.setVaccinationDate(formatter.format(LocalDate.now()));
		vaccinfo.setCountryOfVaccination("CH");
		setVaccinationInfo(new VaccinationInfo[] {
			vaccinfo
		});
		return this;
	}
}
