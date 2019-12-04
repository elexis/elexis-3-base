package at.medevit.elexis.ehc.vacdoc.service;

import java.util.List;

import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.common.mdht.Patient;

/**
 * Service interface for communication with the ehealth interface provided by the meineimpfungen web
 * service.
 * 
 * @see <a href="https://www.meineimpfungen.ch/">meineimpfungen</a>
 * @author thomas
 *
 */
public interface MeineImpfungenService {
	
	public static final String CONFIG_ENDPOINT = "meineimpfungen.endpoint";
	public static final String ENDPOINT_PRODUCTIV = "productiv";
	public static final String ENDPOINT_TEST = "test";
	
	public static final String CONFIG_KEYSTORE_PATH = "meineimpfungen.keystorePath";
	public static final String CONFIG_KEYSTORE_PASS = "meineimpfungen.keystorePass";
	
	public static final String PDQ_REQUEST_PATID_OID = "2.16.756.5.30.1.147.1.1";
	
	public static final String XDS_REPOSITORY_OID = "2.16.756.5.30.1.147.2.3.2";

	
	/**
	 * Get all {@link CdaChVacd} instances available for the patient from the web service.
	 * 
	 * @param ehcPatient
	 * @return
	 */
	public List<CdaChVacd> getDocuments(Patient ehcPatient);
	
	/**
	 * Upload a document to the web service.
	 * 
	 * @param document
	 * @return if the upload was successful
	 */
	public boolean uploadDocument(CdaChVacd document);
	
	/**
	 * Get a list of {@link Patient} instances matching name and date of birth of the provided
	 * {@link ch.elexis.data.Patient} from the web service.
	 * 
	 * @param elexisPatient
	 * @return
	 */
	public List<Patient> getPatients(ch.elexis.data.Patient elexisPatient);
	
	/**
	 * Check if the configuration of this implementation is valid.
	 * 
	 * @return
	 */
	boolean isVaild();
	
	/**
	 * Get the base URL of the meineimpfungen web site.
	 * 
	 * @return
	 */
	public String getBaseUrl();
	
	/**
	 * Update the mandant specific configuration of the service.
	 * 
	 * @return if the update was successful
	 */
	public boolean updateConfiguration();
}
