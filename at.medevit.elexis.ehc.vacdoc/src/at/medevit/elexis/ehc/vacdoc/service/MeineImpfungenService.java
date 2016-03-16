package at.medevit.elexis.ehc.vacdoc.service;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;

import java.net.URI;
import java.net.URISyntaxException;

import org.ehealth_connector.common.Identificator;
import org.ehealth_connector.common.enums.CodeSystems;
import org.ehealth_connector.communication.AffinityDomain;
import org.ehealth_connector.communication.AtnaConfig;
import org.ehealth_connector.communication.Destination;
import org.ehealth_connector.communication.ch.ConvenienceCommunicationCh;
import org.openhealthtools.ihe.xds.response.XDSQueryResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Patient;

public class MeineImpfungenService {

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
		String keystorePath = CoreHub.mandantCfg.get(CONFIG_KEYSTORE_PATH, null);
		String keystorePass = CoreHub.mandantCfg.get(CONFIG_KEYSTORE_PASS, null);
		
		if (keystorePass != null && keystorePath != null) {
			try {
				// set secure destinations
				Destination pdqDestination =
					new Destination(ORGANIZATIONAL_ID, new URI(PDQ_REQUEST_URL),
					keystorePath, keystorePass);
				Destination xdsRegistryDestination = new Destination(ORGANIZATIONAL_ID,
					new URI(XDS_REGISTRY_URL), keystorePath, keystorePass);
				Destination xdsRepositoryDestination = new Destination(ORGANIZATIONAL_ID,
					new URI(XDS_REPOSITORY_URL), keystorePath, keystorePass);
				affinityDomain = new AffinityDomain(pdqDestination, xdsRegistryDestination,
					xdsRepositoryDestination);
				
				AtnaConfig atnaConfig = new AtnaConfig();
				atnaConfig.setAuditRepositoryUri(ATNA_URL);
				affinityDomain.setAtnaConfig(atnaConfig);
			} catch (URISyntaxException e) {
				logger.error("Could not create affinity domain.", e);
			}
		}
	}
	
	public boolean isVaild(){
		return affinityDomain != null;
	}
	
	public void getDocuments(Patient patient){
		Identificator patientId = getPatientId(patient);
		ConvenienceCommunicationCh communication = new ConvenienceCommunicationCh(affinityDomain);
		
		XDSQueryResponseType documents = communication.queryDocuments(patientId);
		if (documents != null) {
			documents.getStatus();
		}
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
