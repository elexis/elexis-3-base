package ch.elexis.docbox.ws.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jws.soap.SOAPBinding;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.codec.binary.Base64;
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

	private javax.xml.ws.Holder<java.lang.Boolean> success = new javax.xml.ws.Holder<java.lang.Boolean>();
	private javax.xml.ws.Holder<java.lang.String> message = new javax.xml.ws.Holder<java.lang.String>();
	private javax.xml.ws.Holder<java.lang.String> documentId = new javax.xml.ws.Holder<java.lang.String>();

	public SendClinicalDocumentClient() {
		service = new CDACHServicesV2_Service();

		WsClientUtil.addWsSecurityAndHttpConfigWithClientCert(service, WsClientConfig.getUsername(),
				WsClientConfig.getPassword());
	}

	public boolean hasAccess() {
		if (port == null) {
			initPort();
		}
		return WsClientUtil.checkAccess(port);
	}

	private synchronized void initPort() {
		if (port == null) {
			final Thread thread = Thread.currentThread();
			final ClassLoader oldLoader = thread.getContextClassLoader();
			try {
				// mitigate "unable to unmarshall metro config file
				// from location [ bundleresource://xxx/META-INF/jaxws-tubes-default.xml ]"
				// MetroConfigLoader will now run within the info.elexis.target.jaxws.core
				// bundle
				thread.setContextClassLoader(SOAPBinding.class.getClassLoader());
				port = service.getCDACHServicesV2();
				((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						WsClientConfig.getDocboxServiceUrl());

				String httpAuthString = WsClientConfig.getDocboxBasicAuthUser() + ":" //$NON-NLS-1$
						+ WsClientConfig.getDocboxBasicAuthPass();

				String base64httpAuthString = new String(Base64.encodeBase64(httpAuthString.getBytes()));

				Map<String, List<String>> headers = new HashMap<String, List<String>>();
				headers.put("Authorization", Collections.singletonList("Basic " + base64httpAuthString));
				((BindingProvider) port).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, headers);
			} finally {
				thread.setContextClassLoader(oldLoader);
			}
		}
	}

	public boolean sendClinicalDocument(InputStream xmlFile, InputStream pdfFile) {
		if (xmlFile == null) {
			throw new IllegalArgumentException("XML input is null.");
		}

		if (port == null) {
			initPort();
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
					(byteArrayOutputStream != null ? byteArrayOutputStream.toByteArray() : null), success, message,
					documentId);
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

	public String getDocumentId() {
		return documentId.value;
	}

	public String getMessage() {
		return message.value;
	}
}
