package ch.elexis.covid.cert.service.rest.model;

//"vaccinationInfo": [
//{
//  "medicinalProductCode": "68267",
//  "numberOfDoses": 2,
//  "totalNumberOfDoses": 2,
//  "vaccinationDate": "2021-05-14",
//  "countryOfVaccination": "CH"
//}
//]

public class VaccinationInfo {
	private String medicinalProductCode;
	private Integer numberOfDoses;
	private Integer totalNumberOfDoses;
	private String vaccinationDate;
	private String countryOfVaccination;

	public String getMedicinalProductCode() {
		return medicinalProductCode;
	}

	public void setMedicinalProductCode(String medicinalProductCode) {
		this.medicinalProductCode = medicinalProductCode;
	}

	public Integer getNumberOfDoses() {
		return numberOfDoses;
	}

	public void setNumberOfDoses(Integer numberOfDoses) {
		this.numberOfDoses = numberOfDoses;
	}

	public Integer getTotalNumberOfDoses() {
		return totalNumberOfDoses;
	}

	public void setTotalNumberOfDoses(Integer totalNumberOfDoses) {
		this.totalNumberOfDoses = totalNumberOfDoses;
	}

	public String getVaccinationDate() {
		return vaccinationDate;
	}

	public void setVaccinationDate(String vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}

	public String getCountryOfVaccination() {
		return countryOfVaccination;
	}

	public void setCountryOfVaccination(String countryOfVaccination) {
		this.countryOfVaccination = countryOfVaccination;
	}
}
