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

import org.apache.commons.lang3.StringUtils;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import ch.elexis.core.model.IPrescription;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.elexis.data.Anwender;
import ch.elexis.data.Person;

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

	public static Medicament fromPrescription(IPrescription prescription) {
		Medicament ret = new Medicament();
		ret.name = prescription.getArticle().getLabel();

		String[] signature = MedicationServiceHolder.get()
				.getSignatureAsStringArray(prescription.getDosageInstruction());
		boolean isFreetext = !signature[0].isEmpty() && signature[1].isEmpty() && signature[2].isEmpty()
				&& signature[3].isEmpty();
		if (isFreetext) {
			String raw = prescription.getDosageInstruction();
			if (raw == null) {
				raw = signature[0];
			}
			ret.dosageText = raw;
		} else {
			ret.dosageMorning = signature[0];
			ret.dosageNoon = signature[1];
			ret.dosageEvening = signature[2];
			ret.dosageNight = signature[3];
			ret.type = "täglich";
		}
		ret.startDate = prescription.getDateFrom() != null
				? DateTimeFormatter.ofPattern("dd.MM.yyyy").format(prescription.getDateFrom()) //$NON-NLS-1$
				: StringUtils.EMPTY;
		ret.endDate = prescription.getDateTo() != null
				? DateTimeFormatter.ofPattern("dd.MM.yyyy").format(prescription.getDateTo()) //$NON-NLS-1$
				: StringUtils.EMPTY;
		ret.remarks = prescription.getRemark();
		ret.reason = prescription.getDisposalComment();

		String prescriptorId = prescription.getPrescriptor() != null ? prescription.getPrescriptor().getId()
				: StringUtils.EMPTY;
		ret.prescriptor = getPrescriptorLabel(prescriptorId);
		return ret;
	}

	private static String getPrescriptorLabel(String prescriptorId) {
		if (prescriptorId != null && !prescriptorId.isEmpty()) {
			Anwender prescriptor = Anwender.load(prescriptorId);
			if (prescriptor != null && prescriptor.exists()) {
				String title = prescriptor.get(Person.TITLE);
				String firstname = prescriptor.get(Person.FLD_NAME2);
				String lastname = prescriptor.get(Person.FLD_NAME1);
				return ((title != null && !title.isEmpty()) ? title + StringUtils.SPACE : StringUtils.EMPTY)
						+ ((firstname != null && !firstname.isEmpty()) ? firstname + StringUtils.SPACE
								: StringUtils.EMPTY)
						+ ((lastname != null && !lastname.isEmpty()) ? lastname + StringUtils.SPACE
								: StringUtils.EMPTY);
			}
		}
		return null;
	}
}
