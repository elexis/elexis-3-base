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

import org.ehealth_connector.cda.ch.CdaCh;
import org.ehealth_connector.cda.ch.CdaChVacd;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;

import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public interface EhcCoreService {
	
	public ClinicalDocument getDocument(InputStream document);
	
	public void importPatient(org.ehealth_connector.common.Patient selectedPatient);
	
	public CdaCh<?> getCdaChDocument(Patient patient, Mandant mandant);
	
	public CdaCh<?> getCdaChDocument(ClinicalDocument document);
	
	public CdaChVacd getVaccinationsDocument(Patient elexisPatient, Mandant elexisMandant);
	
	public InputStream getXdmAsStream(ClinicalDocument document) throws Exception;
	
	public List<ClinicalDocument> getXdmDocuments(File file,
		org.ehealth_connector.common.Patient patient);
		
	public List<org.ehealth_connector.common.Patient> getXdmPatients(File file);
	
	public Patient getElexisPatient(org.ehealth_connector.common.Patient patient);
}
