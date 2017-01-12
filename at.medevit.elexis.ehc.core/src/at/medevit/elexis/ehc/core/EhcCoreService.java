/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.core;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.ehealth_connector.cda.ch.AbstractCdaCh;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;

import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

/**
 * Service interface for access to convenience methods of the ehealthconnector contained within this
 * bundle.
 * 
 * @see <a href="https://sourceforge.net/projects/ehealthconnector/">ehealthconnector</a>
 * @author thomas
 *
 */
public interface EhcCoreService {
	
	/**
	 * Load a {@link ClinicalDocument} implementation from the provided {@link InputStream}.
	 * 
	 * @param documentStream
	 * @return
	 */
	public ClinicalDocument loadDocument(InputStream documentStream);
	
	/**
	 * Wrap the {@link ClinicalDocument} in a {@link AbstractCdaCh} implementation. This is useful
	 * for convenient access to basic fields like patient, author, etc.
	 * 
	 * @param document
	 * @return
	 */
	public AbstractCdaCh<?> getAsCdaChDocument(ClinicalDocument document);
	
	/**
	 * Search for a matching Patient, or create a new Elexis {@link Patient} with the data from the
	 * {@link org.ehealth_connector.common.Patient} provided.
	 * 
	 * @param selectedPatient
	 */
	public Patient getOrCreatePatient(org.ehealth_connector.common.Patient selectedPatient);
	
	/**
	 * Create a {@link AbstractCdaCh} implementation, containing the provided {@link Patient} and
	 * {@link Mandant} as author. Only useful if no specific Document is needed.
	 * 
	 * @param patient
	 * @param mandant
	 * @return
	 */
	public AbstractCdaCh<?> createCdaChDocument(Patient patient, Mandant mandant);
	
	/**
	 * Create a XDM as stream, with the provided document as content.
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public InputStream getXdmAsStream(ClinicalDocument document) throws Exception;
	
	/**
	 * Get all {@link ClinicalDocument} instances from a XDM file.
	 * 
	 * @param file
	 * @return
	 */
	public List<ClinicalDocument> getXdmDocuments(File file);
		
	/**
	 * Get all {@link org.ehealth_connector.common.Patient} instances from a XDM file.
	 * 
	 * @param file
	 * @return
	 */
	public List<org.ehealth_connector.common.Patient> getXdmPatients(File file);
}
