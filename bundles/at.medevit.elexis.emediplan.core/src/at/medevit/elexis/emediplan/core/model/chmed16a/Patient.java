/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.core.model.chmed16a;

import java.time.LocalDateTime;
import java.util.List;

import ch.elexis.core.model.IPatient;
import ch.rgw.tools.TimeTool;

public class Patient {
	public String FName;
	public String LName;
	public String BDt;
	public Integer Gender;
	public String Street;
	public String Zip;
	public String City;
	public String Lng;
	public String Phone;
	public String Rcv;
	public List<PatientId> Ids;
	public List<MedicalData> Med;
	public List<PrivateField> PFields;
	public transient String patientId;
	public transient String patientLabel;

	public static Patient fromPatient(IPatient elexisPatient) {
		Patient ret = new Patient();
		ret.FName = elexisPatient.getFirstName();
		ret.LName = elexisPatient.getLastName();
		LocalDateTime dob = elexisPatient.getDateOfBirth();
		if (dob != null) {
			ret.BDt = new TimeTool(dob).toString(TimeTool.DATE_ISO);
		}
		ch.elexis.core.types.Gender gender = elexisPatient.getGender();
		switch (gender) {
		case FEMALE:
			ret.Gender = 2;
			break;
		case MALE:
			ret.Gender = 1;
			break;
		default:
			ret.Gender = null;
		}
		ret.Street = elexisPatient.getStreet();
		ret.Zip = elexisPatient.getZip();
		ret.City = elexisPatient.getCity();
		ret.Lng = "de";
		ret.Phone = elexisPatient.getMobile();
		return ret;
	}
}
