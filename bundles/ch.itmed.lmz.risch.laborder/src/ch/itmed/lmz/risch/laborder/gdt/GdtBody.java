/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.gdt;

import ch.itmed.lmz.risch.laborder.data.PatientData;

public final class GdtBody {
	private String number;
	private String lastName;
	private String firstName;
	private String birthDate;
	private String street;
	private String sex;
	private String zip;
	private String city;
	private String country;
	private String costObjectName;
	private String insurancePolicyNumber;
	private String insuranceType;

	private PatientData patientData;

	public GdtBody(String formId) throws UnsupportedOperationException {
		if (formId.equals("012.051.001")) {
			patientData = new PatientData(true);
		} else {
			patientData = new PatientData(false);
		}
		setNumber();
		setLastName();
		setFirstName();
		setBirthDate();
		setStreet();
		setSex();
		setZip();
		setCity();
		setCountry();
		setCostObjectName();
		setInsurancePolicyNumber();
		setInsuranceType();
	}

	private void setNumber() {
		number = gdtFormatter(patientData.getNumber(), "3000");
	}

	private void setLastName() {
		lastName = gdtFormatter(patientData.getLastName(), "3101");
	}

	private void setFirstName() {
		firstName = gdtFormatter(patientData.getFirstName(), "3102");
	}

	private void setBirthDate() {
		birthDate = gdtFormatter(patientData.getBirthDate(), "3103");
	}

	private void setStreet() {
		street = gdtFormatter(patientData.getStreet(), "3107");
	}

	private void setSex() {
		sex = gdtFormatter(patientData.getSex(), "3110");
	}

	private void setZip() {
		zip = gdtFormatter(patientData.getZip(), "3112");
	}

	private void setCity() {
		city = gdtFormatter(patientData.getCity(), "3113");
	}

	private void setCountry() {
		country = gdtFormatter(patientData.getCountry(), "3114");
	}

	private void setCostObjectName() {
		costObjectName = gdtFormatter(patientData.getCostObjectName(), "0202");
	}

	private void setInsurancePolicyNumber() {
		insurancePolicyNumber = gdtFormatter(patientData.getInsurancePolicyNumber(), "3119");
	}

	private void setInsuranceType() {
		insuranceType = gdtFormatter(patientData.getInsuranceType(), "6331");
	}

	public String gdtFormatter(String data, String gdtCode) {
		String gdtSizeMask = "000";
		String payload = gdtCode + data + "\r\n";
		int gdtStringLength = payload.length() + 3;

		String fullGdtString = gdtSizeMask.substring(0,
				gdtSizeMask.length() - Integer.toString(gdtStringLength).length()) + gdtStringLength + payload;
		return fullGdtString;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("0228200Obj_Kopfdaten\r\n");
		sb.append("921803.00\r\n");
		sb.append("01082013\r\n");
		sb.append("0178200Obj_Patient\r\n");
		sb.append(number);
		sb.append(lastName);
		sb.append(firstName);
		sb.append(birthDate);
		sb.append(street);
		sb.append(sex);
		sb.append(zip);
		sb.append(city);
		sb.append(country);
		sb.append("011820111\r\n");
		sb.append("0226330Obj_Kostenträger\r\n");
		sb.append(costObjectName);
		sb.append(insurancePolicyNumber);
		sb.append(insuranceType); // TODO: dynamic values
		sb.append("01082015\r\n");
		sb.append("011820222\r\n");

		return sb.toString();
	}
}
