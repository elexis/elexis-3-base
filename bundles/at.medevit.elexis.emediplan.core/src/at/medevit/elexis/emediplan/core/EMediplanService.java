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
package at.medevit.elexis.emediplan.core;

import java.io.OutputStream;
import java.util.List;

import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import at.medevit.elexis.emediplan.core.model.chmed16a.Posology;
import at.medevit.elexis.emediplan.core.model.chmed16a.PrivateField;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;

/**
 * Service interface providing methods to import and export an eMediplabn
 * (http://emediplan.ch/de/home) with Elexis.
 * 
 * @author thomas
 *
 */
public interface EMediplanService {
	
	/**
	 * Get a PDF eMediplan (http://emediplan.ch/de/home) representation of the prescriptions of the
	 * patient, written to the provided {@link OutputStream}.
	 * 
	 * @param author
	 * @param patient
	 * @param prescriptions
	 * @param output
	 */
	public default void exportEMediplanPdf(Mandant author, Patient patient, List<Prescription> prescriptions,
			OutputStream output) {
		exportEMediplanPdf(author, patient, prescriptions, false, output);
	}

	/**
	 * Get a PDF eMediplan (http://emediplan.ch/de/home) representation of the
	 * prescriptions of the patient, written to the provided {@link OutputStream}.
	 * If addDesc is true a desc private field is added to all medication entries of
	 * the JSON emediplan.
	 * 
	 * @param author
	 * @param patient
	 * @param prescriptions
	 * @param addDesc
	 * @param output
	 */
	public void exportEMediplanPdf(Mandant author, Patient patient,
			List<Prescription> prescriptions, boolean addDesc, OutputStream output);

	/**
	 * Get a CHMED json eMediplan (http://emediplan.ch/de/home) representation of the prescriptions
	 * of the patient, written to the provided {@link OutputStream}.
	 * 
	 * @param author
	 * @param patient
	 * @param prescriptions
	 * @param output
	 */
	public default void exportEMediplanJson(Mandant author, Patient patient, List<Prescription> prescriptions,
			OutputStream output) {
		exportEMediplanJson(author, patient, prescriptions, false, output);
	}

	/**
	 * Get a CHMED json eMediplan (http://emediplan.ch/de/home) representation of
	 * the prescriptions of the patient, written to the provided
	 * {@link OutputStream}. If addDesc is true a desc private field is added to all
	 * medication entries of the JSON emediplan.
	 * 
	 * @param author
	 * @param patient
	 * @param prescriptions
	 * @param addDesc
	 * @param output
	 */
	public void exportEMediplanJson(Mandant author, Patient patient,
			List<Prescription> prescriptions, boolean addDesc, OutputStream output);
	
	/**
	 * Get a CHMED eMediplan (http://emediplan.ch/de/home) representation of the prescriptions of
	 * the patient, written to the provided {@link OutputStream}. If addDesc is true a desc private
	 * field is added to all medication entries of the JSON emediplan.
	 * 
	 * @param author
	 * @param patient
	 * @param prescriptions
	 * @param addDesc
	 * @param output
	 */
	public void exportEMediplanChmed(IMandator author, IPatient patient,
		List<IPrescription> prescriptions, boolean addDesc, OutputStream output);
	
	/**
	 * Creates a model representation from a base 64 compressed json chunk
	 * 
	 * @param chunk
	 * @return
	 */
	public Medication createModelFromChunk(String chunk);
	
	/**
	 * Search for {@link ArtikelstammItem} by EAN or PharamaCode and creates for each
	 * {@link Posology} a new {@link Medication} entry with a single {@link Posology}
	 * 
	 * @param medication
	 * @return
	 */
	public void addExistingArticlesToMedication(Medication medication);
	
	/**
	 * Set all existing {@link Prescription} for a {@link Medication}
	 * 
	 * @param medication
	 * @param medicament
	 * @return
	 */
	public void setPresciptionsToMedicament(Medication medication,
		Medicament medicament);
	
	/**
	 * Adds the medication to the Inbox
	 * 
	 * @param medication
	 * @param mandant
	 * @return
	 */
	public boolean createInboxEntry(Medication medication, Mandant mandant);
	
	/**
	 * Get a private field value from the provided {@link Medicament} with matching
	 * name.
	 * 
	 * @param medicament
	 * @param name
	 * @return
	 */
	public default String getPFieldValue(Medicament medicament, String name) {
		if (medicament != null && medicament.PFields != null && name != null) {
			for (PrivateField pField : medicament.PFields) {
				if (name.equals(pField.Nm)) {
					return pField.Val;
				}
			}
		}
		return null;
	}
}
