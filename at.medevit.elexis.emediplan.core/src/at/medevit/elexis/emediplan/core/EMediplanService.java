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
	 * Get a PDF eMediplan (http://emediplan.ch/de/home) representation of the JSON encoded String,
	 * written to the provided {@link OutputStream}.
	 * 
	 * @param json
	 * @param output
	 */
	public void exportEMediplanPdf(Mandant author, Patient patient,
		List<Prescription> prescriptions, OutputStream output);
	
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
	 * Find all existing {@link Prescription} for a {@link Medication}
	 * 
	 * @param medication
	 * @param medicament
	 * @return
	 */
	public List<Prescription> findPresciptionsByMedicament(Medication medication,
		Medicament medicament);
	
}
