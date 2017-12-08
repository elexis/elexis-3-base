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
package at.medevit.elexis.emediplan.core.model.print;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.elexis.data.Anwender;
import ch.elexis.data.Person;
import ch.elexis.data.Prescription;

@XmlRootElement(name = "medicament")
@XmlAccessorType(XmlAccessType.FIELD)
public class Medicament {
	public String name;
	
	public String dosageText;
	
	public String dosageMorning;
	public String dosageNoon;
	public String dosageEvening;
	public String dosageNight;
	
	public String unit;
	public String type;
	
	public String startDate;
	public String endDate;
	
	public String remarks;
	public String reason;
	public String prescriptor;
	
	public static Medicament fromPrescription(Prescription prescription){
		Medicament ret = new Medicament();
		ret.name = prescription.getArtikel().getLabel();
		
		String[] signature = Prescription.getSignatureAsStringArray(prescription.getDosis());
		boolean isFreetext = !signature[0].isEmpty() && signature[1].isEmpty()
			&& signature[2].isEmpty() && signature[3].isEmpty();
		if (isFreetext) {
			ret.dosageText = signature[0];
		} else {
			ret.dosageMorning = signature[0];
			ret.dosageNoon = signature[1];
			ret.dosageEvening = signature[2];
			ret.dosageNight = signature[3];
			ret.type = "täglich";
		}
		ret.startDate = prescription.getBeginDate();
		ret.endDate = prescription.getEndDate();
		
		ret.remarks = prescription.getBemerkung();
		ret.reason = prescription.getDisposalComment();
		
		String prescriptorId = prescription.get(Prescription.FLD_PRESCRIPTOR);
		ret.prescriptor = getPrescriptorLabel(prescriptorId);
		return ret;
	}
	
	private static String getPrescriptorLabel(String prescriptorId){
		if (prescriptorId != null && !prescriptorId.isEmpty()) {
			Anwender prescriptor = Anwender.load(prescriptorId);
			if (prescriptor != null && prescriptor.exists()) {
				String title = prescriptor.get(Person.TITLE);
				String firstname = prescriptor.get(Person.FLD_NAME2);
				String lastname = prescriptor.get(Person.FLD_NAME1);
				return ((title != null && !title.isEmpty()) ? title + " " : "")
					+ ((firstname != null && !firstname.isEmpty()) ? firstname + " " : "")
					+ ((lastname != null && !lastname.isEmpty()) ? lastname + " " : "");
			}
		}
		return null;
	}
}
