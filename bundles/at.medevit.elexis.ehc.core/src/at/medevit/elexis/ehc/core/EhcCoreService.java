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
import java.io.OutputStream;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.projecthusky.common.enums.DocumentDescriptor;
import org.projecthusky.common.hl7cdar2.POCDMT000040ClinicalDocument;
import org.projecthusky.communication.xd.xdm.DocumentContentAndMetadata;

import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

/**
 * Service interface for access to convenience methods of the ehealthconnector
 * contained within this bundle.
 *
 * @see <a href=
 *      "https://sourceforge.net/projects/ehealthconnector/">ehealthconnector</a>
 * @author thomas
 *
 */
public interface EhcCoreService {

	/**
	 * Load a {@link POCDMT000040ClinicalDocument} implementation from the provided
	 * {@link InputStream}.
	 *
	 * @param documentStream
	 * @return
	 */
	public POCDMT000040ClinicalDocument loadDocument(InputStream documentStream);

	/**
	 * Save a {@link POCDMT000040ClinicalDocument} to the provided
	 * {@link OutputStream}.
	 * 
	 * @param cdaDocument
	 * @param outputStream
	 */
	public void saveDocument(POCDMT000040ClinicalDocument cdaDocument, OutputStream outputStream);

	/**
	 * Search for a matching Patient, or create a new Elexis {@link Patient} with
	 * the data from the {@link org.projecthusky.common.model.Patient} provided.
	 *
	 * @param selectedPatient
	 */
	public Patient getOrCreatePatient(org.projecthusky.common.model.Patient selectedPatient);

	/**
	 * Create a {@link POCDMT000040ClinicalDocument}, containing the provided
	 * {@link Patient} and {@link Mandant} as author. Only useful if no specific
	 * Document is needed.
	 *
	 * @param patient
	 * @param mandant
	 * @return
	 */
	public POCDMT000040ClinicalDocument createDocument(Patient patient, Mandant mandant);

	/**
	 * Create a XDM as stream, with the provided
	 * {@link POCDMT000040ClinicalDocument} as content.
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public InputStream getXdmAsStream(POCDMT000040ClinicalDocument document) throws Exception;

	/**
	 * Create a XDM as stream, with the provided {@link Bundle} as content.
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public InputStream getXdmAsStream(Bundle document) throws Exception;

	/**
	 * Get all {@link ClinicalDocument} instances from a XDM file.
	 *
	 * @param file
	 * @return
	 */
	public List<DocumentContentAndMetadata> getXdmDocuments(File file);

	/**
	 * Get all {@link org.projecthusky.common.model.Patient} instances from a XDM
	 * file.
	 *
	 * @param file
	 * @return
	 */
	public List<org.projecthusky.common.model.Patient> getXdmPatients(File file);

	/**
	 * Creates a new xdm container with the files for a patient
	 *
	 * Since {@link DocumentDescriptor} are generated of attachments only the types
	 * CDA_R2 and PDF are mainly supported for other types the file mimetype and the
	 * file extension will be evaluated for creating a new
	 * {@link DocumentDescriptor}.
	 *
	 * @param patient
	 * @param mandant
	 * @param attachments
	 * @param xdmPath
	 * @return returns a ':::' separated string of the xdm file and at least one
	 *         attachment or returns null
	 */
	public String createXdmContainer(Patient patient, Mandant mandant, List<File> attachments, String xdmPath);

	/**
	 * Checks if the file is a cda document
	 *
	 * @param path
	 * @return
	 */
	public boolean isCdaDocument(File file);

	/**
	 * Get the {@link org.projecthusky.common.model.Patient} from the
	 * {@link POCDMT000040ClinicalDocument}.
	 * 
	 * @param document
	 * @return
	 */
	public org.projecthusky.common.model.Patient getPatient(POCDMT000040ClinicalDocument document);

	public POCDMT000040ClinicalDocument getDocument(DocumentContentAndMetadata metadata);

}
