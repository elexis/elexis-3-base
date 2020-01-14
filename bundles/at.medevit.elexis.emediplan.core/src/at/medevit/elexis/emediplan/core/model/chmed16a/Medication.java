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

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IXid;
import ch.elexis.data.Prescription;

public class Medication {
	public Patient Patient;
	public List<Medicament> Medicaments;
	public List<Recommendation> Recoms;
	public List<PrivateField> PFields;
	public String PSchema;
	public int MedType;
	public String Id;
	public String Auth;
	public String Zsr;
	public String Dt;
	public String Rmk;
	public String ValBy;
	public String ValDt;
	
	public transient String chunk;
	
	/**
	 * Create a complete CHMED16A model from the provided {@link Prescription} of one
	 * {@link IPatient}.
	 * 
	 * @param prescriptions
	 * @param addDesc
	 * @return
	 */
	public static Medication fromPrescriptions(@NonNull IMandator author, @NonNull IPatient patient,
		@NonNull List<IPrescription> prescriptions, boolean addDesc){
		Medication ret = new Medication();
		ret.MedType = 1;
		ret.Id = UUID.randomUUID().toString();
		ret.Dt = LocalDateTime.now().toString();
		IXid gln = author.getXid(DOMAIN_EAN);
		if (gln != null) {
			if (gln.getDomainId() != null && !gln.getDomainId().isEmpty()) {
				ret.Auth = gln.getDomainId();
			}
		} else {
			ret.Auth = author.getLabel();
		}
		if (!prescriptions.isEmpty()) {
			ret.Patient =
				at.medevit.elexis.emediplan.core.model.chmed16a.Patient.fromPatient(patient);
			ret.Medicaments = Medicament.fromPrescriptions(prescriptions, addDesc);
		}
		return ret;
	}
	
	public String getNamedBlobId(){
		if (Id == null) {
			throw new IllegalStateException("id cannot be null");
		}
		return "Med_" + Id;
	}
}
