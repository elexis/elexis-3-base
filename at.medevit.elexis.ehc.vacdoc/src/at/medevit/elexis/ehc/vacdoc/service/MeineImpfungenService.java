package at.medevit.elexis.ehc.vacdoc.service;

import java.util.List;

import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.common.Patient;

/**
 * Service interface for communication with the ehealth interface provided by the meineimpfungen web
 * service.
 * 
 * @see <a href="https://www.meineimpfungen.ch/">meineimpfungen</a>
 * @author thomas
 *
 */
public interface MeineImpfungenService {
	
	public static final String CONFIG_TRUSTSTORE_PATH = "meineimpfungen.truststorePath";
	public static final String CONFIG_TRUSTSTORE_PASS = "meineimpfungen.truststorePass";
	
	public static final String CONFIG_KEYSTORE_PATH = "meineimpfungen.keystorePath";
	public static final String CONFIG_KEYSTORE_PASS = "meineimpfungen.keystorePass";
	
	/**
	 * Get all {@link CdaChVacd} instances available for the patient from the web service.
	 * 
	 * @param ehcPatient
	 * @return
	 */
	public List<CdaChVacd> getDocuments(Patient ehcPatient);
	
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
}
