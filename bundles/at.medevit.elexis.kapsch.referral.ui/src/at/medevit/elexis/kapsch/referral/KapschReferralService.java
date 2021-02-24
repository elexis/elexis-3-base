package at.medevit.elexis.kapsch.referral;

import java.util.Optional;

import ch.elexis.data.Patient;

public interface KapschReferralService {
	
	public static final String CONFIG_ENDPOINT = "kapsch.referral.endpoint";
	public static final String ENDPOINT_PRODUCTIV = "productiv";
	public static final String ENDPOINT_TEST = "test";
	
	/**
	 * Get an URL String containing the Patient Information parameters. The URL can be opened in a
	 * browser.
	 * 
	 * @param patient
	 * @return
	 */
	public Optional<String> getPatientReferralUrl(Patient patient);
	
	/**
	 * Send the Patient Information to the Kapsch Referral WebApp via POST. The URL for the
	 * resulting callId is returned.
	 * 
	 * @param patient
	 * @return
	 */
	public Optional<String> sendPatient(Patient patient);
}
