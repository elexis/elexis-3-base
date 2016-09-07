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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.ehealth_connector.cda.ch.AbstractCdaCh;
import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.common.enums.LanguageCode;
import org.ehealth_connector.communication.ConvenienceCommunication;
import org.ehealth_connector.communication.DocumentMetadata;
import org.ehealth_connector.communication.xd.xdm.DocumentContentAndMetadata;
import org.ehealth_connector.communication.xd.xdm.XdmContents;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.RecordTarget;
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
	public AbstractCdaCh<?> getCdaChDocument(Patient patient, Mandant mandant){
		CdaChImpl ret = new CdaChImpl(CHFactory.eINSTANCE.createCDACH().init());
		
		ret.setPatient(EhcCoreMapper.getEhcPatient(patient));
		ret.addAuthor(EhcCoreMapper.getEhcAuthor(mandant));
		return ret;
	}
	
	@Override
	public ClinicalDocument getDocument(InputStream document){
		try {
			return CDAUtil.load(document);
		} catch (Exception e) {
			logger.warn("Error loading document.", e);
		}
		return null;
	}
	
	@Override
	public AbstractCdaCh<?> getCdaChDocument(ClinicalDocument clinicalDocument){
		if (clinicalDocument instanceof CDACH) {
			return new CdaChImpl((CDACH) clinicalDocument);
		}
		logger.warn("Document is not a subclass of CDACH.");
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
	
	@Override
	public InputStream getXdmAsStream(ClinicalDocument document) throws Exception{
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		ByteArrayOutputStream outputStream = null;
		// write document and create an InputStream
		outputStream = new ByteArrayOutputStream();
		CDAUtil.save(document, outputStream);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		outputStream.reset();
		// write XDM and create an InputStream
		DocumentMetadata metaData = conCom.addDocument(DocumentDescriptor.CDA_R2, inputStream);
		metaData.setPatient(getPatient(document));
		conCom.createXdmContents(outputStream);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
	
	private org.ehealth_connector.common.Patient getPatient(ClinicalDocument document){
		EList<RecordTarget> targets = document.getRecordTargets();
		if (targets != null && !targets.isEmpty()) {
			if (targets.size() > 1) {
				logger.warn("Document " + document.getTitle() + " has more than one record target");
			}
			return new org.ehealth_connector.common.Patient(EcoreUtil.copy(targets.get(0)));
		}
		throw new IllegalStateException(
			"Document " + document.getTitle() + " has no record target");
	}
	
	@Override
	public List<ClinicalDocument> getXdmDocuments(File file,
		org.ehealth_connector.common.Patient patient){
		List<ClinicalDocument> ret = null;
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		XdmContents contents = conCom.getXdmContents(file.getAbsolutePath());
		if (contents != null) {
			ret = new ArrayList<ClinicalDocument>();
			List<DocumentContentAndMetadata> dataList = contents.getDocumentAndMetadataList();
			for (DocumentContentAndMetadata documentContentAndMetadata : dataList) {
				XDSDocument xdsDocument = documentContentAndMetadata.getXdsDocument();
				if (xdsDocument != null) {
					ClinicalDocument clinicalDocument;
					try {
						clinicalDocument = CDAUtil.load(xdsDocument.getStream());
						if (clinicalDocument != null) {
							ret.add(clinicalDocument);
						}
					} catch (Exception e) {
						logger
							.error("Could not load document " + xdsDocument.getNewDocumentUniqueId()
								+ " from xdm " + file.getAbsolutePath());
					}
				}
			}
		}
		return ret;
	}
	
	@Override
	public List<org.ehealth_connector.common.Patient> getXdmPatients(File file){
		List<org.ehealth_connector.common.Patient> ret = null;
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		XdmContents contents = conCom.getXdmContents(file.getAbsolutePath());
		if (contents != null) {
			ret = new ArrayList<org.ehealth_connector.common.Patient>();
			List<DocumentContentAndMetadata> dataList = contents.getDocumentAndMetadataList();
			for (DocumentContentAndMetadata documentContentAndMetadata : dataList) {
				ret.add(documentContentAndMetadata.getDocEntry().getPatient());
			}
		}
		return ret;
	}
	
	@Override
	public Patient getElexisPatient(org.ehealth_connector.common.Patient ehcPatient){
		return EhcCoreMapper.getElexisPatient(ehcPatient);
	}
}
