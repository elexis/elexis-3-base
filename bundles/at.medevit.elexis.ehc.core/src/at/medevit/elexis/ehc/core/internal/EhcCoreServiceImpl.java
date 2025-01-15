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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.osgi.service.component.annotations.Component;
import org.projecthusky.common.basetypes.NameBaseType;
import org.projecthusky.common.communication.DocumentMetadata;
import org.projecthusky.common.enums.CodeSystems;
import org.projecthusky.common.enums.DocumentDescriptor;
import org.projecthusky.common.hl7cdar2.POCDMT000040Author;
import org.projecthusky.common.hl7cdar2.POCDMT000040ClinicalDocument;
import org.projecthusky.common.hl7cdar2.POCDMT000040RecordTarget;
import org.projecthusky.common.model.Identificator;
import org.projecthusky.common.model.Name;
import org.projecthusky.common.utils.xml.XmlMarshaller;
import org.projecthusky.common.utils.xml.XmlUnmarshaller;
import org.projecthusky.communication.ConvenienceCommunication;
import org.projecthusky.communication.xd.xdm.DocumentContentAndMetadata;
import org.projecthusky.communication.xd.xdm.XdmContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.core.EhcCoreService;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import jakarta.xml.bind.JAXBException;

@Component
public class EhcCoreServiceImpl implements EhcCoreService {

	private static Logger logger = LoggerFactory.getLogger(EhcCoreServiceImpl.class);

	@Override
	public POCDMT000040ClinicalDocument createDocument(Patient patient, Mandant mandant) {
		POCDMT000040ClinicalDocument ret = new POCDMT000040ClinicalDocument();

		POCDMT000040RecordTarget recordTarget = new POCDMT000040RecordTarget();
		recordTarget.setPatientRole(EhcCoreMapper.getEhcPatient(patient).getMdhtPatientRole());
		ret.getRecordTarget().add(recordTarget);

		POCDMT000040Author author = new POCDMT000040Author();
		author.setAssignedAuthor(EhcCoreMapper.getEhcAuthor(mandant).getAsAuthor());
		ret.getAuthor().add(author);
		return ret;
	}

	@Override
	public POCDMT000040ClinicalDocument loadDocument(InputStream document) {
		try {
			return XmlUnmarshaller.unmarshallAsType(new InputSource(document), POCDMT000040ClinicalDocument.class);
		} catch (Exception e) {
			logger.warn("Error loading document.", e); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public Patient getOrCreatePatient(org.projecthusky.common.model.Patient ehcPatient) {
		Patient patient = EhcCoreMapper.getElexisPatient(ehcPatient, true);
		EhcCoreMapper.importEhcAddress(patient, ehcPatient.getAddress());
		EhcCoreMapper.importEhcPhone(patient, ehcPatient.getTelecoms());
		return patient;
	}

	@Override
	public InputStream getXdmAsStream(POCDMT000040ClinicalDocument document) throws Exception {
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		// write document and create an InputStream
		ByteArrayInputStream inputStream = new ByteArrayInputStream(XmlMarshaller.marshall(document).getBytes());
		// write XDM and create an InputStream
		DocumentMetadata metaData = conCom.addDocument(DocumentDescriptor.CDA_R2, inputStream);
		metaData.setPatient(getPatient(document));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		conCom.createXdmContents(outputStream);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	@Override
	public InputStream getXdmAsStream(Bundle document) throws Exception {
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		// write document and create an InputStream
		ByteArrayInputStream inputStream = new ByteArrayInputStream(ModelUtil.getFhirJson(document).getBytes());
		// write XDM and create an InputStream
		DocumentMetadata metaData = conCom.addDocument(DocumentDescriptor.FHIR_JSON, inputStream);
		metaData.setPatient(getPatient(document));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		conCom.createXdmContents(outputStream);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	public org.projecthusky.common.model.Patient getPatient(Bundle document) {
		Optional<org.hl7.fhir.r4.model.Patient> fhirPatient = document.getEntry().stream()
				.filter(e -> e.getResource() instanceof org.hl7.fhir.r4.model.Patient)
				.map(e -> (org.hl7.fhir.r4.model.Patient) e.getResource()).findFirst();
		if(fhirPatient.isPresent()) {
			Name name = new Name(NameBaseType.builder().withFamily(fhirPatient.get().getNameFirstRep().getFamily())
					.withGiven(fhirPatient.get().getNameFirstRep().getGivenAsSingleString()).build());
			org.projecthusky.common.enums.AdministrativeGender gender = org.projecthusky.common.enums.AdministrativeGender.valueOf(fhirPatient.get().getGender().name()) ;
			Calendar birthDay = new GregorianCalendar();
			birthDay.setTime(fhirPatient.get().getBirthDate());
			
			Identificator id = null;
			Optional<Identifier> ssnIdentifier = fhirPatient.get().getIdentifier().stream()
					.filter(i -> i.getSystem().equals(XidConstants.CH_AHV)
							|| i.getSystem().equals(CodeSystems.SWISS_SSN.getCodeSystemId()))
					.findFirst();
			if (ssnIdentifier.isPresent()) {
				id = new Identificator(CodeSystems.SWISS_SSN.getCodeSystemId(), ssnIdentifier.get().getValue());
			}
			
			return new org.projecthusky.common.model.Patient(name, gender, birthDay, id);
		}
		return null;
	}

	@Override
	public org.projecthusky.common.model.Patient getPatient(POCDMT000040ClinicalDocument document) {
		List<POCDMT000040RecordTarget> targets = document.getRecordTarget();
		if (targets != null && !targets.isEmpty()) {
			if (targets.size() > 1) {
				logger.warn("Document " + document.getTitle() + " has more than one record target"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return new org.projecthusky.common.model.Patient(targets.get(0));
		}
		throw new IllegalStateException("Document " + document.getTitle() + " has no record target"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public List<DocumentContentAndMetadata> getXdmDocuments(File file) {
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		XdmContents contents = conCom.getXdmContents(file.getAbsolutePath());
		if (contents != null) {
			return contents.getDocumentAndMetadataList();
		}
		return Collections.emptyList();
	}

	@Override
	public POCDMT000040ClinicalDocument getDocument(DocumentContentAndMetadata metadata) {
		Document xdsDocument = metadata.getXdsDocument();
		if (xdsDocument != null) {
			try {
				POCDMT000040ClinicalDocument clinicalDocument = XmlUnmarshaller.unmarshallAsType(
						new InputSource(xdsDocument.getDataHandler().getInputStream()),
						POCDMT000040ClinicalDocument.class);
				if (clinicalDocument != null) {
					return clinicalDocument;
				}
			} catch (Exception e) {
				logger.error("Could not load document " + xdsDocument.getDocumentEntry().getEntryUuid());
			}
		}
		return null;
	}

	@Override
	public List<org.projecthusky.common.model.Patient> getXdmPatients(File file) {
		List<org.projecthusky.common.model.Patient> ret = null;
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		XdmContents contents = conCom.getXdmContents(file.getAbsolutePath());
		if (contents != null) {
			ret = new ArrayList<org.projecthusky.common.model.Patient>();
			List<DocumentContentAndMetadata> dataList = contents.getDocumentAndMetadataList();
			for (DocumentContentAndMetadata documentContentAndMetadata : dataList) {
				ret.add(documentContentAndMetadata.getDocEntry().getPatient());
			}
		}
		return ret;
	}

	@Override
	public String createXdmContainer(Patient patient, Mandant mandant, List<File> attachments, String xdmPath) {
		if (patient != null && mandant != null && attachments != null && xdmPath != null) {
			ConvenienceCommunication conCom = new ConvenienceCommunication();
			org.projecthusky.common.model.Patient ehealthPatient = EhcCoreMapper.getEhcPatient(patient);
			System.out.println(ehealthPatient.getIds());
			StringBuilder retInfo = new StringBuilder();
			retInfo.append(xdmPath);
			for (File f : attachments) {
				try {
					if (f.exists()) {
						String attachmentPath = f.getAbsolutePath();
						DocumentDescriptor dc = null;
						if (attachmentPath.toLowerCase().endsWith("xml")) { //$NON-NLS-1$
							if (isCdaDocument(f)) {
								dc = DocumentDescriptor.CDA_R2;
							} else {
								dc = DocumentDescriptor.XML;
							}
						} else if (attachmentPath.toLowerCase().endsWith("pdf")) { //$NON-NLS-1$
							dc = DocumentDescriptor.PDF;
						} else {
							dc = DocumentDescriptor.UNKNOWN;
						}

						FileInputStream in = FileUtils.openInputStream(f);
						DocumentMetadata metaData = conCom.addDocument(dc, in);
						metaData.setPatient(ehealthPatient);
						if (ehealthPatient.getIds() != null && !ehealthPatient.getIds().isEmpty()) {
							metaData.setDestinationPatientId(ehealthPatient.getIds().get(0));
						}
						IOUtils.closeQuietly(in);
						retInfo.append(":::"); //$NON-NLS-1$
						retInfo.append(attachmentPath);

					} else {
						LoggerFactory.getLogger(EhcCoreService.class).warn(
								"creating xdm - patient [{}] - file does not exists [{}]", patient.getId(), //$NON-NLS-1$
								f.getAbsolutePath());
					}
				} catch (IOException e) {
					LoggerFactory.getLogger(EhcCoreService.class).error(
							"creating xdm - patient [{}] - cannot add file [{}]", patient.getId(), f.getAbsolutePath(), //$NON-NLS-1$
							e);
				}
			}
			try {
				conCom.createXdmContents(xdmPath);
				if (retInfo.toString().contains(":::")) { //$NON-NLS-1$
					return retInfo.toString();
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(EhcCoreService.class)
						.error("creating xdm - patient [{}] - cannot create xdm contents", patient.getId(), e); //$NON-NLS-1$
			}
		}
		return null;
	}

	@Override
	public boolean isCdaDocument(File file) {
		if (file != null) {
			try {
				FileInputStream in = FileUtils.openInputStream(file);
				POCDMT000040ClinicalDocument c = XmlUnmarshaller.unmarshallAsType(new InputSource(in),
						POCDMT000040ClinicalDocument.class);
				IOUtils.closeQuietly(in);
				return c != null;
			} catch (Exception e) {
				/* ignore */
			}
		}
		return false;
	}

	public void saveToFile(POCDMT000040ClinicalDocument cda, String outFilePath) throws IOException, JAXBException {
		FileUtils.writeStringToFile(new File(outFilePath), XmlMarshaller.marshall(cda), Charset.forName("UTF-8"));
	}

	@Override
	public void saveDocument(POCDMT000040ClinicalDocument cdaDocument, OutputStream outputStream) {
		try {
			outputStream.write(XmlMarshaller.marshall(cdaDocument).getBytes());
		} catch (Exception e) {
			logger.warn("Error saving document.", e); //$NON-NLS-1$
		}
	}
}
