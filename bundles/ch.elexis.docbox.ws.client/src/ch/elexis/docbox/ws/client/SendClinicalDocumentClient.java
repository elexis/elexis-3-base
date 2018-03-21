package ch.elexis.docbox.ws.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;

import org.hl7.v3.ClinicalDocumentType;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.docbox.ws.cdachservicesv2.CDACHServicesV2;
import ch.docbox.ws.cdachservicesv2.CDACHServicesV2_Service;

public class SendClinicalDocumentClient {
	
	private static Logger logger = LoggerFactory.getLogger(SendClinicalDocumentClient.class);

	private CDACHServicesV2_Service service;
	private CDACHServicesV2 port;
	
	private javax.xml.ws.Holder<java.lang.Boolean> success =
		new javax.xml.ws.Holder<java.lang.Boolean>();
	private javax.xml.ws.Holder<java.lang.String> message =
		new javax.xml.ws.Holder<java.lang.String>();
	private javax.xml.ws.Holder<java.lang.String> documentId =
		new javax.xml.ws.Holder<java.lang.String>();

	public SendClinicalDocumentClient(){
		service = new CDACHServicesV2_Service();
		
		WsClientUtil.addWsSecurityAndHttpConfigWithClientCert(service,
			WsClientConfig.getSecretkey() + WsClientConfig.getUsername(),
			WsClientConfig.getPassword(), WsClientConfig.getP12Path(), null,
			WsClientConfig.getP12Password(), null);
	}

	public boolean hasAccess(){
		if (port == null) {
			port = service.getCDACHServicesV2();
			((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WsClientConfig.getDocboxServiceUrl());
		}
		return WsClientUtil.checkAccess(port);
		
	}

	public boolean sendClinicalDocument(InputStream xmlFile, InputStream pdfFile){
		if (xmlFile == null) {
			throw new IllegalArgumentException("XML input is null.");
		}
		
		if (port == null) {
			port = service.getCDACHServicesV2();
			((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WsClientConfig.getDocboxServiceUrl());
		}
		
		POCDMT000040ClinicalDocument clinicalDocument = null;
		try {
			clinicalDocument = CdaUtil.unmarshall(xmlFile);

			org.hl7.v3.ClinicalDocumentType clinicalDocumentType = new ClinicalDocumentType();
			clinicalDocumentType.setClinicalDocument(clinicalDocument);

			ByteArrayOutputStream byteArrayOutputStream = null;
			if (pdfFile != null) {
				try {
					byteArrayOutputStream = new ByteArrayOutputStream();
					ZipOutputStream out = new ZipOutputStream(byteArrayOutputStream);
					out.putNextEntry(new ZipEntry("file.pdf"));
					
					byte[] buffer = new byte[1024];
					int len;
					while ((len = pdfFile.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					out.closeEntry();
					out.close();
				} catch (IOException e) {
					logger.error("Could not attach pdf.", e);
					byteArrayOutputStream = null;
				}
			}
			
			port.sendClinicalDocument(clinicalDocumentType,
				(byteArrayOutputStream != null ? byteArrayOutputStream.toByteArray() : null),
				success, message, documentId);
		} catch (JAXBException e) {
			logger.error("XML input is not a valid clinical document.", e);
			return false;
		}

		if (!success.value) {
			logger.error("Could not send clinical document. " + message.value);
			if (clinicalDocument != null) {
				logger.debug(CdaUtil.marshallIntoString(clinicalDocument));
			}
		} else {
			logger.debug("Sent document " + message.value);
		}

		return success.value;
	}
	
	public String getDocumentId(){
		return documentId.value;
	}

	public String getMessage(){
		return message.value;
	}
}
