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
package at.medevit.elexis.ehc.core.internal;

import java.io.InputStream;

import org.ehealth_connector.cda.ch.CdaCh;
import org.ehealth_connector.cda.ch.CdaChVacd;
import org.ehealth_connector.cda.ch.enums.LanguageCode;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.ch.CDACH;
import org.openhealthtools.mdht.uml.cda.ch.CHFactory;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.core.EhcCoreService;
import at.medevit.elexis.ehc.core.internal.document.CdaChImpl;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class EhcCoreServiceImpl implements EhcCoreService {
	
	private static Logger logger = LoggerFactory.getLogger(EhcCoreServiceImpl.class);
	
	public EhcCoreServiceImpl(){
		// make sure CDACH is registered and initialized
		CHFactory.eINSTANCE.createCDACH().init();
	}
	
	@Override
	public CdaCh getCdaChDocument(Patient patient, Mandant mandant){
		CdaChImpl ret = new CdaChImpl(CHFactory.eINSTANCE.createCDACH().init());
		
		ret.setPatient(EhcCoreMapper.getEhcPatient(patient));
		ret.addAuthor(EhcCoreMapper.getEhcAuthor(mandant));
		return ret;
	}
	
	@Override
	public CdaCh getDocument(InputStream document){
		ClinicalDocument clinicalDocument;
		try {
			clinicalDocument = CDAUtil.load(document);
			if (clinicalDocument instanceof CDACH) {
				return new CdaChImpl((CDACH) clinicalDocument);
			} else {
				logger.warn("Loaded document is not a subclass of CDACH.");
			}
		} catch (Exception e) {
			logger.warn("Error loading document.", e);
		}
		return null;
	}
	
	@Override
	public void importPatient(org.ehealth_connector.common.Patient ehcPatient){
		Patient patient = EhcCoreMapper.getElexisPatient(ehcPatient);
		EhcCoreMapper.importEhcAddress(patient, ehcPatient.getAddress());
		EhcCoreMapper.importEhcPhone(patient, ehcPatient.getTelecoms());
	}
	
	@Override
	public CdaChVacd getVaccinationsDocument(Patient elexisPatient, Mandant elexisMandant){
		// Create eVACDOC (Header)
		CdaChVacd doc = new CdaChVacd(LanguageCode.GERMAN, null, null);
		doc.setPatient(EhcCoreMapper.getEhcPatient(elexisPatient));
		doc.setCustodian(EhcCoreMapper.getEhcOrganization(elexisMandant));
		doc.addAuthor(EhcCoreMapper.getEhcAuthor(elexisMandant));
		doc.setLegalAuthenticator(EhcCoreMapper.getEhcAuthor(elexisMandant));
		
		return doc;
	}
}
