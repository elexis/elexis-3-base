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
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;

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
	public void exportEMediplanPdf(IMandator author, IPatient patient,
		List<IPrescription> prescriptions, OutputStream output);
	
	/**
	 * Get a CHMED json eMediplan (http://emediplan.ch/de/home) representation of the prescriptions
	 * of the patient, written to the provided {@link OutputStream}.
	 * 
	 * @param author
	 * @param patient
	 * @param prescriptions
	 * @param output
	 */
	public void exportEMediplanJson(IMandator author, IPatient patient,
		List<IPrescription> prescriptions, OutputStream output);
	
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
	 * Set all existing {@link IPrescription} for a {@link Medication}
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
	public boolean createInboxEntry(Medication medication, IMandator mandant);
	
}
