package at.medevit.elexis.ehc.vacdoc.service;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.common.Identificator;
import org.ehealth_connector.common.Name;
import org.ehealth_connector.common.enums.CodeSystems;
import org.ehealth_connector.common.utils.DateUtil;
import org.ehealth_connector.communication.AffinityDomain;
import org.ehealth_connector.communication.ConvenienceMasterPatientIndexV3;
import org.ehealth_connector.communication.Destination;
import org.ehealth_connector.communication.DocumentRequest;
import org.ehealth_connector.communication.MasterPatientIndexQuery;
import org.ehealth_connector.communication.MasterPatientIndexQueryResponse;
import org.ehealth_connector.communication.ch.ConvenienceCommunicationCh;
import org.ehealth_connector.communication.ch.enums.AvailabilityStatus;
import org.ehealth_connector.communication.ch.xd.storedquery.FindDocumentsQuery;
import org.openhealthtools.ihe.atna.nodeauth.SecurityDomainException;
import org.openhealthtools.ihe.xds.metadata.AvailabilityStatusType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.response.DocumentEntryResponseType;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.openhealthtools.ihe.xds.response.XDSRetrieveResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.vacdoc.service.internal.AtnaSecurityConfiguration;
import at.medevit.elexis.ehc.vacdoc.service.internal.SecurityDomainManager;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Patient;

public class MeineImpfungenService {
	
	public static final String CONFIG_TRUSTSTORE_PATH = "meineimpfungen.truststorePath";
	public static final String CONFIG_TRUSTSTORE_PASS = "meineimpfungen.truststorePass";
	
	public static final String CONFIG_KEYSTORE_PATH = "meineimpfungen.keystorePath";
	public static final String CONFIG_KEYSTORE_PASS = "meineimpfungen.keystorePass";
	
	// use dummy sub id of ehc dev OID for now -> TODO get an Elexis OID
	public static final String ORGANIZATIONAL_ID = "2.16.756.5.30.1.139.1.1.3.9999";
	
	private static final String PDQ_REQUEST_URL =
		"https://pilot.suisse-open-exchange.healthcare/openempi-admin/services/PDQSupplier_Port_Soap12";
	private static final String PDQ_REQUEST_PATID_OID = "2.16.756.5.30.1.147.1.1";
	
	private static final String XDS_REGISTRY_URL =
		"https://pilot.suisse-open-exchange.healthcare/openxds/services/DocumentRegistry";
	
	private static final String XDS_REPOSITORY_URL =
		"https://pilot.meineimpfungen.ch/ihe/xds/DocumentRepository";
	private static final String XDS_REPOSITORY_OID = "2.16.756.5.30.1.147.2.3.2";
	
	private static final String ATNA_URL = "tls://pilot.suisse-open-exchange.healthcare:5544";
	
	private AffinityDomain affinityDomain;
	
	private static final Logger logger = LoggerFactory.getLogger(MeineImpfungenService.class);
	
	public MeineImpfungenService(){
		// read the configuration
		String truststorePath = CoreHub.mandantCfg.get(CONFIG_TRUSTSTORE_PATH, null);
		String truststorePass = CoreHub.mandantCfg.get(CONFIG_TRUSTSTORE_PASS, null);
		
		String keystorePath = CoreHub.mandantCfg.get(CONFIG_KEYSTORE_PATH, null);
		String keystorePass = CoreHub.mandantCfg.get(CONFIG_KEYSTORE_PASS, null);
		
		if (truststorePass != null && truststorePath != null && keystorePass != null
			&& keystorePath != null) {
			try {
				// set secure destinations
				Destination pdqDestination = new Destination(ORGANIZATIONAL_ID,
					new URI(PDQ_REQUEST_URL), keystorePath, keystorePass);
				pdqDestination.setSenderApplicationOid(ORGANIZATIONAL_ID);
				pdqDestination.setReceiverApplicationOid(PDQ_REQUEST_PATID_OID);
				pdqDestination.setReceiverFacilityOid(PDQ_REQUEST_PATID_OID);
				
				Destination xdsRegistryDestination = new Destination(ORGANIZATIONAL_ID,
					new URI(XDS_REGISTRY_URL), keystorePath, keystorePass);
				Destination xdsRepositoryDestination = new Destination(ORGANIZATIONAL_ID,
					new URI(XDS_REPOSITORY_URL), keystorePath, keystorePass);
				xdsRegistryDestination.setReceiverApplicationOid(XDS_REPOSITORY_OID);
				xdsRegistryDestination.setReceiverFacilityOid(XDS_REPOSITORY_OID);
				
				affinityDomain = new AffinityDomain();
				affinityDomain.setPdqDestination(pdqDestination);
				affinityDomain.setRegistryDestination(xdsRegistryDestination);
				affinityDomain.addRepository(xdsRepositoryDestination);
				affinityDomain.setPixDestination(pdqDestination);
				
				final AtnaSecurityConfiguration atnaSecConfig = new AtnaSecurityConfiguration();
				atnaSecConfig.setKeyStoreFile(new File(keystorePath));
				atnaSecConfig.setKeyStorePassword(keystorePass);
				atnaSecConfig.setKeyStoreType("PKCS12");
				atnaSecConfig.setTrustStoreFile(new File(truststorePath));
				atnaSecConfig.setTrustStorePassword(truststorePass);
				atnaSecConfig.setTrustStoreType("JKS");
				atnaSecConfig.setUri(new URI(ATNA_URL));
				
				try {
					
					SecurityDomainManager.generateSecurityDomain("suisse-open-exchange.healthcare",
						atnaSecConfig);
					SecurityDomainManager.addUriToSecurityDomain("suisse-open-exchange.healthcare",
						atnaSecConfig.getUri());
				} catch (SecurityDomainException | URISyntaxException e) {
					throw new IllegalStateException(e);
				}
			} catch (URISyntaxException e) {
				logger.error("Could not create affinity domain.", e);
			}
		}
	}
	
	public boolean isVaild(){
		return affinityDomain != null && affinityDomain.getPdqDestination() != null;
	}
	
	public List<CdaChVacd> getDocuments(org.ehealth_connector.common.Patient ehcPatient){
		VacdocService vacdocService = new VacdocService();
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
								vacdocService.getVacdocDocument(documentStream);
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
	public List<DocumentEntryType> getAllPatientDocumentEntryTypes(
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
	
	public List<org.ehealth_connector.common.Patient> getPatients(Patient elexisPatient){
		MasterPatientIndexQuery mpiQuery =
			new MasterPatientIndexQuery(affinityDomain.getPdqDestination());
		
		Name name = new Name(elexisPatient.getVorname(), elexisPatient.getName());
		mpiQuery.addPatientName(true, name);
		
		String birthDate = elexisPatient.getGeburtsdatum();
		if (birthDate != null && !birthDate.isEmpty()) {
			mpiQuery.setPatientDateOfBirth(DateUtil.date(birthDate));
		}
		
		MasterPatientIndexQueryResponse ret =
			ConvenienceMasterPatientIndexV3.queryPatientDemographics(mpiQuery, affinityDomain);
		
		return ret.getPatients();
	}
	
	public void setPatient(Patient elexisPatient){
		throw new UnsupportedOperationException();
		//		org.ehealth_connector.common.Patient ehcPatient =
		//			EhcCoreMapper.getEhcPatient(elexisPatient);
		//		ConvenienceMasterPatientIndexV3.addPatientDemographics(ehcPatient, ORGANIZATIONAL_ID,
		//			affinityDomain);
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
}
