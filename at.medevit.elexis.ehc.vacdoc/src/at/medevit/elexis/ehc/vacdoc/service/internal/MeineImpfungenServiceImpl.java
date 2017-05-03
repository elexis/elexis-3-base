package at.medevit.elexis.ehc.vacdoc.service.internal;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.common.Identificator;
import org.ehealth_connector.common.Name;
import org.ehealth_connector.common.enums.CodeSystems;
import org.ehealth_connector.common.enums.LanguageCode;
import org.ehealth_connector.common.utils.DateUtil;
import org.ehealth_connector.communication.AffinityDomain;
import org.ehealth_connector.communication.AtnaConfig;
import org.ehealth_connector.communication.ConvenienceMasterPatientIndexV3;
import org.ehealth_connector.communication.Destination;
import org.ehealth_connector.communication.DocumentRequest;
import org.ehealth_connector.communication.MasterPatientIndexQuery;
import org.ehealth_connector.communication.MasterPatientIndexQueryResponse;
import org.ehealth_connector.communication.ch.ConvenienceCommunicationCh;
import org.ehealth_connector.communication.ch.DocumentMetadataCh;
import org.ehealth_connector.communication.ch.enums.AvailabilityStatus;
import org.ehealth_connector.communication.ch.enums.ClassCode;
import org.ehealth_connector.communication.ch.enums.ConfidentialityCode;
import org.ehealth_connector.communication.ch.enums.FormatCode;
import org.ehealth_connector.communication.ch.enums.HealthcareFacilityTypeCode;
import org.ehealth_connector.communication.ch.enums.MimeType;
import org.ehealth_connector.communication.ch.enums.PracticeSettingCode;
import org.ehealth_connector.communication.ch.enums.TypeCode;
import org.ehealth_connector.communication.ch.xd.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.atna.auditor.XDSSourceAuditor;
import org.openhealthtools.ihe.atna.auditor.context.AuditorModuleConfig;
import org.openhealthtools.ihe.atna.auditor.context.AuditorModuleContext;
import org.openhealthtools.ihe.atna.nodeauth.NoSecurityDomainException;
import org.openhealthtools.ihe.atna.nodeauth.SecurityDomain;
import org.openhealthtools.ihe.atna.nodeauth.SecurityDomainException;
import org.openhealthtools.ihe.atna.nodeauth.context.NodeAuthModuleContext;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.openhealthtools.ihe.xds.response.XDSResponseType;
import org.openhealthtools.ihe.xds.response.XDSRetrieveResponseType;
import org.openhealthtools.ihe.xds.response.XDSStatusType;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.vacdoc.service.MeineImpfungenService;
import at.medevit.elexis.ehc.vacdoc.service.VacdocService;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

@Component
public class MeineImpfungenServiceImpl implements MeineImpfungenService {
	
	// use dummy sub id of ehc dev OID for now -> TODO get an Elexis OID
	public static final String ORGANIZATIONAL_ID = "2.16.756.5.30.1.139.1.1.3.9999";
	
	private static final String PDQ_REQUEST_URL =
		"https://test.suisse-open-exchange.healthcare/services/mpi/services/PDQSupplier_Port_Soap12";
	

	private static final String XDS_REGISTRY_URL =
		"https://test.suisse-open-exchange.healthcare/services/registry/services/DocumentRegistry";
	
	private static final String XDS_REPOSITORY_URL =
		"https://test.meineimpfungen.ch/ihe/xds/DocumentRepository";
	
	private static final String ATNA_URL = "tls://test.suisse-open-exchange.healthcare:5544";
	
	private AffinityDomain affinityDomain;
	
	private static final Logger logger = LoggerFactory.getLogger(MeineImpfungenServiceImpl.class);
	
	private VacdocService vacdocService;
	
	private ElexisEventListener mandantListener;
	
	@Reference
	public void setVacdocService(VacdocService vacdocService){
		this.vacdocService = vacdocService;
	}
	
	public void unsetVacdocService(VacdocService vacdocService){
		this.vacdocService = null;
	}
	
	@Activate
	public void activate(){
		updateConfiguration();
		if (mandantListener == null) {
			mandantListener = new MandantChangedListener();
			ElexisEventDispatcher.getInstance().addListeners(mandantListener);
		}
	}
	
	@Deactivate
	public void deactivate(){
		if (mandantListener != null) {
			ElexisEventDispatcher.getInstance().removeListeners(mandantListener);
			mandantListener = null;
		}
	}
	
	private void updateAffinityDomain(String truststorePath, String truststorePass,
		String keystorePath, String keystorePass)
		throws URISyntaxException, SecurityDomainException{
		// OHT configuration is leaking to system configuration, so prepare to reset
		Map<String, String> sysProperties = getSystemSSLProperties();
		
		initCertificatesForSecurityDomain(keystorePath, keystorePass, truststorePath,
			truststorePass);
		affinityDomain = getMeineImpfungenAffinityDomain(truststorePath, truststorePass,
			keystorePath, keystorePass);
		AuditorModuleContext ctx = AuditorModuleContext.getContext();
		AuditorModuleConfig auditorConfig = ctx.getAuditor(XDSSourceAuditor.class).getConfig();
		try {
			auditorConfig
				.setAuditRepositoryUri(affinityDomain.getAtnaConfig().getAuditRepositoryUri());
		} catch (Exception e) {
			logger.error("Audit configuration problem", e);
		}
		
		restoreSystemSSLProperties(sysProperties);
	}
	
	private AffinityDomain getMeineImpfungenAffinityDomain(String truststorePath,
		String truststorePass, String keystorePath, String keystorePass) throws URISyntaxException{
		// set secure destinations
		Destination pdqDestination = new Destination(ORGANIZATIONAL_ID, new URI(PDQ_REQUEST_URL),
			keystorePath, keystorePass);
		pdqDestination.setSenderApplicationOid(ORGANIZATIONAL_ID);
		pdqDestination.setReceiverApplicationOid(PDQ_REQUEST_PATID_OID);
		pdqDestination.setReceiverFacilityOid(PDQ_REQUEST_PATID_OID);
		
		Destination xdsRegistryDestination = new Destination(ORGANIZATIONAL_ID,
			new URI(XDS_REGISTRY_URL), keystorePath, keystorePass);
		Destination xdsRepositoryDestination = new Destination(ORGANIZATIONAL_ID,
			new URI(XDS_REPOSITORY_URL), keystorePath, keystorePass);
		xdsRegistryDestination.setReceiverApplicationOid(XDS_REPOSITORY_OID);
		xdsRegistryDestination.setReceiverFacilityOid(XDS_REPOSITORY_OID);
		
		AffinityDomain ret = new AffinityDomain();
		ret.setPdqDestination(pdqDestination);
		ret.setRegistryDestination(xdsRegistryDestination);
		ret.addRepository(xdsRepositoryDestination);
		ret.setPixDestination(pdqDestination);
		
		AtnaConfig atnaConfig = new AtnaConfig();
		atnaConfig.setAuditRepositoryUri(ATNA_URL);
		atnaConfig.setAuditSourceId("EHC-Elexis");
		ret.setAtnaConfig(atnaConfig);
		return ret;
	}
	
	private void initCertificatesForSecurityDomain(final String pathKeyStoreP12,
		final String keyStorePassword, final String pathTrustStoreJks,
		final String trustStorePassword){
		String[] uris = {
			"https://test.meineimpfungen.ch:443",
			"https://test.suisse-open-exchange.healthcare:443",
			"tls://test.suisse-open-exchange.healthcare:5544"
		};
		initCertificatesForSecurityDomain(pathKeyStoreP12, keyStorePassword, pathTrustStoreJks,
			trustStorePassword, "meineimpfungen", uris);
	}
	
	private void initCertificatesForSecurityDomain(final String pathKeyStoreP12,
		final String keyStorePassword, final String pathTrustStoreJks,
		final String trustStorePassword, String securityDomainName, String[] uris){
		
		java.util.Properties properties = new java.util.Properties();
		properties.put("javax.net.ssl.keyStoreType", "PKCS12");
		properties.put("javax.net.ssl.keyStorePassword", keyStorePassword);
		properties.put("javax.net.ssl.keyStore", pathKeyStoreP12);
		properties.put("javax.net.ssl.trustStorePassword", trustStorePassword);
		properties.put("javax.net.ssl.trustStore", pathTrustStoreJks);
		
		try {
			SecurityDomain securityDomain = new SecurityDomain(securityDomainName, properties);
			NodeAuthModuleContext.getContext().getSecurityDomainManager()
				.registerSecurityDomain(securityDomain);
			for (String uri : uris) {
				NodeAuthModuleContext.getContext().getSecurityDomainManager()
					.unregisterURItoSecurityDomain(new java.net.URI(uri));
			}
			
			for (String uri : uris) {
				NodeAuthModuleContext.getContext().getSecurityDomainManager()
					.registerURItoSecurityDomain(new java.net.URI(uri), securityDomainName);
			}
		} catch (NoSecurityDomainException e) {
			logger.error("error with security domain", e);
		} catch (URISyntaxException e) {
			logger.error("error with security domain", e);
		} catch (SecurityDomainException e) {
			logger.error("error with security domain", e);
		}
	}
	
	private void restoreSystemSSLProperties(Map<String, String> sysProperties){
		Set<String> keys = sysProperties.keySet();
		for (String key : keys) {
			String value = sysProperties.get(key);
			if (value != null) {
				System.setProperty(key, value);
			} else {
				System.clearProperty(key);
			}
		}
	}
	
	private Map<String, String> getSystemSSLProperties(){
		HashMap<String, String> ret = new HashMap<>();
		ret.put("javax.net.ssl.keyStoreType", System.getProperty("javax.net.ssl.keyStoreType"));
		ret.put("javax.net.ssl.keyStorePassword",
			System.getProperty("javax.net.ssl.keyStorePassword"));
		ret.put("javax.net.ssl.keyStore", System.getProperty("javax.net.ssl.keyStore"));
		ret.put("javax.net.ssl.trustStorePassword",
			System.getProperty("javax.net.ssl.trustStorePassword"));
		ret.put("javax.net.ssl.trustStore", System.getProperty("javax.net.ssl.trustStore"));
		return ret;
	}
	
	@Override
	public boolean updateConfiguration(){
		affinityDomain = null;
		// read the configuration
		String truststorePath = CoreHub.mandantCfg.get(CONFIG_TRUSTSTORE_PATH, null);
		String truststorePass = CoreHub.mandantCfg.get(CONFIG_TRUSTSTORE_PASS, null);
		
		String keystorePath = CoreHub.mandantCfg.get(CONFIG_KEYSTORE_PATH, null);
		String keystorePass = CoreHub.mandantCfg.get(CONFIG_KEYSTORE_PASS, null);
		
		if (truststorePass != null && truststorePath != null && keystorePass != null
			&& keystorePath != null) {
			try {
				updateAffinityDomain(truststorePath, truststorePass, keystorePath, keystorePass);
			} catch (SecurityDomainException | URISyntaxException e) {
				logger.error("Could not update affinity domain.", e);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isVaild(){
		return affinityDomain != null && affinityDomain.getPdqDestination() != null;
	}
	
	@Override
	public List<CdaChVacd> getDocuments(org.ehealth_connector.common.Patient ehcPatient){
		List<CdaChVacd> ret = new ArrayList<>();
		List<DocumentEntryType> entryTypes = getAllPatientDocumentEntryTypes(ehcPatient);
		try {
			for (DocumentEntryType documentEntryType : entryTypes) {
				if (documentEntryType.getAvailabilityStatus() != null && documentEntryType
					.getAvailabilityStatus() == AvailabilityStatusType.APPROVED_LITERAL) {
					if ("text/xml".equals(documentEntryType.getMimeType())) {
						InputStream documentStream = getDocumentAsInputStream(documentEntryType);
						if (documentStream != null) {
							Optional<CdaChVacd> vacdocOpt =
								vacdocService.loadVacdocDocument(documentStream);
							vacdocOpt.ifPresent(d -> ret.add(d));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Could not load CdaChVacd", e);
			e.printStackTrace(System.err);
		}
		return ret;
	}
	
	/**
	 * Gets the document entry types.
	 *
	 * @param aMyPatientId
	 *            the a my patient id
	 * @return the document entry types
	 */
	private List<DocumentEntryType> getAllPatientDocumentEntryTypes(
		org.ehealth_connector.common.Patient ehcPatient){
		List<DocumentEntryType> ret = new ArrayList<DocumentEntryType>();
		List<Identificator> ids = ehcPatient.getIds();
		if (ids != null && !ids.isEmpty()) {
			FindDocumentsQuery fdq = new FindDocumentsQuery(ids.get(0), null, null, null, null,
				null, null, null, AvailabilityStatus.APPROVED);
			logger.debug("getDocumentEntryTypes");
			final ConvenienceCommunicationCh convComm =
				new ConvenienceCommunicationCh(affinityDomain);
			logger.debug("queryDocuments");
			final XDSQueryResponseType regDocQuery = convComm.queryDocuments(fdq);
			if (regDocQuery != null) {
				final List<DocumentEntryResponseType> docEntrieResponses =
					regDocQuery.getDocumentEntryResponses();
				logger.info("Document Entries found: " + docEntrieResponses.size());
				for (final DocumentEntryResponseType docEntryResponse : docEntrieResponses) {
					final DocumentEntryType docEntry = docEntryResponse.getDocumentEntry();
					ret.add(docEntry);
				}
			}
			convComm.clearDocuments();
		}
		return ret;
	}
	
	private String getDocumentAsString(DocumentEntryType docEntry) throws IOException{
		InputStream inputStream = getDocumentAsInputStream(docEntry);
		try (BufferedReader buffer =
			new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}
	
	/**
	 * Gets the document as input stream.
	 *
	 * @param docEntry
	 *            the doc entry
	 * @return the document as input stream
	 */
	private InputStream getDocumentAsInputStream(DocumentEntryType docEntry){
		DocumentRequest documentRequest = new DocumentRequest(docEntry.getRepositoryUniqueId(),
			affinityDomain.getRepositoryDestination().getUri(), docEntry.getEntryUUID());
		ConvenienceCommunicationCh convComm = new ConvenienceCommunicationCh(affinityDomain);
		XDSRetrieveResponseType rrt = convComm.retrieveDocument(documentRequest);
		try {
			if ((rrt.getErrorList() != null)) {
				logger.error("error retriveing doc " + docEntry.getEntryUUID() + " - "
					+ rrt.getErrorList().getHighestSeverity().getName());
			}
			if ((rrt.getAttachments() == null) || rrt.getAttachments().size() != 1) {
				logger.error("document not downloaded or more than one");
				return null;
			}
			return rrt.getAttachments().get(0).getStream();
		} finally {
			convComm.clearDocuments();
		}
	}
	
	@Override
	public List<org.ehealth_connector.common.Patient> getPatients(Patient elexisPatient){
		MasterPatientIndexQuery mpiQuery =
			new MasterPatientIndexQuery(affinityDomain.getPdqDestination());
		mpiQuery.addDomainToReturn(PDQ_REQUEST_PATID_OID);
		
		Name name = new Name(elexisPatient.getVorname(), elexisPatient.getName());
		mpiQuery.addPatientName(true, name);
		
		String birthDate = elexisPatient.getGeburtsdatum();
		if (birthDate != null && !birthDate.isEmpty()) {
			mpiQuery.setPatientDateOfBirth(DateUtil.date(birthDate));
		}
		MasterPatientIndexQueryResponse ret =
			ConvenienceMasterPatientIndexV3.queryPatientDemographics(mpiQuery, affinityDomain);
		if (!ret.getSuccess()) {
			throw new IllegalStateException("Error contacting meineimpfungen Service");
		}
		return ret.getPatients();
	}
	
	private Identificator getPatientId(Patient elexisPatient){
		// patient AHV
		String socialSecurityNumber = elexisPatient.getXid(DOMAIN_AHV);
		if (socialSecurityNumber != null) {
			socialSecurityNumber = socialSecurityNumber.trim();
			socialSecurityNumber = socialSecurityNumber.replaceAll("\\.", "");
			if (socialSecurityNumber.length() == 11) {
				return new Identificator(CodeSystems.SwissSSNDeprecated.getCodeSystemId(),
					socialSecurityNumber);
			} else if (socialSecurityNumber.length() == 13) {
				return new Identificator(CodeSystems.SwissSSN.getCodeSystemId(),
					socialSecurityNumber);
			}
		}
		return null;
	}
	
	@Override
	public String getBaseUrl(){
		return "https://test.meineimpfungen.ch/";
	}
	
	@Override
	public boolean uploadDocument(CdaChVacd document){
		XDSResponseType response = null;
		try {
			ConvenienceCommunicationCh convComm =
				new ConvenienceCommunicationCh(affinityDomain);
			DocumentMetadataCh metaData = convComm.addChDocument(DocumentDescriptor.CDA_R2,
				getDocumentAsInputStream(document));
			
			setMetadataCdaCh(metaData, document);
			response = convComm.submit();
		} catch (final Exception e) {
			logger.error("Error uploading document", e);
			return false;
		}
		if (response.getStatus() != null) {
			return XDSStatusType.SUCCESS == response.getStatus().getValue();
		}
		return false;
	}
	
	private boolean setMetadataCdaCh(DocumentMetadataCh metaData, CdaChVacd document){
		
		metaData.addAuthor(document.getAuthor());
		
		metaData.setMimeType(MimeType.XML_TEXT);
		Optional<Identificator> patId = getMeineImpfungenPatientId(document.getPatient());
		if (patId.isPresent()) {
			metaData.setDestinationPatientId(patId.get());
			metaData.setSourcePatientId(patId.get());
		} else {
			return false;
		}
		
		metaData.setCodedLanguage(LanguageCode.GERMAN);
		
		metaData.setTypeCode(TypeCode.ELEKTRONISCHER_IMPFAUSWEIS);
		metaData.setFormatCode(FormatCode.EIMPFDOSSIER);
		metaData.setClassCode(ClassCode.ALERTS);
		
		metaData.setHealthcareFacilityTypeCode(
			HealthcareFacilityTypeCode.AMBULANTE_EINRICHTUNG_INKL_AMBULATORIUM);
		metaData.setPracticeSettingCode(PracticeSettingCode.ALLERGOLOGIE);
		metaData.addConfidentialityCode(ConfidentialityCode.ADMINISTRATIVE_DATEN);
		return true;
	}
	
	private Optional<Identificator> getMeineImpfungenPatientId(
		org.ehealth_connector.common.Patient patient){
		List<Identificator> ids = patient.getIds();
		if (ids != null && !ids.isEmpty()) {
			for (Identificator identificator : ids) {
				if (MeineImpfungenService.PDQ_REQUEST_PATID_OID.equals(identificator.getRoot())) {
					return Optional.of(identificator);
				}
			}
		}
		return Optional.empty();
	}
	
	private InputStream getDocumentAsInputStream(CdaChVacd document) throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CDAUtil.save(document.getMdht(), out);
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	private class MandantChangedListener extends ElexisEventListenerImpl {
		public MandantChangedListener(){
			super(Mandant.class, ElexisEvent.EVENT_MANDATOR_CHANGED);
		}
		
		@Override
		public void run(ElexisEvent ev) {
			updateConfiguration();
		}
	}
}
